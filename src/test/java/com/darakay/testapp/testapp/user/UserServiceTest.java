package com.darakay.testapp.testapp.user;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.shaded.org.bouncycastle.util.test.TestFailedException;
import org.testcontainers.shaded.org.bouncycastle.util.test.TestResult;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;


@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @After
    public void after(){
        userRepository.deleteAll();
    }


    @Test
    public void logUp_shouldRegisterNewUser(){
        User newUser = new User("firstName", "lastName", "password");

        long userID = userService.logUp(newUser);

        assertThat(userRepository.existsById(userID)).isTrue();
        assertThat(userRepository.findById(userID).get()).isEqualTo(newUser);
    }

    @Test
    public void logUp_shouldRegisterTwoUsers_WhenUsersHaveSameData(){
        User firstUser =  new User("firstName", "lastName", "password");
        User secondUser =  new User("firstName", "lastName", "password");
        long firstId = userService.logUp(firstUser);
        long secondId = userService.logUp(secondUser);

        assertThat(firstId).isNotEqualTo(secondId);
        assertThat(userRepository.findAllByFirstName("firstName")).hasSize(2);
    }

    


}