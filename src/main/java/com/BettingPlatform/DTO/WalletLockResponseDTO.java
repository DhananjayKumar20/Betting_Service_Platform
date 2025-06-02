package com.BettingPlatform.DTO;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletLockResponseDTO {
	
	private Long userId;
    private BigDecimal stake;
    private String status; // SUCCESS or FAILED
    private String message;

    
}
