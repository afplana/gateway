/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moap.gateway.controller;

import com.moap.gateway.entity.Gateway;
import com.moap.gateway.entity.dto.GatewayDto;
import com.moap.gateway.exception.DeviceNotFoundException;
import com.moap.gateway.exception.GatewayNotFoundException;
import com.moap.gateway.exception.Ipv4FormatException;
import com.moap.gateway.service.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/gateway")
public class GatewayController {

    private final GatewayService gatewayService;

    @GetMapping
    public ResponseEntity<List<GatewayDto>> findAll() {
        return ResponseEntity.ok(gatewayService.findAll()
                .stream()
                .map(GatewayDto::from)
                .collect(Collectors.toList()));
    }

    @RequestMapping(value = "/{serialnumber}")
    public ResponseEntity<GatewayDto> findBySerialNumber(@PathVariable String serialnumber) {
        try {
            return ResponseEntity
                    .ok(GatewayDto
                            .from(gatewayService.findBySerialnumber(serialnumber)));

        } catch (GatewayNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping
    public ResponseEntity<GatewayDto> save(@RequestBody Gateway gateway) {
        try {
            return ResponseEntity
                    .ok(GatewayDto
                            .from(gatewayService.save(gateway)));
        } catch (Ipv4FormatException e) {
            return ResponseEntity
                    .unprocessableEntity()
                    .body(GatewayDto.from(gateway));
        }
    }

    @PutMapping(value = "/{serialNumber}")
    public ResponseEntity<GatewayDto> update(@PathVariable String serialNumber, @RequestBody Gateway gateway) {
        try {
            GatewayDto created = GatewayDto.from(gatewayService.save(
                    gatewayService.findBySerialnumber(serialNumber)
            ));
            return ResponseEntity.ok(created);
        } catch (DeviceNotFoundException | Ipv4FormatException e) {
            return ResponseEntity
                    .unprocessableEntity()
                    .body(GatewayDto.from(gateway));
        }
    }

    @DeleteMapping(value = "/{serialNumber}")
    public ResponseEntity<?> delete(@PathVariable String serialNumber) {

            return  (gatewayService.deleteBySerialNumber(serialNumber))
                    ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                    : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
