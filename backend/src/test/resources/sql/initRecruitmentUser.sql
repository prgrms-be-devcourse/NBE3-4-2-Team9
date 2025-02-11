-- RecruitmentUser 데이터 생성
INSERT INTO recruitment_user (created_at, modified_at, post_id, site_user_id, status)
VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'APPLIED'),  -- 지원자 1이 게시글 1에 지원
       (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 2, 'ACCEPTED'), -- 지원자 2가 게시글 1에 승인
       (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 3, 'REJECTED'), -- 지원자 3이 게시글 2에 거절
       (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 1, 'APPLIED'),  -- 지원자 1이 게시글 2에 다시 지원
       (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 2, 'APPLIED'),  -- 지원자 3이 게시글 2에 지원
       (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, 2, 'APPLIED'); -- 지원자 2가 게시글 4에 지원