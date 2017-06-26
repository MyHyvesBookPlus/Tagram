package nl.myhyvesbookplus.tagram;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        listView = (ListView) view.findViewById(R.id.listview);

        downloadClass = new DownloadClass(getActivity());
        downloadClass.getPostsFromServer();

        return view;
    }

    public void startList() {
        TimeLineAdapter adapter = new TimeLineAdapter(getActivity(), downloadClass.getmList());
        listView.setAdapter(adapter);
    }
}
