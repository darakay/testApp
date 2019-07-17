package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.UserCreateRequest;
import com.darakay.testapp.testapp.repos.UserRepository;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void login_ShouldCreateCorrectJwtToken() throws Exception {
        MvcResult result = mockMvc
                .perform(get("/auth/login") .with(httpBasic("owner", "qwerty")))
                .andReturn();

        assertThat(result.getResponse().getHeader("XXX-JwtToken")).isNotNull();
    }

    @Test
    public void login_ShouldReturn401Response_WhenPasswordIsInvalid() throws Exception {
        mockMvc
                .perform(get("/auth/login") .with(httpBasic("owner", "111")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void login_ShouldReturn401Response_WhenLoginIsInvalid() throws Exception {
        mockMvc
                .perform(get("/auth/login").with(httpBasic("user", "qwerty")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void login_ShouldReturnBadRequest_WhenAuthorizationHeaderIsInvalid() throws Exception {
        mockMvc
                .perform(get("/auth/login").header("Authorization", "duty7dg6vvg0="))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void login_ShouldReturnBadRequest_WhenNoAuthorizationHeader() throws Exception {
        mockMvc
                .perform(get("/auth/login"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void logup_ShouldRedirectToCorrectUrlAndAddNewUserToDatabase() throws Exception {
        UserCreateRequest req = UserCreateRequest.builder()
                .firstName("Vanya")
                .lastName("XXX")
                .login("ivan")
                .password("123")
                .build();

        MvcResult result = mockMvc
                .perform(
                        post("/auth/logup")
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("/users/*"))
                .andExpect(header().exists("XXX-JwtToken"))
                .andReturn();

        long uid = Long.valueOf(result.getResponse().getRedirectedUrl().split("/")[2]);

        assertThat(userRepository.existsById(uid)).isTrue();
        assertThat(userRepository.findById(uid).get().getLogin()).isEqualTo("ivan");
        assertThat(userRepository.findById(uid).get().getPassword()).isEqualTo("123");
    }
}