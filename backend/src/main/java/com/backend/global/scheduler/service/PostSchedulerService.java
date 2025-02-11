package com.backend.global.scheduler.service;


import com.backend.domain.post.entity.Post;
import com.backend.domain.post.repository.PostRepository;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostSchedulerService {

    private final PostRepository postRepository;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    @Transactional
    public void updateRecruitmentStatus(){

        // 스케쥴링 시작
        log.info("스터디 모집 상태 업데이트 시작");

        // 모집 마감해야 할 게시글 조회
        List<Post> expiredPosts = postRepository.findExpiredRecruitmentPosts(ZonedDateTime.now());

        // 모집 마감 처리
        expiredPosts.forEach(Post::updateRecruitmentStatus);

        log.info("스터디 모집 상태 업데이트 완료. 총 {}건 변경", expiredPosts.size());

    }
}
