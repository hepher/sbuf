package org.sbuf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public abstract class NoSqlAbstractService<E, R extends MongoRepository<E, String>> {

    @Autowired
    protected R repository;

    @Transactional
    public E save(E entity) {
        return repository.save(entity);
    }
}
