package com.BettingPlatform.DTO;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSummaryResponseDTO {
	 //private Long userId;
     private Long totalBets;
     private BigDecimal totalWinAmount;
     private BigDecimal totalLossAmount;
     private Long mostPlayGame;

}
