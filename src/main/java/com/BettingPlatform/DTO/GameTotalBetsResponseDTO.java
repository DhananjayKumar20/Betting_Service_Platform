package com.BettingPlatform.DTO;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameTotalBetsResponseDTO {

	
         private Long betId;
		 private Long userId;
		 private Long gameId;
		 private BigDecimal stake;
		 private BigDecimal odds;
		 private String betStatus;
}
