package org.sbuf.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("application")
public class ApplicationProperties {

    private String name;
    private String version;
    private String timestamp;
    private String namespace;
    private String container;
}
