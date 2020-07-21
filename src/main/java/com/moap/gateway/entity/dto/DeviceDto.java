package com.moap.gateway.entity.dto;

import com.moap.gateway.entity.Device;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@AllArgsConstructor
public class DeviceDto {

    String seller;
    OffsetDateTime creationDate;


    public static DeviceDto from(Device device) {
        return new DeviceDto(
                device.getSeller(),
                device.getCreationDate());
    }
}
