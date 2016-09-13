package ru.eadm.nobird.fragment.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.PreferenceMgr;
import ru.eadm.nobird.data.types.AccountElement;
import ru.eadm.nobird.databinding.DialogFragmentAccountItemBinding;

/**
 * Old version of adapter for
 */
public class AccountAdapter extends BaseAdapter {
    private final List<AccountElement> accounts;
    private final long currentAccount;

    public AccountAdapter() {
        accounts = new ArrayList<>();
        currentAccount = PreferenceMgr.getInstance().getCurrentAccountID();
    }

    public void addAll(final List<AccountElement> arg) {
        accounts.addAll(arg);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public Object getItem(int position) {
        return accounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return accounts.get(position).getID();
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        DialogFragmentAccountItemBinding binding;
        if (convertView == null) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            binding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_account_item, parent, false);
            binding.getRoot().setTag(binding);
        } else {
            binding = (DialogFragmentAccountItemBinding) convertView.getTag();
        }

        binding.setAccount(accounts.get(position));
        binding.accountElementActive.setVisibility(accounts.get(position).getID() == currentAccount ? View.VISIBLE : View.GONE);

        return binding.getRoot();
    }
}
