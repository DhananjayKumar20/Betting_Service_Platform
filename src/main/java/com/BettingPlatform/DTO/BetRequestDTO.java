package com.BettingPlatform.DTO;

import java.math.BigDecimal;

import com.BettingPlatform.Model.Currency;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BetRequestDTO 
{
	 private Long userId;
	 private Long gameId;
	 private String BetType;
	 private String GameSubType;
	 private BigDecimal stake;
	 private BigDecimal odds;
	 private String notificationType;
	 private String currency;
	 private BigDecimal averageBet;
}
