package org.ktachibana.cloudemoji.fragments;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.ktachibana.cloudemoji.BaseFragment;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.SourceListViewAdapter;
import org.ktachibana.cloudemoji.events.EntryCopiedAndAddedToHistoryEvent;
import org.ktachibana.cloudemoji.models.Entry;
import org.ktachibana.cloudemoji.models.Source;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;

public class SourceFragment extends BaseFragment {
    private static final String ARG_SOURCE = "source";
    @InjectView(R.id.sourceListView)
    PinnedHeaderListView mSourceListView;
    private Source mSource;
    private SourceListViewAdapter mAdapter;

    public SourceFragment() {
        // Required empty public constructor
    }

    public static SourceFragment newInstance(Source source) {
        SourceFragment fragment = new SourceFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SOURCE, source);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSource = getArguments().getParcelable(ARG_SOURCE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_source, container, false);
        ButterKnife.inject(this, rootView);

        // Setup contents
        mAdapter = new SourceListViewAdapter(getActivity(), mSource);
        mSourceListView.setAdapter(mAdapter);
        mSourceListView.setOnItemClickListener(new PinnedHeaderListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
                Entry entry = (Entry) mAdapter.getItem(section, position);
                EventBus.getDefault().post(new EntryCopiedAndAddedToHistoryEvent(entry));
            }

            @Override
            public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {

            }
        });
        setFastScrollAlwaysVisible(mSourceListView, true);

        return rootView;
    }

    private void setFastScrollAlwaysVisible(ListView listView, boolean fastScrollAlwaysVisible) {
        if (fastScrollAlwaysVisible) {
            int SDK = Build.VERSION.SDK_INT;
            // Above 3.0
            if (SDK >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                listView.setFastScrollAlwaysVisible(true);
            }
        }
    }

    public void setSelection(int index) {
        mSourceListView.setSelection(mAdapter.getPositionForSection(index));
    }
}
