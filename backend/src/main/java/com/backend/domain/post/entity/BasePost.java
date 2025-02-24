package com.backend.domain.post.entity;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import java.util.ArrayList;
import java.util.List;

import com.backend.domain.category.entity.Category;
import com.backend.domain.comment.entity.Comment;
import com.backend.domain.user.entity.SiteUser;
import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

// 게시판 엔티티
@Entity
@Getter
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "post_type") // DTYPE 컬럼 생성
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasePost extends BaseEntity {

    // postId: 게시글의 고유 식별자(PK, Auto Increment)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    // subject: 게시글 제목
    @Column(nullable = false)
    private String subject;

    // content: 게시글 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 카테고리 ID -> 카테고리 테이블과의 관계 설정
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // UserId 한 개의 게시글은 오직 한 유저에게만 속함
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private SiteUser author;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> commentList = new ArrayList<>();

    public BasePost(String subject, String content, Category category, SiteUser author,
            List<Comment> commentList) {
        this.subject = subject;
        this.content = content;
        this.category = category;
        this.author = author;
        this.commentList = commentList != null ? commentList : new ArrayList<>();
    }

    // createDate: 생성일자, BaseEntity 상속
    // modifyDate: 수정일자, BaseEntity 상속

//	// 게시글 수정(모집 게시판)
//	public void updatePost(String subject, String content, Integer numOfApplicants) {
//		updatePost(subject, content);
//		this.numOfApplicants = numOfApplicants;
//	}
//
//	// 게시글 수정
//	public void updatePost(String subject, String content) {
//		// 기존 제목과 다를 때
//		this.subject = subject;
//		// 기존 게시글 내용과 다를 때
//		this.content = content;
//	}
//
//	public void updateRecruitmentStatus(RecruitmentStatus recruitmentStatus) {
//		this.recruitmentStatus = recruitmentStatus;
}
