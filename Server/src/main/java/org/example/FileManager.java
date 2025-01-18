package org.example;

import org.example.model.Message;
import org.example.model.User;
import org.example.model.UserType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private final static String USERS_FILE_NAME = "users.csv";
    private final static String MESSAGES_FILE_NAME = "messages.csv";
    private final static String TEMP_FILE_NAME = "temp_users.csv";

    public static List<User> readUsersFromCsv() {
        System.out.println("readUsersFromCsv");
        List<User> users = new ArrayList<>();
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE_NAME))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 3) {
                    String login = values[0];
                    String password = values[1];
                    UserType userType = UserType.valueOf(values[2]);
                    users.add(new User(login, password, userType));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static List<Message> readMessagesFromCsv() {
        System.out.println("readMessagesFromCsv");
        List<Message> messages = new ArrayList<>();
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(MESSAGES_FILE_NAME))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 4) {
                    String author = values[0];
                    String recipient = values[1];
                    String text = values[2];
                    String date = values[3];
                    messages.add(new Message(text, author, recipient, date));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }


    public static void appendUserToCsv(User user) {
        System.out.println("appendUserToCsv: " + user.toString());
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE_NAME, true))) {
            bw.write(user.getLogin() + "," + user.getPassword() + "," + user.getUserType());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendMessageToCsv(Message message) {
        System.out.println("appendMessageToCsv: " + message.toString());
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MESSAGES_FILE_NAME, true))) {
            bw.write(message.getAuthor() + "," + message.getRecipient() + "," + message.getText() + "," + message.getDate());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteUserFromCsv(String login) {
        System.out.println("deleteUserFromCsv: " + login);
        List<String> lines = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE_NAME))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 3 && !values[0].equals(login)) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TEMP_FILE_NAME))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Path originalPath = Paths.get(USERS_FILE_NAME);
            Path tempPath = Paths.get(TEMP_FILE_NAME);
            Files.delete(originalPath);
            Files.move(tempPath, originalPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMessagesByRecipient(String recipient) {
        System.out.println("deleteMessagesByRecipient: " + recipient);
        List<String> lines = new ArrayList<>();
        String line;

        // Odczytanie wiadomości z pliku CSV i filtrowanie tych, które nie są dla podanego odbiorcy
        try (BufferedReader br = new BufferedReader(new FileReader(MESSAGES_FILE_NAME))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 4) {
                    System.out.println("Checking message for recipient: " + values[2]);
                    if (!values[1].equals(recipient)) {
                        lines.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Zapisanie przefiltrowanych wiadomości do tymczasowego pliku
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TEMP_FILE_NAME))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Zamiana oryginalnego pliku wiadomości na tymczasowy plik
        try {
            Files.move(Paths.get(TEMP_FILE_NAME), Paths.get(MESSAGES_FILE_NAME), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
