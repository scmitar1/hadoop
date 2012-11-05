package test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

public class ClearTable {
	private static final String PROP_PATH = "/properties/DBProperties.properties";
	private Logger log = Logger.getLogger(getClass().getName());
	
	public void init( String tableName ) throws IOException, ClassNotFoundException, SQLException {
		Properties prop = new Properties();
		prop.load(getClass().getResourceAsStream(PROP_PATH));
		String driver = prop.getProperty("driver");
		String url = prop.getProperty("url");
		String user = prop.getProperty("user");
		String password = prop.getProperty("password");
		String table = "\"" + tableName + "\"";
		
		Class.forName(driver);
		
		Connection con = null;
		
		try {
			con = DriverManager.getConnection(url, user, password);
			Statement stmt = con.createStatement();
			boolean ret = stmt.execute("delete from public." + table );
			
			if ( ret ) {
				log.info("delete is completed");
			}
			stmt.close();
		
		} finally {
			con.close();
			log.info("Connection is closed");
		}
	}
	
	public static void main ( String[] args ) throws IOException, ClassNotFoundException, SQLException {
		ClearTable table = new ClearTable();
		table.init("DayCountByIP");
	}
}
