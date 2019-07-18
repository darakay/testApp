package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.dto.UserCreateRequest;
import com.darakay.testapp.testapp.entity.User;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationControllerTest {

    @Autowired
    private JwtTokenService jwtTokenService;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void login_ShouldCreateCorrectAccessToken() throws Exception {
        MvcResult result = mockMvc
                .perform(get("/auth/login") .with(httpBasic("owner", "qwerty")))
                .andReturn();

        assertThat(result.getResponse().getHeader("XXX-AccessToken"))
                .isNotNull();
        assertThat(result.getResponse().getHeader("XXX-RefreshToken")).isNotNull();
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
    public void login_ShouldReturnBadRequest_WhenThereIsNoAuthorizationHeader() throws Exception {
        mockMvc
                .perform(get("/auth/login"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void logup_ShouldReturnCoorectRedirectUriAndAddNewUserToDatabase() throws Exception {
        UserCreateRequest req = UserCreateRequest.builder()
                .firstName("Vanya")
                .lastName("XXX")
                .login("ivan")
                .password("123")
                .build();

        mockMvc
                .perform(
                        post("/auth/logup")
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/auth/login"))
                .andReturn();

        User created = userRepository.findByLogin("ivan").orElse(null);

        assertThat(created).isNotNull();
        assertThat(created.getLogin()).isEqualTo("ivan");
        assertThat(created.getPassword()).isEqualTo("123");

        userRepository.delete(created);
    }

    @Test
    public void logup_ShouldReturnBadRequest_WhenLoginAlreadyExist() throws Exception {
        UserCreateRequest req = UserCreateRequest.builder()
                .firstName("Vanya")
                .lastName("XXX")
                .login("owner")
                .password("123")
                .build();

        mockMvc
                .perform(
                        post("/auth/logup")
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void refreshAccessToken_ShouldRefreshToken_IfOldTokenIsValid() throws Exception {
        MvcResult result = mockMvc
                .perform(
                        get("/auth/refresh")
                                .header("XXX-AccessToken", createAccessToken(1000))
                               .header("XXX-RefreshToken", "123")
                                )
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getHeader("XXX-AccessToken")).isNotNull();
        assertThat(result.getResponse().getHeader("XXX-RefreshToken")).isNotNull();
        assertThat(userRepository.findById(1000).get().getRefreshToken()).isNotEqualTo("123");

        User user = userRepository.findById(1000).get().setSecurityTokens("123", "2020-07-10 00:00:00");
        userRepository.save(user);
    }

    @Test
    public void refreshAccessToken_ShouldReturn403_IfOldTokenIsInvalid() throws Exception {
        mockMvc
                .perform(
                        get("/auth/refresh")
                                .header("XXX-AccessToken", createAccessToken(1000))
                                .header("XXX-RefreshToken", "321")
                )
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void logout() throws Exception {
        mockMvc
                .perform(
                        get("/auth/logout")
                                .header("XXX-AccessToken",createAccessToken(1000))
                )
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(
                get("/api/accounts/1")
                .header("XXX-AccessToken", createAccessToken(1000)))
                .andExpect(status().isForbidden());

    }

    private String createAccessToken(long uid){
        return jwtTokenService.createAccessToken(uid, 0);
    }
}