package it.course.myblog.payload.request;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
@ApiModel(value="BlacklistRequest object is used to blacklist an user and his post/comment")
public class BlacklistRequest {
	
	
	@NotNull
	@ApiModelProperty(notes = "Blacklisted from")
	private LocalDate blacklistedFrom;
	
	@NotNull
	@ApiModelProperty(notes = "Blacklisted to")
	private LocalDate blacklistedUntil;
	
	@NotNull
	@ApiModelProperty(notes = "User id to put in blacklist")
	private Long userId;
	
	@NotNull
	@ApiModelProperty(notes = "Post id to put in blacklist")
	private Long postId;
	
	@ApiModelProperty(notes = "Comment id to put in blacklist")
	private Long commentId = Long.valueOf(0);
	
	@NotNull
	@ApiModelProperty(notes = "Blacklist Reason id")
	private Long blacklistReasonId;
	
}
