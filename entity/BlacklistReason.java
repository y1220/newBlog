package it.course.myblog.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="blacklist_reason")
@Data  @NoArgsConstructor
//getter setter ???
public class BlacklistReason {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="reason", nullable=false, columnDefinition="VARCHAR(120)")
	private String reason;
	
	@Column(name="days", nullable=false)
	private int days;

	public BlacklistReason(Long id, String reason, int days) {
		super();
		this.id = id;
		this.reason = reason;
		this.days = days;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}
	
	

}
