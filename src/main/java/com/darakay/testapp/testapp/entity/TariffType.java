package com.darakay.testapp.testapp.entity;

import lombok.Getter;

public class TariffType {
    @Getter
    private String type;

    private TariffType(String type){
        this.type = type;
    }

    public TariffType(){}

    public static TariffType plain(){
        return new TariffType("PLAIN");
    }
}
