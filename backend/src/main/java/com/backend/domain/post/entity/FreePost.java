package com.backend.domain.post.entity;

import com.backend.domain.category.entity.Category;
import com.backend.domain.comment.entity.Comment;
import com.backend.domain.user.entity.SiteUser;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("Free")
@Entity
public class FreePost extends BasePost {

    @Builder
    public FreePost(String subject, String content, Category category, SiteUser author,
            List<Comment> commentList) {
        super(subject, content, category, author, commentList);
    }
}
