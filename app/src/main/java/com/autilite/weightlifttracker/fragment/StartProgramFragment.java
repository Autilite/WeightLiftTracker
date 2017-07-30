package com.autilite.weightlifttracker.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.database.WorkoutDatabase;
import com.autilite.weightlifttracker.service.WorkoutService;
import com.autilite.weightlifttracker.activity.WorkoutSessionActivity;
import com.autilite.weightlifttracker.adapter.WorkoutAdapter;
import com.autilite.weightlifttracker.program.Workout;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartProgramFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartProgramFragment extends Fragment {
    private static final String ARG_PROGRAM_ID = "ARG_PROGRAM_ID";
    private static final String ARG_PROGRAM_NAME = "ARG_PROGRAM_NAME";
    private static final String ARG_PROGRAM_DAY = "ARG_PROGRAM_DAY";

    private long programId;
    private String programName;
    private int programDay;

    private WorkoutDatabase workoutDb;
    private List<Workout> workouts;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private WorkoutAdapter mAdapter;


    public StartProgramFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param programId The id of the program
     * @param programName The program name
     * @return A new instance of fragment StartProgramFragment.
     */
    public static StartProgramFragment newInstance(long programId, String programName, int programDay) {
        StartProgramFragment fragment = new StartProgramFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PROGRAM_ID, programId);
        args.putString(ARG_PROGRAM_NAME, programName);
        args.putInt(ARG_PROGRAM_DAY, programDay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            programId = getArguments().getLong(ARG_PROGRAM_ID);
            programName = getArguments().getString(ARG_PROGRAM_NAME);
            programDay = getArguments().getInt(ARG_PROGRAM_DAY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout view = (FrameLayout) inflater.inflate(R.layout.fragment_recycle_view, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO prompt to resume existing session
                // Start the service
                Intent workoutService = new Intent(getActivity(), WorkoutService.class);
                workoutService.setAction(WorkoutService.ACTION_START_NEW_SESSION);
                workoutService.putExtra(WorkoutSessionActivity.EXTRA_PROGRAM_ID, programId);
                workoutService.putExtra(WorkoutSessionActivity.EXTRA_PROGRAM_NAME, programName);
                workoutService.putExtra(WorkoutSessionActivity.EXTRA_PROGRAM_DAY, programDay);
                getActivity().startService(workoutService);
            }
        });

        workoutDb = new WorkoutDatabase(getActivity());
        workouts = workoutDb.getProgramWorkouts(programId, programDay);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new WorkoutAdapter(getActivity(), workouts);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

}
