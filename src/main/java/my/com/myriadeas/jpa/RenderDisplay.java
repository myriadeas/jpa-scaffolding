package my.com.myriadeas.jpa;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.validation.metadata.PropertyDescriptor;

import org.apache.commons.lang.StringUtils;

public class RenderDisplay extends Render {

	private Metamodel metamodel;

	public RenderDisplay() {
		super();
	}

	public RenderDisplay(Metamodel metamodel) {
		this.setMetamodel(metamodel);
	}

	public Metamodel getMetamodel() {
		return metamodel;
	}

	public void setMetamodel(Metamodel metamodel) {
		this.metamodel = metamodel;
	}

	public String renderPropertyDisplay(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		String out = "";
		if (property.getPersistentAttributeType().equals(
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
		} else {
			out = renderString(domainClass, property, propertyDescriptor);
		}
		return out;
	}

	public String renderManyToOne(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		StringBuffer out = new StringBuffer();
		EntityType<?> referenceDomainClass = metamodel.entity(property
				.getJavaType());
		out.append("<a href=\"index.html#/"
				+ StringUtils.uncapitalize(referenceDomainClass.getName()) + "/{{"
				+ getReferenceModelName(domainClass, property) + ".id}}\">{{"
				+ getReferenceModelName(domainClass, property) + "."
				+ getDomainToString(referenceDomainClass) + "}}</a>");
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

	public String renderString(EntityType<?> domainClass,
			Attribute<?, ?> property, PropertyDescriptor propertyDescriptor) {
		String out = "{{" + StringUtils.uncapitalize(domainClass.getName()) + "."
				+ property.getName() + "}}";
		return out;
	}

}
