package com.crawljax.plugins.jsmodify.executionTracer;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.crawljax.util.Helper;

/**
 * This class that reads an instrumentation array from the webbrowser and saves the contents in a
 * trace file.
 * 
 */
public class JSExecutionTracer {

	private static final int ONE_SEC = 1000;

	private static String outputFolder;

	private static JSONArray points = new JSONArray();

	public static final String EXECUTIONTRACEDIRECTORY = "executiontrace/";

	/**
	 * @param filename
	 *            How to name the file that will contain the assertions after execution.
	 */
	public JSExecutionTracer(String outFolder) {
		outputFolder = outFolder;
	}

	/**
	 * Retrieves the JavaScript instrumentation array from the webbrowser and writes its contents in
	 * the required format to a file.
	 * 
	 */

	public void retrieveJSInstrResult(WebDriver browser) {

		String filename = getOutputFolder() + EXECUTIONTRACEDIRECTORY + "jsexecutiontrace-";

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		filename += dateFormat.format(date) + ".txt";

		try {

			JavascriptExecutor js = (JavascriptExecutor) browser;
			js.executeScript("sendReally();");
			Thread.sleep(ONE_SEC);

			Trace trace = new Trace();

			PrintWriter file = new PrintWriter(filename);
			file.write(trace.parse(points));
			file.write('\n');
			file.close();
			points = new JSONArray();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a list with all trace files in the executiontracedirectory.
	 * 
	 * @return The list.
	 */
	public List<String> allTraceFiles() {
		ArrayList<String> result = new ArrayList<String>();

		/* find all trace files in the trace directory */
		File dir = new File(getOutputFolder() + EXECUTIONTRACEDIRECTORY);

		String[] files = dir.list();
		if (files == null) {
			return result;
		}
		for (String file : files) {
			if (file.endsWith(".txt")) {
				result.add(getOutputFolder() + EXECUTIONTRACEDIRECTORY + file);
			}
		}

		return result;
	}

	public String getOutputFolder() {
		return Helper.addFolderSlashIfNeeded(outputFolder);
	}

	public void setOutputFolder(String absolutePath) {
		outputFolder = absolutePath;
	}

	/**
	 * Dirty way to save program points from the proxy request threads. TODO: find cleaner way.
	 * 
	 * @param string
	 *            The JSON-text to save.
	 */
	public static void addPoint(String string) {
		JSONArray buffer = null;
		try {
			buffer = new JSONArray(string);
			for (int i = 0; i < buffer.length(); i++) {
				points.put(buffer.get(i));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
