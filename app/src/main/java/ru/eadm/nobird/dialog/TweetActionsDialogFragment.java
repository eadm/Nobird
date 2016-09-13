package ru.eadm.nobird.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.fragment.FragmentMgr;

public class TweetActionsDialogFragment extends DialogFragment {
    private DialogInterface.OnClickListener listener;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        ArrayAdapter<String> adapter;
        final String[] data = getArguments().getStringArray("data");
        if (data != null) {
            adapter = new ArrayAdapter<>(getActivity(), R.layout.dialog_fragment_list_item, data);
        } else {
            adapter = new ArrayAdapter<>(getContext(), R.layout.dialog_fragment_list_item);
        }
        builder.setAdapter(adapter, listener);

        return builder.create();
    }

    public void setListener(final DialogInterface.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDestroyView() { // work around to keep dialog with retain instance state = true
        final Dialog dialog = getDialog();
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    /**
     * Shows export dialog box for tweetElement
     * @param element - target TweetElement object
     */
    public static void showExportDialog(final TweetElement element) {
        final TweetActionsDialogFragment dialogFragment = new TweetActionsDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putStringArray("data", FragmentMgr.getInstance().getContext().getResources().getStringArray(R.array.export_list_dialog_fragment_values));
        dialogFragment.setArguments(arguments);
        dialogFragment.setListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                String text = "";
                switch (which) {
                    case 0:
                        text = element.text.getText().toString();
                        break;
                    case 1:
                        text = "http://twitter.com/#!/"+
                                element.user.username +
                                "/status/" +
                                element.getID();
                        break;
                }
                FragmentMgr.getInstance().copyToClipboard(text);
                dialogFragment.dismiss();
            }
        });
        FragmentMgr.getInstance().showDialog(dialogFragment);
    }
}
