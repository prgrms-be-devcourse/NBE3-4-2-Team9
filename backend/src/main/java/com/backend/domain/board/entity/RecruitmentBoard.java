package com.backend.domain.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// DTYPE 값 설정
@DiscriminatorValue("recruitment")
public class RecruitmentBoard extends Board {

    //모집 상태
    @Column(nullable = false)
    private ZonedDateTime recruitmentClosingDate;

    // 모집 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecruitmentStatus recruitmentStatus;

    // 모집 인원 (나중에 수정)
    @Column(nullable = false)
    private Long numOfApplicants;

    // Lombok의 전체 생성자 어노테이션은 super 생성자가 포함되있지 않아서 따로 작성
    @Builder
    private RecruitmentBoard(Long boardId, String subject, String content,
            ZonedDateTime recruitmentClosingDate, RecruitmentStatus recruitmentStatus,
            Long numOfApplicants) {
        super(boardId, subject, content);
        this.recruitmentClosingDate = recruitmentClosingDate;
        this.recruitmentStatus = recruitmentStatus;
        this.numOfApplicants = numOfApplicants;
    }
}
