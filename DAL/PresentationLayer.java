package g2m.DAL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.Console;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class PresentationLayer {
    public static void main(String[] args) {

        // Take in username and password needed to access the database
        Scanner in = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String user = in.nextLine();

        // Reads in password, makes it invisibile to user typing it in for more security
        Console console = System.console();

        char[] passwordArray = console.readPassword("Enter your password: ");
        String password = new String(passwordArray);

        // Create the DAL and business logic layers
        QuizDal dal = new QuizDal("QuizzingDB", user, password);
        BusinessLogic bLogic = new BusinessLogic(dal);
        
    }
}
