package com.darakay.testapp.testapp.entity;


import lombok.Getter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Entity
public class Transaction {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account source;

    @Getter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipient_id")
    private Account target;

    @Getter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    @Getter
    private double sum;

    @Getter
    private TransactionType type;

    @Getter
    private Time time;

    @Getter
    private Date date;

    @PrePersist
    protected void onCreate(){
        long currentTime = new java.util.Date().getTime();
        this.time = new Time(currentTime);
        this.date = new Date(currentTime);
    }

    public Transaction(Account source, Account target, User author, double sum, TransactionType type) {
        this.source = source;
        this.target = target;
        this.author = author;
        this.sum = sum;
        this.type = type;
    }
}
