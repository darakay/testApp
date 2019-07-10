package com.darakay.testapp.testapp.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Account {

    @Getter
    @Id
    @GeneratedValue
    private long id;

    @Getter
    private double sum;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "accounts")
    private Set<User> users = new HashSet<>();

    @Getter
    @ManyToOne
    @JoinColumn(name="tariff_name")
    private Tariff tariff;

    @Getter
    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;

    private Account(long id, double sum, Set<User> users, Tariff tariff, User owner) {
        this.sum = sum;
        this.users = users;
        this.tariff = tariff;
        this.owner = owner;
    }

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
        return new Account(this.getId(), this.sum + sum, this.users, this.getTariff(), this.owner);
    }
}
