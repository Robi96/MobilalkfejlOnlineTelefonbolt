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

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String LOG_TAG = RegisterActivity.class.getName();

    EditText username;
    EditText email;
    EditText password;
    EditText passwordConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Bundle registerBundle = getIntent().getExtras();
        assert registerBundle != null;
        int secret_key =registerBundle.getInt("REGISTER_KEY");
        if(secret_key != 42){
            finish();
        }
        mAuth = FirebaseAuth.getInstance();
    }

    public void register(View view) {
        username= findViewById(R.id.usernameEditText);
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        passwordConfirmation = findViewById(R.id.passwordConfirmationEditText);

        String Username = username.getText().toString();
        String Email = email.getText().toString();
        String Password = password.getText().toString();
        String PasswordConf = passwordConfirmation.getText().toString();

        if(!Password.equals(PasswordConf)) {
            Log.e(LOG_TAG, "Nem egyezik a két jelszó!");
        }else if(Password.isEmpty() || PasswordConf.isEmpty() || Username.isEmpty() || Email.isEmpty()){
            Log.e(LOG_TAG, "Felhasználónév, email és jelszó kitöltése kötelező!");
        }else {Log.i(LOG_TAG, "Regisztrált: " + Username + ", Email: " + Email + ", jelszava: " + Password);
            mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.d(LOG_TAG, "Felhasználó sikeresen regisztrálva");
                        startShopSite();
                    }else {
                        Log.d(LOG_TAG, "Sikertelen regisztráció");
                        Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void startShopSite() {
        Intent intent = new Intent(this, ShopMainSite.class);
        startActivity(intent);
    }
    public void cancel(View view) {
        finish();
    }
}