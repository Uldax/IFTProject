package ca.udes.bonc.ift_project.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import ca.udes.bonc.ift_project.IFTApplication;
import ca.udes.bonc.ift_project.MainActivity;
import ca.udes.bonc.ift_project.R;
import ca.udes.bonc.ift_project.adapter.EventAdapter;
import ca.udes.bonc.ift_project.communication.QueryEventService;
import ca.udes.bonc.ift_project.communication.QueryIntentService;
import ca.udes.bonc.ift_project.communication.RestApiResultReceiver;
import ca.udes.bonc.ift_project.dataObject.Event;
import ca.udes.bonc.ift_project.utils.AlertDialogManager;
import ca.udes.bonc.ift_project.utils.ConvertJson;
import ca.udes.bonc.ift_project.utils.GPSTracker;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements
        GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        OnMapReadyCallback,
        RestApiResultReceiver.Receiver {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static RestApiResultReceiver mReceiver;
    private static String TAG = "MapFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View contener;
    private FloatingActionButton fabLoc;
    private FloatingActionButton fabList;
    private ListView listMap;
    private View myView;
    private static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private AlertDialogManager alert = new AlertDialogManager();
    private EventAdapter adapter;
    private ProgressBar progressBar;
    private List<Marker> listMarker = new ArrayList<Marker>();
    private ArrayList<Event> data;

    private SupportMapFragment mSupportMapFragment;

    // GPS Location with callback overwrite
    private GPSTracker gps  = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        //fragment.setRetainInstance(true);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mReceiver = new RestApiResultReceiver(new Handler());
        mReceiver.setReceiver(this);

       gps = new GPSTracker(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.myView = inflater.inflate(R.layout.fragment_map, container, false);
        this.contener = container;
        this.fabList = (FloatingActionButton) myView.findViewById(R.id.fabList);
        this.fabLoc = (FloatingActionButton) myView.findViewById(R.id.fabLoc);
        this.listMap = (ListView) myView.findViewById(R.id.listMap);
        this.progressBar = (ProgressBar) myView.findViewById(R.id.progressBar);

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mSupportMapFragment).commit();
        }

        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(this);
        }

        fabLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localisateMe();
            }
        });

        fabList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleListMap();
            }
        });

        listMap.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String idEvent = data.get(i).getId();
                Fragment fragment = new DetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString(DetailFragment.ARG_PARAM1, idEvent);
                fragment.setArguments(bundle);
                ((MainActivity) getActivity()).switchFragment(fragment);
            }
        });
        listMap.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Marker m = listMarker.get(i);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 14));
                return true;
            }
        });
        return myView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("maps", "Call to on map ready");
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("maps", " Permission to access the location is missing.");
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
        gps.start();
        if( !gps.canGetLocation()){
            gps.showSettingsAlert();
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLatitude(), gps.getLongitude()), 14));
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.i("maps Cam change", "Camera : " + cameraPosition.target.latitude + " - " + cameraPosition.target.longitude);
                QueryEventService.startActionGetMarkersByRadius(getContext(), mReceiver, String.valueOf(cameraPosition.target.longitude), String.valueOf(cameraPosition.target.latitude), 200);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now
        outState.putSerializable("data", data);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore some state that needs to happen after the Activity was created
            //
            // Note #1: Our views haven't had their states restored yet
            // This could be a good place to restore a ListView's contents (and it's your last
            // opportunity if you want your scroll position to be restored properly)
            //
            // Note #2:
            // The following line will cause an unchecked type cast compiler warning
            // It's impossible to actually check the type because of Java's type erasure:
            //      At runtime all generic types become Object
            // So the best you can do is add the @SuppressWarnings("unchecked") annotation
            // and understand that you must make sure to not use a different type anywhere
            data = (ArrayList<Event>) savedInstanceState.getSerializable("data");
        } else {
            data = new ArrayList<>();
        }

        //updateEventList(data);
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
        //Memory leak,
        //mReceiver.setReceiver(null);
        mReceiver = null;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    protected void toggleListMap(){
        if(listMap.getVisibility() == View.GONE){
            listMap.setVisibility(View.VISIBLE);
            fabList.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }else {
            listMap.setVisibility(View.GONE);
            fabList.setImageResource(android.R.drawable.ic_menu_more);
        }
    }

    protected void localisateMe(){
        Snackbar.make(this.myView, "Localisation TODO", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }



    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case QueryIntentService.STATUS_RUNNING:
                Log.i(TAG, "Runing status");
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
                this.progressBar.setVisibility(View.GONE);
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.d(TAG, "error = " + error);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void updateEventList(ArrayList<Event> listEvent){
        listMap = (ListView) myView.findViewById(R.id.listMap);


        if((listEvent==null)||(listEvent.size()==0)){
            listMap.setAdapter(null);
            listMap.setVisibility(View.GONE);
            fabList.setImageResource(android.R.drawable.ic_menu_more);
            Toast.makeText(getContext(), "Sorry we don't have event here ", Toast.LENGTH_LONG).show();
            for (Marker m :listMarker)
                m.remove();
            return;
        }

        for (Event e:listEvent) {
            Log.i("maps", "add Marker :" + e.getTitle());
            listMarker.add(mMap.addMarker(new MarkerOptions().position(new LatLng(e.getLatitude(), e.getLongitude())).title(e.getTitle())));
        }
        this.data = listEvent;
        adapter = new EventAdapter(getContext(),R.layout.adapter_event, data,((IFTApplication)getActivity().getApplication()).getUserId());
        listMap.setAdapter(adapter);
    }
}
