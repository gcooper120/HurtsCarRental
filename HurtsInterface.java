import java.sql.*;
import java.util.*;
import java.io.*;
import java.text.*;
import java.time.*;

public class HurtsInterface
{
    static Connection con = null;
    public static void main(String[] args)
    {
        //Get DB log in information
        String name;
        String password;
        Scanner myScanner = new Scanner(System.in);
        boolean notConnected = true;



        try
            {
                //Connecting to Database
                while (notConnected)
                    {
                        System.out.println("Please enter your Oracle username");
                        name = myScanner.nextLine();
                        System.out.println("Please enter your Oracle password");
                        password = myScanner.nextLine();
                        try
                            {
                                Class.forName ("oracle.jdbc.driver.OracleDriver");
                            }
                        catch(Exception e){e.printStackTrace();}
                        try
                            {
                                con=DriverManager.getConnection("jdbc:oracle:thin:@edgar0.cse.lehigh.edu:1521:cse241", name, password);
                                notConnected = false;
                            } catch (Exception e){System.out.println("Incorrect username/password combination. Please try again.");}
                    }

                //Welcome message
                System.out.println("Welcome to Hurts Rent-a-Lemon!");
                System.out.println("Please type the number corresponding to whether you are a customer or manager .");
                //Determining user type
                int mType = 0;
                boolean repeatUserType = false;
                boolean repeatCustomer = false;
                boolean repeatManager = false;
                int input = 0;
                do
                    {
                        System.out.println("1: Customer");
                        System.out.println("2: Manager");
                        try
                            {
                                mType = myScanner.nextInt();
                            }
                        catch (Exception e){}
                        switch (mType)
                            {
                            case 1:
                                do
                                    {
                                        customer();
                                        System.out.println("If you would like to do something else, press 1.");
                                        input = 0;
                                        try
                                            {
                                                input = myScanner.nextInt();
                                            }
                                        catch (Exception e){}
                                        if (input == 1)
                                            repeatCustomer = true;
                                        else
                                            repeatCustomer = false;
                                    } while (repeatCustomer);
                                repeatUserType = false;
                                break;
                            case 2:
                                manager();
                                repeatUserType = false;
                                break;
                            default:
                                System.out.println("I'm sorry, please select one of the availiable options.");
                                repeatUserType = true;
                                myScanner.nextLine();
                                break;
                            }
                    } while (repeatUserType);

                //Closing connection to prevent deadlocks.
            }
        finally
            {
                try
                    {
                        con.close();
                    }
                catch(SQLException e)
                    {
                        System.out.println("Something went wrong.");
                    }
            }
        return;
    }





    /* This method loops through the customer interface and gives the user options to create, view and cancel reservations*/
    public static void customer()
    {
        int custAction=9999;
        Scanner custScan = new Scanner(System.in);
        boolean contSwitch;
        
        do
            {
                System.out.println("Please enter the number for what you would like to do.");
                System.out.println("1: Create Reservation");
                System.out.println("2: View Reservations");
                System.out.println("3: Cancel Reservation");
                try
                    {
                        custAction = custScan.nextInt();
                    }
                catch (Exception e){}
                contSwitch = false;
                switch (custAction)
                    {
                    case 1:
                        {
                            bookReservation();
                            break;
                        }
                    case 2:
                        {
                            viewReservations();
                            break;
                        }
                    case 3:
                        {
                            cancelReservation();
                            break;
                        }
                    default:
                        {
                            System.out.println("I'm sorry, please select one of the availiable options.");
                            contSwitch = true;
                            custScan.nextLine();
                            break;
                        }
                    }
            } while (contSwitch);
        
        return;
    }






