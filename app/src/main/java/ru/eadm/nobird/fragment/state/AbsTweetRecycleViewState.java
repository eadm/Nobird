package ru.eadm.nobird.fragment.state;

import java.util.ArrayList;

import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewRefreshTask;

/**
 * Created by ruslandavletshin on 10/12/15.
 */
public abstract class AbsTweetRecycleViewState {
    private ArrayList<TweetElement> data;
    private AbsTweetRecycleViewRefreshTask task;

    public ArrayList<TweetElement> getData() { return data; }
    public void setData(ArrayList<TweetElement> data) { this.data = data; }

    public AbsTweetRecycleViewRefreshTask getTask() { return task; }
    public void setTask(AbsTweetRecycleViewRefreshTask task) { this.task = task; }
}
