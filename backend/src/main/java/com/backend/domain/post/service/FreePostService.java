package com.backend.domain.post.service;

import com.backend.domain.post.dto.FreePostRequest;
import com.backend.domain.post.dto.PostResponse;
import com.backend.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FreePostService {

	private final PostRepository postRepository;

	public PostResponse save(FreePostRequest freePostRequest) {
		return null;
	}

}
