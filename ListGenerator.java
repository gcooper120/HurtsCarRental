import java.sql.*;
public class ListGenerator {
    static String name = "";
    static String password = "";
  public static void main(String[] args) {
    try {
      Class.forName ("oracle.jdbc.driver.OracleDriver");
    } catch(Exception e){e.printStackTrace();}    
    try (
         Connection con=DriverManager.getConnection("jdbc:oracle:thin:@edgar0.cse.lehigh.edu:1521:cse241", name, password);
         Statement s=con.createStatement();
         ) {
           String q;
           ResultSet result;
           q = "select distinct Name from instructor";
           result = s.executeQuery(q);
           if (!result.next()) System.out.println ("Empty result.");
           else {
             do {
                 System.out.println (result.getString("Name"));
             } while (result.next());
           }
         } catch(Exception e){e.printStackTrace();}
  }
}
