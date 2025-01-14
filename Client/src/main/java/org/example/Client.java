package org.example;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import org.example.model.*;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.List;

public class Client {
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        Response response = null;
        String serverAnswer = "";

        try {
            Socket s = new Socket("localhost", 9999);
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            DataInputStream din = new DataInputStream(s.getInputStream());

            Gson gson = new Gson();

            String command = "";


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

            if (response.getStatus().equals("ADMIN")) {
                do {
                    getAdminMenu();
                    command = br.readLine();
                    dout.writeUTF(gson.toJson(command));
                    switch (command) {
                        case "CreateUser": {
                            User user = createUser();
                            dout.writeUTF(gson.toJson(user));
                            break;
                        }
                        case "GetUsadminersList": {
                            String json = din.readUTF();
                            Type userListType = new TypeToken<List<User>>() {
                            }.getType();
                            List<User> users = gson.fromJson(json, userListType);
                            showUsers(users);
                            break;
                        }
                        case "DeleteUser": {
                            System.out.println("PODAJ LOGIN :");
                            String login = br.readLine();
                            dout.writeUTF(gson.toJson(login));
                            break;
                        }
                        case "SendMessage": {
                            Object data = response.getData();
                            if (data instanceof LinkedTreeMap) {
                                String json = gson.toJson(data);
                                User user = gson.fromJson(json, User.class);
                                Message message = createMessage(user.getLogin());
                                dout.writeUTF(gson.toJson(message));
                            } else if (data instanceof User) {
                                User user = (User) data;
                                Message message = createMessage(user.getLogin());
                                dout.writeUTF(gson.toJson(message));
                            } else {
                                System.out.println("Data is not of type User or LinkedTreeMap: " + data);
                            }
                            System.out.println(data);
                            break;
                        }
                        case "ReadMessage": {
                            Object data = response.getData();
                            if (data instanceof LinkedTreeMap) {
                                String json = gson.toJson(data);
                                User user = gson.fromJson(json, User.class);
                                dout.writeUTF(gson.toJson(user.getLogin()));
                            } else if (data instanceof User) {
                                User user = (User) data;
                                dout.writeUTF(gson.toJson(user.getLogin()));
                            } else {
                                System.out.println("Data is not of type User or LinkedTreeMap: " + data);
                            }
                            String json = din.readUTF();
                            Type messageList = new TypeToken<List<Message>>() {
                            }.getType();
                            List<Message> messages = gson.fromJson(json, messageList);
                            if (messages.size() == 0) {
                                System.out.println("BRAK WIADOMOŚCI");
                            } else {
                                showMessages(messages);
                            }

                            break;
                        }
                        case "DeleteMessages": {
                            Object data = response.getData();
                            if (data instanceof LinkedTreeMap) {
                                String json = gson.toJson(data);
                                User user = gson.fromJson(json, User.class);
                                dout.writeUTF(gson.toJson(user.getLogin()));
                            } else if (data instanceof User) {
                                User user = (User) data;
                                dout.writeUTF(gson.toJson(user.getLogin()));
                            } else {
                                System.out.println("Data is not of type User or LinkedTreeMap: " + data);
                            }
                            break;
                        }
                    }
                } while (!command.equals("Exit"));
            } else {
                System.out.println("jeste userem");
                do {
                } while (!command.equals("Exit"));
            }

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Message createMessage(String author) {
        Message result = new Message();
        try {
            System.out.println("PODAJ ODBIORCĘ :");
            String recipient = br.readLine();
            System.out.println("PODAJ TREŚĆ :");
            String text = br.readLine();

            result.setRecipient(recipient);
            result.setText(text);
            result.setDate(LocalDate.now().toString());
            result.setAuthor(author);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private static void showUsers(List<User> users) {
        for (User user : users) {
            System.out.println(user.getLogin() + "  " + user.getUserType());
        }
    }

    private static void showMessages(List<Message> messages) {
        for (Message message : messages) {
            System.out.println(message.getText() + "  " + message.getAuthor() + "  " + message.getDate());
        }
    }

    private static User createUser() {
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

    private static void getAdminMenu() {
        System.out.println("MENU");
        System.out.println("1. CreateUser");
        System.out.println("2. DeleteUser");
        System.out.println("3. GetUsersList");
        System.out.println("4. ReadMessage");
        System.out.println("5. SendMessage");
        System.out.println("6. DeleteMessages");
        System.out.println("7. Exit");
    }

    private static void getUserMenu() {
        System.out.println("MENU");
        System.out.println("1. ReadMessage");
        System.out.println("2. SendMessage");
        System.out.println("3. DeleteMessages");
        System.out.println("4. Exit");
    }
}