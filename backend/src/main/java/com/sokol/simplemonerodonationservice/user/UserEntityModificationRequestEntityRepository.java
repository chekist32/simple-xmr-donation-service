package com.sokol.simplemonerodonationservice.user;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserEntityModificationRequestEntityRepository extends CrudRepository<UserEntityModificationRequestEntity, UUID> {
}
