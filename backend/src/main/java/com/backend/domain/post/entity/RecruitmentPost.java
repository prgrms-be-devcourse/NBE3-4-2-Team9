package com.backend.domain.post.entity;

import java.time.ZonedDateTime;

import com.backend.domain.jobposting.entity.JobPosting;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@DiscriminatorValue("recruitment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentPost extends Post {
	//TODO 추후 모집 종료에 따른 비즈니스 로직 구현할 것
    private ZonedDateTime recruitmentClosingDate;
    private Integer numOfApplicants;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private RecruitmentStatus recruitmentStatus;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = true)
    private JobPosting jobPosting;
	
	// 게시글 수정(모집 게시판)
	public void updatePost(String subject, String content, Integer numOfApplicants) {
		super.updatePost(subject, content);
		this.numOfApplicants = numOfApplicants;
	}

	public void updateRecruitmentStatus(RecruitmentStatus recruitmentStatus) {
		this.recruitmentStatus = recruitmentStatus;
	}

}
