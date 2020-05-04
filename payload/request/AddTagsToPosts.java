package it.course.myblog.payload.request;

import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor
public class AddTagsToPosts {

	
	List<Long> ids;
	Set<Long> tagIds;
	public AddTagsToPosts(List<Long> ids, Set<Long> tagIds) {
		super();
		this.ids = ids;
		this.tagIds = tagIds;
	}
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
	public Set<Long> getTagIds() {
		return tagIds;
	}
	public void setTagIds(Set<Long> tagIds) {
		this.tagIds = tagIds;
	}
	
	
	
}
