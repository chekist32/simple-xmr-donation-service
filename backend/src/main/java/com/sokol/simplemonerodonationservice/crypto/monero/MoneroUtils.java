package com.sokol.simplemonerodonationservice.crypto.monero;

import com.sokol.simplemonerodonationservice.crypto.monero.monerosubaddress.MoneroSubaddressEntity;
import monero.wallet.model.MoneroSubaddress;

public class MoneroUtils {
    public static MoneroSubaddressEntity MoneroSubaddressToMoneroSubaddressEntityMapper(
            MoneroSubaddress moneroSubaddress,
            String primaryAddress
    ) {
        return new MoneroSubaddressEntity(
                moneroSubaddress.getAddress(),
                primaryAddress,
                moneroSubaddress.getIndex()
        );
    }
}
