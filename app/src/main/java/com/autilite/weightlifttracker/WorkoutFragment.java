package com.autilite.weightlifttracker;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.autilite.weightlifttracker.database.WorkoutContract;
import com.autilite.weightlifttracker.database.WorkoutProgramDbHelper;
import com.autilite.weightlifttracker.program.Exercise;
import com.autilite.weightlifttracker.program.Workout;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkoutFragment extends Fragment implements CreateWorkoutDialog.CreateWorkoutListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private WorkoutProgramDbHelper workoutDb;

    public WorkoutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateWorkoutDialog frag = new CreateWorkoutDialog();
                frag.setTargetFragment(WorkoutFragment.this, 0);
                frag.show(getActivity().getSupportFragmentManager(), "CreateWorkoutDialog");
            }
        });
        workoutDb = new WorkoutProgramDbHelper(getActivity());
        List<Workout> workouts = getAllWorkouts();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.workout_recycler_view);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new WorkoutAdapter(getActivity(), workouts);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        TableLayout table = (TableLayout) dialog.getDialog().findViewById(R.id.workout_create_table);
        EditText nameEditText = (EditText) dialog.getDialog().findViewById(R.id.workout_create_name);
        String workoutName = nameEditText.getText().toString();

        // Create workout
        if (workoutName.equals("")) {
            // TODO prevent UI from closing if empty workout name
            Toast.makeText(getActivity(), "Workout could not be created", Toast.LENGTH_LONG).show();
            return;
        }
        long workoutId = workoutDb.createWorkout(workoutName);
        if (workoutId == -1) {
            Toast.makeText(getActivity(), "Workout could not be created", Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(getActivity(), "New workout \"" + workoutName + "\" created", Toast.LENGTH_LONG).show();
        }

        // Ignore first row since that is the headings
        for (int i = 1; i < table.getChildCount(); i++) {
            View c = table.getChildAt(i);
            if (c instanceof TableRow) {
                // Get the exercise stats
                TableRow row = (TableRow) c;
                Button exBtn = ((Button) row.findViewById(R.id.workout_create_exercise_chooser));
                Object exercise = exBtn.getTag();
                Editable sets = ((EditText) row.findViewById(R.id.workout_create_sets)).getText();
                Editable reps = ((EditText) row.findViewById(R.id.workout_create_reps)).getText();
                Editable weight = ((EditText) row.findViewById(R.id.workout_create_weight)).getText();

                if (exercise == null)
                    continue;
                long exerciseId = Long.valueOf(String.valueOf(exercise));

                if (sets == null || reps == null || weight == null) {
                    // TODO set default values
                    continue;
                }
                int wSets = Integer.parseInt(sets.toString());
                int wReps = Integer.parseInt(reps.toString());
                float wWeight = Float.parseFloat(weight.toString());

                // Create ExerciseStat
                long exerciseStatId = workoutDb.createExerciseStat(exerciseId, wSets, wReps, wWeight);
                if (exerciseStatId == -1) {
                    Toast.makeText(getActivity(), "Could not create exercise " + exBtn.getText().toString(), Toast.LENGTH_LONG).show();
                    continue;
                }
                if (!workoutDb.addExerciseToWorkout(workoutId, exerciseId)){
                    Toast.makeText(getActivity(), "Could not add " + exBtn.getText().toString() +
                            " to " + workoutName, Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    public List<Workout> getAllWorkouts() {
        Cursor workoutCursor = workoutDb.getAllWorkouts();
        List<Workout> workouts = new ArrayList<>();
        while (workoutCursor.moveToNext()) {
            long workoutId = workoutCursor.getLong(workoutCursor.getColumnIndex(WorkoutContract.WorkoutEntry._ID));
            String workoutName = workoutCursor.getString(workoutCursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_NAME));
            Workout w = new Workout(workoutName);

            Cursor eStat = workoutDb.getAllExerciseStatForWorkout(workoutId);
            while (eStat.moveToNext()) {
                long exerciseId = eStat.getLong(0);
                String exerciseName = eStat.getString(1);
                int set = eStat.getInt(2);
                int rep = eStat.getInt(3);
                float weight = eStat.getFloat(4);
                Exercise e = new Exercise(exerciseName, set, rep, weight);
                w.addExercise(e);
            }
            workouts.add(w);
            eStat.close();

        }
        workoutCursor.close();
        return workouts;
    }

    private class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

        private Context mContext;
        private List<Workout> workouts;

        public class WorkoutViewHolder extends RecyclerView.ViewHolder {
            private TextView workoutName;
            private ListView exercises;

            public WorkoutViewHolder(View itemView) {
                super(itemView);
                workoutName = (TextView) itemView.findViewById(R.id.workout_name);
                exercises = (ListView) itemView.findViewById(R.id.workout_exercises);
            }
        }

        public WorkoutAdapter(Context context, List<Workout> workouts) {
            mContext = context;
            this.workouts = workouts;
        }

        @Override
        public WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_card, parent, false);
            return new WorkoutViewHolder(view);
        }

        @Override
        public void onBindViewHolder(WorkoutViewHolder holder, int position) {
            Workout workout = workouts.get(position);

            // Add workout name
            holder.workoutName.setText(workout.getName());

            // just show the exercise name for now
            List<String> exercises = new ArrayList<>();
            for (Exercise e : workout.getExercises()) {
                exercises.add(e.getName());
            }
            ArrayAdapter<String> eAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, exercises);
            holder.exercises.setAdapter(eAdapter);
        }

        @Override
        public int getItemCount() {
            return workouts.size();
        }

    }
}
