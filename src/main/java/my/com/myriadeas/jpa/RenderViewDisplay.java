package my.com.myriadeas.jpa;

import java.util.Date;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import org.hibernate.validator.constraints.Length;

public class RenderViewDisplay extends Render {

	private Metamodel metamodel;

	public RenderViewDisplay() {
		super();
	}

	public RenderViewDisplay(Metamodel metamodel) {
		this.setMetamodel(metamodel);
	}

	public Metamodel getMetamodel() {
		return metamodel;
	}

	public void setMetamodel(Metamodel metamodel) {
		this.metamodel = metamodel;
	}

	public String renderPropertyViewDisplay(EntityType<?> domainClass,
			Attribute<?, ?> property) {
		PropertyDescriptor propertyDescriptor = validatorFactory.getValidator()
				.getConstraintsForClass(domainClass.getJavaType())
				.getConstraintsForProperty(property.getName());
		String out = "";
		if (property.getJavaType().isAssignableFrom(Date.class)
				|| property.getJavaType().isAssignableFrom(java.sql.Date.class)
				|| property.getJavaType().isAssignableFrom(java.sql.Time.class)) {
			out = renderDateEditor(domainClass, property, propertyDescriptor);
		} else if (property.getPersistentAttributeType().equals(
				Attribute.PersistentAttributeType.MANY_TO_ONE)
				|| property.getPersistentAttributeType().equals(
						Attribute.PersistentAttributeType.ONE_TO_ONE)) {
			out = renderManyToOne(domainClass, property, propertyDescriptor);
		} else if (property.getPersistentAttributeType().equals(
				Attribute.PersistentAttributeType.ONE_TO_MANY)
				|| property.getPersistentAttributeType().equals(
						Attribute.PersistentAttributeType.MANY_TO_MANY)) {
			out = renderManyToMany(domainClass, property, propertyDescriptor);
		} else {
			out = renderString(domainClass, property, propertyDescriptor);
		}
		return out;
	}

	public String renderManyToOne(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		String out = "<dd>:<m-form-a ng-model=\""
				+ getModelName(domainClass, property) + "\"></m-form-a></dd>";
		return out;
	}

	public String renderManyToMany(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		String out = "<dd>:";

		String singlePropertyName = property.getName().substring(0,
				property.getName().length() - 1);
		out += "<ul class=\"list-unstyled\">";
		out += "<li ng-repeat=\"" + singlePropertyName + " in "
				+ getModelName(domainClass, property) + "\">";
		out += "<m-form-a ng-model=\"" + singlePropertyName + "\"></m-form-a>";
		out += "</li>";
		out += "</ul>";
		out += "</dd>";
		return out;
	}

	public String renderDateEditor(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {

		String out = "<dd>:{{" + getModelName(domainClass, property)
				+ "| date:\"fullDate\"}}</dd>";
		return out;
	}

	public boolean isLengthConstraint(ConstraintDescriptor<?> constraint) {
		if (constraint
				.getAnnotation()
				.annotationType()
				.isAssignableFrom(
						org.hibernate.validator.constraints.Length.class)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isConstraint(ConstraintDescriptor<?> constraint,
			Class<?> _class) {
		if (constraint.getAnnotation().annotationType()
				.isAssignableFrom(_class)) {
			return true;
		} else {
			return false;
		}
	}

	public Max getMaxConstraint(PropertyDescriptor propertyDescriptor) {
		if (propertyDescriptor != null && propertyDescriptor.hasConstraints()) {
			for (ConstraintDescriptor<?> constraint : propertyDescriptor
					.getConstraintDescriptors()) {
				if (isConstraint(constraint,
						javax.validation.constraints.Max.class)) {
					Max _constraint = (Max) constraint.getAnnotation();
					return _constraint;
				}
			}
		}
		return null;
	}

	public Min getMinConstraint(PropertyDescriptor propertyDescriptor) {
		if (propertyDescriptor != null && propertyDescriptor.hasConstraints()) {
			for (ConstraintDescriptor<?> constraint : propertyDescriptor
					.getConstraintDescriptors()) {
				if (isConstraint(constraint,
						javax.validation.constraints.Min.class)) {
					Min _constraint = (Min) constraint.getAnnotation();
					return _constraint;
				}
			}
		}
		return null;
	}

	public Length getLengthConstraint(PropertyDescriptor propertyDescriptor) {
		if (propertyDescriptor != null && propertyDescriptor.hasConstraints()) {
			for (ConstraintDescriptor<?> constraint : propertyDescriptor
					.getConstraintDescriptors()) {
				if (isLengthConstraint(constraint)) {
					Length lengthConstraint = (Length) constraint
							.getAnnotation();
					return lengthConstraint;
				}
			}
		}
		return null;
	}

	public int getLengthConstraintMaxValue(PropertyDescriptor propertyDescriptor) {
		Length lengthConstraint = getLengthConstraint(propertyDescriptor);
		if (lengthConstraint != null) {
			return lengthConstraint.max();
		}
		return -1;
	}

	public String renderString(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {

		String out = "<dd>:{{" + getModelName(domainClass, property)
				+ "}}</dd>";

		return out;
	}

}
