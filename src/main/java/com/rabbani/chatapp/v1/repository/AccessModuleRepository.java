package com.rabbani.chatapp.v1.repository;

import com.rabbani.chatapp.v1.entity.query.AccessModuleSessionQuery;

import java.util.List;

public interface AccessModuleRepository {

    List<AccessModuleSessionQuery> findAllAccessModules();

}
