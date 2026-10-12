public class WorkoutSetData {
    public int exerciseId;
    public int setNumber;
    public double weight;
    public int reps;

    public WorkoutSetData(int exerciseId, int setNumber, double weight, int reps) {
        this.exerciseId = exerciseId;
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
    }
}