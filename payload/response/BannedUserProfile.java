package it.course.myblog.payload.response;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import it.course.myblog.entity.Blacklist;
import it.course.myblog.entity.BlacklistReason;
import it.course.myblog.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class BannedUserProfile {
	
	private Long id;
	private String username;
	private LocalDate bannedUntil;
	private String reason;
	private List<BlacklistResponse> blacklists;
	private int countBlacklist;
	
	
	
	public static BannedUserProfile create(Users user, Blacklist blacklist, 
			BlacklistReason blacklistReason, List<BlacklistResponse> blacklists, int countBlacklist) {
		return new BannedUserProfile(
			user.getId(),
			user.getUsername(),
			blacklist.getBlacklistedUntil(),
			blacklistReason.getReason(),
			blacklists,
			countBlacklist
			);			
	}

}
