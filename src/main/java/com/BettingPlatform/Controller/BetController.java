package com.BettingPlatform.Controller;

import java.util.List;
import javax.naming.InsufficientResourcesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.BettingPlatform.DTO.BetRequestDTO;
import com.BettingPlatform.DTO.BetResponceDto;
import com.BettingPlatform.DTO.GameTotalBetsResponseDTO;
import com.BettingPlatform.DTO.AdminSummaryResponseDTO;
import com.BettingPlatform.DTO.AverageBetResponseDTO;
import com.BettingPlatform.DTO.UserSummaryResponseDTO;
import com.BettingPlatform.DTO.UserTotalBetsResponseDTO;
import com.BettingPlatform.Service.BetService;
import com.BettingPlatform.exception.NotFoundException;

@RestController
@RequestMapping("/bet")
public class BetController {
	
	private final BetService betService;

	// Constructor Injection
	@Autowired
	public BetController(BetService betService){
		this.betService=betService;
		
	}
	
	@PostMapping("/place")
	public ResponseEntity<BetResponceDto> PlaceBet(@RequestBody BetRequestDTO betRequestDTO) throws InsufficientResourcesException, NotFoundException{
		return ResponseEntity.ok(betService.placeBet(betRequestDTO));
	}
	@PostMapping("/settle")
	public ResponseEntity<BetResponceDto> GameStatus(@RequestBody BetRequestDTO betRequestDTO) throws InsufficientResourcesException, NotFoundException{
		
		return ResponseEntity.ok(betService.GameStatus(betRequestDTO));
	}
		
	@GetMapping("/AdminBetStatics")
	   public AdminSummaryResponseDTO getAdminAnalyticsByType(
	   ) throws NotFoundException {
	        return betService.getAdminAnalytics();
	    }
	
	@GetMapping("/UserBetStatics")
	  public UserSummaryResponseDTO getUserAnalyticsByType(
	   ) throws NotFoundException {
			return betService.getUserAnalytics();
		 }

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<UserTotalBetsResponseDTO>> getAllTotalBetsByUserId(@PathVariable Long userId) {
	    List<UserTotalBetsResponseDTO> response = betService.getAllTotalBetsByUserId(userId);
	    return ResponseEntity.ok(response);
	}

	@GetMapping("/game/{gameId}")
	  public ResponseEntity<List<GameTotalBetsResponseDTO>> getAllBetsByGameId(@PathVariable Long gameId) {
	    List<GameTotalBetsResponseDTO> response = betService.getAllBetsByGameId(gameId);
	    return ResponseEntity.ok(response);
	}	
    @GetMapping("/averageBet/{userId}")
	    public ResponseEntity<AverageBetResponseDTO> getAverageBet(@PathVariable Long userId) {
	        AverageBetResponseDTO response = betService.calculateAverageBetByUserId(userId);
	        return ResponseEntity.ok(response);
	    }
}







	
	
