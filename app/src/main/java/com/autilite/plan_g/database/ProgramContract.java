package com.autilite.plan_g.database;

import android.provider.BaseColumns;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public final class ProgramContract {
    private ProgramContract() {
    }

    public static class ProgramEntry implements BaseColumns {
        public static final String TABLE_NAME = "Programs";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_DESCRIPTION = "Description";
        public static final String COLUMN_NUM_DAYS = "NumDays";
        public static final String COLUMN_CREATION = "CreationTime";
    }
}
