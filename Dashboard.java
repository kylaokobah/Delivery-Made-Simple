package com.example.dmsimpledriver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.example.dmsimpledriver.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class Dashboard extends AppCompatActivity  implements LocationListener {
    private Button logout_bt;
    private GoogleSignInClient mGoogleSignInClient;
    private LinearLayout requests;
    private TextView new_request, request_details;
    FirebaseFirestore db;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String Prefs = "myPrefs";
    String TAG = "dashboard";
    //    private DataUpdateReceiver dataUpdateReceiver;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    LocationManager locationManager;
    ListView listView;
    ArrayList<Ride> deliveries= new ArrayList<>();
    MyAdapter adapter;
    FloatingActionButton view_profile;
    TextView textView;
    FirebaseUser user;
    Button accept_bt, decline_bt, view_bt;
    private Bundle Extras;
    String newRideId = null;
    boolean isDialogVisible =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requesting full screen for display
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);
        view_profile = findViewById(R.id.view_profile);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        // Get a non-default Storage bucket
        // Access a Cloud Firestore instance from  Activity
        db = FirebaseFirestore.getInstance();
        textView = new TextView(this);
        listView = findViewById(R.id.delivery_list);
        adapter =  new MyAdapter(this, deliveries);
        getRideFromDataBase();
        listView.setAdapter(adapter);

        textView.setText("Your pending Jobs");
        textView.setTextSize(30);
        listView.addHeaderView(textView);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);




         Extras =  getIntent().getExtras();
         if(Extras != null && Extras.getString("ride_id") != null){
             Log.d(TAG, "Extra: "+Extras.getString("ride_id"));
             //showNewRide(Extras.getString("ride_id"));
             if (newRideId != null && Extras.getString("ride_id").compareTo(newRideId) == 0 ){
                 Log.d(TAG,"repeated notification action");
             }else {
                 showNewRide(Extras.getString("ride_id"));
             }
         }
        Intent intent = getIntent();
        if(intent.getStringExtra("ride_id") != null){
            Log.d(TAG, "Extra: "+intent.getStringExtra("ride_id"));
           if (newRideId != null && intent.getStringExtra("ride_id").compareTo(newRideId) == 0 ){
               Log.d(TAG,"repeated notification action");

           }else {
               showNewRide(intent.getStringExtra("ride_id"));

           }
        }

        // showing profile
        view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (getApplicationContext(), profile.class));
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE }, REQUEST_CODE);

            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION }, REQUEST_CODE);

            return;
        }
        fetchLocation();


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = "notifiction token: " +  token;
                        Log.d(TAG, msg);
//                        Toast.makeText(dashboardUI.this, msg, Toast.LENGTH_SHORT).show();

                        User curent_user = new  User();
                        curent_user = curent_user.getUser(getApplicationContext());

                        curent_user.saveNotificationToken(Dashboard.this, token);
                        saveTodatabase("notification_token", token);
                    }
                });


    }
    @Override
    public void onResume() {
        super.onResume();
        getRideFromDataBase();
    }

    public  void accept_ride(){
        Log.d(TAG, "accept");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // ...
        super.onSaveInstanceState(outState);

    }

