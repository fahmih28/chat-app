package com.rabbani.chatapp.v1.repository.delegator;

import com.rabbani.chatapp.v1.entity.UserEntity;
import com.rabbani.chatapp.v1.entity.query.UserAuthQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositoryDelegator extends JpaRepository<UserEntity, String> {

    boolean existsByEmailIgnoreCase(String email);

    @Query(nativeQuery = true, value = "select u.id,u.first_name firstName,u.last_name lastName,u.password,ur.role_id roleId,u.email,r.name roleName,r.qualifier roleQualifier from \"user\" u inner join user_role ur  on ur.user_id = u.id" +
            " inner join role r on r.id = ur.role_id" +
            " where u.email = ?1 limit 1")
    Optional<UserAuthQuery> queryAuthByEmail(String email);


    @Query(nativeQuery = true, value = "select u.id,u.first_name firstName,u.last_name lastName,u.password,ur.role_id roleId,u.email,r.name roleName,r.qualifier roleQualifier from \"user\" u inner join user_role ur  on ur.user_id = u.id" +
            " inner join role r on r.id = ur.role_id" +
            " where u.id = ?1 limit 1")
    Optional<UserAuthQuery> queryAuthById(String id);
}
