package ru.eadm.nobird.fragment.task;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import ru.eadm.nobird.data.types.TweetElement;

public abstract class AbsTweetRecycleViewRefreshTask extends AsyncTask<Void, Void, ArrayList<TweetElement>> {
    private WeakReference<AbsTweetRecycleViewFragment> fragment;
    private boolean completed;

    public AbsTweetRecycleViewRefreshTask(final AbsTweetRecycleViewFragment fragment) {
        this.fragment = new WeakReference<>(fragment);
        completed = false;
    }

    public void attachFragment(final AbsTweetRecycleViewFragment fragment) {
        this.fragment = new WeakReference<>(fragment);
        if (!completed) {
            fragment.setRefreshing(true);
        }
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (fragment.get() != null) {
            fragment.get().setRefreshing(true);
        }
    }

    protected abstract ArrayList<TweetElement> doInBackground(Void... params);

    @Override
    protected void onPostExecute(ArrayList<TweetElement> data) {
        super.onPostExecute(data);
        if (fragment.get() != null) {
            for (final TweetElement s : data) {
                fragment.get().adapter.add(s);
                fragment.get().adapter.notifyItemInserted(fragment.get().adapter.getItemCount() - 1);
            }
            fragment.get().setRefreshing(false);
        }

        completed = true;
    }
}
