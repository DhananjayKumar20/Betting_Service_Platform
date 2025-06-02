package com.BettingPlatform.DTO;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletLockRequestDTO {


	 private Long userId;
	    private BigDecimal stake;
}
