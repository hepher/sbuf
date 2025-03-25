package org.sbuf.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Component;

@Component
@Configuration
@EnableMongoAuditing
@EnableMongoRepositories
@ConditionalOnExpression("${sbuf.config.tracing.on-mongo:false}")
public class MongoConfig {
}