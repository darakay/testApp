package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.TransactionDto;
import com.darakay.testapp.testapp.entity.*;
import com.darakay.testapp.testapp.exception.TransactionNotFountException;
import com.darakay.testapp.testapp.repos.AccountRepository;
import com.darakay.testapp.testapp.repos.TransactionRepository;
import com.darakay.testapp.testapp.repos.UserRepository;
import com.darakay.testapp.testapp.service.TransactionResult;
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

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String CONTROLLER_URI = "/users";

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    @Test
    public void userLogUp() throws Exception {
        User testUser = new User( "Иван", "Иванов", "111");

        MvcResult mvcResult = mockMvc.perform(post(CONTROLLER_URI)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(mapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated()).andReturn();

        long uid = extractUserId(mvcResult.getResponse().getRedirectedUrl());

        assertThat(userRepository.findById(uid).get()).isEqualToIgnoringGivenFields(testUser, "id");

    }


    @Test
    public void createUserAccount() throws Exception {
        User saved = userRepository.save(new User("User", "With account", "111"));
        String uri = String.format(CONTROLLER_URI + "/%s/accounts", saved.getId());

        MvcResult result  =
                mockMvc.perform(post(uri)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(mapper.writeValueAsString(TariffType.plain())))
                .andExpect(status().isCreated())
                .andReturn();

        long id = extractUserId(result.getResponse().getRedirectedUrl());

        assertThat(accountRepository.existsById(id)).isTrue();
        assertThat(userRepository.findById(saved.getId()).get().getAccounts().size()).isEqualTo(1);
    }

    @Test
    public void performTransaction_createCorrectTransactionEntity() throws Exception {
        TransactionDto transactionDto =
                TransactionDto.builder().sourceId(1).targetId(2).sum(30).build();

        String uri = CONTROLLER_URI+"/1000/transaction";

        Transaction actual = createTransaction(transactionDto, uri);

        assertThat(actual.getDate()).isBeforeOrEqualsTo(new Date());
        assertThat(actual.getAuthor().getId()).isEqualTo(1000);
        assertThat(actual.getSource().getId()).isEqualTo(1);
        assertThat(actual.getTarget().getId()).isEqualTo(2);
        assertThat(actual.getSum()).isEqualTo(30);
        assertThat(actual.getType()).isEqualTo(TransactionType.TRANSACTION);

        changeAccountSum(30, 1l);
        changeAccountSum(-30, 2l);
    }

    @Test
    public void performTransaction_changeAccountsSum() throws Exception {
        TransactionDto transactionDto =
                TransactionDto.builder().sourceId(1).targetId(2).sum(5).build();

        String uri = CONTROLLER_URI+"/1000/transaction";

        createTransaction(transactionDto, uri);

        assertThat(accountRepository.findById(1l).get().getSum()).isEqualTo(45);
        assertThat(accountRepository.findById(2l).get().getSum()).isEqualTo(5);

        changeAccountSum(5, 1l);
        changeAccountSum(-5, 2l);
    }

    private Transaction createTransaction(TransactionDto dto, String uri) throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                post(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk()).andReturn();

        TransactionResult transactionResult = mapper
                .readValue(mvcResult.getResponse().getContentAsString(), TransactionResult.class);
        return transactionRepository
                .findById(transactionResult.getTransactionId())
                .orElseThrow(TransactionNotFountException::new);
    }

    private void changeAccountSum(double diff, long accountId){
        Account source = accountRepository.findById(accountId).get();
        Account changed = source.changeSum(diff);
        accountRepository.save(changed);
    }


    private long extractUserId(String redirectUri){
        String[] parts = redirectUri.split("/");
        return Long.valueOf(parts[2]);
    }



}