    /* This method first verifies that a user is actually a manager and then displays the manager menu*/
    public static void manager()
    {
        Scanner manScan = new Scanner(System.in);
        String employeeID;
        String employeePass;
        boolean repeatVerification = false;
        int counter = 0;
        //Verifies that user is actually a manager
        do
            {
                System.out.println("Please enter your employee ID");
                employeeID = manScan.nextLine();
                System.out.println("Please enter your employee password");
                employeePass = manScan.nextLine();
                String callManVerification = "{call managerVerification(?,?)}";
                PreparedStatement mV = null;
                try
                    {
                        mV = con.prepareStatement(callManVerification);
                        mV.setString(1,employeeID);
                        mV.setString(2,employeePass);
                        mV.executeQuery();
                        repeatVerification = false;
                    }
                catch (SQLException e)
                    {
                        if (e.getErrorCode() == 20003)
                            {
                                System.out.println("Invalid Manager username/password combination");
                                counter++;
                                if (counter == 3)
                                    {
                                        System.out.println("Too many failed log in attempts...");
                                        System.exit(0);
                                    }
                            }
                    repeatVerification = true;
                    }
               
            } while(repeatVerification);
        int manAction = 0;
        boolean contSwitch;
        //Employee interface loop
        System.out.println("Employee Verified");
        do
            {
                System.out.println("Please enter the number for what you would like to do.");
                System.out.println("1: Return Car");
                System.out.println("2: Print Receipt for Customer");
                System.out.println("3: Apply Discount");
                System.out.println("4: Add Location");
                System.out.println("5: Add Car");
                try
                    {
                        manAction = manScan.nextInt();
                    }
                catch (Exception e){}
                contSwitch = false;
                switch (manAction)
                    {
                    case 1:
                        {
                            returnCar();
                            break;
                        }
                    case 2:
                        {
                            printReceipt();
                            break;
                        }
                    case 3:
                        {
                            applyDiscount();
                            break;
                        }
                    case 4:
                        {
                            addLocation();
                            break;
                        }
                    case 5:
                        {
                            addCar();
                            break;
                        }
                    default:
                        {
                            System.out.println("I'm sorry, please select one of the availiable options.");
                            contSwitch = true;
                            manScan.nextLine();
                            break;
                        }
                    }
                if (!contSwitch)
                    {
                        System.out.println("If you would like to do something else, press 1");
                        int input = 0;
                        try
                            {
                                input = manScan.nextInt();
                            }
                        catch (Exception e){}

                        if (input == 1)
                            contSwitch = true;
                    }
            } while (contSwitch);
        return;
    }





