package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.TransactionRequest;
import com.darakay.testapp.testapp.dto.TransactionResult;
import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.repos.AccountRepository;
import com.darakay.testapp.testapp.repos.TransactionRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TransactionControllerTest {

    private static String URL = "/api/transactions";


    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void performTransaction_ShouldAddTransactionAtDatabase() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
                .sum(20)
                .sourceAccountId(1)
                .targetAccountId(2)
                .build();

        MvcResult result = mockMvc
                .perform(
                        post(URL)
                        .header("XXX-AccessToken", createAccessToken(1000))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        TransactionResult transactionResult = mapper.readValue(result.getResponse().getContentAsString(),
                TransactionResult.class);

        long transactionId = transactionResult.getTransactionId();

        assertThat(transactionResult.isSuccess()).isTrue();
        assertThat(transactionRepository.existsById(transactionId)).isTrue();

        reset(1, 2, 20);
    }

    @Test
    public void performTransaction_ShouldNotPerform_WhenUserDoesNotHaveAccessToAccount() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
                .sum(20)
                .sourceAccountId(1)
                .targetAccountId(3)
                .build();

        MvcResult result = mockMvc
                .perform(
                        post(URL)
                                .header("XXX-AccessToken",  createAccessToken(1000))
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        TransactionResult transactionResult = mapper.readValue(result.getResponse().getContentAsString(),
                TransactionResult.class);


        assertThat(transactionResult.isSuccess()).isFalse();
        assertThat(accountRepository.findById(1L).get().getSum()).isEqualTo(50);
        assertThat(accountRepository.findById(3L).get().getSum()).isEqualTo(0);


    }

    private void reset(long sourceId, long targetId, double diff){
        Account src = accountRepository.findById(sourceId).get().changeSum(diff);
        Account tgt = accountRepository.findById(targetId).get().changeSum(-diff);
        accountRepository.save(src);
        accountRepository.save(tgt);
    }

    private String createAccessToken(long uid){
        return jwtTokenService.createAccessToken(uid, 0);
    }
}