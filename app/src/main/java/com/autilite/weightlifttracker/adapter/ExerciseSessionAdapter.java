package com.autilite.weightlifttracker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.program.Exercise;
import com.autilite.weightlifttracker.widget.ExtendableListView;

import java.util.List;

/**
 * Created by Kelvin on Jun 17, 2017.
 */

public class ExerciseSessionAdapter extends RecyclerView.Adapter<ExerciseSessionAdapter.ExerciseSessionViewHolder> {

    private Context mContext;
    private final List<Exercise> exercises;
    private OnItemClickListener mListener;

    public class ExerciseSessionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int expandedIcon = R.drawable.ic_expand_more_black_24dp;
        int collpaseIcon = R.drawable.ic_expand_less_black_24dp;

        private TextView name;
        private TextView sets;
        private TextView btnOptions;
        private ImageView expandExercises;
        private ExtendableListView listView;

        private boolean isListVisible;

        public ExerciseSessionViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.exercise_name);
            sets = (TextView) itemView.findViewById(R.id.complete_sets);
            btnOptions = (TextView) itemView.findViewById(R.id.exercise_options);
            expandExercises = (ImageView) itemView.findViewById(R.id.expand_exercises);
            listView = (ExtendableListView) itemView.findViewById(R.id.exercise_details);
            listView.setVisibility(View.GONE);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener !=  null) {
                mListener.onItemClick(view, exercises.get(getAdapterPosition()));
            }
        }
    }

    public ExerciseSessionAdapter(Context mContext, List<Exercise> exercises) {
        this.mContext = mContext;
        this.exercises = exercises;
    }

    @Override
    public ExerciseSessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.exercise_session_card, parent, false);
        return new ExerciseSessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ExerciseSessionViewHolder holder, int position) {
        Exercise e = exercises.get(position);
        holder.name.setText(e.getName());
        String completeSet = "Complete set: /" + e.getSets();
        holder.sets.setText(completeSet);

        // Setup exercise sets
        holder.expandExercises.setImageResource(holder.expandedIcon);
        String[] sets = new String[e.getSets()];
        for (int i = 0; i < e.getSets(); ++i) {
            sets[i] = String.valueOf(i+1);
        }
        ArrayAdapter adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1,sets);
        holder.listView.setAdapter(adapter);

        holder.btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        holder.expandExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle list visibility
                holder.isListVisible = !holder.isListVisible;
                holder.listView.setVisibility(holder.isListVisible ? View.VISIBLE : View.GONE);
                holder.expandExercises.setImageResource(holder.isListVisible ? holder.collpaseIcon : holder.expandedIcon);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, Exercise exercise);
    }

}
