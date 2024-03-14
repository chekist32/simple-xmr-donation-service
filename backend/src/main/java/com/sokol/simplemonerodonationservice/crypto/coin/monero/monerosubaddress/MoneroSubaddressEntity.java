package com.sokol.simplemonerodonationservice.crypto.coin.monero.monerosubaddress;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "monero_subaddresses")
public class MoneroSubaddressEntity {
    @Id
    private String subaddress;
    private int index;
    @Column(nullable = false)
    private String primaryAddress;
    @Column(nullable = false)
    private Boolean isIdle = true;
    private Integer lastTransactionBlockheight;
    private String lastTransactionTxHash;
    private Timestamp lastTransactionTimestamp;

    protected MoneroSubaddressEntity() { }

    public MoneroSubaddressEntity(String subaddress, String primaryAddress, int index) {
        this.subaddress = subaddress;
        this.primaryAddress = primaryAddress;
        this.index = index;
    }

    public String getPrimaryAddress() {
        return primaryAddress;
    }

    public String getSubaddress() {
        return subaddress;
    }

    public int getIndex() {
        return index;
    }

    public boolean isIdle() {
        return isIdle;
    }

    public void setIdle(boolean idle) {
        isIdle = idle;
    }

    public Integer getLastTransactionBlockheight() {
        return lastTransactionBlockheight;
    }

    public void setLastTransactionBlockheight(Integer lastTransactionBlockheight) {
        this.lastTransactionBlockheight = lastTransactionBlockheight;
    }

    public String getLastTransactionTxHash() {
        return lastTransactionTxHash;
    }

    public void setLastTransactionTxHash(String lastTransactionTxHash) {
        this.lastTransactionTxHash = lastTransactionTxHash;
    }

    public Timestamp getLastTransactionTimestamp() {
        return lastTransactionTimestamp;
    }

    public void setLastTransactionTimestamp(Timestamp lastTransactionTimestamp) {
        this.lastTransactionTimestamp = lastTransactionTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MoneroSubaddressEntity that = (MoneroSubaddressEntity) o;

        return Objects.equals(subaddress, that.subaddress);
    }

    @Override
    public int hashCode() {
        return subaddress != null ? subaddress.hashCode() : 0;
    }
}
