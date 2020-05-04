package it.course.myblog.payload.request;

import javax.validation.constraints.NotNull;

//import lombok.Getter;
//import lombok.Setter;
//
//@Getter @Setter
public class ChangeRoleRequest {
	
	
	@NotNull
	private Long id;
	@NotNull
	private String oldRoleName;
	@NotNull
	private String newRoleName;
	public ChangeRoleRequest(@NotNull Long id, @NotNull String oldRoleName, @NotNull String newRoleName) {
		super();
		this.id = id;
		this.oldRoleName = oldRoleName;
		this.newRoleName = newRoleName;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOldRoleName() {
		return oldRoleName;
	}
	public void setOldRoleName(String oldRoleName) {
		this.oldRoleName = oldRoleName;
	}
	public String getNewRoleName() {
		return newRoleName;
	}
	public void setNewRoleName(String newRoleName) {
		this.newRoleName = newRoleName;
	}

	
	
	
	
}
