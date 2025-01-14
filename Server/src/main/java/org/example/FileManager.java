package org.example;

import org.example.model.Message;
import org.example.model.User;
import org.example.model.UserType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileManager {

    private final static String USERS_FILE_NAME = "users.csv";
    private final static String MESSAGES_FILE_NAME = "messages.csv";
    private final static String TEMP_FILE_NAME = "temp_users.csv";

    public static List<User> readUsersFromCsv() {
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
        List<Message> messages = new ArrayList<>();
        String line;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(Message.class.getClassLoader().getResourceAsStream(MESSAGES_FILE_NAME))))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 4) {
                    String text = values[0];
                    String author = values[1];
                    String recipient = values[2];
                    LocalDate date = LocalDate.parse(values[3], formatter);
                    messages.add(new Message(text, author, recipient, date));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public static void appendMessageToCsv(Message message) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MESSAGES_FILE_NAME, true))) {
            bw.write(message.getText() + "," + message.getAuthor() + "," + message.getRecipient() + "," + message.getDate());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void addOrUpdateUserInCsv(User updatedUser) {
        List<String> lines = new ArrayList<>();
        String line;
        boolean userUpdated = false;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(FileManager.class.getClassLoader().getResourceAsStream(USERS_FILE_NAME))))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 3 && values[0].equals(updatedUser.getLogin())) {
                    lines.add(updatedUser.getLogin() + "," + updatedUser.getPassword() + "," + updatedUser.getUserType());
                    userUpdated = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!userUpdated) {
            lines.add(updatedUser.getLogin() + "," + updatedUser.getPassword() + "," + updatedUser.getUserType());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Objects.requireNonNull(
                FileManager.class.getClassLoader().getResource(USERS_FILE_NAME)).getFile()))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteUserFromCsv(String login) {
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

}
