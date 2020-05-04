package it.course.myblog.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@AuthenticationPrincipal
public @interface CurrentUser {

}
