package com.example.andtest2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;


public class MainActivity extends AppCompatActivity {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 1;
    private Intent i;
    private String androidPermissions;
    private FitnessOptions fitnessOptions;
    public GoogleSignInAccount getGoogleAccount()
    {
        //Returns the currently signed in google account
        return GoogleSignIn.getAccountForExtension(this,fitnessOptions);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((SeekBar) findViewById(R.id.mentalHealthRatingSlider)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Integer maxprogress = ((SeekBar) findViewById(R.id.mentalHealthRatingSlider)).getMax();
                if (progress <= maxprogress / 5) {
                    findViewById(R.id.mentalHealthTextView).setBackgroundColor(getResources().getColor(R.color.healthy));
                } else if (progress > maxprogress / 5 && progress <= 2 * maxprogress / 5) {
                    findViewById(R.id.mentalHealthTextView).setBackgroundColor(getResources().getColor(R.color.SlightlyStressed));
                } else if (progress > 2 * maxprogress / 5 && progress <= 3 * maxprogress / 5) {
                    findViewById(R.id.mentalHealthTextView).setBackgroundColor(getResources().getColor(R.color.Stressed));
                } else if (progress > 3 * maxprogress / 5 && progress < 4 * maxprogress / 5) {
                    findViewById(R.id.mentalHealthTextView).setBackgroundColor(getResources().getColor(R.color.HighStress));
                } else {
                    findViewById(R.id.mentalHealthTextView).setBackgroundColor(getResources().getColor(R.color.Unhealthy));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        TextView mentalTextView = findViewById(R.id.mentalHealthTextView);
    }

    public void launchSleep(View v)
    {
        i = new Intent(this, SleepActivity.class);
    androidPermissions = Manifest.permission.ACTIVITY_RECOGNITION; // set the permission variable to required permissions
    fitnessOptions = FitnessOptions.builder() // sets the fitnessOptions varable to the required google permissions
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT)
            .build();
    checkPermissionsAndRun();
    }

    public void launchActivity(View v)
    {
        //checkAndroidPermissions("TODO");

        Intent i = new Intent(this, ActivityActivity.class);
        startActivity(i);
    }

    public void launchMood(View v)
    {
        //checkAndroidPermissions("TODO");

        Intent i = new Intent(this, MoodActivity.class);
        startActivity(i);
    }

    public void launchBehaviour(View v)
    {
        //checkAndroidPermissions("TODO");

        Intent i = new Intent(this, BehaviourActivity.class);
        startActivity(i);
    }

    public void launchExercise(View v)
    {
        i = new Intent(this, ExerciseActivity.class); //Set the intent variable to the desired activity
        androidPermissions = Manifest.permission.ACTIVITY_RECOGNITION; // set the permission variable to required permissions
        fitnessOptions = FitnessOptions.builder() // sets the fitnessOptions varable to the required google permissions
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build();
        checkPermissionsAndRun(); // Checks all needed permissions

    }

    public void launchDiet(View v) {
        //checkAndroidPermissions("TODO");

        Intent i = new Intent(this, DietActivity.class);
        startActivity(i);
    }

    public void launchSpiritual(View v) {
        //checkAndroidPermissions("TODO");

        Intent i = new Intent(this, SpiritualActivity.class);
        startActivity(i);
    }


    public void checkPermissionsAndRun()
    {
        if (androidPermissionApproved()) // Check if Android permissions are already granted
        {
            Log.d("Solent", "Android Permissions Granted");
            Log.d("Solent", "Continuing to Google Permissions");
            checkGooglePermissions(); // If yes continue to google permissions
        }
        else
        {
            Log.d("Solent", "Android Permissions Failure");
            requestAndroidPermissions(); // If no request permissions
        }
    }
    public boolean androidPermissionApproved()
    {
        // returns true if android permissions are granted
        return ContextCompat.checkSelfPermission(this, androidPermissions) == PackageManager.PERMISSION_GRANTED;

    }
    public void requestAndroidPermissions()
    {
        switch (androidPermissions) {
            case Manifest.permission.ACTIVITY_RECOGNITION:
                Log.d("Solent", "Requesting...");
                ActivityCompat.requestPermissions(this,
                        new String[]{androidPermissions},
                        MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION);
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        Log.d("Solent", "Android Permission Request Results Processing");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length == 0) {
                    Log.d("Solent", "Request interrupted");

                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    Log.d("Solent", "User Has Granted Android Permission");
                    checkGooglePermissions();
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Log.d("Solent", "User Has Denied Android Permissions. Returning to Main Page");
                }
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }
    public void checkGooglePermissions()
    {
        if(googlePermissionsApproved())
        {
            //Logs a message then launches activity
            Log.d("Solent", "Google Permission Granted");
            Log.d("Solent" , "All Permissions Approved. Launching Activity");
            startActivity(i);
        }
        else
        {
            // logs a message then requests google permission
            Log.d("Solent", "Google Permissions Not Given");
            requestGooglePermissions();
        }
    }
    public Boolean googlePermissionsApproved()
    {
        // returns true if permissions have been granted
        return GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions);
    }
    public void requestGooglePermissions()
    {
        // requests the desired google permissions
        GoogleSignIn.requestPermissions(
                this, // your activity
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, // e.g. 1
                getGoogleAccount(),
                fitnessOptions);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //processes the result of the google permissions request
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Solent", "Google Permission Request Result");

        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                Log.d("Solent", "Google Permission now Granted");
                startActivity(i);
            }
            else
            {
                Log.d("Solent", "Unrecognised Google Permission Code");
            }
        }
        else
        {
            // Explain to the user that the feature is unavailable because
            // the features requires a permission that the user has denied.
            // At the same time, respect the user's decision. Don't link to
            // system settings in an effort to convince the user to change
            // their decision.
            Log.d("Solent", "User Has Denied Google Permissions");
        }
    }

}