package com.example.nick.superheroes;

/**
 * Created by jkloppenburg1 on 10/11/2016.
 */

public class SuperHero {
    private String username;
    private String name;
    private String superpower;
    private String oneThing;

    SuperHero()
    {
        username = "";
        name = "";
        superpower = "";
        oneThing = "";
    }

    public SuperHero(String username, String name, String superpower, String oneThing) {
        this.username = username;
        this.name = name;
        this.superpower = superpower;
        this.oneThing = oneThing;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getSuperpower() {
        return superpower;
    }

    public String getOneThing() {
        return oneThing;
    }
}
