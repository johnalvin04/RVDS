package fyp.ui_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Homepage extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    ProgressDialog dd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        setUpToolbar();

        //setting progress dialog and title
        dd =  new ProgressDialog(this);
        dd.setTitle("Deleting User Profile and Diagnostic Data");

        navigationView = findViewById(R.id.nav_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.resetpassworditem:
                        redirectActivity(Homepage.this,Resetpassword.class);
                        break;

                    case R.id.aboutus:
                        redirectActivity(Homepage.this,AboutUspage.class);
                        break;

                    case R.id.delete:
                        delete(Homepage.this);
                        break;

                    case R.id.logout:
                        logout(Homepage.this);
                        break;
                }
                return false;
            }
        });
    }


    private void setUpToolbar(){
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle  = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.homepageheader,R.string.homepageheader);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        checkCurrenUser();
    }

    public static void redirectActivity(Activity activity, Class classname){
        Intent intent = new Intent(activity, classname);
        activity.startActivity(intent);
    }

    //logout method
    public void logout(final Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Logout");
        builder.setMessage("Confirm Logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent logout = new Intent(activity, Login.class);
                Toast.makeText(activity,"Username: "+ FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+ "\nLogout Successful",Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logout);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    //delete profile method
    public void delete(final Activity activity){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setTitle("Delete Profile");
        builder1.setMessage("Are you sure that you want to delete this profile?");
        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dd.show();
                FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                String email = fuser.getEmail();
                fs.collection("Diagnostics").document(email).delete();
                deleteuser(fuser);
            }
        });
        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder1.show();
    }

    //delete Firebase user
    public void deleteuser(FirebaseUser fuser){
        fuser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dd.dismiss();
                Intent logout = new Intent(Homepage.this, Login.class);
                startActivity(logout);
                Toast.makeText(Homepage.this, "Profile Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //checks current user and displays name in navigation menu
    public void checkCurrenUser() {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(fuser !=null){
            navigationView = findViewById(R.id.nav_menu);
            View view = navigationView.getHeaderView(0);
            TextView displayname = view.findViewById(R.id.displayname_textview);
            String name = fuser.getDisplayName();
            displayname.setText("Welcome! "+ name);
        }
        else{
//            Toast.makeText(getApplicationContext(), "No User Logged In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        }
    }

    public void Dashboardpage(View view) {
        redirectActivity(Homepage.this,Dashboard.class);
    }

    public void Scanpage(View view) {
            redirectActivity(Homepage.this,Scanpage.class);
    }

    public void PastScanpage(View view) {
        redirectActivity(Homepage.this,PastScanpage.class);
    }
}
