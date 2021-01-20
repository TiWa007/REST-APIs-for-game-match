package com.tiwa007.gamematchrestapi.respository;

import com.tiwa007.gamematchrestapi.entity.Interest;
import com.tiwa007.gamematchrestapi.entity.User;
import com.tiwa007.gamematchrestapi.repository.InterestRepository;
import com.tiwa007.gamematchrestapi.repository.UserRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@Sql({"/h2_repository_test.sql"})
public class UserRepositoryTest {

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
    }

    @Test
    public void injectedComponentsAreNotNull(){
        AssertionsForClassTypes.assertThat(userRepository).isNotNull();
        AssertionsForClassTypes.assertThat(interestRepository).isNotNull();
    }


    @Test
    public void whenFindMatchUserByGameAndLevelAndGeography_thenReturnUserList() {
//        given resources/data.sql
//        when
        Interest interest = interestRepository.findById(10001L).get();
        List<User> found = userRepository.findMatchUserByGameAndLevelAndGeography(interest.getGame(),
                interest.getLevel(), interest.getUser().getGeography());
//        then user 10001L and 10003L
        assertThat(found).hasSize(2).contains(userRepository.findById(10001L).get(),
                userRepository.findById(10003L).get());
    }

    @Test
    public void whenFindUserWithMaxCreditByGameAndLevel_thenReturnUserList() {
//        given resources/data.sql
//        when
        List<User> found = userRepository.findUserWithMaxCreditByGameAndLevel("fortnite",
                "noob");
//        then 10001L
        assertThat(found).hasSize(1).contains(userRepository.findById(10001L).get());
    }




}
