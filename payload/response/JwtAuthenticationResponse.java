package it.course.myblog.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class JwtAuthenticationResponse {
	
	private String tokenType = "Bearer";
	
	private String accessToken;
	
	public JwtAuthenticationResponse(String accessToken) {
		this.accessToken =  accessToken;
	}

}
