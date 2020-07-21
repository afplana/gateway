package com.moap.gateway.entity.dto;

import com.moap.gateway.entity.Gateway;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@Value
@AllArgsConstructor
public class GatewayDto {
  
    
    String serialnumber;
    String name;
    String IPv4address;
    Set<DeviceDto> devices;

    public static GatewayDto from (Gateway gateway) {
        return new GatewayDto(
                gateway.getSerialNumber(),
                gateway.getName(),
                gateway.getIPv4Address(),
                gateway.getDevices() != null ?
                        gateway.getDevices().stream().map(DeviceDto::from).collect(Collectors.toSet()):
                        Collections.EMPTY_SET
        );
    }
}
