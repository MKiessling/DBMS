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
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * This is a terminal Database-Management-System which allows to easily create,
 * alter, drop tables and edit their content.
 * 
 * @author Martin Kiessling
 */
public class DBMS {

	/* variables */
	private SQLConnection dbcon;
	private String usr;
	private String url;
	private String pwd;
	private String[] tableHeader;
	private String[] dataTypes;
	private String tableName;
	private boolean isConnected;
	private Scanner scn;

	/**
	 * constructor of DBMS
	 */
	public DBMS() {
		dbInfo();
		isConnected = false;
		tableName = "no table selected";
		scn = new Scanner(System.in);
	}

	/**
	 * saves the parameters to connect to a host.
	 */
	private void getCustomHost() {
		System.out.println();
		System.out.print("Host: ");
		url = "jdbc:mysql://" + scn.nextLine();
		System.out.print("Port: ");
		url = url + ":" + scn.nextLine();
		System.out.print("Database: ");
		url = url + "/" + scn.nextLine();
		System.out.print("Username: ");
		usr = scn.nextLine();
		System.out.print("Password: ");
		pwd = scn.nextLine();
	}

	/**
	 * establishes database-connection
	 */
	private void establishDBConnection() {
		try {
			dbcon = new SQLConnection(usr, pwd, url);
			if (dbcon.isConnected()) {
				isConnected = true;
				System.out.println("\n>>>> Connection established <<<<\n");
			}
		} catch (Exception e) {
			System.err
					.println("\n>>>> Could not establish DB-Connection! <<<<\n");
		}
	}

	/**
	 * receives the table-information
	 */
	private void getTableInformation(String tn) throws SQLException {
		tableHeader = dbcon.returnTableHeader(tn, tableHeader);
		dataTypes = dbcon.returnDataTypes(tn, dataTypes);
	}

	/**
	 * shows program-head
	 */
	private void dbInfo() {
		System.out.println("*********************************");
		System.out.println("*         Terminal DBMS         *");
		System.out.println("*        Martin Kiessling       *");
		System.out.println("*********************************");
	}

	/**
	 * main-menu
	 */
	private void mainMenu() {
		if (!isConnected) {
			System.out.println();
			System.out.println("Main Menu");
			System.out.println("-------------------------------");
			System.out.println("       Connect to host -- > 1");
			System.out.println("-------------------------------");
			System.out.println("                  Exit -- > 3");
			System.out.println();
			System.out.print("Your choice: ");
			switch (new Scanner(System.in).next()) {
			case "1":
				getCustomHost();
				establishDBConnection();
				break;
			case "3":
				System.exit(0);
				break;
			default:
				break;
			}
			mainMenu();
		} else {
			System.out.println("Database Menu");
			System.out.println("--------------------");
			System.out.println("Common activities:");
			System.out.println("  All tables -- > 0");
			System.out.println("Create Table -- > 1");
			System.out.println("Change Table -- > 2");
			if (tableName != "no table selected") {
				System.out.println();
				System.out.println("Table Menu");
				System.out.println("--------------------");
				System.out.println("Actual Table: " + tableName);
				System.out.println();
				System.out.println("Table activities:");
				System.out.println("       Query -- > 3");
				System.out.println("      Insert -- > 4");
				System.out.println("      Update -- > 5");
				System.out.println("      Delete -- > 6");
				System.out.println("        Drop -- > 7");
			}
			System.out.println("--------------------");
			System.out.println("Close Connection -- > 8");
			System.out.println();
			System.out.print("Your choice: ");
			switch (new Scanner(System.in).next()) {
			case "0":
				displayAllTables();
				break;
			case "1":
				createTable();
				break;
			case "2":
				changeTable();
				break;
			case "3":
				if (tableName != "no table selected")
					selectTable();
				break;
			case "4":
				if (tableName != "no table selected")
					insertTable();
				break;
			case "5":
				if (tableName != "no table selected")
					updateTable();
				break;
			case "6":
				if (tableName != "no table selected")
					deleteTable();
				break;
			case "7":
				if (tableName != "no table selected")
					dropTable();
				break;
			case "8":
				closeDBConnection();
				break;
			default:
				System.out.println("Yet not possible");
			}
			mainMenu();
		}
	}

