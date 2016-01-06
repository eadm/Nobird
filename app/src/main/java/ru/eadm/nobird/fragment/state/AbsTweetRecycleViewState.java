package ru.eadm.nobird.fragment.state;

import ru.eadm.nobird.data.PageableArrayList;
import ru.eadm.nobird.data.types.TweetElement;
import ru.eadm.nobird.fragment.task.AbsTweetRecycleViewRefreshTask;

public abstract class AbsTweetRecycleViewState {
    private PageableArrayList<TweetElement> data;
    private AbsTweetRecycleViewRefreshTask task;

    public PageableArrayList<TweetElement> getData() { return data; }
    public void setData(PageableArrayList<TweetElement> data) { this.data = data; }

    public AbsTweetRecycleViewRefreshTask getTask() { return task; }
    public void setTask(AbsTweetRecycleViewRefreshTask task) { this.task = task; }
}
