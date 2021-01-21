package com.tiwa007.gamematchrestapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiwa007.gamematchrestapi.GameMatchRestApiApplication;
import com.tiwa007.gamematchrestapi.controller.InterestRequest;
import com.tiwa007.gamematchrestapi.controller.UserRequest;
import com.tiwa007.gamematchrestapi.controller.UserRequestWithoutInterest;
import com.tiwa007.gamematchrestapi.entity.User;
import com.tiwa007.gamematchrestapi.repository.InterestRepository;
import com.tiwa007.gamematchrestapi.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = GameMatchRestApiApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterestRepository interestRepository;

    @Before
    public void initDb() {
    }

    @After
    public void resetDb() {
        userRepository.deleteAll();
        interestRepository.deleteAll();
    }

    @Test
    public void contextLoads() throws Exception {
        assertThat(mockMvc).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(interestRepository).isNotNull();
    }

//    getAllUsers
    @Test
    @Sql({"/h2_integration_test.sql"})
    public void givenUsers_whenGetAllUsers_thenReturnUserList() throws Exception {

//      when and then
        mockMvc.perform(get("/api/user").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].userId", is(1001)))
                .andExpect(jsonPath("$[1].userId", is(1002)))
                .andExpect(jsonPath("$[2].userId", is(1003)))
                .andExpect(jsonPath("$[3].userId", is(1004)))
                .andExpect(jsonPath("$[4].userId", is(1005)))
                .andExpect(jsonPath("$[5].userId", is(1006)))
                .andExpect(jsonPath("$[6].userId", is(1007)))
                .andExpect(jsonPath("$[7].userId", is(1008)))
                .andExpect(jsonPath("$[8].userId", is(1009)))
                .andExpect(jsonPath("$[9].userId", is(1010)));
    }

