package com.example.cambox;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.cambox.databinding.ActivityLoginBinding;
import com.example.cambox.model.User;
import com.example.cambox.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    ActivityLoginBinding binding;
    DatabaseReference ref;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        ref = FirebaseDatabase.getInstance().getReference();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.mEmail.getText().toString().trim();
                String password = binding.mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    binding.mEmail.setError("Email is Required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    binding.mPassword.setError("Email is Required");
                    return;
                }

//                progressBar.setVisibility(View.VISIBLE);

                user = new User(binding.mEmail.getText().toString(), binding.mPassword.getText().toString());
                //Authenticate user
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //variable to store email and password
                        String emaildb = "";
                        String passdb = "";
                        DataSnapshot data = snapshot.child("Account").child(Util.digest(user.getEmail()));
                        //get encrypted email from key value of user.
                        emaildb = data.getKey();
                        //get the password value
                        passdb = data.child("password").getValue(String.class);
                                Log.d("key", "login pressed");
                        try {
                            if (emaildb.equals(Util.digest(user.getEmail())) && passdb.equals(user.getPassword())) {
                                binding.progressBar2.setVisibility(View.VISIBLE);
                                user.setKey(data.getKey());
                                user.setName(data.child("name").getValue(String.class));
                                user.setPhone(data.child("phone").getValue(String.class));
                                Toast.makeText(Login.this, "Login Successfully.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("user",user);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                            }
                        }catch (NullPointerException e){
                            Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
            }
        });


        binding.createText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }
}