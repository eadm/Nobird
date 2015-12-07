package ru.eadm.nobird.fragment;

import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.ImageMgr;
import ru.eadm.nobird.data.PreferenceMgr;
import ru.eadm.nobird.data.database.DBMgr;
import ru.eadm.nobird.data.types.AccountElement;
import ru.eadm.nobird.notification.NotificationMgr;

/**
 * Created by ruslandavletshin on 07/12/15.
 */
public class Home extends Fragment {
    public static final String TAG = "home_fragment";
    private TextView nameTextView, usernameTextView;
    private AccountElement account;
    private DrawerLayout page;
    private ImageView userImageView;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        page = (DrawerLayout) inflater.inflate(R.layout.fragment_home, container, false);

        nameTextView = (TextView) page.findViewById(R.id.drawer_info_name);
        usernameTextView = (TextView) page.findViewById(R.id.drawer_info_username);
        userImageView = (ImageView) page.findViewById(R.id.drawer_info_image);

        if (savedInstanceState != null) {
            account = savedInstanceState.getParcelable("account");
            updateAccountInfo();
        } else {
            new AccountInitTask().execute();
        }

        return page;
    }

    private void updateAccountInfo() {
        if (account != null) {
            nameTextView.setText(account.name);
            usernameTextView.setText("@" + account.username);
            ImageMgr.getInstance().displayImage(account.image, userImageView);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("account", account);
    }

    private final class AccountInitTask extends AsyncTask<Void, Void, AccountElement> {
        @Override
        protected AccountElement doInBackground(Void... params) {
            return DBMgr.getInstance().getAccount(PreferenceMgr.getInstance().getLong(PreferenceMgr.CURRENT_ACCOUNT_ID));
        }

        @Override
        protected void onPostExecute(final AccountElement accountElement) {
            if (accountElement != null) {
                account = accountElement;
                updateAccountInfo();
            } else {
                NotificationMgr.getInstance().showSnackbar(R.string.error_account_init, page);
            }
        }
    }
}
