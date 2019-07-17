package com.darakay.testapp.testapp.entity;


import lombok.Getter;

import javax.persistence.*;
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
}
