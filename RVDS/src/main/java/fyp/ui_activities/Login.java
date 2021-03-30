package fyp.ui_activities;

// Coded by : John Alvin Joseph

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Login extends AppCompatActivity {

    CardView login,verify;
    String email,password;
    TextInputEditText emailedit,passedit;
    TextView registerbutton,forgotpasswordbutton;
    ProgressDialog ld;
    FirebaseAuth fAuth =  FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialisation of variable to respective id's
        login = findViewById(R.id.login_button);
        verify = findViewById(R.id.verify_button);
        verify.setVisibility(View.GONE);

        registerbutton = findViewById(R.id.newuser_button);
        forgotpasswordbutton = findViewById(R.id.forgotpassword_button);

        emailedit = findViewById(R.id.email_textfield);
        passedit = findViewById(R.id.password_textfield);

        ld = new ProgressDialog(this);
        ld.setTitle("Signing In");

        //login button checks username and password from the textfield
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailedit.getText().toString().trim();
                password = passedit.getText().toString().trim();
                if(email.isEmpty()){
                    emailedit.setError("Email address cannot be empty!");
                }
                else if(password.isEmpty()){
                    passedit.setError("Password cannot be empty!");
                }
                else{
                    ld.show();
                    validatelogin(email,password);
                }
            }
        });

        //register button directs to register page
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        //forgotpassword button directs to resetpassword page
        forgotpasswordbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailedit.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Login.this, "Email Address need to be filled in to Reset Password", Toast.LENGTH_SHORT).show();
                }else{
                    forgotpassword(email);
                }
            }
        });

    }

    //redirect to Home page
    private void openhomepage (){
        Intent intent= new Intent(Login.this,  Homepage.class);
        startActivity(intent);
    }

    //redirect to Register page
    private void register(){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    //validate login information with firebase
    private void validatelogin(String email, String password) {
        //check if username exists
       fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               ld.dismiss();
               if(task.isSuccessful()){
                   FirebaseUser fuser = fAuth.getCurrentUser();
                   updateUi(fuser);
               }
               else{
                   Toast.makeText(Login.this,"Error Occurred"+ "\n"+ Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
               }
           }
       });
    }

    //called when successful login
    private void loginsuccess(){
        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
        openhomepage();
    }

    //send forgot password email method
    private void forgotpassword(String email){
        fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Login.this, "Reset Password Email is Sent", Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    finish();
                    startActivity(getIntent().addFlags(intent.FLAG_ACTIVITY_NO_ANIMATION));
                }
                else{
                    Toast.makeText(Login.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //updates UI accordingly
    private void updateUi(FirebaseUser fuser){
        if (fuser != null) {
            if (fuser.isEmailVerified()) {
                loginsuccess();
            } else {
                Toast.makeText(getApplicationContext(), "Email is not Verified Yet!", Toast.LENGTH_SHORT).show();
                login.setVisibility(View.GONE);
                verify.setVisibility(View.VISIBLE);
                verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fuser.sendEmailVerification();
                        Toast.makeText(getApplicationContext(), "Verification Email sent", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        login.setVisibility(View.VISIBLE);
                        verify.setVisibility(View.GONE);
                    }
                });
            }
        } else {
        }
    }

    //system exit when backpressed
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}