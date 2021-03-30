package fyp.ui_activities;

// Coded by : John Alvin Joseph

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.sohrab.obd.reader.application.Preferences;
import com.sohrab.obd.reader.obdCommand.ObdCommand;
import com.sohrab.obd.reader.obdCommand.ObdConfiguration;
import com.sohrab.obd.reader.obdCommand.SpeedCommand;
import com.sohrab.obd.reader.obdCommand.control.DistanceMILOnCommand;
import com.sohrab.obd.reader.obdCommand.control.ModuleVoltageCommand;
import com.sohrab.obd.reader.obdCommand.engine.LoadCommand;
import com.sohrab.obd.reader.obdCommand.engine.MassAirFlowCommand;
import com.sohrab.obd.reader.obdCommand.engine.RPMCommand;
import com.sohrab.obd.reader.obdCommand.temperature.AirIntakeTemperatureCommand;
import com.sohrab.obd.reader.obdCommand.temperature.EngineCoolantTemperatureCommand;
import com.sohrab.obd.reader.service.ObdReaderService;
import com.sohrab.obd.reader.trip.TripRecord;

import java.util.ArrayList;

import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_CONNECTION_STATUS_MSG;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA;


public class Dashboard extends AppCompatActivity {
    DrawerLayout drawerLayout;
    TextView speed, rpm, load, coolant, battery, mil, connect;
    ProgressDialog dg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setUpToolbar();

        //initialisation of variable to respective id's
        speed = findViewById(R.id.speeddisplay);
        rpm = findViewById(R.id.rpmdisplay);
        load = findViewById(R.id.loaddisplay);
        coolant = findViewById(R.id.coolantdisplay);
        battery = findViewById(R.id.batterydisplay);
        mil = findViewById(R.id.mildisplay);
        connect = findViewById(R.id.connectstatus);

        dg = new ProgressDialog(this);

        dg.setTitle("Fetching Data from Vehicle");
        dg.show();

        //passing obd commands to an array
        ArrayList<ObdCommand> obdCommands = new ArrayList<>();
        obdCommands.add(new SpeedCommand()); //speed
        obdCommands.add(new RPMCommand()); //rpm
        obdCommands.add(new LoadCommand()); //engine load
        obdCommands.add(new EngineCoolantTemperatureCommand()); //coolant temp
        obdCommands.add(new ModuleVoltageCommand()); //fuel pressure
        obdCommands.add(new AirIntakeTemperatureCommand()); //oil temp
        obdCommands.add(new MassAirFlowCommand()); //MAF
        obdCommands.add(new DistanceMILOnCommand()); //MIL distance
        ObdConfiguration.setmObdCommands(this, obdCommands);

        //intent filter to start the service
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_READ_OBD_REAL_TIME_DATA);
        intentFilter.addAction(ACTION_CONNECTION_STATUS_MSG);
        registerReceiver(mObdReaderReceiver, intentFilter);

        //uses the OBD Reader service
        startService(new Intent(this, ObdReaderService.class));

    }

    //bluetooth receiver method
    private final BroadcastReceiver mObdReaderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(ACTION_CONNECTION_STATUS_MSG)) {
                String connectionStatusMsg = intent.getStringExtra(ObdReaderService.INTENT_EXTRA_DATA);
                connect.setText(connectionStatusMsg);

                if (connectionStatusMsg.equals(getString(R.string.connected_ok))) {
                    Toast.makeText(context, "OBD Connected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, connectionStatusMsg, Toast.LENGTH_SHORT).show();
                }
            } else if (action.equals(ACTION_READ_OBD_REAL_TIME_DATA)) {
                dg.dismiss();
                TripRecord tripRecord = TripRecord.getTripRecode(Dashboard.this);

                if (tripRecord.getSpeed() >= 80) {
                    speed.setTextColor(Color.RED);
                }
                else{
                    speed.setTextColor(Color.WHITE);
                }
                speed.setText(String.valueOf(tripRecord.getSpeed())); //display speed
                rpm.setText(tripRecord.getEngineRpm()); //display rpm
                load.setText(tripRecord.getmEngineLoad());//display engine load
                if (tripRecord.getmEngineCoolantTemp() != null) {
                    coolant.setText(tripRecord.getmEngineCoolantTemp());//display coolant temp
                }
                if (tripRecord.getmControlModuleVoltage() != null) {
                    if (Integer.parseInt(tripRecord.getmControlModuleVoltage().substring(0, 2)) <= 11) {
                        battery.setTextColor(Color.RED);
                        Toast.makeText(context, "Battery Voltage Low!", Toast.LENGTH_SHORT).show();
                    }
                    battery.setText(tripRecord.getmControlModuleVoltage()); //display battery voltage
                    mil.setText(tripRecord.getmDistanceTraveledMilOn());//display distance of MIL on
                }
            }
        }
    };

    //sets up toolbar
    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //stops the service when the activity changes
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

    //disable back physical button
    public void onBackPressed() {
        moveTaskToBack(false);
    }

}