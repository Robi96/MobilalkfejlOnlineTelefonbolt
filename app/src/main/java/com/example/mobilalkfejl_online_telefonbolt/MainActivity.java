package com.example.mobilalkfejl_online_telefonbolt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private FirebaseAuth mAuth;
    private static final int REGISTER_KEY = 42;

    EditText usernameStr;
    EditText passwordStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void login(View view) {
        usernameStr = findViewById(R.id.UserName);
        passwordStr = findViewById(R.id.Password);

        String username = usernameStr.getText().toString();
        String password = passwordStr.getText().toString();
        if (username.isEmpty() && password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(LOG_TAG, "Sikeres bejelentkezés");
                        startShopSite();
                    } else {
                        Log.d(LOG_TAG, "Sikertelen bejelentkezés");
                        Toast.makeText(MainActivity.this, "Sikertelen bejelentkezés" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            //Log.i(LOG_TAG, "Bejelentkezett: " + username + ", jelszó: " + password);
        }else{
            Toast.makeText(MainActivity.this, "Email, vagy jelszó üresen maradt", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void register(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        registerIntent.putExtra("REGISTER_KEY", 42);
        startActivity(registerIntent);
    }

    private void startShopSite() {
        Intent intent = new Intent(this, ShopMainSite.class);
        startActivity(intent);
    }
}