package my.com.myriadeas.jpa;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import org.apache.velocity.util.StringUtils;

public class JavaDodRenderEditor extends Render {

	protected Metamodel metamodel;

	public JavaDodRenderEditor() {

	}

	public JavaDodRenderEditor(Metamodel metamodel) {
		this.setMetamodel(metamodel);
	}

	public Metamodel getMetamodel() {
		return metamodel;
	}

	public void setMetamodel(Metamodel metamodel) {
		this.metamodel = metamodel;
	}

	public String renderPropertyEditor(EntityType<?> domainClass,
			Attribute<?, ?> property) {
		String out = "";
		PropertyDescriptor propertyDescriptor = validatorFactory.getValidator()
				.getConstraintsForClass(domainClass.getJavaType())
				.getConstraintsForProperty(property.getName());
		if (property.getJavaType().isAssignableFrom(Boolean.class)
				|| property.getJavaType().isAssignableFrom(boolean.class)) {
			out = renderBooleanEditor(domainClass, property, propertyDescriptor);
		} else if (Number.class.isAssignableFrom(property.getJavaType())
				|| (property.getJavaType().isPrimitive() && !property
						.getJavaType().isAssignableFrom(boolean.class))) {
			out = renderNumberEditor(domainClass, property, propertyDescriptor);
		} else if (property.getJavaType().isAssignableFrom(String.class)) {
			out = renderStringEditor(domainClass, property, propertyDescriptor);
		} else if (property.getJavaType().isAssignableFrom(Date.class)
				|| property.getJavaType().isAssignableFrom(java.sql.Date.class)
				|| property.getJavaType().isAssignableFrom(java.sql.Time.class)) {
			out = renderDateEditor(domainClass, property, propertyDescriptor);
		} else if (property.getJavaType().isAssignableFrom(Calendar.class)) {
			out = renderDateEditor(domainClass, property, propertyDescriptor);
		} else if (property.getJavaType().isAssignableFrom(URL.class)) {
			out = renderStringEditor(domainClass, property, propertyDescriptor);
		} else if (property.getJavaType().isEnum()) {
			out = renderEnumEditor(domainClass, property, propertyDescriptor);
		} else if (property.getJavaType().isAssignableFrom(TimeZone.class)) {
			out = renderSelectTypeEditor("timeZone", domainClass, property,
					propertyDescriptor);
		} else if (property.getJavaType().isAssignableFrom(Locale.class)) {
			out = renderSelectTypeEditor("locale", domainClass, property,
					propertyDescriptor);
		} else if (property.getJavaType().isAssignableFrom(Currency.class)) {
			out = renderSelectTypeEditor("currency", domainClass, property,
					propertyDescriptor);
		} else if (property.getJavaType().isAssignableFrom(Byte[].class)) {
			out = renderByteArrayEditor(domainClass, property,
					propertyDescriptor);
		} else if (property.getPersistentAttributeType().equals(
				Attribute.PersistentAttributeType.MANY_TO_ONE)
				|| property.getPersistentAttributeType().equals(
						Attribute.PersistentAttributeType.ONE_TO_ONE)) {
			out = renderManyToOne(domainClass, property, propertyDescriptor);
		} /*-
			else if ((property.getPersistentAttributeType().equals(Attribute<?,?>.PersistentAttributeType.ONE_TO_MANY) && !property.bidirectional) || (property.manyToMany && property.isOwningSide()));
			
			    out = renderManyToMany(domainClass, property, propertyDescriptor)
			 */else if (property.getPersistentAttributeType().equals(
				Attribute.PersistentAttributeType.ONE_TO_MANY)) {
			out = renderOneToMany(domainClass, property, propertyDescriptor);
		}
		return out;
	}

	public String renderEnumEditor(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		String out = "";
		return out;
	}

	private boolean isMandatoryField(PropertyDescriptor propertyDescriptor) {
		if (propertyDescriptor != null && propertyDescriptor.hasConstraints()) {
			for (ConstraintDescriptor<?> constraint : propertyDescriptor
					.getConstraintDescriptors()) {
				Class<? extends Annotation> annotationType = constraint
						.getAnnotation().annotationType();
				if (annotationType
						.isAssignableFrom(org.hibernate.validator.constraints.NotBlank.class)
						|| annotationType
								.isAssignableFrom(org.hibernate.validator.constraints.NotEmpty.class)
						|| annotationType
								.isAssignableFrom(javax.validation.constraints.NotNull.class)) {
					return true;
				}
			}
		}
		return false;

	}

