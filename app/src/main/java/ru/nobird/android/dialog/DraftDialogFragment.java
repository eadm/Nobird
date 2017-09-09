package ru.nobird.android.dialog;

import ru.nobird.android.R;
import ru.nobird.android.data.database.DBMgr;

/**
 * Dialog that asks to save tweet draft
 */
public class DraftDialogFragment extends AbsConfirmDialogFragment {
    public final static String DATA_KEY = "data";

    @Override
    protected int getTitle() { return R.string.draft_dialog_title; }

    @Override
    protected int getMessage() { return R.string.draft_dialog_message; }

    @Override
    protected void onSuccess() {
        // save the tweet
//        Log.d("DraftDialogFragment", getArguments().getString(DATA_KEY));
        DBMgr.getInstance().saveDraft(getArguments().getString(DATA_KEY));
    }
}
