package nl.myhyvesbookplus.tagram;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import nl.myhyvesbookplus.tagram.controller.DownloadClass;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimelineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimelineFragment extends Fragment {
    final private static String TAG = "TimelineFragment";

    private ListView listView;
    private DownloadClass downloadClass;

    public TimelineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment TimelineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimelineFragment newInstance() {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        listView = (ListView) view.findViewById(R.id.listview);
        downloadClass = new DownloadClass(getActivity());
        downloadClass.getPostsFromServer();

        // Inflate the layout for this fragment
        return view;
    }

    public void startList() {
        TimeLineAdapter adapter = new TimeLineAdapter(getActivity(), downloadClass.getmList());
        listView.setAdapter(adapter);
    }
}
