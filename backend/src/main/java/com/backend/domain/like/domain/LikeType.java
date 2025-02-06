package com.backend.domain.like.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;

public enum LikeType {
	JOB_POSTING, POST;

	@JsonCreator
	public static LikeType from(String param) {
		return Stream.of(LikeType.values())
			.filter(likeType -> likeType.toString().equalsIgnoreCase(param))
			.findFirst()
			.orElse(null);
	}
}
