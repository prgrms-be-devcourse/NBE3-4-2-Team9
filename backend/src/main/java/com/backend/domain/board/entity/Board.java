package com.backend.domain.board.entity;

import com.backend.global.baseentity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 게시판 엔티티
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 강제 추가
//상속 관계로 테이블 정의시 전략 설정
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Table(name="board")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    // board_id: 게시글의 고유 식별자(PK, Auto Increment)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    // subject: 게시글 제목
    @Column(nullable = false)
    private String subject;

    // content: 게시글 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // createDate: 생성일자, BaseEntity 상속
    // modifyDate: 수정일자, BaseEntity 상속

    // 카테고리 ID -> 카테고리 테이블과의 관계 설정 | 다대일 관계, board의 여러 게시글이 하나의 카테고리 참조
    // TODO: category, jobposting 미구현, 구현 이후 다시 작업
    /*@ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category categoryId;*/

    // 채용 ID -> JopPosting table에 채용ID랑 이어짐
//    @ManyToOne
//    @JoinColumn(name = "job_id", nullable = false)
//    private JobPosting jobId;

    public void updatePost(String subject, String content) {
        // 게시글 수정 로직
        // 기존 제목과 다를 때
        if (!this.subject.equals(subject)) {
            this.subject = subject;
        }
        // 기존 게시글 내용과 다를 때
        if (!this.content.equals(content)) {
            this.content = content;
        }

    }
}