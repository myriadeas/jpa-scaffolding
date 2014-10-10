package my.com.myriadeas.jpa;

import java.io.File;
import java.io.IOException;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;

public class UIUtils {

	private JsonNode uiNode;

	private JsonNode extUiNode;

	public UIUtils(String ui, String extUi) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			uiNode = mapper.readTree(new File(ui));
			extUiNode = mapper.readTree(new File(extUi));
		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new RuntimeException("wrong json format");
		} catch (IOException e) {
			throw new RuntimeException("file not exists");
		}
	}

	public String getSetting(EntityType<?> domainClass,
			Attribute<?, ?> property, String setting) {
		return uiNode.path(domainClass.getJavaType().getName())
				.path(property.getName()).path(setting).getTextValue();
	}

	public int getLookupSize(EntityType<?> domainClass) {
		return getLookupSize(domainClass.getJavaType().getSimpleName());

	}

	public int getLookupSize(String domainClass) {
		return extUiNode.path(domainClass).has("lookup") ? extUiNode
				.path(domainClass).path("lookup").getIntValue() : uiNode
				.path(domainClass).path("lookup").getIntValue();
	}

	public String getToString(EntityType<?> domainClass,
			Attribute<?, ?> property) {
		return getToString(domainClass.getJavaType().getSimpleName(),
				property.getName());
	}

	public String getToString(EntityType<?> domainClass) {
		return getToString(domainClass.getJavaType().getSimpleName(),
				domainClass.getName().toLowerCase());
	}

	public String getToString(String domainClass, String placeholderValue) {
		return extUiNode.path(domainClass).has("toString") ? extUiNode
				.path(domainClass).path("toString").getTextValue() : uiNode
				.path(domainClass).path("toString").getTextValue();
	}

	public boolean isDisplayList(EntityType<?> domainClass,
			Attribute<?, ?> property) {
		return isDisplayList(domainClass.getJavaType().getSimpleName(),
				property.getName());
	}

	public boolean isDisplayList(String domainClass, String property) {
		return extUiNode.path(domainClass).path(property).has("displayList") ? extUiNode
				.path(domainClass).path(property).path("displayList")
				.getBooleanValue()
				: uiNode.path(domainClass).path(property).path("displayList")
						.getBooleanValue();
	}

	
}
