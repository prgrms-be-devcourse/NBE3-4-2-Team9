package com.backend.domain.post.service;

import com.backend.domain.post.dto.PostResponse;
import com.backend.domain.post.dto.RecruitmentPostRequest;
import com.backend.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecruitmentPostService {

	private final PostRepository postRepository;

	public PostResponse save(RecruitmentPostRequest recruitmentPostRequest) {
		return null;
	}
}