    /* This is a method that creates a reservation when the user is a customer*/
    public static void bookReservation()
    {
        Scanner brScan = new Scanner(System.in);
        int customerType = 7;
        boolean stop = false;
        String license = null;
        

        do
            {
                System.out.println("Please select one of the following options:");
                System.out.println("1: New Customer");
                System.out.println("2: Old Customer");
                try
                    {
                customerType = brScan.nextInt();
                    }
                catch (Exception e){}
                brScan.nextLine();


                //Create New Customer
                if (customerType == 1)
                    {
                        String first, middle, last, number,sName, city, state, zip;
                        System.out.println("Thank you for choosing Hurts, before we continue, we need to get some basic information about you.");
                        
                        System.out.println("What is your first name?");
                        first = brScan.nextLine();
                        System.out.println("What is your middle name?");
                        middle = brScan.nextLine();
                        System.out.println("What is your last name?");
                        last = brScan.nextLine();
                        System.out.println("What is your license number?");
                        license = brScan.nextLine();
                        System.out.println("What is your street number?");
                        number = brScan.nextLine();
                        System.out.println("What is your street name?");
                        sName = brScan.nextLine();
                        System.out.println("What city is that in?");
                        city = brScan.nextLine();
                        System.out.println("What state is that in?");
                        state = brScan.nextLine();
                        System.out.println("What is your zip code?");
                        zip = brScan.nextLine();
                        String insertRow = "Insert Into "
                            + "Customer(License, first_name, middle_name, last_name, street_number, street_name, city, state, zip) "
                            + "Values (?,?,?,?,?,?,?,?,?)";
                        PreparedStatement prepInsert = null;
                        try
                            {
                                prepInsert = con.prepareStatement(insertRow);
                                prepInsert.setString(1, license);
                                prepInsert.setString(2, first);
                                prepInsert.setString(3, middle);
                                prepInsert.setString(4, last);
                                prepInsert.setString(5, number);
                                prepInsert.setString(6, sName);
                                prepInsert.setString(7, city);
                                prepInsert.setString(8, state);
                                prepInsert.setString(9, zip);
                                prepInsert.executeUpdate();
                                stop = true;
                            } catch (SQLIntegrityConstraintViolationException e)
                            {
                                System.out.println("I'm sorry a customer with that license number already exists");
                                stop = false;
                            } catch (SQLException e)
                            {
                                System.out.println("Something went wrong, please try again");
                                //e.printStackTrace();
                                stop = false;
                            }
                        
                    }
                //Get Old Customer Number
                else if (customerType == 2)
                    {
                        System.out.println("What is your license number?");
                        license = brScan.nextLine();
                        stop = true;
                    }
                //Loop
                else
                    {
                        System.out.println("Please select one of the options");
                    }
            } while (!stop);

        System.out.println("Great, now we just need a little more information about your reservation.");
        int input = 5;
        String clas = null;
        String pid = null;
        String did = null;
        LocalDate sd = null;
        LocalDate ed = null;
        int days = 0;
        String fuel = null;
        int ins = 3;

        boolean repeat;
        String callMakeReservation = "{call makeReservation(?,?,?,?,?,?,?)}";
        PreparedStatement mkRes = null;
        try
            {
               mkRes = con.prepareStatement(callMakeReservation);
            }
        catch (SQLException e) {System.out.println("Error calling procedure");}

        //Get Desired Class
        do
            {
                System.out.println("Please Select your car type:");
                System.out.println("1: Standard");
                System.out.println("2: Luxury");
                System.out.println("3: Exotic");
                try
                    {
                input = brScan.nextInt();
                    }
                catch (Exception e){}
                switch (input)
                    {
                    case 1:
                        {
                            clas = "Standard";
                            repeat = false;
                            break;
                        }
                    case 2:
                        {
                            clas = "Luxury";
                            repeat = false;
                            break;
                        }
                    case 3:
                        {
                            clas = "Exotic";
                            repeat = false;
                            break;
                        }
                    default:
                        {
                    System.out.println("I'm sorry that's not a valid option.");
                    repeat = true;
                    brScan.nextLine();
                    break;
                        }
                    }
            } while (repeat);


        //Generate List of all locations
        List<String> listLocations = null;
        ResultSet resultLocations = null;
        Statement s = null;
        try
            {
                listLocations = new ArrayList<String>();
                String qLocations;
                s = con.createStatement();
                qLocations = "select distinct Location_ID from location";
                resultLocations = s.executeQuery(qLocations);
            
                while (resultLocations.next())
                    {
                        listLocations.add(resultLocations.getString(1));
                        
                    }
            } catch (SQLException e){}
       


        //Get Pickup Location
        do
            {
                input = 50000;
                System.out.println("Please Select your Pickup location");
                for (int i = 0; i < listLocations.size(); i++)
                    {
                        System.out.println(i+1 + ": " + listLocations.get(i));
                    }
                try
                    {
                        input = brScan.nextInt();
                    }
                catch (Exception e){}
                if (input < listLocations.size()+1 && input > 0)
                    {
                        pid = listLocations.get(input-1);
                        repeat = false;
                    }
                else
                    {
                        System.out.println("I'm sorry that is not a valid location");
                        repeat = true;
                        brScan.nextLine();
                    }
            } while (repeat);
        repeat = false;


        //Get Dropoff Location
        char yesOrNo;
        brScan.nextLine();
        do
            {
                input = 50000;
                System.out.println("Is your drop off location different? y/n");
                yesOrNo = brScan.nextLine().charAt(0);
                if (yesOrNo == 'n')
                    {
                        did = pid;
                        repeat = false;
                    }
                else if (yesOrNo == 'y')
                    {
                        do
                            {
                                System.out.println("Please Select your Dropoff location");
                                for (int i = 0; i < listLocations.size(); i++)
                                    {
                                        System.out.println(i+1 + ": " + listLocations.get(i));
                                    }
                                try
                                    {
                                        input = brScan.nextInt();
                                    }
                                catch (Exception e){};
                                if (input < listLocations.size()+1 && input > 0)
                                    {
                                        did = listLocations.get(input-1);
                                        repeat = false;
                                    }
                                else
                                    {
                                        System.out.println("I'm sorry that is not a valid location");
                                        repeat = true;
                                    }
                            } while (repeat);
                        repeat = false;
                    }
                else
                    {
                        System.out.println("Please type y or n.");
                            repeat = true;
                    }
            } while (repeat);
        repeat = false;


        //Get Start Date
        String sdUnparsed = null;
        System.out.println("What day would you like to pick your car up?");
        do {
            System.out.println("Please insert the date in the form: yyyy-MM-dd");
            sdUnparsed = brScan.nextLine();
            try
                {
                    sd = LocalDate.parse(sdUnparsed);
                    repeat = false;
                }
            catch (DateTimeException e)
                {
                    System.out.println("Invalid date");
                    repeat = true;
                }
        } while (repeat);
        repeat = false;
        //Get Reservation Length
        do
            {
                System.out.println("How many days would you like your reservation to be for?");
                try
                    {
                        days = brScan.nextInt();
                        repeat = false;
                        if (days > 14)
                            {
                                System.out.println("Reservations can be at most 14 days long");
                                repeat = true;
                            }
                    }
                catch (Exception e){repeat = true; brScan.nextLine();}
                    
            } while (repeat);
        repeat = false;


        //Get Insurance Preference
        brScan.nextLine();
        do
            {
                System.out.println("Would you like insurance? y/n");
                yesOrNo = brScan.nextLine().charAt(0);
                if (yesOrNo == 'y')
                    {
                        ins = 1;
                        repeat = false;
                    }
                else if (yesOrNo == 'n')
                    {
                        ins = 0;
                        repeat = false;
                    }
                else
                    {
                        System.out.println("Invalid option, please try again");
                        repeat = true;
                    }
            } while (repeat);



        //Calling the stored procedure to create reservation
        try
            {
                mkRes.setString(1, license);
                mkRes.setString(2, clas);
                mkRes.setString(3, pid);
                mkRes.setString(4, did);
                mkRes.setDate(5, java.sql.Date.valueOf(sd));
                mkRes.setInt(6, days);
                mkRes.setInt(7, ins);
                mkRes.executeUpdate();
            }
        catch (SQLIntegrityConstraintViolationException e)
            {
                System.out.println("Our records indicate that you are not a returning customer, please register as a new customer and try again");
            }
        catch (SQLException e)
            {
                if (e.getErrorCode() == 20002)
                    {
                        System.out.println("There are no more " + clas + " cars availiable at this location.");
                    }
                if (e.getErrorCode() == 20004)
                    {
                        System.out.println("You can not create a reservation that starts in the past.");
                    }
            }
       
        return;   

    }

