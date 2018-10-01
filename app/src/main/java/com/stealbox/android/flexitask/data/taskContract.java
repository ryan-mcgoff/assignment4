package com.stealbox.android.flexitask.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ryan Mcgoff (4086944), Jerry Kumar (3821971), Jaydin Mcmullan (9702973)
 */


/**
 * API Contract for the FlexiTask app. Containigng table, column names and values. This allows
 * for easy changes to the database
 */
public final class taskContract {

    // This empty constructor prevents anyone from from accidentally instantiating the contract class
    private taskContract() {}


    /**
     * Content authority, to avoid conflict with other Android providers
     */
    public static final String CONTENT_AUTHORITY = "com.stealbox.android.flexitask";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_Tasks = "tasks";
    public static final String PATH_HISTORY = "history";
    public static final String PATH_IRREGULAR = "irregular";





    public static final class TaskEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_Tasks);
        public static final Uri HISTORY_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_HISTORY);
        public static final Uri IRREGULAR_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_IRREGULAR);


        /** Name of database table for the tasks */
        public final static String TABLE_NAME = "tasks";
        public final static String LABEL_TABLE_NAME = "labels";
        public final static String History_TABLE_NAME = "history";
        public final static String IRREGULAR_TABLE_NAME = "irregular";

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_Tasks;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_Tasks;
        public static final String CONTENT_HISTORY_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORY;
        public static final String CONTENT_IRREGULAR_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORY;
        public static final String CONTENT_IRREGULAR_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_Tasks;
        /**
         * Unique ID number for each task
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        public final static String Label_ID = "label_id";


        /**
         * Is task active (able to be seen on the timeline), or inactive (available to view in the history section).
         * The only possible values are {@link #ACTIVE} or {@link #INACTIVE}
         *
         * Type: INTEGER
         */
        public final static String COLUMN_STATUS = "status";

        /**
         * Type of task.
         * The only possible selections are {@link #TYPE_FIXED}, {@link #TYPE_FLEXI}
         *
         * Type: INTEGER
         */
        public final static String COLUMN_TYPE_TASK = "task_type";


        /**
         * Title of the Task.
         *
         * Type: TEXT
         */
        public final static String COLUMN_TASK_TITLE ="title";


        /**
         * A description of the task
         */

        public final static String COLUMN_DESCRIPTION = "description";

        /**
         * Date for the task
         *
         * Type: TEXT
         */
        public final static String COLUMN_DATE = "date";

        public final static String COLUMN_DATETIME = "datetime";
        public final static String COLUMN_REMINDER_UNIT = "reminderunit";
        public final static String COLUMN_REMINDER_UNIT_BEFORE = "reminderunitbefore";

        /**
         * Time for the task (for Fixed tasks).
         *
         * Type: TEXT
         */
        public final static String COLUMN_TIME = "time";

        /**
         * Time for the task (for Fixed tasks).
         *
         * Type: TEXT
         */
        public final static String COLUMN_LABEL = "label";

        /**
         * How often this event recurs.
         *
         * The only possible selections are {@link #RECURRING_NEVER}, {@link #RECURRING_DAILY},
         * {@link #RECURRING_WEEKLY}, or {@link #RECURRING_YEARLY}, or custom.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_RECCURING_PERIOD = "recurring_period";

        /**
         * A String of dates the task was completed
         *
         * Type: STRING
         */
        public final static String COLUMN_HISTORY = "history";

        /**
         * Last completed date for {@link #TYPE_FLEXI}
         *
         * Type: INTEGER
         */
        public final static String COLUMN_LAST_COMPLETED = "last_completed";

        /**
         * Last completed date for {@link #TYPE_FLEXI}
         *
         * Type: INTEGER
         */
        public final static String COLUMN_LABEL_NAME = "label_name";




        /**
         * Spinner values
         */
        public static final int RECURRING_NEVER = 0;
        public static final int RECURRING_DAILY = 1;
        public static final int RECURRING_WEEKLY = 7;
        public static final int RECURRING_FORTNIGHTLY = 14;
        public static final int RECURRING_YEARLY = 365;

        /**
         * Possible values for type of task
         */
        public static final int TYPE_FIXED = 0;
        public static final int TYPE_FLEXI = 1;
        public static final int TYPE_ALL = 3;
        /**
         * Possible values for active/non-active, rather than deleting, the app could toggle these
         * allowing for a record of inactive tasks ("history") and letting the user reuse old completed tasks
         */
        public static final int INACTIVE = 0;
        public static final int ACTIVE = 1;

    }


}
