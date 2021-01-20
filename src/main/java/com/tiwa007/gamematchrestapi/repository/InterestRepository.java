package com.tiwa007.gamematchrestapi.repository;

import com.tiwa007.gamematchrestapi.entity.Interest;
import com.tiwa007.gamematchrestapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {

    List<Interest> findInterestByUserAndGame(User user, String game);

    @Transactional
    @Modifying
    @Query("update Interest i set i.credit = :credit where i.interestId = :interestId")
    void updateUserInterestCredit(@Param("interestId") Long interestId,
                                  @Param("credit") Integer credit);

    @Transactional
    @Modifying
    @Query("delete from Interest interest where interest.user.userId = :id")
    void deleteInterestsByUserId(@Param("id") Long userId);

}
