package com.backend.domain.chat.mapper;

import com.backend.domain.chat.dto.request.ChatRequest;
import com.backend.domain.chat.dto.response.ChatResponse;
import com.backend.domain.chat.entity.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.ReportingPolicy.IGNORE;


@Mapper(componentModel = "spring", unmappedSourcePolicy = IGNORE, unmappedTargetPolicy = IGNORE)
public interface ChatMapper {

    @Mapping(target = "content", source = "chatRequest.content")
    @Mapping(target = "userId", source = "chatRequest.userId")
    @Mapping(target = "type", source = "chatRequest.type")
    @Mapping(target = "boardId", source = "boardId")
    Chat toChat(ChatRequest chatRequest, Long boardId);

    @Mapping(target = "type", source = "chat.type")
    @Mapping(target = "userId", source = "chat.userId")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "profileImg", source = "profileImg")
    @Mapping(target = "createdAt", source = "chat.createdAt")
    @Mapping(target = "content", source = "chat.content")
    ChatResponse toChatResponse(Chat chat, String username, String profileImg);
}
