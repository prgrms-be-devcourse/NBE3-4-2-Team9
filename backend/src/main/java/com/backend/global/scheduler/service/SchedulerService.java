package com.backend.global.scheduler.service;

import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.jobposting.repository.JobPostingRepository;
import com.backend.domain.jobskill.repository.JobSkillRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.scheduler.apiresponse.Job;
import com.backend.global.scheduler.apiresponse.Jobs;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final JobSkillRepository jobSkillRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;


    // URI로 조합할 OPEN API URL
    private final String API_URL = "https://oapi.saramin.co.kr/job-search";

    // URI로 조합할 apiKey
    @Value("${api.key}")
    private String apiKey;

    // URI로 조합할 한 페이지당 가져올데이터 수
    @Value("${api.count}")
    private Integer count;

    /**
     * 매일 자정(00:00)에 실행될 스케줄러 메서드입니다.
     * <p>
     * - retryTemplate.execute(context -> { ... }) -> API 요청이 실패할 경우 재시도를 수행하는 `RetryTemplate`을
     * 사용합니다. - processJobPostings (totalCount, totalJobs, pageNumber) -> API에서 채용 공고 데이터를 가져와
     * 데이터베이스에 저장하는 핵심 로직을 실행합니다.
     */
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void savePublicData() {
        retryTemplate.execute(context -> {

            int pageNumber = 0;
            int totalCount = 0;
            int totalJobs = Integer.MAX_VALUE;

            processJobPostings(totalCount, totalJobs, pageNumber);

            return null;

        });

    }

    /**
     * - 클래스 내에서 핵심로직이며, fetchJobPostings() 메소드를 통해 가져온 채용공고 데이터들을 저장하기위한 List<JobPosting>,
     * List<JobSkill> 로 변환하여, 저장하도록 하는 메서드이다.
     * - 오늘 가져올수있는 총 공고수(totalJobs) 보다 데이텁베이스에 저장된 공고수(totalCount) 크면 callBack 함수가 멈춘다.
     *
     * @param totalCount 현재 저장된 공고수
     * @param totalJobs  오늘 총 공고 수
     * @param pageNumber 현재 페이지 번호
     */
    public void processJobPostings(int totalCount, int totalJobs, int pageNumber) {
        Jobs jobs = fetchJobPostings(pageNumber, count);

        // JobPosting 클래스로 담기
        List<JobPosting> jobPostingList = jobs.getJobsDetail().getJobList().stream()
            .map(Job::toEntity)
            .toList();

        // 전체 저장
        // 엔티티
        List<JobPosting> savedJobPostingList = saveNewJobs(jobPostingList); //jobId

//        //JSON 응답 파싱
//        List<Job> jobList = jobs.getJobsDetail().getJobList(); //id
//        Map<String, Job> jobMap = jobList.stream()
//            .collect(Collectors.toMap(Job::getId, job -> job));
//
//        for (JobPosting jobPosting : savedJobPostingList) {
//
//            //채용 공고랑 jobPosting이랑 일치하는 애 찾는 if문
//            Job findJob = jobMap.get(jobPosting.getJobId());
//            String jobCode = findJob.getPositionDto().getJobCode().getCode();
//
//            //여러개면 , 기준으로 짜르기
//            String[] jobCodeArray = jobCode.split(",");
//
//            for (String s : jobCodeArray) {
//                //jobSkill 조회
//                Optional<JobSkill> jobSkillOptional = jobSkillRepository.findByCode(
//                    Integer.parseInt(s));
//                if (jobSkillOptional.isEmpty()) {
//                    continue;
//                } else {
//                    JobSkill jobSkill = jobSkillOptional.get();
//                    //JobPosting에 jobskill 설정
//                    jobPosting.getJobPostingJobSkillList().add(
//                        JobPostingJobSkill.builder()
//                            .jobPosting(testJobPosting)
//                            .jobSkill(jobSkill));
//                }
//            }
//        }

        //더티 체킹으로 인해 업데이트 쿼리 자동 발생

        // JobPostingJobSkill (JobPosting이 저장이 안 된 상태로 저장이 될지 모르겠음)

        //총 가져와야되는 개수 초기화
        if (totalJobs == Integer.MAX_VALUE) {
            totalJobs = Integer.parseInt(jobs.getJobsDetail().getTotal());
        }

        totalCount += jobPostingList.size();

        if (totalCount < totalJobs) {
            processJobPostings(totalCount, totalJobs, ++pageNumber);
        }
    }

    /**
     * 지정된 페이지 번호와 가져올 데이터 개수를 기준으로 채용공고 데이터를 가져오는 메서드입니다.
     * <p>
     * - restTemplate : 주어진 URI로 채용공고 api 서버에 GET 요청을 보내, 응답 데이터를 받아오는 역할수행 - objectMapper : JSON
     * 문자열을 Jobs 객체로 변환하는 즉 역직렬화 역할수행.
     *
     * @param pageNumber 현재 페이지 번호
     * @param count      가져올 데이터 개수
     */
    private Jobs fetchJobPostings(int pageNumber, int count) {

        URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
            .queryParam("access-key", apiKey)
            .queryParam("published", getPublishedDate())
            .queryParam("job_mid_cd", "2")
            .queryParam("start", pageNumber) // 현재 페이지숫자
            .queryParam("count", count) //한 번 호출시 가지고 오는 데이터 양
            .build()
            .encode()
            .toUri();

        try {
            String jsonResponse = restTemplate.getForObject(uri, String.class);
            log.info("API 응답: {}", jsonResponse);

            // 이거 지워도 밑에
            if (jsonResponse == null || jsonResponse.isEmpty()) {
                log.error(GlobalErrorCode.NO_DATA_RECEIVED.getMessage());
                throw new GlobalException(GlobalErrorCode.NO_DATA_RECEIVED);
            }

            Jobs dataResponse = objectMapper.readValue(jsonResponse, Jobs.class);

            if (dataResponse.getJobsDetail() == null || dataResponse.getJobsDetail().getJobList()
                .isEmpty()) {
                log.error(GlobalErrorCode.NO_DATA_RECEIVED.getMessage());
                throw new GlobalException(GlobalErrorCode.NO_DATA_RECEIVED);
            }

            return dataResponse;

        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패", e);
            throw new GlobalException(GlobalErrorCode.JSON_PARSING_FAILED);
        }

    }

    /**
     * scheduler가 자정에 실행되기 때문에 전날 데이터를 가져오게 만든 메서드
     */
    private String getPublishedDate() {
        // 전날데이터
        LocalDate today = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return today.format(formatter);
    }

    /**
     * JobPosting, JobSkill 데이터들을 데이터베이스에 저장하기위한 메서드
     *
     * @param newJobs 가공된 JobPosting 데이터 리스트
     */
    @Transactional
    private List<JobPosting> saveNewJobs(List<JobPosting> newJobs) {
        try {
            List<JobPosting> savedJobPostingList = jobPostingRepository.saveAll(newJobs);
            log.info("총 {}개의 공고를 저장했습니다.", savedJobPostingList.size());
            return savedJobPostingList;
        } catch (Exception e) {
            log.error(GlobalErrorCode.DATABASE_SAVE_FAILED.getMessage(), e);
            throw new GlobalException(GlobalErrorCode.DATABASE_SAVE_FAILED);
        }
    }


}
