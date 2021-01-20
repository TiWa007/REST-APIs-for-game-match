package com.tiwa007.gamematchrestapi.controller;


import com.tiwa007.gamematchrestapi.entity.Interest;
import com.tiwa007.gamematchrestapi.entity.User;
import com.tiwa007.gamematchrestapi.exception.InvalidRequestException;
import com.tiwa007.gamematchrestapi.exception.ResourceNotFoundException;
import com.tiwa007.gamematchrestapi.repository.InterestRepository;
import com.tiwa007.gamematchrestapi.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Api(value = "User")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    @Autowired
    private UserRepository userRepository;

    @Resource
    @Autowired
    private InterestRepository interestRepository;

    private final List<String> GAME_LIST = Arrays.asList("fortnite", "call of duty", "dota", "valhalla", "among us");
    private final List<String> LEVEL_LIST = Arrays.asList("noob", "pro", "invincible");
    private final List<String> GEO_LIST = Arrays.asList("Europe","Asia", "USA");


    // get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> userList = userRepository.findAll();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    // get user by id
    @GetMapping(path = "/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId){
        return new ResponseEntity<>(this.getUserFromUserId(userId), HttpStatus.OK);
    }

    // create user
    @ApiOperation(value = "Create new user",
            notes = "Create new user. Interests of user will also be created if exists. User can only have one interest for one game",
            response = User.class)
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {

//        check whether game more than one
        Set<String> gameSet = new HashSet<>();
        for (Interest interest :  user.getInterestSet()) {
            if (gameSet.contains(interest.getGame()))
                throw new InvalidRequestException("User has different interests with same game");
            gameSet.add(interest.getGame());
        }

//      post user
        User registeredUser = userRepository.save(user);

//      update userId of interest
        if (registeredUser.getInterestSet() != null) {
            Set<Interest> interestSet = registeredUser.getInterestSet();
            for (Interest interest : interestSet) {
                interest.setUser(registeredUser);
                interestRepository.save(interest);
            }
        }
        return new ResponseEntity<>(this.userRepository.findById(registeredUser.getUserId()).get(), HttpStatus.CREATED);
    }

    /**
     * Update user by userId. Interest of user will not be updated.
     * @param user
     * @param userId
     * @return HttpStatus.OK
     */
    @ApiOperation(value = "Update user",
            notes = "Update user by userId. Interest of user will not be updated.")
    @PutMapping("/{userId}")
    public ResponseEntity updateUserById(@Valid @RequestBody User user, @PathVariable Long userId) {
        User existingUser = this.getUserFromUserId(userId);
//      update user
        existingUser.setGender(user.getGender());
        existingUser.setGeography(user.getGeography());
        existingUser.setName(user.getName());
        existingUser.setNickname(user.getNickname());
        userRepository.save(existingUser);

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
        this.getUserById(userId);
        interestRepository.deleteInterestsByUserId(userId);
        userRepository.deleteById(userId);
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
        this.checkGameAndLevelAndGeography(game, level, geography);
        List<User> matchUserList = userRepository.findMatchUserByGameAndLevelAndGeography(game, level, geography);
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

        User user = this.getUserFromUserId(userId);
        Interest userMatchInterest = this.getInterestFromInterestId(interestId, userId);
        List<User> matchUserList = userRepository.findMatchUserByGameAndLevelAndGeography(userMatchInterest.getGame(),
                userMatchInterest.getLevel(), user.getGeography());

        matchUserList.remove(user);

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

        this.checkGameAndLevelAndGeography(game, level, null);

        List<User> userList = this.userRepository.findUserWithMaxCreditByGameAndLevel(game, level);

        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    //------------------- Interest --------------------------

    // get interest by interestId
    @GetMapping(path = "/{userId}/interest/{interestId}")
    public ResponseEntity<Interest> getInterestByInterestId(@PathVariable Long userId,
                                                @PathVariable Long interestId){
        Interest interest =  this.getInterestFromInterestId(userId, interestId);
        return new ResponseEntity<>(interest, HttpStatus.OK);
    }

    @PostMapping(path = "/{userId}/interest")
    public ResponseEntity<Interest> createUserInterest(@Valid @RequestBody Interest interest,
                                                   @PathVariable Long userId){
        User user =  this.getUserFromUserId(userId);
        List<Interest> interestList = this.interestRepository.findInterestByUserAndGame(user, interest.getGame());
//        post interest
        if (interest.getInterestId() == null && interestList.size() > 0)
            throw new InvalidRequestException("User already has interest with game: " + interest.getGame());

        interest.setUser(user);
        Interest resInterest = interestRepository.save(interest);
        return new ResponseEntity<>(resInterest, HttpStatus.OK);
    }

    @PutMapping(path = "/{userId}/interest/{interestId}")
    public ResponseEntity updateUserInterestByInterestId(@Valid @RequestBody Interest interest,
                                                   @PathVariable Long userId,
                                                   @PathVariable Long interestId){
        if (interest.getInterestId() != interestId)
            throw new InvalidRequestException("The interest has a different interestId: " + interest.getInterestId()
            + " from path variable: " + interestId);

        User user =  this.getUserFromUserId(userId);

        Interest existingInterest = this.getInterestFromInterestId(interestId, userId);
        if (existingInterest.getGame() != interest.getGame()) {
            List<Interest> interestList = this.interestRepository.findInterestByUserAndGame(user, interest.getGame());
            if (interestList.size() > 0)
                throw new InvalidRequestException("User already has interest with game: " + interest.getGame());

        }

        existingInterest.setCredit(interest.getCredit());
        existingInterest.setGame(interest.getGame());
        existingInterest.setLevel(interest.getLevel());
        interestRepository.save(existingInterest);

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/interest/{interestId}")
    public ResponseEntity deleteUserInterestByInterestId(@PathVariable Long userId, @PathVariable Long interestId){

        this.getUserFromUserId(userId);
        this.getInterestFromInterestId(interestId, userId);

        interestRepository.deleteById(interestId);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Update user interest credit
     * @param userId
     * @param interestId
     * @param credit
     */
    @ApiOperation(value = "Update user interest credit")
    @PutMapping(path="/{userId}/interest/{interestId}/credit")
    public ResponseEntity updateUserInterestCreditByInterestId(@PathVariable Long userId,
                                                               @PathVariable Long interestId,
                                                               @ApiParam(
                                                                       name = "credit",
                                                                       type = "String",
                                                                       value = "Credit should be zero or positive integer",
                                                                       example = "1",
                                                                       required = true)
                                                               @RequestParam Integer credit) {

        if (credit < 0)
            throw new InvalidRequestException("Credit should be zero or positive");

        this.getUserFromUserId(userId);
        this.getInterestFromInterestId(interestId, userId);

        this.interestRepository.updateUserInterestCredit(interestId, credit);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Check game, level and geography whether belongs corresponding list
     * @param game
     * @param level
     * @param geography
     * @throws InvalidRequestException
     */
    private void checkGameAndLevelAndGeography(String game, String level, String geography) {
        StringBuilder message = new StringBuilder();
        if (game != null && !this.GAME_LIST.contains(game)) {
            message.append("Invalid ").append("game: ").append(game).append(" and it should be one of ")
                    .append(GAME_LIST.toString());
        }
        if (level != null && !this.LEVEL_LIST.contains(level)) {
            if (message.length() != 0) message.append("; ");
            message.append("Invalid ").append("level: ").append(level).append(" and it should be one of ")
                    .append(LEVEL_LIST.toString());
        }
        if (geography != null && !this.GEO_LIST.contains(geography)) {
            if (message.length() != 0) message.append("; ");
            message.append("Invalid ").append("geography: ").append(geography).append(" and it should be one of ")
                    .append(GEO_LIST.toString());
        }
        if (message.length() > 0)
            throw new InvalidRequestException(message.toString());
    }

    /**
     * Get user by userId and check whether user exists
     * @param userId
     * @return User
     * @throws ResourceNotFoundException
     * if user does not exist with userId
     */
    private User getUserFromUserId(Long userId) {
        User user =  this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User cannot be found with id: " + userId));
        return user;
    }

    /**
     * Get interest by interestId and check whether interest exists and the userId of interest is the same as userId
     * @param interestId
     * @param userId
     * @return Interest
     * @throws ResourceNotFoundException
     * if interest does not exist with interestId
     * @throws InvalidRequestException
     * if user with userId does not have the interest with interestId
     */
    private Interest getInterestFromInterestId(Long interestId, Long userId) {
        Interest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new ResourceNotFoundException("Interest cannot be found with id: " + interestId));
        if (interest.getUser() == null || interest.getUser().getUserId() != userId)
            throw new InvalidRequestException("User with userId: " + userId +
                    " does not have the interest with interestId : " + interestId);
        return interest;
    }
}
