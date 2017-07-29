package com.autilite.weightlifttracker.program;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kelvin on 09/12/16.
 */

public class Exercise implements Parcelable {
    private final long id;
    private long baseExerciseId;
    private String name;
    private int sets;
    private int reps;
    private double weight;
    private double weightIncrement;
    private int restTime;           // In seconds

    public Exercise(long id, long baseExerciseId, String name, int sets, int reps, double weight) {
        // TODO configurable default values
        this(id, baseExerciseId, name, sets, reps, weight, 5);
    }

    public Exercise(long id, long baseExerciseId, String name, int sets, int reps, double weight, double weightIncrement) {
        // TODO configurable default values
        this(id, baseExerciseId, name, sets, reps, weight, weightIncrement, 90);
    }

    public Exercise(long id, long baseExerciseId, String name, int sets, int reps, double weight, double weightIncrement, int restTime) {
        this.id = id;
        this.baseExerciseId = baseExerciseId;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.weightIncrement = weightIncrement;
        this.restTime = restTime;
    }

    protected Exercise(Parcel in) {
        id = in.readLong();
        baseExerciseId = in.readLong();
        name = in.readString();
        sets = in.readInt();
        reps = in.readInt();
        weight = in.readDouble();
        weightIncrement = in.readDouble();
        restTime = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(baseExerciseId);
        dest.writeString(name);
        dest.writeInt(sets);
        dest.writeInt(reps);
        dest.writeDouble(weight);
        dest.writeDouble(weightIncrement);
        dest.writeInt(restTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    public long getId() {
        return id;
    }

    public long getBaseExerciseId() {
        return baseExerciseId;
    }

    public void setBaseExerciseId(long baseExerciseId) {
        this.baseExerciseId = baseExerciseId;
    }

    public void setWeightIncrement(double weightIncrement) {
        this.weightIncrement = weightIncrement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeightIncrement() {
        return weightIncrement;
    }

    public void setWeightIncrement(float weightIncrement) {
        this.weightIncrement = weightIncrement;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

}