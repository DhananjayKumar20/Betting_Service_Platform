package com.BettingPlatform.DTO;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BetRequestEvent {
	 private Long userId;
	 private Long gameId;
	 private Long betId;
	 private String BetType;
	 private String GameSubType;
	 private BigDecimal stake;
	 private BigDecimal odds;
	 private String notificationType;
	 private String currency;
	 private String betStatus;
	 private BigDecimal averageBet;
	 private String Reason;
}
