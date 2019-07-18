package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.UserInfo;
import com.darakay.testapp.testapp.dto.UserTransactionDto;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.repos.UserRepository;
import com.darakay.testapp.testapp.security.jwt.JwtTokenService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String CONTROLLER_URI = "/api/users";
    @Autowired
    private  JwtTokenService jwtTokenService;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private UserRepository userRepository;

    @Test
    public void getUser_ShouldReturnAllInfoAboutPrincipal() throws Exception {

        UserInfo expected = UserInfo.fromEntity(userRepository.findById(2000).orElseThrow(UserNotFoundException::new));

        MvcResult result = mockMvc
                .perform(
                    get(CONTROLLER_URI+"/2000")
                    .header("XXX-AccessToken", createAccessToken(2000)))
                .andExpect(status().isOk())
                .andReturn();
        UserInfo actual = mapper.readValue(result.getResponse().getContentAsString(), UserInfo.class);

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    public void getTransactions() throws Exception {
        String uri = CONTROLLER_URI + "/2000/transaction";

        MvcResult result = mockMvc
                .perform(get(uri).header("XXX-AccessToken", createAccessToken(2000)))
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
                .perform(get(uri).header("XXX-AccessToken", createAccessToken(2000)))
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
                .perform(get(uri).header("XXX-AccessToken", createAccessToken(2000)))
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
    public void getTransactionsSortedByTransactionSumWithPagination() throws Exception {
        String uri = CONTROLLER_URI + "/2000/transaction?sortedBy=sum&limit=1&offset=2";

        MvcResult result = mockMvc
                .perform(get(uri).header("XXX-AccessToken", createAccessToken(2000)))
                .andReturn();

        List<UserTransactionDto> userTransactionDtos =
                mapper.readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<UserTransactionDto>>() {});

        assertThat(userTransactionDtos).asList().containsSequence(
                UserTransactionDto.builder().accountId(2).otherId(1)
                        .sum(2000).date("2019-07-10 08:20:32").type("transaction").build());

    }

    private String createAccessToken(long uid){
        return jwtTokenService.createAccessToken(uid, 0);
    }
}