package com.autilite.weightlifttracker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.program.Program;
import com.autilite.weightlifttracker.program.Workout;
import com.autilite.weightlifttracker.widget.ExtendableListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kelvin on Jun 15, 2017.
 */

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder>{

    private Context mContext;
    private List<Program> programs;

    public class ProgramViewHolder extends RecyclerView.ViewHolder {
        private TextView programName;
        private ExtendableListView workouts;

        // Reuse workout_card for display purposes
        public ProgramViewHolder(View itemView) {
            super(itemView);
            programName = (TextView) itemView.findViewById(R.id.workout_name);
            workouts = (ExtendableListView) itemView.findViewById(R.id.workout_exercises);
        }
    }

    public ProgramAdapter(Context mContext, List<Program> programs) {
        this.mContext = mContext;
        this.programs = programs;
    }

    public ProgramViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate program card
        View view = LayoutInflater.from(mContext).inflate(R.layout.workout_card, parent, false);
        return new ProgramViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProgramViewHolder holder, int position) {
        Program program = programs.get(position);

        holder.programName.setText(program.getName());

        List<String> workout = new ArrayList<>();
        for (Workout w : program.getWorkouts()) {
            workout.add(w.getName());
        }
        ArrayAdapter<String> eAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, workout);
        holder.workouts.setAdapter(eAdapter);
    }

    @Override
    public int getItemCount() {
        return programs.size();
    }
}