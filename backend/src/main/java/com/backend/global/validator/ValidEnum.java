package com.backend.global.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ValidEnum
 * <p>검증할 Enum 필드에 사용할 어노테이션 <br>
 * null 옵션을 true로 설정시 일치하는 Enum Value가 없으면 null로 초기화 <br>
 * enumClass 옵션은 필수로 설정할 것 <br>
 * Validation: {@link EnumValidator}</p>
 * @author Kim Dong O
 */
@Constraint(validatedBy = EnumValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEnum {
	String message() default "요청 값이 유효하지 않습니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	Class<? extends Enum<?>> enumClass();

	boolean nullable() default false;
}
