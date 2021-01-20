package com.tiwa007.gamematchrestapi.Service;

import com.tiwa007.gamematchrestapi.common.exception.InvalidRequestException;
import com.tiwa007.gamematchrestapi.common.exception.ResourceNotFoundException;
import com.tiwa007.gamematchrestapi.entity.Interest;
import com.tiwa007.gamematchrestapi.entity.User;
import com.tiwa007.gamematchrestapi.repository.InterestRepository;
import com.tiwa007.gamematchrestapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class InterestService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterestRepository interestRepository;

    private final List<String> GAME_LIST = Arrays.asList("fortnite", "call of duty", "dota", "valhalla", "among us");
    private final List<String> LEVEL_LIST = Arrays.asList("noob", "pro", "invincible");
    private final List<String> GEO_LIST = Arrays.asList("Europe","Asia", "USA");


    // get interest by interestId
    public Interest getInterestByInterestId(Long userId, Long interestId){
        Interest interest =  this.getInterestFromInterestId(interestId, userId);
        return interest;
    }

    public Interest createUserInterest(Interest interest, Long userId){
        User user =  this.getUserFromUserId(userId);
        List<Interest> interestList = this.interestRepository.findInterestByUserAndGame(user, interest.getGame());
//        post interest
        if (interest.getInterestId() == null && interestList.size() > 0)
            throw new InvalidRequestException("User already has interest with game: " + interest.getGame());

        interest.setUser(user);
        Interest resInterest = interestRepository.save(interest);
        return resInterest;
    }

    public void updateUserInterestByInterestId(Interest interest, Long userId, Long interestId){
        if (interest.getInterestId() != interestId)
            throw new InvalidRequestException("The interest has a different interestId: " + interest.getInterestId()
                    + " from path variable: " + interestId);

        User user =  this.getUserFromUserId(userId);

        Interest existingInterest = this.getInterestFromInterestId(interestId, userId);
        if (existingInterest.getGame() != interest.getGame()) {
            List<Interest> interestList = this.interestRepository.findInterestByUserAndGame(user, interest.getGame());
            if (interestList.size() > 1 ||
                    (interestList.size() == 1 && interestList.get(0).getInterestId().longValue() != interestId.longValue()))
                throw new InvalidRequestException("User already has interest with game: " + interest.getGame());
        }

        existingInterest.setCredit(interest.getCredit());
        existingInterest.setGame(interest.getGame());
        existingInterest.setLevel(interest.getLevel());
        interestRepository.save(existingInterest);
    }

    public void deleteUserInterestByInterestId(Long userId, Long interestId){

        this.getUserFromUserId(userId);
        this.getInterestFromInterestId(interestId, userId);

        interestRepository.deleteById(interestId);
    }

    /**
     * Update user interest credit
     * @param userId
     * @param interestId
     * @param credit
     */
    public void updateUserInterestCreditByInterestId(Long userId, Long interestId, Integer credit) {

        if (credit < 0)
            throw new InvalidRequestException("Credit should be zero or positive");

        this.getUserFromUserId(userId);
        this.getInterestFromInterestId(interestId, userId);

        this.interestRepository.updateUserInterestCredit(interestId, credit);
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
        if (interest.getUser() == null || interest.getUser().getUserId().longValue() != userId.longValue())
            throw new InvalidRequestException("User with userId: " + userId +
                    " does not have the interest with interestId : " + interestId);
        return interest;
    }
}
