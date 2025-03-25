package org.sbuf.security.jwt.impl;


import org.sbuf.security.jwt.Jwt;

import java.io.IOException;

public class StandardJwt extends Jwt<JwtHeader, JwtPayload> {

    public StandardJwt(String jwt) throws IOException {
        super(jwt);
    }
}
