package com.rabbani.chatapp.v1.repository;

import com.rabbani.chatapp.v1.entity.query.AccessModuleSessionQuery;
import com.rabbani.chatapp.v1.repository.delegator.AccessModuleRepositoryDelegator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DefaultAccessModuleRepository implements AccessModuleRepository {

    private final AccessModuleRepositoryDelegator accessModuleRepositoryDelegator;

    @Override
    public List<AccessModuleSessionQuery> findAllAccessModules() {
        return accessModuleRepositoryDelegator.findAllAccessModules();
    }

}
