package com.rabbani.chatapp.v1.repository;

import com.rabbani.chatapp.v1.entity.query.UserAuthQuery;

import java.util.Optional;

public interface UserRepository {

    boolean existsByEmailIgnoreCase(String email);

    Optional<UserAuthQuery> findByEmail(String email);

    Optional<UserAuthQuery> findById(String id);
}
