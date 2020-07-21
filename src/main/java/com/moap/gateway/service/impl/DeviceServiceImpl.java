package com.moap.gateway.service.impl;

import com.moap.gateway.entity.Device;
import com.moap.gateway.entity.Gateway;
import com.moap.gateway.exception.DeviceNotFoundException;
import com.moap.gateway.repository.DeviceRepository;
import com.moap.gateway.service.DeviceService;
import com.moap.gateway.service.GatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final GatewayService gatewayService;

    @Override
    public List<Device> findAll() {
        log.debug("Retrieve all devices from database");
        return deviceRepository.findAll();
    }

    @Override
    public List<Device> findByGateway(String serialnumber) {
        log.debug("Find all devices for Gateway {}", serialnumber);
        return deviceRepository.findByGateway_SerialNumber(serialnumber);
    }

    @Override
    public Device findById(long id) throws DeviceNotFoundException {
        log.debug("Find Devices by id: {}", id);
        return deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device with id: "+id+" does not exist!"));
    }

    @Override
    public Device save(Device d) {
        log.debug("Saving new device in database");
        return deviceRepository.save(d);
    }

    @Override
    public Device addDeviceToGateway(String serialNumber, Device device) {
        log.debug("Adding device {} to Gateway {}", device.getId(), serialNumber);
        Gateway target = gatewayService.findBySerialnumber(serialNumber);
        Device example = new Device().setGateway(target);
        if (deviceRepository.count(Example.of(example)) < 10) {
            device.setGateway(target);
            return deviceRepository.save(device);
        }
        throw new IllegalStateException("Limit reached!. Gateway "+ serialNumber + " can not accept more devices");
    }

    @Override
    public boolean deleteById(long id) {
        log.debug("Delete device with id {} from database!", id);
        deviceRepository.deleteById(id);
        return deviceRepository.existsById(id);
    }
}
