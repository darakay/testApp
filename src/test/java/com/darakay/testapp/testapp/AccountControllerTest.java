package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.AccountCreateRequestDto;
import com.darakay.testapp.testapp.dto.AccountDto;
import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.repos.AccountRepository;
import com.darakay.testapp.testapp.repos.UserRepository;
import com.darakay.testapp.testapp.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "owner", password = "qwe8rty")
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
    @WithMockUser(username = "owner", password = "qwerty")
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

    @Test
    @WithMockUser(username = "owner", password = "qwerty")
    public void getAccount_ShouldReturnCorrectAccount() throws Exception {
        AccountDto expected = AccountDto.builder().sum(50).tariffName("plain").ownerId(1000).build();

        MvcResult result = mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isOk())
                .andReturn();

        AccountDto actual = mapper.readValue(result.getResponse().getContentAsString(), AccountDto.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @WithMockUser(username = "user3", password = "qwerty")
    public void getAccount_ShouldNotReturnAccount_WhenPrincipalIsNotAccountOwnerOrUser() throws Exception {
        mockMvc.perform(get("/accounts/2"))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "user3", password = "qwerty")
    public void deleteAccount_ShouldDeleteAccountById() throws Exception {
        mockMvc.perform(delete("/accounts/3"))
                .andExpect(status().isNoContent())
                .andReturn();

        assertThat(accountRepository.existsById(3L)).isFalse();
    }

    @Test
    @WithMockUser(username = "user3", password = "qwerty")
    public void deleteAccount_ShouldNotDeleteAccount_WhenPrincipalIsNotAccountOwner() throws Exception {
        mockMvc.perform(delete("/accounts/2"))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "owner", password = "qwerty")
    public void getAccountUsers_ShouldReturnAccountUsers() throws Exception {
       mockMvc.perform(get("/accounts/1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(username = "user3", password = "qwerty")
    public void getAccountUsers_ShouldNotReturnUsers_WhenPrincipalIsNotAccountOwner() throws Exception {
        mockMvc.perform(get("/accounts/1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "owner", password = "qwerty")
    public void deleteAccountUser_ShouldDeleteAccountUser() throws Exception {
        mockMvc.perform(delete("/accounts/1/users/4000"))
                .andExpect(status().isNoContent());

        Account account = accountRepository.findById(1L).get();
        assertThat(account.getUsers().size()).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = "user3", password = "qwerty")
    public void deleteAccountUser_ShouldNotDeleteAccountUser_WhenPrincipalIsNotAccountOwner() throws Exception {
        mockMvc.perform(delete("/accounts/1/users/4000"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "owner", password = "qwerty")
    public void getAccountTransaction_ShouldReturnAllAccountTransactions() throws Exception {
        mockMvc.perform(get("/accounts/1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "user3", password = "qwerty")
    public void getAccountTransaction_ShouldNotReturnAllAccountTransactions_WhenUserIsNotAccountOwner() throws Exception {
        mockMvc.perform(get("/accounts/1/transactions"))
                .andExpect(status().isForbidden());
    }
}