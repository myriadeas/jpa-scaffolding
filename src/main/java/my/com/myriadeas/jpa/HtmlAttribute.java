package my.com.myriadeas.jpa;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class HtmlAttribute extends java.util.LinkedHashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String toString() {
		Iterator<Entry<String, String>> i = entrySet().iterator();
		if (!i.hasNext())
			return "";
		StringBuilder sb = new StringBuilder();
		for (;;) {
			Entry<String, String> e = i.next();
			String key = e.getKey();
			String value = e.getValue();
			sb.append(key);
			sb.append('=').append('"');
			sb.append(value);
			sb.append('"').append(' ');
			if (!i.hasNext())
				return sb.toString();
		}
	}

}
