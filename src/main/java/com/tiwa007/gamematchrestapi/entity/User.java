package com.tiwa007.gamematchrestapi.entity;

import com.tiwa007.gamematchrestapi.exception.validator.InStringArray;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="user_id")
    private Long userId;

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
    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private Set<Interest> interestSet = new HashSet<>();

    public User() {
    }

    public User(String name, String gender, String nickname, String geography, Set<Interest> interestSet) {
        this.name = name;
        this.gender = gender;
        this.nickname = nickname;
        this.geography = geography;
        this.interestSet = interestSet;
    }

    public User(String name, String gender, String nickname, String geography) {
        this.name = name;
        this.gender = gender;
        this.nickname = nickname;
        this.geography = geography;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Set<Interest> getInterestSet() {
        return interestSet;
    }

    public void setInterestSet(Set<Interest> interestSet) {
        this.interestSet = interestSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) &&
                Objects.equals(name, user.name) &&
                Objects.equals(gender, user.gender) &&
                Objects.equals(nickname, user.nickname) &&
                Objects.equals(geography, user.geography);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, gender, nickname, geography);
    }
}
