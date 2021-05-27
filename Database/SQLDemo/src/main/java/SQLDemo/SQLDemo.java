package SQLDemo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SQLDemo {

	public static String CONN_URL = "jdbc:sqlite:C:/sqlite/db/test.db"; // One unified place for the connection url
	public static Connection conn; // save the connection object so that we can use it and not create it every time
	
	public static Connection createNewDatabase() {    
		
		File directory = new File("C:/sqlite/db");
		if(!directory.exists())
		{
			directory.mkdirs();
		}
		
	    try{
	    	Connection conn = DriverManager.getConnection(CONN_URL);
	        if (conn != null) {
	        	System.out.println(conn.getAutoCommit());
	            return conn;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	    return null;
	}
	
	public static void createTables() {
	    
	    // putting the SQL as a string in java
	    String salesrepSQL = "CREATE TABLE IF NOT EXISTS salesrep " +
	        				"( salesrep_id integer PRIMARY KEY, sr_fname text, sr_lname text, sr_commission real);";
	    String invoiceSQL = "CREATE TABLE IF NOT EXISTS invoice " +
	        				"( invc_id integer PRIMARY KEY, invc_number text, invc_sr_id integer, invc_amount real);";
	    
	    try (Statement stmt = conn.createStatement();
	         Statement stmt2 = conn.createStatement()) {
	            // create a new table
	            stmt.execute(salesrepSQL);
	        	stmt2.execute(invoiceSQL);
	    } catch (SQLException e) {
	    	System.out.println("Error:\n"+e.getMessage());
	    }
	}
	
	public static void addSalesRep(String fname, String lname, double commission) {    
	    // SQL where all fields are replaced with '?'
	    String sql = "INSERT INTO salesrep (sr_fname,sr_lname,sr_commission) VALUES (?,?,?)";
	        
	    try(PreparedStatement  pstmt = conn.prepareStatement(sql)){ // Create a prepared statement){
	        pstmt.setString(1, fname); // Set the first paramater (or '?') to fname
	        pstmt.setString(2, lname); // Set the second parameter to lname
	        pstmt.setDouble(3, commission); // Set the third parameter to commission
	        pstmt.executeUpdate(); // execute the prepaired statement with the set parameters
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	private static void mainMenu() {
	
		System.out.println(" ___________________________________________ \n"
						 + "|               SQL Tutorial                |\n"
						 + "|             Select an option              |\n"
						 + "|-------------------------------------------|\n"
						 + "| 1. Add data from SQL                      |\n"
						 + "| 2. View data                              |\n"
						 + "| 3. SQL Direct (type sql and see results)  |\n"
						 + "| 4. Calculate Commision                    |\n"
						 + "| 5. Count Sales by salesrep                |\n"
						 + "| 6. Total sales                            |\n"
						 + "| 7. Exit                                   |\n"
						 + "|___________________________________________|\n");
		
	}
	
	public static void loadData() {
		
		try (Statement stmt = conn.createStatement();){
			String sql = new String(Files.readAllBytes(Paths.get("data.sql")));
			stmt.execute(sql);			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	public static void viewData(Scanner kb) {
		
		String menu = "\n"
				   + " ______________________________ \n"
				   + "|        Select an option      |\n"
				   + "|------------------------------|\n"
				   + "| 1. View Sales Reps           |\n"
				   + "| 2. View Invoices             |\n"
				   + "| 3. View Sales Reps WHERE     |\n"
				   + "| 4. View Invoces WHERE        |\n"
				   + "| 5. MainMenu                  |\n"
				   + "|------------------------------|\n"
				   + "| (Option 3 and 4 allow you to |\n"
				   + "| Type a where caluse in as a  |\n"
				   + "| Test to see what works and   |\n"
				   + "| what does not)               |\n"
				   + "|______________________________|\n";
		
		System.out.println(menu);
		
		int cmd = 0;
		do
		{
			cmd = kb.nextInt();
			switch(cmd)
			{
			case 1:
				viewSalesRep("WHERE true");
				break;
			case 2: 
				viewInvoice("WHERE true");
				break;
			case 3: 
				System.out.println("Enter WHERE Clause");
				viewSalesRep(kb.nextLine());
				break;
			case 4:
				System.out.println("Enter WHERE Clause");
				viewInvoice(kb.nextLine());
				break;
			case 5:
				break;
			}
			if(cmd != 5)
				System.out.println("Enter the next option");
		} while(cmd != 5);
	
	}
	
	public static void viewInvoice(String whereClause) {
		try (Statement stmt = conn.createStatement())
		{
			ResultSet rs = stmt.executeQuery("SELECT * FROM invoice "+ whereClause);
			System.out.println(" ___________________________________________________ \n"
					 		 + "| invc_id | invc_number | invc_sr_id | invc_amount  |\n"
					 		 + "|---------|-------------|------------|--------------|");
	
			
			while(rs.next()) {
				System.out.printf("| %7d | %11s | %10d | % 12.2f |\n", rs.getInt("invc_id"),rs.getInt("invc_number"),rs.getInt("invc_sr_id"),rs.getDouble("invc_amount"));
			}
			System.out.println("|_________|_____________|____________|______________|");
		} catch (SQLException e)
		{
			System.out.println("\nError:\n"+ e.getMessage()+"\n");
		}
		
	}

	public static void viewSalesRep(String whereClause) {
		try (Statement stmt = conn.createStatement())
		{
			ResultSet rs = stmt.executeQuery("SELECT * FROM salesrep "+ whereClause);
			System.out.println(" _________________________________________________________________ \n"
					 		 + "| salesrep_id |      sr_fname   |      sr_lname   | sr_commission |\n"
					 		 + "|-------------|-----------------|-----------------|---------------|");
	
			
			while(rs.next()) {
				System.out.printf("| %11d | %15s | %15s | % 13.4f |\n", rs.getInt("salesrep_id"),rs.getString("sr_fname"),rs.getString("sr_lname"),rs.getDouble("sr_commission"));
			}
			System.out.println("|_____________|_________________|_________________|_______________|");
		} catch (SQLException e)
		{
			System.out.println("\nError:\n"+ e.getMessage()+"\n");
		}
	}

	public static void executeSQL(String sql) {
		try (Statement stmt = conn.createStatement())
		{
			if(sql.contains("UPDATE") || sql.contains("INSERT") || sql.contains("DELETE") )
			{
				int rowEffect = stmt.executeUpdate(sql);
				System.out.println("SQL Executed: "+rowEffect+" rows Affected");
			}
			else
			{
				ResultSet rs = stmt.executeQuery(sql);
				ResultSetMetaData rsmd = rs.getMetaData();
				int columns = rsmd.getColumnCount();
				System.out.print("\n ");
				for(int y = 0; y < 3; y++)
				{
					for(int x = 1; x <= columns; x++)
					{
						if(y==0)
						{
							if(x!=columns)
								System.out.print("_________________");
							else
								System.out.print("________________ \n");
						}
						else if(y == 1)
						{
							if(x!=columns)
								System.out.printf("| %15s |", rsmd.getColumnName(columns));
							else
								System.out.printf("| %15s |\n", rsmd.getColumnName(columns));
						}
						else if(y == 2)
						{
							if(x!=columns)
								System.out.print("|-----------------|");
							else
								System.out.print("|-----------------|\n");
						}
					}
				}
				while(rs.next()) {
					for(int x = 1; x <= columns; x++)
					{
						System.out.printf("| %15s |", rs.getObject(x).toString());
					}
					System.out.println("");
				}
				
				for(int x = 1; x <= columns; x++)
				{
					System.out.print("|_________________|");
				}
				System.out.println();
				
			}
		} catch (SQLException e)
		{
			System.out.println("\nError:\n"+ e.getMessage()+"\n");
		}
				
	}
	
	public static void totalSales() {
		System.out.println("The SQL execute for this is\n"
				 + "SELECT SUM(invoice_amount) AS totalAmount FROM invoice");

		try (Statement stmt = conn.createStatement())
		{
			ResultSet rs = stmt.executeQuery("SELECT SUM(invoice_amount) AS totalAmount FROM invoice");
			System.out.println(" ________________ \n"
					 		 + "|   totalAmount  |\n"
					 		 + "|----------------|");
		
			
			while(rs.next()) {
				System.out.printf("| %14.2f |\n", rs.getDouble("totalAmount"));
			}
			System.out.println("|________________|");
		} catch (SQLException e)
		{
			System.out.println("\nError:\n"+ e.getMessage()+"\n");
		}
	}

	public static void countSales() {
		System.out.println("The SQL execute for this is\n"
				 + "SELECT salesrep_id,sr_fname,sr_lname,COUNT(incv_id) FROM salesrep JOIN invoice ON salesrep_id = invc_sr_id GROUP BY salesrep_id,sr_fname,sr_lname");

		try (Statement stmt = conn.createStatement())
		{
			ResultSet rs = stmt.executeQuery("SELECT salesrep_id,sr_fname,sr_lname,COUNT(incv_id) AS sales_made FROM salesrep JOIN invoice ON salesrep_id = invc_sr_id GROUP BY salesrep_id,sr_fname,sr_lname");
			System.out.println(" ______________________________________________________________ \n"
					 		 + "| salesrep_id |      sr_fname   |      sr_lname   | sales_made |\n"
					 		 + "|-------------|-----------------|-----------------|------------|");
		
			
			while(rs.next()) {
				System.out.printf("| %11d | %15s | %15s | % 10d |\n", rs.getInt("salesrep_id"),rs.getInt("sr_fname"),rs.getInt("sr_lname"),rs.getInt("sales_made"));
			}
			System.out.println("|_____________|_________________|_________________|____________|");
		} catch (SQLException e)
		{
			System.out.println("\nError:\n"+ e.getMessage()+"\n");
		}
	}

	public static void calculateCommision() {
		System.out.println("The SQL execute for this is\n"
				 + "SELECT salesrep_id,sr_fname,sr_lname,SUM(incv_amount)*sr_commission \n"
				 + "FROM salesrep JOIN invoice ON salesrep_id = invc_sr_id GROUP BY salesrep_id,sr_fname,sr_lname,sr_commission");
		
	}
	
	public static void main(String args[]) throws Exception
	{

		conn = createNewDatabase();
		createTables();
		Scanner kb = new Scanner(System.in);
		int cmd = 0;
		do
		{
			mainMenu();
			cmd = kb.nextInt();
			switch(cmd)
			{
			case 1:
				loadData();
				break;
			case 2:
				viewData(kb);
				break;
			case 3:
				System.out.println("\nType Your Statement all on one line then hit enter");
				kb.nextLine(); // Trash the line ending character for previus line
				String sql = kb.nextLine();
				executeSQL(sql);
				break;
			case 4:
				calculateCommision();
				break;
			case 5:
				countSales();
				break;
			case 6:
				totalSales();
				break;
			case 7:
				break;
			}
		}while(cmd != 7);
		
	}
}
