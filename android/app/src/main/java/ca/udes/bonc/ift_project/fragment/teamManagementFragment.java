package ca.udes.bonc.ift_project.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import ca.udes.bonc.ift_project.R;
import ca.udes.bonc.ift_project.communication.QueryEventService;
import ca.udes.bonc.ift_project.communication.RestApiResultReceiver;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link teamManagementFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link teamManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class teamManagementFragment extends Fragment implements RestApiResultReceiver.Receiver {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static RestApiResultReceiver mReceiver;
    private String eventID;
    private int nbTeam;
    private Button addTeamButton = null;
    private Button shuffleParticipantsButton = null;
    private EditText teamNameEditText = null;
    private ListView teamListView = null;

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

    }

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment teamManagementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static teamManagementFragment newInstance(String param1, String param2) {
        teamManagementFragment fragment = new teamManagementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public teamManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventID = getArguments().getString(ARG_PARAM1);
            nbTeam = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_team_management, container, false);

        //Gettings Views
        addTeamButton = (Button) view.findViewById(R.id.addTeamButton);
        shuffleParticipantsButton = (Button) view.findViewById(R.id.shuffleParticipantsButton);
        teamNameEditText= (EditText) view.findViewById(R.id.teamNameEditText);
        teamListView = (ListView) view.findViewById(R.id.teamListView);

        //Settings Button listeners
        addTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryEventService.startActionCreateTeam(v.getContext(), mReceiver, eventID, teamNameEditText.getText().toString() );
                teamNameEditText.getText().clear();
            }
        });
        shuffleParticipantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryEventService.startActionShuffleParticipants(v.getContext(), mReceiver, eventID);
            }
        });
        return view;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
