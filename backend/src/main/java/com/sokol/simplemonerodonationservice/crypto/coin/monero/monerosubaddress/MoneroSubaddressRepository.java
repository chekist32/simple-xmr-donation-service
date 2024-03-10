package com.sokol.simplemonerodonationservice.crypto.coin.monero.monerosubaddress;

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

    @Modifying
    @Query("UPDATE MoneroSubaddressEntity SET isIdle = :isIdle WHERE subaddress = :subaddress")
    void updateIsIdleBySubaddress(@Param("subaddress") String subaddress, @Param("isIdle") boolean isIdle);
}
