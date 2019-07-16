package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.AccountCreateRequestDto;
import com.darakay.testapp.testapp.dto.UserDto;
import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.entity.Tariff;
import com.darakay.testapp.testapp.entity.TariffType;
import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.repos.AccountRepository;
import com.darakay.testapp.testapp.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AccountControllerTest {

    private final static String CONTROLLER_URL = "/accounts";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void createAccount_ShouldAddCreatedAccountAtDatabaseAndReturnCorrectRedirectUri() throws Exception {
        AccountCreateRequestDto req = AccountCreateRequestDto.builder().tariffName("plain").build();
        MvcResult result = mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        long aid = Long.valueOf(result.getResponse().getRedirectedUrl().split("/")[2]);

        assertThat(accountRepository.existsById(aid)).isTrue();
    }

    @Test

    public void createAccount_ShouldAddCurrentPrincipalAsAccountOwner() throws Exception {
        AccountCreateRequestDto req = AccountCreateRequestDto.builder().tariffName("plain").build();
        MvcResult result = mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        long aid = Long.valueOf(result.getResponse().getRedirectedUrl().split("/")[2]);

        assertThat(accountRepository.findById(aid).get().getOwner().getId()).isEqualTo(1000);
    }
}