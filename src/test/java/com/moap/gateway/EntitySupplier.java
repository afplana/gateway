package com.moap.gateway;

import com.moap.gateway.entity.Device;
import com.moap.gateway.entity.Gateway;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntitySupplier {

    public static Supplier<Device> getOne(long id, String seller, Gateway gateway) {
        return () -> new Device()
                .setId(1L)
                .setCreationDate(OffsetDateTime.now())
                .setSeller(seller)
                .setGateway(gateway);
    }

    public static Supplier<List<Device>> getLIst(long id, String seller, Gateway gateway) {
        return () -> Stream.of(id, id +1, id + 2, id + 3, id + 4 )
                .map(aLong -> new Device()
                        .setId(aLong)
                        .setCreationDate(OffsetDateTime.now())
                        .setSeller(seller.concat("_") + aLong)
                        .setGateway(gateway))
                .collect(Collectors.toList());
    }

    public static Supplier<Gateway> getOne(String serial, String name, String ipv4) {
        return () -> new Gateway()
                .setSerialNumber(serial)
                .setName(name)
                .setIPv4Address(ipv4);
    }
}
