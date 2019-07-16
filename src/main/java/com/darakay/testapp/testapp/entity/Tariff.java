package com.darakay.testapp.testapp.entity;


import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@EqualsAndHashCode
@Entity
public class Tariff {

    @Id
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

    public Tariff(String name, String type, double rate, double ownerLimit, double userLimit) {
        this.name = name;
        this.type = type;
        this.rate = rate;
        this.ownerLimit = ownerLimit;
        this.userLimit = userLimit;
    }

    public Tariff() {
    }

}
