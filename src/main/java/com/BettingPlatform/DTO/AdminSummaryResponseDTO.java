package com.BettingPlatform.DTO;

//import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminSummaryResponseDTO {
	 //  private Long userId;
	   private Long totalBets;
	  // private BigDecimal totalWinAmount;
	  // private BigDecimal totalLossAmount;
	   //private String HighestPlayedGame;
	   private Double totalAmount;
	   private Long mostPopularGame;
	   private Long highestWinningUser;


}
