package org.sbuf.resolver;

import org.sbuf.util.LabelUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface UserIp {
    String value() default LabelUtils.USER_IP;
}
