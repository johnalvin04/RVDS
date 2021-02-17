package fyp.ui_activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import fyp.adapters.PastScanAdapter;
import fyp.model.TroubleCodes;

public class PastScanpage extends AppCompatActivity {
    DrawerLayout drawerLayout;
    RecyclerView pastscansdpt;
    ProgressDialog rd;

    ArrayList<TroubleCodes>pastscan = new ArrayList<>();

    FirebaseFirestore fs = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_scanpage);
        pastscansdpt = findViewById(R.id.pastscan_listview);
        pastscansdpt.setLayoutManager(new LinearLayoutManager(PastScanpage.this));

        rd = new ProgressDialog(this);
        rd.setTitle("Fetching Scan Results");
        //rd.show();

        setUpToolbar();
        getData();
    }

    //method to receive the data
    public void getData(){
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        //rd.dismiss();
        if(fuser !=null) {
            String email = fuser.getEmail();
            CollectionReference fstore = fs.collection("Diagnostics").document(email).collection("Date");
            fstore.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshot) {
                    if(!documentSnapshot.isEmpty()){
                        for (DocumentSnapshot documentSnapshots : documentSnapshot.getDocuments()) {
                            String date = documentSnapshots.getId();
                            String faultcodes = documentSnapshots.getString("Code");
                            String description = documentSnapshots.getString("Description");
                            TroubleCodes pastsc = new TroubleCodes(date,faultcodes,description);
                            pastscan.add(pastsc);
                        }
                        pastscansdpt.setAdapter(new PastScanAdapter(pastscan, PastScanpage.this));
                    }
                    else{
                        TroubleCodes nosc = new TroubleCodes("No Diagnosis Scans",null, null);
                        pastscan.add(nosc);
                        pastscansdpt.setAdapter(new PastScanAdapter(pastscan, PastScanpage.this));
                    }
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}

