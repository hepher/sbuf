package org.sbuf.main;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class LoggingComponent<S extends LoggingComponent<S>> {

    @Autowired
    protected S self;

    public void log(String format, Object... args) {
        log.info(format, args);
    }

    public void logWarn(String format, Object... args) {
        log.warn(format, args);
    }
    public void logError(String format, Object... args) {
        log.error(format, args);
    }
}
