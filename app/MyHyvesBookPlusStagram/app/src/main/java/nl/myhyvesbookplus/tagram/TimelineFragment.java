package nl.myhyvesbookplus.tagram;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;



import nl.myhyvesbookplus.tagram.controller.DownloadClass;


public class TimelineFragment extends Fragment {

    private ListView listView;
    private DownloadClass downloadClass;

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        listView = (ListView) view.findViewById(R.id.list);
        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) view.findViewById(R.id.swipe);

        swipeView.setEnabled(false);
        downloadClass = new DownloadClass(getActivity());
        downloadClass.getPostsFromServer();

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadClass = new DownloadClass(getActivity());
                downloadClass.getPostsFromServer();
                swipeView.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    swipeView.setEnabled(true);
                else
                    swipeView.setEnabled(false);
            }
        });
        return view;
    }

    public void startList() {
        TimeLineAdapter adapter = new TimeLineAdapter(getActivity(), downloadClass.getmList());
        listView.setAdapter(adapter);
    }
}
