package org.sbuf.security.jwt;

import org.sbuf.model.dto.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Jwks extends AbstractEntity {

    private List<Jwk> keys;

    public void addKey(Jwk jwk) {
        if (keys == null) {
            keys = new ArrayList<>();
        }
        keys.add(jwk);
    }

    public Jwk findJwkByKey(String key) {
        if (StringUtils.isBlank(key) || keys == null) {
            return null;
        }

        for (Jwk jwk : keys) {
            if (jwk.getKeyId().equals(key))
                return jwk;
        }

        return null;
    }
}
