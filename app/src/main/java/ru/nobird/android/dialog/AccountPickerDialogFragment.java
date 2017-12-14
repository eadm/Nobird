package ru.nobird.android.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.lang.ref.WeakReference;
import java.util.List;

import ru.nobird.android.R;
import ru.nobird.android.data.SharedPreferenceHelper;
import ru.nobird.android.data.database.DBMgr;
import ru.nobird.android.data.types.AccountElement;
import ru.nobird.android.fragment.LoginFragment;
import ru.nobird.android.fragment.implementation.FragmentMgr;
import ru.nobird.android.fragment.Home;
import ru.nobird.android.fragment.implementation.adapter.AccountAdapter;


/**
 * Dialog to pick accounts
 */
public class AccountPickerDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
//    private final static String TAG = "AccountPickerDialogFrg";
    private AccountAdapter adapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(R.string.manage_accounts);
        alertDialogBuilder.setAdapter(adapter, this);

        alertDialogBuilder.setPositiveButton(R.string.dialog_fragment_account_add, this);

        return alertDialogBuilder.create();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        adapter = new AccountAdapter();
        new AccountsLoadTask(this).execute();
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

    public static void show() {
        FragmentMgr.getInstance().showDialog(new AccountPickerDialogFragment());
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        if (which >= 0) {
            SharedPreferenceHelper.getInstance().setCurrentAccountID(adapter.getItemId(which));
            FragmentMgr.getInstance().replaceFragment(0, new Home(), false);
//            Log.d(TAG, ((AccountElement)adapter.getItem(which)).username);
        } else if (which == -1) {
            FragmentMgr.getInstance().replaceFragment(0, new LoginFragment(), true);
        }
    }

    private final class AccountsLoadTask extends AsyncTask<Void, Void, List<AccountElement>> {
        private final WeakReference<AccountPickerDialogFragment> dialogFragmentWeakReference;

        public AccountsLoadTask(final AccountPickerDialogFragment dialogFragment) {
            this.dialogFragmentWeakReference = new WeakReference<>(dialogFragment);
        }

        @Override
        protected List<AccountElement> doInBackground(final Void... params) {
            return DBMgr.getInstance().getAccounts();
        }

        @Override
        protected void onPostExecute(final List<AccountElement> accountElements) {
            final AccountPickerDialogFragment dialog = dialogFragmentWeakReference.get();
            if (dialog != null && dialog.adapter != null) {
                dialog.adapter.addAll(accountElements);
            }
        }
    }
}