//    private void getRideFromDataBase2() {
//        db.collection("newRides").whereEqualTo("driver_id",user.getEmail()).get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot documentSnapshots) {
//                        if (documentSnapshots.isEmpty()) {
//                            Log.d(TAG, "onSuccess: LIST EMPTY");
//                            textView.setText("You have no pending Jobs");
//                            return;
//                        } else {
//                            // Convert the whole Query Snapshot to a list
//                            // of objects directly! No need to fetch each
//                            // document.
//                            ArrayList<Ride> tp = new ArrayList<>();
//
//                            for(QueryDocumentSnapshot doc : documentSnapshots){
//                            //    Ride r = doc.toObject(Ride.class);
//                                //  Log.d(TAG, "ride "+ r.getDrop_off_location());
//                                String current_driver_id =  doc.getData().get("current_driver_id") != null ? doc.getData().get("current_driver_id").toString() : "";
//                                String  user_id =    doc.getData().get("user_id") !=null ?  doc.getData().get("user_id").toString(): "";
//                                String  status =    doc.getData().get("status") !=null ?  doc.getData().get("status").toString(): "";
//                                String  user_phone =    doc.getData().get("user_phone") !=null ?  doc.getData().get("user_phone").toString(): "";
//
//                                Map<String, String>  pick_up_location =    (Map) doc.getData().get("pick_up_location");
//                                Map<String, String>    drop_off_location =    (Map) doc.getData().get("drop_off_location");
//                                ArrayList<String>  rejected_drivers_id  = (ArrayList<String>) doc.getData().get("rejected_drivers_id");
//
//                                Ride d = new Ride(doc.getId(),user_id,pick_up_location, drop_off_location, rejected_drivers_id,
//                                        current_driver_id,status,user_phone);
//                                tp.add(d);
//                                adapter.items.add(d);
//                            }
//                            Log.d(TAG, "recived rides count"+  tp.size());
//                            // Add all to your list
////                            deliveries = tp ;
//
//                            adapter.notifyDataSetChanged();
//
//                        }
//                    };
//                });
//    }


    private void getRideFromDataBase() {
        db.collection("newRides").whereEqualTo("driver_id",user.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                            // Convert the whole Query Snapshot to a list
                            // of objects directly! No need to fetch each
                            // document.
                            ArrayList<Ride> tp = new ArrayList<>();

                            for(QueryDocumentSnapshot doc : value){
                                //    Ride r = doc.toObject(Ride.class);
                                //  Log.d(TAG, "ride "+ r.getDrop_off_location());
                                String current_driver_id =  doc.getData().get("current_driver_id") != null ? doc.getData().get("current_driver_id").toString() : "";
                                String  user_id =    doc.getData().get("user_id") !=null ?  doc.getData().get("user_id").toString(): "";
                                String  status =    doc.getData().get("status") !=null ?  doc.getData().get("status").toString(): "";
                                String  user_phone =    doc.getData().get("user_phone") !=null ?  doc.getData().get("user_phone").toString(): "";
                                Map<String, String>  pick_up_location =    (Map) doc.getData().get("pick_up_location");
                                Map<String, String>    drop_off_location =    (Map) doc.getData().get("drop_off_location");
                                ArrayList<String>  rejected_drivers_id  = (ArrayList<String>) doc.getData().get("rejected_drivers_id");

                                Ride d = new Ride(doc.getId(),user_id,pick_up_location, drop_off_location, rejected_drivers_id,
                                        current_driver_id, status,user_phone);
                                tp.add(d);
//
                            }
                            Log.d(TAG, "recived rides count"+  tp.size());
                            // Add all to your list
                            deliveries = tp ;
                            adapter.items = deliveries;

                            adapter.notifyDataSetChanged();


                    };
                });
    }

    private void saveTodatabase(String notification_token, String token) {
        //asynchronously retrieve multiple documents

        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, currentUser.getUid() + " user id  => " + currentUser.getEmail());
        if(currentUser != null){

            db.collection("users")
                    .whereEqualTo("email",currentUser.getEmail() )
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "user found"+task.getResult());

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    db.collection("users").document(document.getId()).update("notification_token",token);
                                }

                            } else {
                                Log.d(TAG, "user not found");
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }


    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.i("Message: ","Location changed, " + location.getAccuracy() + " , " + location.getLatitude()+ "," + location.getLongitude());
        currentLocation = location;
        saveLocationToDatabase(location.getLongitude(),location.getLatitude());
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION }, REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, this);

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d(TAG, "fetching location ");
                    currentLocation = location;
