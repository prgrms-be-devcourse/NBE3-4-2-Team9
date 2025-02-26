package com.backend.domain.post.repository;

import com.backend.domain.post.dto.PostPageResponse;
import com.backend.domain.post.entity.BasePost;
import com.backend.domain.post.util.PostSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasePostRepository extends JpaRepository<BasePost, Long> {

    Page<PostPageResponse> findAll(PostSearchCondition postSearchCondition, Pageable pageable);
}
