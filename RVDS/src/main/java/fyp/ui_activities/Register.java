 package fyp.ui_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

 public class Register extends AppCompatActivity {
    CardView register;
    String username,email, password,repassword;
    TextInputEditText useredit,emailedit,passedit,repassedit;

    AwesomeValidation validation;
    DrawerLayout drawerLayout;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setUpToolbar();

        //initialisation of variables
        register = findViewById(R.id.register_button);

        useredit = findViewById(R.id.username_textfield);
        emailedit = findViewById(R.id.email_textfield);
        passedit = findViewById(R.id.password_textfield);
        repassedit = findViewById(R.id.repassword_textfield);

        //register new user
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = useredit.getText().toString().trim();
                email = emailedit.getText().toString().trim();
                password = passedit.getText().toString().trim();
                repassword = repassedit.getText().toString().trim();

                //validate if password and username contains any characters besides alphanumerical
                //checks if password and retype password are the same
                if(validate(password,repassword,username)){
                    saveuser(email,password);
                }
            }
        });

    }

    //validation method
    private boolean validate(String password, String repassword, String username) {
        validation = new AwesomeValidation(ValidationStyle.BASIC);
        validation.addValidation(this, R.id.username_textfield, "^[\\.a-zA-Z0-9,!? ]*$", R.string.inputusername);
        validation.addValidation(this, R.id.password_textfield, "^[a-zA-Z0-9]+$", R.string.inputpassword);
        validation.addValidation(this, R.id.repassword_textfield, "^[a-zA-Z0-9]+$", R.string.inputrepassword);

        if (!validation.validate()){
            return false;
        }
        else if(TextUtils.isEmpty(username)){
            useredit.setError("Username cannot be empty!");
            return false;
        }
        else if(TextUtils.isEmpty(email)){
            emailedit.setError("Email is Required!");
            return false;
        }
        else if(password.length() <6){
            passedit.setError("Password must be >= 6 characters!");
            return false;
        }
        else if (!repassword.equals(password)){
            repassedit.setError("Passwords don't match!");
            return false;
        }
        else {
            return true;
        }
    }

    //saves user into Firebase
    private void saveuser( String email, String password){
        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser fuser  = fAuth.getCurrentUser();
                    UserProfileChangeRequest profileupdate = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                    fuser.updateProfile(profileupdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            fuser.sendEmailVerification();
                            Toast.makeText(Register.this,"Account Created\n"+fuser.getDisplayName()+"\nVerification Email Sent",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Register.this, Login.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
                else{
                    Toast.makeText(Register.this,"Error Occurred"+ "\n"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //disable physical backpress
     public void onBackPressed() {
         moveTaskToBack(false);
     }

     //sets up toolbar
     public void setUpToolbar(){
         drawerLayout = findViewById(R.id.drawer_layout);
         Toolbar toolbar = findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);
         getSupportActionBar().setDisplayShowHomeEnabled(true);
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     }
}