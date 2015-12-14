package ca.udes.bonc.ift_project.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import ca.udes.bonc.ift_project.R;
import ca.udes.bonc.ift_project.communication.QueryEventService;
import ca.udes.bonc.ift_project.communication.QueryIntentService;
import ca.udes.bonc.ift_project.communication.RestApiResultReceiver;
import ca.udes.bonc.ift_project.utils.ConvertJson;


public class EventDetailFragment extends Fragment implements RestApiResultReceiver.Receiver {
    private String TAG = "MyEventFragment";
    private OnFragmentInteractionListener mListener;
    private ListView listView;
    private ProgressBar progressBar;
    private static RestApiResultReceiver mReceiver;

    private static final String ARG_PARAM1 = "idEvent";
    private String idEvent;


    public static EventDetailFragment newInstance(String param1) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public EventDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idEvent = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        this.listView = (ListView) view.findViewById(R.id.listView);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        QueryEventService.startActionFindEvent(container.getContext(),mReceiver,idEvent);
        return view;
    }

    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case QueryIntentService.STATUS_RUNNING:
                Log.i(TAG, "Runing status");
                this.progressBar.setVisibility(View.VISIBLE);
                break;
            case QueryIntentService.STATUS_FINISHED:
                String results = resultData.getString("results");
                this.progressBar.setVisibility(View.GONE);
                Log.i(TAG, "result = " + results);
                //updateEventList(ConvertJson.convert_event(results));
                break;
            case QueryIntentService.STATUS_ERROR:
                //todo handl error
                this.progressBar.setVisibility(View.GONE);
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.d(TAG, "error = " + error);
                break;
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
