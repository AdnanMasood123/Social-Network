package com.example.socialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ALL")
public class LoginActivity extends AppCompatActivity
{
    private static final int RC_SIGN_IN =1 ;
    private EditText Email,Password;
    private TextView donthaveAccount,Forget;
    private Button Login;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private ImageView GoogleSignin;
    private GoogleApiClient mGoogleSignInClient;
    private ProgressDialog mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth=FirebaseAuth.getInstance();
        // currentUserId=mAuth.getCurrentUser().getUid();
       // RootRef= FirebaseDatabase.getInstance().getReference();

        Email=(EditText)findViewById(R.id.Login_email);
        Password=(EditText)findViewById(R.id.Login_password);
        Login=(Button) findViewById(R.id.Login_button);
        donthaveAccount=(TextView) findViewById(R.id.Dont_have_account);
        GoogleSignin=(ImageView) findViewById(R.id.Login_Google);
        Forget=(TextView)findViewById(R.id.Forget_Password);
        mLoadingBar=new ProgressDialog(this);




        Forget.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(getApplicationContext(),ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });


        Login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               Login();
            }
        });



        donthaveAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                SendToRegisterActivity();
            }

        });


/*
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();



        mGoogleSignInClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener()
                {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
                    {
                        Toast.makeText(getApplicationContext()," Connection Failed",Toast.LENGTH_LONG).show();
                    }

                }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();


*/

    }





    private void SendToRegisterActivity()
    {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void SendToMainActivity()
    {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }



    private void Login()
    {


        String email=Email.getText().toString();
        String password=Password.getText().toString();



        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(getApplicationContext(),"Please Provide Your Email",Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(getApplicationContext(),"Please Provide Your Password",Toast.LENGTH_LONG).show();
        }

        else
        {

            mLoadingBar.setTitle("Loging-In  User");
            mLoadingBar.setMessage("Please Wait while we are Loging-In  User!");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {
                        SendToMainActivity();
                        Toast.makeText(getApplicationContext()," User Logged in SuccessFully",Toast.LENGTH_LONG).show();
                        mLoadingBar.dismiss();
                    }
                    else
                    {
                        String Error=task.getException().toString();
                        Toast.makeText(getApplicationContext(),"Error "+Error,Toast.LENGTH_LONG).show();
                        mLoadingBar.dismiss();
                    }
                }
            });
        }

    }



/*
    private void signIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
*/

/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(result.isSuccess())
            {
                GoogleSignInAccount account=result.getSignInAccount();
               //mmji firebaseAuthWithGoogle(account);
            }
        }

    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount idToken)
    {
        idToken.getId();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            SendToMainActivity();
                            Toast.makeText(getApplicationContext(),"SuccessFully Sined In",Toast.LENGTH_LONG).show();

                        }
                        else
                            {
                                Toast.makeText(getApplicationContext(),"Error : "+task.getException().toString(),Toast.LENGTH_LONG).show();
                            }

                    }
                });

    }


*/

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            SendToMainActivity();
        }
    }

}