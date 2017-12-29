import java.sql.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
public class RentsGenerator
{
    //Password Data
    static String name = "";
    static String password = "";
    //Main Method
    public static void main(String[] args)
    {
        Random generator = new Random();
        try {
            Class.forName ("oracle.jdbc.driver.OracleDriver");
        } catch(Exception e){e.printStackTrace();}    
        try (
             Connection con=DriverManager.getConnection("jdbc:oracle:thin:@edgar0.cse.lehigh.edu:1521:cse241",name,password);
             Statement s=con.createStatement();
             ) {
        List<String> listCustomer = new ArrayList<String>(200);
        List<Integer> listReservations = new ArrayList<Integer>(200);
        String qCustomer = "select License from Customer";
        String qReservations = "select Res_ID from Reservation";
        ResultSet result = s.executeQuery(qCustomer);
        while (result.next())
            {
                listCustomer.add(result.getString(1));
            }
        result = s.executeQuery(qReservations);
        while (result.next())
            {
                listReservations.add(result.getInt(1));
            }
        int numReservations = listReservations.size();
        int numCustomers = listCustomer.size();
        int resid;
        String license;
        String insertRow = "Insert INTO "
            + "Rents(License, res_ID) "
            + "Values(?,?)";
        PreparedStatement prepInsert = con.prepareStatement(insertRow);
        for (int i = 0; i < numReservations; i++)
            {
                license = listCustomer.get(generator.nextInt(numCustomers));
                resid = listReservations.get(i);
                try {
                    prepInsert.setString(1, license);
                    prepInsert.setInt(2, resid);
                    prepInsert.executeQuery();
                } catch (SQLIntegrityConstraintViolationException SQLe){System.out.println("NOPE");}

            }
        con.close();
        prepInsert.close();
        s.close();
        result.close();
        } catch (Exception e){e.printStackTrace();}

    }
}
