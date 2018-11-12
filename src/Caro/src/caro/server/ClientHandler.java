/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package caro.server;


import caro.common.GPos;
import caro.common.KMessage;
import caro.common.Room;
import caro.common.Users;
import caro.database.DataFunc;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Tien Nam
 */
public class ClientHandler extends Thread {
        public Room room = null;
    
        private Socket socket;
        ObjectInputStream inputStream;
        ObjectOutputStream outputStream;

        public Users user;
        
        Boolean execute = true;
        
        ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            
            execute = true;
        }

        
        
        

        void ReceiveMessage(KMessage msg) throws IOException {
            
            switch (msg.getType()) {
                case 0: {
                    Users temp = (Users)msg.getObject();
                    DataFunc df = new DataFunc();
                    user = df.checkLogin(temp.getUsername(), temp.getPassword());
                    if(user != null)
                    {
                        Boolean flag = true;
                        // Kiem tra coi dang nhap co bị trung voi dang nhap truoc khong
                        for (ClientHandler cli : Main.lstClient) {
                            if (cli!=this && cli.user!=null && cli.user.getUsername().equalsIgnoreCase(user.getUsername()))
                            {
                                user = null;
                                break;
                            }
                        }
                        if (user!=null)
                            System.out.println("Server: Xin chao " + user.getUsername());
                    }
                    SendMessage(0, user);
                    break;
                }
                case 1: { // đăng ký tài khoản
                    Users temp = (Users)msg.getObject();
                    DataFunc df = new DataFunc();
                    boolean exits;
                    exits = df.checkAva(df.getId(temp.getUsername()));
                    if (exits == true) {
                        SendMessage(1, temp.getUsername() + " is existing");
                        return;
                    }

                    boolean succ = df.register(temp.getUsername(), temp.getPassword());
                    if (succ == true) {
                        SendMessage(1, "Register succesfully");
                    }
                    
                    break;
                }
                case 10: 
                {
                    System.out.println(msg.getObject().toString());
                    break;
                }
                //Room
                case 20: // Join room
                {
                    room = Main.lstRoom.get(Integer.parseInt(msg.getObject().toString()));
                    if (room.add(this)==false) //full
                    {
                        int[] arrRoom = new int[Main.lstRoom.size()];
                        for (int i=0; i<Main.lstRoom.size(); i++)
                        {
                            arrRoom[i] = Main.lstRoom.get(i).countAvailable();
                        }
                        SendMessage(22, arrRoom);
                    }
                    else
                        SendMessage(20, null);
                    
                    break;
                }
                case 21: //Get all room
                {
                    int[] arrRoom = new int[Main.lstRoom.size()];
                    for (int i=0; i<Main.lstRoom.size(); i++)
                    {
                        arrRoom[i] = Main.lstRoom.get(i).countAvailable();
                    }
                    SendMessage(21, arrRoom);
                    break;
                }
                case 28:
                {
                    if (room.client1!=null && room.client2!=null)
                    {
                        Users[] arrUser = new Users[2];
                        arrUser[0] = room.client1.user;
                        arrUser[1] = room.client2.user;
                        room.client1.SendMessage(34, arrUser);
                        room.client2.SendMessage(34, arrUser);
                        room.client2.SendMessage(36, null);
                    }
                    break;
                }
                case 30: // Lay ban co
                {
                    GPos gPos = (GPos)msg.getObject();
                    if (gPos!=null)
                        room.put(this, gPos);
                    
                    if (room != null) {
                        for (ClientHandler cli : room.lstClientView) {
                                cli.SendMessage(30, room.pieceses);
                        }
                    }
        
                    break;
                }
                case 39: //Exit room
                {
                    if (room!=null)
                    {
                        room.clientExit(this);
                    }
                    break;
                }
                case 41: //View
                {
                    room = Main.lstRoom.get(Integer.parseInt(msg.getObject().toString()));
                    room.lstClientView.add(this);
                    SendMessage(20, null);
                    break;
                }
            }
        }

        public void SendMessage(int ty, Object obj) throws IOException {
            KMessage temp = new KMessage(ty, obj);
            SendMessage(temp);
        }
                
        public void SendMessage(KMessage msg) throws IOException {
            outputStream.reset();
            outputStream.writeObject(msg);
        }
        
        public Boolean closeClient() throws Throwable
        {
            
            
            if (room!=null) // Thong bao thoat room
            {
                try {
                    room.lstClientView.remove(this);
                } catch (Exception e) {
                    
                }
                
                room.clientExit(this);
            }
            
            Main.lstClient.remove(this);
            try {
                this.socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Client Exit");
            execute = false;
            
            
            return true;
        }
        
        @Override
        public void run() {
            
            while (execute) {
                
                try {
                    Object o = inputStream.readObject();
                    if (o != null) {
                        ReceiveMessage((KMessage)o);
                    }
                    
                } catch (IOException e) {
                    try {
                        closeClient();
                    } catch (Throwable ex) {
                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }


    }