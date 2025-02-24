package com.backend.domain.post.entity;

import com.backend.domain.category.entity.Category;
import com.backend.domain.comment.entity.Comment;
import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.voter.entity.Voter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("Recruitment")
@Entity
public class RecruitmentPost extends BasePost {

    // 모집 게시판에만 필요한 부분
    private ZonedDateTime recruitmentClosingDate; // 모집 기간
    private Integer numOfApplicants; // 모집 인원

    @Enumerated(EnumType.STRING)
    @Column(nullable = true) // 모집 게시판 아니면 Null 가능
    private RecruitmentStatus recruitmentStatus; // 모집 상태

    // 채용 ID -> JopPosting table에 채용ID랑 이어짐
    @ManyToOne
    @JoinColumn(name = "job_id", nullable = true)
    private JobPosting jobPosting;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Voter> voterList = new ArrayList<>();

    @Builder
    public RecruitmentPost(String subject, String content, Category category, SiteUser author,
            ZonedDateTime recruitmentClosingDate, Integer numOfApplicants,
            RecruitmentStatus recruitmentStatus, JobPosting jobPosting,
            List<Comment> commentList) {
        super(subject, content, category, author, commentList);
        this.recruitmentClosingDate = recruitmentClosingDate;
        this.recruitmentStatus = recruitmentStatus;
        this.numOfApplicants = numOfApplicants;
        this.jobPosting = jobPosting;
    }
}