    /* This method enables the customer to see their reservation history*/
    public static void viewReservations()
    {
        Scanner vrScan = new Scanner(System.in);
        String license;
        System.out.println("Please input your 9-character license number:");
        license = vrScan.nextLine();
        String getLicense = "Select * "
            + "from reservation natural join rents "
            + "where license = ?";
        PreparedStatement prepGetLicense = null;
        ResultSet reservations = null;
        String rid = null;
        String clas = null;
        String pid = null;
        String did = null;
        String sDate = null;
        String eDate = null;
        int ins = 0;
        //Printing Header
        System.out.println("Reservation List for customer " + license + ":");
        System.out.format("%10s %10s %10s %10s %20s %20s %10s\n", "Reservation", "Class", "Pickup", "DropOff", "Start", "End", "Insurance");
        System.out.println("-------------------------------------------------------------------------------------------------");
        try
            {
                //Executing Query
                prepGetLicense = con.prepareStatement(getLicense);
                prepGetLicense.setString(1, license);
                reservations = prepGetLicense.executeQuery();


                if (!reservations.next())
                    {
                        System.out.println("There are no reservations matching this license");
                    }
                else
                    {
                        do
                            {
                                //Storing query results and printing
                                rid = reservations.getString("res_id");
                                clas = reservations.getString("class");
                                pid = reservations.getString("pickup_id");
                                did = reservations.getString("dropoff_id");
                                sDate = reservations.getDate("start_date").toString();
                                eDate = reservations.getDate("end_date").toString();
                                ins = reservations.getInt("hasinsurance");
                                System.out.format("%10s %10s %10s %10s %20s %20s %10d\n", rid, clas, pid, did, sDate, eDate, ins);
                            } while (reservations.next());
                    }
            }
        catch (SQLException e){System.out.println("Oops, something went wrong!");}

        return;

    }
    /* This method cancels a reservation that was already created. It checks that a reservation is in the future and that the reservation actually exists*/
    public static void cancelReservation()
    {
        Scanner crScan = new Scanner(System.in);
        int rid = -1;
        String callCancelReservation = "{call cancelReservation(?)}";
        PreparedStatement canRes = null;
        boolean repeat = false;
        do
            {
                try
                    {
                        System.out.println("Please input the reservation number you would like to remove.");
                        rid = crScan.nextInt();
                        repeat = false;
                    }
                catch(Exception e)
                    {
                        System.out.println("Invalid reservation number.");
                        repeat = true;
                        crScan.nextLine();
                    }
            }while (repeat);
        try
            {
                canRes = con.prepareStatement(callCancelReservation);
                canRes.setInt(1, rid);
                canRes.executeUpdate();
            }
        catch(SQLException e)
            {
                if (e.getErrorCode() == 20001)
                    {
                        System.out.println("Can not cancel a reservation that began in the past.");
                    }
                else
                    {
                        System.out.println("That reservation does not exist.");
                    }
            }
        
        return;
    }

