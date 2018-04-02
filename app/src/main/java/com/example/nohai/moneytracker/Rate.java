package com.example.nohai.moneytracker;

/**
 * Created by nohai on 4/1/2018.
 */

public class Rate {
    public String name;
    public String value;
    public String multiplier;

    public Rate(String value, String name, String multiplier)
    {this.value = value;
        this.name = name;
        this.multiplier = multiplier;
    }

}
