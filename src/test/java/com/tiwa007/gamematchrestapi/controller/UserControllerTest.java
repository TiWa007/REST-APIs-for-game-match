package com.tiwa007.gamematchrestapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiwa007.gamematchrestapi.Service.UserService;
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
    private UserService userService;


    @Test
    public void contextLoads() throws Exception {
        assertThat(mockMvc).isNotNull();
        assertThat(userService).isNotNull();
    }

    //    getAllUsers

    @Test
    public void givenUsers_whenGetAllUsers_thenReturnUserList() throws Exception {
//        given
        List<User> userList = createUserList();
        given(userService.getAllUsers()).willReturn(userList);

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

        verify(userService, VerificationModeFactory.times(1)).getAllUsers();
    }

    @Test
    public void givenUserId_whenGetUserById_thenReturnUser() throws Exception {

//        given
        List<User> userList = createUserList();
        given(userService.getUserById(userList.get(0).getUserId())).willReturn(userList.get(0));
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

        verify(userService, VerificationModeFactory.times(1)).getUserById(userList.get(0).getUserId());
    }

//    createUser()


    @Test
    public void givenUserWithoutInterest_whenCreateUser_thenReturnUser() throws Exception {
        //        given
        User user = new User("name1", "male", "nkname1", "USA");
        User user1 = new User("name1", "male", "nkname1", "USA");
        user1.setUserId(1L);

        given(userService.createUser(user)).willReturn(user1);

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

        verify(userService, VerificationModeFactory.times(1)).createUser(user);
    }

    @Test
    public void givenUserWithInterest_whenCreateUser_thenReturnUser() throws Exception {
        //        given
        User userRB = produceUser(null,"name1", "male", "nkname1", "USA", 0,
                null, "dota", "noob");
        User userFinal = produceUser(1L,"name1", "male", "nkname1", "USA", 0,
                1L, "dota", "noob");

        given(userService.createUser(userRB)).willReturn(userFinal);

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

        verify(userService, VerificationModeFactory.times(1)).createUser(userRB);

    }


//    updateUserById

    @Test
    public void givenUserWithInterest_whenUpdateUserById_thenSucess() throws Exception {
        //        given
        User userRB = produceUser(1L,"name1", "male", "nkname1", "USA", 0,
                1L, "dota", "noob");

        //      when & then
        mockMvc.perform(put("/api/user/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRB)))
                .andExpect(status().isOk());

    }


//      deleteUserById
    @Test
    public void whenDeleteUserById_ThenSuccess() throws Exception {
        //      when & then
        mockMvc.perform(delete("/api/user/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

//    getMatchUserByGameAndLevelAndGeography
    @Test
    public void givenUsers_whenGetMatchUserByGameAndLevelAndGeography_thenReturnUserList() throws Exception {
//        given
        List<User> userList = createUserList();
        given(userService.getMatchUserByGameAndLevelAndGeography("dota", "noob", "USA"))
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

        verify(userService, VerificationModeFactory.times(1))
                .getMatchUserByGameAndLevelAndGeography("dota", "noob", "USA");
    }

    //    getOtherUserMatchUserInterest
    @Test
    public void givenUserIdAndInterestId_whenGetOtherUserMatchUserInterest_thenReturnUserList() throws Exception {
//        given
        Long userId = 1L;
        Long interestId = 1L;
        List<User> userList = createUserList();

        given(userService.getOtherUserMatchUserInterest(userId, interestId))
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

        verify(userService, VerificationModeFactory.times(1))
                .getOtherUserMatchUserInterest(userId, interestId);

    }

    //    getUserWithMaxCreditByGameAndLevel
    @Test
    public void givenGameAndLevel_whenGetUserWithMaxCreditByGameAndLevel_thenReturnUserList() throws Exception {
//        given
        List<User> userList = createUserList();
        given(userService.getUserWithMaxCreditByGameAndLevel("dota", "noob"))
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
        verify(userService, VerificationModeFactory.times(1))
                .getUserWithMaxCreditByGameAndLevel("dota", "noob");
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
