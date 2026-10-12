/**
 * File Info:
 * Manages SQLite database connections, relational schema creation, CRUD operations, 
 * workouts, template deletions, and Personal Record (PR) analytics for the StrongOpening application.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

public class DatabaseManager {
    // Local SQLite database file URL
    private static final String URL = "jdbc:sqlite:strongopening.db";

    /**
     * Establishes a connection to the local SQLite database.
     * @return Connection object, or null if connection fails.
     */
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

    /**
     * Creates all required relational database tables if they do not already exist.
     */
    public static void createTables() {
        String sqlExercises = "CREATE TABLE IF NOT EXISTS exercises (\n"
                + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "    name TEXT NOT NULL,\n"
                + "    muscle_group TEXT,\n"
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
                + "    FOREIGN KEY (template_id) REFERENCES templates(id) ON DELETE CASCADE,\n"
                + "    FOREIGN KEY (exercise_id) REFERENCES exercises(id)\n"
                + ");";

        String sqlWorkoutLogs = "CREATE TABLE IF NOT EXISTS workout_logs (\n"
                + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "    template_id INTEGER,\n"
                + "    date TEXT NOT NULL,\n"
                + "    FOREIGN KEY (template_id) REFERENCES templates(id)\n"
                + ");";

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
            
            stmt.execute(sqlExercises);
            stmt.execute(sqlTemplates);
            stmt.execute(sqlTemplateExercises);
            stmt.execute(sqlWorkoutLogs);
            stmt.execute(sqlWorkoutSets);
            
            System.out.println("All workout database tables created successfully!");

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    /**
     * Inserts a new exercise into the database.
     */
    public static void insertExercise(String name, String muscleGroup, String mediaPath) {
        String sql = "INSERT INTO exercises(name, muscle_group, media_path) VALUES(?, ?, ?);";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, muscleGroup);
            pstmt.setString(3, mediaPath);
            pstmt.executeUpdate();
            System.out.println("Successfully added exercise: " + name);

        } catch (SQLException e) {
            System.out.println("Error inserting exercise: " + e.getMessage());
        }
    }

    /**
     * Overloaded method to add a custom exercise with just a name from the GUI.
     */
    public static void addExercise(String name) {
        insertExercise(name, "General", null);
    }

    /**
     * Retrieves all exercises available in the database.
     */
    public static List<Exercise> getAllExercises() {
        List<Exercise> exercises = new ArrayList<>();
        String sql = "SELECT id, name, muscle_group, media_path FROM exercises;";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                exercises.add(new Exercise(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("muscle_group"),
                    rs.getString("media_path")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching exercises: " + e.getMessage());
        }

        return exercises;
    }

    /**
     * Logs a completed workout session and its corresponding sets using a transactional block.
     */
    public static void logWorkout(int templateId, List<WorkoutSetData> setsToLog) {
        String sqlLog = "INSERT INTO workout_logs(template_id, date) VALUES(?, ?);";
        String sqlSet = "INSERT INTO workout_sets(workout_log_id, exercise_id, set_number, weight, reps) VALUES(?, ?, ?, ?, ?);";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtLog = conn.prepareStatement(sqlLog, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement pstmtSet = conn.prepareStatement(sqlSet)) {

                pstmtLog.setInt(1, templateId);
                pstmtLog.setString(2, LocalDate.now().toString());
                pstmtLog.executeUpdate();

                try (ResultSet generatedKeys = pstmtLog.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int workoutLogId = generatedKeys.getInt(1);

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

    /**
     * Retrieves all workout templates along with their mapped exercises.
     */
    public static List<Template> getAllTemplates() {
        List<Template> templates = new ArrayList<>();
        String sqlTemplate = "SELECT id, template_name FROM templates;";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rsTemplate = stmt.executeQuery(sqlTemplate)) {

            while (rsTemplate.next()) {
                int templateId = rsTemplate.getInt("id");
                String templateName = rsTemplate.getString("template_name");
                List<Exercise> templateExercises = getExercisesForTemplate(conn, templateId);

                templates.add(new Template(templateId, templateName, templateExercises));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching templates: " + e.getMessage());
        }

        return templates;
    }

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
                    exercises.add(new Exercise(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("muscle_group"),
                        rs.getString("media_path")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching exercises for template: " + e.getMessage());
        }

        return exercises;
    }

    /**
     * Data transfer object representing historical workout items.
     */
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

    /**
     * Fetches detailed workout history sorted by latest date.
     */
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
        } catch (SQLException e) {
            System.out.println("Error fetching workout history: " + e.getMessage());
        }

        return history;
    }

    /**
     * Safely deletes a routine template and cleans up foreign key dependencies using transactions.
     */
    public static void deleteTemplate(int templateId) {
        String sqlDeleteTE = "DELETE FROM template_exercises WHERE template_id = ?;";
        String sqlDeleteTemplate = "DELETE FROM templates WHERE id = ?;";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtTE = conn.prepareStatement(sqlDeleteTE);
                 PreparedStatement pstmtTemplate = conn.prepareStatement(sqlDeleteTemplate)) {

                pstmtTE.setInt(1, templateId);
                pstmtTE.executeUpdate();

                pstmtTemplate.setInt(1, templateId);
                pstmtTemplate.executeUpdate();

                conn.commit();
                System.out.println("Template successfully deleted.");
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error deleting template, transaction rolled back: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    /**
     * Personal Record model to hold max weight aggregates per exercise.
     */
    public static class PersonalRecord {
        public String exerciseName;
        public double maxWeight;

        public PersonalRecord(String exerciseName, double maxWeight) {
            this.exerciseName = exerciseName;
            this.maxWeight = maxWeight;
        }
    }

    /**
     * Calculates all-time personal records (maximum weight lifted) per exercise.
     */
    public static List<PersonalRecord> getPersonalRecords() {
        List<PersonalRecord> prs = new ArrayList<>();
        String sql = "SELECT e.name AS exercise_name, MAX(ws.weight) AS max_weight " +
                     "FROM workout_sets ws " +
                     "JOIN exercises e ON ws.exercise_id = e.id " +
                     "GROUP BY e.id, e.name " +
                     "ORDER BY e.name;";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                prs.add(new PersonalRecord(
                    rs.getString("exercise_name"),
                    rs.getDouble("max_weight")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching PRs: " + e.getMessage());
        }

        return prs;
    }
}