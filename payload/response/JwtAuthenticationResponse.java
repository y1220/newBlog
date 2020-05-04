package it.course.myblog.payload.response;

public class JwtAuthenticationResponse {
	
	// mukimei
	private String tokenType = "Bearer";
	
	private String accessToken;
	
	public JwtAuthenticationResponse(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
}
