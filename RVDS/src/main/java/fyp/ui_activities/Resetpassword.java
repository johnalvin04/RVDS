package fyp.ui_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Resetpassword extends AppCompatActivity {
    CardView update;
    String password,repassword;
    TextInputEditText passedit,repassedit;
    TextInputLayout passlayout,repasslayout;

    AwesomeValidation validation;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        setUpToolbar();

        update = findViewById(R.id.updatepassword_button);

        passlayout = findViewById(R.id.password_textInput);
        repasslayout = findViewById(R.id.repassword_textInput);

        passedit = findViewById(R.id.password_textfield);
        repassedit = findViewById(R.id.repassword_textfield);

        //updates the new password to the database
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = passedit.getText().toString().trim();
                repassword = repassedit.getText().toString().trim();

                if(validate()){
                    checkCurrenUser(password);
                }
            }
        });
    }
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    public void setUpToolbar(){
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    //check login existence of current user
    public void checkCurrenUser(String password) {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(fuser !=null){
            updatepassword(password,fuser);
        }
        else{
            Toast.makeText(getApplicationContext(), "No User Logged In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,Login.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean validate() {
        validation = new AwesomeValidation(ValidationStyle.BASIC);
        validation.addValidation(this, R.id.password_textfield, "^[a-zA-Z0-9]+$", R.string.inputpassword);
        validation.addValidation(this, R.id.repassword_textfield, "^[a-zA-Z0-9]+$", R.string.inputrepassword);

        if (!validation.validate()){
            return false;
        }
        else if(password.length() <6){
            passedit.setError("Password must be >= 6 characters!");
            return false;
        }
        else if(!repassword.equals(password)){
            repassedit.setError("Passwords don't match!");
            return false;
        }
        else {
            return true;
        }
    }

    //updates the new password once the email is verified
    private void updatepassword(String password, FirebaseUser fuser){
        fuser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Resetpassword.this,"Password Updated",Toast.LENGTH_SHORT).show();
                backtohome();
            }
        });
    }

    private void backtohome(){
        Intent intent= new Intent(Resetpassword.this, Homepage.class);
        startActivity(intent);
        finish();
    }
}