//    getUserById
    @Test
    @Sql({"/h2_integration_test.sql"})
    public void givenUserId_whenGetUserById_thenReturnUser() throws Exception {

//      when & then
        mockMvc.perform(get("/api/user/{id}", 1001L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1001)))
                .andExpect(jsonPath("$.name", is("Name1001")))
                .andExpect(jsonPath("$.gender", is("male")))
                .andExpect(jsonPath("$.nickname", is("nickname1001")))
                .andExpect(jsonPath("$.geography", is("Europe")))
                .andExpect(jsonPath("$.interestSet", hasSize(2)))
                .andExpect(jsonPath("$.interestSet[0].interestId", is(1001)))
                .andExpect(jsonPath("$.interestSet[0].game", is("fortnite")))
                .andExpect(jsonPath("$.interestSet[0].level", is("noob")))
                .andExpect(jsonPath("$.interestSet[0].credit", is(10)))
                .andExpect(jsonPath("$.interestSet[1].interestId", is(1002)))
                .andExpect(jsonPath("$.interestSet[1].game", is("call of duty")))
                .andExpect(jsonPath("$.interestSet[1].level", is("noob")))
                .andExpect(jsonPath("$.interestSet[1].credit", is(8)));
    }

    //    createUser()
    @Test
    public void givenUserWithoutInterest_whenCreateUser_thenReturnUser() throws Exception {
        //        given
        User user = new User("name1", "male", "nkname1", "USA");

        //      when & then
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.name", is("name1")))
                .andExpect(jsonPath("$.gender", is("male")))
                .andExpect(jsonPath("$.nickname", is("nkname1")))
                .andExpect(jsonPath("$.geography", is("USA")))
                .andExpect(jsonPath("$.interestSet", hasSize(0)));
    }

    @Test
    public void givenUserWithInterest_whenCreateUser_thenReturnUser() throws Exception {
        //        given
        UserRequest userRB = produceTestUser("name1", "male", "nkname1", "USA", 0,
                "dota", "noob");

        //      when & then
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRB)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.name", is("name1")))
                .andExpect(jsonPath("$.gender", is("male")))
                .andExpect(jsonPath("$.nickname", is("nkname1")))
                .andExpect(jsonPath("$.geography", is("USA")))
                .andExpect(jsonPath("$.interestSet", hasSize(1)))
                .andExpect(jsonPath("$.interestSet[0].interestId").exists())
                .andExpect(jsonPath("$.interestSet[0].game", is("dota")))
                .andExpect(jsonPath("$.interestSet[0].level", is("noob")))
                .andExpect(jsonPath("$.interestSet[0].credit", is(0)));
    }

    //    createUser() with exception
    public void givenUserWithInvalidInformation_whenCreateUser_thenException() throws Exception {
        //        given
        UserRequest userRB = produceTestUser("name1", "famale", "nkname1", "USAA", -1,
                "dota1", "noob1");

        //      when & then
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRB)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void givenUserWithSameInterest_whenCreateUser_thenException() throws Exception {
        //        given
        UserRequest userRB = produceTestUser( "name1", "male", "nkname1", "USA", 0,
                "dota", "noob");
        InterestRequest dupInterest = new InterestRequest("dota", "noob", 10);
        userRB.getInterestSet().add(dupInterest);
//        when & then
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRB)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User has different interests with same game"));

    }

    //    updateUserById

    @Test
    @Sql({"/h2_integration_test.sql"})
    public void givenUserWithInterest_whenUpdateUserById_thenSucess() throws Exception {
        //        given
        UserRequestWithoutInterest userRB = new UserRequestWithoutInterest("name1", "female", "nkname1", "USA");

        //      when & then
        mockMvc.perform(put("/api/user/{userId}", 1001L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRB)))
                .andExpect(status().isOk());

        User user = this.userRepository.findById(1001L).get();
        assertThat(user.getGeography()).isEqualTo(userRB.getGeography());
        assertThat(user.getName()).isEqualTo(userRB.getName());
        assertThat(user.getGender()).isEqualTo(userRB.getGender());
        assertThat(user.getNickname()).isEqualTo(userRB.getNickname());
    }

    //      deleteUserById
    @Test
    @Sql({"/h2_integration_test.sql"})
    public void whenDeleteUserById_ThenSuccess() throws Exception {
        //      when & then
        mockMvc.perform(delete("/api/user/{userId}", 1001L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        assertThat(this.userRepository.findById(1001L).isPresent()).isFalse();
        assertThat(this.interestRepository.findById(1001L).isPresent()).isFalse();
        assertThat(this.interestRepository.findById(1002L).isPresent()).isFalse();

    }

    //    getMatchUserByGameAndLevelAndGeography
    @Test
    @Sql({"/h2_integration_test.sql"})
    public void givenUsers_whenGetMatchUserByGameAndLevelAndGeography_thenReturnUserList() throws Exception {
//      when and then
        mockMvc.perform(get("/api/user/match")
                .contentType(MediaType.APPLICATION_JSON)
                .param("game", "dota")
                .param("level", "noob")
                .param("geography", "Asia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId", is(1002)))
                .andExpect(jsonPath("$[0].name", is("Name1002")))
                .andExpect(jsonPath("$[0].gender", is("male")))
                .andExpect(jsonPath("$[0].nickname", is("nickname1002")))
                .andExpect(jsonPath("$[0].geography", is("Asia")))
                .andExpect(jsonPath("$[0].interestSet", hasSize(3)))
                .andExpect(jsonPath("$[1].userId", is(1005)))
                .andExpect(jsonPath("$[1].name", is("Name1005")))
                .andExpect(jsonPath("$[1].gender", is("female")))
                .andExpect(jsonPath("$[1].nickname", is("nickname1005")))
                .andExpect(jsonPath("$[1].geography", is("Asia")))
                .andExpect(jsonPath("$[1].interestSet", hasSize(1)))
                .andExpect(jsonPath("$[1].interestSet[0].interestId", is(1009)))
                .andExpect(jsonPath("$[1].interestSet[0].game", is("dota")))
                .andExpect(jsonPath("$[1].interestSet[0].level", is("noob")))
                .andExpect(jsonPath("$[1].interestSet[0].credit", is(5)));
    }


    //    getOtherUserMatchUserInterest
    @Test
    @Sql({"/h2_integration_test.sql"})
    public void givenUserIdAndInterestId_whenGetOtherUserMatchUserInterest_thenReturnUserList() throws Exception {

//      when and then
        mockMvc.perform(get("/api/user/{userId}/match/{interestId}", 1001L, 1001L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(1003)));
    }

//        getUserWithMaxCreditByGameAndLevel
    @Test
    @Sql({"/h2_integration_test.sql"})
    public void givenGameAndLevel_whenGetUserWithMaxCreditByGameAndLevel_thenReturnUserList() throws Exception {

//      when and then
        mockMvc.perform(get("/api/user/interest/credit/max")
                .contentType(MediaType.APPLICATION_JSON)
                .param("game", "dota")
                .param("level", "noob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(1005)));
    }

//    Helper methods

    private UserRequest produceTestUser(String name, String gender, String nickname, String geography, Integer credit,
                                        String game, String level) {
        UserRequest user = new UserRequest(name, gender, nickname, geography, null);
        InterestRequest interest = new InterestRequest(game, level, credit);
        Set<InterestRequest> interestSet = new HashSet<>();
        interestSet.add(interest);
        user.setInterestSet(interestSet);
        return user;
    }


    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
