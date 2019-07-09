package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.tariff.Tariff;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AccountControllerTest {

    private final static String CONTROLLER_URL = "/accounts";

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void getTariffByAccountId() throws Exception {
        String uri = CONTROLLER_URL + "/1/tariff";

        Tariff expectedTariff = new Tariff(1,"plain", "debit", 0.07, 50000, 30000);

        mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(mapper.writeValueAsString(expectedTariff)));
    }

    @Test
    public void getTariffByAccountId_shouldReturnNotFound_ThenAccountIdIsNotExist() throws Exception {
        String uri = CONTROLLER_URL + "/3456/tariff";

        mockMvc.perform(get(uri))
                .andExpect(status().isNotFound());
    }
}