//                    Toast.makeText(this, location.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "fetching latitude  " + currentLocation.getLatitude());
                    saveLocationToDatabase(location.getLatitude(), location.getLongitude());

                }
            }
        });
    }


    private void saveLocationToDatabase(Double lng, Double lat) {
        //asynchronously retrieve multiple documents
        Log.d(TAG, "saving location ...");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && currentLocation != null){
            db.collection("users")
                    .whereEqualTo("email",currentUser.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.R)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String id = document.getId().replace("@","__");
                                    id = id.replace(".","_");
                                    mDatabase.child("users").child(id).child("location").setValue( Map.of(
                                            "latitude", lat,
                                            "longitude", lng,
                                            "bearing",currentLocation.getBearing(),
                                            "speed", currentLocation.getSpeed(),
                                            "provider",currentLocation.getProvider()
                                    ));

                                    db.collection("users").document(document.getId()).update("location",
                                            Map.of(
                                                    "latitude", lat,
                                                    "longitude", lng,
                                                    "bearing",currentLocation.getBearing(),
                                                    "speed", currentLocation.getSpeed(),
                                                    "provider",currentLocation.getProvider()
                                            )
                                    );

                                }



                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }


    }

    private void showNewRide(String ride_id) {

        db.collection("newRides").document(ride_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                Log.d(TAG, "result : "+task.getResult());
                Map<String, String> pick_up_location = (Map <String, String> ) task.getResult().get("pick_up_location");
                Map<String, String> drop_off_location = (Map <String, String> ) task.getResult().get("drop_off_location");
//                NavController navController = Navigation.findNavController(dashboardUI.this, R.id.nav_host_fragment);
                Bundle b = new Bundle();
                b.putString("lat1", pick_up_location.get("latitude"));
                b.putString("lng1", pick_up_location.get("longitude"));
                b.putString("lat2", drop_off_location.get("latitude"));
                b.putString("lng2", drop_off_location.get("longitude"));
                b.putString("ride_id", ride_id);

                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                bottomSheet.setArguments(b);
                bottomSheet.show(getSupportFragmentManager(),
                        "New Job!");
                isDialogVisible = true;

//                navController.navigate(R.id.navigation_notifications, b);

            }

        });

    }

    class MyAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<Ride> items;

        public MyAdapter( Context context, ArrayList<Ride> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            // inflate the layout for each list row
            if (view == null) {
                view = LayoutInflater.from(context).
                        inflate(R.layout.listview_delivery_layout, viewGroup, false);
            }
            // get current item to be displayed
            Ride currentItem = (Ride) getItem(i);
            // get the TextView for item name and item description
//            GoogleMap map  = (GoogleMap) view.findViewById(R.id.delivery_map);
            TextView delivery_title = view.findViewById(R.id.delivery_title);
            TextView ride_status_tv = view.findViewById(R.id.ride_status_tv);
            TextView call = view.findViewById(R.id.call_bt);
            Button pick_up =  view.findViewById(R.id.pick_up);
            Button drop_off =  view.findViewById(R.id.drop_off);
            Button done =  view.findViewById(R.id.done);
            delivery_title.setText("for: "+ currentItem.getUser_id());
                if (currentItem.getUser_phone() != null ){
                    call.setText("Tel: "+currentItem.getUser_phone());
                    call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            Intent i = new Intent(Intent.ACTION_CALL);
                            i.setData(Uri.parse("tel:"+currentItem.getUser_phone()));
                            startActivity(i);
                        }
                    });
                }
                else {
                    call.setText("Tel: "+"Not Provided");

                }

            ride_status_tv.setText(currentItem.getStatus().replace("_", " "));

            pick_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+currentItem.getPick_up_location().get("pickUp_address")+"a&avoid=tf");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                }
            });
            drop_off.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+currentItem.getDrop_off_location().get("dropOff_address")+"a&avoid=tf");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                }
            });
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Dashboard.this, UploadImage.class);
                    intent.putExtra("ride_id",currentItem.getId());
                    startActivity(intent);
                }
            });
            //sets the text for item name and item description from the current item object


            // returns the view for the current row
            return view;
        }
    }




}