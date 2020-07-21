package com.moap.gateway.controller;

import com.moap.gateway.entity.Device;
import com.moap.gateway.entity.dto.DeviceDto;
import com.moap.gateway.exception.DeviceNotFoundException;
import com.moap.gateway.exception.GatewayNotFoundException;
import com.moap.gateway.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/device")
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public ResponseEntity<List<DeviceDto>> findAll() {
        return ResponseEntity.ok(deviceService.findAll()
                .stream()
                .map(DeviceDto::from)
                .collect(Collectors.toList()));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DeviceDto> findById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(DeviceDto.from(deviceService.findById(id)));
        } catch (DeviceNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping(value = "/sn/{serialNumber}")
    public ResponseEntity<List<DeviceDto>> findByGateway(@PathVariable String serialNumber) {
        return ResponseEntity.ok(deviceService.findByGateway(serialNumber)
                .stream()
                .map(DeviceDto::from)
                .collect(Collectors.toList()));
    }



    @PostMapping("/{serialNumber}")
    public ResponseEntity<DeviceDto> addDeviceToGateway(@PathVariable String serialNumber, @RequestBody Device device) {
        try {
            return ResponseEntity
                    .ok(DeviceDto
                            .from(deviceService.addDeviceToGateway(serialNumber, device)));
        } catch (GatewayNotFoundException | IllegalStateException e) {
            return ResponseEntity
                    .unprocessableEntity()
                    .body(DeviceDto.from(device));
        }

    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<DeviceDto> updateDevice(@PathVariable long id, @RequestBody Device device) {
        try {
            DeviceDto created = DeviceDto.from(deviceService.save(
                    deviceService.findById(id)
            ));
            return ResponseEntity.ok(created);
        } catch (DeviceNotFoundException e) {
            return ResponseEntity
                    .unprocessableEntity()
                    .body(DeviceDto.from(device));
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Device> deleteById(@PathVariable long id) {
        return  (deviceService.deleteById(id))
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
