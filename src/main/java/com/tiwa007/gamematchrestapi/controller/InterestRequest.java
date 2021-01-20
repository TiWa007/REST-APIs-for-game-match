package com.tiwa007.gamematchrestapi.controller;

import com.tiwa007.gamematchrestapi.common.exception.validator.InStringArray;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

public class InterestRequest {

    @NotBlank(message = "Game cannot be empty")
    @InStringArray(message = "Game should be one of 'fortnite', 'call of duty', 'dota', 'valhalla', 'among us'",
            values = {"fortnite", "call of duty", "dota", "valhalla", "among us"})
    private String game;

    @NotBlank(message = "Level cannot be empty")
    @InStringArray(message = "Level should be one of 'noob', 'pro', 'invincible'", values = {"noob", "pro", "invincible"})
    private String level;

    @PositiveOrZero(message = "Credit cannot be negative")
    @Column(columnDefinition = "integer default 0")
    private Integer credit;

    public InterestRequest(String game, String level, Integer credit) {
        this.game = game;
        this.level = level;
        this.credit = credit;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }
}
