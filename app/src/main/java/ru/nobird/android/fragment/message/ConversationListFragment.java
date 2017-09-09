package ru.nobird.android.fragment.message;

import android.support.v4.app.Fragment;

import ru.nobird.android.R;
import ru.nobird.android.data.PageableArrayList;
import ru.nobird.android.data.database.DBMgr;
import ru.nobird.android.data.types.ConversationElement;
import ru.nobird.android.fragment.implementation.FragmentMgr;
import ru.nobird.android.fragment.implementation.adapter.ConversationRecycleViewAdapter;
import ru.nobird.android.fragment.implementation.adapter.PageableRecyclerViewAdapter;
import ru.nobird.android.fragment.implementation.task.AbsRecycleViewFragment;
import ru.nobird.android.fragment.implementation.task.AbsRecycleViewRefreshTask;


/**
 * Fragment to display senders and last message
 */

public final class ConversationListFragment extends AbsRecycleViewFragment<ConversationElement> {
    @Override
    protected AbsRecycleViewRefreshTask<ConversationElement> createTask(final AbsRecycleViewRefreshTask.Position position) {
        return new ConversationsLoadTask(this, position);
    }

    @Override
    protected PageableRecyclerViewAdapter<ConversationElement, ?> createAdapter() {
        return new ConversationRecycleViewAdapter();
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.drawer_messages);
    }

    public static void show() {
        final Fragment fragment = new ConversationListFragment();
        FragmentMgr.getInstance().replaceFragment(0, fragment, true);
    }

    private final class ConversationsLoadTask extends AbsRecycleViewRefreshTask<ConversationElement> {
        private ConversationsLoadTask(final AbsRecycleViewFragment<ConversationElement> fragment, final Position position) {
            super(fragment, position);
        }

        @Override
        protected PageableArrayList<ConversationElement> doInBackground(final Long... params) {
            final PageableArrayList<ConversationElement> elements = new PageableArrayList<>(DBMgr.getInstance().getConversations());
            if (elements.size() > 0) {
                elements.setCursors(true, true, elements.get(elements.size() - 1).getID(), elements.get(0).getID());
            }
            return elements;
        }
    }

    private final class ConversationsApiLoadTask extends AbsRecycleViewRefreshTask<ConversationElement> {
        private ConversationsApiLoadTask(final AbsRecycleViewFragment<ConversationElement> fragment, final Position position) {
            super(fragment, position);
        }

        @Override
        protected PageableArrayList<ConversationElement> doInBackground(final Long... params) {
//            try {
//                final PageableArrayList<ConversationElement> elements = new PageableArrayList<>(DirectMessageResources.getDirectMessages(params[0], params[1]));
//                if (elements.size() > 0) {
//                    elements.setCursors(true, true, elements.get(elements.size() - 1).getID(), elements.get(0).getID());
//                }
//                return elements;
//            } catch (final TwitterException e) {
                return null;
//            }
        }
    }
}
