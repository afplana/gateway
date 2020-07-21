package com.moap.gateway.repository;

import com.moap.gateway.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByGateway_SerialNumber(String serialNumber);
}
