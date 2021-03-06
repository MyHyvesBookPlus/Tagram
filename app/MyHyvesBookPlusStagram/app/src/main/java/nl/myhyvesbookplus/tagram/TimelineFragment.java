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
import android.widget.ProgressBar;
import android.widget.Toast;

import nl.myhyvesbookplus.tagram.controller.DownloadClass;


public class TimelineFragment extends Fragment {

    /* Some protected and private inits */
    private ListView listView;
    private DownloadClass downloadClass;
    private ProgressBar progressBar;

    /* Required empty public constructor */
    public TimelineFragment() {}

    /**
     * Overridden onCreateView method which creates the ListView and contains a possible refresh
     * functionality (swipe down page for result).
     *
     * https://www.survivingwithandroid.com/2014/05/android-swiperefreshlayout-tutorial-2.html
     * Above reference was largely copied from.
     * @param inflater The inflater used for the fragment.
     * @param container The container which holds this fragment.
     * @param savedInstanceState The state which was provided by onCreate.
     * @return  the timeLineInflater View which is required for the ListView to be updated.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View timeLineInflater = inflater.inflate(R.layout.fragment_timeline, container, false);
        listView = timeLineInflater.findViewById(R.id.list);
        final SwipeRefreshLayout swipeView = timeLineInflater.findViewById(R.id.swipe);

        progressBar = timeLineInflater.findViewById(R.id.progressbar_timeline);
        progressBar.setVisibility(View.VISIBLE);

        swipeView.setEnabled(false);
        downloadClass = new DownloadClass(getActivity());
        downloadClass.getPostsFromServer();

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadClass.getPostsFromServer();
                Toast.makeText(getActivity(), R.string.refreshing,
                        Toast.LENGTH_LONG).show();
                swipeView.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                swipeView.setEnabled(firstVisibleItem == 0);
            }
        });
        return timeLineInflater;
    }

    /**
     * Start display of the list; uses an adapter and listener in the main activity.
     */
    public void startList() {
        TimeLineAdapter adapter = new TimeLineAdapter(getActivity(), downloadClass.getmList());
        listView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }
}
