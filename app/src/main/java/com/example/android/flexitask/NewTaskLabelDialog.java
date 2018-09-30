package com.example.android.flexitask;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Ryan McGoff (4086944), Jerry Kumar (3821971), Jaydin Mcmullan (9702973)
 * <p>
 * Creates a new task dialog for the main activity to use.
 * Sets up an interface + listener to alert main activity of when a new task label
 * has been selected. The main activity then creates it by storing a copy in the database and adding
 * it to the nav draw.
 */
public class NewTaskLabelDialog extends AppCompatDialogFragment {

    private EditText editTextLabelName;
    private NewTaskLabelDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.new_label_dialog, null);

        builder.setView(view).setTitle("Create new task label")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newLabelName = editTextLabelName.getText().toString();
                        listener.applyNewTaskLabelName(newLabelName);

                    }
                });

        editTextLabelName = view.findViewById(R.id.edit_label_name);


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (NewTaskLabelDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "" +
                    "Forgot to implement dialog listener");
        }
    }

    public interface NewTaskLabelDialogListener {
        void applyNewTaskLabelName(String newTextLabel);
    }
}
