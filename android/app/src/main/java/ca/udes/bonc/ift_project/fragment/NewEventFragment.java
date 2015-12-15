package ca.udes.bonc.ift_project.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;




import java.util.Calendar;

import ca.udes.bonc.ift_project.R;
import ca.udes.bonc.ift_project.adapter.PlacesAutoCompleteAdapter;
import ca.udes.bonc.ift_project.communication.QueryEventService;
import ca.udes.bonc.ift_project.communication.QueryIntentService;
import ca.udes.bonc.ift_project.communication.RestApiResultReceiver;
import ca.udes.bonc.ift_project.dataObject.Categories;
import ca.udes.bonc.ift_project.dataObject.Location;
import ca.udes.bonc.ift_project.dataObject.Place;
import ca.udes.bonc.ift_project.dataObject.Types;
import ca.udes.bonc.ift_project.utils.GooglePlaces;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewEventFragment extends Fragment implements RestApiResultReceiver.Receiver{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "NewEventFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private int currentYear;
    private int currentMonth;
    private int currentDay;
    private Location location;
    private String placeId;

    public RestApiResultReceiver mReceiver;

    //UI element
    private EditText edDate;
    private EditText titleEditText;
    private Button saveButton;
    private Spinner spinnerCateg;
    private Switch typeSwitch;
    private EditText maxParticipantEditext;
    private AutoCompleteTextView autocompleteView;

    //element Value
    private String edDatValue;
    private String titleValue;
    private String categValue ;
    private String typeValue = Types.LOISIR;
    private int maxParticipantValue;
    private String placeName;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewEventFragment newInstance(String param1, String param2) {
        NewEventFragment fragment = new NewEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NewEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        final Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH);
        currentDay = c.get(Calendar.DAY_OF_MONTH);

        mReceiver = new RestApiResultReceiver(new Handler());
        mReceiver.setReceiver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_newevent, container, false);
        this.edDate = (EditText) view.findViewById(R.id.edDate);
        this.spinnerCateg = (Spinner) view.findViewById(R.id.spinnerCategory);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_item, Categories.getCategories());
        this.spinnerCateg.setAdapter(adapter);
        edDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });

        //switch value
        typeSwitch = (Switch) view.findViewById(R.id.swCompet);
        typeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                   typeValue = Types.COMPETITIF;
                } else {
                    typeValue = Types.LOISIR;
                }
            }
        });


        titleEditText = (EditText) view.findViewById(R.id.edTitle);
        maxParticipantEditext = (EditText) view.findViewById(R.id.edMaxParti);
        //Autocomplete
        autocompleteView = (AutoCompleteTextView) view.findViewById(R.id.autocomplete);
        autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item));
        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                Place selectedPlace =  (Place)parent.getItemAtPosition(position);
                placeId = selectedPlace.getId();
                Log.e(TAG,  selectedPlace.getId());
                (new Thread() {
                    public void run() {
                        location = GooglePlaces.getPlacesLocation(placeId);
                        Log.d(TAG,location.toString());
                    }
                }).start();
            }
        });

        //create button
        saveButton = (Button) view.findViewById(R.id.eventCreateButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get and check input
                if(setArgs() && location != null) {
                    Log.d(TAG,"All args ok");
                    QueryEventService.startActionCreateEvent(v.getContext(),mReceiver, location.getLng(),location.getLat(),titleValue,categValue,maxParticipantValue,typeValue,placeName );
                }
            }
        });
        return view;
    }

    public boolean setArgs(){
        boolean missingField = false;
        if (titleEditText.getText().toString().trim().equals("")){
            missingField = true;
            titleEditText.setError("required field");
        } else titleValue = titleEditText.getText().toString();

        categValue = spinnerCateg.getSelectedItem().toString();
        //TODO const here
        if (categValue.equals("Choisir")){
            //TODO add error
            missingField = true;
        }

        //TODO : maxparticipant must be int
        if (maxParticipantEditext.getText().toString().trim().equals("")){
            missingField = true;
            maxParticipantEditext.setError("required field");
        } else maxParticipantValue = Integer.valueOf(maxParticipantEditext.getText().toString());

        if (autocompleteView.getText().toString().trim().equals("")){
            missingField = true;
            autocompleteView.setError("required field");
        } else placeName = autocompleteView.getText().toString();
        return ! missingField;
    }



    public void openDatePicker(){
        new DatePickerDialog(this.getContext(), setDate, currentYear, currentMonth, currentDay).show();
    }
    public void openTimePicker(){
        new TimePickerDialog(this.getContext(),setTime,12,00,true).show();
    }

    private DatePickerDialog.OnDateSetListener setDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            edDate.setText((month + 1) + "-" + day + "-" + year);
            openTimePicker();
        }
    };
    private TimePickerDialog.OnTimeSetListener setTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            String sMinute = String.valueOf(minute);
            if(minute < 10) sMinute = "0"+minute;
            edDate.setText(edDate.getText().toString() + " @ " + hour + "h" + sMinute);
        }

    };


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

    //call when service send to receiver
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case QueryIntentService.STATUS_RUNNING:
                Log.i(TAG, "Runing status");
                //show progress
                break;
            case QueryIntentService.STATUS_FINISHED:
                String results = resultData.getString("results");
                Log.i(TAG, "result status =");
                Log.i(TAG, results);
                // do something interesting
                // hide progress
                break;
            case QueryIntentService.STATUS_ERROR:
                //todo handl error
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.e(TAG, "error = " + error);
                break;
        }
    }
}
