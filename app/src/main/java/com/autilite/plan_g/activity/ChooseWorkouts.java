package com.autilite.plan_g.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.autilite.plan_g.R;
import com.autilite.plan_g.database.WorkoutDatabase;
import com.autilite.plan_g.program.Workout;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChooseWorkouts extends AppCompatActivity {

    public static final String EXTRA_SELECTED_IDS = "EXTRA_SELECTED_IDS";
    public static final String EXTRA_DAY = "EXTRA_PROGRAM_DAY";

    public static final String EXTRA_RESULT_CHOSEN_WORKOUTS = "EXTRA_RESULT_CHOSEN_WORKOUTS";
    public static final String EXTRA_RESULT_DAY = "EXTRA_RESULT_PROGRAM_DAY";

    public static final String RESULT_ACTION = "com.autilite.plan_g.activity.ChooseWorkouts.RESULT_ACTION";

    private static final String INSTANCE_KEY_FRAGMENT = "INSTANCE_KEY_FRAGMENT";

    private int day;

    private ChooseWorkoutsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_workout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            // We are being restored from a previous state so we don't need to re-initialize
            // the fragment
            fragment = (ChooseWorkoutsFragment) getSupportFragmentManager().getFragment(savedInstanceState, INSTANCE_KEY_FRAGMENT);
        } else if (getIntent().getExtras() != null) {
            // Since the program may not have been created let, we let the parent activity
            // handle the passing the default selected workout IDs
            day = getIntent().getIntExtra(EXTRA_DAY, -1);
            long[] selectedIds = getIntent().getLongArrayExtra(EXTRA_SELECTED_IDS);

            fragment = (ChooseWorkoutsFragment) ChooseWorkoutsFragment.newInstance(selectedIds);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, INSTANCE_KEY_FRAGMENT, fragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void finish() {
        setResult();
        super.finish();
    }

    private void setResult() {
        Intent intent = new Intent();
        intent.setAction(RESULT_ACTION);
        intent.putExtra(EXTRA_RESULT_DAY, day);
        intent.putExtra(EXTRA_RESULT_CHOSEN_WORKOUTS, fragment.getSelectedWorkouts());
        setResult(Activity.RESULT_OK, intent);
    }

    public static class ChooseWorkoutsFragment extends Fragment {
        private static final String ARG_SELECTED_IDS = "ARG_SELECTED_IDS";

        private View view;
        private RecyclerView recyclerView;
        private WorkoutSelectAdapter adapter;

        private WorkoutDatabase db;

        private List<Workout> workouts;
        private Set<Long> selectedWorkouts;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            db = new WorkoutDatabase(getActivity());
            if (getArguments() != null) {
                long[] ids = getArguments().getLongArray(ARG_SELECTED_IDS);
                selectedWorkouts = new HashSet<>();
                if (ids != null) {
                    // The ID that is about to be looped is the currently selected Workouts.
                    // We do not expect the size of this list to be very big so even though
                    // we loop through this array a couple of times, we do not expect there
                    // to be any performance issues
                    for (long id : ids) {
                        selectedWorkouts.add(id);
                    }
                }
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            db.close();
        }

        public static Fragment newInstance(long[] selectedIds) {
            Bundle args = new Bundle();
            args.putLongArray(ARG_SELECTED_IDS, selectedIds);

            Fragment frag = new ChooseWorkoutsFragment();
            frag.setArguments(args);
            return frag;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            if (view != null) {
                return view;
            }
            view = inflater.inflate(R.layout.fragment_recycle_view, container, false);

            // TODO refactor fragment_recycle_view to not include fab
            View fab = view.findViewById(R.id.fab);
            fab.setVisibility(View.GONE);

            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            workouts = db.getAllWorkoutsList();
            adapter = new WorkoutSelectAdapter(workouts);
            recyclerView.setAdapter(adapter);

            return view;
        }

        public long[] getSelectedWorkouts() {
            Long[] ids = selectedWorkouts.toArray(new Long[selectedWorkouts.size()]);
            long[] primitiveArray = new long[selectedWorkouts.size()];
            for (int i = 0; i < selectedWorkouts.size(); i++) {
                primitiveArray[i] = ids[i];
            }
            return primitiveArray;
        }

        private class WorkoutSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
            private final List<Workout> workouts;

            public WorkoutSelectAdapter(List<Workout> workouts) {
                this.workouts = workouts;
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_checkbox, parent, false);
                return new WorkoutSelectViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                Workout workout = workouts.get(position);
                WorkoutSelectViewHolder wsh = ((WorkoutSelectViewHolder) holder);
                wsh.bindWorkout(workout);
            }

            @Override
            public int getItemCount() {
                return workouts.size();
            }

            private class WorkoutSelectViewHolder extends RecyclerView.ViewHolder {
                private AppCompatCheckBox checkbox;
                private Workout workout;

                public WorkoutSelectViewHolder(View itemView) {
                    super(itemView);
                    checkbox = (AppCompatCheckBox)itemView.findViewById(R.id.checkbox);
                    checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                selectedWorkouts.add(workout.getId());
                            } else {
                                selectedWorkouts.remove(workout.getId());
                            }
                        }
                    });
                }

                public void bindWorkout(Workout workout) {
                    this.workout = workout;
                    checkbox.setText(workout.getName());
                    checkbox.setHint(workout.getDescription());
                    checkbox.setChecked(selectedWorkouts.contains(workout.getId()));
                }
            }
        }
    }

}
