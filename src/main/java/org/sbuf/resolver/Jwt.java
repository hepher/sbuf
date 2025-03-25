package org.sbuf.resolver;



import org.sbuf.util.LabelUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Jwt {
    boolean required() default true;
    String value() default LabelUtils.JWT_VALUE;
    AuthTokenType tokenType() default AuthTokenType.BEARER;
    ParameterType type() default ParameterType.HEADER;
}
