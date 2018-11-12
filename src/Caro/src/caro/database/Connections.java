package caro.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tien Nam
 */
public class Connections {
    
    
   
    private static Connection getConnect(String hostName, String dbName, String user, String pass) {
       try {
           Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Connections.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       String url = "jdbc:mysql://"+hostName+":3306/"+dbName;
       Connection con = null;
       
        try {
            con = DriverManager.getConnection(url, user, pass);
        } catch (SQLException ex) {
            System.out.println("Loi try xuat CSDL");
            Logger.getLogger(Connections.class.getName()).log(Level.SEVERE, null, ex);
        }
         return con;
    }
    
     
     
     public static Connection Newconnect(){
        String hostName =  "Localhost";
        String dbName = "caro";
        String user = "root";
        String pass = "1234";
        
        return getConnect(hostName, dbName, user, pass);
    }
}
