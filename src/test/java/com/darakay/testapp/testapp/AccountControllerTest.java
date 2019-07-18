package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.AccountCreateRequestDto;
import com.darakay.testapp.testapp.dto.AccountDto;
import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.repos.AccountRepository;
import com.darakay.testapp.testapp.security.jwt.JwtTokenService;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    private static String URL = "/api/accounts";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtTokenService tokenService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void createAccount_ShouldAddCreatedAccountAtDatabaseAndReturnCorrectRedirectUri() throws Exception {
        String token = tokenService.createAccessToken(1000L, 0);

        AccountCreateRequestDto req = AccountCreateRequestDto.builder().tariffName("plain").build();
        MvcResult result = mockMvc.perform(post(URL)
                .header("XXX-AccessToken", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("/api/accounts/*"))
                .andReturn();

        long aid = Long.valueOf(result.getResponse().getRedirectedUrl().split("/")[3]);

        assertThat(accountRepository.existsById(aid)).isTrue();
    }

    @Test
    public void createAccount_ShouldAddCurrentPrincipalAsAccountOwner() throws Exception {
        String token = tokenService.createAccessToken(1000L, 0);

        AccountCreateRequestDto req = AccountCreateRequestDto.builder().tariffName("plain").build();
        MvcResult result = mockMvc.perform(post(URL)
                .header("XXX-AccessToken", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        long aid = Long.valueOf(result.getResponse().getRedirectedUrl().split("/")[3]);

        assertThat(accountRepository.findById(aid).get().getOwner().getId()).isEqualTo(1000);
    }

    @Test
    public void getAccount_ShouldReturnCorrectAccount() throws Exception {
        String token = tokenService.createAccessToken(1000L, 0);

        AccountDto expected = AccountDto.builder().sum(50).tariffName("plain").ownerId(1000).build();

        MvcResult result = mockMvc.perform(get(URL+"/1")
                .header("XXX-AccessToken", token))
                .andExpect(status().isOk())
                .andReturn();

        AccountDto actual = mapper.readValue(result.getResponse().getContentAsString(), AccountDto.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getAccount_ShouldNotReturnAccount_WhenPrincipalIsNotAccountOwnerOrUser() throws Exception {
        String token = tokenService.createAccessToken(3000L, 0);

        mockMvc.perform(get(URL+"/2")
                .header("XXX-AccessToken", token))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void deleteAccount_ShouldDeleteAccountById() throws Exception {
        String token = tokenService.createAccessToken(3000L, 0);

        AccountCreateRequestDto req = AccountCreateRequestDto.builder().tariffName("plain").build();

        MvcResult result = mockMvc.perform(post("/api/accounts")
                .header("XXX-AccessToken", token)
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsString(req))).andReturn();

        mockMvc.perform(delete(result.getResponse().getRedirectedUrl())
                .header("XXX-AccessToken", token))
                .andExpect(status().isNoContent())
                .andReturn();

        long accountId = Long.valueOf(result.getResponse().getRedirectedUrl().split("/")[3]);

        assertThat(accountRepository.existsById(accountId)).isTrue();
    }

    @Test
    public void deleteAccount_ShouldNotDeleteAccount_WhenPrincipalIsNotAccountOwner() throws Exception {
        String token = tokenService.createAccessToken(3000L, 0);
        mockMvc.perform(delete(URL+"/2")
                .header("XXX-AccessToken", token))
                .andExpect(status().isForbidden());

    }

    @Test
    public void getAccountUsers_ShouldReturnAccountUsers() throws Exception {
       String token = tokenService.createAccessToken(1000L, 0);
       int userCount = accountRepository.findById(1L).get().getUsers().size();
       mockMvc.perform(get(URL+"/1/users")
               .header("XXX-AccessToken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(userCount)));
    }

    @Test
    public void getAccountUsers_ShouldNotReturnUsers_WhenPrincipalIsNotAccountOwner() throws Exception {
        String token = tokenService.createAccessToken(3000L, 0);
        mockMvc.perform(get(URL+"/1/users")
                .header("XXX-AccessToken", token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteAccountUser_ShouldDeleteAccountUser() throws Exception {
        String token = tokenService.createAccessToken(1000L, 0);
        mockMvc.perform(delete(URL+"/1/users/4000")
                .header("XXX-AccessToken", token))
                .andExpect(status().isNoContent());

        Account account = accountRepository.findById(1L).get();
        assertThat(account.getUsers().size()).isEqualTo(1);
    }

    @Test
    public void deleteAccountUser_ShouldNotDeleteAccountUser_WhenPrincipalIsNotAccountOwner() throws Exception {
        String token = tokenService.createAccessToken(3000L, 0);
        mockMvc.perform(delete(URL+"/1/users/4000")
                .header("XXX-AccessToken", token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getAccountTransaction_ShouldReturnAllAccountTransactions() throws Exception {
        String token = tokenService.createAccessToken(1000L, 0);
        mockMvc.perform(get(URL+"/1/transactions")
                .header("XXX-AccessToken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andReturn();
    }

    @Test
    public void getAccountTransaction_ShouldNotReturnAllAccountTransactions_WhenUserIsNotAccountOwner() throws Exception {
        String token = tokenService.createAccessToken(3000L, 0);
        mockMvc.perform(get(URL+"/1/transactions")
                .header("XXX-AccessToken", token))
                .andExpect(status().isForbidden());
    }
}