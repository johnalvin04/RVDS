package fyp.ui_activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sohrab.obd.reader.application.Preferences;
import com.sohrab.obd.reader.obdCommand.ObdCommand;
import com.sohrab.obd.reader.obdCommand.ObdConfiguration;
import com.sohrab.obd.reader.obdCommand.control.TroubleCodesCommand;
import com.sohrab.obd.reader.service.ObdReaderService;
import com.sohrab.obd.reader.trip.TripRecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fyp.adapters.DTCadapter;
import fyp.model.ClearTroubleCodes;
import fyp.model.TroubleCodes;

import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_CONNECTION_STATUS_MSG;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA;

public class Scanpage extends AppCompatActivity {
    DrawerLayout drawerLayout;
    RecyclerView dtccode;
    ProgressDialog pd,sd,cd;

    ArrayList<TroubleCodes> listTroubleCodes = new ArrayList<>();

    FirebaseFirestore fs = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanpage);
        dtccode = findViewById(R.id.scan_listview);
        dtccode.setLayoutManager(new LinearLayoutManager(Scanpage.this));

        pd = new ProgressDialog(this);
        sd = new ProgressDialog(this);
        cd = new ProgressDialog(this);

        pd.setTitle("Scanning For Fault Codes");
        sd.setTitle("Saving Fault Codes");
        cd.setTitle("Clearing Fault Codes");
        pd.show();

        setUpToolbar();

        stopService(new Intent(this, ObdReaderService.class));

        //obd commands into an array
        ArrayList<ObdCommand> obdCommands = new ArrayList<>();
        obdCommands.add(new TroubleCodesCommand());
        ObdConfiguration.setmObdCommands(this, obdCommands);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_READ_OBD_REAL_TIME_DATA);
        intentFilter.addAction(ACTION_CONNECTION_STATUS_MSG);
        registerReceiver(mObdReaderReceiver, intentFilter);

        startService(new Intent(this, ObdReaderService.class));
    }

    private final BroadcastReceiver mObdReaderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(ACTION_CONNECTION_STATUS_MSG)) {
                String connectionStatusMsg = intent.getStringExtra(ObdReaderService.INTENT_EXTRA_DATA);

                if (connectionStatusMsg.equals(getString(R.string.connected_ok))) {
                    Toast.makeText(context, "OBD Connected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, connectionStatusMsg, Toast.LENGTH_SHORT).show();
                }
            } else if (action.equals(ACTION_READ_OBD_REAL_TIME_DATA)) {
                pd.dismiss();
                TripRecord tripRecord = TripRecord.getTripRecode(Scanpage.this);
                String value = tripRecord.getmFaultCodes().replaceAll("\n", "").trim();

                stopService(new Intent(Scanpage.this, ObdReaderService.class));

                if (!value.isEmpty())
                {
                    int size = value.length();
                    int loop = size/5;
                    int x = 0;
                    for(int i = 0; i<loop;i++){
                        int y = x + 5;
                        String dtccodes = value.substring(x,y);
                        String description = TroubleCodes.getFaultCode(dtccodes);
                        TroubleCodes faults;
                        if(description == null){
                            faults = new TroubleCodes(dtccodes, "Manufacturing Specific Fault Code");
                        }
                        else{
                            faults = new TroubleCodes(dtccodes, description);
                        }
                        listTroubleCodes.add(faults);
                        x = x+ 5;
                    }
                    dtccode.setAdapter(new DTCadapter(listTroubleCodes, Scanpage.this));
                }
                else {
                    TroubleCodes nofaults = new TroubleCodes("No Fault Codes Stored", null);
                    listTroubleCodes.add(nofaults);
                    dtccode.setAdapter(new DTCadapter(listTroubleCodes, Scanpage.this));
                }
            }
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        return true;
    }

    //option selection menu
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                savecode();
                break;
            case R.id.clearcodes:
                clearcode(this);
                break;
        }
        return false;
    }

    //saving results method
    public void savecode() {
        sd.show();
        Map<String,List<String>> diagnostics = new HashMap<>();
        String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        ArrayList<String> listFaults=new ArrayList<>();
        ArrayList<String> listDescription=new ArrayList<>();
        if(fuser !=null){
            String email = fuser.getEmail();
            for (int i = 0; i < listTroubleCodes.size();i++) {
                String faultCodes = listTroubleCodes.get(i).getTroubleCode();
                String description = listTroubleCodes.get(i).getDescription();
                listFaults.add(faultCodes);
                listDescription.add(description);

            }
            diagnostics.put("Code",listFaults);
            diagnostics.put("Description",listDescription);
            fs.collection("Diagnostics").document(email).collection("Date").document(date).set(diagnostics).addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid) {
                    sd.dismiss();
                    Toast.makeText(Scanpage.this, "Scan Results Saved!", Toast.LENGTH_SHORT).show();
                    Intent saveintent = new Intent(Scanpage.this, Homepage.class);
                    startActivity(saveintent);
                }
            });
        }
    }

    //clearing dtc codes method
    public void clearcode(final Activity activity) {
        onDestroy();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Clear DTC Codes");
        builder.setMessage("Confirm Clear?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cd.show();
                ArrayList<ObdCommand> obdCommand = new ArrayList<>();
                obdCommand.add(new ClearTroubleCodes());
                ObdConfiguration.setmObdCommands(Scanpage.this, obdCommand);

                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ACTION_CONNECTION_STATUS_MSG);
                registerReceiver(mObdReaderReceiver, intentFilter);

                startService(new Intent(Scanpage.this, ObdReaderService.class));

                cd.dismiss();

                Toast.makeText(getApplicationContext(), "Fault Codes cleared", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Scanpage.this, Homepage.class);
                startActivity(intent);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregister receiver
        unregisterReceiver(mObdReaderReceiver);
        //stop service
        stopService(new Intent(this, ObdReaderService.class));
        // This will stop background thread if any running immediately.
        Preferences.get(this).setServiceRunningStatus(false);
    }
}
