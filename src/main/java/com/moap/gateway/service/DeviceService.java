package com.moap.gateway.service;

import com.moap.gateway.entity.Device;

import java.util.List;

public interface DeviceService {

	List<Device> findAll();
    
    List<Device> findByGateway(String serialnumber);

	Device findById(long id);

	Device save(Device d);
	
	Device addDeviceToGateway(String serialNumber, Device device);

	boolean deleteById(long id);
}
