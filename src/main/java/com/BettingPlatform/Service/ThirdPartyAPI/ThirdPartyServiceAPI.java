package com.BettingPlatform.Service.ThirdPartyAPI;

import javax.naming.InsufficientResourcesException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

//import com.BettingPlatform.DTO.AdminTotalBetsResponseDTO;
import com.BettingPlatform.DTO.BetRequestDTO;
import com.BettingPlatform.DTO.GameResponseDTO;
import com.BettingPlatform.DTO.GameResultResponseDTO;
//import com.BettingPlatform.DTO.NotificationRequestDTO;
import com.BettingPlatform.DTO.NotificationResponseDTO;
//import com.BettingPlatform.DTO.TotalBetsRequestDTO;
import com.BettingPlatform.DTO.WalletCreaditResponseDTO;
import com.BettingPlatform.DTO.WalletCreditRequestDTO;
import com.BettingPlatform.DTO.WalletLockResponseDTO;
//import com.BettingPlatform.exception.AdminServiceException;
import com.BettingPlatform.exception.NotFoundException;
import com.BettingPlatform.exception.NotificationFailureException;
import com.BettingPlatform.exception.WalletOperationException;
 
@Component
public class ThirdPartyServiceAPI{
	
	@Value("${wallet.api.baseurl}")
	private String walletApiBaseUrl;
	

	@Value("${gameservice.api.baseurl}")
	private String gameServiceApiBaseUrl;
	
	//@Value("${walletservice.api.releaseurl}")
	//private String walletserviceApiReleaseUrl;
	
	@Value("${notificationservice.api.baseurl}")
	private String notificationServiceApiBaseUrl;
	

	@Value("${adminservice.api.baseurl}")
	private String adminServiceApiBaseUrl;
	
	private String lockfundUrl="/lock_funds";
	 
    private String checkGameUrl = "/play";     //("/play") provide GameEngineService
	
    private String checkGameStatusUrl = "/check_game_status";
    
    private String  unlockedfundUrl = "/unlocked";
    
    private String getNotifiyUrl = "/Notify";
    
    private final RestTemplateBuilder restTemplateBuilder;
	
	public ThirdPartyServiceAPI(RestTemplateBuilder restTemplateBuilder,
			
	  	@Value("${wallet.api.baseurl}")String walletApiBaseUrl,
	    @Value("${gameservice.api.baseurl}")String gameServiceApiBaseUrl,
		//@Value("${gameservice.api.statusurl}")String gameServiceApiStatusUrl,
		//@Value("${walletservice.api.releaseurl}")String walletserviceApiReleaseUrl,
		@Value("${notificationservice.api.baseurl}")String notificationServiceApiBaseUrl,
		@Value("${adminservice.api.baseurl}")String adminServiceApiBaseUrl){
		
		this.restTemplateBuilder=restTemplateBuilder;
		this.walletApiBaseUrl=walletApiBaseUrl;
		this.gameServiceApiBaseUrl = gameServiceApiBaseUrl;
		//this.walletserviceApiReleaseUrl = walletserviceApiReleaseUrl;
		this.notificationServiceApiBaseUrl = notificationServiceApiBaseUrl;
	}
	    //Call Wallet Service to lock funds for the bet
	
	public WalletLockResponseDTO lockAmount(BetRequestDTO betRequestDTO) throws InsufficientResourcesException{
		RestTemplate restTemplete = restTemplateBuilder.build();
		
		String Url=walletApiBaseUrl+lockfundUrl;
		
		ResponseEntity<WalletLockResponseDTO> response	= restTemplete.postForEntity(
	            Url,betRequestDTO,WalletLockResponseDTO.class);
		
		WalletLockResponseDTO walletResponseDTO=response.getBody();
		
		if(walletResponseDTO==null || !"LOCKED".equals(walletResponseDTO.getStatus())){
			throw new InsufficientResourcesException("Insufficient Balance. Please add funds.");
		}
		return walletResponseDTO;
	}
	 //  Calling Game Engine Service to get game URL based on gameId, betType, and gameSubType
	public GameResponseDTO getGameUrl(BetRequestDTO betRequestDTO)throws NotFoundException {
		RestTemplate restTemplete = restTemplateBuilder.build();

		 String Url = gameServiceApiBaseUrl + checkGameUrl;
		           
	     ResponseEntity<GameResponseDTO> response = restTemplete.postForEntity(Url,betRequestDTO, GameResponseDTO.class);
		
	     GameResponseDTO gameResponseDTO= response.getBody();// Returns game status (example:- "PENDING", "FINISHED") from Game Engine
	     if(gameResponseDTO==null) {
	    	 throw new NotFoundException("Game Url Not Found !");
	     }
	     return gameResponseDTO;
	}
	//call the GameService to getGamesStatus
	 public GameResultResponseDTO getGamesStatus(BetRequestDTO betRequestDTO)throws NotFoundException{

	    RestTemplate restTemplete = restTemplateBuilder.build(); 
	    String Url = gameServiceApiBaseUrl + checkGameStatusUrl;
	    
	    ResponseEntity<GameResultResponseDTO> response = restTemplete.postForEntity(Url,betRequestDTO, GameResultResponseDTO.class);

	    GameResultResponseDTO gameResultResponseDTO= response.getBody();  // Returns game status (example:- "WIN", "LOSS") from Game Engine
	    if (gameResultResponseDTO == null) {
            throw new NotFoundException("Game result not found!");
        }
	    return gameResultResponseDTO;
	}
	// Call the wallet service to release the winning amount
	 public WalletCreaditResponseDTO creditWinningsAmount(WalletCreditRequestDTO walletCreditRequestDTO)throws NotFoundException {
		 
		  RestTemplate restTemplete = restTemplateBuilder.build(); 
	 
		  String Url =  walletApiBaseUrl +  unlockedfundUrl;
		  
		  ResponseEntity<WalletCreaditResponseDTO> response = 
				  restTemplete.postForEntity(Url,walletCreditRequestDTO, WalletCreaditResponseDTO.class);

		  WalletCreaditResponseDTO walletCreaditResponseDTO= response.getBody();
			if (walletCreaditResponseDTO != null && "UNLOCKED".equals(walletCreaditResponseDTO.getStatus())) {	  
	            throw new WalletOperationException("Failed to credit winnings to the user's wallet!");
	        }
		  return walletCreaditResponseDTO;
	 }
	//call notification service to get notify user via email
	 public NotificationResponseDTO NotifyUser(BetRequestDTO betRequestDto)throws NotFoundException {
		
		  RestTemplate restTemplete = restTemplateBuilder.build(); 
	 
		  String Url = notificationServiceApiBaseUrl + getNotifiyUrl;
		  
		  ResponseEntity<NotificationResponseDTO> response = 
				  restTemplete.postForEntity(Url,betRequestDto, NotificationResponseDTO.class);

		  NotificationResponseDTO notificationResponseDTO= response.getBody();
		  if(notificationResponseDTO ==null) {
			  throw new NotificationFailureException("Failed to send notification to the user!");
		  }
		 return notificationResponseDTO;
	 
	 }
}
	 
	 