	/**
	 * assistent to create a table
	 */
	private void createTable() {
		System.out.println();
		System.out.println("********************************");
		System.out.println("*    create table assistent    *");
		System.out.println("********************************");
		System.out.println();
		System.out.print("Enter new table name: ");
		String newTableName = scn.nextLine();
		System.out
				.println("Enter columns with datatype in format divided by ',':");
		String newColumns = scn.nextLine();
		String query = "CREATE TABLE " + newTableName + " (" + newColumns + ")";
		try {
			System.out.println("Now executing: " + query);
			System.out.println("...executing...\n");
			dbcon.runQuery(query);
			System.out
					.println(">>>> Table " + newTableName + " created <<<<\n");
		} catch (Exception e) {
			System.err.println(">>>> Could not create table " + newTableName
					+ " - check input <<<<\n");
		}
	}

	/**
	 * assistent to run a custom query
	 */
	private void selectTable() {
		System.out.println();
		System.out.println("********************************");
		System.out.println("*    select table assistent    *");
		System.out.println("********************************");
		System.out.println();
		System.out.println("Query-Output will be presented as an HTML-Page!\n");
		System.out.println("Available columns:");
		for (int i = 0; i < tableHeader.length; i++) {
			System.out.print(tableHeader[i] + " ");
		}
		System.out
				.println("\nEnter columns you want to select divided by ',':");
		String query = "SELECT " + scn.nextLine() + " FROM " + tableName;
		System.out
				.println("Enter additional query parameters or just press 'Enter':");
		query = query + " " + scn.nextLine();
		try {
			System.out.println("Now executing: " + query);
			System.out.println("...executing...\n");
			new HTML(dbcon.returnQuery(query), dbcon.returnTempHeader(query))
					.createHTML(query);
			System.out.println(">>>> Output created <<<<\n");
		} catch (SQLException e) {
			System.err.println(">>>> Could not execute query <<<<\n");
		} catch (IOException e) {
			System.err.println(">>>> Could not create output <<<<\n");
		} catch (Exception e) {
			System.err.println(">>>> An error occured <<<<\n");
		}
	}

	/**
	 * assistent to change the table
	 */
	private void changeTable() {
		System.out.println();
		System.out.println("********************************");
		System.out.println("*    change table assistent    *");
		System.out.println("********************************");
		System.out.println();
		System.out.print("Enter tablename: ");
		String tn = scn.nextLine();
		try {
			System.out.println("...executing...\n");
			getTableInformation(tn);
			tableName = tn;
			System.out.println(">>>> Table changed <<<<\n");
		} catch (SQLException e) {
			System.err.println(">>>> Could not change table! <<<<\n");
		}
	}

	/**
	 * assistent to update a table
	 */
	private void updateTable() {
		System.out.println();
		System.out.println("********************************");
		System.out.println("*    update table assistent    *");
		System.out.println("********************************");
		System.out.println();
		System.out.println("Available columns:");
		for (int i = 0; i < tableHeader.length; i++) {
			System.out.print(tableHeader[i] + " ");
		}
		System.out
				.println("\nEnter columns you want to update in format 'column=value':");
		String query = "UPDATE " + tableName + " SET " + scn.nextLine();
		System.out
				.println("\nEnter parameters which records should be updated or leave it blank (!Warning! every column will be updated):");
		query = query + scn.nextLine();
		try {
			System.out.println("Now executing: " + query);
			System.out.println("...executing...\n");
			dbcon.runQuery(query);
			System.out.println(">>>> Table " + tableName + " updated <<<<\n");
		} catch (Exception e) {
			System.err.println(">>>> Could not update table! <<<<\n");
		}
	}

