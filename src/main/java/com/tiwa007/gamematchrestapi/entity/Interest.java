package com.tiwa007.gamematchrestapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tiwa007.gamematchrestapi.common.exception.validator.InStringArray;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.util.Objects;

@Entity
@Table(name = "interests")
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="interest_id")
    private Long interestId;

    // fortnite, call of duty, dota, valhalla, among us
    @NotBlank(message = "Game cannot be empty")
    @InStringArray(message = "Game should be one of 'fortnite', 'call of duty', 'dota', 'valhalla', 'among us'",
            values = {"fortnite", "call of duty", "dota", "valhalla", "among us"})
    private String game;

    // noob, pro, invincible
    @NotBlank(message = "Level cannot be empty")
    @InStringArray(message = "Level should be one of 'noob', 'pro', 'invincible'", values = {"noob", "pro", "invincible"})
    private String level;

    @PositiveOrZero(message = "Credit cannot be negative")
    @Column(columnDefinition = "integer default 0")
    private Integer credit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public Interest() {
    }

    public Interest(String game, String level, Integer credit, User user) {
        this.game = game;
        this.level = level;
        this.credit = credit;
        this.user = user;
    }

    public Long getInterestId() {
        return interestId;
    }

    public void setInterestId(Long interestId) {
        this.interestId = interestId;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interest interest = (Interest) o;
        return Objects.equals(interestId, interest.interestId) &&
                Objects.equals(game, interest.game) &&
                Objects.equals(level, interest.level) &&
                Objects.equals(credit, interest.credit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interestId, game, level, credit);
    }
}
