package it.course.myblog.payload.request;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class AddTagsToPosts {
	
	List<Long> ids;
	Set<Long> tagIds;

}
