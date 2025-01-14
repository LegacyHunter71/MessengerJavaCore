package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.model.Response;
import org.example.model.User;
import org.example.model.UserCredentials;
import org.example.model.UserType;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class Client {
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) {


        try {
            Socket s = new Socket("localhost", 9999);
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            DataInputStream din = new DataInputStream(s.getInputStream());

            Gson gson = new Gson();
            String serverAnswer = "";
            String command = "";
            Response response = null;

            do {
                System.out.println("PODAJ LOGIN :");
                String login = br.readLine();
                System.out.println("PODAJ HASŁO :");
                String password = br.readLine();
                UserCredentials userCredentials = new UserCredentials(login, password);
                dout.writeUTF(gson.toJson(userCredentials));
                String serverAnswerJson = din.readUTF();
                response = gson.fromJson(serverAnswerJson, Response.class);
                serverAnswer = response.getStatus();
                System.out.println(serverAnswer);
            }
            while (serverAnswer.equals("error"));

            if (response.getData().toString().equals("ADMIN")) {
                do{
                    getAdminMenu();
                    command = br.readLine();
                    dout.writeUTF(gson.toJson(command));
                switch (command) {
                    case "CreateUser":{
                        User user = createUser();
                        dout.writeUTF(gson.toJson(user));
                        break;}
                    case "GetUsersList":{
                        dout.writeUTF(gson.toJson("ALL"));
                        String json = din.readUTF();
                        Type userListType = new TypeToken<List<User>>() {}.getType();
                        List<User> users = gson.fromJson(json, userListType);
                        showUsers(users);
                        break;}
                    case "DeleteUser":{
                        System.out.println("PODAJ LOGIN :");
                        String login = br.readLine();
                        dout.writeUTF(gson.toJson(login));
                        break;}
                    case "ReadMessage":{break;}
                    case "SendMessage":{break;}
                    case "DeleteMessages":{break;}
                }
                }while(!command.equals("Exit"));
            }else{
                System.out.println("jeste userem");
                do{}while(!command.equals("exit"));
            }

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void showUsers(List<User> users){
        for (User user : users) {
            System.out.println(user.getLogin()+"  "+user.getUserType());
        }
    }
    private static User createUser()  {
        User result = new User();
        try {
            System.out.println("PODAJ LOGIN :");
            String login = br.readLine();
            System.out.println("PODAJ HASŁO");
            String password = br.readLine();
            System.out.println("PODAJ ROLE");
            String role = br.readLine();
            result.setLogin(login);
            result.setPassword(password);
            result.setUserType(UserType.valueOf(role));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    private static void getAdminMenu(){
        System.out.println("MENU");
        System.out.println("1. CreateUser");
        System.out.println("2. DeleteUser");
        System.out.println("3. GetUsersList");
        System.out.println("4. ReadMessage");
        System.out.println("5. SendMessage");
        System.out.println("6. DeleteMessages");
        System.out.println("7. Exit");
    }
    private static void getUserMenu(){
        System.out.println("MENU");
        System.out.println("1. ReadMessage");
        System.out.println("2. SendMessage");
        System.out.println("3. DeleteMessages");
        System.out.println("4. Exit");
    }
}