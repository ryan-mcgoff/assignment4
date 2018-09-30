package com.example.android.flexitask;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import android.support.v4.content.CursorLoader;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.flexitask.data.taskContract;
import com.example.android.flexitask.data.taskDBHelper;

import java.util.Calendar;

/**
 * Created by Ryan Mcgoff (4086944), Jerry Kumar (3821971), Jaydin Mcmullan (9702973)
 * <p>
 * A {@link Fragment} subclass for the Fixed-task fragment of the app
 * that implements the {@link LoaderManager] interface to pass fixed task data to the a cursor
 * adaptor for the fragment's listview.
 */
public class FixedTaskTimeLine extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TASKLOADER = 0;
    TaskCursorAdaptor mTaskCursorAdaptor;


    private android.support.design.widget.FloatingActionButton mFabFixedTask;
    private taskDBHelper mDbHelper;
    /*ID of list item clicked*/
    private long item_iD;
    private Toolbar bottomBar;
    private boolean toolBarShown;
    private int lastClickedPostion;
    private long lastClickedID;
    private String labelSQL;
    private String label;
    private ImageView editButtonToolBar;
    private ImageView doneButtonToolBar;
    private ImageView deleteButtonToolBar;

    public FixedTaskTimeLine() {
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inflates the XML layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_fixed_task_timeline, container, false);

        //get label filter selected from the navigation bar
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        label = preferences.getString("label", "All");

        if (!(label.equals("All"))) {
            labelSQL = "AND label = " + "'" + label + "'";

        } else {
            labelSQL = "";
        }

        mDbHelper = new taskDBHelper(getActivity());
        mFabFixedTask = rootView.findViewById(R.id.fixedFab);
        colorSwitch();

        mFabFixedTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FixedTaskEditor.class);

                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the tasks data
        final ListView timeLineListView = rootView.findViewById(R.id.timelineListView);

        //Find and set empty view on the ListView, so that it only shows a helpful message when the list has 0 items.
        View emptyView = rootView.findViewById(R.id.empty_view);
        timeLineListView.setEmptyView(emptyView);

        //sets up an Cursoradaptor for the listview
        //The adaptor creates a list item for each row in the returned cursor
        mTaskCursorAdaptor = new TaskCursorAdaptor(getActivity(), null);
        timeLineListView.setAdapter(mTaskCursorAdaptor);

        bottomBar = rootView.findViewById(R.id.toolbar);
        editButtonToolBar = bottomBar.findViewById(R.id.edit);
        doneButtonToolBar = bottomBar.findViewById(R.id.done);
        deleteButtonToolBar = bottomBar.findViewById(R.id.delete);

        //On click listener for anytime a user clicks on a listView item
        timeLineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                item_iD = id;
                if (toolBarShown == false) {
                    lastClickedPostion = position;
                    lastClickedID = id;

                    timeLineListView.setItemChecked(position, true);

                    Animation fabDown = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);
                    Animation slideUpBar = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
                    Animation slideUpDone = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
                    Animation slideUpEdit = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
                    Animation slideUpDel = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
                    slideUpBar.setStartOffset(150);
                    slideUpDone.setStartOffset(170);
                    slideUpEdit.setStartOffset(230);
                    slideUpDel.setStartOffset(280);

                    getActivity().setTitle("task selected");
                    toolBarShown = true;
                    mFabFixedTask.setVisibility(View.GONE);

                    Animation bottomUp = AnimationUtils.loadAnimation(getContext(), R.anim.show_from_bottom);
                    mFabFixedTask.startAnimation(fabDown);
                    bottomBar.setVisibility(View.VISIBLE);
                    doneButtonToolBar.setVisibility(View.VISIBLE);
                    editButtonToolBar.setVisibility(View.VISIBLE);
                    deleteButtonToolBar.setVisibility(View.VISIBLE);
                    bottomBar.startAnimation(slideUpBar);
                    doneButtonToolBar.startAnimation(slideUpDone);
                    editButtonToolBar.startAnimation(slideUpEdit);
                    deleteButtonToolBar.startAnimation(slideUpDel);
                } else {
                    lastClickedPostion = position;
                    lastClickedID = id;

                    timeLineListView.setItemChecked(position, false);

                    resetUI();

                }


            }
        });

        /*START OF TOOLBAR BUTTONS*/

        /*EDITING BUTTON -
         * gets the URI for the selected list item, and sends it to the editor activity for processing
         */
        editButtonToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FixedTaskEditor.class);

                //creates a URI for the specific task that was clicked on
                //ie: task on row three
                //would be "content.example.android.flexitask/task" + "3" (the ID)
                timeLineListView.setItemChecked(lastClickedPostion, false);
                resetUI();
                Uri currentTaskUri = ContentUris.withAppendedId(taskContract.TaskEntry.CONTENT_URI, item_iD);

                //sets the URI for that intent
                intent.setData(currentTaskUri);

                startActivity(intent);
            }
        });


        /*DONE BUTTON -
         * When the done button (tick) on the toolbar has been selected, the app updates
         * the date for that task based on the recurring period. Or if it doesn't have one it simply deletes that task
         */
        doneButtonToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creates a URI for the specific task that was clicked on
                //ie: task on row three
                //would be "content.example.android.flexitask/task" + "3" (the ID)
                Uri currentTaskUri = ContentUris.withAppendedId(taskContract.TaskEntry.CONTENT_URI, item_iD);

                Cursor cursorc = getActivity().getContentResolver().query(currentTaskUri, null, null, null, null, null);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();


                //Creates a raw SQL statment to retrieve the recurring number and due date for the selected task

                if (cursorc != null && cursorc.moveToFirst()) {
                    int recurringColumnIndex = cursorc.getColumnIndex(taskContract.TaskEntry.COLUMN_RECCURING_PERIOD);
                    int dateColumnIndex = cursorc.getColumnIndex(taskContract.TaskEntry.COLUMN_DATE);
                    int reminderUnitIndex = cursorc.getColumnIndex(taskContract.TaskEntry.COLUMN_REMINDER_UNIT);
                    int reminderBeforeIndex = cursorc.getColumnIndex(taskContract.TaskEntry.COLUMN_REMINDER_UNIT_BEFORE);
                    int titleColumnIndex = cursorc.getColumnIndex(taskContract.TaskEntry.COLUMN_TASK_TITLE);

                    String mtitle = cursorc.getString(titleColumnIndex);
                    int recurringNumber = cursorc.getInt(recurringColumnIndex);
                    long dateLong = cursorc.getLong(dateColumnIndex);
                    String mReminderBefore = cursorc.getString(reminderBeforeIndex);
                    String mReminderUnit = cursorc.getString(reminderUnitIndex);

                    long todayDate = Calendar.getInstance().getTimeInMillis();

                    // Data to add to the history database
                    ContentValues cvHistory = new ContentValues();
                    String title = cursorc.getString(titleColumnIndex);
                    cvHistory.put(taskContract.TaskEntry.COLUMN_TASK_TITLE, title);
                    cvHistory.put(taskContract.TaskEntry.COLUMN_DESCRIPTION, "S");
                    cvHistory.put(taskContract.TaskEntry.COLUMN_TYPE_TASK, taskContract.TaskEntry.TYPE_FIXED);

                    cvHistory.put(taskContract.TaskEntry.COLUMN_LAST_COMPLETED, String.valueOf(todayDate));
                    cvHistory.put(taskContract.TaskEntry.COLUMN_STATUS, String.valueOf(0));
                    cvHistory.put(taskContract.TaskEntry.COLUMN_DATE, String.valueOf(todayDate));
                    cvHistory.put(taskContract.TaskEntry.COLUMN_DATETIME, String.valueOf(todayDate));


                    //if there isn't a recurring period selected for that task, then delete it
                    if (recurringNumber == 0) {
                        currentTaskUri = ContentUris.withAppendedId(taskContract.TaskEntry.CONTENT_URI, item_iD);

                        Calendar c = Calendar.getInstance();

                        getActivity().getContentResolver().insert(taskContract.TaskEntry.HISTORY_URI, cvHistory);
                        getActivity().getContentResolver().delete(currentTaskUri, null, null);
                        cancelAlarm();

                    }
                    //if there is a recurring period for that task, and the task isn't
                    // overdue (date>todayDate) yet, then update the tasks due date then add the number of recurring days for the
                    //new due date
                    else if (dateLong > todayDate) {
                        //because datelong is in milliseconds we times the recurring number (which is measured in days) by
                        //the number of days in a millisecond to convert the recurring number of days into milliseconds
                        dateLong += 86400000L * recurringNumber;
                        ContentValues cv = new ContentValues();
                        cv.put(taskContract.TaskEntry.COLUMN_TASK_TITLE, mtitle);
                        cv.put(taskContract.TaskEntry.COLUMN_DATE, dateLong);

                        //update task with new due date
                        getActivity().getContentResolver().update(currentTaskUri, cv, null, null);
                        getActivity().getContentResolver().insert(taskContract.TaskEntry.HISTORY_URI, cvHistory);


                        //set new reminder
                        setReminder(title, dateLong, mReminderUnit, mReminderBefore);

                    } else {
                        // if task is overdue, then find the next due date for the task that is bigger than today's date
                        while (todayDate > dateLong) {

                            dateLong += recurringNumber * 86400000L;
                            ContentValues cv = new ContentValues();
                            cv.put(taskContract.TaskEntry.COLUMN_TASK_TITLE, mtitle);
                            cv.put(taskContract.TaskEntry.COLUMN_DATE, dateLong);
                            getActivity().getContentResolver().update(currentTaskUri, cv, null, null);
                            getActivity().getContentResolver().insert(taskContract.TaskEntry.HISTORY_URI, cvHistory);
                        }
                        setReminder(title, dateLong, mReminderUnit, mReminderBefore);
                    }
                }

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                int fixedCount = preferences.getInt("fixedCount", 0);
                SharedPreferences.Editor editor = preferences.edit();
                fixedCount++;
                editor.putInt("fixedCount", fixedCount);
                editor.apply();

                long weeklygoalCount = preferences.getLong("weekTasks", 0);
                SharedPreferences.Editor editor2 = preferences.edit();
                weeklygoalCount++;
                editor2.putLong("weekTasks", weeklygoalCount);
                editor2.apply();

                //reset loader to show new changes
                getLoaderManager().restartLoader(TASKLOADER, null, FixedTaskTimeLine.this);
                //unselect item and reset UI after changes have been made
                timeLineListView.setItemChecked(lastClickedPostion, false);
                resetUI();

            }
        });



        /* DELETE BUTTON -
        * gets the URI for the selected listitem, and deletes it by calling cthe content resolver*/
        deleteButtonToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri currentTaskUri = ContentUris.withAppendedId(taskContract.TaskEntry.CONTENT_URI, item_iD);
                getActivity().getContentResolver().delete(currentTaskUri, null, null);
                resetUI();
                cancelAlarm();

            }
        });

        /*END OF TOOLBAR BUTTONS*/

        //when this fragment is first created, it will initialise the loader, which calls the OnCreate
        // loader method which in turn retrieves data from the database
        getLoaderManager().initLoader(TASKLOADER, null, this);

        setHasOptionsMenu(true);

        return rootView;

    }

    /**
     * Sets the reminder for fixed task
     *
     * @param reminderUnit   amount
     * @param reminderBefore ie: weekly, daily
     * @param taskTitle      of the task to set alarm for
     * @param date           of the task
     */
    private void setReminder(String taskTitle, long date, String reminderUnit, String reminderBefore) {

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(getContext().ALARM_SERVICE);

        cancelAlarm();

        if (!(reminderBefore.equals("") || reminderUnit.equals(""))) {

            Intent intent = new Intent(getContext(), AlertReceiverReminder.class);
            intent.putExtra("title", taskTitle);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int) item_iD, intent, 0);

            Calendar reminderCalander = Calendar.getInstance();
            reminderCalander.setTimeInMillis(date);

            switch (reminderBefore) {
                case ("Minutes before"):
                    reminderCalander.add(Calendar.MINUTE, -Integer.valueOf(reminderUnit));
                    break;
                case ("Hours before"):
                    reminderCalander.add(Calendar.HOUR_OF_DAY, -Integer.valueOf(reminderUnit));
                    break;
                case ("Days before"):
                    reminderCalander.add(Calendar.DAY_OF_YEAR, -Integer.valueOf(reminderUnit));
                    break;

                case ("Weeks before"):
                    reminderCalander.add(Calendar.WEEK_OF_YEAR, -Integer.valueOf(reminderUnit));
                    break;
            }
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, (reminderCalander.getTimeInMillis()), pendingIntent);
        }
    }

    private void cancelAlarm() {

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(getContext().ALARM_SERVICE);
        Intent i = new Intent(getContext(), AlertReceiverReminder.class);
        PendingIntent pi = PendingIntent.getBroadcast(getContext(), (int) item_iD, i,
                PendingIntent.FLAG_NO_CREATE); // find the old PendingIntent
        if (pi != null) {
            // Now cancel the alarm that matches the old PendingIntent
            alarmManager.cancel(pi);
        }

    }

    /*Checks user colour preferences and changes UI*/
    private void colorSwitch() {
        String colourSetting = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString("color_preference_key", "OCOLOUR");

        switch (colourSetting) {
            case ("DCOLOUR"):
                mFabFixedTask.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccentD)));
                break;

            case ("PCOLOUR"):
                mFabFixedTask.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccentP)));
                break;

            case ("TCOLOUR"):
                mFabFixedTask.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccentT)));
                break;
            default:
                mFabFixedTask.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        }
    }

    /**
     * Method for resetting UI. Sets title back to the name of the app, and hides
     * the tool bar and shows the floatingActionButton
     */
    public void resetUI() {
        if (label.equals("")) {
            getActivity().setTitle("Tasks");
        } else {
            getActivity().setTitle(label);
        }
        Animation fabUp = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
        Animation slideDownBar = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);
        Animation slideDownDone = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);
        Animation slideDownEdit = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);
        Animation slideDownDel = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);

        slideDownEdit.setStartOffset(80);
        slideDownDel.setStartOffset(160);
        slideDownBar.setStartOffset(180);
        fabUp.setStartOffset(210);

        doneButtonToolBar.startAnimation(slideDownDone);
        editButtonToolBar.startAnimation(slideDownEdit);
        editButtonToolBar.startAnimation(slideDownEdit);
        deleteButtonToolBar.startAnimation(slideDownDel);
        bottomBar.startAnimation(slideDownBar);
        mFabFixedTask.startAnimation(fabUp);

        bottomBar.setVisibility(View.GONE);
        doneButtonToolBar.setVisibility(View.GONE);
        editButtonToolBar.setVisibility(View.GONE);
        deleteButtonToolBar.setVisibility(View.GONE);
        mFabFixedTask.setVisibility(View.VISIBLE);
        toolBarShown = false;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.timeline_menu, menu);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        label = preferences.getString("label", "All");
        if (label.equals("All")) {
            menu.removeItem(menu.findItem(R.id.deleteLabel).getItemId());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //FOR DEBUGGING PURPOSES
            case R.id.help:
                HelpDialog bd = new HelpDialog();
                Bundle b = new Bundle();
                b.putString(HelpDialog.TITLE_KEY, "FixedTask help");
                b.putString(HelpDialog.MESSAGE_KEY, getResources().getString(R.string.fixedHelpMessage));
                bd.setArguments(b);
                bd.show(getFragmentManager(), "help fragment");

                return true;
            case R.id.deleteLabel:
                deleteLabel();
        }
        return super.onOptionsItemSelected(item);
    }

    /*Deletes label from database*/
    public void deleteLabel() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("label", "All");
        labelSQL = "";

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM " + taskContract.TaskEntry.LABEL_TABLE_NAME
                + " WHERE " + taskContract.TaskEntry.COLUMN_LABEL_NAME + "= '" + label + "'");

        db.close();
        resetUI();
        notifyMainAcitivty();
    }


    /*Notifies the navigation bar that a label has been deleted*/
    private void notifyMainAcitivty() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("labeldelete");
        intent.putExtra("labelToDelete", label);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }


    /**
     * This onCreateLoader method creates a new Cursor that contains the URI for the database and the columns wanted. It's passed
     * to the content resolver which uses the URI to determine which contentProvider we want. The contentProvider is then
     * responsible for interfacing with the database and returning a cursor (with the requested data from
     * our projection) back to the contentResolver, and finally back to the loadermanager. The loader manager then passes
     * this cursor object to the {@link #onLoadFinished(Loader, Cursor)} .
     *
     * @param id   the loader's ID
     * @param args any arguments you want to pass to the loader when creating it (NOT USED)
     * @return new cursorLoader with SQL projection and URI for the database we want to query. (this is passed to the cotent
     * resolver by the loaderManager)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                taskContract.TaskEntry._ID,
                taskContract.TaskEntry.COLUMN_TASK_TITLE,
                taskContract.TaskEntry.COLUMN_DESCRIPTION,
                taskContract.TaskEntry.COLUMN_TYPE_TASK,
                taskContract.TaskEntry.COLUMN_LAST_COMPLETED,
                taskContract.TaskEntry.COLUMN_DATE,
                taskContract.TaskEntry.COLUMN_TIME,
                taskContract.TaskEntry.COLUMN_HISTORY,
                taskContract.TaskEntry.COLUMN_STATUS,
                taskContract.TaskEntry.COLUMN_RECCURING_PERIOD};

        String WHERE = "task_type='0' AND status='1'" + labelSQL;

        // Perform a query on the tasks table, connects to contentResolver which matches the URI
        //with the appropriate content provider(Task Provider)
        return new CursorLoader(getActivity(),
                taskContract.TaskEntry.CONTENT_URI,
                projection,
                WHERE,
                null,
                "CAST(" + taskContract.TaskEntry.COLUMN_DATETIME + " AS DOUBLE)");
    }

    /**
     * Once the cursor loader set up in {@link #onCreateLoader(int, Bundle)} has been given the
     * returned data cursor, the {@link LoaderManager} calls this method with the data.
     * This method then gives the cursor adaptor the data to process
     *
     * @param loader the cursor loader
     * @param data   the returned cursor with the requested data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //once the cursor is returned the loadermanager pases it on to the loader finish method
        // which updates the cusoradapter with new data from database
        mTaskCursorAdaptor.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //when the data needs to be deleted
        mTaskCursorAdaptor.swapCursor(null);
    }
}
