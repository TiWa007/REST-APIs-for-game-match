package com.tiwa007.gamematchrestapi.service;

import com.tiwa007.gamematchrestapi.common.exception.InvalidRequestException;
import com.tiwa007.gamematchrestapi.common.exception.ResourceNotFoundException;
import com.tiwa007.gamematchrestapi.entity.Interest;
import com.tiwa007.gamematchrestapi.entity.User;
import com.tiwa007.gamematchrestapi.repository.InterestRepository;
import com.tiwa007.gamematchrestapi.repository.UserRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class UserServiceTest {

    @TestConfiguration
    static class UserServiceTestContextConfiguration {
        @Bean
        public UserService userService() {
            return new UserService();
        }
    }

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private InterestRepository interestRepository;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void contextLoads() throws Exception {
        assertThat(userService).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(interestRepository).isNotNull();
    }

    //    getAllUsers

    @Test
    public void givenUsers_whenGetAllUsers_thenReturnUserList() throws Exception {
//        given
        List<User> userList = createUserList();
        given(userRepository.findAll()).willReturn(userList);

//      when
        List<User> obtainedUserList = userService.getAllUsers();
//      then

        for (User user : userList) {
            assertThat(obtainedUserList).contains(user);
        }

        verify(userRepository, VerificationModeFactory.times(1)).findAll();
        reset(userRepository);
    }

    @Test
    public void givenUserId_whenGetUserById_thenReturnUser() throws Exception {

//        given
        List<User> userList = createUserList();
        given(userRepository.findById(userList.get(0).getUserId())).willReturn(Optional.of(userList.get(0)));
//      when
        User user = this.userService.getUserById(userList.get(0).getUserId());
//      then
        assertThat(user).isEqualTo(userList.get(0));
        verify(userRepository, VerificationModeFactory.times(1)).findById(userList.get(0).getUserId());
        reset(userRepository);
    }

    @Test
    public void givenInvalidUserId_whenGetUserById_thenException() throws Exception {
        // expectations
        Long userId = 400L;
        exceptionRule.expect(ResourceNotFoundException.class);
        exceptionRule.expectMessage("User cannot be found with id: " + userId);

        // init tested
        this.userService.getUserById(400L);
    }

//    createUser()

    @Test
    public void givenUserWithSameInterest_whenCreateUser_thenException() throws Exception {
        //        given
        User user = produceUser(null, "name1", "male", "nkname1", "USA", 0,
                null, "dota", "noob");
        Interest dupInterest = new Interest("dota", "noob", 10, null);
        user.getInterestSet().add(dupInterest);

        // expectations
        Long userId = 400L;
        exceptionRule.expect(InvalidRequestException.class);
        exceptionRule.expectMessage("User has different interests with same game");

        // when & then
        this.userService.createUser(user);

    }

    @Test
    public void givenUserWithoutInterest_whenCreateUser_thenReturnUser() throws Exception {
        //        given
        User user = new User("name1", "male", "nkname1", "USA");
        User user1 = new User("name1", "male", "nkname1", "USA");
        user1.setUserId(1L);

        given(userRepository.save(user)).willReturn(user1);
        given(userRepository.findById(1L)).willReturn(Optional.of(user1));

        //     When
        User createdUser = this.userService.createUser(user);

//        Then
        assertThat(createdUser).isEqualTo(user1);
        verify(userRepository, VerificationModeFactory.times(1)).save(user);
        verify(userRepository, VerificationModeFactory.times(1)).findById(1L);
        reset(userRepository);
    }

    @Test
    public void givenUserWithInterest_whenCreateUser_thenReturnUser() throws Exception {
        //        given
        User userRB = produceUser(null,"name1", "male", "nkname1", "USA", 0,
                null, "dota", "noob");
        User userS1 = produceUser(1L,"name1", "male", "nkname1", "USA", 0,
                null, "dota", "noob");
        User userFinal = produceUser(1L,"name1", "male", "nkname1", "USA", 0,
                1L, "dota", "noob");

        given(userRepository.save(userRB)).willReturn(userS1);
        given(interestRepository.save(userS1.getInterestSet().iterator().next()))
                .willReturn(userFinal.getInterestSet().iterator().next());
        given(userRepository.findById(1L)).willReturn(Optional.of(userFinal));

        //     When
        User createdUser = this.userService.createUser(userRB);

//        Then
        assertThat(createdUser).isEqualTo(userFinal);

        verify(userRepository, VerificationModeFactory.times(1)).save(userRB);
        verify(interestRepository, VerificationModeFactory.times(1))
                .save(userS1.getInterestSet().iterator().next());
        verify(userRepository, VerificationModeFactory.times(1)).findById(userFinal.getUserId());

        reset(userRepository);
        reset(interestRepository);
    }


//    updateUserById

    @Test
    public void givenUserWithInterest_whenUpdateUserById_thenSucess() throws Exception {
        //        given
        User userRB = produceUser(1L,"name1", "male", "nkname1", "USA", 0,
                1L, "dota", "noob");
        User userS1 = produceUser(1L,"name2", "male", "nkname1", "USA", 0,
                1L, "dota", "noob");

        given(userRepository.findById(1L)).willReturn(Optional.of(userRB));
        given(userRepository.save(userRB)).willReturn(userS1);
//      when
        this.userService.updateUserById(userRB, 1L);
//      Then
        verify(userRepository, VerificationModeFactory.times(1)).findById(1L);
        verify(userRepository, VerificationModeFactory.times(1)).save(userRB);
        reset(userRepository);
    }


//      deleteUserById
    @Test
    public void whenDeleteUserById_ThenSuccess() throws Exception {
        //        given
        User userRB = produceUser(1L,"name1", "male", "nkname1", "USA", 0,
                1L, "dota", "noob");
        given(userRepository.findById(1L)).willReturn(Optional.of(userRB));

        //      when & then

        this.userService.deleteUserById(1L);

        verify(interestRepository, VerificationModeFactory.times(1)).deleteInterestsByUserId(1L);
        verify(userRepository, VerificationModeFactory.times(1)).deleteById(1L);

        reset(userRepository);
        reset(interestRepository);

    }

//    getMatchUserByGameAndLevelAndGeography
    @Test
    public void givenUsers_whenGetMatchUserByGameAndLevelAndGeography_thenReturnUserList() throws Exception {
//        given
        List<User> userList = createUserList();
        given(userRepository.findMatchUserByGameAndLevelAndGeography("dota", "noob", "USA"))
                .willReturn(userList);

//      when and then
        List<User> resList = this.userService.getMatchUserByGameAndLevelAndGeography("dota", "noob", "USA");
        assertThat(resList.size()).isEqualTo(2);
        for (User user : userList) {
            assertThat(resList).contains(user);
        }
        verify(userRepository, VerificationModeFactory.times(1))
                .findMatchUserByGameAndLevelAndGeography("dota", "noob", "USA");
        reset(userRepository);
    }

    //    getOtherUserMatchUserInterest
    @Test
    public void givenUserIdAndInterestId_whenGetOtherUserMatchUserInterest_thenReturnUserList() throws Exception {
//        given
        Long userId = 1L;
        Long interestId = 1L;

        List<User> userList = createUserList();
        given(userRepository.findById(userId)).willReturn(Optional.of(userList.get(0)));
        given(interestRepository.findById(interestId))
                .willReturn(Optional.of(userList.get(0).getInterestSet().iterator().next()));
        given(userRepository.findMatchUserByGameAndLevelAndGeography("dota", "noob", "USA"))
                .willReturn(Arrays.asList(userList.get(1)));

//      when and then
        List<User> resList = this.userService.getOtherUserMatchUserInterest(userId, interestId);
        assertThat(resList.size()).isEqualTo(1);
        assertThat(resList).contains(userList.get(1));

        verify(userRepository, VerificationModeFactory.times(1)).findById(userId);
        verify(interestRepository, VerificationModeFactory.times(1)).findById(interestId);
        verify(userRepository, VerificationModeFactory.times(1))
                .findMatchUserByGameAndLevelAndGeography("dota", "noob", "USA");

        reset(userRepository);
        reset(interestRepository);
    }

    //    getUserWithMaxCreditByGameAndLevel
    @Test
    public void givenGameAndLevel_whenGetUserWithMaxCreditByGameAndLevel_thenReturnUserList() throws Exception {
//        given
        List<User> userList = createUserList();
        given(userRepository.findUserWithMaxCreditByGameAndLevel("dota", "noob"))
                .willReturn(userList);

//      when and then
        List<User> resList = this.userRepository.findUserWithMaxCreditByGameAndLevel("dota", "noob");
        assertThat(resList.size()).isEqualTo(2);
        for (User user : userList) {
            assertThat(resList).contains(user);
        }

        verify(userRepository, VerificationModeFactory.times(1))
                .findUserWithMaxCreditByGameAndLevel("dota", "noob");
        reset(userRepository);
    }

    private User produceUser(Long userId, String name, String gender, String nickname, String geography, Integer credit,
                             Long interestId, String game, String level) {
        User user = new User(name, gender, nickname, geography, null);
        user.setUserId(userId);
        Interest interest = new Interest(game, level, credit, user);
        interest.setInterestId(interestId);
        Set<Interest> interestSet = new HashSet<>();
        interestSet.add(interest);
        user.setInterestSet(interestSet);
        return user;
    }

    private List<User> createUserList() {
        User user1 = produceUser(1L,"name1", "male", "nkname1", "USA", 0,
                1L, "dota", "noob");
        User user2 = produceUser(2L,"name2", "male", "nkname2", "USA", 0,
                2L,"dota", "noob");
        List<User> userList =  Arrays.asList(user1, user2);
        return userList;
    }

}
