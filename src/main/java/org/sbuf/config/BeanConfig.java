package org.sbuf.config;

import org.sbuf.util.ApplicationContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class BeanConfig {

    @Autowired
    public void setContext(ApplicationContext applicationcontext) {
        ApplicationContextUtils.setApplicationContext(applicationcontext);
    }

    @Autowired
    public void setConfigurableBeanFactory(ConfigurableBeanFactory beanFactory) {
        ApplicationContextUtils.setConfigurableBeanFactory(beanFactory);
    }
}
