package fr.epita.android.pri;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.DeviceLoginButton;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    Button loginbut;
    EditText login, password;
    TextView forgot, signup, loginstat;
    LoginButton fbloginbut;
    SignInButton googlesignbut;
    CallbackManager callbackManager;

    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;

    DatabaseHandler dh = new DatabaseHandler(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        loginbut = (Button) findViewById(R.id.loginbutton);
        login = (EditText) findViewById(R.id.logintext);
        password = (EditText) findViewById(R.id.passwordtext);
        forgot = (TextView) findViewById(R.id.forgotpass);
        signup = (TextView) findViewById(R.id.signup);
        fbloginbut = (LoginButton) findViewById(R.id.facebook_login);
        loginstat = (TextView) findViewById(R.id.login_status);
        googlesignbut = (SignInButton) findViewById(R.id.google_login);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();

        callbackManager = CallbackManager.Factory.create();
        fbloginbut.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                Toast.makeText(getApplicationContext(),
                        "Login Successful !", Toast.LENGTH_LONG).show();
                loginstat.setText("Login Successful ! \n" +
                        loginResult.getAccessToken().getUserId());


            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),
                        "Login Failed !", Toast.LENGTH_LONG).show();
                loginstat.setText("Login Failed !");
            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        loginbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);

                String loginuser = login.getText().toString();
                String passuser = password.getText().toString();
                String passdb = dh.searchPass(loginuser);

                if (passuser.equals(passdb)) {
                    if (loginuser.equalsIgnoreCase("admin") && passuser.equalsIgnoreCase("pass")) {
                        Toast.makeText(getApplicationContext(), "Redirecting... Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Username or password is incorrect !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                Toast.makeText(getApplicationContext(),
                        "Forgot Password ? Really !!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                Toast.makeText(getApplicationContext(),
                        "Thats a super Idea !!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        googlesignbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.google_login:
                        signIn();
                        break;}
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_CODE){
    GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
    resulthandler(result);
}

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
          }

    private void signOut() {
    }

    private void resulthandler(GoogleSignInResult result) {
        if(result.isSuccess())
        {
           // Intent it=new Intent(getApplicationContext(),Profile.class);
            GoogleSignInAccount account=result.getSignInAccount();
            String name=account.getDisplayName();
            String email=account.getEmail();
            updateUI(true);
        //    startActivity(it);
            loginstat.setText(name+" "+email);

        }
        else {
            updateUI(false);
        }
    }

    private void updateUI(boolean isLogin) {

        if (isLogin==true)
        {
            googlesignbut.setVisibility(View.GONE);
            Intent it=new Intent(getApplicationContext(),Profile.class);
            startActivity(it);
        }
        else
        {
            googlesignbut.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_login:
                signIn();
                break;}
    }
}





