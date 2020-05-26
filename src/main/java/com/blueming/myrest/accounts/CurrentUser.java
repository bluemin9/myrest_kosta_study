package com.blueming.myrest.accounts;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 런타임에 사용하는 annotation 만들기
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
// 아래처럼 선언 시 로그인 안한 guest 계정일 경우 에러가 터짐
//@AuthenticationPrincipal(expression = "account")
// guest 계정도 사용하기 위해 아래처럼 선언
// anonymousUser(guest) 인 경우면 null, 로그인 했으면 account 클래스 mapping
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
public @interface CurrentUser {
}
