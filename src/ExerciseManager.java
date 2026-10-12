import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExerciseManager {
    // Our in-memory data structure for O(1) lookups by name
    private Map<String, Exercise> exerciseMap;

    public ExerciseManager() {
        exerciseMap = new HashMap<>();
        loadExercisesIntoCache();
    }

    // Load from SQLite into our HashMap cache
    private void loadExercisesIntoCache() {
        List<Exercise> exercises = DatabaseManager.getAllExercises();
        for (Exercise ex : exercises) {
            // Store with lowercase key so user searches are case-insensitive
            exerciseMap.put(ex.getName().toLowerCase(), ex);
        }
        System.out.println("Cached " + exerciseMap.size() + " exercises into memory for instant lookup.");
    }

    // Instant O(1) search!
    public Exercise findExercise(String name) {
        return exerciseMap.get(name.toLowerCase());
    }

    // Add a new exercise to both the Database AND the live cache
    public void addExercise(String name, String muscleGroup, String mediaPath) {
        DatabaseManager.insertExercise(name, muscleGroup, mediaPath);
        // Refresh cache or add directly
        Exercise newEx = new Exercise(name, muscleGroup, mediaPath);
        exerciseMap.put(name.toLowerCase(), newEx);
    }
}