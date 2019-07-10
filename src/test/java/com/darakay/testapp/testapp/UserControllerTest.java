package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.entity.Account;
import com.darakay.testapp.testapp.entity.TariffType;
import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.repos.AccountRepository;
import com.darakay.testapp.testapp.repos.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
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

    @Before
    public void before(){
        userRepository.deleteAll();
        //accountRepository.deleteAll();
    }


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

        assertThat(accountRepository.existsById(id));
        assertThat(userRepository.findById(saved.getId()).get().getAccounts().size()).isEqualTo(1);
    }







    private long extractUserId(String redirectUri){
        String[] parts = redirectUri.split("/");
        return Long.valueOf(parts[2]);
    }



}