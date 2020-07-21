package com.moap.gateway.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.moap.gateway.EntitySupplier;
import com.moap.gateway.entity.Device;
import com.moap.gateway.entity.Gateway;
import com.moap.gateway.exception.DeviceNotFoundException;
import com.moap.gateway.exception.GatewayNotFoundException;
import com.moap.gateway.service.impl.DeviceServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.moap.gateway.EntitySupplier.getLIst;
import static com.moap.gateway.EntitySupplier.getOne;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ActiveProfiles("test")
@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    DeviceServiceImpl deviceService;

    JacksonTester<Device> deviceJacksonTester;

    private final List<Device> devices = new ArrayList<>();
    private final Gateway gateway = getOne("111", "MockGateway", "10.10.1.1").get();

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JacksonTester.initFields(this, mapper);

        devices.addAll(getLIst(0L, "MockSeller", gateway).get());
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(deviceService);
    }

    @Test
    void findAll_TestStatusOK_WhenDataInDB() throws Exception {
        when(deviceService.findAll()).thenReturn(devices);
        mvc.perform(get("/device")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].seller", is("MockSeller_0")))
                .andExpect(jsonPath("$.[1].seller", is("MockSeller_1")))
                .andExpect(jsonPath("$.[2].seller", is("MockSeller_2")))
                .andExpect(jsonPath("$.[3].seller", is("MockSeller_3")))
                .andExpect(jsonPath("$.[4].seller", is("MockSeller_4")));
    }

    @Test
    void findAll_TestStatusOk_WhenNoDataInDB() throws Exception {
        when(deviceService.findAll()).thenReturn(emptyList());
        mvc.perform(get("/device")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(emptyList())));
    }

    @Test
    void findById_TestStatusOK_WhenDeviceInDB() throws Exception {
        when(deviceService.findById(3)).thenReturn(devices.get(3));
        mvc.perform(get("/device/3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller", is("MockSeller_3")));
    }

    @Test
    void findById_TestStatusNoContent_WhenDeviceNotInDB() throws Exception {
        when(deviceService.findById(1000)).thenThrow(new DeviceNotFoundException("Not Found"));
        mvc.perform(get("/device/1000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void findByGateway_TestStatusOK_WhenCorrectParamProvided() throws Exception {
        when(deviceService.findByGateway("111")).thenReturn(devices);
        mvc.perform(get("/device/sn/111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].seller", is("MockSeller_0")))
                .andExpect(jsonPath("$.[1].seller", is("MockSeller_1")))
                .andExpect(jsonPath("$.[2].seller", is("MockSeller_2")))
                .andExpect(jsonPath("$.[3].seller", is("MockSeller_3")))
                .andExpect(jsonPath("$.[4].seller", is("MockSeller_4")));
    }

    @Test
    void findByGateway_TestStatusOK_WhenInCorrectParamProvided() throws Exception {
        when(deviceService.findByGateway("1111")).thenReturn(Collections.emptyList());
        mvc.perform(get("/device/sn/1111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(Collections.emptyList())));
    }

    @Test
    void addDeviceToGateway_TestStatusOK_WhenServiceReturnDevice() throws Exception {
        Device device = EntitySupplier.getOne(1L, "MockSeller_100", gateway).get();
        when(deviceService.addDeviceToGateway(anyString(), ArgumentMatchers.any())).thenReturn(device);

        mvc.perform(post("/device/111")
                .content(deviceJacksonTester.write(device).getJson())
                .contentType("application/json;charset=UTF-8")
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller", is("MockSeller_100")));
    }

    @Test
    void addDeviceToGateway_TestStatusUnprocessableEntity_WhenServiceThrowsEx() throws Exception {
        Device device = EntitySupplier.getOne(1L, "MockSeller_100", gateway).get();
        when(deviceService.addDeviceToGateway(anyString(), ArgumentMatchers.any()))
                .thenThrow(new GatewayNotFoundException("Not Found"));

        mvc.perform(post("/device/111")
                .content(deviceJacksonTester.write(device).getJson())
                .contentType("application/json;charset=UTF-8")
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.seller", is("MockSeller_100")));
    }

    @Test
    void updateDevice_TestStatusOK_WhenServiceReturnUpdatedDevice() throws Exception {
        Device device = EntitySupplier.getOne(1L, "MockSeller_100", gateway).get();
        Device updated = EntitySupplier.getOne(1L, "MockSeller_200", gateway).get();
        when(deviceService.findById(1L)).thenReturn(device);
        when(deviceService.save(device)).thenReturn(updated);

        mvc.perform(put("/device/1")
                .content(deviceJacksonTester.write(updated).getJson())
                .contentType("application/json;charset=UTF-8")
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller", is("MockSeller_200")));
    }

    @Test
    void updateDevice_TestStatusUnprocessableEntity_WhenServiceTrowEx() throws Exception {
        Device updated = EntitySupplier.getOne(1L, "MockSeller_200", gateway).get();
        when(deviceService.findById(1L)).thenThrow(new DeviceNotFoundException("NotFound"));

        mvc.perform(put("/device/1")
                .content(deviceJacksonTester.write(updated).getJson())
                .contentType("application/json;charset=UTF-8")
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.seller", is("MockSeller_200")));
    }

    @Test
    void deleteById_TestStatusOK_WhenServiceReturnTrue() throws Exception {
        when(deviceService.deleteById(1)).thenReturn(true);
        mvc.perform(delete("/device/1")).andExpect(status().isNoContent());
    }

    @Test
    void deleteById_TestStatusOK_WhenServiceReturnFLase() throws Exception {
        when(deviceService.deleteById(1)).thenReturn(false);
        mvc.perform(delete("/device/1")).andExpect(status().isInternalServerError());
    }
}