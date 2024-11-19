//package com.example.youtube_android.entities;
//
//import androidx.annotation.NonNull;
//import androidx.room.Entity;
//import androidx.room.PrimaryKey;
//
//import java.io.Serializable;
//import java.util.UUID;
//
//@Entity
//public class User implements Serializable {
//    private String _id;
//    private String firstName;
//    private String lastName;
//
//    @PrimaryKey
//    @NonNull
//    private String username;
//
//    private String password;
//    private String image;
//
//    public User(String _id, String firstName, String lastName, String username, String password, String image) {
//        this._id = _id != null ? _id : generateRandomId();
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.username = username;
//        this.password = password;
//        this.image = image;
//    }
//
//    // Add an empty constructor
//    public User() {}
//    // Generate a random ID
//    private String generateRandomId() {
//        return UUID.randomUUID().toString();
//    }
//    public String get_id(){return _id;}
//
//
//    // Getters
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public String getUsername() {
//        return username; // Ensure this matches the field name
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public String getImage() {
//        return image;
//    }
//    public void set_id(String _id){ this._id = _id; }
//
//    // Setters
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public void setUsername(String username) { // Ensure this matches the field name
//        this.username = username;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public void setImage(String image) {
//        this.image = image;
//    }
//}


package com.example.youtube_android.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class User implements Serializable {
    private String _id;
    private String firstName;
    private String lastName;

    @PrimaryKey
    @NonNull
    private String username;

    private String password;
    private String image;

    public User(String _id, String firstName, String lastName, String username, String password, String image) {
        this._id = _id; // Remove the random ID generation logic
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.image = image;
    }

    // Add an empty constructor
    public User() {}

    // Getters
    public String get_id() {
        return _id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getImage() {
        return image;
    }

    // Setters
    public void set_id(String _id) {
        this._id = _id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setImage(String image) {
        this.image = image;
    }
}



