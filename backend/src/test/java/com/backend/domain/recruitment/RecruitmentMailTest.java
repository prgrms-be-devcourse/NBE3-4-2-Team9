package com.backend.domain.recruitment;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.backend.domain.post.entity.RecruitmentPost;
import com.backend.domain.post.entity.RecruitmentStatus;
import com.backend.domain.post.repository.post.PostJpaRepository;
import com.backend.domain.post.repository.recruitment.RecruitmentPostRepository;
import com.backend.domain.recruitmentUser.entity.RecruitmentUser;
import com.backend.domain.recruitmentUser.entity.RecruitmentUserStatus;
import com.backend.domain.recruitmentUser.repository.RecruitmentUserRepository;
import com.backend.domain.recruitmentUser.service.RecruitmentAuthorService;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.mail.service.MailService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"/sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/initRecruitmentUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/delete.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RecruitmentMailTest {

    @Autowired
    private RecruitmentAuthorService recruitmentAuthorService;

    @Autowired
    private PostJpaRepository postRepository;

    @Autowired
    private RecruitmentUserRepository recruitmentUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecruitmentPostRepository recruitmentPostRepository;

    // 실제 Bean 대신 Mock 사용
    @Autowired
    private MailService mailService;

    @Test
    @DisplayName("메일 보내기 테스트")
    public void testUpdateRecruitmentStatusAndSendEmail() {

        // 1. SQL 데이터에서 모집 게시글(모집 게시판, 예: "테스트 제목6")을 조회
        RecruitmentPost post = recruitmentPostRepository.findAll().stream()
                .filter(p -> "테스트 제목6".equals(p.getSubject()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("모집 게시글을 찾을 수 없습니다."));

        // ✅ 기존 지원자 수 기록
        int beforeUserCount = recruitmentUserRepository.countAcceptedByPostId(post.getPostId());
        int maxApplicants = post.getNumOfApplicants();

        // 2. 모집 신청자로 사용할 SiteUser를 조회
        SiteUser applicant = userRepository.findAll().stream()
                .filter(u -> "ho_gok@naver.com".equals(u.getEmail()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("신청자 SiteUser를 찾을 수 없습니다."));

        // 3. 모집 게시글에 대해 RecruitmentUser 엔티티를 생성하여 신청자로 등록
        //    모집 게시판에서는 모집 신청 시 accept() 호출로 상태를 ACCEPTED로 변경하고, currentUserCount를 증가
        RecruitmentUser recruitmentUser = RecruitmentUser.builder()
                .post(post)
                .siteUser(applicant)
                // 아래 accept() 호출로 ACCEPTED로 변경됩니다.
                .status(RecruitmentUserStatus.APPLIED)
                .build();
        recruitmentUser.accept();
        recruitmentUserRepository.save(recruitmentUser);

        // ✅ 지원자 수 증가 후 검증 (accept() 호출로 증가했는지)
        assertEquals(beforeUserCount + 1, recruitmentUserRepository.countAcceptedByPostId(post.getPostId()),
                "✅ 모집 신청 후 currentUserCount가 1 증가해야 합니다.");

        // 4. updateRecruitmentStatus 메서드를 호출합니다.
        //    조건: post.getNumOfApplicants() <= post.getCurrentUserCount()
        recruitmentAuthorService.updateRecruitmentStatus(post);

        // 5. DB에서 해당 Post를 다시 조회하여 모집 상태가 CLOSED로 업데이트되었는지 검증
        RecruitmentPost updatedPost = recruitmentPostRepository.findById(post.getPostId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        assertEquals(RecruitmentStatus.CLOSED, updatedPost.getRecruitmentStatus(),
                "모집 상태가 CLOSED로 업데이트되어야 합니다.");

        // ✅ 모집 상태가 CLOSED로 변경되었는지 확인
        if (recruitmentUserRepository.countAcceptedByPostId(post.getPostId()) >= maxApplicants) {
            assertEquals(RecruitmentStatus.CLOSED, updatedPost.getRecruitmentStatus(),
                    "✅ 모집이 다 찼을 때 상태가 CLOSED로 변경되어야 합니다.");
        } else {
            assertEquals(RecruitmentStatus.OPEN, updatedPost.getRecruitmentStatus(),
                    "✅ 아직 모집이 다 차지 않았다면 OPEN 상태여야 합니다.");
        }

        // 실제 이메일 확인 가능
        System.out.println("📩 이메일이 정상적으로 발송되었는지 네이버 메일함에서 확인하세요!");
    }

    @Test
    @DisplayName("유저 Count 테스트")
    public void testCurrentUserCountIncrease() {
        // 1. 모집 게시글을 조회 (제목이 '테스트 제목6'인 게시글)
        RecruitmentPost post = recruitmentPostRepository.findAll().stream()
                .filter(p -> "테스트 제목6".equals(p.getSubject()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("모집 게시글을 찾을 수 없습니다."));

        // ✅ 기존 지원자 수 기록
        int beforeUserCount = recruitmentUserRepository.countAcceptedByPostId(post.getPostId());

        // 2. 모집 신청자로 사용할 SiteUser를 조회 (이메일로 사용자 찾기)
        SiteUser applicant = userRepository.findAll().stream()
                .filter(u -> "ho_gok@naver.com".equals(u.getEmail()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("신청자 SiteUser를 찾을 수 없습니다."));

        // 3. 모집 게시글에 대해 RecruitmentUser 엔티티를 생성하고 신청자로 등록
        RecruitmentUser recruitmentUser = RecruitmentUser.builder()
                .post(post)
                .siteUser(applicant)
                .status(RecruitmentUserStatus.APPLIED) // 'APPLIED' 상태로 생성
                .build();

        // recruitmentUser.accept()를 호출하여 상태를 ACCEPTED로 변경하고, currentUserCount 증가
        recruitmentUser.accept();
        recruitmentUserRepository.save(recruitmentUser);

        // 4. 모집 게시글에서 지원자 수가 증가했는지 검증
        assertEquals(beforeUserCount + 1, recruitmentUserRepository.countAcceptedByPostId(post.getPostId()));
        System.out.println(beforeUserCount + recruitmentUserRepository.countAcceptedByPostId(post.getPostId()));

        // Count 로그
        System.out.println("신청한 사람 수 : " + recruitmentUserRepository.countAcceptedByPostId(post.getPostId()));
    }

    @Test
    @DisplayName("OPEN => CLOSED 테스트")
    public void CloseTest() {
        // 1. SQL 데이터에서 모집 게시글을 조회 (null 상태)
        RecruitmentPost post = recruitmentPostRepository.findAll().stream()
                .filter(p -> "테스트 제목5".equals(p.getSubject()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("모집 게시글을 찾을 수 없습니다."));

        //2명
        // 2. 모집 상태가 OPEN 상태여야 하므로 먼저 모집 상태를 OPEN으로 설정
        assertEquals(RecruitmentStatus.OPEN, post.getRecruitmentStatus(), "모집 상태는 OPEN이어야 합니다.");

        // ✅ 기존 지원자 수 기록
        int beforeUserCount = recruitmentUserRepository.countAcceptedByPostId(post.getPostId());
        System.out.println("현재 모집 인원: " + beforeUserCount);

        // 3. 모집 신청자로 사용할 SiteUser를 조회
        SiteUser applicant = userRepository.findAll().stream()
                .filter(u -> "ho_gok@naver.com".equals(u.getEmail()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("신청자 SiteUser를 찾을 수 없습니다."));

        // 4. 모집 게시글에 대해 RecruitmentUser 엔티티를 생성하여 신청자로 등록
        RecruitmentUser recruitmentUser = RecruitmentUser.builder()
                .post(post)
                .siteUser(applicant)
                .status(RecruitmentUserStatus.APPLIED)
                .build();
        recruitmentUser.accept();
        recruitmentUserRepository.save(recruitmentUser);

        // 모집 상태 업데이트 전후 current_user_count 확인
        System.out.println("업데이트 전 모집 인원: " + recruitmentUserRepository.countAcceptedByPostId(post.getPostId()));

        // 5. 모집 상태를 업데이트하고 확인
        recruitmentAuthorService.updateRecruitmentStatus(post);

        // 6. DB에서 해당 Post를 다시 조회하여 모집 상태가 CLOSED로 업데이트되었는지 검증
        RecruitmentPost updatedPost = recruitmentPostRepository.findById(post.getPostId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 7. 모집 인원 수가 num_of_applicants에 도달했으므로 상태는 CLOSED로 변경되어야 함
        assertEquals(RecruitmentStatus.CLOSED, updatedPost.getRecruitmentStatus(),
                "모집 상태는 CLOSED로 업데이트되어야 합니다.");

        // 추가로 상태가 OPEN인지 CLOSED인지 직접 확인하기 위한 로그
        System.out.println("게시글 상태: " + updatedPost.getRecruitmentStatus());
    }

    @Test
    @DisplayName("인원이 다 차있지않으면 OPEN으로 나오는지 테스트")
    public void testRecruitmentStatusOpen() {
        // 1. SQL 데이터에서 모집 게시글을 조회 (recruitment_status가 null로 설정)
        RecruitmentPost post = recruitmentPostRepository.findAll().stream()
                .filter(p -> "테스트 제목5".equals(p.getSubject()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("모집 게시글을 찾을 수 없습니다."));

/*        // 2. 모집 게시글의 상태가 null인 상태인지 확인
        assertNull(post.getRecruitmentStatus(), "모집 상태는 null이어야 합니다.");*/

        // 3. 모집 신청자로 사용할 SiteUser를 조회
        SiteUser applicant = userRepository.findAll().stream()
                .filter(u -> "ho_gok@naver.com".equals(u.getEmail()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("신청자 SiteUser를 찾을 수 없습니다."));

        // 4. 모집 게시글에 대해 RecruitmentUser 엔티티를 생성하여 신청자로 등록
        RecruitmentUser recruitmentUser = RecruitmentUser.builder()
                .post(post)
                .siteUser(applicant)
                .status(RecruitmentUserStatus.APPLIED)
                .build();
        recruitmentUser.accept();
        recruitmentUserRepository.save(recruitmentUser);

        // 5. 모집 상태가 OPEN으로 유지되는지 확인 (모집 인원이 다 차지 않음)
        recruitmentAuthorService.updateRecruitmentStatus(post);

        // 6. DB에서 해당 Post를 다시 조회하여 모집 상태가 OPEN으로 유지되었는지 검증
        RecruitmentPost updatedPost = recruitmentPostRepository.findById(post.getPostId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 현재 지원자가 한 명일 때, 모집 인원이 다 차지 않았으므로 상태는 OPEN이어야 함
        assertEquals(RecruitmentStatus.OPEN, updatedPost.getRecruitmentStatus(),
                "모집 상태는 OPEN이어야 합니다.");

        // 추가로 상태가 OPEN인지 CLOSED인지 직접 확인하기 위한 로그
        System.out.println("게시글 상태: " + updatedPost.getRecruitmentStatus());
    }
}