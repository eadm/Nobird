package ru.eadm.nobird.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.fragment.FragmentMgr;

public class ListDialogFragment extends DialogFragment {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private AdapterView.OnItemClickListener listener;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View page = inflater.inflate(R.layout.dialog_fragment_list, container, false);
        listView = (ListView) page.findViewById(R.id.dialog_fragment_list);
        if (adapter != null) {
            listView.setAdapter(adapter);
        }

        if (listener != null) {
            listView.setOnItemClickListener(listener);
        }
        return page;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        final String[] data = getArguments().getStringArray("data");
        if (data != null) {
            adapter = new ArrayAdapter<>(getActivity(), R.layout.dialog_fragment_list_item, data);
        } else {
            adapter = new ArrayAdapter<>(getContext(), R.layout.dialog_fragment_list_item);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void setListener(final AdapterView.OnItemClickListener listener) {
        this.listener = listener;
        if (listView != null) listView.setOnItemClickListener(listener);
    }

    /**
     * Shows export dialog box for tweetElement
     * @param element - target TweetElement object
     */
    public static void showExportDialog(final TweetElement element) {
        final ListDialogFragment dialogFragment = new ListDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putStringArray("data", FragmentMgr.getInstance().getContext().getResources().getStringArray(R.array.export_list_dialog_fragment_values));
        dialogFragment.setArguments(arguments);
        dialogFragment.setListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                String text = "";
                switch (position) {
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
