package com.sokol.simplemonerodonationservice.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIsEnabledFalse(String email);

    @Query("FROM UserEntity WHERE email = :principal or username = :principal")
    Optional<UserEntity> findByPrincipal(@Param("principal") String principal);

    long countByIsEnabledTrue();
}
