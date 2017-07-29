package com.autilite.weightlifttracker.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.activity.EditExerciseStat;
import com.autilite.weightlifttracker.database.WorkoutDatabase;
import com.autilite.weightlifttracker.program.Exercise;
import com.autilite.weightlifttracker.program.Workout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateWorkout extends Fragment {

    private static final int CREATE_EXERCISE = 1;
    private static final int EDIT_EXERCISE = 2;

    private RecyclerView mRecyclerView;
    private AddExerciseAdapter mAdapter;
    private WorkoutDatabase db;

    private EditText mEditName;
    private EditText mEditDescription;

    private List<Exercise> exercises;

    public CreateWorkout() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new WorkoutDatabase(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_workout, container, false);
        mEditName = (EditText) view.findViewById(R.id.input_name);
        mEditDescription = (EditText) view.findViewById(R.id.input_description);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        exercises = new ArrayList<>();
        mAdapter = new AddExerciseAdapter(exercises);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_EXERCISE) {
            if (resultCode == Activity.RESULT_OK) {
                Exercise e = data.getParcelableExtra(EditExerciseStat.EXTRA_RESULT_EXERCISE);
                exercises.add(e);
                mAdapter.notifyExerciseInserted(exercises.size() - 1);
            }
        } else if (requestCode == EDIT_EXERCISE) {
            if (resultCode == Activity.RESULT_OK) {
                Exercise e = data.getParcelableExtra(EditExerciseStat.EXTRA_RESULT_EXERCISE);
                // Traversing through the entire exercise list is okay because the usual
                // length of a single workout should not be very high
                // As such, we don't need to worry about performance
                for (int i = 0; i < exercises.size(); i++) {
                    Exercise exercise = exercises.get(i);
                    if (exercise.getId() == e.getId()) {
                        exercises.set(i, e);
                        mAdapter.notifyExerciseChanged(i);
                        break;
                    }
                }
            }
        }
    }

    public Workout save() {
        String name = mEditName.getText().toString();
        String description = mEditDescription.getText().toString();

        if (name.equals("")) {
            return null;
        }
        long workoutId = db.createWorkout(name);
        if (workoutId == -1) {
            return null;
        }
        Workout workout = new Workout(workoutId, name);

        for (Exercise e : exercises) {
            long id = db.addExerciseToWorkout(workoutId, e.getId());
            if (id != -1) {
                workout.addExercise(e);
            } else {
                String s = String.format(getString(R.string.add_workout_exercise_fail), e.getName());
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }
        }

        return workout;
    }

    public class AddExerciseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEADER_VIEW = 0;
        private static final int CONTENT_VIEW = 1;
        private static final int FOOTER_VIEW = 2;

        private static final int HEADER_SIZE = 1;
        private static final int FOOTER_SIZE = 1;

        private List<Exercise> exercises;

        public AddExerciseAdapter(List<Exercise> exercises) {
            this.exercises = exercises;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == HEADER_VIEW) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header, parent, false);
                return new TitleViewHolder(view);
            }
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_exercise, parent, false);
            return new ExerciseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == HEADER_VIEW) {
                TextView text = ((TitleViewHolder) holder).textView;
                text.setText(getString(R.string.exercises));
            } else if (holder.getItemViewType() == CONTENT_VIEW) {
                Exercise e = exercises.get(position - HEADER_SIZE);

                ExerciseViewHolder vh = (ExerciseViewHolder) holder;
                vh.setExercise(e);
                vh.updateView();
            } else if (holder.getItemViewType() == FOOTER_VIEW) {
                ExerciseViewHolder vh = (ExerciseViewHolder) holder;
                vh.updateView();
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position < HEADER_SIZE) {
                return HEADER_VIEW;
            } else if (position >= HEADER_SIZE + exercises.size()) {
                return FOOTER_VIEW;
            } else
                return CONTENT_VIEW;
        }

        @Override
        public int getItemCount() {
            return exercises.size() + HEADER_SIZE + FOOTER_SIZE;
        }

        /**
         * A helper function to notify any registered observers that the <code>Exercise</code>
         * reflected at <code>position</code> has been newly inserted.
         *
         * @param position Position of the newly inserted Exercise in the data set
         *
         * @see #notifyItemInserted(int)
         */
        public void notifyExerciseInserted(int position) {
            notifyItemInserted(HEADER_SIZE + position);
        }

        /**
         * A helper function to notify any registered observers that the <code>Exercise</code>
         * reflected at <code>position</code> has been changed.
         *
         * @param position Position of the newly inserted Exercise in the data set
         *
         * @see #notifyItemChanged(int)
         */
        public void notifyExerciseChanged(int position) {
            notifyItemChanged(HEADER_SIZE + position);
        }

        public class TitleViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;

            public TitleViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.header);
            }
        }

        public class ExerciseViewHolder extends RecyclerView.ViewHolder {

            private TextView exerciseName;
            private TextView exerciseDetail;
            private Exercise exercise;

            public ExerciseViewHolder(View itemView) {
                super(itemView);
                exerciseName = (TextView) itemView.findViewById(R.id.exercise_name);
                exerciseDetail = (TextView) itemView.findViewById(R.id.exercise_details);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getItemViewType() == FOOTER_VIEW) {
                            // Start activity to create new exercise
                            Intent intent = new Intent(getActivity(), EditExerciseStat.class);
                            startActivityForResult(intent, CREATE_EXERCISE);
                        } else {
                            // Start activity to edit the existing exercise
                            Intent intent = new Intent(getActivity(), EditExerciseStat.class);
                            intent.putExtra(EditExerciseStat.EXTRA_EXERCISE, exercise);
                            startActivityForResult(intent, EDIT_EXERCISE);
                        }
                    }
                });
            }

            public void setName(String name) {
                exerciseName.setText(name);
            }

            private void setDetails(String details) {
                exerciseDetail.setText(details);
            }

            private void setStatGone() {
                exerciseDetail.setVisibility(View.GONE);
            }

            private void setStatVisible() {
                exerciseDetail.setVisibility(View.VISIBLE);
            }

            public void setExercise(Exercise exercise) {
                this.exercise = exercise;
            }

            public void updateView() {
                if (getItemViewType() == FOOTER_VIEW) {
                    setName(getString(R.string.add_exercise));
                    setStatGone();
                } else {
                    setStatVisible();
                    setName(exercise.getName());
                    setDetails(exercise.getSets() + "x" + exercise.getReps());
                }
            }
        }

    }
}
