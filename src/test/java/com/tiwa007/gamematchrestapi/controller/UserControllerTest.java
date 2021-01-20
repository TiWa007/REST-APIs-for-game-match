package com.tiwa007.gamematchrestapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiwa007.gamematchrestapi.entity.Interest;
import com.tiwa007.gamematchrestapi.entity.User;
import com.tiwa007.gamematchrestapi.repository.InterestRepository;
import com.tiwa007.gamematchrestapi.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private InterestRepository interestRepository;

    @Test
    public void contextLoads() throws Exception {
        assertThat(mockMvc).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(interestRepository).isNotNull();
    }

    //    getAllUsers

    @Test
    public void givenUsers_whenGetAllUsers_thenReturnUserList() throws Exception {
//        given
        List<User> userList = createUserList();
        given(userRepository.findAll()).willReturn(userList);

//      when and then
        mockMvc.perform(get("/api/user").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[0].name", is("name1")))
                .andExpect(jsonPath("$[0].gender", is("male")))
                .andExpect(jsonPath("$[0].nickname", is("nkname1")))
                .andExpect(jsonPath("$[0].geography", is("USA")))
                .andExpect(jsonPath("$[0].interestSet", hasSize(1)))
                .andExpect(jsonPath("$[0].interestSet[0].interestId", is(1)))
                .andExpect(jsonPath("$[0].interestSet[0].game", is("dota")))
                .andExpect(jsonPath("$[0].interestSet[0].level", is("noob")))
                .andExpect(jsonPath("$[0].interestSet[0].credit", is(0)))
                .andExpect(jsonPath("$[1].userId", is(2)))
                .andExpect(jsonPath("$[1].name", is("name2")))
                .andExpect(jsonPath("$[1].gender", is("male")))
                .andExpect(jsonPath("$[1].nickname", is("nkname2")))
                .andExpect(jsonPath("$[1].geography", is("USA")))
                .andExpect(jsonPath("$[1].interestSet", hasSize(1)))
                .andExpect(jsonPath("$[1].interestSet[0].interestId", is(2)))
                .andExpect(jsonPath("$[1].interestSet[0].game", is("dota")))
                .andExpect(jsonPath("$[1].interestSet[0].level", is("noob")))
                .andExpect(jsonPath("$[1].interestSet[0].credit", is(0)));

        verify(userRepository, VerificationModeFactory.times(1)).findAll();
        reset(userRepository);
    }

    @Test
    public void givenUserId_whenGetUserById_thenReturnUser() throws Exception {

//        given
        List<User> userList = createUserList();
        given(userRepository.findById(userList.get(0).getUserId())).willReturn(Optional.of(userList.get(0)));
//      when & then
        mockMvc.perform(get("/api/user/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.name", is("name1")))
                .andExpect(jsonPath("$.gender", is("male")))
                .andExpect(jsonPath("$.nickname", is("nkname1")))
                .andExpect(jsonPath("$.geography", is("USA")))
                .andExpect(jsonPath("$.interestSet", hasSize(1)))
                .andExpect(jsonPath("$.interestSet[0].interestId", is(1)))
                .andExpect(jsonPath("$.interestSet[0].game", is("dota")))
                .andExpect(jsonPath("$.interestSet[0].level", is("noob")))
                .andExpect(jsonPath("$.interestSet[0].credit", is(0)));

        verify(userRepository, VerificationModeFactory.times(1)).findById(userList.get(0).getUserId());
        reset(userRepository);
    }

    @Test
    public void givenInvalidUserId_whenGetUserById_thenException() throws Exception {
        mockMvc.perform(get("/api/user/{id}", 400L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("User cannot be found with id: 400"));
    }

//    createUser()

    @Test
    public void givenUserWithSameInterest_whenCreateUser_thenException() throws Exception {
        //        given
        User user = produceUser(null, "name1", "male", "nkname1", "USA", 0,
                null, "dota", "noob");
        Interest dupInterest = new Interest("dota", "noob", 10, null);
        user.getInterestSet().add(dupInterest);
//        when & then
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User has different interests with same game"));

    }

    @Test
    public void givenUserWithoutInterest_whenCreateUser_thenReturnUser() throws Exception {
        //        given
        User user = new User("name1", "male", "nkname1", "USA");
        User user1 = new User("name1", "male", "nkname1", "USA");
        user1.setUserId(1L);

        given(userRepository.save(user)).willReturn(user1);
        given(userRepository.findById(1L)).willReturn(Optional.of(user1));

        //      when & then
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.name", is("name1")))
                .andExpect(jsonPath("$.gender", is("male")))
                .andExpect(jsonPath("$.nickname", is("nkname1")))
                .andExpect(jsonPath("$.geography", is("USA")))
                .andExpect(jsonPath("$.interestSet", hasSize(0)));

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

        //      when & then
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRB)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.name", is("name1")))
                .andExpect(jsonPath("$.gender", is("male")))
                .andExpect(jsonPath("$.nickname", is("nkname1")))
                .andExpect(jsonPath("$.geography", is("USA")))
                .andExpect(jsonPath("$.interestSet", hasSize(1)))
                .andExpect(jsonPath("$.interestSet[0].interestId", is(1)))
                .andExpect(jsonPath("$.interestSet[0].game", is("dota")))
                .andExpect(jsonPath("$.interestSet[0].level", is("noob")))
                .andExpect(jsonPath("$.interestSet[0].credit", is(0)));

        verify(userRepository, VerificationModeFactory.times(1)).save(userRB);
        verify(interestRepository, VerificationModeFactory.times(1))
                .save(userS1.getInterestSet().iterator().next());
        verify(userRepository, VerificationModeFactory.times(1)).findById(userFinal.getUserId());

        reset(userRepository);
        reset(interestRepository);
    }

//    Todo inner condition

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

        //      when & then
        mockMvc.perform(put("/api/user/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRB)))
                .andExpect(status().isOk());

        verify(userRepository, VerificationModeFactory.times(1)).findById(1L);
        verify(userRepository, VerificationModeFactory.times(1)).save(userRB);
        reset(userRepository);
    }

//    TODO check inner interest changed

//      deleteUserById
    @Test
    public void whenDeleteUserById_ThenSuccess() throws Exception {
        //        given
        User userRB = produceUser(1L,"name1", "male", "nkname1", "USA", 0,
                1L, "dota", "noob");
        given(userRepository.findById(1L)).willReturn(Optional.of(userRB));

        //      when & then
        mockMvc.perform(delete("/api/user/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

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
        mockMvc.perform(get("/api/user/match")
                .contentType(MediaType.APPLICATION_JSON)
                .param("game", "dota")
                .param("level", "noob")
                .param("geography", "USA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[0].name", is("name1")))
                .andExpect(jsonPath("$[0].gender", is("male")))
                .andExpect(jsonPath("$[0].nickname", is("nkname1")))
                .andExpect(jsonPath("$[0].geography", is("USA")))
                .andExpect(jsonPath("$[0].interestSet", hasSize(1)))
                .andExpect(jsonPath("$[0].interestSet[0].interestId", is(1)))
                .andExpect(jsonPath("$[0].interestSet[0].game", is("dota")))
                .andExpect(jsonPath("$[0].interestSet[0].level", is("noob")))
                .andExpect(jsonPath("$[0].interestSet[0].credit", is(0)))
                .andExpect(jsonPath("$[1].userId", is(2)))
                .andExpect(jsonPath("$[1].name", is("name2")))
                .andExpect(jsonPath("$[1].gender", is("male")))
                .andExpect(jsonPath("$[1].nickname", is("nkname2")))
                .andExpect(jsonPath("$[1].geography", is("USA")))
                .andExpect(jsonPath("$[1].interestSet", hasSize(1)))
                .andExpect(jsonPath("$[1].interestSet[0].interestId", is(2)))
                .andExpect(jsonPath("$[1].interestSet[0].game", is("dota")))
                .andExpect(jsonPath("$[1].interestSet[0].level", is("noob")))
                .andExpect(jsonPath("$[1].interestSet[0].credit", is(0)));

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
        mockMvc.perform(get("/api/user/{userId}/match/{interestId}", userId, interestId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(2)))
                .andExpect(jsonPath("$[0].name", is("name2")))
                .andExpect(jsonPath("$[0].gender", is("male")))
                .andExpect(jsonPath("$[0].nickname", is("nkname2")))
                .andExpect(jsonPath("$[0].geography", is("USA")))
                .andExpect(jsonPath("$[0].interestSet", hasSize(1)))
                .andExpect(jsonPath("$[0].interestSet[0].interestId", is(2)))
                .andExpect(jsonPath("$[0].interestSet[0].game", is("dota")))
                .andExpect(jsonPath("$[0].interestSet[0].level", is("noob")))
                .andExpect(jsonPath("$[0].interestSet[0].credit", is(0)));

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
        mockMvc.perform(get("/api/user/interest/credit/max")
                .contentType(MediaType.APPLICATION_JSON)
                .param("game", "dota")
                .param("level", "noob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[0].name", is("name1")))
                .andExpect(jsonPath("$[0].gender", is("male")))
                .andExpect(jsonPath("$[0].nickname", is("nkname1")))
                .andExpect(jsonPath("$[0].geography", is("USA")))
                .andExpect(jsonPath("$[0].interestSet", hasSize(1)))
                .andExpect(jsonPath("$[0].interestSet[0].interestId", is(1)))
                .andExpect(jsonPath("$[0].interestSet[0].game", is("dota")))
                .andExpect(jsonPath("$[0].interestSet[0].level", is("noob")))
                .andExpect(jsonPath("$[0].interestSet[0].credit", is(0)))
                .andExpect(jsonPath("$[1].userId", is(2)))
                .andExpect(jsonPath("$[1].name", is("name2")))
                .andExpect(jsonPath("$[1].gender", is("male")))
                .andExpect(jsonPath("$[1].nickname", is("nkname2")))
                .andExpect(jsonPath("$[1].geography", is("USA")))
                .andExpect(jsonPath("$[1].interestSet", hasSize(1)))
                .andExpect(jsonPath("$[1].interestSet[0].interestId", is(2)))
                .andExpect(jsonPath("$[1].interestSet[0].game", is("dota")))
                .andExpect(jsonPath("$[1].interestSet[0].level", is("noob")))
                .andExpect(jsonPath("$[1].interestSet[0].credit", is(0)));
        verify(userRepository, VerificationModeFactory.times(1))
                .findUserWithMaxCreditByGameAndLevel("dota", "noob");
        reset(userRepository);
    }


//------------------- Interest --------------------------
//  getInterestByInterestId
    @Test
    public void givenUserIdAndInterestId_whenGetInterestByInterestId_thenReturnInterest() throws Exception {

//        given
        List<User> userList = createUserList();
        Interest interest = userList.get(0).getInterestSet().iterator().next();
        given(interestRepository.findById(interest.getInterestId())).willReturn(Optional.of(interest));
//      when & then
        mockMvc.perform(get("/api/user/{userId}/interest/{interestId}", userList.get(0).getUserId(), interest.getInterestId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interestId", is(1)))
                .andExpect(jsonPath("$.game", is("dota")))
                .andExpect(jsonPath("$.level", is("noob")))
                .andExpect(jsonPath("$.credit", is(0)));

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
        mockMvc.perform(post("/api/user/{userId}/interest", userList.get(0).getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(interest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interestId", is(4)))
                .andExpect(jsonPath("$.game", is("fortnite")))
                .andExpect(jsonPath("$.level", is("pro")))
                .andExpect(jsonPath("$.credit", is(1)));

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
        mockMvc.perform(post("/api/user/{userId}/interest", userList.get(0).getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(interest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("User already has interest with game: " + interest.getGame()));

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
        mockMvc.perform(put("/api/user/{userId}/interest/{interestId}",
                userList.get(0).getUserId(), interest.getInterestId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateInterest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

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
        mockMvc.perform(put("/api/user/{userId}/interest/{interestId}",
                userList.get(0).getUserId(), 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateInterest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("The interest has a different interestId: " + interest.getInterestId()
                                + " from path variable: " + 1));

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
        Interest updateInterest = new Interest("fortnite", "noob", 1, userList.get(0));
        updateInterest.setInterestId(4L);
        Interest existingInterest = new Interest("fortnite", "noob", 1, userList.get(0));
        existingInterest.setInterestId(3L);

        given(userRepository.findById(userList.get(0).getUserId())).willReturn(Optional.of(userList.get(0)));
        given(interestRepository.findById(interest.getInterestId()))
                .willReturn(Optional.of(interest));
        given(interestRepository.findInterestByUserAndGame(userList.get(0), interest.getGame()))
                .willReturn(Arrays.asList(existingInterest));
        given(interestRepository.save(interest)).willReturn(updateInterest);

//      when & then
        mockMvc.perform(put("/api/user/{userId}/interest/{interestId}",
                userList.get(0).getUserId(), 4L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(interest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("User already has interest with game: " + interest.getGame()));

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
        mockMvc.perform(delete("/api/user/{userId}/interest/{interestId}",
                userList.get(0).getUserId(), interest.getInterestId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

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
        mockMvc.perform(put("/api/user/{userId}/interest/{interestId}/credit",
                userList.get(0).getUserId(), interest.getInterestId())
                .contentType(MediaType.APPLICATION_JSON)
                .param("credit", credit.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

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


    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
