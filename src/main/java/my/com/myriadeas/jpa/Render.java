package my.com.myriadeas.jpa;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.ejb.metamodel.AbstractAttribute;

public class Render {

	ValidatorFactory validatorFactory = Validation
			.buildDefaultValidatorFactory();
	UIUtils uiUtils = new UIUtils("src/main/resources/ui.json",
			"src/main/resources/ext-ui.json");

	public Render() {
		super();
	}

	public boolean isIncluded(Attribute<?, ?> property) {
		return !isExcluded(property);
	}

	public boolean isExcluded(Attribute<?, ?> property) {
		List<String> toStringPropertyList = Arrays.asList(new String[] {
				"createdBy", "createdDate", "lastModifiedBy",
				"lastModifiedDate", "version", "id" });
		return toStringPropertyList.contains(property.getName())
				|| ((AbstractAttribute) property).getWeight() == Integer.MIN_VALUE;
	}

	public boolean isAssociation(Attribute<?, ?> property) {
		return !property.getPersistentAttributeType().equals(
				PersistentAttributeType.BASIC);
	}

	public boolean isSelection(Attribute<?, ?> property) {
		if (isAssociation(property)) {
			return uiUtils
					.getLookupSize(property.getJavaType().getSimpleName()) == 1;
		}
		return false;
	}

	public boolean isSearchBox(Attribute<?, ?> property) {
		if (isAssociation(property)) {
			return uiUtils
					.getLookupSize(property.getJavaType().getSimpleName()) == 3;
		}
		return false;
	}

	public String getInputName(EntityType<?> domainClass,
			Attribute<?, ?> property) {
		return "input" + StringUtils.capitalize(property.getName());
	}

	public String getFormName(EntityType<?> domainClass) {
		return "form";
	}

	public String getFormPropertyName(EntityType<?> domainClass,
			Attribute<?, ?> property) {
		return getFormName(domainClass) + "['"
				+ getInputName(domainClass, property) + "']";
	}

	public String getFormPropertyError(EntityType<?> domainClass,
			Attribute<?, ?> property) {
		return getFormPropertyName(domainClass, property)
				+ ".$error."
				+ StringUtils.uncapitalize(property.getJavaType()
						.getSimpleName());
	}

	public String getModelName(EntityType<?> domainClass,
			Attribute<?, ?> property) {
		return StringUtils.uncapitalize(domainClass.getName()) + "."
				+ property.getName();
	}

	public String getReferenceModelName(EntityType<?> domainClass,
			Attribute<?, ?> property) {
		return StringUtils.uncapitalize(domainClass.getName()) + "."
				+ StringUtils.uncapitalize(property.getName());
	}

	public String getDisplayDomainToString(EntityType<?> propertyDomainClass,
			EntityType<?> domainClass, Attribute<?, ?> property) {
		String toString = getDefaultDomainToString(propertyDomainClass);
		if (StringUtils.isNotBlank(uiUtils.getToString(propertyDomainClass))) {
			toString = uiUtils.getToString(propertyDomainClass);
		}
		return stringReplace(toString, "{{domainName}}",
				getModelName(domainClass, property));
	}

	public String getDomainToString(EntityType<?> domainClass) {
		String toString = getDefaultDomainToString(domainClass);

		if (StringUtils.isNotBlank(uiUtils.getToString(domainClass))) {
			toString = uiUtils.getToString(domainClass);
		}
		return stringReplace(toString, "{{domainName}}",
				StringUtils.uncapitalize(domainClass.getName()));
	}

	public String getDefaultDomainToString(EntityType<?> domainClass) {
		for (Attribute<?, ?> property : domainClass.getAttributes()) {
			if (isToStringProperty(property)) {
				return "{{domainName}}." + property.getName();
			}
		}
		return "{{domainName}}.id";
	}

	public boolean isUniqueConstraint(EntityType<?> domainClass,
			Attribute<?, ?> property) {
		return isUniqueConstraintFromTableAnnotation(domainClass, property)
				|| isUniqueConstraintFromColumnAnnotation(domainClass, property);
	}

	private boolean isUniqueConstraintFromTableAnnotation(
			EntityType<?> domainClass, Attribute<?, ?> property) {
		Table tableAnnotation = domainClass.getJavaType().getAnnotation(
				Table.class);
		if (tableAnnotation != null
				&& tableAnnotation.uniqueConstraints() != null) {
			for (UniqueConstraint uniqueConstraint : tableAnnotation
					.uniqueConstraints()) {
				for (String columnName : uniqueConstraint.columnNames()) {
					if (columnName.indexOf(getPropertyColumnName(property)) == 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isUniqueConstraintFromColumnAnnotation(
			EntityType<?> domainClass, Attribute<?, ?> property) {
		Column columnAnnotation;
		if (property.getName().equals("code")) {
			return true;
		}
		try {

			columnAnnotation = domainClass.getJavaType()
					.getDeclaredField(property.getName())
					.getAnnotation(Column.class);
			return (columnAnnotation != null && columnAnnotation.unique());
		} catch (NoSuchFieldException e) {
		} catch (SecurityException e) {
		}
		return false;
	}

	private String getPropertyColumnName(Attribute<?, ?> property) {
		if (isAssociation(property)) {
			return property.getName() + "_";
		} else {
			return property.getName();
		}
	}

	private boolean isToStringProperty(Attribute<?, ?> property) {
		List<String> toStringPropertyList = Arrays.asList(new String[] {
				"name", "desc", "display", "id" });
		return property.getName().length() > 4 ? toStringPropertyList
				.contains(property.getName().substring(4)) : false;
	}

	private String stringReplace(String input, String placeholder, String value) {
		return input.replace(placeholder, value);
	}

	public String getPlaceholder(EntityType<?> domainClass,
			Attribute<?, ?> property) {
		return "{{'" + getModelName(domainClass, property)
				+ ".placeholder' | i18n}}";
	}

}