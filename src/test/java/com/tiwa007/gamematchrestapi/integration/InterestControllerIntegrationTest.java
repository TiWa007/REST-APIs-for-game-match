package com.tiwa007.gamematchrestapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiwa007.gamematchrestapi.GameMatchRestApiApplication;
import com.tiwa007.gamematchrestapi.controller.InterestRequest;
import com.tiwa007.gamematchrestapi.entity.Interest;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = GameMatchRestApiApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class InterestControllerIntegrationTest {
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

    //  getInterestByInterestId
    @Test
    @Sql({"/h2_integration_test.sql"})
    public void givenUserIdAndInterestId_whenGetInterestByInterestId_thenReturnInterest() throws Exception {
//      when & then
        mockMvc.perform(get("/api/user/{userId}/interest/{interestId}", 1001, 1001)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interestId", is(1001)))
                .andExpect(jsonPath("$.game", is("fortnite")))
                .andExpect(jsonPath("$.level", is("noob")))
                .andExpect(jsonPath("$.credit", is(10)));
    }

//      createUserInterest
    @Test
    @Sql({"/h2_integration_test.sql"})
    public void givenInterest_whenCreateUserInterest_thenReturnInterest() throws Exception {

//        given
        InterestRequest interest = new InterestRequest("dota", "pro", 1);

//      when & then
        mockMvc.perform(post("/api/user/{userId}/interest", 1001)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(interest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interestId").exists())
                .andExpect(jsonPath("$.game", is("dota")))
                .andExpect(jsonPath("$.level", is("pro")))
                .andExpect(jsonPath("$.credit", is(1)));
    }

    //  updateUserInterestByInterestId
    @Test
    @Sql({"/h2_integration_test.sql"})
    public void givenInterestAndInterestId_whenUpdateUserInterestByInterestId_thenReturnInterest() throws Exception {


        InterestRequest updateInterest = new InterestRequest("among us", "pro", 1);

//      when & then
        mockMvc.perform(put("/api/user/{userId}/interest/{interestId}",
                1001, 1001)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateInterest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        Interest interest = interestRepository.findById(1001L).get();
        assertThat(interest.getGame()).isEqualTo(updateInterest.getGame());
        assertThat(interest.getLevel()).isEqualTo(updateInterest.getLevel());
        assertThat(interest.getCredit()).isEqualTo(updateInterest.getCredit());
    }

    //  updateUserInterestByInterestId
    @Test
    @Sql({"/h2_integration_test.sql"})
    public void givenInterestWithSameGame_whenUpdateUserInterestByInterestId_thenReturnInterest() throws Exception {


        InterestRequest updateInterest = new InterestRequest("call of duty", "pro", 1);

//      when & then
        mockMvc.perform(put("/api/user/{userId}/interest/{interestId}",
                1001, 1001)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateInterest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User already has interest with game: " + updateInterest.getGame()));
    }

    //  deleteUserInterestByInterestId
    @Test
    @Sql({"/h2_integration_test.sql"})
    public void givenInterestId_whenDeleteUserInterestByInterestId_thenSuccess() throws Exception {

//      when & then
        mockMvc.perform(delete("/api/user/{userId}/interest/{interestId}",
                1001, 1001)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        assertThat(this.interestRepository.findById(1001L).isPresent()).isFalse();
    }

    //  updateUserInterestCreditByInterestId
    @Test
    @Sql({"/h2_integration_test.sql"})
    public void givenInterestCreditAndInterestId_whenUpdateUserInterestCreditByInterestId_thenSucess() throws Exception {

//      when & then
        mockMvc.perform(put("/api/user/{userId}/interest/{interestId}/credit",
                1001, 1001)
                .contentType(MediaType.APPLICATION_JSON)
                .param("credit", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
        assertThat(this.interestRepository.findById(1001L).get().getCredit()).isEqualTo(1000);


    }


    //  Helper method
    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
