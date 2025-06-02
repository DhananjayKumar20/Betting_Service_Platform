package com.BettingPlatform.DTO;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AverageBetResponseDTO {
    private Long userId;
    private BigDecimal averageBet;
    
    /*public AverageBetResponseDTO(Long userId, BigDecimal averageBet) {
        this.userId = userId;
        this.averageBet = averageBet;
    }*/

}
