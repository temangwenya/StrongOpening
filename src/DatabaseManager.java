/** File Info:
 * This class manages the SQLite database connection and table creation for the Strong Opening application.
 */

// Imports
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

public class DatabaseManager {
    // Creates a local file named 'strongopening.db' in project directory
    private static final String URL = "jdbc:sqlite:strongopening.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("Connected to local SQLite database successfully!");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }

    public static void createTables() {
        // SQL commands to build relational tables
        String sqlExercises = "CREATE TABLE IF NOT EXISTS exercises (\n"
        + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
        + "    name TEXT NOT NULL,\n"
        + "    muscle_group TEXT NOT NULL,\n"
        + "    media_path TEXT\n" 
        + ");";

        String sqlTemplates = "CREATE TABLE IF NOT EXISTS templates (\n"
                + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "    template_name TEXT UNIQUE NOT NULL\n"
                + ");";

        String sqlTemplateExercises = "CREATE TABLE IF NOT EXISTS template_exercises (\n"
                + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "    template_id INTEGER,\n"
                + "    exercise_id INTEGER,\n"
                + "    default_sets INTEGER,\n"
                + "    FOREIGN KEY (template_id) REFERENCES templates(id),\n"
                + "    FOREIGN KEY (exercise_id) REFERENCES exercises(id)\n"
                + ");";

                // Workout Logs (The session itself)
        String sqlWorkoutLogs = "CREATE TABLE IF NOT EXISTS workout_logs (\n"
                + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "    template_id INTEGER,\n"
                + "    date TEXT NOT NULL,\n"
                + "    FOREIGN KEY (template_id) REFERENCES templates(id)\n"
                + ");";

        // 5. Workout Sets (The actual performance data per set)
        String sqlWorkoutSets = "CREATE TABLE IF NOT EXISTS workout_sets (\n"
                + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "    workout_log_id INTEGER,\n"
                + "    exercise_id INTEGER,\n"
                + "    set_number INTEGER,\n"
                + "    weight REAL,\n"
                + "    reps INTEGER,\n"
                + "    FOREIGN KEY (workout_log_id) REFERENCES workout_logs(id),\n"
                + "    FOREIGN KEY (exercise_id) REFERENCES exercises(id)\n"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            
            // Execute all your table creations here...
            stmt.execute(sqlWorkoutLogs);
            stmt.execute(sqlWorkoutSets);
            System.out.println("Workout logging tables created successfully!");

        } catch (SQLException e) {
            System.out.println("Error creating workout tables: " + e.getMessage());
        }



        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            
            // Executes table creation queries
            stmt.execute(sqlExercises);
            stmt.execute(sqlTemplates);
            stmt.execute(sqlTemplateExercises);
            stmt.execute(sqlWorkoutLogs);
            
