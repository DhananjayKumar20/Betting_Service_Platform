package com.BettingPlatform.Model;

import java.math.BigDecimal;

import com.BettingPlatform.DTO.BetRequestDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bets")
public class Bets extends BaseModel{
	private Long userId;
    private Long gameId;
    private BigDecimal stake;
    private BigDecimal odds;
    @Enumerated(EnumType.ORDINAL)
    private DelieveredStatus delieveredStatus;
    @Enumerated(EnumType.ORDINAL)
    private NotificationType notificationType;
    @Enumerated(EnumType.ORDINAL) 
    private BetStatus betStatus;  // PENDING, WON, LOST, CANCELED
    @Enumerated(EnumType.ORDINAL)
    private Currency currency;
    private String RiskFlagReason;
    
    public static Bets form(BetRequestDTO betRequestDto) {
    	Bets bets = new Bets();
		bets.setUserId(betRequestDto.getUserId());
		bets.setGameId(betRequestDto.getGameId());
		bets.setStake(betRequestDto.getStake());
		bets.setOdds(betRequestDto.getOdds());
		bets.setBetStatus(BetStatus.PENDING);
		bets.setCurrency(Currency.valueOf(betRequestDto.getCurrency()));
		return bets;
    }
    
}
