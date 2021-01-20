package com.tiwa007.gamematchrestapi.controller;


import com.tiwa007.gamematchrestapi.service.UserService;
import com.tiwa007.gamematchrestapi.entity.Interest;
import com.tiwa007.gamematchrestapi.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Api(value = "User")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> userList = userService.getAllUsers();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    // get user by id
    @GetMapping(path = "/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId){
        return new ResponseEntity<>(this.userService.getUserById(userId), HttpStatus.OK);
    }

    // create user
    @ApiOperation(value = "Create new user",
            notes = "Create new user. Interests of user will also be created if exists. " +
                    "In [Request Body], [name] and [nickname] cannot be empty and should only consist of letter and number with more than 2 characters. " +
                    "[gender] cannot be empty and should be 'male' or 'female'. " +
                    "[geography] cannot be empty and should be one of 'Europe', 'Asia', 'USA'. " +
                    "[interestSet] can be empty. For attributes of interest, " +
                    "[game] cannot be empty should be one of 'fortnite', 'call of duty', 'dota', 'valhalla', 'among us'. " +
                    "[level] cannot be empty and should be one of 'noob', 'pro', 'invincible'. " +
                    "[credit] can be left empy but cannot be negative. " +
                    "User can only have one interest for one game",
            response = User.class)
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserRequest userRequest) {
        User user = this.createUserFromUserRequest(userRequest);
        User createdUser = this.userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Update user by userId. Interest of user will not be updated.
     * @param userRequestWithoutInterest
     * @param userId
     * @return HttpStatus.OK
     */
    @ApiOperation(value = "Update user",
            notes = "Update only user information by userId. Interest of user should not contain. " +
                    "In [Request Body], [name] and [nickname] cannot be empty and should only consist of letter and number with more than 2 characters. " +
                    "[gender] cannot be empty and should be 'male' or 'female'. " +
                    "[geography] cannot be empty and should be one of 'Europe', 'Asia', 'USA'. " +
                    "User can only have one interest for one game")
    @PutMapping("/{userId}")
    public ResponseEntity updateUserById(@Valid @RequestBody UserRequestWithoutInterest userRequestWithoutInterest
            , @PathVariable Long userId) {

        User user = new User(userRequestWithoutInterest.getName(), userRequestWithoutInterest.getGender(),
                userRequestWithoutInterest.getNickname(), userRequestWithoutInterest.getGeography());

        this.userService.updateUserById(user, userId);

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Delete user and interest of the user by userId
     * @param userId
     * @return HttpStatus.OK
     */
    @ApiOperation(value = "Delete user and interest of the user by userId.")
    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUserById(@PathVariable Long userId){
        this.userService.deleteUserById(userId);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Get a list of matched users with same game, level and geography
     * @param game
     * @param level
     * @param geography,
     * @return List<User>
     */
    @ApiOperation(value = "Get a list of matched users with same game, level and geography", response = List.class)
    @GetMapping(path = "/match")
    public ResponseEntity<List<User>> getMatchUserByGameAndLevelAndGeography(
            @ApiParam(
                    name = "game",
                    type = "String",
                    value = "Game should be one of 'fortnite', 'call of duty', 'dota', 'valhalla', 'among us'",
                    example = "fortnite",
                    required = true)
            @RequestParam String game,
            @ApiParam(
                    name = "level",
                    type = "String",
                    value = "Level should be one of 'noob', 'pro', 'invincible'",
                    example = "noob",
                    required = true)
            @RequestParam String level,
            @ApiParam(
                    name = "geography",
                    type = "String",
                    value = "Geography should be one of 'Europe', 'Asia', 'USA'",
                    example = "Europe",
                    required = true)
            @RequestParam String geography){
        List<User> matchUserList = this.userService.getMatchUserByGameAndLevelAndGeography(game, level, geography);
        return new ResponseEntity<>(matchUserList, HttpStatus.OK);
    }

    /**
     * Get a list of other users that matches user geography and user interest game and level
     * @param userId
     * @param interestId
     * @return list of match users
     */
    @ApiOperation(value = "Get a list of other users that matches user geography and user interest game and level", response = List.class)
    @GetMapping(path = "/{userId}/match/{interestId}")
    public ResponseEntity<List<User>> getOtherUserMatchUserInterest(
            @PathVariable Long userId,
            @PathVariable Long interestId){

        List<User> matchUserList = this.userService.getOtherUserMatchUserInterest(userId, interestId);

        return new ResponseEntity<>(matchUserList, HttpStatus.OK);
    }

    /**
     * Get list of users with maximum credit among users with same game and level
     * @return list of users
     */
    @ApiOperation(value = "Get a list of users with maximum credit among users with same game and level", response = List.class)
    @GetMapping(path = "/interest/credit/max")
    public ResponseEntity<List<User>> getUserWithMaxCreditByGameAndLevel(
            @ApiParam(
                    name = "game",
                    type = "String",
                    value = "Game should be one of 'fortnite', 'call of duty', 'dota', 'valhalla', 'among us'",
                    example = "fortnite",
                    required = true)
            @RequestParam String game,
            @ApiParam(
                    name = "level",
                    type = "String",
                    value = "Level should be one of 'noob', 'pro', 'invincible'",
                    example = "noob",
                    required = true)
            @RequestParam String level){

        List<User> userList = this.userService.getUserWithMaxCreditByGameAndLevel(game, level);

        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

//    Helper methods

    private User createUserFromUserRequest(UserRequest userRequest) {
        User user = new User(userRequest.getName(), userRequest.getGender(), userRequest.getNickname(), userRequest.getGeography());
        Set<Interest> interestSet = new HashSet<>();
        for (InterestRequest interestRequest : userRequest.getInterestSet()) {
            Interest interest = new Interest(interestRequest.getGame(), interestRequest.getLevel(), interestRequest.getCredit(), null);
            interestSet.add(interest);
        }
        user.setInterestSet(interestSet);
        return user;
    }
}