            System.out.println("All workout database tables created successfully!");

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    public static void insertExercise(String name, String muscleGroup, String mediaPath) {
        String sql = "INSERT INTO exercises(name, muscle_group, media_path) VALUES(?, ?, ?);";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, muscleGroup);
            pstmt.setString(3, mediaPath);
            pstmt.executeUpdate();
            System.out.println("Successfully added: " + name);

        } catch (SQLException e) {
            System.out.println("Error inserting exercise: " + e.getMessage());
        }
    }

    public static List<Exercise> getAllExercises() {
        List<Exercise> exercises = new ArrayList<>();
        String sql = "SELECT id, name, muscle_group, media_path FROM exercises;";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Exercise exercise = new Exercise(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("muscle_group"),
                    rs.getString("media_path")
                );
                exercises.add(exercise);
            }
            System.out.println("Successfully loaded " + exercises.size() + " exercises from database.");

        } catch (SQLException e) {
            System.out.println("Error fetching exercises: " + e.getMessage());
        }

        return exercises;
    }

    public static void insertTemplate(String templateName, List<Integer> exerciseIds, int defaultSets) {
        String sqlTemplate = "INSERT OR IGNORE INTO templates(template_name) VALUES(?);";
        String sqlTemplateExercise = "INSERT INTO template_exercises(template_id, exercise_id, default_sets) VALUES(?, ?, ?);";

        try (Connection conn = connect()) {
            // Start transaction
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtTemplate = conn.prepareStatement(sqlTemplate, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement pstmtTE = conn.prepareStatement(sqlTemplateExercise)) {
                
                // 1. Insert the template name
                pstmtTemplate.setString(1, templateName);
                pstmtTemplate.executeUpdate();

                // Retrieve the auto-generated template ID
                try (ResultSet generatedKeys = pstmtTemplate.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int templateId = generatedKeys.getInt(1);

                        // 2. Link each exercise to the template using batch execution
                        for (int exerciseId : exerciseIds) {
                            pstmtTE.setInt(1, templateId);
                            pstmtTE.setInt(2, exerciseId);
                            pstmtTE.setInt(3, defaultSets);
                            pstmtTE.addBatch();
                        }
                        pstmtTE.executeBatch();
                        
                        // Commit transaction
                        conn.commit();
                        System.out.println("Successfully created template: " + templateName);
                    } else {
                        conn.rollback();
                        System.out.println("Failed to create template, no ID obtained.");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Transaction rolled back due to error: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void logWorkout(int templateId, List<WorkoutSetData> setsToLog) {
        String sqlLog = "INSERT INTO workout_logs(template_id, date) VALUES(?, ?);";
        String sqlSet = "INSERT INTO workout_sets(workout_log_id, exercise_id, set_number, weight, reps) VALUES(?, ?, ?, ?, ?);";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtLog = conn.prepareStatement(sqlLog, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement pstmtSet = conn.prepareStatement(sqlSet)) {

                // 1. Log the main workout session with today's date
                pstmtLog.setInt(1, templateId);
                pstmtLog.setString(2, LocalDate.now().toString());
                pstmtLog.executeUpdate();

                // Get the generated workout log ID
                try (ResultSet generatedKeys = pstmtLog.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int workoutLogId = generatedKeys.getInt(1);

                        // 2. Insert each set performed during the workout
                        for (WorkoutSetData set : setsToLog) {
                            pstmtSet.setInt(1, workoutLogId);
                            pstmtSet.setInt(2, set.exerciseId);
                            pstmtSet.setInt(3, set.setNumber);
                            pstmtSet.setDouble(4, set.weight);
                            pstmtSet.setInt(5, set.reps);
                            pstmtSet.addBatch();
                        }
                        pstmtSet.executeBatch();
                        
                        conn.commit();
                        System.out.println("Workout successfully logged and saved locally!");
                    } else {
                        conn.rollback();
                        System.out.println("Failed to log workout session.");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error during workout log transaction: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    public static List<Template> getAllTemplates() {
        List<Template> templates = new ArrayList<>();
        String sqlTemplate = "SELECT id, template_name FROM templates;";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rsTemplate = stmt.executeQuery(sqlTemplate)) {

            while (rsTemplate.next()) {
                int templateId = rsTemplate.getInt("id");
                String templateName = rsTemplate.getString("template_name");

                // Fetch exercises linked to this template
                List<Exercise> templateExercises = getExercisesForTemplate(conn, templateId);

                // Create our Template object
                Template template = new Template(templateId, templateName, templateExercises);
                templates.add(template);
            }
            System.out.println("Successfully loaded " + templates.size() + " workout templates.");

        } catch (SQLException e) {
            System.out.println("Error fetching templates: " + e.getMessage());
        }

        return templates;
    }

    // Helper method to grab exercises belonging to a specific template
    private static List<Exercise> getExercisesForTemplate(Connection conn, int templateId) {
        List<Exercise> exercises = new ArrayList<>();
        String sql = "SELECT e.id, e.name, e.muscle_group, e.media_path " +
                     "FROM exercises e " +
                     "JOIN template_exercises te ON e.id = te.exercise_id " +
                     "WHERE te.template_id = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, templateId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Exercise ex = new Exercise(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("muscle_group"),
                        rs.getString("media_path")
                    );
                    exercises.add(ex);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching exercises for template: " + e.getMessage());
        }

        return exercises;
    }

    public static class WorkoutHistoryItem {
        public String date;
        public String templateName;
        public String exerciseName;
        public int setNumber;
        public double weight;
        public int reps;

        public WorkoutHistoryItem(String date, String templateName, String exerciseName, int setNumber, double weight, int reps) {
            this.date = date;
            this.templateName = templateName;
            this.exerciseName = exerciseName;
            this.setNumber = setNumber;
            this.weight = weight;
            this.reps = reps;
        }
    }

    public static List<WorkoutHistoryItem> getWorkoutHistory() {
        List<WorkoutHistoryItem> history = new ArrayList<>();
        String sql = "SELECT wl.date, t.template_name, e.name AS exercise_name, ws.set_number, ws.weight, ws.reps " +
                     "FROM workout_logs wl " +
                     "JOIN templates t ON wl.template_id = t.id " +
                     "JOIN workout_sets ws ON wl.id = ws.workout_log_id " +
                     "JOIN exercises e ON ws.exercise_id = e.id " +
                     "ORDER BY wl.date DESC, wl.id DESC;";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                history.add(new WorkoutHistoryItem(
                    rs.getString("date"),
                    rs.getString("template_name"),
                    rs.getString("exercise_name"),
                    rs.getInt("set_number"),
                    rs.getDouble("weight"),
                    rs.getInt("reps")
                ));
            }
            System.out.println("Loaded " + history.size() + " past set records.");

        } catch (SQLException e) {
            System.out.println("Error fetching workout history: " + e.getMessage());
        }

        return history;
    }

    public static void deleteTemplate(int templateId) {
    String sqlDeleteTE = "DELETE FROM template_exercises WHERE template_id = ?;";
    String sqlDeleteTemplate = "DELETE FROM templates WHERE id = ?;";

    try (Connection conn = connect()) {
        conn.setAutoCommit(false);

        try (PreparedStatement pstmtTE = conn.prepareStatement(sqlDeleteTE);
             PreparedStatement pstmtTemplate = conn.prepareStatement(sqlDeleteTemplate)) {

            // 1. Delete links to exercises first (foreign key dependency)
            pstmtTE.setInt(1, templateId);
            pstmtTE.executeUpdate();

            // 2. Delete the template itself
            pstmtTemplate.setInt(1, templateId);
            pstmtTemplate.executeUpdate();

            conn.commit();
            System.out.println("Template successfully deleted.");
        } catch (SQLException e) {
            conn.rollback();
            System.out.println("Error deleting template, transaction rolled back: " + e.getMessage());
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
        }
    } catch (SQLException e) {
        System.out.println("Database connection error: " + e.getMessage());
    }
}

    public static void addExercise(String name) {
        String sql = "INSERT INTO exercises(name) VALUES(?);";
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            System.out.println("Exercise added successfully: " + name);
        } catch (SQLException e) {
            System.out.println("Error adding exercise: " + e.getMessage());
        }
    }




    // Quick test main method
    public static void main(String[] args) {
        createTables();

        /* insertExercise("Barbell Bench Press", "Chest", "assets/media/bench_press.mp4");
        insertExercise("Conventional Deadlift", "Back/Posterior", "assets/media/deadlift.mp4");
        insertExercise("Barbell Back Squat", "Legs", "assets/media/squat.mp4"); */

        /* List<Exercise> myLifts = getAllExercises();
        for (Exercise ex : myLifts) {
            System.out.println("Loaded into Java Object -> " + ex);
        } */

        /* // Test creating a template with Exercise IDs 1 and 3, setting default sets to 3
        List<Integer> starterLifts = List.of(1, 3);
        insertTemplate("Full Body Day", starterLifts, 3); */

        List<Template> myTemplates = getAllTemplates();
        for (Template t : myTemplates) {
            System.out.println("\n" + t);
            System.out.println("Exercises in this routine:");
            for (Exercise ex : t.getExercises()) {
                System.out.println(" - " + ex.getName() + " (" + ex.getMuscleGroup() + ")");
            }
        }
    }
}