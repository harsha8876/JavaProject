package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class HospitalAnalyticsApp extends Application {
    private TextField patientInflowField, doctorPerformanceField, systemEfficiencyField;
    private final String jsonFilePath = "hospital_analytics.json";

    // Counters for doctor performance and system efficiency
    private int casesHandledByDoctor = 0;
    private int successfulOutcomes = 0;

    @Override
    public void start(Stage primaryStage) {
        // Initialize UI components
        patientInflowField = new TextField();
        doctorPerformanceField = new TextField();
        systemEfficiencyField = new TextField();

        patientInflowField.setPromptText("Patient Inflow");
        doctorPerformanceField.setPromptText("Doctor Performance (%)");
        systemEfficiencyField.setPromptText("System Efficiency (%)");

        // Buttons
        Button saveButton = new Button("Save Data");
        Button loadButton = new Button("Load Data");
        Button addCaseButton = new Button("Add Case Handled");
        Button addOutcomeButton = new Button("Add Successful Outcome");

        saveButton.setOnAction(e -> saveData());
        loadButton.setOnAction(e -> loadData());
        addCaseButton.setOnAction(e -> incrementDoctorPerformance());
        addOutcomeButton.setOnAction(e -> incrementSystemEfficiency());

        // Layout and styling
        VBox layout = new VBox(10,
                createStyledLabel("Hospital Analytics", 18, Color.DARKBLUE),
                patientInflowField, doctorPerformanceField, systemEfficiencyField,
                new HBox(10, addCaseButton, addOutcomeButton),
                new HBox(10, saveButton, loadButton)
        );
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4f4f4;");

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Hospital Analytics");
        primaryStage.show();

        // Load data after UI components are initialized
        loadData();
    }


    private Label createStyledLabel(String text, int fontSize, Color color) {
        Label label = new Label(text);
        label.setFont(new Font(fontSize));
        label.setTextFill(color);
        return label;
    }

    private void incrementDoctorPerformance() {
        casesHandledByDoctor++;
        doctorPerformanceField.setText(String.valueOf(calculateDoctorPerformance()));
        saveData();
    }

    private void incrementSystemEfficiency() {
        successfulOutcomes++;
        systemEfficiencyField.setText(String.valueOf(calculateSystemEfficiency()));
        saveData();
    }

    private int calculateDoctorPerformance() {
        // Example calculation based on handled cases
        return casesHandledByDoctor * 10;  // Placeholder formula
    }

    private int calculateSystemEfficiency() {
        // Example calculation based on successful outcomes
        return successfulOutcomes * 5;  // Placeholder formula
    }

    private void saveData() {
        try {
            JSONObject analyticsData = new JSONObject();
            analyticsData.put("patientInflow", Integer.parseInt(patientInflowField.getText()));
            analyticsData.put("doctorPerformance", calculateDoctorPerformance());
            analyticsData.put("systemEfficiency", calculateSystemEfficiency());
            analyticsData.put("casesHandledByDoctor", casesHandledByDoctor);
            analyticsData.put("successfulOutcomes", successfulOutcomes);

            try (FileWriter file = new FileWriter(jsonFilePath)) {
                file.write(analyticsData.toString(4));  // Pretty-print JSON for readability
                file.flush();
            }
            showAlert("Data saved successfully!");
        } catch (Exception e) {
            showAlert("Error saving data: " + e.getMessage());
        }
    }

    private void loadData() {
        File file = new File(jsonFilePath);
        if (!file.exists()) {
            showAlert("No saved data found. Please save data first.");
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            StringBuilder jsonContent = new StringBuilder();
            int i;
            while ((i = reader.read()) != -1) {
                jsonContent.append((char) i);
            }

            JSONObject analyticsData = new JSONObject(jsonContent.toString());
            patientInflowField.setText(String.valueOf(analyticsData.getInt("patientInflow")));
            doctorPerformanceField.setText(String.valueOf(analyticsData.getInt("doctorPerformance")));
            systemEfficiencyField.setText(String.valueOf(analyticsData.getInt("systemEfficiency")));

            showAlert("Data loaded successfully!");
        } catch (IOException e) {
            showAlert("Error loading data: " + e.getMessage());
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
