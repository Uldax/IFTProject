package ca.udes.bonc.ift_project.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
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

import ca.udes.bonc.ift_project.IFTApplication;
import ca.udes.bonc.ift_project.MainActivity;
import ca.udes.bonc.ift_project.R;
import ca.udes.bonc.ift_project.adapter.ParticipantAdapter;
import ca.udes.bonc.ift_project.communication.QueryEventService;
import ca.udes.bonc.ift_project.communication.QueryIntentService;
import ca.udes.bonc.ift_project.communication.RestApiResultReceiver;
import ca.udes.bonc.ift_project.dataObject.Categories;
import ca.udes.bonc.ift_project.dataObject.Event;
import ca.udes.bonc.ift_project.dataObject.Types;
import ca.udes.bonc.ift_project.utils.ConvertJson;


public class DetailFragment extends Fragment implements RestApiResultReceiver.Receiver{
    public static final String ARG_PARAM1 = "idEvent";
    public static final String ARG_PARAM2 = "param2";
    private String TAG = "DetailFragment";
    private OnFragmentInteractionListener mListener;
    private static RestApiResultReceiver mReceiver;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String idEvent;
    private String idUser;

    private View view;

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
    private Button btClose;
    private Button btDelete;
    private ParticipantAdapter adapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
        fragment.setRetainInstance(true);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idEvent = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            Log.d(TAG,"event id :" + idEvent);
        }

        mReceiver = new RestApiResultReceiver(new Handler());
        mReceiver.setReceiver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        this.idUser = ((IFTApplication)getActivity().getApplication()).getUserId();

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
        this.btClose = (Button) view.findViewById(R.id.btClose);
        this.btDelete = (Button) view.findViewById(R.id.btDelete);
        QueryEventService.startActionGetOneEvent(container.getContext(), mReceiver, idEvent);
        return view;
    }

    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case QueryIntentService.STATUS_RUNNING:
                Log.i(TAG, "Runing status");
                this.progressBar.setVisibility(View.VISIBLE);
                break;
            case QueryIntentService.STATUS_FINISHED:
                this.progressBar.setVisibility(View.GONE);
                String results = resultData.getString("results");
                Log.i(TAG, "result = " + results);
                switch (resultData.getString("action")){
                    case QueryIntentService.ACTION_GET_ONE:
                        updateInterface(ConvertJson.convert_event(results));
                        break;
                    case QueryIntentService.ACTION_ADD_EVENT_PARTICIPANT:
                        btParticip.setText("Your participation was successfully register !");
                        btParticip.setEnabled(false);
                        break;
                    case QueryIntentService.ACTION_CLOSE_EVENT:
                        new AlertDialog.Builder(getContext())
                                .setTitle("Close Event")
                                .setMessage("This event have been close")
                                .setCancelable(false)
                                .setPositiveButton("hum oky ?", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        btClose.setEnabled(false);
                                    }
                                })
                                .show();
                        break;
                    case QueryIntentService.ACTION_DELETE_EVENT:
                        getActivity().onBackPressed();
                        break;
                }
                break;
            case QueryIntentService.STATUS_ERROR:
                //todo handl error
                this.progressBar.setVisibility(View.GONE);
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.d(TAG, "error = " + error);
                break;
        }
    }

    private void updateInterface(final Event event) {
        this.listParticip = (ListView) view.findViewById(R.id.listParticip);
        this.title.setText(event.getTitle());
        this.date.setText(DateFormat.format("d MMM @ kk:mm", event.getDate()));
        Log.d(TAG, "positionName :" +event.getPositionName());
        this.place.setText(event.getPositionName());
        this.author.setText("by " + event.getAuthorName());
        if(event.getType().equals(Types.LOISIR)) this.modeCompet.setVisibility(View.GONE);
        if(idUser.equals(event.getAuthorID())) this.star.setVisibility(View.GONE);
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
        adapter = new ParticipantAdapter(getContext(),R.layout.adapter_participant, participants,idUser);
        listParticip.setAdapter(adapter);

        if( participants.containsKey(idUser) ){
            btParticip.setText("Your participation was already register !");
            btParticip.setEnabled(false);
        }else {
            this.btParticip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QueryEventService.startActionAddParticipant(getContext(), mReceiver, idEvent, idUser);
                }
            });
        }

        if( event.getAuthorID().equals(idUser) ){
            this.btParticip.setText("Managment Interface");
            btParticip.setEnabled(true);
            this.btParticip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new teamManagementFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(teamManagementFragment.ARG_PARAM1, idEvent);
                    bundle.putInt(teamManagementFragment.ARG_PARAM2, event.getListTeam().size());
                    fragment.setArguments(bundle);
                    ((MainActivity) getActivity()).switchFragment(fragment);
                }
            });
            if(event.getStatus().equals("created")){
                this.btClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Close Event")
                                .setMessage("Are you to close this event")
                                .setCancelable(true)
                                .setPositiveButton("Yeah sure !", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        QueryEventService.startActionCloseEvent(getContext(),mReceiver,event.getId());
                                    }
                                })
                                .setNegativeButton("AH nannnn", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                })
                                .show();
                    }
                });
            }else{
                btClose.setVisibility(View.GONE);
                title.setTextColor(getResources().getColor(R.color.eventClose));
            }
            this.btDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete Event")
                            .setMessage("Are you to delete this event")
                            .setCancelable(true)
                            .setPositiveButton("Yeah sure !", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    QueryEventService.startActionDeleteEvent(getContext(), mReceiver, event.getId());
                                }
                            })
                            .setNegativeButton("AH nannnn", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            })
                            .show();
                }
            });
        }else{
            btClose.setVisibility(View.GONE);
            btDelete.setVisibility(View.GONE);
        }

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
