import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox; 
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class StrongOpeningApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("StrongOpening - Weightlifting Tracker");

        // 1. Ensure database tables exist
        DatabaseManager.createTables();

        showTemplateSelector();
    }

    // Screen 1: Pick a routine to start
    private void showTemplateSelector() {
        List<Template> templates = DatabaseManager.getAllTemplates();

        ListView<Template> listView = new ListView<>();
        listView.getItems().addAll(templates);
        
        // Custom cell factory so it displays the template name nicely
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Template item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTemplateName() + " (" + item.getExercises().size() + " exercises)");
                }
            }
        });

        Button startWorkoutBtn = new Button("Start Selected Routine");
        startButtonAction(listView, startWorkoutBtn);

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(new Label("Select a Workout Routine:"), listView, startWorkoutBtn);

        Scene scene = new Scene(root, 400, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startButtonAction(ListView<Template> listView, Button btn) {
        btn.setOnAction(e -> {
            Template selectedTemplate = listView.getSelectionModel().getSelectedItem();
            if (selectedTemplate != null) {
                showActiveWorkoutScreen(selectedTemplate);
            } else {
                showAlert("Selection Error", "Please select a workout template first!");
            }
        });
    }

    // Screen 2: Active Workout Logger Screen
    private void showActiveWorkoutScreen(Template template) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label header = new Label("Active Workout: " + template.getTemplateName());
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox exercisesContainer = new VBox(15);
        List<ExerciseInputRow> inputRows = new ArrayList<>();

        // Create input fields for each exercise in the routine
        for (Exercise ex : template.getExercises()) {
            Label exLabel = new Label(ex.getName() + " (" + ex.getMuscleGroup() + ")");
            
            TextField weightField = new TextField();
            weightField.setPromptText("Weight (lbs/kg)");
            
            TextField repsField = new TextField();
            repsField.setPromptText("Reps");

            HBox setRow = new HBox(10, new Label("Set 1:"), weightField, repsField);
            VBox exBox = new VBox(5, exLabel, setRow);
            exercisesContainer.getChildren().add(exBox);

            // Track inputs for saving later
            inputRows.add(new ExerciseInputRow(ex.getId(), weightField, repsField));
        }

        ScrollPane scrollPane = new ScrollPane(exercisesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

        Button finishBtn = new Button("Finish & Save Workout");
        finishBtn.setOnAction(e -> {
            List<WorkoutSetData> setsToLog = new ArrayList<>();
            try {
                for (int i = 0; i < inputRows.size(); i++) {
                    ExerciseInputRow row = inputRows.get(i);
                    double weight = Double.parseDouble(row.weightField.getText());
                    int reps = Integer.parseInt(row.repsField.getText());
                    
                    // Log set 1 for this exercise as an example
                    setsToLog.add(new WorkoutSetData(row.exerciseId, 1, weight, reps));
                }

                // Save using your existing backend method!
                DatabaseManager.logWorkout(template.getId(), setsToLog);
                showAlert("Success", "Workout logged and saved successfully!");
                showTemplateSelector(); // Return to main menu

            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter valid numbers for weight and reps.");
            }
        });

        Button backBtn = new Button("Cancel");
        backBtn.setOnAction(e -> showTemplateSelector());

        root.getChildren().addAll(header, scrollPane, finishBtn, backBtn);

        Scene scene = new Scene(root, 450, 450);
        primaryStage.setScene(scene);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Helper class to hold UI references
    private static class ExerciseInputRow {
        int exerciseId;
        TextField weightField;
        TextField repsField;

        public ExerciseInputRow(int exerciseId, TextField weightField, TextField repsField) {
            this.exerciseId = exerciseId;
            this.weightField = weightField;
            this.repsField = repsField;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}