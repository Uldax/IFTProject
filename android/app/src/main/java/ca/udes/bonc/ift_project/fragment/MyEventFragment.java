package ca.udes.bonc.ift_project.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import ca.udes.bonc.ift_project.IFTApplication;
import ca.udes.bonc.ift_project.MainActivity;
import ca.udes.bonc.ift_project.R;
import ca.udes.bonc.ift_project.adapter.EventAdapter;
import ca.udes.bonc.ift_project.communication.QueryEventService;
import ca.udes.bonc.ift_project.communication.QueryIntentService;
import ca.udes.bonc.ift_project.communication.RestApiResultReceiver;
import ca.udes.bonc.ift_project.dataObject.Event;
import ca.udes.bonc.ift_project.utils.ConvertJson;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyEventFragment extends Fragment implements RestApiResultReceiver.Receiver {
    private String TAG = "MyEventFragment";
    private OnFragmentInteractionListener mListener;
    private ListView listView;
    private ProgressBar progressBar;
    private List<Event> data;
    private static RestApiResultReceiver mReceiver;

    public static MyEventFragment newInstance(String param1, String param2) {
        MyEventFragment fragment = new MyEventFragment();
        return fragment;
    }

    public MyEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceiver = new RestApiResultReceiver(new Handler());
        mReceiver.setReceiver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myevent, container, false);
        this.listView = (ListView) view.findViewById(R.id.listView);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String idEvent = data.get(i).getId();
                Fragment fragment = new NewEventFragment();
                ((MainActivity) getActivity()).switchFragment(fragment);

            }
        });
        this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        QueryEventService.startActionFindForUser(this.getContext(), mReceiver, ((IFTApplication)getActivity().getApplication()).getUserId());
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
                updateEventList(ConvertJson.convert_list_event(results));
                break;
            case QueryIntentService.STATUS_ERROR:
                //todo handl error
                this.progressBar.setVisibility(View.GONE);
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.d(TAG, "error = " + error);
                break;
        }
    }

    private void updateEventList(List<Event> listEvent) {
        if((listEvent==null)||(listEvent.size()==0)){
            Toast.makeText(getContext(), "Sorry we don't find your event", Toast.LENGTH_LONG).show();
            return;
        }
        this.data = listEvent;
        EventAdapter adapter = new EventAdapter(getContext(),R.layout.adapter_event, listEvent,((IFTApplication)getActivity().getApplication()).getUserId());
        listView.setAdapter(adapter);
        return;
    }

    // TODO: Rename method, update argument and hook method into UI event
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
