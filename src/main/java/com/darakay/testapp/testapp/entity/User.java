package com.darakay.testapp.testapp.entity;


import lombok.Getter;
import org.apache.commons.lang3.time.DateUtils;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_account",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id"))
    private Set<Account> accounts = new HashSet<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private Set<Account> ownAccounts;

    private String refreshToken;

    private Timestamp expiresAt;

    private String firstName;

    private String lastName;

    private String login;

    private String password;

    public User() {
    }

    public User(String firstName, String lastName, String login, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
    }


    public void addAccounts(Account account) {
        this.accounts.add(account);
    }

    public User deleteAccount(Account account) {
        accounts.remove(account);
        return this;
    }

    public User setSecurityTokens(String token, long expires) {
        this.refreshToken = token;
        this.expiresAt = new Timestamp(expires);
        return this;
    }

    public User setSecurityTokens(String token, String expires) {
        this.refreshToken = token;
        this.expiresAt = Timestamp.valueOf(expires);
        return this;
    }

    public User expire(){
        this.expiresAt = new Timestamp(DateUtils.addMinutes(new Date() ,- 1).getTime());
        return this;
    }

    public Long getExpiresAt(){
        if(expiresAt == null)
            return 0L;
        return expiresAt.getTime();
    }
}
