import java.sql.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.time.*;
import java.util.concurrent.ThreadLocalRandom;
public class ReservationGenerator
{
    //Password Data
    static String name = "gcc219";
    static String password = "gct123!";
    //Main Method
    public static void main(String[] args)
    {
        String classList[] = {"Standard", "Luxury", "Exotic"};
        LocalDate startDate = LocalDate.of(1990, 1, 1); //start date
        long start = startDate.toEpochDay();
        LocalDate endDate = LocalDate.now(); //end date
        long end = endDate.toEpochDay();
        long randomEpochDay;

        try {
            Class.forName ("oracle.jdbc.driver.OracleDriver");
        } catch(Exception e){e.printStackTrace();}    
        try (
             Connection con=DriverManager.getConnection("jdbc:oracle:thin:@edgar0.cse.lehigh.edu:1521:cse241", name, password);
             )
            {
                LocalDate earlier, later;
                long daysToAdd;
                int res_id, clas, pickup, dropoff, fuel, ins;
                Random generator = new Random();
                String insertRow = "INSERT INTO "
                    + "Reservation(Res_ID, Class, Pickup_id, Dropoff_ID, Start_Date, End_Date, fuel_used, hasinsurance) "
                    + "Values (?,?,?,?,?,?,?,?)";
                PreparedStatement prepInsert = con.prepareStatement(insertRow);
                List<String> listLocations = new ArrayList<String>();
                String qLocations;
                ResultSet resultLocations;
                Statement s = con.createStatement();
                qLocations = "select distinct Location_ID from location";
                resultLocations = s.executeQuery(qLocations);
                while (resultLocations.next())
                    {
                        listLocations.add(resultLocations.getString(1));

                    }

                for (int i = 0; i < 200; i++)
                    {
                        prepInsert = con.prepareStatement(insertRow);
                        res_id = 100000000 + i;
                        clas = generator.nextInt(3);
                        pickup = generator.nextInt(5);
                        dropoff = generator.nextInt(5);
                        randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();
                        earlier = LocalDate.ofEpochDay(randomEpochDay);
                        daysToAdd = (long)generator.nextInt(14) + 1;
                        later = earlier.plusDays(daysToAdd);
                        ins = generator.nextInt(2);
                        fuel = generator.nextInt(15);
                        try {
                            prepInsert.setInt(1, res_id);
                            prepInsert.setString(2, classList[clas]);
                            prepInsert.setString(3, listLocations.get(pickup));
                            prepInsert.setString(4, listLocations.get(dropoff));
                            prepInsert.setDate(5, java.sql.Date.valueOf(earlier));
                            prepInsert.setDate(6, java.sql.Date.valueOf(later));
                            prepInsert.setInt(7, fuel);
                            prepInsert.setInt(8, ins);
                            prepInsert.executeUpdate();
                        } catch (SQLIntegrityConstraintViolationException SQLe){i--;}
                    }
                con.close();
                prepInsert.close();
                s.close();
                resultLocations.close();
            } catch (Exception e){e.printStackTrace();}
    }
}
   
