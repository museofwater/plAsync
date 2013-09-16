package org.plasync.client.android.testapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import org.plasync.client.android.testapp.R;

/**
 * Created by ericwood on 8/12/13.
 */
public class ServerUrlDialogFragment extends DialogFragment {

    public interface ServerUrlDialogListener {
        void onFinishEditDialog(String inputText);
    }

    private EditText txtServerUrl;

    private ServerUrlDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.server_url_dialog_fragment, null);
        txtServerUrl = (EditText) view.findViewById(R.id.txtServerUrl);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onFinishEditDialog(txtServerUrl.getText().toString());
                        }
                    }
                })
                .setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ServerUrlDialogFragment.this.getDialog().cancel();
                    }
                });

        Dialog dialog = builder.create();

        dialog.setTitle(R.string.SERVER_URL_DIALOG_TITLE);

        // Show soft keyboard automatically
        txtServerUrl.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return dialog;
    }

    public void setListener(ServerUrlDialogListener listener) {
        this.listener = listener;
    }
}
