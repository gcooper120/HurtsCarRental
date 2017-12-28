import java.sql.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
public class ReservesGenerator
{
    //Password Data
    static String name = "gcc219";
    static String password = "gct123!";
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
        List<Integer> listCar = new ArrayList<Integer>(200);
        List<Integer> listReservations = new ArrayList<Integer>(150);
        String qCar = "select Car_id from Car";
        String qReservations = "select Res_ID from Reservation";
        ResultSet result = s.executeQuery(qCar);
        while (result.next())
            {
                listCar.add(result.getInt(1));
            }
        result = s.executeQuery(qReservations);
        while (result.next())
            {
                listReservations.add(result.getInt(1));
            }
        int numReservations = listReservations.size();
        int numCars = listCar.size();
        int carid, resid;
        String insertRow = "Insert INTO "
            + "Reserves(Res_ID, car_ID) "
            + "Values(?,?)";
        PreparedStatement prepInsert = con.prepareStatement(insertRow);
        for (int i = 0; i < numReservations; i++)
            {
                carid = listCar.get(i);
                resid = listReservations.get(i);
                try {
                    prepInsert.setInt(1, resid);
                    prepInsert.setInt(2, carid);
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
