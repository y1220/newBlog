package it.course.myblog.payload.request;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class BlacklistSignedAsVerifiedRequest {
	
	@NotNull
	private Long blacklistId;
	
	@NotNull
	private Long blacklistReasonId;
	
	
	private boolean toBan;
	
	
}
