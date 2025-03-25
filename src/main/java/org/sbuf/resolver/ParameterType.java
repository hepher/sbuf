package org.sbuf.resolver;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParameterType {
    HEADER("header"), BODY("body"), QUERY("query"), PATH("path");

    final String value;
}
