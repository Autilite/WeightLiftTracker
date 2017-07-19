package com.autilite.weightlifttracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.autilite.weightlifttracker.program.Exercise;
import com.autilite.weightlifttracker.program.Program;
import com.autilite.weightlifttracker.program.Workout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.autilite.weightlifttracker.database.ExerciseInfoContract.ExerciseInfoEntry;
import static com.autilite.weightlifttracker.database.ExerciseStatContract.ExerciseStatEntry;
import static com.autilite.weightlifttracker.database.WorkoutContract.WorkoutEntry;
import static com.autilite.weightlifttracker.database.WorkoutListContract.WorkoutListEntry;
import static com.autilite.weightlifttracker.database.ProgramContract.ProgramEntry;
import static com.autilite.weightlifttracker.database.ProgramWorkoutContract.ProgramWorkoutEntry;
import static com.autilite.weightlifttracker.database.ProgramSessionContract.ProgramSessionEntry;
import static com.autilite.weightlifttracker.database.ExerciseSessionContract.ExerciseSessionEntry;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public class WorkoutProgramDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "WorkoutProgram.db";

    private static final String SQL_CREATE_TABLE_EXERCISE_INFO =
            "CREATE TABLE " + ExerciseInfoEntry.TABLE_NAME + " (" +
                    ExerciseInfoEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    ExerciseInfoEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    ExerciseInfoEntry.COLUMN_DESCRIPTION + " TEXT)";

    private static final String SQL_DELETE_TABLE_EXERCISE_INFO =
            "DROP TABLE IF EXISTS " + ExerciseInfoEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_EXERCISE_STATS =
            "CREATE TABLE " + ExerciseStatEntry.TABLE_NAME + " (" +
                    ExerciseStatEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    ExerciseStatEntry.COLUMN_EXERCISE_ID + " INTEGER NOT NULL," +
                    ExerciseStatEntry.COLUMN_SET + " INTEGER," +
                    ExerciseStatEntry.COLUMN_REP + " INTEGER," +
                    ExerciseStatEntry.COLUMN_WEIGHT + " REAL," +
                    ExerciseStatEntry.COLUMN_AUTOINC + " REAL," +
                    "FOREIGN KEY (" + ExerciseStatEntry.COLUMN_EXERCISE_ID + ") " +
                    "REFERENCES " + ExerciseInfoEntry.TABLE_NAME + " (" + ExerciseInfoEntry._ID + ") " +
                    "ON DELETE RESTRICT ON UPDATE CASCADE)";

    private static final String SQL_DELETE_TABLE_EXERCISE_STATS =
            "DROP TABLE IF EXISTS " + ExerciseStatEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_WORKOUT =
            "CREATE TABLE " + WorkoutEntry.TABLE_NAME + " (" +
                    WorkoutEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    WorkoutEntry.COLUMN_NAME + " TEXT NOT NULL)";

    private static final String SQL_DELETE_TABLE_WORKOUT =
            "DROP TABLE IF EXISTS " + WorkoutEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_WORKOUT_LIST =
            "CREATE TABLE " + WorkoutListEntry.TABLE_NAME + " (" +
                    WorkoutListEntry.COLUMN_WORKOUT_ID + " INTEGER NOT NULL," +
                    WorkoutListEntry.COLUMN_EXERCISE_ID + " INTEGER NOT NULL," +
                    "FOREIGN KEY (" + WorkoutListEntry.COLUMN_WORKOUT_ID + ") " +
                    "REFERENCES " + WorkoutEntry.TABLE_NAME + "(" + WorkoutEntry._ID + ") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE," +
                    "FOREIGN KEY (" + WorkoutListEntry.COLUMN_EXERCISE_ID + ") " +
                    "REFERENCES " + ExerciseStatEntry.TABLE_NAME + "(" + ExerciseStatEntry._ID + ") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE)";

    private static final String SQL_DELETE_TABLE_WORKOUT_LIST =
            "DROP TABLE IF EXISTS " + ExerciseStatContract.ExerciseStatEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_PROGRAM =
            "CREATE TABLE " + ProgramEntry.TABLE_NAME + " (" +
                    ProgramEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    ProgramEntry.COLUMN_NAME + " TEXT NOT NULL)";

    private static final String SQL_DELETE_TABLE_PROGRAM =
            "DROP TABLE IF EXISTS " + WorkoutContract.WorkoutEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_PROGRAM_WORKOUTS =
            "CREATE TABLE " + ProgramWorkoutEntry.TABLE_NAME + " (" +
                    ProgramWorkoutEntry.COLUMN_PROGRAM_ID + " INTEGER NOT NULL," +
                    ProgramWorkoutEntry.COLUMN_WORKOUT_ID + " INTEGER NOT NULL," +
                    ProgramWorkoutEntry.COLUMN_NAME_DAY + " INTEGER NOT NULL," +
                    "FOREIGN KEY (" + ProgramWorkoutEntry.COLUMN_PROGRAM_ID + " )" +
                    "REFERENCES " + ProgramEntry.TABLE_NAME + "(" + ProgramEntry._ID + ") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE," +
                    "FOREIGN KEY (" + ProgramWorkoutEntry.COLUMN_WORKOUT_ID + ") " +
                    "REFERENCES " + WorkoutEntry.TABLE_NAME + " (" + WorkoutEntry._ID + ")" +
                    "ON DELETE CASCADE ON UPDATE CASCADE)";

    private static final String SQL_DELETE_TABLE_PROGRAM_WORKOUTS =
            "DROP TABLE IF EXISTS " + ProgramWorkoutContract.ProgramWorkoutEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_PROGRAM_SESSION =
            "CREATE TABLE " + ProgramSessionEntry.TABLE_NAME+ " (" +
                    ProgramSessionEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    ProgramSessionEntry.COLUMN_PROGRAM_ID + " INTEGER NOT NULL," +
                    ProgramSessionEntry.COLUMN_PROGRAM_DAY + " INTEGER NOT NULL," +
                    ProgramSessionEntry.COLUMN_DATE + " INTEGER NOT NULL," +
                    "FOREIGN KEY (" + ProgramSessionEntry.COLUMN_PROGRAM_ID + ") " +
                    "REFERENCES " + ProgramEntry.TABLE_NAME + " (" +
                    ProgramEntry._ID +") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE)";

    private static final String SQL_DELETE_TABLE_PROGRAM_SESSION =
            "DROP TABLE IF EXISTS " + ProgramSessionEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_EXERCISE_SESSION =
            "CREATE TABLE " + ExerciseSessionEntry.TABLE_NAME+ " (" +
                    ExerciseSessionEntry.COLUMN_PROGRAM_SESSION_ID + " INTEGER NOT NULL," +
                    ExerciseSessionEntry.COLUMN_WORKOUT_ID + " INTEGER NOT NULL," +
                    ExerciseSessionEntry.COLUMN_EXERCISE_ID + " INTEGER NOT NULL," +
                    ExerciseSessionEntry.COLUMN_SET_NUMBER + " INTEGER NOT NULL," +
                    ExerciseSessionEntry.COLUMN_SUCCESSFUL_REPS + " INTEGER NOT NULL," +
                    ExerciseSessionEntry.COLUMN_WEIGHT + " REAL," +
                    ExerciseSessionEntry.COLUMN_IS_SET_SUCCESSFUL + " INTEGER NOT NULL DEFAULT 0," +
                    "PRIMARY KEY ("+ ExerciseSessionEntry.COLUMN_PROGRAM_SESSION_ID +", " +
                    ExerciseSessionEntry.COLUMN_WORKOUT_ID + "," +
                    ExerciseSessionEntry.COLUMN_EXERCISE_ID + "," +
                    ExerciseSessionEntry.COLUMN_SET_NUMBER + ")," +
                    "FOREIGN KEY (" + ExerciseSessionEntry.COLUMN_PROGRAM_SESSION_ID + ") " +
                    "REFERENCES " + ProgramSessionEntry.TABLE_NAME + " (" +
                    ProgramSessionEntry._ID +") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE," +
                    "FOREIGN KEY (" + ExerciseSessionEntry.COLUMN_WORKOUT_ID +") " +
                    "REFERENCES " + WorkoutListEntry.TABLE_NAME +
                    " (" + WorkoutListEntry.COLUMN_WORKOUT_ID + ") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE," +
                    "FOREIGN KEY (" + ExerciseSessionEntry.COLUMN_EXERCISE_ID +")" +
                    " REFERENCES " + WorkoutListEntry.TABLE_NAME +
                    " ("+ WorkoutListEntry.COLUMN_EXERCISE_ID +") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE)";

    private static final String SQL_DELETE_TABLE_EXERCISE_SESSION =
            "DROP TABLE IF EXISTS " + ExerciseSessionContract.ExerciseSessionEntry.TABLE_NAME;

    public WorkoutProgramDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EXERCISE_INFO);
        insertDefaultExercises(sqLiteDatabase);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EXERCISE_STATS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_WORKOUT);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_WORKOUT_LIST);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PROGRAM);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PROGRAM_WORKOUTS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PROGRAM_SESSION);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EXERCISE_SESSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    private void insertDefaultExercises(SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put(ExerciseInfoEntry.COLUMN_NAME, "Squats");
        db.insert(ExerciseInfoEntry.TABLE_NAME, null, cv);
        cv.put(ExerciseInfoEntry.COLUMN_NAME, "Deadlift");
        db.insert(ExerciseInfoEntry.TABLE_NAME, null, cv);
        cv.put(ExerciseInfoEntry.COLUMN_NAME, "Overhead Press");
        db.insert(ExerciseInfoEntry.TABLE_NAME, null, cv);
    }

    public Cursor getAllExerciseInfo() {
        SQLiteDatabase db = getReadableDatabase();
        String sqlGetAllExerciseInfo = "select * from " + ExerciseInfoEntry.TABLE_NAME;
        return db.rawQuery(sqlGetAllExerciseInfo, null);
    }

    public long createProgram(String programName) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ProgramEntry.COLUMN_NAME, programName);
        return db.insert(ProgramEntry.TABLE_NAME, null, cv);
    }

    public long createWorkout(String workoutName) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(WorkoutEntry.COLUMN_NAME, workoutName);
        return db.insert(WorkoutEntry.TABLE_NAME, null, cv);
    }

    public long createExerciseStat(long exerciseId,  int sets, int reps, float weight, float autoInc) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ExerciseStatEntry.COLUMN_EXERCISE_ID, exerciseId);
        cv.put(ExerciseStatEntry.COLUMN_SET, sets);
        cv.put(ExerciseStatEntry.COLUMN_REP, reps);
        cv.put(ExerciseStatEntry.COLUMN_WEIGHT, weight);
        cv.put(ExerciseStatEntry.COLUMN_AUTOINC, autoInc);
        return db.insert(ExerciseStatEntry.TABLE_NAME, null, cv);
    }

    public long createExerciseStat(long exerciseId, int sets, int reps, float weight) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ExerciseStatEntry.COLUMN_EXERCISE_ID, exerciseId);
        cv.put(ExerciseStatEntry.COLUMN_SET, sets);
        cv.put(ExerciseStatEntry.COLUMN_REP, reps);
        cv.put(ExerciseStatEntry.COLUMN_WEIGHT, weight);
        return db.insert(ExerciseStatEntry.TABLE_NAME, null, cv);
    }

    public long addExerciseToWorkout(long workoutId, long exerciseStatId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(WorkoutListEntry.COLUMN_WORKOUT_ID, workoutId);
        cv.put(WorkoutListEntry.COLUMN_EXERCISE_ID, exerciseStatId);
        return db.insert(WorkoutListEntry.TABLE_NAME, null, cv);
    }

    public boolean addWorkoutToProgram(long programId, long workoutId, int day) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ProgramWorkoutEntry.COLUMN_PROGRAM_ID, programId);
        cv.put(ProgramWorkoutEntry.COLUMN_WORKOUT_ID, workoutId);
        cv.put(ProgramWorkoutEntry.COLUMN_NAME_DAY, day);
        return db.insert(ProgramWorkoutEntry.TABLE_NAME, null, cv) != -1;
    }

    /**
     * Returns a cursor for all rows in the Workouts table
     * @return Cursor
     */
    public Cursor getAllWorkouts() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from " + WorkoutEntry.TABLE_NAME;
        return db.rawQuery(sql, null);
    }

    /**
     * Returns a cursor for all rows in the Programs table
     * @return Cursor
     */
    public Cursor getAllPrograms() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from " + ProgramEntry.TABLE_NAME;
        return db.rawQuery(sql, null);
    }

    /**
     * Selects the table with ExerciseStat.ID, ExerciseInfo.Name, Set, Rep, Weight for the
     * workout
     * @param workoutId The workout id to query the exercises
     * @return
     */
    public Cursor getAllExerciseStatForWorkout(long workoutId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + ExerciseStatEntry.TABLE_NAME + "." + ExerciseStatEntry._ID + ", " +
                ExerciseInfoEntry.TABLE_NAME + "." + ExerciseInfoEntry.COLUMN_NAME + ", " +
                ExerciseStatEntry.TABLE_NAME + "." + ExerciseStatEntry.COLUMN_SET + ", " +
                ExerciseStatEntry.TABLE_NAME + "." + ExerciseStatEntry.COLUMN_REP + ", " +
                ExerciseStatEntry.TABLE_NAME + "." + ExerciseStatEntry.COLUMN_WEIGHT + ", " +
                WorkoutEntry.TABLE_NAME + "." + WorkoutEntry._ID + " " +
                "FROM " + WorkoutEntry.TABLE_NAME + " " +
                "INNER JOIN  " + WorkoutListEntry.TABLE_NAME + " ON " +
                WorkoutEntry.TABLE_NAME + "." + WorkoutEntry._ID +
                " = " + WorkoutListEntry.TABLE_NAME + "." + WorkoutListEntry.COLUMN_WORKOUT_ID + " " +
                "INNER JOIN " + ExerciseStatEntry.TABLE_NAME + " ON " +
                WorkoutListEntry.TABLE_NAME+ "." + WorkoutListEntry.COLUMN_EXERCISE_ID +
                " = " + ExerciseStatEntry.TABLE_NAME + "." + ExerciseStatEntry._ID + " " +
                "INNER JOIN " + ExerciseInfoEntry.TABLE_NAME + " ON " +
                ExerciseStatEntry.TABLE_NAME + "." + ExerciseStatEntry.COLUMN_EXERCISE_ID +
                " = " + ExerciseInfoEntry.TABLE_NAME + "." + ExerciseInfoEntry._ID + " " +
                "WHERE " + WorkoutEntry.TABLE_NAME + "." + WorkoutEntry._ID + "=" + workoutId +
                ";";
        return db.rawQuery(sql, null);
    }

    /**
     * Returns the {@link ProgramWorkoutEntry} table joined with {@link WorkoutEntry} for progId
     *
     * @param progId The id to select
     * @return
     */
    public Cursor getProgramWorkoutTableJoinedWithName(long progId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + ProgramWorkoutEntry.TABLE_NAME + " " +
                "INNER JOIN " + WorkoutEntry.TABLE_NAME + " ON " +
                ProgramWorkoutEntry.TABLE_NAME + "." + ProgramWorkoutEntry.COLUMN_WORKOUT_ID +
                " = " + WorkoutEntry.TABLE_NAME + "." + WorkoutEntry._ID + " " +
                "WHERE " + ProgramWorkoutEntry.TABLE_NAME + "." + ProgramWorkoutEntry.COLUMN_PROGRAM_ID +
                " = " + progId +
                ";";
        return db.rawQuery(sql, null);
    }

    /**
     * Returns the name associated with progId or null if it doesn't exist
     *
     * @param progId The id used to query the database
     * @return The program name or null
     */
    public String getProgramName(long progId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + ProgramEntry.COLUMN_NAME + " FROM " + ProgramEntry.TABLE_NAME +
                " WHERE " + ProgramEntry._ID + "=" + progId;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return null;
        } else {
            cursor.moveToNext();
            String name = cursor.getString(0);
            cursor.close();
            return name;
        }
    }

    public boolean checkIfWorkoutExist(long workoutId) {
        SQLiteDatabase db = getReadableDatabase();
        boolean exist;
        String sql = "SELECT * FROM " + WorkoutEntry.TABLE_NAME +
                " WHERE " + WorkoutEntry._ID  + " = " + workoutId;
        Cursor cursor = db.rawQuery(sql, null);

        // workoutId exist if it can find a result in the Workout table
        exist = cursor.getCount() > 0;
        cursor.close();
        return exist;
    }

    public List<Program> getAllProgramsList() {
        List<Program> listOfPrograms = new ArrayList<>();
        // Get Cursor of all program IDs
        Cursor programs = getAllPrograms();

        // For each programID, grab all its workouts
        while(programs.moveToNext()) {
            // Get program info
            long progId = programs.getLong(programs.getColumnIndex(ProgramContract.ProgramEntry._ID));
            String progName = programs.getString(programs.getColumnIndex(ProgramContract.ProgramEntry.COLUMN_NAME));
            Program p = new Program(progId, progName, "");

            // Grab workouts associated with the program
            Cursor programWorkouts = getProgramWorkoutTableJoinedWithName(progId);
            while(programWorkouts.moveToNext()) {
                // TODO add robust way of getting column index
                long workoutId = programWorkouts.getLong(programWorkouts.getColumnIndex(
                        ProgramWorkoutContract.ProgramWorkoutEntry.COLUMN_WORKOUT_ID));
                String workoutName = programWorkouts.getString(programWorkouts.getColumnIndex(
                        WorkoutContract.WorkoutEntry.COLUMN_NAME));
                Workout w = new Workout(workoutId, workoutName);
                p.addWorkout(w);
            }
            listOfPrograms.add(p);
            programWorkouts.close();
        }
        programs.close();

        return listOfPrograms;
    }

    public List<Workout> getProgramWorkouts(long programId) {
        // Get cursor with all workout Ids
        Cursor workoutCursor = getProgramWorkoutTableJoinedWithName(programId);
        List<Workout> workouts = new ArrayList<>();

        // Go through each of the workoutId
        while (workoutCursor.moveToNext()) {
            long workoutId = workoutCursor.getLong(workoutCursor.getColumnIndex(WorkoutContract.WorkoutEntry._ID));
            String workoutName = workoutCursor.getString(workoutCursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_NAME));
            Workout w = new Workout(workoutId, workoutName);

            // Get list of exercise for workoutId
            Cursor eStat = getAllExerciseStatForWorkout(workoutId);
            while (eStat.moveToNext()) {
                long exerciseId = eStat.getLong(0);
                String exerciseName = eStat.getString(1);
                int set = eStat.getInt(2);
                int rep = eStat.getInt(3);
                float weight = eStat.getFloat(4);
                Exercise e = new Exercise(exerciseId, exerciseName, set, rep, weight);
                w.addExercise(e);
            }
            workouts.add(w);
            eStat.close();

        }
        workoutCursor.close();
        return workouts;
    }

    public List<Workout> getAllWorkoutsList() {
        // Get cursor with all workout Ids
        Cursor workoutCursor = getAllWorkouts();
        List<Workout> workouts = new ArrayList<>();

        // Go through each of the workoutId
        while (workoutCursor.moveToNext()) {
            long workoutId = workoutCursor.getLong(workoutCursor.getColumnIndex(WorkoutContract.WorkoutEntry._ID));
            String workoutName = workoutCursor.getString(workoutCursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_NAME));
            Workout w = new Workout(workoutId, workoutName);

            // Get list of exercise for workoutId
            Cursor eStat = getAllExerciseStatForWorkout(workoutId);
            while (eStat.moveToNext()) {
                long exerciseId = eStat.getLong(0);
                String exerciseName = eStat.getString(1);
                int set = eStat.getInt(2);
                int rep = eStat.getInt(3);
                float weight = eStat.getFloat(4);
                Exercise e = new Exercise(exerciseId, exerciseName, set, rep, weight);
                w.addExercise(e);
            }
            workouts.add(w);
            eStat.close();

        }
        workoutCursor.close();
        return workouts;
    }

    public List<Exercise> getAllExerciseInfoList (long workoutId) {
        List<Exercise> list = new LinkedList<>();

        // Get list of exercise for workoutId
        Cursor eStat = getAllExerciseStatForWorkout(workoutId);
        while (eStat.moveToNext()) {
            long exerciseId = eStat.getLong(0);
            String exerciseName = eStat.getString(1);
            int set = eStat.getInt(2);
            int rep = eStat.getInt(3);
            float weight = eStat.getFloat(4);
            Exercise e = new Exercise(exerciseId, exerciseName, set, rep, weight);
            list.add(e);
        }
        eStat.close();
        return list;
    }

}