	/**
	 * assistent to insert into a table
	 */
	private void insertTable() {
		System.out.println();
		System.out.println("********************************");
		System.out.println("*    insert table assistent    *");
		System.out.println("********************************");
		System.out.println();
		System.out.println("Explanation: Column[Datatype(Max_Length)]:");
		for (int i = 0; i < tableHeader.length; i++) {
			System.out.print(tableHeader[i] + "[" + dataTypes[i] + "]  ");
		}
		System.out.println();
		System.out.println();
		System.out
				.println("Please type in the columns you want to insert into divided by ',':");
		String insertColumns = scn.nextLine();
		System.out
				.println("Please type in the values you want to insert into divided by ',' according to your columns:");
		String insertValues = scn.nextLine();
		String query = ("INSERT INTO " + tableName + " (" + insertColumns
				+ ") VALUES (" + insertValues + ")");
		try {
			System.out.println("Now executing: " + query);
			System.out.println("...executing...\n");
			dbcon.runQuery(query);
			System.out.println(">>>> Insert executed <<<\n");
		} catch (Exception e) {
			System.err.print(">>>> Insert not executed! <<<<\n");
		}
	}

	/**
	 * assistent to delete content of a table
	 */
	private void deleteTable() {
		System.out.println();
		System.out.println("********************************");
		System.out.println("*    delete table assistent    *");
		System.out.println("********************************");
		System.out.println();
		System.out
				.println("Enter parameters for deletion or nothing for a full wipe:");
		String query = "DELETE FROM " + tableName + " " + scn.nextLine();
		;
		try {
			System.out.println("Now executing: " + query);
			System.out.println("...executing...\n");
			dbcon.runQuery(query);
			System.out.println(">>>> Records deleted <<<<\n");
		} catch (Exception e) {
			System.err.println(">>>> Could not delete records <<<<\n");
		}
	}

	/**
	 * assistent to drop a table
	 */
	private void dropTable() {
		System.out.println();
		System.out.println("********************************");
		System.out.println("*     drop table assistent     *");
		System.out.println("********************************");
		System.out.println();
		System.out.print("Do you really want to drop table " + tableName
				+ "? (y/n) --> ");
		String query = "DROP TABLE " + tableName;
		switch (scn.next()) {
		case "y":
			try {
				System.out.println("Now executing: " + query);
				System.out.println("...executing...\n");
				dbcon.runQuery(query);
				System.out.println(">>>> Table " + tableName
						+ " dropped <<<<\n");
				tableName = "no table selected";
			} catch (Exception e) {
				System.err.println(">>>> Could not drop table <<<<\n");
			}
			break;
		default:
			System.out.println(">>>> Table not dropped! <<<<\n");
		}
	}

	/**
	 * assistent to display all tables of selected db
	 */
	private void displayAllTables() {
		System.out.println();
		System.out.println("********************************");
		System.out.println("*      display all tables      *");
		System.out.println("********************************");
		System.out.println();
		String tempUrl = url.substring(14);
		String query = "select table_name from information_schema.tables where table_schema = "
				+ "'" + tempUrl.substring(tempUrl.indexOf('/') + 1) + "'";
		ResultSet allTables;
		try {
			allTables = dbcon.returnQuery(query);
			System.out.println("All tables in " + url);
			while (allTables.next()) {
				System.out.println(allTables.getString(1));
			}
			System.out.println();
		} catch (SQLException e) {
			System.err.println(">>>> Can not show all tables! <<<<\n");
		}
	}

	/**
	 * closes the database-connection
	 */
	private void closeDBConnection() {
		try {
			dbcon.closeConnection();
			isConnected = false;
			System.out.println("\n>>>> Connection closed <<<<");
		} catch (SQLException e) {
			System.err.println("\n>>>> Could not close Connection <<<<");
		}
		mainMenu();
	}

	/**
	 * main-method
	 */
	public static void main(String[] args) {
		DBMS dbin = new DBMS();
		dbin.mainMenu();
	}
}
