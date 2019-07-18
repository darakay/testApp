package com.darakay.testapp.testapp.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Getter
public class Account {

    @Id
    @GeneratedValue
    private long id;

    private double sum;

    @ManyToMany(mappedBy = "accounts", fetch = FetchType.EAGER)
    private Set<User> users = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="tariff_name")
    private Tariff tariff;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "source", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<Transaction> withdrawals = new HashSet<>();

    @OneToMany(mappedBy = "target", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<Transaction> deposits = new HashSet<>();

    public Account(double initialSum, Tariff tariff, User owner) {
        this.sum = initialSum;
        this.tariff = tariff;
        this.owner = owner;
    }

    public Account(){}

    public Set<User> getUsers() {
        return users;
    }

    public Account changeSum(double sum) {
        this.sum += sum;
        return this;
    }

    public Account removeUser(User user){
        this.users.remove(user);
        return this;
    }

    public List<Transaction> getWithdrawals(){
        return new ArrayList<>(withdrawals);
    }

    public List<Transaction> getDeposits(){
        return new ArrayList<>(deposits);
    }
}
