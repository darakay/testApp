package com.darakay.testapp.testapp.entity;


import lombok.Getter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Entity
@Getter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account source;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private Account target;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private double sum;

    private String type;

    private Time time;

    private Date date;

    @PrePersist
    protected void onCreate(){
        long currentTime = new java.util.Date().getTime();
        this.time = new Time(currentTime);
        this.date = new Date(currentTime);
    }

    public Transaction(Account source, Account target, User user, double sum, TransactionType type) {
        this.source = source;
        this.target = target;
        this.user = user;
        this.sum = sum;
        this.type = type.name();
    }

    public Transaction() {
    }
}
