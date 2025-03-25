package org.sbuf.resolver;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthTokenType {
    BEARER("Bearer");

    final String value;
}
