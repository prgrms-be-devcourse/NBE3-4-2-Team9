//package com.backend.domain.chat.handler;
//
//import com.backend.domain.chat.exception.WebSocketException;
//import com.backend.domain.user.entity.SiteUser;
//import com.backend.domain.user.repository.UserRepository;
//import com.backend.global.exception.GlobalErrorCode;
//import com.backend.global.exception.GlobalException;
//import com.backend.standard.util.JwtUtil;
//import java.util.Map;
//import java.util.Objects;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class StompHandler implements ChannelInterceptor {
//
//    public static final String DEFAULT_PATH = "/topic/";
//
//    private final UserRepository userRepository;
//    private final JwtUtil jwtUtil;
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        StompCommand command = accessor.getCommand();
//
//        if (StompCommand.CONNECT.equals(command)) {
//            SiteUser user = getUserByAuthorizationHeader(accessor.getFirstNativeHeader("Authorization"));
//            setValue(accessor, "userId", user.getId());
//            setValue(accessor, "username", user.getName());
//        } else if (StompCommand.SUBSCRIBE.equals(command)) {
//            Long userId = (Long) getValue(accessor, "userId");
//            Long boardId = parseBoardIdFromPath(accessor);
//            log.debug("userId : " + userId + " boardId : " + boardId);
//            setValue(accessor, "boardId", boardId);
//            validateUserInBoard(userId, boardId);
//        } else if (StompCommand.DISCONNECT == command) {
//            Long userId = (Long) getValue(accessor, "userId");
//            log.info("DISCONNECTED userId : {}", userId);
//        }
//
//        log.info("header : " + message.getHeaders());
//        log.info("message:" + message);
//
//        return message;
//    }
//
//    private SiteUser getUserByAuthorizationHeader(String authHeaderValue) {
//        String accessToken = getTokenByAuthorizationHeader(authHeaderValue);
//        Long userId = jwtUtil.getUserId(accessToken);
//
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
//    }
//
//    private String getTokenByAuthorizationHeader(String authHeaderValue) {
//        if (Objects.isNull(authHeaderValue) || authHeaderValue.isBlank()) {
//            throw new WebSocketException("authHeaderValue: " + authHeaderValue);
//        }
//        return authHeaderValue.replace("Bearer ", "");
//    }
//
//    private Long parseBoardIdFromPath(StompHeaderAccessor accessor) {
//        String destination = accessor.getDestination();
//        return Long.parseLong(destination.substring(DEFAULT_PATH.length()));
//    }
//
//    private void validateUserInBoard(Long userId, Long boardId) {
//        // Todo boardUser 개발 후 검증 로직 작성
//    }
//
//    private Object getValue(StompHeaderAccessor accessor, String key) {
//        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
//        Object value = sessionAttributes.get(key);
//        if (Objects.isNull(value)) {
//            throw new WebSocketException(key + " 에 해당하는 값이 없습니다.");
//        }
//        return value;
//    }
//
//    private void setValue(StompHeaderAccessor accessor, String key, Object value) {
//        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
//        sessionAttributes.put(key, value);
//    }
//
//    private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) {
//        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
//        if (Objects.isNull(sessionAttributes)) {
//            throw new WebSocketException("SessionAttributes가 null입니다.");
//        }
//        return sessionAttributes;
//    }
//}
