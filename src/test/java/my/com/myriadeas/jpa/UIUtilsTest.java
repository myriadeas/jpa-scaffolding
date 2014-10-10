package my.com.myriadeas.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UIUtilsTest {

	@Test
	public void testToString() {
		UIUtils uiUtils = new UIUtils("src/main/resources/ui.json",
				"src/main/resources/ext-ui.json");
		String domainClass = "my.com.myriadeas.dao.domain.Patron";
		assertEquals("firstname", uiUtils.getToString(domainClass, ""));
	}

	@Test
	public void testDisplayList() {
		UIUtils uiUtils = new UIUtils("src/main/resources/ui.json",
				"src/main/resources/ext-ui.json");
		String domainClass = "my.com.myriadeas.dao.domain.Patron";
		String property = "username";
		assertTrue(uiUtils.isDisplayList(domainClass, property));
		property = "firstname";
		assertFalse(uiUtils.isDisplayList(domainClass, property));
	}

}
