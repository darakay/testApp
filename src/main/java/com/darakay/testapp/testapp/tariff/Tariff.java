package com.darakay.testapp.testapp.tariff;


import com.darakay.testapp.testapp.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode
@ToString
@Entity
public class Tariff {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "tariff")
    private Set<Account> accounts = new HashSet<>();

    @Getter
    private String name;

    @Getter
    private String type;

    @Getter
    private double rate;

    @Getter
    private double ownerLimit;

    @Getter
    private double userLimit;

    public Tariff(long id, String name, String type, double rate, double ownerLimit, double userLimit) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.rate = rate;
        this.ownerLimit = ownerLimit;
        this.userLimit = userLimit;
    }

    public Tariff() {
    }
}
