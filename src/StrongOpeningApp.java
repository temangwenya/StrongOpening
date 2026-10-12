/**
 * File Info:
 * Main JavaFX Application class for StrongOpening. Features a Tokyo Arcade synthwave 
 * aesthetic, retro fighting game landing screen, interactive fighter-select bounce cards, 
 * routine creator GUI, active workout logger, and modular scene navigation.
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

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("STRONG OPENING // ARCADE TERMINAL");

        DatabaseManager.createTables();

        showLandingScreen();
    }

    /**
     * Landing Screen: Retro arcade fighting game title screen with 
     * intense cyberpunk gradients, glowing title text, and a flashing "PRESS START" prompt.
     */
    private void showLandingScreen() {
        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);

        Label subTitle = new Label("NEO-TOKYO LIFTING LEAGUE");
        subTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #00f0ff; -fx-effect: dropshadow(three-pass-box, rgba(0, 240, 255, 0.6), 8, 0.0, 0, 0);");

        Label titleMain = new Label("STRONG");
        titleMain.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: #ff2a85; -fx-effect: dropshadow(three-pass-box, rgba(255, 42, 133, 0.8), 12, 0.0, 0, 0);");

        Label titleSub = new Label("OPENING");
        titleSub.setStyle("-fx-font-size: 38px; -fx-font-weight: bold; -fx-text-fill: #9d4edd; -fx-effect: dropshadow(three-pass-box, rgba(157, 78, 221, 0.8), 12, 0.0, 0, 0);");

        Label flavorLabel = new Label("INSERT COIN TO BUILD MASS");
        flavorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #e0aaff;");

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

        AnimationTimer pulseTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            private boolean bright = true;
            @Override
            public void handle(long now) {
                if (now - lastUpdate > 500_000_000) {
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

        startCoinBtn.setOnAction(e -> {
            pulseTimer.stop();
            showTemplateSelector();
        });

        root.getChildren().addAll(subTitle, titleMain, titleSub, flavorLabel, new Label("\n"), startCoinBtn);

        createStyledScene(root, 440, 520);
    }
 
    /**
     * Screen 1: Main arcade dashboard featuring fighter-select slide-and-bounce mechanics.
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

        Button createRoutineBtn = new Button("CREATE ROUTINE");
        createRoutineBtn.setMaxWidth(Double.MAX_VALUE);
        createRoutineBtn.setOnAction(e -> showCreateRoutineScreen());

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

        // Structured 3x2 Grid for Dashboard Controls
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.add(startWorkoutBtn, 0, 0, 2, 1);
        buttonGrid.add(createRoutineBtn, 0, 1);
        buttonGrid.add(historyBtn, 1, 1);
        buttonGrid.add(prBtn, 0, 2);
        buttonGrid.add(addExerciseBtn, 1, 2);
        buttonGrid.add(deleteBtn, 0, 3, 2, 1);

        root.getChildren().addAll(titleLabel, characterCard, new Label("CHOOSE ROUTINE MODULE:"), listView, buttonGrid);

        createStyledScene(root, 440, 660);
    }

    /**
     * Screen 2: Routine Creator GUI.
     * Allows building custom workout templates by specifying a routine name and selecting exercises.
     */
    private void showCreateRoutineScreen() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label header = new Label("CONSTRUCT NEW ROUTINE MODULE");
        header.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff2a85;");

        TextField routineNameField = new TextField();
        routineNameField.setPromptText("ROUTINE TITLE (E.G. PUSH DAY A)");

        Label selectLabel = new Label("SELECT EXERCISES TO INCLUDE:");
        
        List<Exercise> allExercises = DatabaseManager.getAllExercises();
        ListView<CheckBox> exerciseListView = new ListView<>();
        
        for (Exercise ex : allExercises) {
            CheckBox cb = new CheckBox(ex.getName().toUpperCase() + " (" + ex.getMuscleGroup() + ")");
            cb.setUserData(ex.getId());
            cb.setStyle("-fx-text-fill: #00f0ff; -fx-font-family: 'Courier New', monospace;");
            exerciseListView.getItems().add(cb);
        }
        exerciseListView.setPrefHeight(180);

        Button saveBtn = new Button("BUILD ROUTINE MODULE");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> {
            String name = routineNameField.getText().trim();
            List<Integer> selectedIds = new ArrayList<>();
            
            for (CheckBox cb : exerciseListView.getItems()) {
                if (cb.isSelected()) {
                    selectedIds.add((Integer) cb.getUserData());
                }
            }

            if (name.isEmpty()) {
                showAlert("ERROR", "Routine title cannot be blank.");
            } else if (selectedIds.isEmpty()) {
                showAlert("ERROR", "Select at least one exercise for the routine.");
            } else {
                boolean success = DatabaseManager.createTemplate(name, selectedIds);
                if (success) {
                    showAlert("SUCCESS", "Routine module successfully compiled into core memory!");
                    showTemplateSelector();
                } else {
                    showAlert("DATABASE ERROR", "Failed to compile routine module.");
                }
            }
        });

        Button backBtn = new Button("RETURN TO MENU");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> showTemplateSelector());

        root.getChildren().addAll(header, routineNameField, selectLabel, exerciseListView, saveBtn, backBtn);

        createStyledScene(root, 440, 540);
    }

    /**
     * Arcade Fighter Select Slide & Bounce Animation Helper.
     */
    private void playArcadeBounceAnimation(VBox card) {
        AnimationTimer bounceTimer = new AnimationTimer() {
            private long startTime = -1;
            private final double duration = 300_000_000.0;

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
                    double easeOutElastic = 1 + --progress * progress * progress * (2.7 * progress + 1.7);
                    double translation = -100 * (1 - easeOutElastic);
                    card.setTranslateX(translation);
                }
            }
        };
        bounceTimer.start();
    }

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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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

    public static void main(String[] args) {
        launch(args);
    }
}