package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgresqlTest {

	public static void main(String[] args) {
		Connection con = null;
		
		String driver="org.postgresql.Driver";
		String url = "jdbc:postgresql://112.136.130.6:5432/logdata";
		String username = "hadoop";
		String passwd = "hadoop";
		
		try {
			Class.forName(driver);
		
			System.out.println("Driver Loading Complete");
			
			con = DriverManager.getConnection(url, username, passwd);
			System.out.println("Connection Complete");
			
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from test");
			
			if (rs.last()) {
				rs.next();				
			}
			System.out.println("select Complete");
			
			rs.close();
			stmt.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				}catch(SQLException eee) {
					
				}
			}
		}
	}
}
