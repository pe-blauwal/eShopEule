package org.senju.eshopeule.exceptions;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String msg, Throwable cause) {super(msg, cause);}
    public JwtAuthenticationException(String message) {super(message);}
}
