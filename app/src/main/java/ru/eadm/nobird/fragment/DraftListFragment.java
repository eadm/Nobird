package ru.eadm.nobird.fragment;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.List;

import ru.eadm.nobird.R;
import ru.eadm.nobird.data.database.DBHelper;
import ru.eadm.nobird.data.database.DBMgr;
import ru.eadm.nobird.data.types.DraftElement;
import ru.eadm.nobird.design.DividerItemDecoration;
import ru.eadm.nobird.fragment.adapter.StringRecycleViewAdapter;

/**
 * Fragment that displays drafts
 */
public class DraftListFragment extends Fragment {
    private StringRecycleViewAdapter adapter;
    private DraftListTask draftListTask;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View page = inflater.inflate(R.layout.fragment_list, container, false);
        page.findViewById(R.id.fragment_list_swipe_refresh_layout).setEnabled(false); // disable refresh

        final Toolbar toolbar = (Toolbar) page.findViewById(R.id.fragment_list_toolbar);
        toolbar.setTitle(getString(R.string.drafts));

        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final RecyclerView recyclerView = (RecyclerView) page.findViewById(R.id.fragment_list_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(
                getContext(), R.drawable.list_divider, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);

        final ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, final int swipeDir) {
                final int pos = viewHolder.getAdapterPosition();
                DBMgr.getInstance().removeElementFromTableByID(DBHelper.TABLE_DRAFTS, "id", adapter.get(pos).getID());
                adapter.remove(pos);
            }

            @Override
            public void onChildDraw(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    final float width = (float) viewHolder.itemView.getWidth();
                    final float alpha = 1.0f - Math.abs(dX) / width;
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
                            actionState, isCurrentlyActive);
                }
            }
        };

        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return page;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        adapter = new StringRecycleViewAdapter(new StringRecycleViewAdapter.OnDataClickListener<String>() {
            @Override
            public void onClick(final String data) {
                ComposeFragment.open(0, data);
            }
        });
        draftListTask = new DraftListTask(this);
        draftListTask.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter = null;
        if (draftListTask != null) {
            draftListTask.cancel(true);
            draftListTask = null;
        }
    }

    public static void show() {
        final DraftListFragment fragment = new DraftListFragment();
        FragmentMgr.getInstance().replaceFragment(0, fragment, true);
    }

    private final class DraftListTask extends AsyncTask<Void, Void, List<DraftElement>> {
        private final WeakReference<DraftListFragment> fragmentWeakReference;

        public DraftListTask(final DraftListFragment fragment) {
            this.fragmentWeakReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<DraftElement> doInBackground(final Void... params) {
            return DBMgr.getInstance().getDrafts();
        }

        @Override
        protected void onPostExecute(final List<DraftElement> drafts) {
            final DraftListFragment fragment = fragmentWeakReference.get();
            if (fragment != null) {
                fragment.adapter.addAll(drafts);
            }
        }
    }
}
