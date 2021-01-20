package com.tiwa007.gamematchrestapi.controller;


import com.tiwa007.gamematchrestapi.common.exception.validator.InStringArray;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

public class UserRequest {

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 45, message = "Name should have more than 2 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Name can only consist of letter and number")
    private String name;

    // male, female
    @NotBlank(message = "Gender cannot be empty")
    @InStringArray(message = "Gender should be 'male' or 'female'", values = {"male","female"})
    private String gender;

    @NotBlank(message = "Nickname cannot be empty")
    @Size(min = 2, max = 45, message = "Name should have more than 2 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Nickname can only consist of letter and number")
    private String nickname;

    //    Europe, Asia, USA
    @NotBlank(message = "Geography cannot be empty")
    @InStringArray(message = "Geography should be one of 'Europe', 'Asia', 'USA'", values = {"Europe","Asia", "USA"})
    private String geography;

    //  One to Many   https://blog.csdn.net/liyiming2017/article/details/90218062
    @Valid
    private Set<InterestRequest> interestSet = new HashSet<>();

    public UserRequest() {
    }

    public UserRequest(String name, String gender, String nickname, String geography, Set<InterestRequest> interestSet) {
        this.name = name;
        this.gender = gender;
        this.nickname = nickname;
        this.geography = geography;
        this.interestSet = interestSet;
    }

    public UserRequest(String name, String gender, String nickname, String geography) {
        this.name = name;
        this.gender = gender;
        this.nickname = nickname;
        this.geography = geography;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGeography() {
        return geography;
    }

    public void setGeography(String geography) {
        this.geography = geography;
    }

    public Set<InterestRequest> getInterestSet() {
        return interestSet;
    }

    public void setInterestSet(Set<InterestRequest> interestSet) {
        this.interestSet = interestSet;
    }

}
