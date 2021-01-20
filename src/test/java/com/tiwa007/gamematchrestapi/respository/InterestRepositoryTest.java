package com.tiwa007.gamematchrestapi.respository;

import com.tiwa007.gamematchrestapi.entity.Interest;
import com.tiwa007.gamematchrestapi.entity.User;
import com.tiwa007.gamematchrestapi.repository.InterestRepository;
import com.tiwa007.gamematchrestapi.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@Sql({"/h2_repository_test.sql"})
public class InterestRepositoryTest {

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
    }

    @Test
    public void injectedComponentsAreNotNull(){
        assertThat(userRepository).isNotNull();
        assertThat(interestRepository).isNotNull();
    }

    @Test
    public void givenUserAndGame_whenFindInterestByUserAndGame_thenReturnInterestList() {
//        given resources/data.sql
//        when
        User user = userRepository.findById(10001L).get();
        List<Interest> interestList = interestRepository.findInterestByUserAndGame(user, "fortnite");
//        then
        assertThat(interestList).hasSize(1);
        assertThat(interestList.get(0).getInterestId()).isEqualTo(10001L);
    }

    @Test
    public void givenInterestIdAndCredit_whenUpdateUserInterestCredit_thenSucess() {
//        given resources/data.sql
//        when
        interestRepository.updateUserInterestCredit(10001L, 8);
        Interest interest = interestRepository.findById(10001L).get();
//        then
        assertThat(interest).isNotNull();
        assertThat(interest.getCredit()).isEqualTo(8);
    }

    @Test
    public void givenUserId_whenDeleteInterestsByUserId_thenSuccess() {
//        given resources/data.sql
//        when
        interestRepository.deleteInterestsByUserId(10001L);
        User user = userRepository.findById(10001L).get();
//        then
        assertThat(user.getInterestSet()).isNotNull();
    }
}
