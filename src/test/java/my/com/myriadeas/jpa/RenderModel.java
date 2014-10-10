package my.com.myriadeas.jpa;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class RenderModel {
	@NotBlank
	@Length(max = 60)
	private String desc;
	
	@NotBlank
	private String desc2;
}
