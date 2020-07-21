package com.moap.gateway.service;

import com.moap.gateway.entity.Gateway;

import java.util.List;

public interface GatewayService {

    Gateway save(Gateway g);

    List<Gateway> findAll();

    Gateway findBySerialnumber(String serialnumber);

    boolean deleteBySerialNumber(String serialnumber);
}
