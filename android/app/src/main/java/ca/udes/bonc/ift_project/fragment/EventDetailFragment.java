package ca.udes.bonc.ift_project.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import ca.udes.bonc.ift_project.IFTApplication;
import ca.udes.bonc.ift_project.R;
import ca.udes.bonc.ift_project.communication.QueryEventService;
import ca.udes.bonc.ift_project.communication.QueryIntentService;
import ca.udes.bonc.ift_project.communication.RestApiResultReceiver;
import ca.udes.bonc.ift_project.dataObject.Categories;
import ca.udes.bonc.ift_project.dataObject.Event;
import ca.udes.bonc.ift_project.dataObject.Types;
import ca.udes.bonc.ift_project.utils.ConvertJson;


public class EventDetailFragment extends Fragment implements RestApiResultReceiver.Receiver {
    private String TAG = "MyEventFragment";
    private OnFragmentInteractionListener mListener;
    private static RestApiResultReceiver mReceiver;

    private static final String ARG_PARAM1 = "idEvent";
    private static final String ARG_PARAM2 = "param2";
    private String idEvent;

    private TextView title;
    private TextView date;
    private TextView place;
    private TextView author;
    private TextView modeCompet;
    private TextView star;
    private ImageView img_cat;
    private ListView listParticip;
    private ProgressBar progressBar;
    private Button btParticip;



    public static EventDetailFragment newInstance(String param1, String param2) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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

        mReceiver = new RestApiResultReceiver(new Handler());
        mReceiver.setReceiver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        this.listParticip = (ListView) view.findViewById(R.id.listParticip);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        this.title = (TextView) view.findViewById(R.id.title);
        this.date = (TextView) view.findViewById(R.id.date);
        this.place = (TextView) view.findViewById(R.id.place);
        this.author = (TextView) view.findViewById(R.id.author);
        this.modeCompet = (TextView) view.findViewById(R.id.modeCompet);
        this.star = (TextView) view.findViewById(R.id.star);
        this.img_cat = (ImageView) view.findViewById(R.id.img_cat);
        this.btParticip = (Button) view.findViewById(R.id.btParticip);
        QueryEventService.startActionFindEvent(container.getContext(), mReceiver, idEvent);
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
                updateInterface(ConvertJson.convert_event(results));
                break;
            case QueryIntentService.STATUS_ERROR:
                //todo handl error
                this.progressBar.setVisibility(View.GONE);
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.d(TAG, "error = " + error);
                break;
        }
    }

    private void updateInterface(Event event) {
        this.title.setText(event.getTitle());
        this.date.setText(DateFormat.format("d MMM @ kk:mm", event.getDate()));
        this.place.setText(event.getLatitude() + " - " + event.getLongitude());
        this.author.setText("by " + event.getAuthorName());
        if(event.getType().equals(Types.LOISIR)) this.modeCompet.setVisibility(View.GONE);
        if((((IFTApplication)getActivity().getApplication()).getUserId()).equals(event.getAuthorID())) this.star.setVisibility(View.GONE);
        switch (event.getCategory()){
            case Categories.FOOTBALL :
                this.img_cat.setImageResource(R.drawable.football);
                break;
            case Categories.HOCKEY :
                this.img_cat.setImageResource(R.drawable.hockey);
                break;
            case Categories.SOCIAL :
                this.img_cat.setImageResource(R.drawable.social);
                break;
            case Categories.RANDO :
                this.img_cat.setImageResource(R.drawable.run);
                break;
            case Categories.VIDEOGAME :
                this.img_cat.setImageResource(R.drawable.videogame);
                break;
        }
        HashMap<String,String> participants = event.getListParticipant();
        if(!participants.containsKey((((IFTApplication)getActivity().getApplication()).getUserId())))
            this.btParticip.setVisibility(View.GONE);
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
