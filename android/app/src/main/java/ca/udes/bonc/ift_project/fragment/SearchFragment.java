package ca.udes.bonc.ift_project.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ca.udes.bonc.ift_project.IFTApplication;
import ca.udes.bonc.ift_project.R;
import ca.udes.bonc.ift_project.adapter.EventAdapter;
import ca.udes.bonc.ift_project.communication.QueryEventService;
import ca.udes.bonc.ift_project.communication.QueryIntentService;
import ca.udes.bonc.ift_project.communication.RestApiResultReceiver;
import ca.udes.bonc.ift_project.dataObject.Categories;
import ca.udes.bonc.ift_project.dataObject.Event;
import ca.udes.bonc.ift_project.utils.ConvertJson;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements RestApiResultReceiver.Receiver {
    private static final String TAG = "Search Fragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static RestApiResultReceiver mReceiver;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int currentYear;
    private int currentMonth;
    private int currentDay;

    private EditText edDate;
    private Spinner spinnerCateg;
    private EditText txtName;
    private EditText txtAuthor;
    private ProgressBar progressBar;
    private ListView listMap;
    private CheckBox cbLoisir;
    private CheckBox cbCompetitif;


    private View view;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        fragment.setRetainInstance(true);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
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
        this.view = inflater.inflate(R.layout.fragment_search, container, false);
        this.spinnerCateg = (Spinner) view.findViewById(R.id.spinnerCategory);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_item, Categories.getCategories());
        this.spinnerCateg.setAdapter(adapter);
        this.listMap = (ListView) view.findViewById(R.id.listMap);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        this.txtName = (EditText) view.findViewById(R.id.edName);
        this.txtAuthor = (EditText) view.findViewById(R.id.edAuthor);
        this.edDate = (EditText) view.findViewById(R.id.edDate);
        this.cbLoisir = (CheckBox) view.findViewById(R.id.cbLoisir);
        this.cbCompetitif = (CheckBox) view.findViewById(R.id.cbCompetitif);
        edDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });
        Button btn = (Button) view.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
            Log.i(TAG, "search pressed");
        }
    }

    public void openDatePicker(){
        new DatePickerDialog(this.getContext(), setDate, currentYear, currentMonth, currentDay).show();
    }

    private DatePickerDialog.OnDateSetListener setDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            edDate.setText((month + 1) + "-" + day + "-" + year);
        }
    };

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

    public void search(){
        progressBar.setVisibility(View.VISIBLE);
        String name;
        String date;
        String author;
        String category;
        String mode;

        category = spinnerCateg.getSelectedItem().toString();
        if (category.equals(Categories.CHOISIR))
            category="";
        name = txtName.getText().toString();
        author = txtAuthor.getText().toString();
        date = edDate.getText().toString();
        mode = "";
        if(cbLoisir.isChecked())
            mode = "loisir";
        if(cbCompetitif.isChecked())
            mode = "competitif";
        if(cbLoisir.isChecked() && cbCompetitif.isChecked())
            mode = "";

        Log.i(TAG, "search for category=" + category + ", name=" + name + ", author=" + author + ", date=" + date + ", mode=" + mode);
        QueryEventService.startActionFind(getContext(), mReceiver, category, name, date, author, mode);
    }

    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case QueryIntentService.STATUS_RUNNING:
                Log.i(TAG, "Running status");
                this.progressBar.setVisibility(View.VISIBLE);
                //show progress
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

    public void updateEventList(List<Event> listEvent){
        listMap = (ListView) view.findViewById(R.id.listMap);

        EventAdapter adapter = new EventAdapter(getContext(), R.layout.adapter_event, listEvent, ((IFTApplication) getActivity().getApplication()).getUserId());
        listMap.setAdapter(adapter);
    }
}
