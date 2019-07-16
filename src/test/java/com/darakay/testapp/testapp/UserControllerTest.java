package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.TransactionDto;
import com.darakay.testapp.testapp.dto.TransactionResult;
import com.darakay.testapp.testapp.dto.UserTransaction;
import com.darakay.testapp.testapp.entity.*;
import com.darakay.testapp.testapp.exception.TransactionNotFountException;
import com.darakay.testapp.testapp.repos.AccountRepository;
import com.darakay.testapp.testapp.repos.TransactionRepository;
import com.darakay.testapp.testapp.repos.UserRepository;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        User testUser = new User( "Иван", "Иванов", "ivan", "111");

        MvcResult mvcResult = mockMvc.perform(post(CONTROLLER_URI)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(mapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated()).andReturn();

        long uid = extractUserId(mvcResult.getResponse().getRedirectedUrl());

        assertThat(userRepository.findById(uid).get()).isEqualToIgnoringGivenFields(testUser, "id");

    }

    @Test
    public void performTransaction_createCorrectTransactionEntity() throws Exception {
        TransactionDto transactionDto =
                new TransactionDto(1000L, 1, 2, 30, 0);

        String uri = CONTROLLER_URI+"/1000/transaction";

        Transaction actual = createTransaction(transactionDto, uri);

        reset(30, 1, 2, actual);

        assertThat(actual.getDate()).isBeforeOrEqualsTo(new Date());
        assertThat(actual.getUser().getId()).isEqualTo(1000);
        assertThat(actual.getSource().getId()).isEqualTo(1);
        assertThat(actual.getTarget().getId()).isEqualTo(2);
        assertThat(actual.getSum()).isEqualTo(30);
        assertThat(actual.getType()).isEqualTo(TransactionType.TRANSACTION.name());
    }

    @Test
    public void performTransaction_changeAccountsSum() throws Exception {
        TransactionDto transactionDto =
                new TransactionDto(1000L, 1, 2, 5, 0);

        String uri = CONTROLLER_URI+"/1000/transaction";

        Transaction actual = createTransaction(transactionDto, uri);

        assertThat(accountRepository.findById(1l).get().getSum()).isEqualTo(45);
        assertThat(accountRepository.findById(2l).get().getSum()).isEqualTo(55);

        reset(5, 1, 2, actual);
    }

    @Test
    public void performTransaction_doNotCauseADeadlock() throws Exception {
        TransactionDto trans1 =
                new TransactionDto(1000L, 1, 2, 15, 56);

        TransactionDto trans2 =
                new TransactionDto(2000L, 2, 1, 10, 56);

        ExecutorService service = Executors.newFixedThreadPool(2);
        Transaction first = service
                .submit(() -> createTransaction(trans1, CONTROLLER_URI+"/1000/transaction"))
                .get();
        Transaction second = service
                .submit(() -> createTransaction(trans2, CONTROLLER_URI+"/2000/transaction"))
                .get();

        Thread.sleep(1000);

        assertThat(accountRepository.findById(1l).get().getSum()).isEqualTo(45);
        assertThat(accountRepository.findById(2l).get().getSum()).isEqualTo(55);

        reset(5, 1, 2, first, second);
    }

    @Test
    public void getTransactions() throws Exception {
        String uri = CONTROLLER_URI + "/2000/transaction";

        MvcResult result = mockMvc.perform(get(uri)).andReturn();

        List<UserTransaction> userTransactions =
                mapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<UserTransaction>>(){});

        assertThat(userTransactions).asList().contains(
                UserTransaction.builder().accountId(2).otherId(1)
                        .sum(2000).date("2019-07-10 08:20:32").type("transaction").build(),
                UserTransaction.builder().accountId(2).otherId(1)
                        .sum(500).date("2019-07-11 09:20:32").type("transaction").build());

    }

    @Test
    public void getTransactionsSortedByDate() throws Exception {
        String uri = CONTROLLER_URI + "/2000/transaction?sortedBy=date";

        MvcResult result = mockMvc.perform(get(uri)).andReturn();

        List<UserTransaction> userTransactions =
                mapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<UserTransaction>>(){});

        assertThat(userTransactions).asList().containsSequence(
                UserTransaction.builder().accountId(2).otherId(1)
                        .sum(2000).date("2019-07-10 08:20:32").type("transaction").build(),
                UserTransaction.builder().accountId(2).otherId(1)
                        .sum(500).date("2019-07-11 09:20:32").type("transaction").build());

    }

    @Test
    public void getTransactionsSortedByTransactionSum() throws Exception {
        String uri = CONTROLLER_URI + "/2000/transaction?sortedBy=sum";

        MvcResult result = mockMvc.perform(get(uri)).andReturn();

        List<UserTransaction> userTransactions =
                mapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<UserTransaction>>(){});

        assertThat(userTransactions).asList().containsSequence(
                UserTransaction.builder().accountId(2).otherId(1)
                        .sum(500).date("2019-07-11 09:20:32").type("transaction").build(),
                UserTransaction.builder().accountId(2).otherId(1)
                        .sum(2000).date("2019-07-10 08:20:32").type("transaction").build());

    }

    @Test
    public void getTransactions_() throws Exception {
        String uri = CONTROLLER_URI + "/2000/transaction?sortedBy=sum&limit=1&offset=2";

        MvcResult result = mockMvc.perform(get(uri)).andReturn();

        List<UserTransaction> userTransactions =
                mapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<UserTransaction>>(){});

        assertThat(userTransactions).asList().containsSequence(
                UserTransaction.builder().accountId(2).otherId(1)
                        .sum(2000).date("2019-07-10 08:20:32").type("transaction").build());

    }

    @Test
    public void getTransactions_failed_whenUserIsNotAccountOwnerOrUser() throws Exception {
        String uri = CONTROLLER_URI + "/1000/transaction";

        TransactionDto transactionDto =  new TransactionDto(2000L, 2, 1, 50, 56);

        MvcResult result = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(transactionDto))).andReturn();

        TransactionResult transactionResult =
                mapper.readValue(result.getResponse().getContentAsString(), TransactionResult.class);

        assertThat(transactionResult.isSuccess()).isFalse();
        assertThat(accountRepository.findById(2L).get().getSum()).isEqualTo(50);
        assertThat(accountRepository.findById(1L).get().getSum()).isEqualTo(50);
    }

    @Test
    public void getTransactions_isOk_whenTransactionAuthorIsAccountUser() throws Exception {
        String uri = CONTROLLER_URI + "/3000/transaction";

        TransactionDto transactionDto =  new TransactionDto(1000L, 1, 2, 25, 56);

        MvcResult result = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(transactionDto))).andReturn();

        TransactionResult transactionResult =
                mapper.readValue(result.getResponse().getContentAsString(), TransactionResult.class);

        assertThat(transactionResult.isSuccess()).isTrue();
        assertThat(accountRepository.findById(1L).get().getSum()).isEqualTo(25);
        assertThat(accountRepository.findById(2L).get().getSum()).isEqualTo(75);

        reset(25, 1, 2, transactionRepository.findById(transactionResult.getTransactionId()).get());
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

    private void reset(double diff, long source, long target, Transaction... transactions){
        Account first = accountRepository.findById(source).get();
        accountRepository.save(first.changeSum(diff));
        Account second = accountRepository.findById(target).get();
        accountRepository.save(second.changeSum(-diff));
        transactionRepository.deleteAll(Arrays.asList(transactions));
    }

    private long extractUserId(String redirectUri){
        String[] parts = redirectUri.split("/");
        return Long.valueOf(parts[2]);
    }
}