package fyp.ui_activities;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import fyp.adapters.PastScanAdapter;
import fyp.model.TroubleCodes;

public class PastScanpage extends AppCompatActivity {
    DrawerLayout drawerLayout;
    RecyclerView pastscansdpt;
    ProgressDialog sd;
    ArrayList<TroubleCodes> pastscan = new ArrayList<>();

    FirebaseFirestore fs = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_scanpage);

        //initialising recyclerview to respective id
        pastscansdpt = findViewById(R.id.pastscan_listview);
        pastscansdpt.setLayoutManager(new LinearLayoutManager(PastScanpage.this));

        sd = new ProgressDialog(this);
        sd.setTitle("Fetching Scan Results");
        sd.show();

        setUpToolbar();
        getData();
    }

    //method to receive the data
    public void getData() {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        ArrayList<String> faultcodes = new ArrayList<>();
        ArrayList<String> faultdesc = new ArrayList<>();
        final String[] dtc = {" "};
        final String[] dc = {" "};
        final String[] date = {" "};

        if (fuser != null) {
            String email = fuser.getEmail();
            //Firebase Firestore reference
            CollectionReference fstore = fs.collection("Diagnostics").document(email).collection("Date");
            //Firestore querying
            fstore.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshot) {
                    sd.dismiss();
                    if (!documentSnapshot.isEmpty()) {
                        for (DocumentSnapshot ds : documentSnapshot) {
                            //add array into arraylist
                            faultcodes.add(ds.get("Code").toString());
                            faultdesc.add(ds.get("Description").toString());
                            //iterate arraylist and add into string array
                            for (int j = 0; j < faultcodes.size(); j++) {
                                dtc[0] = faultcodes.get(j);
                                dc[0] = faultdesc.get(j);
                                date[0] = ds.getId();
                            }
                            TroubleCodes pastsc = new TroubleCodes(date[0], dtc[0], dc[0]);
                            pastscan.add(pastsc);
                        }
                        pastscansdpt.setAdapter(new PastScanAdapter(pastscan, PastScanpage.this));
                    } else {
                        TroubleCodes nosc = new TroubleCodes("No Diagnosis Scans", null, null);
                        pastscan.add(nosc);
                        pastscansdpt.setAdapter(new PastScanAdapter(pastscan, PastScanpage.this));
                    }
                }
            });
        }

    }

    //disable physical back button
    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    //sets up toolbar
    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}

