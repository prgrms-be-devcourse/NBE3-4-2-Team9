package com.backend.domain.post.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = 668423616L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final com.backend.global.baseentity.QBaseEntity _super = new com.backend.global.baseentity.QBaseEntity(this);

    public final com.backend.domain.category.entity.QCategory categoryId;

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.backend.domain.jobposting.entity.QJobPosting jobId;

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Long> numOfApplicants = createNumber("numOfApplicants", Long.class);

    public final DateTimePath<java.time.ZonedDateTime> recruimentClosingDate = createDateTime("recruimentClosingDate", java.time.ZonedDateTime.class);

    public final EnumPath<RecruitmentStatus> recruitmentStatus = createEnum("recruitmentStatus", RecruitmentStatus.class);

    public final StringPath subject = createString("subject");

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.categoryId = inits.isInitialized("categoryId") ? new com.backend.domain.category.entity.QCategory(forProperty("categoryId")) : null;
        this.jobId = inits.isInitialized("jobId") ? new com.backend.domain.jobposting.entity.QJobPosting(forProperty("jobId"), inits.get("jobId")) : null;
    }

}

