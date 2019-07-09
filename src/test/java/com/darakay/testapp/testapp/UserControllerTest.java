package com.darakay.testapp.testapp;

import com.darakay.testapp.testapp.account.Account;
import com.darakay.testapp.testapp.account.AccountRepository;
import com.darakay.testapp.testapp.tariff.TariffType;


import com.darakay.testapp.testapp.user.User;
import com.darakay.testapp.testapp.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
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

import java.net.URI;
import java.net.URL;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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


    @Test
    public void userLogUp() throws Exception {

        userRepository.deleteAll();


        User testUser = new User( "Иван", "Иванов", "111");

        MvcResult mvcResult = mockMvc.perform(post(CONTROLLER_URI)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(mapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated()).andReturn();

        long uid = extractUserId(mvcResult.getResponse().getRedirectedUrl());

        assertThat(userRepository.findById(uid).get()).isEqualToIgnoringGivenFields(testUser, "id");

    }



    private long extractUserId(String redirectUri){
        String[] parts = redirectUri.split("/");
        return Long.valueOf(parts[2]);
    }



}