	public String renderStringEditor(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		StringBuffer out = new StringBuffer();
		int length = getLengthAllowed(propertyDescriptor);
		out.append(property.getJavaType().getCanonicalName() + " "
				+ property.getName() + " = index + \"_" + property.getName() + "\";");
		out.append("\n");
		out.append("if (" + property.getName() + ".length() > " + length
				+ ") {");
		out.append(property.getName() + " = " + property.getName()
				+ ".substring(0, " + length + ");");
		out.append("}");
		out.append("\n");
		out.append("obj.set"
				+ StringUtils.capitalizeFirstLetter(property.getName()) + "("
				+ property.getName() + ");");

		return out.toString();
	}

	private int getLengthAllowed(PropertyDescriptor propertyDescriptor) {
		if (propertyDescriptor == null) {
			return 20;
		}
		for (ConstraintDescriptor<?> cd : propertyDescriptor
				.getConstraintDescriptors()) {
			if (cd.getAnnotation()
					.annotationType()
					.isAssignableFrom(
							org.hibernate.validator.constraints.Length.class)) {
				return Integer
						.valueOf(cd.getAttributes().get("max").toString());
			}
		}
		return 20;

	}

	public String renderByteArrayEditor(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		String out = "";
		return out;
	}

	public String renderManyToOne(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		StringBuffer out = new StringBuffer();
		if (domainClass.getJavaType().isAssignableFrom(property.getJavaType())) {
			// don't want recursive loop
			return "";
		}

		out.append("obj.set"
				+ StringUtils.capitalizeFirstLetter(property.getName())
				+ "(context.getBean(" + property.getJavaType().getSimpleName()
				+ "DataOnDemand.class).getRandom());");
		return out.toString();
	}

	public String renderManyToMany(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		String out = "";
		return out;
	}

	public String renderOneToMany(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		String out = "";
		return out;
	}

	public String renderNumberEditor(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		StringBuffer out = new StringBuffer();
		if (property.getJavaType().isAssignableFrom(BigDecimal.class)) {
			out.append(property.getJavaType().getCanonicalName() + " "
					+ property.getName() + " = new "
					+ property.getJavaType().getCanonicalName() + "(index);");
		} else if (property.getJavaType().isAssignableFrom(Integer.class)) {
			out.append(property.getJavaType().getCanonicalName() + " "
					+ property.getName() + " = new Integer(index);");
		} else {
			out.append(property.getJavaType().getCanonicalName() + " "
					+ property.getName() + " = new Integer(index)."
					+ property.getJavaType().getSimpleName().toLowerCase()
					+ "Value();");
		}
		out.append("\n");
		out.append("obj.set"
				+ StringUtils.capitalizeFirstLetter(property.getName()) + "("
				+ property.getName() + ");");

		return out.toString();
	}

	public String renderBooleanEditor(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		StringBuffer out = new StringBuffer();
		out.append(property.getJavaType().getCanonicalName() + " "
				+ property.getName() + " = new "
				+ property.getJavaType().getCanonicalName() + "(true);");
		out.append("\n");
		out.append("obj.set"
				+ StringUtils.capitalizeFirstLetter(property.getName()) + "("
				+ property.getName() + ");");

		return out.toString();
	}

	public String renderDateEditor(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		StringBuffer out = new StringBuffer();
		out.append("Calendar calendar = java.util.Calendar.getInstance();\n");
		out.append("calendar.set(2020, 1, index);\n");
		out.append("Date " + property.getName() + " = calendar.getTime();");
		out.append("\n");
		out.append("obj.set"
				+ StringUtils.capitalizeFirstLetter(property.getName()) + "("
				+ property.getName() + ");");
		return out.toString();
	}

	public String renderSelectTypeEditor(String type,
			EntityType<?> domainClass, Attribute<?, ?> property,
			PropertyDescriptor propertyDescriptor) {
		String out = "";
		return out;
	}

	public String renderNoSelection(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		String out = "";
		return out;
	}
}
