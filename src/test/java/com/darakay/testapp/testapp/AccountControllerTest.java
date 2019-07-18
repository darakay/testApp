package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.AccountCreateRequestDto;
import com.darakay.testapp.testapp.dto.AccountDto;
import com.darakay.testapp.testapp.dto.TransactionDto;
import com.darakay.testapp.testapp.dto.UserDto;
import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.entity.Transaction;
import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.repos.AccountRepository;
import com.darakay.testapp.testapp.repos.UserRepository;
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

import java.util.List;
import java.util.stream.Collectors;

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
    private JwtTokenService jwtTokenService;


    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void createAccount_ShouldAddCreatedAccountToDatabaseAndReturnCorrectRedirectUri() throws Exception {
        String token = createAccessToken(1000);

        AccountCreateRequestDto req = AccountCreateRequestDto.builder().tariffName("plain").build();
        MvcResult result = mockMvc.perform(post(URL)
                .header("XXX-AccessToken", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("/api/accounts/{spring:[0-9]+}"))
                .andReturn();

        long aid = extractId(result.getResponse().getRedirectedUrl(), 3);

        assertThat(accountRepository.existsById(aid)).isTrue();
    }

    @Test
    public void createAccount_ShouldCreateAccountForPrincipal() throws Exception {

        String token = createAccessToken(1000);
        AccountCreateRequestDto req = AccountCreateRequestDto.builder().tariffName("plain").build();

        MvcResult result = mockMvc.perform(post(URL)
                .header("XXX-AccessToken", token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(req)))
                .andReturn();

        long aid = extractId(result.getResponse().getRedirectedUrl(), 3);

        assertThat(accountRepository.findById(aid).get().getOwner().getId()).isEqualTo(1000);
    }

    @Test
    public void getAccount_ShouldReturnCorrectAccountDto() throws Exception {
        String token =createAccessToken(1000);

        AccountDto expected = AccountDto.builder().sum(50).tariffName("plain").ownerId(1000).build();

        MvcResult result = mockMvc.perform(get(URL+"/1")
                .header("XXX-AccessToken", token))
                .andExpect(status().isOk())
                .andReturn();

        AccountDto actual = mapper.readValue(result.getResponse().getContentAsString(), AccountDto.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getAccount_DhouldReturn403Error_WhenPrincipalIsNotAccountOwner() throws Exception {
        String token = createAccessToken(3000);

        mockMvc.perform(get(URL+"/2")
                .header("XXX-AccessToken", token))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void deleteAccount_ShouldDeleteAccount() throws Exception {
        String token = createAccessToken(3000);

        AccountCreateRequestDto req = AccountCreateRequestDto.builder().tariffName("plain").build();
        MvcResult result = mockMvc.perform(post("/api/accounts")
                .header("XXX-AccessToken", token)
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsString(req))).andReturn();

        mockMvc.perform(delete(result.getResponse().getRedirectedUrl())
                .header("XXX-AccessToken", token))
                .andExpect(status().isNoContent())
                .andReturn();

        long accountId = extractId(result.getResponse().getRedirectedUrl(), 3);

        assertThat(accountRepository.existsById(accountId)).isTrue();
    }

    @Test
    public void deleteAccount_ShouldReturn403_WhenPrincipalIsNotAccountOwner() throws Exception {
        String token = createAccessToken(3000);
        mockMvc.perform(delete(URL+"/2")
                .header("XXX-AccessToken", token))
                .andExpect(status().isForbidden());


        assertThat(accountRepository.existsById(2L)).isTrue();
    }

    @Test
    public void getAccountUsers_ShouldReturnAccountUsers() throws Exception {
       String token =createAccessToken(1000);
       List<UserDto> expected = accountRepository
               .findById(1L).get().getUsers()
               .stream().map(UserDto::fromEntity)
               .collect(Collectors.toList());
       mockMvc.perform(get(URL+"/1/users")
               .header("XXX-AccessToken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expected.size())));
    }

    @Test
    public void getAccountUsers_ShouldReturn403Error_WhenPrincipalIsNotAccountOwner() throws Exception {
        String token = createAccessToken(3000);
        mockMvc.perform(get(URL+"/1/users")
                .header("XXX-AccessToken", token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteAccountUser_ShouldDeleteAccountUser() throws Exception {
        String token = createAccessToken(1000);
        mockMvc.perform(delete(URL+"/1/users/4000")
                .header("XXX-AccessToken", token))
                .andExpect(status().isNoContent());

        Account account = accountRepository.findById(1L).get();
        User deleted = userRepository.findById(4000).get();
        assertThat(account.getUsers().contains(deleted)).isFalse();
    }

    @Test
    public void deleteAccountUser_Shouldreturn403_WhenPrincipalIsNotAccountOwner() throws Exception {
        String token = createAccessToken(3000);
        mockMvc.perform(delete(URL+"/1/users/4000")
                .header("XXX-AccessToken", token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getAccountTransaction_ShouldReturnAllAccountTransactions() throws Exception {
        String token =createAccessToken(1000);
        List<Transaction> deposits = accountRepository.findById(1L).get().getDeposits();
        List<Transaction> trans = accountRepository.findById(1L).get().getWithdrawals();
        trans.addAll(deposits);
        List<TransactionDto> expected = trans.stream().map(TransactionDto::fromEntity).collect(Collectors.toList());
        mockMvc.perform(get(URL+"/1/transactions")
                .header("XXX-AccessToken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expected.size())))
                .andReturn();
    }

    @Test
    public void getAccountTransaction_Shouldreturn403Error_WhenPrincipalIsNotAccountOwner() throws Exception {
        String token = createAccessToken(3000);
        mockMvc.perform(get(URL+"/1/transactions")
                .header("XXX-AccessToken", token))
                .andExpect(status().isForbidden());
    }

    private String createAccessToken(long uid){
        return jwtTokenService.createAccessToken(uid, 0);
    }

    private long extractId(String path, int pos){
        return Long.valueOf(path.split("/")[pos]);
    }
}