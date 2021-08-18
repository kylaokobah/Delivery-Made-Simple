package com.example.dmsimpledriver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;


import java.util.HashMap;
import java.util.Map;

public class MainActivity<googleApiClient> extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private EditText editTextEmail;
    private EditText editTextPass;
    private Button btnlogin_norm;
    private Button forgot;
    private Button register;
    private Button new_googlebtn;
    boolean valid = true;
    private static final String EMAIL = "email";
    private static final String USER_POSTS = "user_posts";
    private static final String AUTH_TYPE = "rerequest";
    //    SignInButton google_signInButton;
    private GoogleApiClient googleApiClient;
    private static final int SIGN_IN=1;
    private final String TAG= "MainActivity:";
    private boolean userFound, signedIn;
    FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private Intent dash;
    private boolean emailExist = false;
    private DocumentReference docRef;
    public static String usernam;
    private Bundle Extras;
    String newRideId = null;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernam = "";
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("1048071252142-7m6ln9dvvrkdbae9kghlj86sbs4ff8vu.apps.googleusercontent.com")
                .requestIdToken("512365624915-6anssjrcit296k1fkdjcfhrnitkmjc1v.apps.googleusercontent.com")

//                .requestIdToken("1048071252142-7m6ln9dvvrkdbae9kghlj86sbs4ff8vu.apps.googleusercontent.com")
//                .requestIdToken("1006308317058-9nlfkr70dh01c8q3jjc6elde5e07e5ri.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleApiClient = new GoogleApiClient.Builder( this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        Extras =  getIntent().getExtras();


//        if(Extras != null &&Extras.getString("ride_id")!=null&& mAuth.getCurrentUser()!=null){
            if(Extras != null ){
                Log.d(TAG, "Extra: "+Extras);
                Log.d(TAG, "Extra: ride_id "+Extras.getString("ride_id"));
                if (Extras.getString("ride_id") != null){
                    Intent i =  new Intent (MainActivity.this, Dashboard.class);
                    i.putExtra("ride_id",Extras.getString("ride_id"));
                    startActivity(i);
                    finish();
                }


        }
//        dash = new Intent(MainActivity.this, Dashboard.class);

        userFound = false;
        signedIn = false;
        /*Google Login*/
