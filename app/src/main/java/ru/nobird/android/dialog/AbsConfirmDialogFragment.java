package ru.nobird.android.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Abs dialog fragment to create yes/no dialogs
 */
public abstract class AbsConfirmDialogFragment extends DialogFragment implements Dialog.OnClickListener{
    protected abstract int getTitle();
    protected abstract int getMessage();

    protected abstract void onSuccess();

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(getTitle());
        alertDialogBuilder.setMessage(getMessage());
        alertDialogBuilder.setPositiveButton(android.R.string.yes, this);
        alertDialogBuilder.setNegativeButton(android.R.string.no, this);

        return alertDialogBuilder.create();
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        switch (which) {
            case -1:
                onSuccess();
            break;
            case -2:
            break;
        }
    }
}
