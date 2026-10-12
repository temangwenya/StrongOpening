public class Exercise {
    private int id;
    private String name;
    private String muscleGroup;
    private String mediaPath;

    // Constructor for creating a new exercise object
    public Exercise(int id, String name, String muscleGroup, String mediaPath) {
        this.id = id;
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.mediaPath = mediaPath;
    }

    // Overloaded constructor (if you are adding a new exercise before it has an ID from the database)
    public Exercise(String name, String muscleGroup, String mediaPath) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.mediaPath = mediaPath;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getMuscleGroup() { return muscleGroup; }
    public String getMediaPath() { return mediaPath; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }
    public void setMediaPath(String mediaPath) { this.mediaPath = mediaPath; }

    @Override
    public String toString() {
        return name + " (" + muscleGroup + ")";
    }
}
