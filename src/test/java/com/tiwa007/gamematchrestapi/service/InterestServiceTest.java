package com.tiwa007.gamematchrestapi.service;

import com.tiwa007.gamematchrestapi.common.exception.InvalidRequestException;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class InterestServiceTest {

    @TestConfiguration
    static class InterstServiceTestContextConfiguration {
        @Bean
        public InterestService interestService() {
            return new InterestService();
        }
    }

    @Autowired
    private InterestService interestService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private InterestRepository interestRepository;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void contextLoads() throws Exception {
        assertThat(interestService).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(interestRepository).isNotNull();
    }

//  getInterestByInterestId
    @Test
    public void givenUserIdAndInterestId_whenGetInterestByInterestId_thenReturnInterest() throws Exception {

//        given
        List<User> userList = createUserList();
        Interest interest = userList.get(0).getInterestSet().iterator().next();
        given(interestRepository.findById(interest.getInterestId())).willReturn(Optional.of(interest));
//      when & then
        Interest restInterest = this.interestService.getInterestByInterestId(userList.get(0).getUserId(), interest.getInterestId());

        assertThat(restInterest).isEqualTo(interest);
        verify(interestRepository, VerificationModeFactory.times(1)).findById(interest.getInterestId());
        reset(interestRepository);
    }

//  createUserInterest
    @Test
    public void givenInterest_whenCreateUserInterest_thenReturnInterest() throws Exception {

//        given
        List<User> userList = createUserList();
        Interest interest = new Interest("fortnite", "pro", 1, userList.get(0));
        Interest resInterest = new Interest("fortnite", "pro", 1, userList.get(0));
        resInterest.setInterestId(4L);

        given(userRepository.findById(userList.get(0).getUserId())).willReturn(Optional.of(userList.get(0)));
        given(interestRepository.findInterestByUserAndGame(userList.get(0), "fortnite"))
                .willReturn(new ArrayList<Interest>());
        given(interestRepository.save(interest)).willReturn(resInterest);

//      when & then
        Interest createdInterest = this.interestService.createUserInterest(interest, userList.get(0).getUserId());

        assertThat(createdInterest).isEqualTo(resInterest);

        verify(userRepository, VerificationModeFactory.times(1))
                .findById(userList.get(0).getUserId());
        verify(interestRepository, VerificationModeFactory.times(1))
                .findInterestByUserAndGame(userList.get(0), "fortnite");
        verify(interestRepository, VerificationModeFactory.times(1)).save(interest);

        reset(userRepository);
        reset(interestRepository);
    }

    @Test
    public void givenInterestWithSameGame_whenCreateUserInterest_thenException() throws Exception {

//        given
        List<User> userList = createUserList();
        Interest interest = new Interest("fortnite", "pro", 1, userList.get(0));

        given(userRepository.findById(userList.get(0).getUserId())).willReturn(Optional.of(userList.get(0)));
        given(interestRepository.findInterestByUserAndGame(userList.get(0), "fortnite"))
                .willReturn(Arrays.asList(interest));

//      when & then
        exceptionRule.expect(InvalidRequestException.class);
        exceptionRule.expectMessage("User already has interest with game: " + interest.getGame());

        this.interestService.createUserInterest(interest,userList.get(0).getUserId());

        verify(userRepository, VerificationModeFactory.times(1))
                .findById(userList.get(0).getUserId());
        verify(interestRepository, VerificationModeFactory.times(1))
                .findInterestByUserAndGame(userList.get(0), "fortnite");

        reset(userRepository);
        reset(interestRepository);
    }

    //  updateUserInterestByInterestId
    @Test
    public void givenInterestAndInterestId_whenUpdateUserInterestByInterestId_thenReturnInterest() throws Exception {

//        given
        List<User> userList = createUserList();
        Interest interest = new Interest("fortnite", "pro", 1, userList.get(0));
        interest.setInterestId(4L);
        Interest updateInterest = new Interest("fortnite", "noob", 1, userList.get(0));
        updateInterest.setInterestId(4L);

        given(userRepository.findById(userList.get(0).getUserId())).willReturn(Optional.of(userList.get(0)));
        given(interestRepository.findById(interest.getInterestId()))
                .willReturn(Optional.of(interest));
        given(interestRepository.save(interest)).willReturn(updateInterest);

//      when & then

        this.interestService.updateUserInterestByInterestId(interest,
                userList.get(0).getUserId(), interest.getInterestId());

        verify(userRepository, VerificationModeFactory.times(1))
                .findById(userList.get(0).getUserId());
        verify(interestRepository, VerificationModeFactory.times(1))
                .findById(interest.getInterestId());
        verify(interestRepository, VerificationModeFactory.times(1)).save(interest);

        reset(userRepository);
        reset(interestRepository);
    }

    @Test
    public void givenInterestHasDifferentInterestId_whenUpdateUserInterestByInterestId_thenException() throws Exception {

//        given
        List<User> userList = createUserList();
        Interest interest = new Interest("fortnite", "pro", 1, userList.get(0));
        interest.setInterestId(4L);
        Interest updateInterest = new Interest("fortnite", "noob", 1, userList.get(0));
        updateInterest.setInterestId(4L);

        given(userRepository.findById(userList.get(0).getUserId())).willReturn(Optional.of(userList.get(0)));
        given(interestRepository.findById(interest.getInterestId()))
                .willReturn(Optional.of(interest));
        given(interestRepository.save(interest)).willReturn(updateInterest);

//      when & then

        exceptionRule.expect(InvalidRequestException.class);
        exceptionRule.expectMessage("The interest has a different interestId: " + interest.getInterestId()
                + " from path variable: " + 1);

        this.interestService.updateUserInterestByInterestId(interest, userList.get(0).getUserId(), 1L);

        verify(userRepository, VerificationModeFactory.times(0))
                .findById(userList.get(0).getUserId());
        verify(interestRepository, VerificationModeFactory.times(0))
                .findById(interest.getInterestId());
        verify(interestRepository, VerificationModeFactory.times(0)).save(interest);

    }

    @Test
    public void givenInterestWithSameGame_whenUpdateUserInterestByInterestId_thenException() throws Exception {

//        given
        List<User> userList = createUserList();
        Interest interest = new Interest("fortnite", "pro", 1, userList.get(0));
        interest.setInterestId(4L);
        Interest updateInterest = new Interest("dota", "noob", 1, userList.get(0));
        updateInterest.setInterestId(4L);
        Interest existingInterest = new Interest("fortnite", "noob", 1, userList.get(0));
        existingInterest.setInterestId(3L);

        given(userRepository.findById(userList.get(0).getUserId())).willReturn(Optional.of(userList.get(0)));
        given(interestRepository.findById(interest.getInterestId()))
                .willReturn(Optional.of(updateInterest));
        given(interestRepository.findInterestByUserAndGame(userList.get(0), interest.getGame()))
                .willReturn(Arrays.asList(existingInterest));

//      when & then

        exceptionRule.expect(InvalidRequestException.class);
        exceptionRule.expectMessage("User already has interest with game: " + interest.getGame());

        this.interestService.updateUserInterestByInterestId(interest, userList.get(0).getUserId(), 4L);

        verify(userRepository, VerificationModeFactory.times(1))
                .findById(userList.get(0).getUserId());
        verify(interestRepository, VerificationModeFactory.times(1))
                .findById(interest.getInterestId());
        verify(interestRepository, VerificationModeFactory.times(1))
                .findInterestByUserAndGame(userList.get(0), interest.getGame());
        verify(interestRepository, VerificationModeFactory.times(0)).save(interest);
    }

    //  deleteUserInterestByInterestId
    @Test
    public void givenInterestId_whenDeleteUserInterestByInterestId_thenSuccess() throws Exception {

//        given
        List<User> userList = createUserList();
        Interest interest = userList.get(0).getInterestSet().iterator().next();

        given(userRepository.findById(userList.get(0).getUserId())).willReturn(Optional.of(userList.get(0)));
        given(interestRepository.findById(interest.getInterestId()))
                .willReturn(Optional.of(interest));

//      when & then
        this.interestService.deleteUserInterestByInterestId(userList.get(0).getUserId(), interest.getInterestId());

        verify(userRepository, VerificationModeFactory.times(1))
                .findById(userList.get(0).getUserId());
        verify(interestRepository, VerificationModeFactory.times(1))
                .findById(interest.getInterestId());

        reset(userRepository);
        reset(interestRepository);
    }

    //  updateUserInterestCreditByInterestId
    @Test
    public void givenInterestCreditAndInterestId_whenUpdateUserInterestCreditByInterestId_thenSucess() throws Exception {

//        given
        List<User> userList = createUserList();
        Interest interest = userList.get(0).getInterestSet().iterator().next();
        Integer credit = 8;


        given(userRepository.findById(userList.get(0).getUserId())).willReturn(Optional.of(userList.get(0)));
        given(interestRepository.findById(interest.getInterestId()))
                .willReturn(Optional.of(interest));

//      when & then
        this.interestService.updateUserInterestCreditByInterestId(userList.get(0).getUserId(), interest.getInterestId(), credit);

        verify(userRepository, VerificationModeFactory.times(1))
                .findById(userList.get(0).getUserId());
        verify(interestRepository, VerificationModeFactory.times(1))
                .findById(interest.getInterestId());

        reset(userRepository);
        reset(interestRepository);
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
