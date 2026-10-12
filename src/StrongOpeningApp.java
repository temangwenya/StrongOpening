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
    
    // Screen 1: Pick a routine to start, delete, view history, or add exercises
    private void showTemplateSelector() {
        List<Template> templates = DatabaseManager.getAllTemplates();

        ListView<Template> listView = new ListView<>();
        listView.getItems().addAll(templates);
        
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

        Button deleteBtn = new Button("Delete Selected Routine");
        deleteBtn.setOnAction(e -> {
            Template selectedTemplate = listView.getSelectionModel().getSelectedItem();
            if (selectedTemplate != null) {
                DatabaseManager.deleteTemplate(selectedTemplate.getId());
                showAlert("Success", "Template deleted successfully!");
                showTemplateSelector(); // Refresh the list view
            } else {
                showAlert("Selection Error", "Please select a routine to delete!");
            }
        });

        Button historyBtn = new Button("View Workout History");
        historyBtn.setOnAction(e -> showHistoryScreen());

        Button addExerciseBtn = new Button("Add Custom Exercise");
        addExerciseBtn.setOnAction(e -> showAddExerciseScreen());

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(
            new Label("Select a Workout Routine:"), 
            listView, 
            startWorkoutBtn, 
            deleteBtn, 
            historyBtn, 
            addExerciseBtn
        );

        Scene scene = new Scene(root, 400, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    // Screen 3: Workout History Logbook View
    private void showHistoryScreen() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label header = new Label("Past Workout History");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        List<DatabaseManager.WorkoutHistoryItem> historyItems = DatabaseManager.getWorkoutHistory();

        ListView<String> historyListView = new ListView<>();
        if (historyItems.isEmpty()) {
            historyListView.getItems().add("No workouts logged yet. Go lift!");
        } else {
            for (DatabaseManager.WorkoutHistoryItem item : historyItems) {
                String entry = String.format("[%s] %s -> %s (Set %d: %.1f lbs x %d reps)", 
                    item.date, item.templateName, item.exerciseName, item.setNumber, item.weight, item.reps);
                historyListView.getItems().add(entry);
            }
        }

        Button backBtn = new Button("Back to Routines");
        backBtn.setOnAction(e -> showTemplateSelector());

        root.getChildren().addAll(header, historyListView, backBtn);

        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);
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

    // Screen 4: Add Custom Exercise Form
    private void showAddExerciseScreen() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label header = new Label("Add New Exercise");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField nameField = new TextField();
        nameField.setPromptText("Exercise name (e.g., Incline Bench Press)");

        Button saveBtn = new Button("Save Exercise");
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                DatabaseManager.addExercise(name);
                showAlert("Success", "Exercise added successfully!");
                showTemplateSelector(); // Return to main menu
            } else {
                showAlert("Error", "Exercise name cannot be empty.");
            }
        });

        Button backBtn = new Button("Back to Routines");
        backBtn.setOnAction(e -> showTemplateSelector());

        root.getChildren().addAll(header, new Label("Exercise Name:"), nameField, saveBtn, backBtn);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
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