import java.sql.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.*;

/**
*This class is used to generate customers for the database
*Takes 4 file arguments, name list, last name list, city list, street name list
*/
public class CustomerGenerator
{
    //Password Data
    static String name = "";
    static String password = "";
    //Main Method
    public static void main(String[] args)
    {
        //Hardcoded arrays
        String stateList[] = {"AK","AL","AR","AZ","CA","CO","CT","DC","DE","FL","GA","GU","HI","IA","ID", "IL","IN","KS","KY","LA","MA","MD","ME","MH","MI","MN","MO","MS","MT","NC","ND","NE","NH","NJ","NM","NV","NY", "OH","OK","OR","PA","PR","PW","RI","SC","SD","TN","TX","UT","VA","VI","VT","WA","WI","WV","WY"};
        char[] middleNames = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        //Generating other arrays
        File nameFile = null;
        File lastNameFile = null;
        File cityFile = null;
        File streetNameFile = null;
        try
            {
        nameFile = new File(args[0]);
        lastNameFile = new File(args[1]);
        cityFile = new File(args[2]);
        streetNameFile = new File(args[3]);
            }
        catch (Exception e)
            {
                System.out.println("Format: CustomerGenerator firstName.txt lastName.txt city.txt street.txt");
                System.exit(0);
            }
        Scanner firstScanner, lastScanner, cityScanner, streetNameScanner;
        String[] firstNames = new String[20];
        String[] lastNames = new String[20];
        String[] cityNames = new String[385];
        String[] streetNames = new String[20];
         try
             {
                firstScanner = new Scanner(nameFile);
                lastScanner = new Scanner(lastNameFile);
                cityScanner = new Scanner(cityFile);
                streetNameScanner = new Scanner(streetNameFile);
                for (int i = 0; i < 20; i++)
                    {
                        firstNames[i] = firstScanner.nextLine();
                        lastNames[i] = lastScanner.nextLine();
                        streetNames[i] = streetNameScanner.nextLine();
                    }
                for (int j = 0; j < 385; j++)
                    {
                        cityNames[j] = cityScanner.nextLine();
                    }
                firstScanner.close();
                lastScanner.close();
                cityScanner.close();
                streetNameScanner.close();
                //Populating Database
                try {
                    Class.forName ("oracle.jdbc.driver.OracleDriver");
                } catch(Exception e){e.printStackTrace();}    
                try (
                     Connection con=DriverManager.getConnection("jdbc:oracle:thin:@edgar0.cse.lehigh.edu:1521:cse241", name, password);
                     Statement s=con.createStatement();
                     )
                    {
                        String q;
                        ResultSet result;
                        int i;
                        Random generator = new Random();
                        int License, firstIndex, middleIndex, lastIndex, streetNumber, streetNameIndex, cityIndex, stateIndex, zip;
                        
                        for (int k = 0; k < 100; k++)
                            {
                                License = generator.nextInt(999999999) + 1;
                                firstIndex = generator.nextInt(20);
                                middleIndex = generator.nextInt(26);
                                lastIndex = generator.nextInt(20);
                                streetNumber = generator.nextInt(99999) + 1;
                                streetNameIndex = generator.nextInt(20);
                                cityIndex = generator.nextInt(385);
                                stateIndex = generator.nextInt(50);
                                zip = generator.nextInt(89999) + 10001;
                                q = "insert into Customer Values(" + License + ",'" + firstNames[firstIndex] + "','" + middleNames[middleIndex] + "','" + lastNames[lastIndex] + "'," + streetNumber + ",'" + streetNames[streetNameIndex] + "','" + cityNames[cityIndex] + "','" + stateList[stateIndex] + "'," + zip + ")";
                                i = s.executeUpdate(q);
                            }
                    } catch (Exception e){e.printStackTrace();} 
             }
         catch (FileNotFoundException ex){}
    }
}
