package com.example.dmsimpledriver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.tasks.OnFailureListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register_User extends AppCompatActivity {

    EditText fname, pass,email,phone, address;
    FirebaseFirestore db;
    Button sign_up;
    boolean emailExist = false;
    FirebaseAuth mAuth;
    private final String TAG = "SignUpActivity:";

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__user);
        fname = findViewById(R.id.full_name);
        pass = findViewById(R.id.first_password);
        email = findViewById(R.id.new_email);
        phone = findViewById(R.id.new_number);
        address = findViewById(R.id.new_address);
        sign_up = findViewById(R.id.btnregister_user);
        mAuth = FirebaseAuth.getInstance();

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        email.addTextChangedListener(new TextWatcher() {
                                         @Override
                                         public void beforeTextChanged (CharSequence s,int start, int count,
                                                                        int after){
                                         }
                                         @Override
                                         public void onTextChanged ( final CharSequence s, int start, int before,
                                                                     int count){
                                             //You need to remove this to run only once

                                         }
                                         @Override
                                         public void afterTextChanged ( final Editable s){
                                             //avoid triggering event when text is empty
                                             /*DocumentReference docRef = db.collection("registered_users").document(email.getText().toString());
                                             docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                     if (task.isSuccessful()) {
                                                         DocumentSnapshot document = task.getResult();
                                                         if (document.exists()) {
                                                             Toast.makeText(Register_User.this, "Email address already exist!", Toast.LENGTH_LONG).show();
                                                             int R = (17) & 0xff;
                                                             email.setTextColor(R);

                                                         } else {
                                                             Log.d(TAG, "No such document");
                                                         }
                                                     } else {
                                                         Log.d(TAG, "Email checking failed with ", task.getException());
                                                     }
                                                 }
                                             });*/

                                         }
                                     }

        );
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(fname, pass, email, phone, address );
            }
        });


    }
    public static boolean isEmpty(EditText field){
        return field.getText().toString() == null && field.getText().toString().equals(" ") ? true:false;
    }

    public boolean isValidEmail(EditText email){
        final String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email.getText().toString());
        return matcher.matches();
    }

    public boolean isValidPhone(EditText phone) {
        Pattern pattern = Pattern.compile("^\\d{10}$");
        Matcher matcher = pattern.matcher(phone.getText().toString());
        return matcher.matches();
    }

    public boolean isValidPass(EditText pass){
        return pass.getText().toString().length() > 6 ? true:false;
    }

    protected boolean userExist(String emailEntered){

        DocumentReference docRef = db.collection("users").document(emailEntered);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(Register_User.this, "Email address already exist!", Toast.LENGTH_LONG).show();
                        int R = (17) & 0xff;
                        email.setTextColor(R);
                        emailExist = true;

                    }
                } else {
                    Log.d(TAG, "Email checking failed with ", task.getException());
                }
            }

        });
        return emailExist;
    }

    public void registerUser(EditText fname, EditText pass, EditText email, EditText phone, EditText address){

        if(!isEmpty(fname) && !isEmpty(pass) && !isEmpty(email) && !isEmpty(phone) && !isEmpty(address)
        ) {
            if (!isValidEmail(email)) {
                Toast.makeText(Register_User.this, "Email not Valid!", Toast.LENGTH_LONG).show();
            }
            else if(!isValidPhone(phone)){
                Toast.makeText(Register_User.this, "Phone Number not Valid!", Toast.LENGTH_LONG).show();
            }
            else if(!isValidPass(pass)){
                Toast.makeText(Register_User.this, "Password not Valid!", Toast.LENGTH_LONG).show();
            }
            else{
                User myUser = new User(fname.getText().toString(),
                        email.getText().toString(), phone.getText().toString(), pass.getText().toString(), address.getText().toString());
                Map<String, Object> my_user = new HashMap<>();
                my_user.put("firstname", myUser.getFullName());
                my_user.put("phone", myUser.getPhone());
                my_user.put("password", "");
                my_user.put("login_type", "email_and_password");
                my_user.put("user_type", "driver");
                my_user.put("email", myUser.getEmail());
                my_user.put("address", myUser.getAddress());

//                if(!userExist(email.getText().toString())) {


                mAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    db.collection("users").document(email.getText().toString()).set(my_user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
//                            mAuth.createUserWithEmailAndPassword(email.getText().toString(), myUser.getPassword().toString());
                                            myUser.saveUser(Register_User.this, myUser);
                                            if(user == null) {
                                                mAuth.signInWithEmailAndPassword(email.getText().toString(), myUser.getPassword().toString())
                                                        .addOnCompleteListener(Register_User.this, new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()) {
                                                                    // Sign in success, update UI with the signed-in user's information
                                                                    Log.d(TAG, "signInWithEmail:success");
                                                                    FirebaseUser user = mAuth.getCurrentUser();
//                                                                User myuser = new User(user.getDisplayName(),user.getEmail(),  user.getPhoneNumber(), user.getUid(),"");
//                                                                myuser.saveUser(getApplicationContext(), myuser);
                                                                    startActivity(new Intent(Register_User.this, Dashboard.class));
                                                                    finish();

                                                                } else {
                                                                    // If sign in fails, display a message to the user.
                                                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                                                    Toast.makeText(Register_User.this, "Authentication failed.",
                                                                            Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });

                                            }else{
                                                startActivity(new Intent(Register_User.this, Dashboard.class));
                                                finish();
                                            }

                                            Log.d(TAG, "DocumentSnapshot successfully written!");


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }

                                    });

                                } else {
                                    task.getException().getMessage();
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(Register_User.this, "Authentication failed. Reason: "+task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });




//                }
//                else{
//                    Log.d(TAG, "User "+email.getText().toString()+" already exists!");
//                    Toast.makeText(Register_User.this, "User "+email.getText().toString()+" already existS!", Toast.LENGTH_LONG).show();
//                }
            }
        }
        else{
            Log.d(TAG, "One of the field is empty!");
        }
    }

}