    /* This method is used when a customer returns a car */
    public static void returnCar()
    {
        Scanner rcScan = new Scanner(System.in);
        int rid = -1;
        int fuel = -1;
        int odo = -1;
        String callReturnCar = "{call returnCar(?)}";
        PreparedStatement retCar = null;
        String updateFuelUsed = "update reservation " +
            "set fuel_used = ? " +
            "where res_id = ?";
        PreparedStatement updateFuel = null;
        String updateMilesDriven = "update car " +
            "set odometer_reading = odometer_reading + ? " +
            "where car_id = ?";
        PreparedStatement updateMiles = null;
        String callCalculateCharges = "{call CalculateCharges(?)}";
        PreparedStatement calcCharges = null;
        boolean repeat = false;
        System.out.println("Please input the reservation ID you are returning");
        do
            {
                try
                    {
                        rid = rcScan.nextInt();
                        repeat = false;
                    }
                catch (Exception e)
                    {
                        System.out.println("Please enter a valid number for the reservation ID");
                        repeat = true;
                        rcScan.nextLine();
                    }
            }while(repeat);
        try
            {
                retCar = con.prepareStatement(callReturnCar);
                retCar.setInt(1, rid);
                retCar.executeUpdate();
            }
        catch (SQLException e)
            {
                if (e.getErrorCode() == 20005)
                    {
                        System.out.println("That car has already been returned.");
                        return;
                    }
                else if (e.getErrorCode() == 20007)
                    {
                        System.out.println("Reservation has not yet begun");
                        return;
                    }
                else if (e.getErrorCode() == 20006)
                    {
                        System.out.println("That reservation does not exist");
                        return;
                    }
                else
                    {
                        System.out.println("Oops, something went wrong");
                    }
            }
      
        System.out.println("How many gallons of fuel did the customer use?");
        do
            {
            try
                {
                    fuel = rcScan.nextInt();
                    if (fuel < 0)
                        {
                            throw new NumberFormatException();
                        }
                    repeat = false;
                }
            catch (Exception e)
                {
                    System.out.println("Please enter a valid number for the fuel used.");
                    repeat = true;
                }
            }while(repeat);
        do
            {
                try
                    {
                        updateFuel = con.prepareStatement(updateFuelUsed);
                        updateFuel.setInt(1, fuel);
                        updateFuel.setInt(2, rid);
                        updateFuel.executeUpdate();
                        repeat = false;
                    }
                catch (SQLException e)
                    {
                        System.out.println("Oops, something went wrong.");
                        repeat = true;
                    }

            }while(repeat);
        System.out.println("How many miles were driven?");
        do
            {
                try
                    {
                        odo = rcScan.nextInt();
                        if (odo < 0)
                            {
                                throw new NumberFormatException();
                            }
                        repeat = false;
                    }
                catch (Exception e)
                    {
                        System.out.println("Please enter a valid number of miles drive.");
                        repeat = true;
                    }
            }while(repeat);
        try
            {
                updateMiles = con.prepareStatement(updateMilesDriven);
                updateMiles.setInt(1, odo);
                updateMiles.setInt(2, rid);
                updateMiles.executeUpdate();
            }
        catch (SQLException e)
            {
                System.out.println("Oops, something went wrong.");
            }

        try
            {
                calcCharges = con.prepareStatement(callCalculateCharges);
                calcCharges.setInt(1, rid);
                calcCharges.executeUpdate();
            }
        catch (SQLException e)
            {
            System.out.println("Oops, something went wrong.");
            }
        return;

    }
    /*This method prints out a customer's receipt after the car has been returned*/
    public static void printReceipt()
    {
        Scanner prScan = new Scanner(System.in);
        int rid = -1;
        boolean invalidResID;
        do
            {
                System.out.println("Please input your reservation number");
                try
                    {
                        rid = prScan.nextInt();
                        invalidResID = false;
                    }
                catch(Exception e)
                    {
                        System.out.println("Please enter a valid reservation number.");
                        invalidResID = true;
                        prScan.next();
                    }
            } while (invalidResID);
        String getReceipt = "Select * "
            + "from charges "
            + "where res_id = ?";
        PreparedStatement prepGetReceipt =null;
        ResultSet receipt = null;
        try
            {
                prepGetReceipt = con.prepareStatement(getReceipt);
                prepGetReceipt.setInt(1, rid);
                receipt = prepGetReceipt.executeQuery();
            }
        catch(SQLException e)
            {
                e.printStackTrace();
            }
        int base, fuel, ins, drop;
        double total;
        try
            {
                if (!receipt.next())
                    {
                        String checkE = "Select * "
                            + "from reservation "
                            + "where res_id = ?";
                        PreparedStatement checkExists = null;
                        ResultSet exists = null;
                        try
                            {
                                checkExists = con.prepareStatement(checkE);
                                checkExists.setInt(1, rid);
                                exists = prepGetReceipt.executeQuery();
                            }
                        catch(SQLException e)
                            {
                                //e.printStackTrace();
                                System.out.println("Oops, something went wrong.");
                            }
                        if (!exists.next())
                            {
                                System.out.println("A reservation with this number does not exist.");
                            }
                        else
                            {
                                System.out.println("This car has not been returned yet.");
                            }
                    }
                else
                    {
                        String getDisc = "Select discount_percent " +
                            "from reduces natural join discount " +
                            "where res_id = ?";
                        PreparedStatement discPercent = null;
                        ResultSet result = null;
                        boolean discount;
                        double disc = 0;
                        try
                            {
                                discPercent = con.prepareStatement(getDisc);
                                discPercent.setInt(1, rid);
                                result = discPercent.executeQuery();
                            }
                        catch(SQLException e)
                            {
                                System.out.println("Something went wrong.");
                            }
                        double multiplier = 1;
                        if (!result.next())
                            {
                                multiplier = 1;
                                discount = false;
                            }
                        else
                            {
                                disc = result.getDouble("discount_percent");
                                multiplier = 1.0 - disc;
                                discount = true;
                            }
                        System.out.println("Receipt for reservation " + rid + ":");
                        base = receipt.getInt("base_charge");
                        fuel = receipt.getInt("fuel_charge");
                        drop = receipt.getInt("dropoff_charge");
                        total = receipt.getDouble("total_charge");
                        ins = receipt.getInt("insurance_charge");
                        if (discount)
                            {
                                double CaD = total * multiplier;
                                System.out.format("%20s %20s %20s %20s %20s %20s %20s\n", "Base Charge", "Fuel Charge", "Insurance Charge", "Dropoff Charge", "Total Charge", "Discount Percent", "Charge after Discount");
                                System.out.format("%20d %20d %20d %20d %20f, %20f, &20f\n", base, fuel, ins, drop, total, disc, CaD);
                            }
                        else
                            {
                                System.out.format("%20s %20s %20s %20s %20s\n", "Base Charge", "Fuel Charge", "Insurance Charge", "Dropoff Charge", "Total Charge");
                                System.out.format("%20d %20d %20d %20d %20f\n", base, fuel, ins, drop, total);
                            }
                    }
            }
        catch (SQLException e)
            {
                e.printStackTrace();
            }
 
        return;
    }
    /*This method allows us to apply discounts to customers that qualify for them */
    public static void applyDiscount()
    {
        Scanner adScan = new Scanner(System.in);
        String applyDisc = "{call applyDiscount(?,?)}";
        PreparedStatement prepApplyDiscount = null;
        boolean invalid = true;
        String disc = null;
        int rid = -1;
        do
            {
                System.out.println("Please enter the reservation id you wish to apply a discount to.");
                try
                    {
                        rid = adScan.nextInt();
                        invalid = false;
                    }
                catch(Exception e)
                    {
                        System.out.println("Please enter a valid reservation id.");
                        invalid = true;
                    }
                adScan.nextLine();
            }while(invalid);
        System.out.println("Please enter the discount you wish to use.");
        disc = adScan.nextLine();
        try
            {
                prepApplyDiscount = con.prepareStatement(applyDisc);
                prepApplyDiscount.setString(1, disc);
                prepApplyDiscount.setInt(2, rid);
                prepApplyDiscount.execute();
            }
        catch (SQLException e)
            {
                if (e.getErrorCode() == 20012)
                    {
                        System.out.println("That reservation does not exist");
                    }
                else if (e.getMessage().contains("UNIQUE"))
                    {
                        System.out.println("Only one discount may be applied to each reservation");
                    }
                else if (e.getErrorCode() == 20011)
                    {
                        System.out.println("That discount does not exist.");
                    }
                else if (e.getErrorCode() == 20010)
                    {
                        System.out.println("Charges have not been generated for this reservation, please return the car first.");
                    }
            }
    }
    /* Adds a location to the database, PLSQL triggers update the inventory table after insert*/
    public static void addLocation()
    {
        Scanner alScan = new Scanner(System.in);
        String lid, city, state;
        String addLoc = "insert into location (location_id, city, state) values (?,?,?)";
        PreparedStatement prepAddLoc = null;
        System.out.println("Please enter the location ID you wish to add");
        lid = alScan.nextLine();
        System.out.println("Please enter the location's city");
        city = alScan.nextLine();
        System.out.println("Please enter the location's state");
        state = alScan.nextLine();
        try
            {
                prepAddLoc = con.prepareStatement(addLoc);
                prepAddLoc.setString(1, lid.toUpperCase());
                prepAddLoc.setString(2, city);
                prepAddLoc.setString(3, state);
                prepAddLoc.execute();
            }
        catch(SQLException e)
            {
            String errorMsg = e.getMessage();
            if (e.getMessage().contains("LOCATION_ID"))
                {
                    System.out.println("The value you entered for Location ID is invalid.");
                }
            else if (e.getMessage().contains("CITY"))
                {
                    System.out.println("The value you entered for City is invalid.");
                }
            else if (e.getMessage().contains("STATE"))
                {
                    System.out.println("The value you entered for State is invalid.");
                }
            else if (e.getErrorCode() == 00001)
                {
                    System.out.println("That location already exists.");
                }
            else
                {
                    System.out.println("Sorry, something went wrong");
                    e.printStackTrace();
                }
            }

        return;
    }

