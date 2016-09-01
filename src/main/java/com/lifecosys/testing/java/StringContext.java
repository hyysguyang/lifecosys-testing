package com.lifecosys.testing.java;

import javaslang.collection.Map;
import org.apache.commons.lang3.text.StrSubstitutor;

/**
 * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
 */
public class StringContext {

	Map<String, Object> context;

	public StringContext(Map<String, Object> values) {
		this.context = values;
	}

	/**
	 * Will Escape ' to "
	 * 
	 * @param source
	 * @return
	 */
	public String s(String source) {
		return StrSubstitutor.replace(source.replaceAll("'", "\""), context.toJavaMap());
	}
}
