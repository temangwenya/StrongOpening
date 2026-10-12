/**
 * File Info:
 * Main JavaFX Application class for StrongOpening. Features a Tokyo Arcade synthwave 
 * aesthetic, retro fighting game landing screen, interactive fighter-select bounce cards, 
 * advanced routine compiler, match countdown, and active workout tracking.
 */

// Imports
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
 
    private void showTemplateSelector() {
        List<Template> templates = DatabaseManager.getAllTemplates();

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("SYSTEM: SELECT FIGHTER ROUTINE");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #ff2a85;");

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

    private static class RoutineExerciseRow {
        Exercise exercise;
        int restTimerSeconds = 60;
        VBox setsContainer = new VBox(5);
        List<String> setTypes = new ArrayList<>();
        Label timerDisplayLabel;

        public RoutineExerciseRow(Exercise exercise) {
            this.exercise = exercise;
            setTypes.add("WARMUP");
            setTypes.add("WORK");
        }
    }

    private void showCreateRoutineScreen() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label header = new Label("ADVANCED ROUTINE COMPILER");
        header.setStyle("-fx-font-size: 16px; -fx-text-fill: #ff2a85; -fx-font-weight: bold;");

        TextField routineNameField = new TextField();
        routineNameField.setPromptText("ROUTINE TITLE (E.G. PUSH DAY A)");

        HBox quickAddBox = new HBox(8);
        quickAddBox.setAlignment(Pos.CENTER);
        TextField newExNameField = new TextField();
        newExNameField.setPromptText("NEW EXERCISE NAME");
        Button addNewExBtn = new Button("+ ADD EXERCISE");
        addNewExBtn.setStyle("-fx-background-color: #1a0b2e; -fx-text-fill: #00f0ff; -fx-border-color: #00f0ff;");
        
        quickAddBox.getChildren().addAll(newExNameField, addNewExBtn);

        ComboBox<Exercise> exerciseComboBox = new ComboBox<>();
        refreshExerciseComboBox(exerciseComboBox);

        Button addSelectedExBtn = new Button("INCLUDE EXERCISE IN ROUTINE");
        addSelectedExBtn.setMaxWidth(Double.MAX_VALUE);

        VBox routineExercisesContainer = new VBox(10);
        List<RoutineExerciseRow> routineRows = new ArrayList<>();

        addNewExBtn.setOnAction(e -> {
            String name = newExNameField.getText().trim();
            if (!name.isEmpty()) {
                DatabaseManager.addExercise(name);
                newExNameField.clear();
                refreshExerciseComboBox(exerciseComboBox);
                showAlert("SUCCESS", "Registered new exercise to core memory!");
            } else {
                showAlert("ERROR", "Exercise name cannot be empty.");
            }
        });

        addSelectedExBtn.setOnAction(e -> {
            Exercise selected = exerciseComboBox.getValue();
            if (selected != null) {
                RoutineExerciseRow row = new RoutineExerciseRow(selected);
                routineRows.add(row);
                
                VBox card = renderExerciseConfigCard(row, routineRows, routineExercisesContainer);
                routineExercisesContainer.getChildren().add(card);
            } else {
                showAlert("SELECTION ERROR", "Choose an exercise from the drop-down list.");
            }
        });

        ScrollPane scrollPane = new ScrollPane(routineExercisesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(260);

        Button saveRoutineBtn = new Button("BUILD & SAVE ROUTINE MODULE");
        saveRoutineBtn.setMaxWidth(Double.MAX_VALUE);
        saveRoutineBtn.setOnAction(e -> {
            String name = routineNameField.getText().trim();
            if (name.isEmpty()) {
                showAlert("ERROR", "Please enter a routine title.");
                return;
            }
            if (routineRows.isEmpty()) {
                showAlert("ERROR", "Add at least one exercise to the routine.");
                return;
            }

            List<DatabaseManager.TemplateExerciseConfig> configs = new ArrayList<>();
            for (RoutineExerciseRow r : routineRows) {
                configs.add(new DatabaseManager.TemplateExerciseConfig(r.exercise.getId(), r.restTimerSeconds, r.setTypes));
            }

            boolean success = DatabaseManager.createTemplateDetailed(name, configs);
            if (success) {
                showAlert("SUCCESS", "Routine compiled and stored in core memory!");
                showTemplateSelector();
            } else {
                showAlert("DATABASE ERROR", "Failed to compile routine module.");
            }
        });

        Button backBtn = new Button("RETURN TO MENU");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> showTemplateSelector());

        root.getChildren().addAll(header, routineNameField, quickAddBox, new HBox(10, exerciseComboBox, addSelectedExBtn), scrollPane, saveRoutineBtn, backBtn);

        createStyledScene(root, 480, 680);
    }

    private VBox renderExerciseConfigCard(RoutineExerciseRow row, List<RoutineExerciseRow> allRows, VBox container) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: #120926; -fx-border-color: #00f0ff; -fx-border-width: 1px; -fx-padding: 10px;");

        Label title = new Label(row.exercise.getName().toUpperCase());
        title.setStyle("-fx-text-fill: #ff2a85; -fx-font-weight: bold;");

        row.timerDisplayLabel = new Label("REST TIMER: " + row.restTimerSeconds + "S");
        row.timerDisplayLabel.setStyle("-fx-text-fill: #e0aaff; -fx-font-size: 11px;");

        Button minus10Btn = new Button("-10S");
        minus10Btn.setStyle("-fx-font-size: 10px;");
        minus10Btn.setOnAction(e -> {
            if (row.restTimerSeconds >= 10) {
                row.restTimerSeconds -= 10;
                row.timerDisplayLabel.setText("REST TIMER: " + row.restTimerSeconds + "S");
            }
        });

        Button plus10Btn = new Button("+10S");
        plus10Btn.setStyle("-fx-font-size: 10px;");
        plus10Btn.setOnAction(e -> {
            row.restTimerSeconds += 10;
            row.timerDisplayLabel.setText("REST TIMER: " + row.restTimerSeconds + "S");
        });

        HBox timerControls = new HBox(8, row.timerDisplayLabel, minus10Btn, plus10Btn);
        timerControls.setAlignment(Pos.CENTER_LEFT);

        Button addWarmupBtn = new Button("+ WARMUP");
        Button addWorkBtn = new Button("+ WORK");
        Button addDropBtn = new Button("+ DROP");
        Button addFailBtn = new Button("+ FAILURE");

        addWarmupBtn.setStyle("-fx-font-size: 9px;");
        addWorkBtn.setStyle("-fx-font-size: 9px;");
        addDropBtn.setStyle("-fx-font-size: 9px;");
        addFailBtn.setStyle("-fx-font-size: 9px;");

        addWarmupBtn.setOnAction(e -> { row.setTypes.add("WARMUP"); updateSetsUI(row); });
        addWorkBtn.setOnAction(e -> { row.setTypes.add("WORK"); updateSetsUI(row); });
        addDropBtn.setOnAction(e -> { row.setTypes.add("DROP"); updateSetsUI(row); });
        addFailBtn.setOnAction(e -> { row.setTypes.add("FAILURE"); updateSetsUI(row); });

        HBox setButtons = new HBox(5, addWarmupBtn, addWorkBtn, addDropBtn, addFailBtn);

        Button removeExBtn = new Button("REMOVE EXERCISE");
        removeExBtn.setStyle("-fx-border-color: #ff3333; -fx-text-fill: #ff3333; -fx-font-size: 10px;");
        removeExBtn.setOnAction(e -> {
            allRows.remove(row);
            container.getChildren().remove(card);
        });

        updateSetsUI(row);

        card.getChildren().addAll(new HBox(15, title, removeExBtn), timerControls, setButtons, row.setsContainer);
        return card;
    }

    private void updateSetsUI(RoutineExerciseRow row) {
        row.setsContainer.getChildren().clear();
        for (int i = 0; i < row.setTypes.size(); i++) {
            final int index = i;
            String type = row.setTypes.get(i);
            Label setLabel = new Label("SET " + (i + 1) + " [" + type + "]");
            setLabel.setStyle("-fx-text-fill: #00f0ff; -fx-font-size: 11px;");

            Button deleteSetBtn = new Button("X");
            deleteSetBtn.setStyle("-fx-text-fill: #ff3333; -fx-font-size: 9px; -fx-padding: 2px 5px;");
            deleteSetBtn.setOnAction(e -> {
                row.setTypes.remove(index);
                updateSetsUI(row);
            });

            HBox setRow = new HBox(8, setLabel, deleteSetBtn);
            setRow.setAlignment(Pos.CENTER_LEFT);
            row.setsContainer.getChildren().add(setRow);
        }
    }

    private void refreshExerciseComboBox(ComboBox<Exercise> combo) {
        combo.getItems().clear();
        combo.getItems().addAll(DatabaseManager.getAllExercises());
        if (!combo.getItems().isEmpty()) {
            combo.getSelectionModel().selectFirst();
        }
    }

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
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0b0614;");

        Label announcerLabel = new Label("GET READY FOR BATTLE");
        announcerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #00f0ff; -fx-font-weight: bold;");

        Label countLabel = new Label("3");
        countLabel.setStyle("-fx-font-size: 64px; -fx-font-weight: bold; -fx-text-fill: #ff2a85; -fx-effect: dropshadow(three-pass-box, rgba(255, 42, 133, 0.8), 16, 0.0, 0, 0);");

        Label subAnnounce = new Label("ROUTINE: " + template.getTemplateName().toUpperCase());
        subAnnounce.setStyle("-fx-font-size: 14px; -fx-text-fill: #e0aaff;");

        root.getChildren().addAll(announcerLabel, countLabel, subAnnounce);
        createStyledScene(root, 440, 520);

        AnimationTimer matchIntroTimer = new AnimationTimer() {
            private long lastNanoTime = -1;
            private int currentCount = 3;
            private boolean foughtDisplayed = false;
            private long fightShowTime = -1;

            @Override
            public void handle(long now) {
                if (lastNanoTime == -1) {
                    lastNanoTime = now;
                    return;
                }

                if (!foughtDisplayed && now - lastNanoTime >= 1_000_000_000L) {
                    currentCount--;
                    lastNanoTime = now;
                    if (currentCount > 0) {
                        countLabel.setText(String.valueOf(currentCount));
                    } else if (currentCount == 0) {
                        countLabel.setText("READY");
                        countLabel.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: #00f0ff;");
                    } else {
                        countLabel.setText("FIGHT!");
                        countLabel.setStyle("-fx-font-size: 52px; -fx-font-weight: bold; -fx-text-fill: #ff2a85; -fx-effect: dropshadow(three-pass-box, rgba(255, 42, 133, 1), 20, 0, 0, 0);");
                        foughtDisplayed = true;
                        fightShowTime = now;
                    }
                } else if (foughtDisplayed && now - fightShowTime >= 800_000_000L) {
                    stop();
                    showActualWorkoutLogger(template);
                }
            }
        };
        matchIntroTimer.start();
    }

    private int activeRestSeconds = 60;
    private AnimationTimer currentRestAnimationTimer;

    private void showActualWorkoutLogger(Template template) {
        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label header = new Label("FIGHTING: " + template.getTemplateName().toUpperCase());
        header.setStyle("-fx-font-size: 16px; -fx-text-fill: #00f0ff;");

        Label restTimerLabel = new Label("REST TIMER: --:--");
        restTimerLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ff2a85; -fx-font-weight: bold;");

        Button restMinus10Btn = new Button("-10S");
        Button restPlus10Btn = new Button("+10S");
        Button startRestBtn = new Button("START 60S");

        restMinus10Btn.setOnAction(e -> activeRestSeconds = Math.max(0, activeRestSeconds - 10));
        restPlus10Btn.setOnAction(e -> activeRestSeconds += 10);
        startRestBtn.setOnAction(e -> startRestCountdownDynamic(restTimerLabel, 60));

        HBox restBox = new HBox(8, restTimerLabel, startRestBtn, restMinus10Btn, restPlus10Btn);
        restBox.setAlignment(Pos.CENTER);

        VBox exercisesContainer = new VBox(15);
        List<ExerciseInputRow> inputRows = new ArrayList<>();

        for (Exercise ex : template.getExercises()) {
            Label exLabel = new Label(ex.getName().toUpperCase() + " (" + ex.getMuscleGroup() + ")");
            exLabel.setStyle("-fx-text-fill: #ff2a85; -fx-font-weight: bold;");

            TextField warmUpWeight = new TextField();
            warmUpWeight.setPromptText("W-WEIGHT");
            TextField warmUpReps = new TextField();
            warmUpReps.setPromptText("W-REPS");
            HBox warmUpRow = new HBox(10, new Label("WARM-UP:"), warmUpWeight, warmUpReps);
            warmUpRow.setAlignment(Pos.CENTER_LEFT);

            TextField workWeight = new TextField();
            workWeight.setPromptText("WORK WEIGHT");
            TextField workReps = new TextField();
            workReps.setPromptText("WORK REPS");
            HBox workRow = new HBox(10, new Label("SET 1:   "), workWeight, workReps);
            workRow.setAlignment(Pos.CENTER_LEFT);
            
            VBox exBox = new VBox(6, exLabel, warmUpRow, workRow);
            exBox.setStyle("-fx-border-color: #1a0b2e; -fx-border-width: 1px; -fx-padding: 8px; -fx-background-color: #120926;");
            exercisesContainer.getChildren().add(exBox);

            inputRows.add(new ExerciseInputRow(ex.getId(), workWeight, workReps));
        }

        ScrollPane scrollPane = new ScrollPane(exercisesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

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

        root.getChildren().addAll(header, restBox, scrollPane, finishBtn, backBtn);

        createStyledScene(root, 480, 620);
    }

    private void startRestCountdownDynamic(Label timerLabel, int initialSeconds) {
        if (currentRestAnimationTimer != null) {
            currentRestAnimationTimer.stop();
        }

        activeRestSeconds = initialSeconds;
        currentRestAnimationTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 1_000_000_000L) {
                    if (activeRestSeconds > 0) {
                        timerLabel.setText(String.format("REST: %02d:%02d", activeRestSeconds / 60, activeRestSeconds % 60));
                        timerLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ff2a85; -fx-font-weight: bold;");
                        activeRestSeconds--;
                    } else {
                        timerLabel.setText("REST OVER // FIGHT!");
                        timerLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #00f0ff; -fx-font-weight: bold;");
                        stop();
                    }
                    lastUpdate = now;
                }
            }
        };
        currentRestAnimationTimer.start();
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

        // --- Manual Addition Section ---
        Label manualLabel = new Label("MANUAL ENTRY:");
        manualLabel.setStyle("-fx-text-fill: #e0aaff; -fx-font-size: 11px;");

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

        // --- Cloud API Importer Section ---
        Label cloudHeader = new Label("--- OR SYNC FROM CLOUD API ---");
        cloudHeader.setStyle("-fx-text-fill: #00f0ff; -fx-font-size: 11px;");

        TextField apiKeyField = new TextField();
        apiKeyField.setPromptText("PASTE YOUR API-NINJAS KEY");

        ComboBox<String> muscleSelector = new ComboBox<>();
        muscleSelector.getItems().addAll("biceps", "triceps", "chest", "lats", "quads", "hamstrings", "shoulders");
        muscleSelector.getSelectionModel().selectFirst();
        muscleSelector.setMaxWidth(Double.MAX_VALUE);

        Button fetchCloudBtn = new Button("DOWNLOAD EXERCISES");
        fetchCloudBtn.setMaxWidth(Double.MAX_VALUE);
        fetchCloudBtn.setStyle("-fx-background-color: #ff2a85; -fx-text-fill: #0b0614; -fx-font-weight: bold;");

        fetchCloudBtn.setOnAction(e -> {
            String key = apiKeyField.getText().trim();
            String selectedMuscle = muscleSelector.getValue();
            
            if (key.isEmpty()) {
                showAlert("API ERROR", "API key required to access cloud memory!");
            } else {
                DatabaseManager.importExercisesFromApi(key, selectedMuscle);
                showAlert("SYNC COMPLETE", "Cloud exercise data loaded into local core memory!");
                showTemplateSelector();
            }
        });

        Button returnMenuBtn = new Button("RETURN TO MENU");
        returnMenuBtn.setMaxWidth(Double.MAX_VALUE);
        returnMenuBtn.setOnAction(e -> showTemplateSelector());

        // Add all unique nodes cleanly to the root container without duplication
        root.getChildren().addAll(
            header, 
            manualLabel, 
            nameField, 
            saveBtn, 
            cloudHeader, 
            apiKeyField, 
            muscleSelector, 
            fetchCloudBtn, 
            new Label(""), 
            returnMenuBtn
        );

        createStyledScene(root, 420, 580);
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