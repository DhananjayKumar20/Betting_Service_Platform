package com.BettingPlatform.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameResultResponseDTO {
	
	 private Long userId;
	 private Long gameId;
	 private String gameStatus; // WIN or LOSS or Draw
	
	

}
