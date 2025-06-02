package com.BettingPlatform.DTO;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletCreaditResponseDTO {
	
	 private Long userId;
	 private Long betId;
	 private String betStatus;
	 private String status;
	 private BigDecimal winningAmount;
	 private BigDecimal refundAmount;
}
