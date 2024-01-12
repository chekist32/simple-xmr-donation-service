package com.sokol.simplemonerodonationservice.monero.monerosubaddress;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MoneroSubaddressRepository extends CrudRepository<MoneroSubaddressEntity, Integer> {
    List<MoneroSubaddressEntity> findAllByPrimaryAddress(String primaryAddress);
    Optional<MoneroSubaddressEntity> findFirstByPrimaryAddressAndIsIdleTrue(String primaryAddress);
    Optional<MoneroSubaddressEntity> findByIndex(int moneroSubaddressIndex);
    @Modifying
    @Transactional
    @Query("UPDATE MoneroSubaddressEntity SET isIdle = :isIdle WHERE id = :id ")
    void updateIsIdleById(@Param("id") Integer id, @Param("isIdle") boolean isIdle);
    @Modifying
    @Transactional
    @Query("UPDATE MoneroSubaddressEntity SET isIdle = :isIdle WHERE subaddress = :subaddress ")
    void updateIsIdleBySubaddress(@Param("subaddress") String subaddress, @Param("isIdle") boolean isIdle);

}
