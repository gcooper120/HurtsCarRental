import java.sql.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
/**
*This class is used to generate data for the contains table in the database.
*For each car in the database, it is assigned to a specific location's inventory
*/
public class ContainsGenerator
{
    //Password Data
    static String name = "";
    static String password = "";
    //Main Method
    public static void main(String[] args)
    {
        //Load jdbc driver
        try {
            Class.forName ("oracle.jdbc.driver.OracleDriver");
        } catch(Exception e){e.printStackTrace();}    
        
        //Attempt to run generator, just prints tack traces on errors.
        try (
             Connection con=DriverManager.getConnection("jdbc:oracle:thin:@edgar0.cse.lehigh.edu:1521:cse241",name,password);
             Statement s=con.createStatement();
             ) {
            String qStandard, qLuxury, qExotic, qLocations;
            ResultSet resultStandard;
            ResultSet resultLuxury, resultExotic, resultLocations;


            //Generating lists of the different car types
            List<Integer> listStandard = new ArrayList<Integer>();
            List<Integer> listLuxury = new ArrayList<Integer>();
            List<Integer> listExotic = new ArrayList<Integer>();
            List<String> listLocations = new ArrayList<String>();
            qStandard = "select Car_ID from car where class = 'Standard'";
            qLuxury = "select Car_ID from car where class = 'Luxury'";
            qExotic = "select Car_ID from car where class = 'Exotic'";
            qLocations = "select distinct Location_ID from location";
            resultStandard = s.executeQuery(qStandard);
            while (resultStandard.next())
                {
                    listStandard.add(Integer.parseInt(resultStandard.getString(1)));
                }
            resultLuxury = s.executeQuery(qLuxury);
            while (resultLuxury.next())
                {
                    listLuxury.add(Integer.parseInt(resultLuxury.getString(1)));
                }
            resultExotic = s.executeQuery(qExotic);
            while (resultExotic.next())
                {
                    listExotic.add(Integer.parseInt(resultExotic.getString(1)));
                }
            resultLocations = s.executeQuery(qLocations);
            while (resultLocations.next())
                {
                    listLocations.add(resultLocations.getString(1));

                }

            //Generates a random location/car combo
            Random generator = new Random();
            int randLoc, randCar;
            int locSize = listLocations.size();
            int numStandardCars = listStandard.size();
            int numLuxuryCars = listLuxury.size();
            int numExoticCars = listExotic.size();
            String insertRow = "INSERT INTO "
                + "Contains(Inventory_ID, Car_ID) Values"
                + "(?,?)";
            PreparedStatement prepInsert = con.prepareStatement(insertRow);
            String inventory_ID;

            //Assigns standard cars to their inventories
            for (int i = 0; i < numStandardCars; i++)
            {
                randLoc = generator.nextInt(locSize);
                inventory_ID = listLocations.get(randLoc) + "_S";
                try {
                    prepInsert.setString(1, inventory_ID);
                    prepInsert.setInt(2, listStandard.get(i));
                    prepInsert.executeUpdate();
                } catch (SQLIntegrityConstraintViolationException SQLe){}
            }
            //Assigns luxury cars to their inventories
            for (int i = 0; i < numLuxuryCars; i++)
            {
                System.out.println(i);
                randLoc = generator.nextInt(locSize);
                inventory_ID = listLocations.get(randLoc) + "_L";
                try {
                    prepInsert.setString(1, inventory_ID);
                    prepInsert.setInt(2, listLuxury.get(i));
                    prepInsert.executeUpdate();
                } catch (SQLIntegrityConstraintViolationException SQLe){}
            }
            //Assigns exotic cars to their locations
            for (int i = 0; i < numExoticCars; i++)
            {
                randLoc = generator.nextInt(locSize);
                inventory_ID = listLocations.get(randLoc) + "_E";
                try {
                    prepInsert.setString(1, inventory_ID);
                    prepInsert.setInt(2, listExotic.get(i));
                    prepInsert.executeUpdate();
                } catch (SQLIntegrityConstraintViolationException SQLe){}
            }
            con.close();
            prepInsert.close();
        } catch(Exception e){e.printStackTrace();}

    }
}
