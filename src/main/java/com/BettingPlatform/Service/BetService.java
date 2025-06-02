
package com.BettingPlatform.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.naming.InsufficientResourcesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.BettingPlatform.DTO.AverageBetResponseDTO;
import com.BettingPlatform.DTO.AdminSummaryResponseDTO;
import com.BettingPlatform.DTO.BetRequestDTO;
import com.BettingPlatform.DTO.BetRequestEvent;
import com.BettingPlatform.DTO.BetResponceDto;
import com.BettingPlatform.DTO.GameResponseDTO;
import com.BettingPlatform.DTO.GameResultResponseDTO;
import com.BettingPlatform.DTO.GameTotalBetsResponseDTO;
import com.BettingPlatform.DTO.NotificationResponseDTO;
import com.BettingPlatform.DTO.UserSummaryResponseDTO;
import com.BettingPlatform.DTO.UserTotalBetsResponseDTO;
import com.BettingPlatform.DTO.WalletCreaditResponseDTO;
import com.BettingPlatform.DTO.WalletCreditRequestDTO;
import com.BettingPlatform.DTO.WalletLockResponseDTO;
import com.BettingPlatform.Model.BetStatus;
import com.BettingPlatform.Model.Bets;
import com.BettingPlatform.Model.DelieveredStatus;
import com.BettingPlatform.Service.ThirdPartyAPI.ThirdPartyServiceAPI;
import com.BettingPlatform.exception.NotFoundException;
import com.BettingPlatform.repository.BetRepository;

@Service
public class BetService {
	private final ThirdPartyServiceAPI thirdPartyServiceAPI;
	
	private final BetRepository betRepository;
	@Autowired
    private KafkaTemplate<String, BetRequestEvent> kafkaTemplate;
	
	//Constructor Injection
	@Autowired
	public BetService(ThirdPartyServiceAPI thirdPartyServiceAPI, BetRepository betRepository){
	    this.thirdPartyServiceAPI=thirdPartyServiceAPI;
	    this.betRepository=betRepository;
	}	
	public BetResponceDto placeBet(BetRequestDTO betRequestDto) throws InsufficientResourcesException,NotFoundException {
		
		//call wallet service to lock the bet amount
				WalletLockResponseDTO response=thirdPartyServiceAPI.lockAmount(betRequestDto);
				
				GameResponseDTO gameResponseDTO=null;
				if(response.getStatus().equals("LOCKED")) {
					//call GameService to generate game UrL
					 gameResponseDTO=thirdPartyServiceAPI.getGameUrl(betRequestDto);
				}
					     // Save the bet in the Database
				    Bets saveBet = betRepository.save(Bets.form(betRequestDto));
					       //call notification service to get nifty user via email
				    NotificationResponseDTO notificationResponseDTO=
				    		thirdPartyServiceAPI.NotifyUser(betRequestDto);
				  
				          //return the response DTO for fronted user
				    BetResponceDto betResponceDto = BetResponceDto.from(saveBet);	
					// Set gameUrl only in response for fronted user, But not set in DataBase
				    if(gameResponseDTO!=null) {
				    	betResponceDto.setGameUrl(gameResponseDTO.getGameUrl());
				    }
					betResponceDto.setMessage("Bet placed successfully.");	
		
		Long userId = betRequestDto.getUserId();
		 // Calculate average bet using existing method
		AverageBetResponseDTO responses = calculateAverageBetByUserId(userId);
	    System.out.println("Average Bet for userId " + userId + ": " + responses.getAverageBet());

		Bets saveBets = betRepository.save(Bets.form(betRequestDto));
		
		// producer request
		BetRequestEvent event = new BetRequestEvent();
		event.setUserId(betRequestDto.getUserId());
		event.setGameId(betRequestDto.getGameId());
		event.setBetType(betRequestDto.getBetType());
		event.setGameSubType(betRequestDto.getGameSubType());
		event.setStake(betRequestDto.getStake());
		event.setOdds(betRequestDto.getOdds());
		event.setNotificationType(betRequestDto.getNotificationType());
		event.setCurrency(betRequestDto.getCurrency());
		event.setAverageBet(responses.getAverageBet());
		 // Send event to Kafka
		kafkaTemplate.send("betplace-request", event);
		betResponceDto.setBetStatus("PLACED");
		
		return betResponceDto;		
	}	
	     // create new method for consumer  
		
