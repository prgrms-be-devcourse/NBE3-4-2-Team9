package com.backend.domain.chat.controller;

import static org.springframework.http.MediaType.*;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.backend.domain.chat.dto.request.ChatRequest;
import com.backend.domain.chat.dto.response.ChatResponse;
import com.backend.domain.chat.dto.response.ChatResponses;
import com.backend.domain.chat.dto.response.ChatsInBoard;
import com.backend.domain.chat.service.ChatService;
import com.backend.global.response.GenericResponse;

import lombok.RequiredArgsConstructor;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatControllerV1 {

	private final ChatService chatService;

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@ResponseBody
	@GetMapping(value = "/list/{boardId}", produces = APPLICATION_JSON_VALUE)
	public GenericResponse<ChatResponses> getChattingList(
			@PathVariable(name = "boardId") Long boardId) {
		List<ChatResponse> result = chatService.getAllByBoardId(boardId);
		return GenericResponse.of(true, HttpStatus.OK.value(), new ChatResponses(result), "요청 성공");
	}

	@ResponseBody
	@GetMapping(value = "/page/{boardId}", produces = APPLICATION_JSON_VALUE)
	public GenericResponse<ChatsInBoard> getChattingList(
			@PathVariable(name = "boardId") Long boardId,
			@PageableDefault(sort = "createdAt") Pageable pageable) {

		ChatsInBoard result = chatService.getByBoardId(boardId, pageable);

		return GenericResponse.of(true, HttpStatus.OK.value(), result, "요청 성공");
	}

	@MessageMapping("/msg/{boardId}")
	@SendTo("/topic/{boardId}")
	public ChatResponse sendMessage(@DestinationVariable Long boardId,
			@Header("simpSessionAttributes") Map<String, Object> sessionAttributes,
			@Payload ChatRequest chatRequest) {

		log.info("🚀 [WebSocket] 메시지 처리 중 - boardId: {}, content: {}", boardId, chatRequest.content());
		ChatResponse response = chatService.save(chatRequest, boardId, sessionAttributes);
		log.info("✅ [WebSocket] 메시지 처리 완료 - boardId: {}, response: {}", boardId, response);

		return response;
	}


}

