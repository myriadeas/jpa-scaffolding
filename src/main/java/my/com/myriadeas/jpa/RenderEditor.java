package my.com.myriadeas.jpa;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

public class RenderEditor extends Render {

	protected Metamodel metamodel;

	private RenderViewDisplay renderViewdisplay = new RenderViewDisplay();

	public RenderEditor() {

	}

	public RenderEditor(Metamodel metamodel) {
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
		out = "<m-form-input name=\""
				+ StringUtils.uncapitalize(domainClass.getName())
				+ "\" property=\"" + property.getName() + "\" size=\""
				+ getSpanClassFromMaxValue(propertyDescriptor)
				+ "\" form=\"form\">";

		if (property.getJavaType().isAssignableFrom(Boolean.class)
				|| property.getJavaType().isAssignableFrom(boolean.class)) {
			out += renderBooleanEditor(domainClass, property,
					propertyDescriptor);
		} else if (Number.class.isAssignableFrom(property.getJavaType())
				|| (property.getJavaType().isPrimitive() && !property
						.getJavaType().isAssignableFrom(boolean.class))) {
			out += renderNumberEditor(domainClass, property, propertyDescriptor);
		} else if (property.getJavaType().isAssignableFrom(String.class)) {
			out += renderStringEditor(domainClass, property, propertyDescriptor);
		} else if (property.getJavaType().isAssignableFrom(Date.class)
				|| property.getJavaType().isAssignableFrom(java.sql.Date.class)
				|| property.getJavaType().isAssignableFrom(java.sql.Time.class)) {
			out += renderDateEditor(domainClass, property, propertyDescriptor);
		} else if (property.getJavaType().isAssignableFrom(Calendar.class)) {
			out += renderDateEditor(domainClass, property, propertyDescriptor);
		} else if (property.getJavaType().isAssignableFrom(URL.class)) {
			out += renderStringEditor(domainClass, property, propertyDescriptor);
		} else if (property.getJavaType().isEnum()) {
			out += renderEnumEditor(domainClass, property, propertyDescriptor);
		} else if (property.getJavaType().isAssignableFrom(TimeZone.class)) {
			out += renderSelectTypeEditor("timeZone", domainClass, property,
					propertyDescriptor);
		} else if (property.getJavaType().isAssignableFrom(Locale.class)) {
			out += renderSelectTypeEditor("locale", domainClass, property,
					propertyDescriptor);
		} else if (property.getJavaType().isAssignableFrom(Currency.class)) {
			out += renderSelectTypeEditor("currency", domainClass, property,
					propertyDescriptor);
		} else if (property.getJavaType().isAssignableFrom(Byte[].class)) {
			out += renderByteArrayEditor(domainClass, property,
					propertyDescriptor);
		} else if (property.getPersistentAttributeType().equals(
				Attribute.PersistentAttributeType.MANY_TO_ONE)
				|| property.getPersistentAttributeType().equals(
						Attribute.PersistentAttributeType.ONE_TO_ONE)) {
			out += renderManyToOne(domainClass, property, propertyDescriptor);
		} /*-else if ((property.getPersistentAttributeType().equals(
				Attribute.PersistentAttributeType.ONE_TO_MANY) && !property..bidirectional)
				|| (property.i.manyToMany && property.isOwningSide())) {
			out = renderManyToMany(domainClass, property, propertyDescriptor);
			} */else if (property.getPersistentAttributeType().equals(
				Attribute.PersistentAttributeType.ONE_TO_MANY)
				|| property.getPersistentAttributeType().equals(
						Attribute.PersistentAttributeType.MANY_TO_MANY)) {
			out += renderOneToMany(domainClass, property, propertyDescriptor);
		}
		if (StringUtils.isBlank(out)) {
			System.out.println("not render: " + property.getName());
		}
		out += "</m-form-input>";
		return out;
	}

	public String renderEnumEditor(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		String out = "";
		return out;
	}

	private String getSpanClassFromMaxValue(
			PropertyDescriptor propertyDescriptor) {
		int maxValue = renderViewdisplay
				.getLengthConstraintMaxValue(propertyDescriptor);
		String spanClass = "";
		if (maxValue < 0) {
			spanClass = "m";
		} else if (maxValue <= 1) {
			spanClass = "xxs";
		} else if (maxValue <= 10) {
			spanClass = "xs";
		} else if (maxValue <= 20) {
			spanClass = "s";
		} else if (maxValue <= 40) {
			spanClass = "m";
		} else if (maxValue <= 80) {
			spanClass = "l";
		} else if (maxValue <= 100) {
			spanClass = "xl";
		} else {
			spanClass = "m";
		}

		return spanClass;
	}

	public String renderStringEditor(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		String out = "";
		HtmlAttribute htmlAttribute = getDefaultHtmlAttribute(domainClass,
				property, propertyDescriptor);
		htmlAttribute.put("type", "text");
		out = "<input " + htmlAttribute + "/>";
		return out;
	}

	public String renderByteArrayEditor(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		String out = "";
		return out;
	}

