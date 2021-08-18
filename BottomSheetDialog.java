package com.example.dmsimpledriver;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class BottomSheetDialog extends BottomSheetDialogFragment implements OnMapReadyCallback {
    GoogleMap notification_map;
    private Bundle Extras;
    Button reject_bt, accept_bt;
    String TAG = "dialog";
    String  ride_id = "-1";
    String lng1 = "0";
    String lat1= "0";
    String lat2 = "0";
    String lng2 = "0";
    String Prefs = "myPrefs";
    TextView text_notifications, time_tv;
    CardView cardviewMap;
    private FragmentActivity myContext;
    ProgressBar mProgressBar;
    CountDownTimer mCountDownTimer;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.bottom_sheet,
                container, false);

        Button algo_button = v.findViewById(R.id.decline_bt);
        Button course_button = v.findViewById(R.id.accept_bt);
        GoogleMap googleMap;
        MapView mMapView = (MapView) v.findViewById(R.id.mapView);
        MapsInitializer.initialize(getActivity());

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(this);

        time_tv = v.findViewById(R.id.time_tv);
        accept_bt = v.findViewById(R.id.accept_bt);
        reject_bt = v.findViewById(R.id.decline_bt);
        mProgressBar = v.findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        Extras = getArguments();
        if(Extras != null && Extras.getString("ride_id") != null){
            Log.d(TAG, "Extra: "+Extras.getString("ride_id"));
        ride_id = Extras.getString("ride_id");

        }
        if (savedInstanceState != null) {
            //Restore the fragment's state here
            ride_id =  savedInstanceState.getString("ride_id",ride_id);
            lng1 = savedInstanceState.getString("lng1" , lng1);
            lat1 = savedInstanceState.getString("lat1", lat1 );
            lat2 = savedInstanceState.getString("lat2", lat2 );
            lng2  = savedInstanceState.getString("lng2" , lng2);
            if(ride_id != "-1"){
                showRide(ride_id, lat1,
                        lng1,lat2
                        ,lng2);
            }

        }
        if(getArguments()!=null){

            ride_id = getArguments().getString( "ride_id");
            lng1 =  getArguments().getString( "lat1");
            lat1=  getArguments().getString("lng1");
            lat2 = getArguments().getString("lat2");
            lng2 = getArguments().getString( "lng2");
            if (notification_map!=null){
                showRide(ride_id, lat1,
                        lng1,lat2
                        ,lng2);
            }
        }
        accept_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accept();
                hide_element();
            }
        });
        reject_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refuse();
                hide_element();

            }
        });


        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        notification_map = googleMap;
        if(ride_id != "-1"){
            showRide(ride_id, lat1,
                    lng1,lat2
                    ,lng2);
        }
    }

    public void showRide(String ride_id, String lat1, String lng1, String lat2, String lng2){
        start();
        Log.d("tag_f", "Method call directly from activity"+ride_id);


        if (notification_map != null){
            Log.d("tag_f", "map is valide");

            LatLng latLng1 = new LatLng(Double.parseDouble(lat1),Double.parseDouble(lng1));
            LatLng latLng2 = new LatLng(Double.parseDouble(lat2),Double.parseDouble(lng2));
            MarkerOptions markerOptions1 = new MarkerOptions().position(latLng1).title("From");
            MarkerOptions markerOptions2 = new MarkerOptions().position(latLng2).title("To");
//            notification_map.animateCamera(CameraUpdateFactory.newLatLng(latLng1));
            notification_map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 12));
            notification_map.addMarker(markerOptions1);
            notification_map.addMarker(markerOptions2);

            Polyline polyline1 = notification_map.addPolyline(new PolylineOptions()
                    .add(
                            latLng1,
                            latLng2
                    ));

        }
    }


    public  void start(){
        final int[] i = {0};
        mProgressBar.setProgress(i[0]);
        mCountDownTimer=new CountDownTimer(15000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress"+ i[0] + millisUntilFinished);
                i[0]++;
                mProgressBar.setProgress((int) i[0] *100/(15000/1000));

            }

            @Override
            public void onFinish() {
                //Do what you want
                i[0]++;
                mProgressBar.setProgress(100);
                time_out();
            }
        };
        mCountDownTimer.start();


    }
    public void accept() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url ="https://us-central1-dmsimple-ce745.cloudfunctions.net/acceptRideRequest?new_ride_id="+ride_id+"&driver_id="+user.getEmail();
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                textView.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void refuse() {

        if (ride_id != "-1"){
            DocumentReference washingtonRef = db.collection("newRide").document(ride_id);
            washingtonRef.update("rejected_drivers_id", FieldValue.arrayUnion(user.getEmail()));
        }
        hide_element();

    }


    public void time_out() {

        if (ride_id != "-1"){
            DocumentReference washingtonRef = db.collection("newRide").document(ride_id);
            washingtonRef.update("rejected_drivers_id", FieldValue.arrayUnion(user.getEmail()));
        }
        hide_element();
    }
    public void hide_element(){
        accept_bt.setVisibility(View.GONE);
        reject_bt.setVisibility(View.GONE);
        this.dismiss();
        this.dismiss();
    }
}