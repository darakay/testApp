package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.UserCreateRequest;
import com.darakay.testapp.testapp.dto.UserTransactionDto;
import com.darakay.testapp.testapp.repos.UserRepository;
import com.darakay.testapp.testapp.security.jwt.JwtTokenService;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String CONTROLLER_URI = "/api/users";

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void getTransactions() throws Exception {
        String uri = CONTROLLER_URI + "/2000/transaction";

        MvcResult result = mockMvc
                .perform(get(uri).header("XXX-JwtToken", jwtTokenService.create(2000)))
                .andReturn();

        List<UserTransactionDto> userTransactionDtos =
                mapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<UserTransactionDto>>(){});

        assertThat(userTransactionDtos).asList().contains(
                UserTransactionDto.builder().accountId(2).otherId(1)
                        .sum(2000).date("2019-07-10 08:20:32").type("transaction").build(),
                UserTransactionDto.builder().accountId(2).otherId(1)
                        .sum(500).date("2019-07-11 09:20:32").type("transaction").build());

    }

    @Test
    public void getTransactionsSortedByDate() throws Exception {
        String uri = CONTROLLER_URI + "/2000/transaction?sortedBy=date";

        MvcResult result = mockMvc
                .perform(get(uri).header("XXX-JwtToken", jwtTokenService.create(2000)))
                .andReturn();

        List<UserTransactionDto> userTransactionDtos =
                mapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<UserTransactionDto>>(){});

        assertThat(userTransactionDtos).asList().containsSequence(
                UserTransactionDto.builder().accountId(2).otherId(1)
                        .sum(2000).date("2019-07-10 08:20:32").type("transaction").build(),
                UserTransactionDto.builder().accountId(2).otherId(1)
                        .sum(500).date("2019-07-11 09:20:32").type("transaction").build());

    }

    @Test
    public void getTransactionsSortedByTransactionSum() throws Exception {
        String uri = CONTROLLER_URI + "/2000/transaction?sortedBy=sum";

        MvcResult result = mockMvc
                .perform(get(uri).header("XXX-JwtToken", jwtTokenService.create(2000)))
                .andReturn();

        List<UserTransactionDto> userTransactionDtos =
                mapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<UserTransactionDto>>(){});

        assertThat(userTransactionDtos).asList().containsSequence(
                UserTransactionDto.builder().accountId(2).otherId(1)
                        .sum(500).date("2019-07-11 09:20:32").type("transaction").build(),
                UserTransactionDto.builder().accountId(2).otherId(1)
                        .sum(2000).date("2019-07-10 08:20:32").type("transaction").build());

    }

    @Test
    public void getTransactions_() throws Exception {
        String uri = CONTROLLER_URI + "/2000/transaction?sortedBy=sum&limit=1&offset=2";

        MvcResult result = mockMvc
                .perform(get(uri).header("XXX-JwtToken", jwtTokenService.create(2000)))
                .andReturn();

        List<UserTransactionDto> userTransactionDtos =
                mapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<UserTransactionDto>>() {
                        });

        assertThat(userTransactionDtos).asList().containsSequence(
                UserTransactionDto.builder().accountId(2).otherId(1)
                        .sum(2000).date("2019-07-10 08:20:32").type("transaction").build());

    }
}