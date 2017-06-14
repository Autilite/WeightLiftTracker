package com.autilite.weightlifttracker.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.database.ProgramContract;
import com.autilite.weightlifttracker.database.ProgramWorkoutContract;
import com.autilite.weightlifttracker.database.WorkoutContract;
import com.autilite.weightlifttracker.database.WorkoutProgramDbHelper;
import com.autilite.weightlifttracker.fragment.dialog.AbstractCreateDialog;
import com.autilite.weightlifttracker.fragment.dialog.CreateProgramDialog;
import com.autilite.weightlifttracker.program.Program;
import com.autilite.weightlifttracker.program.Workout;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramFragment extends Fragment implements AbstractCreateDialog.CreateDialogListener {

    private WorkoutProgramDbHelper workoutDb;
    private List<Program> programs;

    public ProgramFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycle_view, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateProgramDialog frag = new CreateProgramDialog();
                frag.setTargetFragment(ProgramFragment.this, 0);
                frag.show(getActivity().getSupportFragmentManager(), "CreateProgramDialog");
            }
        });
        workoutDb = new WorkoutProgramDbHelper(getActivity());
        programs = getAllPrograms();
        return view;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        TableLayout table = (TableLayout) dialog.getDialog().findViewById(R.id.entry_create_table);
        EditText nameEditText = (EditText) dialog.getDialog().findViewById(R.id.heading_create_name_editview);
        String programName = nameEditText.getText().toString();

        // 1) Create the program
        if (programName.equals("")) {
            Toast.makeText(getActivity(), "Program could not be created", Toast.LENGTH_LONG).show();
            return;
        }
        long programId = workoutDb.createProgram(programName);
        if (programId == -1) {
            Toast.makeText(getActivity(), "Program could not be created", Toast.LENGTH_LONG).show();
            return;
        }
        Program program = new Program(programName, "");

        // 2) Retrieve the values from the table
        // Ignore first row because that's the header
        for (int i = 1; i < table.getChildCount(); i++) {
            TableRow c = (TableRow) table.getChildAt(i);
            // Workout Button
            Button workoutBtn = (Button) c.findViewById(R.id.program_create_workout_chooser);
            String workoutName = workoutBtn.getText().toString();
            // WorkoutId is stored in tag
            Object buttonTag = workoutBtn.getTag();
            if (buttonTag == null) {
                continue;
            }
            long workoutId = Long.parseLong(buttonTag.toString());

            // Day spinner
            AppCompatSpinner daySpinner = (AppCompatSpinner) c.findViewById(R.id.program_create_day);
            int day = Integer.parseInt(daySpinner.getSelectedItem().toString());

            // 3) Validate and insert into table
            if (!workoutDb.addWorkoutToProgram(programId, workoutId, day)){
                Toast.makeText(getActivity(), "Could not add " + workoutName +
                        " to " + programName, Toast.LENGTH_LONG).show();
            }

            Toast.makeText(getActivity(), "Workout " + workoutName + " added to " + programName,
                    Toast.LENGTH_LONG).show();

            Workout workout = new Workout(workoutName);
            program.addWorkout(workout);
        }

        // Update the UI
        // TODO
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    public List<Program> getAllPrograms() {
        List<Program> listOfPrograms = new ArrayList<>();
        // Get Cursor of all program IDs
        Cursor programs = workoutDb.getAllPrograms();

        // For each programID, grab all its workouts
        while(programs.moveToNext()) {
            // Get program info
            long progId = programs.getLong(programs.getColumnIndex(ProgramContract.ProgramEntry._ID));
            String progName = programs.getString(programs.getColumnIndex(ProgramContract.ProgramEntry.COLUMN_NAME));
            Program p = new Program(progName, "");

            // Grab workouts associated with the program
            Cursor programWorkouts = workoutDb.getProgramWorkoutTableJoinedWithName(progId);
            while(programWorkouts.moveToNext()) {
                // TODO add robust way of getting column index
                long workoutId = programWorkouts.getLong(programWorkouts.getColumnIndex(
                               ProgramWorkoutContract.ProgramWorkoutEntry.COLUMN_WORKOUT_ID));
                String workoutName = programWorkouts.getString(programWorkouts.getColumnIndex(
                                WorkoutContract.WorkoutEntry.COLUMN_NAME));
                Workout w = new Workout(workoutName);
                p.addWorkout(w);
            }
            listOfPrograms.add(p);
            programWorkouts.close();
        }
        programs.close();

        return listOfPrograms;
    }
}
