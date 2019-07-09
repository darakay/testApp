package com.darakay.testapp.testapp.account;

import com.darakay.testapp.testapp.tariff.Tariff;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;


@EqualsAndHashCode
@Entity
@Table(name = "account", schema = "test")
public class Account {

    @Getter
    @Id
    @GeneratedValue
    private long id;

    @Getter
    private double sum;

    @Getter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="tariff_id")
    private Tariff tariff;

    public Account(double initialSum, Tariff tariff) {
        this.sum = initialSum;
        this.tariff = tariff;
    }

    public Account(){}
}
