package com.tiwa007.gamematchrestapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiwa007.gamematchrestapi.service.InterestService;
import com.tiwa007.gamematchrestapi.entity.Interest;
import com.tiwa007.gamematchrestapi.entity.User;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = InterestController.class)
public class InterestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterestService interestService;


    @Test
    public void contextLoads() throws Exception {
        assertThat(mockMvc).isNotNull();
        assertThat(interestService).isNotNull();
    }

//  getInterestByInterestId
    @Test
    public void givenUserIdAndInterestId_whenGetInterestByInterestId_thenReturnInterest() throws Exception {

//        given
        List<User> userList = createUserList();
        Interest interest = userList.get(0).getInterestSet().iterator().next();
        given(interestService.getInterestByInterestId(userList.get(0).getUserId(), interest.getInterestId()))
                .willReturn(interest);
//      when & then
        mockMvc.perform(get("/api/user/{userId}/interest/{interestId}", userList.get(0).getUserId(), interest.getInterestId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interestId", is(1)))
                .andExpect(jsonPath("$.game", is("dota")))
                .andExpect(jsonPath("$.level", is("noob")))
                .andExpect(jsonPath("$.credit", is(0)));

        verify(interestService, VerificationModeFactory.times(1))
                .getInterestByInterestId(userList.get(0).getUserId(), interest.getInterestId());
    }

    //  createUserInterest
    @Test
    public void givenInterest_whenCreateUserInterest_thenReturnInterest() throws Exception {

//        given
        List<User> userList = createUserList();
        Interest interest = new Interest("fortnite", "pro", 1, null);
        Interest resInterest = new Interest("fortnite", "pro", 1, userList.get(0));
        resInterest.setInterestId(4L);

        given(interestService.createUserInterest(interest, userList.get(0).getUserId())).willReturn(resInterest);

//      when & then
        mockMvc.perform(post("/api/user/{userId}/interest", userList.get(0).getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(interest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interestId", is(4)))
                .andExpect(jsonPath("$.game", is("fortnite")))
                .andExpect(jsonPath("$.level", is("pro")))
                .andExpect(jsonPath("$.credit", is(1)));

        verify(interestService, VerificationModeFactory.times(1))
                .createUserInterest(interest, userList.get(0).getUserId());
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

        given(interestService.createUserInterest(interest, interest.getInterestId())).willReturn(updateInterest);

//      when & then
        mockMvc.perform(put("/api/user/{userId}/interest/{interestId}",
                userList.get(0).getUserId(), interest.getInterestId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateInterest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

    }

    //  deleteUserInterestByInterestId
    @Test
    public void givenInterestId_whenDeleteUserInterestByInterestId_thenSuccess() throws Exception {


//      when & then
        mockMvc.perform(delete("/api/user/{userId}/interest/{interestId}",
                1L, 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    //  updateUserInterestCreditByInterestId
    @Test
    public void givenInterestCreditAndInterestId_whenUpdateUserInterestCreditByInterestId_thenSucess() throws Exception {

//      when & then
        mockMvc.perform(put("/api/user/{userId}/interest/{interestId}/credit",
                1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .param("credit", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
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
