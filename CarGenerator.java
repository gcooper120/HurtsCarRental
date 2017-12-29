import java.sql.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
public class CarGenerator
{
    //Password Data
    static String name = "";
    static String password = "";
    //Main Method
    public static void main(String[] args)
    {
        String makeList[] = {"Tesla", "Ford", "Chevrolet", "Toyota", "BMW", "Lexus"};
        String modelList[] = {"Model S", "Model X", "Model 3", "Fusion", "Mustang","F150", "Cruise", "Camaro", "Corvette", "Camry", "Corolla", "RAV4", "325i", "X5", "i8", "S-Class", "GLE SUV", "C-Class"};
        String classList[] = {"Standard", "Luxury", "Exotic"};


        try {
            Class.forName ("oracle.jdbc.driver.OracleDriver");
        } catch(Exception e){e.printStackTrace();}    
        try (
             Connection con=DriverManager.getConnection("jdbc:oracle:thin:@edgar0.cse.lehigh.edu:1521:cse241", name, password);
             )
            {
                String q;
                int i;
                Random generator = new Random();
                int id, make, model, clas, odom;
                String insertRow = "INSERT INTO "
                    + "Car(Car_ID, Make, Model, Class, Odometer_Reading) Values"
                    + "(?,?,?,?,?)";
                PreparedStatement prepInsert = con.prepareStatement(insertRow);
                for (int j = 0; j < 200; j++)
                    {
                        prepInsert = con.prepareStatement(insertRow);
                        id = 100+j;
                        make = generator.nextInt(6);
                        clas =  generator.nextInt(3);
                        model = clas + make * 3;
                        odom = generator.nextInt(100000);

                        try {
                            prepInsert.setInt(1, id);
                            prepInsert.setString(2, makeList[make]);
                            prepInsert.setString(3, modelList[model]);
                            prepInsert.setString(4, classList[clas]);
                            prepInsert.setInt(5, odom);
                            prepInsert.executeUpdate();
                        } catch (SQLIntegrityConstraintViolationException SQLe){j--;}
                    }
                con.close();
                prepInsert.close();
            } catch (Exception e){e.printStackTrace();}
    }
}

     
