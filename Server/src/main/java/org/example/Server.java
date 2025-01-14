package org.example;

import com.google.gson.Gson;
import org.example.model.Message;
import org.example.model.Response;
import org.example.model.User;
import org.example.model.UserCredentials;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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

            if (response.getStatus().equals("ADMIN")) {
                String command = "";
                do {
                    try {
                        command = gson.fromJson(din.readUTF(), String.class);
                        switch (command) {
                            case "CreateUser": {
                                User user = gson.fromJson(din.readUTF(), User.class);
                                FileManager.appendUserToCsv(user);
                                break;
                            }
                            case "GetUsersList": {
                                List<User> allUsers = FileManager.readUsersFromCsv();
                                dout.writeUTF(gson.toJson(allUsers));
                                break;
                            }
                            case "DeleteUser": {
                                String loginToDelete = din.readUTF();
                                FileManager.deleteUserFromCsv(gson.fromJson(loginToDelete, String.class));
                                break;
                            }
                            case "SendMessage": {
                                Message message = gson.fromJson(din.readUTF(), Message.class);
                                FileManager.appendMessageToCsv(message);
                                break;
                            }
                            case "ReadMessage": {
                                try {
                                    String userLoginJson = din.readUTF();
                                    String userLogin = gson.fromJson(userLoginJson, String.class);
                                    List<Message> messages = FileManager.readMessagesFromCsv();
                                    List<Message> result = getMessagesByRecipient(messages, userLogin);
                                    dout.writeUTF(gson.toJson(result));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    System.out.println("Error reading message: " + e.getMessage());
                                }
                                break;
                            }
                            case "DeleteMessages": {
                                String userLoginJson = din.readUTF();
                                String userLogin = gson.fromJson(userLoginJson, String.class);
                                FileManager.deleteMessagesByRecipient(userLogin);
                                break;
                            }
                            case "Exit": {
                                System.out.println("Closing connection...");
                                break;
                            }
                            default: {
                                System.out.println("Unknown command: " + command);
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Error processing command: " + e.getMessage());
                    }
                } while (!command.equals("Exit"));

                // Zamknięcie zasobów po zakończeniu pętli
                try {
                    if (din != null) din.close();
                    if (dout != null) dout.close();
                    if (s != null) s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("jeste userem");
                String command = "";
                do {
                    // Implementacja dla użytkownika
                } while (!command.equals("exit"));

                // Zamknięcie zasobów po zakończeniu pętli
                try {
                    if (din != null) din.close();
                    if (dout != null) dout.close();
                    if (s != null) s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }




        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading message: " + e.getMessage());
            throw new RuntimeException(e);

        }
    }
    public static List<Message> getMessagesByRecipient(List<Message> messages, String recipient) {
        List<Message> filteredMessages = new ArrayList<>();
        for (Message message : messages) {
            if (message.getRecipient().equals(recipient)) {
                filteredMessages.add(message);
            }
        }
        return filteredMessages;
    }



    public static Optional<User> findUserByLogin(List<User> users, String login) {
        return users.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst();
    }

    private static Response responseAfterCheckUser(Optional<User> user, UserCredentials userCredentials) {
        if (user.isPresent() && user.get().getPassword().equals(userCredentials.getPassword())) {
            return new Response(user.get().getUserType().toString(), user.get());
        } else return new Response("error", "Incorrect login or password");
    }
}