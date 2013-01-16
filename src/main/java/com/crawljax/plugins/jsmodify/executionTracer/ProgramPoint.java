package com.crawljax.plugins.jsmodify.executionTracer;

import org.json.JSONArray;
import org.json.JSONException;

public class ProgramPoint {

	protected String name;
	protected String lineNo;

	public ProgramPoint(String name, String lineNo) {
		this.name = name;
		this.lineNo = lineNo;

	}

	public String getName() {
		return name;
	}

	public String getLineNo() {
		return lineNo;
	}

	public String getTraceRecord(JSONArray data) throws JSONException {
		StringBuffer result = new StringBuffer();

		result.append(name + "::" + lineNo + "\n");

		for (int i = 0; i < data.length(); i++) {

			JSONArray item = data.getJSONArray(i);

			for (int j = 0; j < item.length(); j++)
				result.append(item.get(j) + "::");

		}

		result.append("\n");
		result.append("================================================");
		result.append("\n");

		return result.toString();
	}

}
