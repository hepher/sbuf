package org.sbuf.resolver;


import org.sbuf.util.LabelUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface UserId {
    String value() default LabelUtils.USER_ID_PARAMETER_VALUE;
    String description() default "UserId for the in-app user";
    boolean required() default false;
    ParameterType type() default ParameterType.HEADER;
}
