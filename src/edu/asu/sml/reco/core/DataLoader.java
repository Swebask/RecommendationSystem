package edu.asu.sml.reco.core;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;

public class DataLoader {

	
	public static Connection openConnection() {
		 Connection c = null;
	      try {
	         Class.forName("org.postgresql.Driver");
	         c = DriverManager
	            .getConnection("jdbc:postgresql://localhost:5432/testdb",
	            "postgres", "postgres");
	         
	      } catch (Exception e) {
	         e.printStackTrace();
	         System.err.println(e.getClass().getName()+": "+e.getMessage());
	         System.exit(0);
	      }
	      System.out.println("Opened database successfully");
	      return c;
	}
	
	public static void createTable(Connection dbConnection) throws SQLException {
		Statement stmt = dbConnection.createStatement();
        String sql = "CREATE TABLE USERS " +
                     "(ID INT PRIMARY KEY     NOT NULL," +
                     " USERID          TEXT    NOT NULL, " +
                     " PRODUCTID       TEXT     NOT NULL, " +
                     " REVIEWID        TEXT    NOT NULL)";
        stmt.executeUpdate(sql);
        stmt.close();
        
        stmt = dbConnection.createStatement();
        sql = "CREATE TABLE PRODUCT " +
        		"(ID INT PRIMARY KEY     NOT NULL," +
                " PRODUCTID          TEXT    NOT NULL, " +
                " USERID       TEXT     NOT NULL, " +
                " REVIEWID        TEXT    NOT NULL)";
        stmt.executeUpdate(sql);
        stmt.close();
        
        stmt = dbConnection.createStatement();
        sql = "CREATE TABLE REVIEW " +
        		"(ID INT PRIMARY KEY     NOT NULL," +
                " PRODUCTID          TEXT    NOT NULL, " +
                "TITLE	TEXT, "+
                " PRICE       REAL, " +
                " USERID        TEXT    NOT NULL,"+ 
                "NAME	TEXT, "+
                "HELPFULNESS		TEXT,"+
                 "SCORE		REAL,"
                 + "SUMMARY		TEXT,"
                 + "REVIEW		TEXT)";
        stmt.executeUpdate(sql);
        stmt.close();
        System.out.println("Tables created successfully");
	}
	
	private static String getValueFromKVPair(String line){
		String[] keyValue = line.split(":");
		return keyValue[1].trim();
	}
	
	public static void parseDataFileAndInsertInDB(String fileName, Connection dbConnection) {
		InputStream gzipStream;
		try {
			gzipStream = new GZIPInputStream(new FileInputStream(fileName));
			BufferedReader buffered = new BufferedReader(new InputStreamReader(gzipStream));
			
			String line=null;
			int reviewId =1;
			while((line= buffered.readLine())!=null) {
				String productId = line;
				String title, userID, profileName,helpfulness, reviewTime, summary, text;
				double price=-1, score=-1;
				//while(line != null && !line.equals("")) {
					title = getValueFromKVPair(buffered.readLine());
					String priceS = getValueFromKVPair(buffered.readLine());
					if(!priceS.equals("unknown"))
						price = Double.parseDouble(priceS);
					userID = getValueFromKVPair(buffered.readLine());
					profileName = getValueFromKVPair(buffered.readLine());
					helpfulness = getValueFromKVPair(buffered.readLine());
					String scoreS = getValueFromKVPair(buffered.readLine());
					if(!scoreS.equals("unknown"))
						score = Double.parseDouble(scoreS);
					reviewTime = getValueFromKVPair(buffered.readLine());
					summary = getValueFromKVPair(buffered.readLine());
					text = getValueFromKVPair(buffered.readLine());
				//}
					if(reviewId > 100000)
						break;
					else{
						if(reviewId%1000==0)
							System.out.println("Review "+reviewId +"processed..");
					}
				PreparedStatement preparedStatement = null;
				
				String stm = "INSERT INTO review(id, productid,title,price,userid,name,helpfulness,score,summary,review) "
						+ "VALUES(?, ?,?, ?,?,?,?, ?,?, ?)";
				preparedStatement = dbConnection.prepareStatement(stm);
				int index =1;
				preparedStatement.setInt(index++, reviewId);
				preparedStatement.setString(index++, productId);
				preparedStatement.setString(index++, title);
				preparedStatement.setDouble(index++, price);
				preparedStatement.setString(index++, userID);
				preparedStatement.setString(index++, profileName);
				preparedStatement.setString(index++, helpfulness);
				preparedStatement.setDouble(index++, score);
				preparedStatement.setString(index++, summary);
				preparedStatement.setString(index++, text);
				preparedStatement.executeUpdate();
				
				stm = "INSERT INTO product(id,productid,userid,reviewid) "
						+ "VALUES(?, ?,?, ?)";
				preparedStatement = dbConnection.prepareStatement(stm);
				preparedStatement.setInt(1, reviewId);
				preparedStatement.setString(2, productId);  
				preparedStatement.setString(3, userID);
				preparedStatement.setString(4, ""+reviewId);
				preparedStatement.executeUpdate();
				
				stm = "INSERT INTO users(id,userid,productid,reviewid) "
						+ "VALUES(?, ?,?, ?)";
				preparedStatement = dbConnection.prepareStatement(stm);
				preparedStatement.setInt(1, reviewId);
				preparedStatement.setString(2, userID);
				preparedStatement.setString(3, productId);  
				preparedStatement.setString(4, ""+reviewId);
				preparedStatement.executeUpdate();
				buffered.readLine();
				reviewId++;
				
				
			}
			buffered.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		Connection dBConnection = openConnection();
		try {
			createTable(dBConnection);
			parseDataFileAndInsertInDB("/windows/drive2/ASU Spring 2015/"
					+ "Statistical_Machine_Learning/Music.txt.gz", dBConnection);
			dBConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
