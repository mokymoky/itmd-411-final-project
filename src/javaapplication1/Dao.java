package javaapplication1;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() {

	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		final String createTicketsTable = "CREATE TABLE nahn1_tickets(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200), start_date VARCHAR(200), end_date VARCHAR(200), status VARCHAR(200))";
		final String createUsersTable = "CREATE TABLE nahn_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";

		try {

			// execute queries to create tables

			statement = getConnection().createStatement();
			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);
			System.out.println("Created tables in given database...");

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// add users to user table
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {

				sql = "insert into nahn_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int insertRecords(String ticketName, String ticketDesc) {
		//add user and status into table
		int id = 0;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate("INSERT INTO nahn1_tickets(ticket_issuer, ticket_description, start_date, status) VALUES( '" + ticketName + "','" + ticketDesc + "',current_timestamp(),'Active')", Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;

	}

	public ResultSet readRecords() {
		//View all records
		ResultSet results = null;
		try {
			statement = getConnection().createStatement();
			results = statement.executeQuery("SELECT * FROM nahn1_tickets");
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}

	public void closeRecords(int ticketNum) {
		try {
			//close ticket of choosing
			statement = getConnection().createStatement();

			//Sets status to close ticket, and adds timestamp to end date
			String sql = "UPDATE nahn1_tickets SET status = 'Closed', end_date = current_timestamp() WHERE ticket_id = " + ticketNum + ";" ;
			statement.executeUpdate(sql);
			System.out.println("Ticket ID: " + ticketNum + " has been closed...");

			statement.close();
			connect.close();
		}
		catch (SQLException se) {
			se.printStackTrace();
			System.out.println("There was a problem closing the record");
			System.out.println(se.getMessage());
		}
	}

	// continue coding for updateRecords implementation
	public void updateRecords(int tid, String ticketDesc) throws SQLException {
		//Updates ticket of choice
		statement = connect.createStatement();

		//asks which ticket user, then what description to give
		String sql = "UPDATE nahn1_tickets SET ticket_description = '" + ticketDesc + "' WHERE ticket_id = '" + tid +"'";
		statement.executeUpdate(sql);
		JOptionPane.showMessageDialog(null, "Update successful.");
	}


	// continue coding for deleteRecords implementation
	public int deleteRecords(int tid) {

		try {
			statement = getConnection().createStatement();

			//deletes ticket of choosing
			String sql = "DELETE FROM nahn1_tickets WHERE ticket_id = " + tid;
			statement.executeUpdate(sql);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return tid;


	}
}