package com.BettingPlatform.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.BettingPlatform.DTO.UserTotalBetsResponseDTO;
import com.BettingPlatform.Model.Bets;

@Repository
public interface BetRepository extends JpaRepository<Bets, Long> {

	 Optional<Bets> findByUserIdAndGameId(Long userId, Long gameId);

	List<Bets> findAllBetsByUserId(Long userId);

	List<Bets>findAllBetsByGameId(Long gameId);
    
    @Query("SELECT COUNT(b) FROM Bets b")
    long countTotalBets();
    
    @Query("SELECT SUM(b.stake) FROM Bets b")

    Double sumTotalBetAmount();
	
	@Query("SELECT b.userId FROM Bets b WHERE b.betStatus = com.BettingPlatform.Model.BetStatus.WIN GROUP BY b.userId ORDER BY SUM(b.stake) DESC")
	List<Long> findHighestWinningUser();
	//@Query("SELECT b.userId FROM Bets b WHERE b.betStatus = com.BettingPlatform.Model.BetStatus.WIN GROUP BY b.userId ORDER BY SUM(b.winAmount) DESC")

	
	@Query("SELECT b.gameId FROM Bets b GROUP BY b.gameId ORDER BY COUNT(b) DESC")
	List<Long> findMostPopularGame();

	@Query("SELECT SUM(b.stake) FROM Bets b WHERE b.betStatus = com.BettingPlatform.Model.BetStatus.WIN")
	BigDecimal getTotalWinAmount();
	
	@Query("SELECT SUM(b.stake) FROM Bets b WHERE b.betStatus = com.BettingPlatform.Model.BetStatus.LOSS")
	BigDecimal getTotalLossAmount();

	@Query("SELECT b.gameId FROM Bets b GROUP BY b.gameId ORDER BY COUNT(b) DESC")
	List<Long> findMostPlayGame();

}

