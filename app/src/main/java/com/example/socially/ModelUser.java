package com.example.socially;

public class ModelUser {
    String uid, coverImage, currentCity, dateOfBirth, email, firstName, gender, homeTown, lastName, profileImage, relationshipStatus;

    public ModelUser() {
    }

    public ModelUser(String uid, String coverImage, String currentCity, String dateOfBirth, String email, String firstName, String gender, String homeTown, String lastName, String profileImage, String relationshipStatus) {
        this.uid = uid;
        this.coverImage = coverImage;
        this.currentCity = currentCity;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.firstName = firstName;
        this.gender = gender;
        this.homeTown = homeTown;
        this.lastName = lastName;
        this.profileImage = profileImage;
        this.relationshipStatus = relationshipStatus;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }
}
