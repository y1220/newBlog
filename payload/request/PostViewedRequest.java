package it.course.myblog.payload.request;

import java.time.Instant;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data @AllArgsConstructor @NoArgsConstructor
public class PostViewedRequest {
	
	private Instant startDate;
	private Instant endDate;
	private Long postId;

}
