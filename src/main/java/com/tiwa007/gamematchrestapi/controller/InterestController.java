package com.tiwa007.gamematchrestapi.controller;

import com.tiwa007.gamematchrestapi.service.InterestService;
import com.tiwa007.gamematchrestapi.entity.Interest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "Interest")
@RestController
@RequestMapping("/api/user")
public class InterestController {

    @Autowired
    private InterestService interestService;

    // get interest by interestId
    @GetMapping(path = "/{userId}/interest/{interestId}")
    public ResponseEntity<Interest> getInterestByInterestId(@PathVariable Long userId,
                                                            @PathVariable Long interestId){

        Interest interest =  this.interestService.getInterestByInterestId(userId, interestId);

        return new ResponseEntity<>(interest, HttpStatus.OK);
    }

    @ApiOperation(value = "Create new interest",
            notes = "Create new interest. " +
                    "In [Request Body], [game] cannot be empty should be one of 'fortnite', 'call of duty', 'dota', 'valhalla', 'among us'. " +
                    "[level] cannot be empty and should be one of 'noob', 'pro', 'invincible'. " +
                    "[credit] can be left empty but cannot be negative. " +
                    "User can only have one interest for one game",
            response = Interest.class)
    @PostMapping(path = "/{userId}/interest")
    public ResponseEntity<Interest> createUserInterest(@Valid @RequestBody InterestRequest interestRequest,
                                                       @PathVariable Long userId){
        Interest interest = new Interest(interestRequest.getGame(), interestRequest.getLevel(), interestRequest.getCredit(), null);
        Interest resInterest = this.interestService.createUserInterest(interest, userId);
        return new ResponseEntity<>(resInterest, HttpStatus.OK);
    }

    @ApiOperation(value = "Update interest",
            notes = "Update interest. " +
                    "In [Request Body], [game] cannot be empty should be one of 'fortnite', 'call of duty', 'dota', 'valhalla', 'among us'. " +
                    "[level] cannot be empty and should be one of 'noob', 'pro', 'invincible'. " +
                    "[credit] can be left empty but cannot be negative. " +
                    "User can only have one interest for one game",
            response = Interest.class)
    @PutMapping(path = "/{userId}/interest/{interestId}")
    public ResponseEntity updateUserInterestByInterestId(@Valid @RequestBody InterestRequest interestRequest,
                                                         @PathVariable Long userId,
                                                         @PathVariable Long interestId){

        Interest interest = new Interest(interestRequest.getGame(), interestRequest.getLevel(), interestRequest.getCredit(), null);
        interest.setInterestId(interestId);

        this.interestService.updateUserInterestByInterestId(interest, userId, interestId);

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/interest/{interestId}")
    public ResponseEntity deleteUserInterestByInterestId(@PathVariable Long userId, @PathVariable Long interestId){

        this.interestService.deleteUserInterestByInterestId(userId, interestId);

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

        this.interestService.updateUserInterestCreditByInterestId(userId, interestId, credit);

        return new ResponseEntity(HttpStatus.OK);
    }
}
