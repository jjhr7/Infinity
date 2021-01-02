package com.example.infinitycrop.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.LoadAnimations.IntroductoryActivity2;
import com.example.infinitycrop.ui.list_machine.MainActivityMachineList;
import com.example.infinitycrop.ui.logmail.LoginActivity;
import com.example.infinitycrop.ui.logmail.RegisterActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class FragmentLog extends Fragment {
    /*private GoogleApiClient googleApiClient;
     private SignInButton signInButton;
     public static final int SIGN_IN_CODE = 777;*/
    //Variable para gestionar FirebaseAuth
    private FirebaseAuth mAuth;
    //Agregar cliente de inicio de sesión de Google
    private GoogleSignInClient mGoogleSignInClient;
    //Constante
    int RC_SIGN_IN = 1;
    SignInButton btnGoogle;
    String TAG = "GoogleSignIn";
    View contentLog;
    Button btnLogin;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    private LoginButton loginButtonFacebook;
    private CallbackManager mCallbackManager;
    ViewPager viewPager;
    ImageView imglogo;
    private  static  final String TAG2 = "FacebookAuthentication";


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        final View root =inflater.inflate(R.layout.fragment_logr,container,false);
        //Botones
         btnGoogle =  root.findViewById(R.id.btn_google);
         btnGoogle.setVisibility(View.GONE);
        imglogo = root.findViewById(R.id.logimg);
        imglogo.setVisibility(View.GONE);
        loginButtonFacebook = root.findViewById(R.id.btn_facebook);
        loginButtonFacebook.setVisibility(View.GONE);
        loginButtonFacebook.setReadPermissions("email","public_profile");
        contentLog = root.findViewById(R.id.content);
        contentLog.setVisibility(View.GONE);
        btnLogin = root.findViewById(R.id.btn_mail);
        btnLogin.setVisibility(View.GONE);


        //animar log
        contentLog.setVisibility(View.VISIBLE);
        CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {//Se pone la cuenta atras para que no se superponga con la animación
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                btnGoogle.setVisibility(View.VISIBLE);
                loginButtonFacebook.setVisibility(View.VISIBLE);
                imglogo.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.VISIBLE);
            }
        }.start();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),
                        LoginActivity.class);
                startActivity(intent);
            }
        });


        //Listeners
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // Configurar Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Crear un GoogleSignInClient con las opciones especificadas por gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity().getApplication(), gso);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        //Facebook
        mCallbackManager = CallbackManager.Factory.create();
        loginButtonFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG2,"onSuccess"+loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG2,"onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG2,"onError: "+error);
            }

        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    Intent dashboardActivity = new Intent(getActivity().getApplicationContext(), MainActivityMachineList.class);
                    startActivity(dashboardActivity);
                    getActivity().onBackPressed();//es como poner this.activity.finish()
                }
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null){
                    mAuth.signOut();
                }
            }
        };

        return root;

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode,resultCode,data);

        super.onActivityResult(requestCode, resultCode, data);
        //Resultado devuelto al iniciar el Intent de GoogleSignInApi.getSignInIntent (...);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(task.isSuccessful()){
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In fallido, actualizar GUI
                    Log.w(TAG, "Google sign in failed", e);
                }
            }else{
                Log.d(TAG, "Error, login no exitoso:" + task.getException().toString());
                Toast.makeText(getActivity().getApplicationContext(), "Ocurrio un error. "+task.getException().toString(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseFirestore db=FirebaseFirestore.getInstance();
                            String userID= mAuth.getCurrentUser().getUid();
                            String name= mAuth.getCurrentUser().getDisplayName();
                            String mail= mAuth.getCurrentUser().getEmail();
                            Map<String, Object> data = new HashMap<>();
                            data.put("username", name);
                            data.put("mail", mail);
                            db.collection("Usuarios").document(userID)
                                    .set(data);
                            /*db.collection("Usuarios").document(userID)
                                    .collection("Maquinas").document("fecha")
                                    .set(data);*/

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //Iniciar DASHBOARD u otra actividad luego del SigIn Exitoso
                            Intent dashboardActivity = new Intent(getActivity().getApplicationContext(), IntroductoryActivity2.class);
                            startActivity(dashboardActivity);
                            getActivity().onBackPressed();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }
    @Override
    public void onStart() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){ //si no es null el usuario ya esta logueado
            //mover al usuario al dashboard
            Intent dashboardActivity = new Intent(getActivity().getApplicationContext(), MainActivityMachineList.class);
            startActivity(dashboardActivity);
        }
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    //metodo para navegar entre activitys con los botones


    //Funciones Login con facebook
    private void handleFacebookToken(AccessToken token){
        Log.d(TAG2,"handleFacebookToken" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG2,"sing in with credential: successfully");
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user!=null){ //si no es null el usuario ya esta logueado
                        Intent dashboardActivity = new Intent(getActivity().getApplication(), MainActivityMachineList.class);
                        startActivity(dashboardActivity);
                    }
                }else{
                    Log.d(TAG2,"sing in with credential: failed", task.getException());
                    Toast.makeText(getActivity(),"Authentication Failed", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
    @Override
    public void onStop() {
        super.onStop();
        if(authStateListener != null){
            mAuth.removeAuthStateListener(authStateListener);
            getActivity().finish();
        }
    }

}
