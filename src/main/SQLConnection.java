package main;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class creates a connection to an SQL-database
 * 
 * @author Martin Kiessling
 */
public class SQLConnection {
	
	/* variables */
	private String username;
	private String password;
	private String driver = "com.mysql.jdbc.Driver";
	private String url;
	private Connection connection;

	/**
	 * constructor of SQLConnection
	 */
	public SQLConnection(String usr, String pwd, String url) throws Exception {
		this.username = usr;
		this.password = pwd;
		this.url = url;
		this.connectToDB();
	}

	/**
	 * creates connection to database
	 */
	public void connectToDB() throws Exception {
		Class.forName(this.driver);
		this.connection = DriverManager.getConnection(this.url, this.username,
				this.password);
	}

	/**
	 * closes connection to database
	 */
	public void closeConnection() throws SQLException {
		if (this.connection != null) {
				this.connection.close();
		}
	}

	/**
	 * tests if client is connected
	 */
	public boolean isConnected() {
		try {
			ResultSet rs = this.returnQuery("SELECT 1;");
			if (rs == null) {
				return false;
			}
			if (rs.next()) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * returns ResultSet of a query
	 */
	public ResultSet returnQuery(String query) throws SQLException {
		Statement stmt = this.connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}

	/**
	 * returns tableheader
	 */
	public String[] returnTableHeader(String tableName, String[] th)
			throws SQLException {
		String query = "select COLUMN_NAME from Information_schema.columns where table_name = '"
				+ tableName + "'";
		Statement stmt = this.connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		int i = 0;
		rs.last();
		th = new String[rs.getRow()];
		rs.first();
		do {
			th[i] = rs.getString(1);
			i++;
		} while (rs.next());
		return th;
	}

	/**
	 * returns a temp-header
	 */
	public String[] returnTempHeader(String query) throws SQLException {
		Statement stmt = this.connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		ResultSetMetaData rsmd = rs.getMetaData();
		String[] tempHeader = new String[rsmd.getColumnCount()];
		for (int i = 0; i < tempHeader.length; i++) {
			tempHeader[i] = rsmd.getColumnLabel(i + 1);

		}
		return tempHeader;
	}

	/**
	 * returns datatypes in table
	 */
	public String[] returnDataTypes(String tableName, String[] dt)
			throws SQLException {
		String query = "select concat(data_type,'(', character_maximum_length,')') from Information_schema.columns where table_name = '"
				+ tableName + "'";
		Statement stmt = this.connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		int i = 0;
		rs.last();
		dt = new String[rs.getRow()];
		rs.first();
		do {
			dt[i] = rs.getString(1);
			i++;
		} while (rs.next());
		return dt;
	}

	/**
	 * runs a query
	 */
	public boolean runQuery(String query) throws Exception {
		Statement stmt = this.connection.createStatement();
		return stmt.execute(query);
	}
}
