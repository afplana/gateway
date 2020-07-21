package com.moap.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.moap.gateway.EntitySupplier;
import com.moap.gateway.entity.Device;
import com.moap.gateway.entity.Gateway;
import com.moap.gateway.exception.GatewayNotFoundException;
import com.moap.gateway.exception.Ipv4FormatException;
import com.moap.gateway.repository.DeviceRepository;
import com.moap.gateway.repository.GatewayRepository;
import com.moap.gateway.service.impl.GatewayServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.List;
import java.util.Set;

import static com.moap.gateway.EntitySupplier.getLIst;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@WebMvcTest(GatewayController.class)
class GatewayControllerTest {


    @Autowired
    MockMvc mvc;

    @MockBean
    GatewayServiceImpl gatewayService;

    @MockBean
    GatewayRepository gatewayRepository;

    @MockBean
    DeviceRepository deviceRepository;

    JacksonTester<Gateway> gatewayJacksonTester;

    private final List<Device> mockDevices = new ArrayList<>();
    Gateway mockGateway;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        JacksonTester.initFields(this, mapper);
        mockGateway = EntitySupplier.getOne("19877", "gateway_1", "10.10.1.0").get();
        mockDevices.addAll(getLIst(0L, "MockSeller", mockGateway).get());
        mockGateway.setDevices(Set.copyOf(mockDevices));
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(gatewayService);
    }

    @Test
    void findAll_TestStatusOK_WhenServiceReturnDevices() throws Exception {
        when(gatewayService.findAll()).thenReturn(singletonList(mockGateway));
        mvc.perform(get("/gateway")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].serialnumber", is("19877")))
                .andExpect(jsonPath("$.[0].name", is("gateway_1")))
                .andExpect(jsonPath("$.[0].ipv4address", is("10.10.1.0")));
    }

    @Test
    void findBySerialNumber_TestStatusOK_WhenServiceReturnDevice() throws Exception {
        when(gatewayService.findBySerialnumber("19877")).thenReturn(mockGateway);
        mvc.perform(get("/gateway/19877")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serialnumber", is("19877")))
                .andExpect(jsonPath("$.name", is("gateway_1")))
                .andExpect(jsonPath("$.ipv4address", is("10.10.1.0")));
    }

    @Test
    void findBySerialNumber_TestStatusNoContent_WhenServiceTrowEx() throws Exception {
        when(gatewayService.findBySerialnumber("19876")).thenThrow(new GatewayNotFoundException("Not Found"));
        mvc.perform(get("/gateway/19876")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void save_TestStatusOK_WhenServiceSaveCorrectly() throws Exception {
        when(gatewayService.save(any())).thenReturn(mockGateway);
        mvc.perform(post("/gateway")
                .contentType("application/json;charset=UTF-8")
                .content(gatewayJacksonTester.write(mockGateway).getJson())
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serialnumber", is("19877")))
                .andExpect(jsonPath("$.name", is("gateway_1")))
                .andExpect(jsonPath("$.ipv4address", is("10.10.1.0")));
    }

    @Test
    void save_TestStatusUnprocessableEntity_WhenWrongIpAddrFromat() throws Exception {
        mockGateway.setIPv4Address("1234.1235.677.3");
        when(gatewayService.save(any())).thenThrow(new Ipv4FormatException("Bad Format"));
        mvc.perform(post("/gateway")
                .contentType("application/json;charset=UTF-8")
                .content(gatewayJacksonTester.write(mockGateway).getJson())
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void update() throws Exception {
        mockGateway.setIPv4Address("1234.1235.677.3");
        when(gatewayService.save(any())).thenThrow(new Ipv4FormatException("Bad Format"));
        mvc.perform(post("/gateway")
                .contentType("application/json;charset=UTF-8")
                .content(gatewayJacksonTester.write(mockGateway).getJson())
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void delete() {
    }
}