package com.backend.global.mail.util;

import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;

/**
 * TemplateMaker 인터페이스 입니다.
 * <p>이메일 템플릿을 만들어 반환합니다.</p>
 *
 * @author Kim Dong O
 */
public interface TemplateMaker {

	/**
	 * @param newMimeMessage
	 * @param usernameList
	 * @param title
	 * @param templateName
	 * @return {@link MimeMessage}
	 * @implSpec 파라미터 값이 없는 메일을 전송할 때 사용하며 템플릿, 타이틀을 설정하여 반환합니다.
	 */
	MimeMessage create(MimeMessage newMimeMessage, List<String> usernameList, String title,
		TemplateName templateName, Map<String, String> htmlParameterMap);
}
