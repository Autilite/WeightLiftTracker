package com.autilite.plan_g.activity;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.autilite.plan_g.R;
import com.autilite.plan_g.fragment.AbstractBaseModelFragment;
import com.autilite.plan_g.fragment.EditExerciseStatFragment;
import com.autilite.plan_g.fragment.dialog.SelectExerciseFragment;
import com.autilite.plan_g.program.BaseExercise;
import com.autilite.plan_g.program.BaseModel;
import com.autilite.plan_g.program.Exercise;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditExerciseStat extends CreateForm implements EditExerciseStatFragment.OnEditExerciseFragmentInteractionListener, SelectExerciseFragment.OnExerciseSelectInteractionListener{
    private static final String TAG = EditExerciseStat.class.getName();
    private Exercise exercise;

    public EditExerciseStat() {
    }

    @Override
    protected AbstractBaseModelFragment getCreateModelInstance() {
        setTitle(R.string.create_exercise);
        return EditExerciseStatFragment.newInstance(null);
    }

    @Override
    protected AbstractBaseModelFragment getEditModelInstance(@NonNull BaseModel model) {
        setTitle(R.string.edit_exercise);
        exercise = (Exercise) model;
        return EditExerciseStatFragment.newInstance(exercise);
    }

    @Override
    protected boolean onDeleteEntry(@NonNull BaseModel model) {
        // TODO only delete exercise from the workout list
        return db.deleteExercise(model.getId());
    }

    @Override
    protected BaseModel insertNewEntry(Bundle fields) {
        BaseExercise baseExercise = fields.getParcelable(EditExerciseStatFragment.FIELD_KEY_BASE_EXERCISE);
        String name = fields.getString(EditExerciseStatFragment.FIELD_KEY_NAME);
        String note = fields.getString(EditExerciseStatFragment.FIELD_KEY_DESCRIPTION);
        int sets = fields.getInt(EditExerciseStatFragment.FIELD_KEY_SETS);
        int reps = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS);
        int repsMin = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS_MIN);
        int repsMax = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS_MAX);
        int repsIncr = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS_INCR);
        double weight = fields.getDouble(EditExerciseStatFragment.FIELD_KEY_WEIGHT);
        double weightIncr = fields.getDouble(EditExerciseStatFragment.FIELD_KEY_WEIGHT_INCR);
        int restTime = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REST_TIMER);

        if (baseExercise == null) {
            return null;
        }

        long id = db.createExercise(baseExercise.getId(), sets, reps, repsMin, repsMax, repsIncr, weight, weightIncr, restTime);
        if (id != -1) {
            return new Exercise(id, name, note, baseExercise.getId(), sets, reps, repsMin, repsMax, repsIncr, weight, weightIncr, restTime);
        }
        return null;
    }

    @Override
    protected BaseModel editEntry(Bundle fields) {
        BaseExercise baseExercise = fields.getParcelable(EditExerciseStatFragment.FIELD_KEY_BASE_EXERCISE);
        String name = fields.getString(EditExerciseStatFragment.FIELD_KEY_NAME);
        String note = fields.getString(EditExerciseStatFragment.FIELD_KEY_DESCRIPTION);
        int sets = fields.getInt(EditExerciseStatFragment.FIELD_KEY_SETS);
        int reps = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS);
        int repsMin = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS_MIN);
        int repsMax = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS_MAX);
        int repsIncr = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS_INCR);
        double weight = fields.getDouble(EditExerciseStatFragment.FIELD_KEY_WEIGHT);
        double weightIncr = fields.getDouble(EditExerciseStatFragment.FIELD_KEY_WEIGHT_INCR);
        int restTime = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REST_TIMER);

        if (baseExercise == null) {
            return null;
        }

        try {
            int numRowsUpdate = db.updateExercise(
                    exercise.getId(), baseExercise.getId(), sets, reps, repsMin, repsMax, repsIncr, weight, weightIncr, restTime);
            if (numRowsUpdate == 1) {
                exercise.setBaseExerciseId(baseExercise.getId());
                exercise.setName(name);
                exercise.setDescription(note);
                exercise.setSets(sets);
                exercise.setRepRange(reps, repsMin, repsMax);
                exercise.setRepsIncrement(repsIncr);
                exercise.setWeight(weight);
                exercise.setWeightIncrement(weightIncr);
                exercise.setRestTime(restTime);
                return exercise;
            }
        } catch (SQLiteConstraintException e) {
            Log.d(TAG, e.getMessage());
        } catch (IllegalArgumentException e) {
            // The database was successfully able to update the exercise but updating the model was
            // unsuccessful. This is likely due to an inconsistency of constraints.
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    @Override
    public void onChooseExercise() {
        SelectExerciseFragment selectExerciseFragment = new SelectExerciseFragment();
        selectExerciseFragment.show(getSupportFragmentManager(), "fragment_select_exercise");
    }

    @Override
    public void onNewExerciseEntry(BaseExercise exercise) {
        EditExerciseStatFragment exerciseStatFragment = (EditExerciseStatFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (exerciseStatFragment != null) {
            exerciseStatFragment.updateExerciseView(exercise);
        } else {
            Log.w(TAG, EditExerciseStatFragment.class.getName() + " could not be found.");
        }

        Fragment selectExerciseFragment = getSupportFragmentManager().findFragmentByTag("fragment_select_exercise");
        if (selectExerciseFragment != null) {
            // TODO consider communicating directly with the dialog's fragment rather than the dialogfragment
            ((SelectExerciseFragment) selectExerciseFragment).dismiss();
        }
    }

    @Override
    public void onLinkExerciseEntry(Exercise exercise) {
        // TODO
    }
}
