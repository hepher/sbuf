package org.sbuf.security.validation;


import org.sbuf.security.validation.impl.JwtSecurityRequestAuthenticator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Authenticated {
    Class<? extends SecurityRequestAuthenticator> validationClass() default JwtSecurityRequestAuthenticator.class;
    String unauthorizedMessage() default "";
}
