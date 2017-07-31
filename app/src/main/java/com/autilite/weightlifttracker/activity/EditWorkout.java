package com.autilite.weightlifttracker.activity;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.fragment.AbstractFormFragment;
import com.autilite.weightlifttracker.fragment.CreateWorkout;
import com.autilite.weightlifttracker.program.BaseModel;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditWorkout extends CreateForm {
    private static final String RESULT_ACTION = "com.autilite.weightlifttracker.activity.EditWorkout.RESULT_ACTION";

    public static final String EXTRA_RESULT_WORKOUT = "RESULT_WORKOUT";

    @Override
    protected AbstractFormFragment createContentFragment() {
        return new CreateWorkout();
    }

    @Override
    protected boolean saveForm() {
        BaseModel model = contentFragment.save();
        boolean isSuccess = model != null;
        if (!isSuccess) {
            Toast.makeText(this, R.string.create_workout_fail, Toast.LENGTH_SHORT).show();
        } else {
            Intent result = new Intent(RESULT_ACTION);
            result.setAction(RESULT_ACTION);
            result.putExtra(EXTRA_RESULT_WORKOUT, model);
            setResult(Activity.RESULT_OK, result);
        }
        return isSuccess;
    }
}