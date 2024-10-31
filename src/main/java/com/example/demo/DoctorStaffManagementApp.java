package com.example.demo;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DoctorStaffManagementApp extends Application {
    private TextField nameField, idField, roleField, specialtyField, shiftField;
    private Button addProfileButton, displayProfilesButton;
    private TextArea displayArea;

    private final String filePath = "C:\\Users\\Shetty.Harsha\\IdeaProjects\\demo\\src\\main\\java\\com\\example\\demo\\doctor.json";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Doctor and Staff Management");

        nameField = new TextField();
        nameField.setPromptText("Name");

        idField = new TextField();
        idField.setPromptText("ID");

        roleField = new TextField();
        roleField.setPromptText("Role (Doctor/Staff)");

        specialtyField = new TextField();
        specialtyField.setPromptText("Specialty (Doctors Only)");

        shiftField = new TextField();
        shiftField.setPromptText("Shift (e.g., Morning, Evening)");

        addProfileButton = new Button("Add Profile");
        addProfileButton.setOnAction(e -> addProfile());

        displayProfilesButton = new Button("Display Profiles");
        displayProfilesButton.setOnAction(e -> displayProfiles());

        displayArea = new TextArea();
        displayArea.setPrefHeight(200);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.getChildren().addAll(
                new Label("Add Doctor/Staff Profile"),
                nameField, idField, roleField, specialtyField, shiftField,
                addProfileButton, displayProfilesButton, displayArea
        );

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addProfile() {
        String name = nameField.getText();
        String id = idField.getText();
        String role = roleField.getText();
        String specialty = specialtyField.getText();
        String shift = shiftField.getText();

        if (name.isEmpty() || id.isEmpty() || role.isEmpty() || shift.isEmpty()) {
            displayArea.setText("Please fill in all fields.");
            return;
        }

        try {
            JSONArray profilesArray = readProfilesFromFile();
            JSONObject profile = new JSONObject();
            profile.put("name", name);
            profile.put("id", id);
            profile.put("role", role);
            profile.put("specialty", role.equalsIgnoreCase("Doctor") ? specialty : "N/A");
            profile.put("shift", shift);

            // Check for conflicts only if the role is "Doctor"
            if (role.equalsIgnoreCase("Doctor") && checkConflict(profilesArray, shift)) {
                displayArea.setText("Scheduling conflict! Shift already assigned.");
                return;
            }

            profilesArray.put(profile);
            saveProfilesToFile(profilesArray);

            displayArea.setText("Profile added successfully!");
            clearFields();

        } catch (IOException ex) {
            displayArea.setText("Error saving profile.");
        }
    }

    private boolean checkConflict(JSONArray profilesArray, String shift) {
        for (int i = 0; i < profilesArray.length(); i++) {
            JSONObject profile = profilesArray.getJSONObject(i);
            // Check conflict only if the profile role is "Doctor"
            if (profile.getString("role").equalsIgnoreCase("Doctor") &&
                    profile.getString("shift").equalsIgnoreCase(shift)) {
                return true;
            }
        }
        return false;
    }


    private void displayProfiles() {
        try {
            JSONArray profilesArray = readProfilesFromFile();
            displayArea.setText(profilesArray.toString(2));
        } catch (IOException ex) {
            displayArea.setText("Error reading profiles.");
        }
    }

    private JSONArray readProfilesFromFile() throws IOException {
        if (Files.exists(Paths.get(filePath))) {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return new JSONArray(content);
        }
        return new JSONArray();
    }

    private void saveProfilesToFile(JSONArray profilesArray) throws IOException {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(profilesArray.toString(2));
        }
    }

    private void clearFields() {
        nameField.clear();
        idField.clear();
        roleField.clear();
        specialtyField.clear();
        shiftField.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

