package com.tiwa007.gamematchrestapi.Service;

import com.tiwa007.gamematchrestapi.entity.Interest;
import com.tiwa007.gamematchrestapi.entity.User;
import com.tiwa007.gamematchrestapi.common.exception.InvalidRequestException;
import com.tiwa007.gamematchrestapi.common.exception.ResourceNotFoundException;
import com.tiwa007.gamematchrestapi.repository.InterestRepository;
import com.tiwa007.gamematchrestapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterestRepository interestRepository;

    private final List<String> GAME_LIST = Arrays.asList("fortnite", "call of duty", "dota", "valhalla", "among us");
    private final List<String> LEVEL_LIST = Arrays.asList("noob", "pro", "invincible");
    private final List<String> GEO_LIST = Arrays.asList("Europe","Asia", "USA");


    // get all users
    public List<User> getAllUsers(){
        List<User> userList = userRepository.findAll();
        return userList;
    }

    // get user by id
    public User getUserById(Long userId){
        return this.getUserFromUserId(userId);
    }

    // create user
    public User createUser(User user) {

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
        return this.userRepository.findById(registeredUser.getUserId()).get();
    }

    /**
     * Update user by userId. Interest of user will not be updated.
     * @param user
     * @param userId
     * @return User
     */
    public void updateUserById(User user, Long userId) {
        User existingUser = this.getUserFromUserId(userId);
//      update user
        existingUser.setGender(user.getGender());
        existingUser.setGeography(user.getGeography());
        existingUser.setName(user.getName());
        existingUser.setNickname(user.getNickname());
        userRepository.save(existingUser);
    }

    /**
     * Delete user and interest of the user by userId
     * @param userId
     */
    public void deleteUserById(Long userId){
        this.getUserById(userId);
        interestRepository.deleteInterestsByUserId(userId);
        userRepository.deleteById(userId);
    }

    /**
     * Get a list of matched users with same game, level and geography
     * @param game
     * @param level
     * @param geography,
     * @return List<User>
     */
    public List<User> getMatchUserByGameAndLevelAndGeography(String game, String level, String geography){
        this.checkGameAndLevelAndGeography(game, level, geography);
        List<User> matchUserList = userRepository.findMatchUserByGameAndLevelAndGeography(game, level, geography);
        return matchUserList;
    }

    /**
     * Get a list of other users that matches user geography and user interest game and level
     * @param userId
     * @param interestId
     * @return list of match users
     */
    public List<User> getOtherUserMatchUserInterest(Long userId, Long interestId){

        User user = this.getUserFromUserId(userId);
        Interest userMatchInterest = this.getInterestFromInterestId(interestId, userId);
        List<User> matchUserList = userRepository.findMatchUserByGameAndLevelAndGeography(userMatchInterest.getGame(),
                userMatchInterest.getLevel(), user.getGeography());

        matchUserList.remove(user);

        return matchUserList;
    }

    /**
     * Get list of users with maximum credit among users with same game and level
     * @return list of users
     */
    public List<User> getUserWithMaxCreditByGameAndLevel(String game, String level){

        this.checkGameAndLevelAndGeography(game, level, null);

        List<User> userList = this.userRepository.findUserWithMaxCreditByGameAndLevel(game, level);

        return userList;
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
