package com.sokol.simplemonerodonationservice.crypto.coin.monero.monerosubaddress;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoneroSubaddressRepository extends CrudRepository<MoneroSubaddressEntity, String> {
    List<MoneroSubaddressEntity> findAllByPrimaryAddress(String primaryAddress);

    Optional<MoneroSubaddressEntity> findFirstByPrimaryAddressAndIsIdleTrue(String primaryAddress);

    @Modifying
    @Query("UPDATE MoneroSubaddressEntity SET isIdle = :isIdle WHERE subaddress = :subaddress")
    void updateIsIdleBySubaddress(@Param("subaddress") String subaddress, @Param("isIdle") boolean isIdle);
}
