package it.course.myblog.payload;

public class UserIdentityAvailability {

	private boolean avalaible;

	public UserIdentityAvailability(boolean avalaible) {
		super();
		this.avalaible = avalaible;
	}

	public boolean isAvalaible() {
		return avalaible;
	}

	public void setAvalaible(boolean avalaible) {
		this.avalaible = avalaible;
	}
	
}
