/*******************************************************************************
 * Copyright (c) 2012 Martin Kiessling
 * This source is subject to the BragiSoft Permissive License.
 * Please see the License.txt file for more information.
 * All other rights reserved.
 * 
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY 
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 *******************************************************************************/
import java.awt.Desktop;
import java.io.*;
import java.sql.ResultSet;

/**
 * This class creates an HTML-Output out of the given SQL-Query
 * 
 * @author Martin Kiessling
 */
public class HTML {
	
	/* variables */
	private String[] tableHeader;
	private ResultSet rs;
	private BufferedWriter bw;
	private long timestamp;
	private File out;
	private String s;

	/**
	 * constructor of HTML
	 */
	public HTML(ResultSet rs, String[] th) throws IOException {
		this.rs = rs;
		this.timestamp = (System.currentTimeMillis() / 1000);
		this.tableHeader = th;
		out = new File("out_" + timestamp + ".htm");
		bw = new BufferedWriter(new FileWriter(out));
	}

	/**
	 * creates the html-output and opens it with standard-browser
	 */
	@SuppressWarnings("deprecation")
	public void createHTML(String query) throws Exception {
		bw.write("<meta http-equiv=Content-Type content=text/html;"
				+ " charset=UTF-8/>");
		bw.write("<html>");
		bw.write("<body>");
		bw.write("<h1>Query-Result " + timestamp + "</h1>");
		bw.write("</br>");
		bw.write(query);
		bw.write("</br>");
		bw.write("<table width=100% border=0 cellspacing=10>");
		bw.write("<tr>");
		for (int i = 0; i < tableHeader.length; i++) {
			bw.write("<th align=left>");
			bw.write(tableHeader[i]);
			bw.write("</th>");
		}
		bw.write("</tr>");
		while (rs.next()) {
			bw.write("<tr>");
			for (int i = 1; i < tableHeader.length + 1; i++) {
				bw.write("<td>");
				if ((s = rs.getString(i)) != null) {
					bw.write(s);
				} else {
					bw.write("");
				}
				bw.write("</td>");
			}
			bw.write("</tr>");
		}
		bw.write("</table>");
		bw.write("</br><p>");
		bw.write("Date created: " + new java.util.Date().toGMTString());
		bw.write("</p>");
		bw.write("</body>");
		bw.write("</html>");
		bw.close();
		Desktop.getDesktop().browse(out.toURI());
	}
}
