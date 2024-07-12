package com.rabbani.chatapp.v1.repository.delegator;

import com.rabbani.chatapp.v1.entity.AccessModuleEntity;
import com.rabbani.chatapp.v1.entity.query.AccessModuleSessionQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessModuleRepositoryDelegator extends JpaRepository<AccessModuleEntity,String> {

    @Query(nativeQuery = true,value = "select code,qualifier from access_module")
    List<AccessModuleSessionQuery> findAllAccessModules();
}
