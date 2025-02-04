package com.backend.domain.chat.entity;

import com.backend.global.baseentity.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Chat extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chat_id")
	private Long id;

	private Long boardId;

	private Long userId;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Enumerated(EnumType.STRING)
	private MessageType type;
}
