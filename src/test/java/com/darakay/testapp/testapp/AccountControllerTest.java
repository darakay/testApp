package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.UserDto;
import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.entity.Tariff;
import com.darakay.testapp.testapp.entity.TariffType;
import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.repos.AccountRepository;
import com.darakay.testapp.testapp.service.UserService;
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
    public void getTariffByAccountId() throws Exception {
        String uri = CONTROLLER_URL + "/1/tariff";

        Tariff expectedTariff = new Tariff("plain", "debit", 0.07, 50000, 30000);

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

    @Test
    public void getAccountUsers() throws Exception {
        String uri = CONTROLLER_URL + "/1/users";

        MvcResult result = mockMvc.perform(get(uri))
                .andExpect(status().isOk()).andReturn();

        List<UserDto> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<UserDto>>() {
        });

        assertThat(actual.size()).isEqualTo(3);
        assertThat(actual).asList().contains(new UserDto(2000, "AccountUser1", ""),
                new UserDto(3000, "AccountUser2", ""),
                new UserDto(4000, "AccountUser3", ""));
    }

    @Test
    public void getAccountOwner() throws Exception {
        String uri = CONTROLLER_URL + "/1/owner";

        MvcResult result = mockMvc.perform(get(uri))
                .andExpect(status().isOk()).andReturn();

        UserDto actualOwner = mapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

        assertThat(actualOwner).isEqualTo(new UserDto(1000, "AccountOwner", ""));
    }

    @Test
    public void closeAccount() throws Exception {
        User user = userService.logUp(new User("first", "last", "234", "111"));

        Account account = userService.createAccountForUser(user.getId(), TariffType.plain());

        String uri = CONTROLLER_URL + "/" + account.getId();

        mockMvc.perform(delete(uri))
                .andExpect(status().isOk()).andReturn();

        assertThat(accountRepository.existsById(account.getId())).isFalse();
    }
}