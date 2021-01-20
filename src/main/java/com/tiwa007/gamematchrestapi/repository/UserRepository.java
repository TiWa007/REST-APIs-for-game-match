package com.tiwa007.gamematchrestapi.repository;

import com.tiwa007.gamematchrestapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    List<User> findUserByNickname(String nickname);

    @Query("SELECT user FROM Interest interest WHERE interest.game = :game AND interest.level = :level " +
            "AND interest.user.geography = :geography")
    List<User> findMatchUserByGameAndLevelAndGeography(@Param("game") String game,
                                                       @Param("level") String level,
                                                       @Param("geography") String geography);

    @Query("SELECT interest.user FROM Interest interest WHERE interest.game = :game AND interest.level = :level " +
            "AND interest.credit = " +
            "(SELECT MAX(interest1.credit) FROM Interest interest1 " +
            "WHERE interest1.game = :game AND interest1.level = :level)")
    List<User> findUserWithMaxCreditByGameAndLevel(@Param("game") String game,
                                                   @Param("level") String level);

}
