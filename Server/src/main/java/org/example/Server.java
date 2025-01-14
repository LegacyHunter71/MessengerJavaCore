package org.example;

import com.google.gson.Gson;
import org.example.model.Response;
import org.example.model.User;
import org.example.model.UserCredentials;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

public class Server {
    public static void main(String[] args) {

        ServerSocket ss = null;
        try {
            ss = new ServerSocket(9999);
            Socket s = ss.accept();

            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            Gson gson = new Gson();
            String flag = "";
            UserCredentials userCredentials = null;
            Response response = null;
            String command = "";

            do {
                String loginJson = din.readUTF();
                userCredentials = gson.fromJson(loginJson, UserCredentials.class);
                System.out.println(userCredentials.toString());
                List<User> users = FileManager.readUsersFromCsv();
                Optional<User> user = findUserByLogin(users, userCredentials.getLogin());
                response = responseAfterCheckUser(user, userCredentials);
                flag = response.getStatus();
                dout.writeUTF(gson.toJson(response));

            } while (flag.equals("error"));

            if (response.getData().toString().equals("ADMIN")) {
                do{
                    command = gson.fromJson(din.readUTF(), String.class);
                    switch (command) {
                        case "CreateUser":{
                            User user = gson.fromJson(din.readUTF(), User.class);
                            FileManager.appendUserToCsv(user);
                            break;}
                        case "ALL":{
                            List<User> allUsers = FileManager.readUsersFromCsv();
                            dout.writeUTF(gson.toJson(allUsers));
                            break;}
                        case "DeleteUser":{
                            String loginToDelete = din.readUTF();
                            FileManager.deleteUserFromCsv(gson.fromJson(loginToDelete, String.class));
                            break;}
                        case "ReadMessage":{break;}
                        case "SendMessage":{break;}
                        case "DeleteMessages":{break;}
                    }
                }while(command.equals("exit"));
            } else {
                System.out.println("jeste userem");
                do{}while(!command.equals("exit"));
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<User> findUserByLogin(List<User> users, String login) {
        return users.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst();
    }

    private static Response responseAfterCheckUser(Optional<User> user, UserCredentials userCredentials) {
        if (user.isPresent() && user.get().getPassword().equals(userCredentials.getPassword())) {
            return new Response("ok", user.get().getUserType());
        } else return new Response("error", "Incorrect login or password");
    }
}