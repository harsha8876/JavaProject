package com.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PatientRecordsApp extends Application {
    private static final String FILE_PATH = "C:\\Users\\Shetty.Harsha\\IdeaProjects\\demo\\src\\main\\java\\com\\example\\demo\\PatientRecord.json";

    private JSONArray patients;
    private ListView<String> recordsListView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        patients = loadData();

        TextField idField = new TextField();
        idField.setPromptText("Enter Patient ID");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter Patient Name");

        TextArea medicalHistoryField = new TextArea();
        medicalHistoryField.setPromptText("Medical History");

        TextArea ongoingTreatmentsField = new TextArea();
        ongoingTreatmentsField.setPromptText("Ongoing Treatments");

        TextArea allergiesField = new TextArea();
        allergiesField.setPromptText("Allergies");

        TextArea emergencyContactsField = new TextArea();
        emergencyContactsField.setPromptText("Emergency Contacts (comma-separated)");

        Button loadButton = new Button("Load Patient");
        Button addButton = new Button("Add Patient");
        Button viewAllButton = new Button("View All Records");

        loadButton.setOnAction(e -> loadPatientRecord(idField.getText(), nameField.getText()));
        addButton.setOnAction(e -> addPatientRecord(idField.getText(), nameField.getText(), medicalHistoryField.getText(),
                ongoingTreatmentsField.getText(), allergiesField.getText(), emergencyContactsField.getText()));
        viewAllButton.setOnAction(e -> viewAllRecords());

        recordsListView = new ListView<>();

        VBox vbox = new VBox(10, idField, nameField, medicalHistoryField, ongoingTreatmentsField,
                allergiesField, emergencyContactsField, loadButton, addButton, viewAllButton, recordsListView);
        Scene scene = new Scene(vbox, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Patient Records Management");
        primaryStage.show();
    }

    private JSONArray loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("patients", new JSONArray());
                writeFile(jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)));
            return new JSONObject(content).getJSONArray("patients");
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private void loadPatientRecord(String id, String name) {
        if (id.isEmpty() && name.isEmpty()) {
            showAlert("Please provide either ID or Name.");
            return;
        }
        boolean found = false;
        for (int i = 0; i < patients.length(); i++) {
            JSONObject patient = patients.getJSONObject(i);
            if (patient.getString("id").equals(id) || patient.getString("name").equalsIgnoreCase(name)) {
                showAlert("Patient Found: " + patient.toString());
                found = true;
                break;
            }
        }
        if (!found) {
            showAlert("No patient data found.");
        }
    }

    private void addPatientRecord(String id, String name, String medicalHistory, String ongoingTreatments, String allergies, String emergencyContacts) {
        // Check if the ID already exists
        for (int i = 0; i < patients.length(); i++) {
            JSONObject patient = patients.getJSONObject(i);
            if (patient.getString("id").equals(id)) {
                showAlert("A patient with this ID already exists. Please use a unique ID.");
                return;
            }
        }
        JSONObject newPatient = new JSONObject();
        newPatient.put("id", id);
        newPatient.put("name", name);
        newPatient.put("medicalHistory", medicalHistory);
        newPatient.put("ongoingTreatments", ongoingTreatments);
        newPatient.put("allergies", allergies);
        newPatient.put("emergencyContacts", new JSONArray(emergencyContacts.split(",")));

        // Add the new patient to the array and save it
        patients.put(newPatient);
        writeFile(new JSONObject().put("patients", patients).toString());
        showAlert("New patient record added successfully.");
    }


    private void writeFile(String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void viewAllRecords() {
        recordsListView.getItems().clear(); // Clear previous records
        if (patients.length() == 0) {
            showAlert("No patient data found.");
            return;
        }
        for (int i = 0; i < patients.length(); i++) {
            JSONObject patient = patients.getJSONObject(i);
            String record = "ID: " + patient.getString("id") +
                    ", Name: " + patient.getString("name") +
                    ", Medical History: " + patient.getString("medicalHistory") +
                    ", Ongoing Treatments: " + patient.getString("ongoingTreatments") +
                    ", Allergies: " + patient.getString("allergies") +
                    ", Emergency Contacts: " + patient.getJSONArray("emergencyContacts").toString();
            recordsListView.getItems().add(record);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
