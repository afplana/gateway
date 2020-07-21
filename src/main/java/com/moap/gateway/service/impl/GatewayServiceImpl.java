package com.moap.gateway.service.impl;

import com.moap.gateway.entity.Gateway;
import com.moap.gateway.exception.GatewayNotFoundException;
import com.moap.gateway.exception.Ipv4FormatException;
import com.moap.gateway.repository.GatewayRepository;
import com.moap.gateway.service.GatewayService;
import com.moap.gateway.utils.Ipv4Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayServiceImpl implements GatewayService {

    private final GatewayRepository gatewayRepository;

    @Override
    public Gateway save(Gateway g) {
        if(Ipv4Utils.validIPv4(g.getIPv4Address())){
            return gatewayRepository.save(g);
        }
        throw new Ipv4FormatException("Gateway "+g.getSerialNumber()+" contains an invalid ipv4 address");
    }

    @Override
    public List<Gateway> findAll() {
        return gatewayRepository.findAll();
    }

    @Override
    public Gateway findBySerialnumber(String serialnumber) {
        return gatewayRepository.findBySerialNumber(serialnumber)
                .orElseThrow(() -> new GatewayNotFoundException("Gateway "+serialnumber+" does not exist in database"));
    }


    @Override
    public boolean deleteBySerialNumber(String serialnumber) {
        gatewayRepository.deleteById(serialnumber);
        return gatewayRepository.existsById(serialnumber);
    }

    private boolean validIPv4(String ip) {
        String regex = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        Pattern valid = Pattern.compile(regex);
        return valid.pattern().matches(ip);
    }
}
