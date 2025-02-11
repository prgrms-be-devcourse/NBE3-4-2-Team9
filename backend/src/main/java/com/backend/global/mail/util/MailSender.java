package com.backend.global.mail.util;

import jakarta.mail.internet.MimeMessage;

/**
 * MailSender
 * <p> 메일 전송 기능 인터페이스 입니다. </p>
 * @author Kim Dong O
 */
public interface MailSender {
	/**
	 * @implSpec {@link MimeMessage}를 받아 메일 전송을 합니다.
	 * @param mimeMessage
	 */
	void send(MimeMessage mimeMessage);

	/**
	 * @implSpec 빈 MimeMessage를 생성해 반환합니다.
	 * @return {@link MimeMessage}
	 */
	MimeMessage createMimeMessage();
}
