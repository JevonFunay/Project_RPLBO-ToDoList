package org.example.project_rplbo.util;

import java.io.*;
import java.time.LocalDate;

public class SessionManager implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String SESSION_FILE = "session.ser";
//    private String gender = "Male";
    private String username;
//    private String password;
//    private LocalDate date;

    private static volatile SessionManager instance;
    private boolean isLoggedIn;

    // Private constructor to prevent instantiation from outside
    private SessionManager() {
        isLoggedIn = false;
    }

    // Static method to get the singleton instance
    public static SessionManager getInstance() { //3
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                    instance.createSessionFile();
                }
            }
        }
        return instance;
    }

    // Method to check if the session file doesn't exist
    public void createSessionFile() { //1
        File file = new File(SESSION_FILE);
        if (!file.exists()) {
            saveSession();
        } else {
            loadSession();
        }
    }

    private void loadSession() { //1
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SESSION_FILE))) {
            SessionManager sessionManager = (SessionManager) ois.readObject();
//            this.date = sessionManager.date;
//            this.password = sessionManager.password;
            this.username = sessionManager.username;
            this.isLoggedIn = sessionManager.isLoggedIn;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading session: " + e.getMessage());
        }
    }

    private void saveSession() { //3
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SESSION_FILE))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to check if user is logged in
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    // Method to simulate login
    public void login() { //1
        isLoggedIn = true;
        saveSession();
    }

    // Method to simulate logout
    public void logout() { //1
        isLoggedIn = false;
        saveSession();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        saveSession();
    }
}

