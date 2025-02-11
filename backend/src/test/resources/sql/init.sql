-- SiteUser 데이터 생성
INSERT INTO site_user (created_at, modified_at, email, introduction, job, kakao_id, name, password,
                       profile_img, user_role)
values (CURRENT_DATE, CURRENT_DATE, 'testEmail1@naver.com', 'test', 'test', 'testId1', 'testName1',
        'testPassword', 'testImg', 'ROLE_USER'),
       (CURRENT_DATE, CURRENT_DATE, 'testEmail2@naver.com', 'test', 'test', 'testId2', 'testName2',
        'testPassword', 'testImg', 'ROLE_USER'),
       (CURRENT_DATE, CURRENT_DATE, 'testEmail3@naver.com', 'test', 'test', 'testId3', 'testName3',
        'testPassword', 'testImg', 'ROLE_USER'),
       (CURRENT_DATE, CURRENT_DATE, 'admin@naver.com', 'test', 'test', 'testId4', 'testName4',
        'testPassword', 'testImg', 'ROLE_ADMIN');

-- Category 데이터 생성
-- ID 1 -> 자유 게시판, ID 2 -> 모집 게시판
INSERT INTO category (created_at, modified_at, name)
VALUES (CURRENT_DATE, CURRENT_DATE, '자유 게시판'),
       (CURRENT_DATE, CURRENT_DATE, '모집 게시판');

-- JobPosting 데이터 생성
INSERT INTO job_posting (apply_cnt, close_date, company_link, company_name, experience_level_code,
                         experience_level_max, experience_level_min, experience_level_name,
                         job_posting_status, open_date, post_date, require_educate_code,
                         require_educate_name, salary_code, salary_name, subject, url, job_id)
values (1, CURRENT_DATE + 1, 'testLink', 'testCompany', 2, 3, 1, '경력 1~3년', 'ACTIVE', CURRENT_DATE,
        CURRENT_DATE, 0, '학력 무관', 22, '1억원 이상', '테스트 제목1', 'testUrl', 1),
       (2, CURRENT_DATE + 1, 'testLink', 'testCompany', 2, 3, 1, '경력 1~3년', 'ACTIVE', CURRENT_DATE,
        CURRENT_DATE, 0, '학력 무관', 22, '1억원 이상', '테스트 제목2', 'testUrl', 2),
       (3, CURRENT_DATE + 1, 'testLink', 'testCompany', 2, 3, 1, '경력 1~3년', 'ACTIVE', CURRENT_DATE,
        CURRENT_DATE, 0, '학력 무관', 22, '1억원 이상', '테스트 제목3', 'testUrl', 3),
       (4, CURRENT_DATE + 1, 'testLink', 'testCompany', 2, 3, 1, '경력 1~3년', 'ACTIVE', CURRENT_DATE,
        CURRENT_DATE, 0, '학력 무관', 22, '1억원 이상', '테스트 제목4', 'testUrl', 4),
       (5, CURRENT_DATE + 1, 'testLink', 'testCompany', 2, 3, 1, '경력 1~3년', 'ACTIVE', CURRENT_DATE,
        CURRENT_DATE, 0, '학력 무관', 22, '1억원 이상', '테스트 제목5', 'testUrl', 5),

       (6, CURRENT_DATE + 1, 'testLink', 'testCompany', 1, 0, 0, '신입', 'ACTIVE', CURRENT_DATE,
        CURRENT_DATE, 0, '학력 무관', 99, '면접 후 결정', '테스트 제목6', 'testUrl', 6),
       (7, CURRENT_DATE + 1, 'testLink', 'testCompany', 1, 0, 0, '신입', 'ACTIVE', CURRENT_DATE,
        CURRENT_DATE, 0, '학력 무관', 99, '면접 후 결정', '테스트 제목7', 'testUrl', 7),
       (8, CURRENT_DATE + 1, 'testLink', 'testCompany', 1, 0, 0, '신입', 'ACTIVE', CURRENT_DATE,
        CURRENT_DATE, 0, '학력 무관', 99, '면접 후 결정', '테스트 제목8', 'testUrl', 8),
       (9, CURRENT_DATE + 1, 'testLink', 'testCompany', 1, 0, 0, '신입', 'ACTIVE', CURRENT_DATE,
        CURRENT_DATE, 0, '학력 무관', 99, '면접 후 결정', '테스트 제목9', 'testUrl', 9),
       (10, CURRENT_DATE + 1, 'testLink', 'testCompany', 1, 0, 0, '신입', 'ACTIVE', CURRENT_DATE,
        CURRENT_DATE, 0, '학력 무관', 99, '면접 후 결정', '테스트 제목10', 'testUrl', 10),
       (11, CURRENT_DATE + 1, 'testLink', 'testCompany', 1, 0, 0, '신입', 'ACTIVE', CURRENT_DATE,
        CURRENT_DATE, 0, '학력 무관', 99, '면접 후 결정', '테스트 제목11', 'testUrl', 11),
       (12, CURRENT_DATE + 1, 'testLink', 'testCompany', 1, 0, 0, '신입', 'ACTIVE', CURRENT_DATE,
        CURRENT_DATE, 0, '학력 무관', 99, '면접 후 결정', '테스트 제목12', 'testUrl', 12);

-- Post 데이터 생성
INSERT INTO post (created_at, modified_at, content, num_of_applicants, recruitment_closing_date,
                  recruitment_status, subject, category_id, job_id, user_id)
VALUES (CURRENT_DATE, CURRENT_DATE, 'testContent1', null, CURRENT_DATE + 1, null, 'testSubject', 1,
        null, 1),
       (CURRENT_DATE, CURRENT_DATE, '테스트 내용2', null, CURRENT_DATE + 1, null, '테스트 제목2', 1,
        null, 1),
       (CURRENT_DATE, CURRENT_DATE, '테스트 내용3', null, CURRENT_DATE + 1, null, '테스트 제목3', 1,
        null, 2),
       (CURRENT_DATE, CURRENT_DATE, '테스트 내용4', null, CURRENT_DATE + 1, 'OPEN', '테스트 제목4', 2,
        null, 2),
       (CURRENT_DATE, CURRENT_DATE, '테스트 내용5', null, CURRENT_DATE + 1, 'CLOSED', '테스트 제목5', 2,
        null, 3),
       (CURRENT_DATE, CURRENT_DATE, '테스트 내용6', null, CURRENT_DATE + 1, null, '테스트 제목6', 2,
        null, 3);

-- JobSkill 데이터 생성
INSERT INTO job_skill (job_skill_code, job_skill_name)
VALUES (1, 'JAVA'),
       (2, 'C'),
       (3, 'PYTHON');


INSERT INTO job_posting_job_skill(job_posting_id, job_skill_id)
VALUES (1, 1),
       (1, 2);


-- Chat 데이터 생성
INSERT INTO chat (created_at, modified_at, post_id, user_id, content, type)
VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'content0', 'CHAT'),
       (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'content1', 'CHAT'),

       (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 2, 'content0', 'CHAT'),
       (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 2, 'content1', 'CHAT');


INSERT into user_job_skill (user_id, job_skill_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (2, 1),
       (2, 2),
       (2, 3),
       (3, 1),
       (3, 2),
       (3, 3);

INSERT INTO voter (job_posting_id, site_user_id)
VALUES (1, 1),
       (2, 1),
       (3, 1),
       (1, 2);
