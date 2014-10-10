package my.com.myriadeas.jpa;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import org.junit.Test;

public class RenderViewDisplayTest {
	ValidatorFactory validatorFactory = Validation
			.buildDefaultValidatorFactory();
	@Test
	public void testRenderString() {
		RenderViewDisplay renderViewDisplay = new RenderViewDisplay();
		EntityType<?> domainClass = null;
		Attribute<?, ?> property = null;
		PropertyDescriptor propertyDescriptor = validatorFactory.getValidator()
				.getConstraintsForClass(RenderModel.class)
				.getConstraintsForProperty("desc");
		for(ConstraintDescriptor<?> constraint : propertyDescriptor
				.getConstraintDescriptors()){
			System.out.println(renderViewDisplay.isLengthConstraint(constraint));
			
		}
		System.out.println("Length Constraint= " + renderViewDisplay.getLengthConstraint(propertyDescriptor));
		System.out.println("max length= " + renderViewDisplay.getLengthConstraintMaxValue(propertyDescriptor));
		
		propertyDescriptor = validatorFactory.getValidator()
				.getConstraintsForClass(RenderModel.class)
				.getConstraintsForProperty("desc2");
		System.out.println("max length= " + renderViewDisplay.getLengthConstraintMaxValue(propertyDescriptor));
		
	}

}
