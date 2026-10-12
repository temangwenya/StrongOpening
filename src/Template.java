import java.util.List;

public class Template {
    private int id;
    private String templateName;
    private List<Exercise> exercises; // Exercises included in this routine

    public Template(int id, String templateName, List<Exercise> exercises) {
        this.id = id;
        this.templateName = templateName;
        this.exercises = exercises;
    }

    public Template(String templateName, List<Exercise> exercises) {
        this.templateName = templateName;
        this.exercises = exercises;
    }

    // Getters
    public int getId() { return id; }
    public String getTemplateName() { return templateName; }
    public List<Exercise> getExercises() { return exercises; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

    @Override
    public String toString() {
        return "Template: " + templateName + " (" + exercises.size() + " exercises)";
    }
}