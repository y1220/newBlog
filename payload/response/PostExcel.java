package it.course.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostExcel {

	private Long idPost;
	private String title;
	private String isFree; // Y-N

	private Long totalViews;
	private Long uniqueViews;

	private String isBanned; // Y-N
	private Double avgRating;

	private long numberOfPurchases;
}
