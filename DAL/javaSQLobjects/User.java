package g2m.DAL.javaSQLobjects;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private String googleId;
    private String username;
    private String email;
    private boolean isInstructor;
    private LocalDateTime created_at;
    private LocalDateTime lastLogin;

    //Constructor
    public User(int userId, String googleId, String username, String email, boolean isInstructor, LocalDateTime created_at, LocalDateTime lastLogin)
    {
        this.userId = userId;
        this.googleId = googleId;
        this.username = username;
        this.email = email;
        this.isInstructor = isInstructor;
        this.created_at = created_at;
        this.lastLogin = lastLogin;
    }

    //Setters & Getters
    
    public int getuserId() {
        return userId;
    }

    public void setuserId(int userId) {
        this.userId = userId;
    }

    public String getgoogleId() {
        return googleId;
    }

    public void setgoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public boolean getisInstructor() {
        return isInstructor;
    }

    public void setisInstructor(boolean isInstructor) {
        this.isInstructor = isInstructor;
    } 

    public LocalDateTime getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setisInstructor(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    } 

    public String toString(){
        String toReturn = "";
        toReturn += "googleId: " + googleId;
        toReturn += "\nUser ID: " + userId;
        toReturn += "\nusername: " + username;
        toReturn += "\nemail: " + email;
        toReturn += "\nisInstructor: $" + isInstructor;
        toReturn += "\ncreated at: " + created_at;
        toReturn += "\nlast login at: $" + lastLogin;

        return toReturn;
    }
}

