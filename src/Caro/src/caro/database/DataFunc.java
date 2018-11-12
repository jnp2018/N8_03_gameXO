package caro.database;


import caro.common.Users;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
public class DataFunc {
    Connection con = Connections.Newconnect();
    
    
    
    public ArrayList<Users> getUserList() {
       
        PreparedStatement stm = null;
        ResultSet rs = null;
        ArrayList<Users> uslist = new ArrayList<Users>();
        
        try {
            
            String sql = "Select * from users";
           
            stm = con.prepareStatement(sql);
            rs = stm.executeQuery();
            
            
            
            while (rs.next()) {
                Users us = new Users();
                us.setId(rs.getInt("id"));
                us.setUsername(rs.getString("username"));
                us.setPassword(rs.getString("pass"));
                us.setWin(rs.getInt("win"));
                us.setLose(rs.getInt("lose"));
                us.setScore(rs.getInt("score"));
                uslist.add(us);
            }
           
        } catch (SQLException ex) {
                  
        }    
        return uslist;
    }
    
    public Users checkLogin(String username, String password) {
        try {
                    
            String sql = "Select * From users Where username = ? and pass = ?";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setString(1, username);
            stm.setString(2, password);
            ResultSet rs = stm.executeQuery();
            boolean result = rs.next();
            rs.close();
            stm.close();
            if (result) {
                return getUser(username);
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    public boolean register(String username, String password) {
         
                   
           
        String sql = "Insert into users(username, pass) values (?, ?)";
        
        PreparedStatement pst = null;
        
        try {
            pst = con.prepareStatement(sql);
            
            pst.setString(1, username);
            pst.setString(2, password);
            
            pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DataFunc.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
                        
       return true; 
}
    
    public boolean checkAva(int id) {
        try {

            String sql = "Select * From users Where id = ?";
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            boolean result = rs.next();
            rs.close();
            stm.close();
            if (result) {
                return true;
            }
            
        } catch (Exception e) {
        }
        return false;
    }
    
    public boolean updateUser(Users user) throws SQLException 
    {
            String sqlStatement =
            "update users " +
            "set username = ?,"  +
            "pass = ?,"  +
            "win = ?,"  +
            "lose = ?,"  +
            "score = ?"  +
            " where Id = ?;";
            PreparedStatement updateQuery  = con.prepareStatement(sqlStatement);
            updateQuery.setString(1, user.getUsername());
            updateQuery.setString(2, user.getPassword());
            updateQuery.setInt(3, user.getWin());
            updateQuery.setInt(4, user.getLose());
            updateQuery.setInt(5, user.getScore());
            updateQuery.setInt(6, user.getId());
            updateQuery.execute();
                              
            return true;
    }
    
    public boolean updateWin(int  id, int win) throws SQLException 
    {
    
        
            if(checkAva(id) == false)
                return false;
            
            String sqlStatement =
            "update users " +
            "set win = ?"  +
            " where Id = ?";
            PreparedStatement updateQuery  = con.prepareStatement(sqlStatement);

            updateQuery.setInt(1, win);
            updateQuery.setInt(2, id);

            updateQuery.execute();
                              
            return true;
    }
    public boolean updateLose(int id, int lose) throws SQLException 
    {
    
            String sqlStatement =
            "update users " +
            "set lose = ?"  +
            " where Id = ?"; 
            PreparedStatement updateQuery  = con.prepareStatement(sqlStatement);

            updateQuery.setInt(1, lose);
            updateQuery.setInt(2, id);

            updateQuery.execute();
                              
            return true;
    }
    public int getId(String username)    {
        PreparedStatement stm = null;
        ResultSet rs = null;
        int result = 0;
        try {
           
            stm = con.prepareStatement("select * from users Where username = ?");
            stm.setString(1, username);
            rs = stm.executeQuery();
            
            while (rs.next()) {
               result = rs.getInt("id");
               break;
            }
            rs.close();
            stm.close();
        } catch (SQLException ex) {
                  
        }    
        return result;
    
    }
    public Users getUser(String username)    {
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            
            String sql = "select * from users where username = ?";
            stm = con.prepareStatement(sql);
            stm.setString(1, username);
            
            
            
            rs = stm.executeQuery();
            
         
            
            while (rs.next()) {
       
                    Users us = new Users();
                    us.setId(rs.getInt("id"));
                    us.setUsername(rs.getString("username"));
                    us.setPassword(rs.getString("pass"));
                    us.setWin(rs.getInt("win"));
                    us.setLose(rs.getInt("lose"));
                    us.setScore(rs.getInt("score"));
                   
                    return us;
                
                
        }
           
        } catch (SQLException ex) {
                  
        }    
        return null;
    }
    public boolean DeleteUser(int Id) throws SQLException
    {
            PreparedStatement stm = null;
            
            stm = con.prepareStatement("delete from Users Where Id = ?");
            stm.setInt(1, Id);
                                 
            stm.execute();
        return false;
    
    }
    
    
    public static void main(String[] args) {
       Users u = new DataFunc().checkLogin("tiennam", "1234");
       
        System.out.println(u.getId());
    }
    
}
