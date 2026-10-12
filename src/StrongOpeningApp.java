/**
 * File Info:
 * Main JavaFX Application class for the StrongOpening application.
 * Orchestrates the retro fighting game landing screen, arcade dashboard, 
 * fighter-select spring bounce animations, active workout logger, history logbook, 
 * PR tracking, and CSS styling injection.
 */

import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox; 
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class StrongOpeningApp extends Application {

    // Central primary stage reference for window management and scene transitions
    private Stage primaryStage;

    /**
     * JavaFX application entry point.
     * Initializes window parameters, verifies database relational schemas, 
     * and boots into the retro arcade fighting game landing page.
     */
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("STRONG OPENING // ARCADE TERMINAL");

        // Ensure all SQLite database tables are created on launch[cite: 1, 2]
        DatabaseManager.createTables();

        // Boot into the arcade fighting game title screen[cite: 2]
        showLandingScreen();
    }

    /**
     * Landing Screen: Retro arcade fighting game title screen.
     * Features cyberpunk neon styling, glowing drop shadows, and a flashing "PRESS START" button.
     */
    private void showLandingScreen() {
        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);

        // Subtitle header matching the Neo-Tokyo arcade theme
        Label subTitle = new Label("NEO-TOKYO LIFTING LEAGUE");
        subTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #00f0ff; -fx-effect: dropshadow(three-pass-box, rgba(0, 240, 255, 0.6), 8, 0.0, 0, 0);");

        // Main game title styling (Strong)
        Label titleMain = new Label("STRONG");
        titleMain.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: #ff2a85; -fx-effect: dropshadow(three-pass-box, rgba(255, 42, 133, 0.8), 12, 0.0, 0, 0);");

        // Main game title styling (Opening)
        Label titleSub = new Label("OPENING");
        titleSub.setStyle("-fx-font-size: 38px; -fx-font-weight: bold; -fx-text-fill: #9d4edd; -fx-effect: dropshadow(three-pass-box, rgba(157, 78, 221, 0.8), 12, 0.0, 0, 0);");

        // Flavor arcade tag line
        Label flavorLabel = new Label("INSERT COIN TO BUILD MASS");
        flavorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #e0aaff;");

        // Flashing "PRESS START" coin action button
        Button startCoinBtn = new Button(">> PRESS START TO LIFT <<");
        startCoinBtn.setStyle(
            "-fx-background-color: #ff2a85; " +
            "-fx-text-fill: #0b0614; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 15px 30px; " +
            "-fx-background-radius: 0px; " +
            "-fx-border-color: #00f0ff; " +
            "-fx-border-width: 2px;"
        );

        // Animation timer to create a pulsing arcade cabinet button effect
        AnimationTimer pulseTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            private boolean bright = true;
            @Override
            public void handle(long now) {
                if (now - lastUpdate > 500_000_000) { // Toggle every 0.5 seconds
                    bright = !bright;
                    if (bright) {
                        startCoinBtn.setStyle(
                            "-fx-background-color: #ff2a85; -fx-text-fill: #0b0614; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 15px 30px; -fx-background-radius: 0px; -fx-border-color: #00f0ff; -fx-border-width: 2px;"
                        );
                    } else {
                        startCoinBtn.setStyle(
                            "-fx-background-color: #1a0b2e; -fx-text-fill: #ff2a85; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 15px 30px; -fx-background-radius: 0px; -fx-border-color: #ff2a85; -fx-border-width: 2px;"
                        );
                    }
                    lastUpdate = now;
                }
            }
        };
        pulseTimer.start();

        // Transition from landing page to main routine dashboard
        startCoinBtn.setOnAction(e -> {
            pulseTimer.stop();
            showTemplateSelector();
        });

        root.getChildren().addAll(subTitle, titleMain, titleSub, flavorLabel, new Label("\n"), startCoinBtn);

        createStyledScene(root, 440, 520);
    }
 
    /**
     * Screen 1: Main Routine Selector & Dashboard.
     * Features an interactive fighting-game character select card that slides and spring-bounces 
     * when a routine is clicked, alongside structured grid controls.
     */
    private void showTemplateSelector() {
        List<Template> templates = DatabaseManager.getAllTemplates();

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("SYSTEM: SELECT FIGHTER ROUTINE");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #ff2a85;");

        // --- Arcade Character / Routine Preview Card (Slide & Bounce) ---
        VBox characterCard = new VBox(8);
        characterCard.setAlignment(Pos.CENTER);
        characterCard.setPadding(new Insets(15));
        characterCard.setStyle(
            "-fx-background-color: #120926; " +
            "-fx-border-color: #00f0ff; " +
            "-fx-border-width: 2px; " +
            "-fx-background-radius: 0px;"
        );
        characterCard.setPrefHeight(110);

        Label cardTitle = new Label(">> SELECT A ROUTINE <<");
        cardTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #00f0ff; -fx-font-weight: bold;");
        
        Label cardDetails = new Label("Awaiting fighter selection...");
        cardDetails.setStyle("-fx-font-size: 12px; -fx-text-fill: #e0aaff;");

        characterCard.getChildren().addAll(cardTitle, cardDetails);

        // --- Routine List View Component ---
        ListView<Template> listView = new ListView<>();
        listView.getItems().addAll(templates);
        listView.setPrefHeight(130);
        
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Template item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("> " + item.getTemplateName().toUpperCase() + " [" + item.getExercises().size() + " EXS]");
                }
            }
        });

        // Trigger the arcade slide-and-bounce spring animation when a routine is selected
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cardTitle.setText("FIGHTER: " + newVal.getTemplateName().toUpperCase());
                cardDetails.setText("LOADED EXERCISES: " + newVal.getExercises().size() + " MODULES");
                playArcadeBounceAnimation(characterCard);
            }
        });

        // --- Action Buttons ---
        Button startWorkoutBtn = new Button("START WORKOUT");
        startWorkoutBtn.setMaxWidth(Double.MAX_VALUE);
        startButtonAction(listView, startWorkoutBtn);

        Button historyBtn = new Button("VIEW HISTORY");
        historyBtn.setMaxWidth(Double.MAX_VALUE);
        historyBtn.setOnAction(e -> showHistoryScreen());

        Button addExerciseBtn = new Button("ADD EXERCISE");
        addExerciseBtn.setMaxWidth(Double.MAX_VALUE);
        addExerciseBtn.setOnAction(e -> showAddExerciseScreen());

        Button prBtn = new Button("PERSONAL RECS");
        prBtn.setMaxWidth(Double.MAX_VALUE);
        prBtn.setOnAction(e -> showPrScreen());

        Button deleteBtn = new Button("DELETE ROUTINE");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setStyle("-fx-border-color: #ff3333; -fx-text-fill: #ff3333;");
        deleteBtn.setOnAction(e -> {
            Template selectedTemplate = listView.getSelectionModel().getSelectedItem();
            if (selectedTemplate != null) {
                DatabaseManager.deleteTemplate(selectedTemplate.getId());
                showAlert("SUCCESS", "Template deleted from core memory!");
                showTemplateSelector();
            } else {
                showAlert("ERROR", "Select a routine module first!");
            }
        });

        // Clean structured 2x2 grid layout for dashboard controls
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.add(startWorkoutBtn, 0, 0, 2, 1);
        buttonGrid.add(historyBtn, 0, 1);
        buttonGrid.add(prBtn, 1, 1);
        buttonGrid.add(addExerciseBtn, 0, 2);
        buttonGrid.add(deleteBtn, 1, 2);

        root.getChildren().addAll(titleLabel, characterCard, new Label("CHOOSE ROUTINE MODULE:"), listView, buttonGrid);

        createStyledScene(root, 420, 620);
    }

    /**
     * Arcade Fighter Select Slide & Bounce Animation Helper.
     * Simulates a classic fighting game character select snap-in with an elastic spring overshoot.
     */
    private void playArcadeBounceAnimation(VBox card) {
        AnimationTimer bounceTimer = new AnimationTimer() {
            private long startTime = -1;
            private final double duration = 300_000_000.0; // 300ms duration

            @Override
            public void handle(long now) {
                if (startTime == -1) startTime = now;
                double elapsed = now - startTime;
                double progress = elapsed / duration;

                if (progress >= 1.0) {
                    card.setTranslateX(0);
                    card.setScaleX(1.0);
                    card.setScaleY(1.0);
                    stop();
                } else {
                    // Ease-out elastic calculation for bounce effect
                    double easeOutElastic = 1 + --progress * progress * progress * (2.7 * progress + 1.7);
                    double translation = -100 * (1 - easeOutElastic);
                    card.setTranslateX(translation);
                }
            }
        };
        bounceTimer.start();
    }

    /**
     * Screen 2: Active Workout Logger Screen.
     * Generates input fields for weight and reps for each exercise mapped to the selected template.
     */
    private void showActiveWorkoutScreen(Template template) {
        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label header = new Label("LOGGING: " + template.getTemplateName().toUpperCase());
        header.setStyle("-fx-font-size: 16px; -fx-text-fill: #00f0ff;");

        VBox exercisesContainer = new VBox(12);
        List<ExerciseInputRow> inputRows = new ArrayList<>();

        for (Exercise ex : template.getExercises()) {
            Label exLabel = new Label(ex.getName().toUpperCase() + " (" + ex.getMuscleGroup() + ")");
            
            TextField weightField = new TextField();
            weightField.setPromptText("WEIGHT");
            
            TextField repsField = new TextField();
            repsField.setPromptText("REPS");

            HBox setRow = new HBox(10, new Label("SET 1:"), weightField, repsField);
            setRow.setAlignment(Pos.CENTER_LEFT);
            
            VBox exBox = new VBox(5, exLabel, setRow);
            exercisesContainer.getChildren().add(exBox);

            inputRows.add(new ExerciseInputRow(ex.getId(), weightField, repsField));
        }

        ScrollPane scrollPane = new ScrollPane(exercisesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(280);

        Button finishBtn = new Button("COMMIT & SAVE WORKOUT");
        finishBtn.setMaxWidth(Double.MAX_VALUE);
        finishBtn.setOnAction(e -> {
            List<WorkoutSetData> setsToLog = new ArrayList<>();
            try {
                for (ExerciseInputRow row : inputRows) {
                    double weight = Double.parseDouble(row.weightField.getText());
                    int reps = Integer.parseInt(row.repsField.getText());
                    setsToLog.add(new WorkoutSetData(row.exerciseId, 1, weight, reps));
                }

                DatabaseManager.logWorkout(template.getId(), setsToLog);
                showAlert("SUCCESS", "Workout telemetry recorded!");
                showTemplateSelector();
            } catch (NumberFormatException ex) {
                showAlert("INPUT ERROR", "Numeric values required for weight & reps.");
            }
        });

        Button backBtn = new Button("ABORT SESSION");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> showTemplateSelector());

        root.getChildren().addAll(header, scrollPane, finishBtn, backBtn);

        createStyledScene(root, 440, 520);
    }

    /**
     * Screen 3: Workout History Logbook View.
     * Displays past completed workout sets ordered chronologically.
     */
    private void showHistoryScreen() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label header = new Label("ARCHIVED TELEMETRY (HISTORY)");
        header.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff2a85;");

        List<DatabaseManager.WorkoutHistoryItem> historyItems = DatabaseManager.getWorkoutHistory();

        ListView<String> historyListView = new ListView<>();
        if (historyItems.isEmpty()) {
            historyListView.getItems().add("> NO LOGGED DATA FOUND.");
        } else {
            for (DatabaseManager.WorkoutHistoryItem item : historyItems) {
                String entry = String.format("[%s] %s -> %s (SET %d: %.1fLBS x %DR)", 
                    item.date, item.templateName.toUpperCase(), item.exerciseName.toUpperCase(), item.setNumber, item.weight, item.reps);
                historyListView.getItems().add(entry);
            }
        }

        Button backBtn = new Button("RETURN TO MENU");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> showTemplateSelector());

        root.getChildren().addAll(header, historyListView, backBtn);

        createStyledScene(root, 440, 480);
    }

    /**
     * Screen 4: Add Custom Exercise Form.
     * Allows registering new custom exercises directly into the database.
     */
    private void showAddExerciseScreen() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label header = new Label("REGISTER NEW EXERCISE");
        header.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff2a85;");

        TextField nameField = new TextField();
        nameField.setPromptText("EXERCISE TITLE (E.G. NEON CURL)");

        Button saveBtn = new Button("WRITE TO DATABASE");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                DatabaseManager.addExercise(name);
                showAlert("SUCCESS", "Exercise registered successfully!");
                showTemplateSelector();
            } else {
                showAlert("ERROR", "Exercise title cannot be blank.");
            }
        });

        Button backBtn = new Button("RETURN TO MENU");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> showTemplateSelector());

        root.getChildren().addAll(header, new Label("ENTER IDENTIFIER:"), nameField, saveBtn, backBtn);

        createStyledScene(root, 400, 360);
    }

    /**
     * Screen 5: Personal Records View.
     * Queries and displays all-time maximum weight records per exercise.
     */
    private void showPrScreen() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label header = new Label("ALL-TIME PEAK METRICS (PRs)");
        header.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff2a85;");

        List<DatabaseManager.PersonalRecord> prs = DatabaseManager.getPersonalRecords();

        ListView<String> prListView = new ListView<>();
        if (prs.isEmpty()) {
            prListView.getItems().add("> NO PEAK RECORDS ESTABLISHED YET.");
        } else {
            for (DatabaseManager.PersonalRecord pr : prs) {
                String entry = String.format("%s --> %.1f LBS MAX", pr.exerciseName.toUpperCase(), pr.maxWeight);
                prListView.getItems().add(entry);
            }
        }

        Button backBtn = new Button("RETURN TO MENU");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> showTemplateSelector());

        root.getChildren().addAll(header, prListView, backBtn);

        createStyledScene(root, 440, 480);
    }

    /**
     * Helper action binder for selecting and starting workout routines.
     */
    private void startButtonAction(ListView<Template> listView, Button btn) {
        btn.setOnAction(e -> {
            Template selectedTemplate = listView.getSelectionModel().getSelectedItem();
            if (selectedTemplate != null) {
                showActiveWorkoutScreen(selectedTemplate);
            } else {
                showAlert("SELECTION ERROR", "Select a template module to initiate!");
            }
        });
    }

    /**
     * Standardized arcade pop-up alert dialog utility.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Data Transfer Object class for tracking input controls during active workout sessions.
     */
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

    /**
     * Centralized scene builder helper.
     * Injects the cyberpunk arcade stylesheet (`style.css`), updates stage settings, and displays the window.
     */
    private Scene createStyledScene(VBox root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        try {
            String cssPath = getClass().getResource("style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            try {
                String fallbackPath = new java.io.File("bin/style.css").toURI().toString();
                scene.getStylesheets().add(fallbackPath);
            } catch (Exception ex) {
                System.out.println("Could not load stylesheet: " + ex.getMessage());
            }
        }
        primaryStage.setScene(scene);
        primaryStage.show();
        return scene;
    }

    /**
     * Main application execution launcher.
     */
    public static void main(String[] args) {
        launch(args);
    }
}