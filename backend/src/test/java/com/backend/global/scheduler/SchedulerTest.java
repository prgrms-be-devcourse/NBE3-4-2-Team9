package com.backend.global.scheduler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.jobposting.repository.JobPostingRepository;
import com.backend.global.scheduler.service.SchedulerService;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {"/sql/delete.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class SchedulerTest {

    @Autowired
    private JobPostingRepository jobPostingRepository;
    @Autowired
    private SchedulerService schedulerService;

    @Test
    void testSchedulerJob(){
        //totalJobs(11)가 실제 사람인의 total(1000) 데이터 개수보다 적고 totalJobs % count = 0이 아니면 문제 발생
        schedulerService.processJobPostings(0, 9, 0);

        //DB에서 데이터 조회 후 사이즈 가져오는 로직
        int result = jobPostingRepository.findAll().size();

        Assertions.assertThat(result).isEqualTo(9);
    }
}
