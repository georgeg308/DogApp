package com.example.georgeg308.dogapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Login extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    public final static String EXTRA_MESSAGE = "com.example.georgeg308.dogapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                createSignInIntent();


            }
        });

        findViewById(R.id.buttonExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);

            }
        });
        //createSignInIntent();
    }


    public void createSignInIntent() {

        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        FirebaseApp.initializeApp(getBaseContext());
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this,"Signed In",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, MainActivity.class);
                String message = user.getUid();
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
                finish();

                // ...
            } else {
                Toast.makeText(this,"Sign In failed",Toast.LENGTH_SHORT).show();
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...

            }
        }
    }

}