//        google_signInButton=findViewById(R.id.google_login_button);
//        new_googlebtn=findViewById(R.id.new_googlebtn);
//        new_googlebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signIn();
//                if (v== new_googlebtn) {
//                    google_signInButton.performClick();
//                }
//                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
//
//                //updateUI(account);
//
//            }
//        });





        /*Email Login*/
        //email login with your credentials
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPass = findViewById(R.id.editTextPass);
        btnlogin_norm = (Button) findViewById(R.id.btnlogin_norm);

        btnlogin_norm.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick (View v){

                                                 if(editTextEmail.getText().toString().equals("")) {
                                                     Toast.makeText(getApplicationContext(), "Please type an email", Toast.LENGTH_SHORT).show();

                                                 }else if(editTextPass.getText().toString().equals("")){
                                                     Toast.makeText(getApplicationContext(), "Please type a password", Toast.LENGTH_SHORT).show();

                                                 }


                                                 else if(!Register_User.isEmpty(editTextEmail) && !Register_User.isEmpty(editTextPass)){
                                                     mAuth.signInWithEmailAndPassword(editTextEmail.getText().toString(), editTextPass.getText().toString())
                                                             .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                                                 @Override
                                                                 public void onComplete(@NonNull Task<AuthResult> task) {
                                                                     if (task.isSuccessful()) {
                                                                         // Sign in success, update UI with the signed-in user's information
                                                                         Log.d(TAG, "signInWithEmail:success");
                                                                         FirebaseUser user = mAuth.getCurrentUser();
                                                                         User myuser = new User(user.getDisplayName(),user.getEmail(),  user.getPhoneNumber(), user.getUid(),"");
                                                                         myuser.saveUser(getApplicationContext(), myuser);
                                                                         startActivity(new Intent (getApplicationContext(), Dashboard.class));

                                                                     } else {
                                                                         // If sign in fails, display a message to the user.
                                                                         Log.w(TAG, "signInWithEmail:failure", task.getException());
                                                                         Toast.makeText(MainActivity.this, "Authentication failed.",
                                                                                 Toast.LENGTH_SHORT).show();

                                                                     }
                                                                 }
                                                             });



                                                     // check if correct sign in type is used.
                                                     docRef =  db.collection("users").document(editTextEmail.getText().toString());
                                                     docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                         @Override
                                                         public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                             if (task.isSuccessful()) {
                                                                 DocumentSnapshot document = task.getResult();
                                                                 if (document.exists() && document.get("user_type").equals("driver")) {
                                                                     userFound = true;
                                                                     if (document.get("login_type").equals("email_and_password")) {

                                                                     }else {
                                                                         Toast.makeText(getApplicationContext(), "Wrong login Type, please try a different way! ", Toast.LENGTH_SHORT).show();
                                                                     }
                                                                 }
                                                             } else {
                                                                 Log.d(TAG, "Email checking failed with ", task.getException());
                                                                 Toast.makeText(getApplicationContext(), "Password and email entered do not match!", Toast.LENGTH_LONG).show();

                                                             }


                                                         }
                                                     });

                                                     // end of checking login type.
                                                 }

                                                 else{
                                                     Toast.makeText(getApplicationContext(), "Provide both login and password to log in", Toast.LENGTH_LONG).show();
                                                 }

                                             }
                                         }
        );


        /*Forgot Password*/

        forgot = (Button) findViewById(R.id.forgot);
        findViewById(R.id.forgot).setOnClickListener(new View.OnClickListener() {

                                                         //calling ForgotPassword class
                                                         @Override
                                                         public void onClick (View v){
                                                             startActivity(new Intent (getApplicationContext(),ForgotPassword.class));
                                                         }
                                                     }
        );


        /*Create New User*/

        register = (Button) findViewById(R.id.register);
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {

                                                           //calling register_user class
                                                           @Override
                                                           public void onClick (View v){
                                                               startActivity(new Intent (getApplicationContext(),Register_User.class));
                                                           }
                                                       }
        );


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            GoogleSignInAccount account = null;
            try {
                account = task.getResult(ApiException.class);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            }
            if (account != null) {
                Intent regIntent = new Intent(MainActivity.this, Dashboard.class);
                String personName = account.getDisplayName();
                String personEmail = account.getEmail();
                String personId = account.getId();
                String id = account.getIdToken();
                Uri personPhoto = account.getPhotoUrl();
                Map<String, Object> my_user = new HashMap<>();
                my_user.put("firstname", personName);
                my_user.put("email", personEmail);
                my_user.put("login_type","google_sign_in");
                my_user.put("password", " ");
                my_user.put("id_token", account.getIdToken());


                if (!userExist(personEmail)) {

                    mAuth.createUserWithEmailAndPassword(personEmail, id);
                    User newUser = new User(personName, personEmail, "", account.getId(),"");
                    newUser.saveUser(getApplicationContext(), newUser);

                    db.collection("users").document(personEmail).set(my_user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mAuth.signInWithEmailAndPassword(personEmail,id)
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d(TAG, "signInWithEmail:success");
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                User myuser = new User(user.getDisplayName(),user.getEmail(),  user.getPhoneNumber(), user.getUid(),"");
                                                myuser.saveUser(getApplicationContext(), myuser);
                                                startActivity(new Intent (getApplicationContext(), Dashboard.class));

                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }

                    });

                } else {

                    User newUser = new User(personName, personEmail, "", account.getIdToken(),"");
                    newUser.saveUser(getApplicationContext(), newUser);

                    mAuth.signInWithEmailAndPassword(personEmail,id)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        User myuser = new User(user.getDisplayName(),user.getEmail(),  user.getPhoneNumber(), user.getUid(),"");
                                        myuser.saveUser(getApplicationContext(), myuser);
                                        startActivity(new Intent (getApplicationContext(), Dashboard.class));
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });


                    Log.d(TAG, "User " + personEmail + " already exists!");
                    Toast.makeText(MainActivity.this, "Welcome Back", Toast.LENGTH_LONG).show();

                }


            }
        }

    }

    protected boolean loginSuccess (String emailEntered, String pass){
        if (docRef != null)
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            userFound = true;
                            if (document.get("password").equals(pass)) {
                                Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_SHORT).show();
                                signedIn = true;
                            }
                        }
                    } else {
                        Log.d(TAG, "Email checking failed with ", task.getException());
                    }
                }

            });
        return signedIn;
    }
    @Override
    protected void onStart() {
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Extras = getIntent().getExtras();
        if(Extras != null ){
            Log.d(TAG, "Extra: "+Extras);
            Log.d(TAG, "Extra: ride_id "+Extras.getString("ride_id"));
            if (Extras.getString("ride_id") != null){
                Intent i =  new Intent (this, Dashboard.class);
                i.putExtra("ride_id",Extras.getString("ride_id"));
                startActivity(i);
                finish();
            }


        }
        if(currentUser != null){
            startActivity(new Intent (MainActivity.this, Dashboard.class));
            finish();
        }

//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account!=null){
//            dash.putExtra("username", account.getDisplayName());
//            dash.putExtra("email", account.getEmail());
//            dash.putExtra("ID", account.getId());
//
//            startActivity(new Intent(getApplicationContext(), Dashboard.class));
//
//            finish();
//        }
        //updateUI(account);


    }

    protected boolean userExist(String emailEntered){
        DocumentReference docRef = db.collection("users").document(emailEntered);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        emailExist = true;
                        Toast.makeText(getApplicationContext(), "Email address already exist!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "Email checking failed with ", task.getException());
                }
            }

        });
        return emailExist;
    }

    // [START handleSignInResult]
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            //mStatusTextView.setText(getString(R.string.app_name, account.getDisplayName()));
            //findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            String personName = account.getDisplayName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();
            /*homeIntent.putExtra("username", personName);
            homeIntent.putExtra("email", personEmail);
            homeIntent.putExtra("ID", personId);
            homeIntent.putExtra("profilePict", personPhoto);
            homeIntent.putExtra("lastname", personFamilyName);*/
            startActivity(new Intent(getApplicationContext(), Dashboard.class));

        } else {
            //mStatusTextView.setText(R.string.signed_out);

            //findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            Toast.makeText(this,"Didnt signed in yet",Toast.LENGTH_LONG).show();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


}