		@KafkaListener(topics = "betplace-request", groupId = "bet-service-group")
		public void handleBetPlaced(BetRequestEvent event) {
	        System.out.println("Consumed event: " + event);
		
	        
	        Bets bet = betRepository.findById(event.getBetId()).get();
	        if (bet == null) {
	            throw new RuntimeException("Bet not found");
	        }
	            // Update bet status to 'FLAGGED' and add risk flag reason
	            bet.setBetStatus(BetStatus.FLAGGED); 	            
	            bet.setRiskFlagReason(event.getReason());
	            betRepository.save(bet);

	            System.out.println("Bet " + event.getBetId() + " flagged due to risk: " + event.getReason());
	        }     				
	

//<--------------------------get gameStatus----------------------------->	
	public BetResponceDto GameStatus(BetRequestDTO betRequestDTO) throws InsufficientResourcesException,NotFoundException{
		
		//call the GameService to getGamesStatus
		GameResultResponseDTO response = thirdPartyServiceAPI.getGamesStatus(betRequestDTO);
		WalletCreaditResponseDTO walletCreaditResponseDTO;
		WalletCreditRequestDTO walletCreditRequestDTO;	
		NotificationResponseDTO notificationResponseDTO;
    	BetRequestDTO betRequestDto = new BetRequestDTO();
    	
		// GameStatus WIn/Lose/Draw
	    if (response.getGameStatus().equalsIgnoreCase("WIN")) {
	    	
	    	Optional<Bets> bet = betRepository.findById(betRequestDTO.getUserId());
	  	    if (bet== null)
	  	    {
	  	        throw new NotFoundException("bet not found with ID: " + betRequestDTO.getUserId());
	  	    }	  	    
	  		BigDecimal winningAmount = bet.get().getStake().multiply(bet.get().getOdds());
	  		
	  	   walletCreditRequestDTO=  WalletCreditRequestDTO.from(bet, response.getGameStatus(), winningAmount);	 
	  	    
	    	      // Call the wallet service to release the winning amount
	    	walletCreaditResponseDTO=thirdPartyServiceAPI.creditWinningsAmount(walletCreditRequestDTO);
	    	       //check UNLOCKED if not-->give exception
	    	if (walletCreaditResponseDTO.getStatus().equals("UNLOCKED")) {
	    		   // Proceed with the next steps	    		
	    		betRequestDto.setUserId(bet.get().getUserId());
	    		betRequestDto.setNotificationType(bet.get().getNotificationType().name());
		    	
		    	   //call notification service to get nifty user via email
				notificationResponseDTO=thirdPartyServiceAPI.NotifyUser(betRequestDto);
				   //notificationResponseDTO.setDelieveredStatus(bet.get().getDelieveredStatus());
	    	
	    	} else {
	    	    throw new InsufficientResourcesException("Failed to unlock funds. Please try again.");
	    	}	    				
	    	 Bets UpdatSaveBets = betRepository.save(Bets.form(betRequestDto));
	    
	    	 UpdatSaveBets.setDelieveredStatus(DelieveredStatus.valueOf(notificationResponseDTO.getDelieveredStatus()));
	    	 UpdatSaveBets.setBetStatus(BetStatus.WIN);
		         //Bets UpdatSaveBets = betRepository.save(bet);     // Save to database
		    		         
	        BetResponceDto betResponceDto = BetResponceDto.from(UpdatSaveBets);	     
	        betResponceDto.setMessage("Congratulations! You've won. Winnings have been credited to your wallet.");

	        return betResponceDto;
		    
	    } else if (response.getGameStatus().equalsIgnoreCase("LOSS")) {
	    	
	    	Optional<Bets> bet = betRepository.findById(betRequestDTO.getUserId());
	  	    if (bet== null)
	  	    {
	  	        throw new NotFoundException("Bet not found with ID: " + betRequestDTO.getUserId());
	  	    }	
	  	    
	        walletCreditRequestDTO=  WalletCreditRequestDTO.from(bet, response.getGameStatus(), null);	 
	  	    
	               // Call the wallet service to release the winning amount
	    	walletCreaditResponseDTO=thirdPartyServiceAPI.creditWinningsAmount(walletCreditRequestDTO);
	        
                   //check UNLOCKED if not-->give exception	    	
	    	if (walletCreaditResponseDTO.getStatus().equals("UNLOCKED")) {
	    	       // Proceed with the next steps
	    		betRequestDto.setUserId(bet.get().getUserId());
	    		betRequestDto.setNotificationType(bet.get().getNotificationType().name());
		    	
		    	  //call notification service to get nifty user via email
				notificationResponseDTO=thirdPartyServiceAPI.NotifyUser(betRequestDto);    	
	    	} else {
	    	    throw new InsufficientResourcesException("Failed to unlock funds. Please try again.");
	    	}	    	
	    	Bets UpdatSaveBets = betRepository.save(Bets.form(betRequestDto));
	    	UpdatSaveBets.setDelieveredStatus(DelieveredStatus.valueOf(notificationResponseDTO.getDelieveredStatus()));
			UpdatSaveBets.setBetStatus(BetStatus.LOSS);
	
		    BetResponceDto betResponceDto = BetResponceDto.from(UpdatSaveBets);   
		    betResponceDto.setMessage("Better luck next time.");
	        return betResponceDto;
	    	
	    } else if (response.getGameStatus().equalsIgnoreCase("DRAW")) {
	    	
	    	Optional<Bets> bet = betRepository.findById(betRequestDTO.getUserId());
	  	    if (bet== null)
	  	    {
	  	        throw new NotFoundException("Bet not found with ID: " + betRequestDTO.getUserId());
	  	    }	  
	  	    walletCreditRequestDTO=  WalletCreditRequestDTO.from(bet, response.getGameStatus(), null);	 
	
	        // Call the wallet service to release the winning amount
	    	walletCreaditResponseDTO=thirdPartyServiceAPI.creditWinningsAmount(walletCreditRequestDTO);
       
             //check UNLOCKED if not-->give exception	    	
	    	if (walletCreaditResponseDTO.getStatus().equals("UNLOCKED")) {
	    	      // Proceed with the next steps
	    		betRequestDto.setUserId(bet.get().getUserId());
	    		betRequestDto.setNotificationType(bet.get().getNotificationType().name());
		           //call notification service to get nifty user via email
				notificationResponseDTO=thirdPartyServiceAPI.NotifyUser(betRequestDto);		    
	    	} else {
	    	    throw new InsufficientResourcesException("Failed to unlock funds. Please try again.");
	    	}    	
	    	Bets UpdatSaveBets = betRepository.save(Bets.form(betRequestDto));
	    	UpdatSaveBets.setDelieveredStatus(DelieveredStatus.valueOf(notificationResponseDTO.getDelieveredStatus()));
	    	UpdatSaveBets.setBetStatus(BetStatus.DRAW);		        
	    	
		    BetResponceDto betResponceDto = BetResponceDto.from(UpdatSaveBets);    
		    betResponceDto.setMessage("Game was a draw. Stake refunded.");
		    return betResponceDto;	    	
	    } else {
	        throw new IllegalArgumentException("Unknown game status: " +response );
	    }	    
	}
//<------------------------get all Admin summary----------------------->	
	public AdminSummaryResponseDTO getAdminAnalytics() {
        //total bet count
		AdminSummaryResponseDTO responseDTO = new AdminSummaryResponseDTO();        
				
		long totalBets = betRepository.countTotalBets();
		System.out.println("Total Bets: " + totalBets);
		responseDTO.setTotalBets(totalBets);		
		
	     Double totalAmount = betRepository.sumTotalBetAmount();
	     System.out.println("Total Amount: " + totalAmount);
	     responseDTO.setTotalAmount(totalAmount);
		
		 List<Long> mostPopularGames = betRepository.findMostPopularGame();
		 Long mostPopularGame = mostPopularGames.isEmpty() ? null : mostPopularGames.get(0);
		 System.out.println("Most Popular Game: " + mostPopularGame);
		 responseDTO.setMostPopularGame(mostPopularGame);

		 List<Long> highestWinningUsers =  betRepository.findHighestWinningUser();
		 Long highestWinningUser = highestWinningUsers.isEmpty() ? null : highestWinningUsers.get(0);
		 System.out.println("Highest Winning User: " + highestWinningUser);
		 responseDTO.setHighestWinningUser(highestWinningUser);
		 return responseDTO;
	}
//<------------------------get all user summary------------------------->	
	public UserSummaryResponseDTO getUserAnalytics() {
		// TODO Auto-generated method stub
		UserSummaryResponseDTO responseDTO = new UserSummaryResponseDTO();
		long totalBets = betRepository.countTotalBets();
		System.out.println("Total Bets: " + totalBets);
		responseDTO.setTotalBets(totalBets);				
		
		 BigDecimal winAmount = betRepository.getTotalWinAmount();
		 System.out.println("TotalWinAmount: " + winAmount);
		 responseDTO.setTotalWinAmount(winAmount);
		 
		 BigDecimal lossAmount = betRepository.getTotalLossAmount();
		 System.out.println("TotalLossAmount: " + lossAmount);
		 responseDTO.setTotalLossAmount(lossAmount);	
		 
		 List<Long> mostPlayGame = betRepository.findMostPlayGame();
		 Long mostPlayedGame = mostPlayGame.isEmpty() ? null : mostPlayGame.get(0);
		 System.out.println("Most Played Game ID: " + mostPlayGame);
		 responseDTO.setMostPlayGame(mostPlayedGame);
		 return responseDTO;
	}
//<------------------------all list bets Placed by user--------------------->
	public List<UserTotalBetsResponseDTO> getAllTotalBetsByUserId(Long userId) {
		    // Fetch all bets for the user
			List<UserTotalBetsResponseDTO> responseList = new ArrayList<>();
			List<Bets> betList = betRepository.findAllBetsByUserId(userId);
		    System.out.println("Bet List for userId: " + userId + " - " + betList);		
		    if (betList != null && !betList.isEmpty()) {
		        for (Bets bet : betList) {
		            UserTotalBetsResponseDTO response = new UserTotalBetsResponseDTO();
		            response.setBetId(bet.getId());
		            response.setUserId(bet.getUserId());
		            response.setGameId(bet.getGameId());
		            response.setStake(bet.getStake());
		            response.setOdds(bet.getOdds());
		            if (bet.getBetStatus() != null) {
		                response.setBetStatus(bet.getBetStatus().name());
		            } else {
		                response.setBetStatus("UNKNOWN"); // Or null, or skip setting it
		            }
		            responseList.add(response);
		        }
		    } else {
		        // If no bets are found, you can return an empty list or a default response
		        System.out.println("No bets found for userId: " + userId);
		    }
		    return responseList;		
		}
//<------------------------all list bets Placed on specific game-------------------->
	public List<GameTotalBetsResponseDTO> getAllBetsByGameId(Long gameId) {
		// TODO Auto-generated method stub
		List<GameTotalBetsResponseDTO> responseList = new ArrayList<>();
	    List<Bets> betList = betRepository.findAllBetsByGameId(gameId);
	    System.out.println("Bet List for gameId: " + gameId + " - " + betList);
	    if (betList != null && !betList.isEmpty()) {
	        for (Bets bet : betList) {
	        	GameTotalBetsResponseDTO response = new GameTotalBetsResponseDTO();
	            response.setBetId(bet.getId());
	            response.setUserId(bet.getUserId());
	            response.setGameId(bet.getGameId());
	            response.setStake(bet.getStake());
	            response.setOdds(bet.getOdds());
	            if (bet.getBetStatus() != null) {
	                response.setBetStatus(bet.getBetStatus().name());
	            } else {
	                response.setBetStatus("UNKNOWN");
	            }
	            responseList.add(response);
	        }
	    } else {
	        System.out.println("No bets found for gameId: " + gameId);
	    }
		return responseList;	    
	}
	
	public AverageBetResponseDTO calculateAverageBetByUserId(Long userId) {
	    List<Bets> betList = betRepository.findAllBetsByUserId(userId);
	    System.out.println("Fetched Bet List for userId " + userId + ": " + betList);

	    BigDecimal totalStake = BigDecimal.ZERO;
	    BigDecimal average = BigDecimal.ZERO;

	    if (betList != null && !betList.isEmpty()) {
	        for (Bets bet : betList) {
	            BigDecimal stake = bet.getStake(); // assuming getStake() returns BigDecimal
	            if (stake != null) {
	                totalStake = totalStake.add(stake);
	            } else {
	                // Handle the null case appropriately, e.g., log or set to zero
	                totalStake = totalStake.add(BigDecimal.ZERO);
	            }
	        }
	        int numberOfBets = betList.size();
	        average = totalStake.divide(BigDecimal.valueOf(numberOfBets), 2, RoundingMode.HALF_UP);
	        //System.out.println(average);
	    }  
	    	    AverageBetResponseDTO response = new AverageBetResponseDTO();
	    	    response.setUserId(userId);
	    	    response.setAverageBet(average.setScale(2, RoundingMode.HALF_UP));
	    	    return response;	   
	}
}

	




	