	public String renderManyToOne(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		EntityType<?> propertyDomainClass = metamodel.entity(property
				.getJavaType());
		StringBuffer out = new StringBuffer();

		if (isSearchBox(property)) {
			out.append("<m-search-input ng-model=\""
					+ this.getModelName(domainClass, property)
					+ "\" entity-name=\""
					+ StringUtils.uncapitalize(propertyDomainClass.getName())
					+ "\">");
			out.append("<span class=\"form-control\" disabled=\"true\" name=\""
					+ getInputName(domainClass, property) + "\" ng-bind=\""
					+ this.getModelName(domainClass, property)
					+ ".getDescription()\">");
			out.append("</m-form-select>");
		} else {
			out.append("<m-form-select ng-model=\""
					+ this.getModelName(domainClass, property)
					+ "\" entity-name=\""
					+ StringUtils.uncapitalize(propertyDomainClass.getName())
					+ "\">");
			out.append("<select class=\"form-control\" name=\""
					+ getInputName(domainClass, property) + "\">");
			out.append("</select>");
			out.append("</m-form-select>");
		}
		return out.toString();
	}

	public String renderManyToMany(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		String out = "";
		return out;
	}

	public String renderOneToMany(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		String out = "<div class=\"row\">";
		out += "<div class=\"col-md-8\">";
		String singlePropertyName = property.getName().substring(0,
				property.getName().length() - 1);
		out += "<div ng-repeat=\"" + singlePropertyName + " in "
				+ property.getName() + "\" class=\"checkbox\">";
		out += "<label>";
		HtmlAttribute htmlAttribute = new HtmlAttribute();
		htmlAttribute.put("type", "checkbox");
		htmlAttribute.put("checklist-model",
				getModelName(domainClass, property));
		htmlAttribute.put("checklist-value", singlePropertyName);
		out += "<input " + htmlAttribute + "/>";
		out += "<m-form-a ng-model=\"" + singlePropertyName + "\"></m-form-a>";
		out += "</label>";
		out += "</div>";
		out += "</div>";
		out += "</div>";
		return out;
	}

	public String renderNumberEditor(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		/*-
		 * <input type="number" ng-model="{string}" [name="{string}"] [ng-required="{string}"]>
		 */

		String customType = property.getJavaType().getSimpleName()
				.toLowerCase();
		HtmlAttribute htmlAttribute = getDefaultHtmlAttribute(domainClass,
				property, propertyDescriptor);
		htmlAttribute.put("type", "number");
		htmlAttribute.put("class", "form-control");
		htmlAttribute.put(customType, "");
		String out = "<input " + htmlAttribute + "/>";
		return out;
	}

	public String renderBooleanEditor(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		String out = "";
		HtmlAttribute htmlAttribute = getDefaultHtmlAttribute(domainClass,
				property, propertyDescriptor);
		htmlAttribute.put("type", "checkbox");
		htmlAttribute.put("ng-required", "false");
		htmlAttribute.put("class", "");
		out = "<input " + htmlAttribute + "/>";
		return out;
	}

	public String renderDateEditor(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		String required = "";
		if (isMandatoryField(propertyDescriptor)) {
			required = "ng-required=\"true\"";
		}
		StringBuffer out = new StringBuffer();
		// out.append("<div class=\"form-horizontal\">");

		out.append("<p class=\"input-group\">");
		out.append("<input  class=\"form-control\" type=\"text\" name=\""
				+ getInputName(domainClass, property)
				+ "\" datepicker-popup=\"dd-MMMM-yyyy\" ng-model=\""
				+ getModelName(domainClass, property)
				+ "\" is-open=\"datepickers."
				+ property.getName()
				+ "\" min-date=\"minDate\" max-date=\"'2100-01-01'\" "
				+ "placeholder=\""
				+ getPlaceholder(domainClass, property)
				+ "\" "
				+ "datepicker-options=\"dateOptions\" date-disabled=\"disabled(date, mode)\" "
				+ "close-text=\"Close\" " + required + " />");
		out.append("<span class=\"input-group-btn\">");
		out.append("<button class=\"btn btn-default\" type=\"button\" ng-click=\"open($event, \'"
				+ property.getName()
				+ "\')\"><i class=\"glyphicon glyphicon-calendar\"></i></button>");
		out.append("</span>");
		out.append("</p>");
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

	private HtmlAttribute getDefaultHtmlAttribute(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		HtmlAttribute htmlAttribute = new HtmlAttribute();
		// htmlAttribute.put("domain", domainClass.getName());
		// htmlAttribute.put("property", property.getName());
		htmlAttribute.put("class", "form-control");
		htmlAttribute.put("name", getInputName(domainClass, property));
		htmlAttribute.put("placeholder", getPlaceholder(domainClass, property));
		htmlAttribute.put("ng-model", getModelName(domainClass, property));
		// htmlAttribute.put("on-demand-validation", "true");
		if (this.isUniqueConstraint(domainClass, property)) {
			htmlAttribute.put("ng-blur", "");
			htmlAttribute.put("ng-unique", property.getName());
			htmlAttribute
					.put("table-name", domainClass.getName().toLowerCase());

		}
		if (isMandatoryField(propertyDescriptor)) {
			htmlAttribute.put("ng-required", "true");
		}
		Length length = renderViewdisplay
				.getLengthConstraint(propertyDescriptor);
		if (length != null) {
			htmlAttribute.put("maxlength", "" + length.max());
			htmlAttribute.put("minlength", "" + length.min());
		}

		Max max = renderViewdisplay.getMaxConstraint(propertyDescriptor);
		Min min = renderViewdisplay.getMinConstraint(propertyDescriptor);
		if (max != null) {
			htmlAttribute.put("max", "" + max.value());
		}
		if (min != null) {
			htmlAttribute.put("min", "" + min.value());
		}

		return htmlAttribute;
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
}