    /*This method adds a car to the database */
    public static void addCar()
    {
        Scanner acScan = new Scanner(System.in);
        String make = "";
        String model = "";
        String clas = "";
        int odo = -1;
        String lid = "";
        int input = -1;
        boolean repeat;
        System.out.println("What is the car's make?");
        do
            {
                try
                    {
                        make = acScan.nextLine();
                    }
                catch(Exception e){}
            }while (make.isEmpty());
        System.out.println("What is the car's model?");
        do
            {
                try
                    {
                        model = acScan.nextLine();
                    }
                catch(Exception e){}
            }while (model.isEmpty());

        do
            {
                System.out.println("Type the number corresponding to the car's class.");
                System.out.println("1: Standard");
                System.out.println("2: Luxury");
                System.out.println("3: Exotic");
                try
                    {
                        input = acScan.nextInt();
                    }
                catch(Exception e){}
                if (input == 1)
                    {
                        clas = "Standard";
                    }
                else if (input == 2)
                    {
                        clas = "Luxury";
                    }
                else if (input == 3)
                    {
                        clas = "Exotic";
                    }
                else
                    {
                        input = -1;
                        acScan.nextLine();
                    }
            }while (input == -1);
        do
            {
                System.out.println("What is the car's odometer reading?");

                try
                    {
                        odo = acScan.nextInt();
                    }
                catch(Exception e)
                    {
                        acScan.nextLine();
                    }
            }while (odo < 0);
        List<String> listLocations = null;
        ResultSet resultLocations = null;
        Statement s = null;
        try
            {
                listLocations = new ArrayList<String>();
                String qLocations;
                s = con.createStatement();
                qLocations = "select distinct Location_ID from location";
                resultLocations = s.executeQuery(qLocations);
            
                while (resultLocations.next())
                    {
                        listLocations.add(resultLocations.getString(1));
                        
                    }
            }
        catch (SQLException e){}
        do
            {
                input = 50000;
                System.out.println("Please select the car's location");
                for (int i = 0; i < listLocations.size(); i++)
                    {
                        System.out.println(i+1 + ": " + listLocations.get(i));
                    }
                try
                    {
                        input = acScan.nextInt();
                    }
                catch (Exception e){}
                if (input < listLocations.size()+1 && input > 0)
                    {
                        lid = listLocations.get(input-1);
                        repeat = false;
                    }
                else
                    {
                        System.out.println("I'm sorry that is not a valid location");
                        repeat = true;
                        acScan.nextLine();
                    }
            } while (repeat);

        String callAddCar = "{call addCar(?,?,?,?,?)}";
        PreparedStatement addCar = null;
        try
            {
                addCar = con.prepareStatement(callAddCar);
                addCar.setString(1, make);
                addCar.setString(2, model);
                addCar.setString(3, clas);
                addCar.setInt(4, odo);
                addCar.setString(5, lid);
                addCar.execute();
            }
        catch(SQLException e)
            {
                if (e.getMessage().contains("MAKE"))
                    {
                        System.out.println("The value you entered for make is invalid.");
                    }
                else if (e.getMessage().contains("MODEL"))
                    {
                        System.out.println("The value you entered for model is invalid.");
                    }
                else if (e.getMessage().contains("ODOMETER_READING"))
                    {
                        System.out.println("The value you entered for odometer reading is invalid.");
                    }
                else if (e.getMessage().contains("LOCATION_ID"))
                    {
                        System.out.println("The value you entered for location ID is invalid.");
                    }
                else
                    {
                        System.out.println("Something went wrong");
                    }
            }
        return;
    }

}
