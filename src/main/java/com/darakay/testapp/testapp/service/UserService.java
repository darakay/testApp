package com.darakay.testapp.testapp.service;

import com.darakay.testapp.testapp.exception.BadRequestException;
import com.darakay.testapp.testapp.security.UserData;
import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.exception.UserNotFoundException;
import com.darakay.testapp.testapp.repos.UserRepository;
import com.darakay.testapp.testapp.security.jwt.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public UserService(UserRepository userRepository, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    public User logUp(User user){
        return userRepository.save(user);
    }

    public String login(String authenticationHeader) throws BadRequestException {
        String[] credentials = getCredentialsValue(authenticationHeader);
        User user = loadByLogin(credentials[0]).orElseThrow(() -> new UsernameNotFoundException("Invalid login!"));
        if(user.getPassword().equals(credentials[1])){
            return jwtTokenService.create(user.getId());
        }
        throw new BadCredentialsException("Invalid password");
    }

    public Optional<User> loadByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public User getUserById(long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public User getCurrentPrincipal(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserData principal = (UserData) auth.getPrincipal();
        return userRepository.findById(principal.getId()).get();
    }

    public User getUserByIdOrNull(long id){
        return userRepository.findById(id).orElse(null);
    }

    private String[] getCredentialsValue(String headerValue) throws BadRequestException {
        if(headerValue == null ||headerValue.split(" ").length != 2)
            throw new BadRequestException();
        String encodedValue = headerValue.split(" ")[1];
        String decodedValue = new String(Base64.getDecoder().decode(encodedValue));
        if(decodedValue.split(":").length != 2)
            throw new BadRequestException();
        return decodedValue.split(":");
    }
}
