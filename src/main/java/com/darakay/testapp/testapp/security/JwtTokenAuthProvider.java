package com.darakay.testapp.testapp.security;

import com.darakay.testapp.testapp.entity.User;
import com.darakay.testapp.testapp.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenAuthProvider implements AuthenticationProvider {

    private final JwtParser jwtParser;

    public JwtTokenAuthProvider() {
        jwtParser = Jwts.parser().setSigningKey("secretKey");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = authentication.getPrincipal().toString();
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            long uid = claims.get("id", Long.class);
                return new AuthenticatedUserToken(new UserData(uid), authentication.getDetails());
        } catch (MalformedJwtException e){
            throw new BadCredentialsException("Invalid token");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
