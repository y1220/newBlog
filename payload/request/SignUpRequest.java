package it.course.myblog.payload.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@ApiModel(value="SignUpRequest object is used to user registration")
public class SignUpRequest {
	
	@Pattern(regexp = "[A-Za-z]+[[\\s]*[A-Za-z]+]*", message = "please provide a name made by characters or numbers or space(mixed one is also fine)")
	@ApiModelProperty(notes = "Insert name")
	private String name;
	
	@ApiModelProperty(notes = "Insert lastname")
	private String lastname;
	
	@NotBlank
	@Size(min=3, max=20)
	@Pattern(regexp = "[A-Za-z0-9]+", message = "please provide a name made by characters or numbers(mixed one is also fine)")
	@ApiModelProperty(notes = "Insert username")
	private String username;
	
	@NotBlank
	@Size(min=6, max=120)
	@Email
	@Pattern(regexp = ".+@.+\\..+", message = "Please provide a valid email address")
	@ApiModelProperty(notes = "Insert email")
	private String email;
	
	@NotBlank
	@Size(min=5, max=8)
	@ApiModelProperty(notes = "Insert password")
	private String password;
	
	@ApiModelProperty(notes = "User want receive newsletter? Default = false")
	private boolean hasNewsletter = false;
}
