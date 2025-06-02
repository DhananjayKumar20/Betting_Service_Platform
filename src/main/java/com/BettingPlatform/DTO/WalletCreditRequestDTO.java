package com.BettingPlatform.DTO;

import java.math.BigDecimal;
import java.util.Optional;
import com.BettingPlatform.Model.Bets;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletCreditRequestDTO {
	
	    private Long userId;
	    private Long betId;
	    private String gameStatus;
	    private BigDecimal winningAmount;
	    private String currency;
	    private BigDecimal refundAmount;
	    

	   public static WalletCreditRequestDTO from(Optional<Bets> bet, String gameStatus, BigDecimal winningAmount) {
	   // public static WalletCreditRequestDTO from(Bets bet, BigDecimal winningAmount) {
	        WalletCreditRequestDTO walletCreditRequestDTO = new WalletCreditRequestDTO();
	        walletCreditRequestDTO.setUserId(bet.get().getUserId());
	        walletCreditRequestDTO.setBetId(bet.get().getId());
	        walletCreditRequestDTO.setGameStatus(gameStatus);
	        walletCreditRequestDTO.setWinningAmount(winningAmount);
		    walletCreditRequestDTO.setCurrency(bet.get().getCurrency().name());
	        return walletCreditRequestDTO;
	    }

}
