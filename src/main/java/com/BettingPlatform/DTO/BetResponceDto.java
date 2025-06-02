package com.BettingPlatform.DTO;

import java.math.BigDecimal;

import com.BettingPlatform.Model.Bets;
import com.BettingPlatform.Model.Currency;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BetResponceDto {
	
	private Long betId;
	private Long userId;
    private Long gameId;
    private BigDecimal stake;
    private BigDecimal odds;
    private String currency;
    private BigDecimal winningAmount;
    private BigDecimal refundAmount;
	private String betStatus;
	private String gameUrl;
    private String message;
    
    public static BetResponceDto from(Bets bets) {
    	
    	 BetResponceDto betResponceDto = new BetResponceDto();
    	 betResponceDto.setBetId(bets.getId());
    	 betResponceDto.setUserId(bets.getUserId());
    	 betResponceDto.setGameId(bets.getGameId());
    	 betResponceDto.setStake(bets.getStake());
    	 betResponceDto.setOdds(bets.getOdds());
    	 betResponceDto.setCurrency(bets.getCurrency().name());
    	 betResponceDto.setBetStatus(bets.getBetStatus().name());
         return betResponceDto;
     }	
}
