package com.crawljax.plugins.jsmodify.executionTracer;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class Trace {
	private ArrayList<ProgramPoint> programPoints;

	/**
	 * Construct a trace representation.
	 */
	public Trace() {
		programPoints = new ArrayList<ProgramPoint>();
	}

	/**
	 * Get or create a program point defined by name.
	 * 
	 * @param name
	 *            Name of the program point.
	 * @return The ProgramPoint object.
	 */
	public ProgramPoint addProgramPoint(String name, String lineNo) {
		ProgramPoint p = new ProgramPoint(name, lineNo);
		programPoints.add(p);
		return p;
	}

	/**
	 * Parse JSON object into a trace.
	 * 
	 * @param jsonObject
	 *            The JSON object.
	 * @return The string representation of the JSON object.
	 * @throws JSONException
	 *             On error.
	 */
	public String parse(JSONArray jsonObject) throws JSONException {
		StringBuffer result = new StringBuffer();
		for (int j = 0; j < jsonObject.length(); j++) {

			JSONArray value = jsonObject.getJSONArray(j);
			String programPointName = value.getString(0);
			String lineNo = value.getString(1);
			ProgramPoint prog = addProgramPoint(programPointName, lineNo);

			/* output all the values */
			result.append(prog.getTraceRecord(value.getJSONArray(2)));

		}

		return result.toString();
	}

}
