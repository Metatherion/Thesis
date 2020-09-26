package com.example.andtest2;

import androidx.annotation.NonNull;
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
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ExerciseActivity extends AppCompatActivity
{

    private static final String TAG = "Solent";
    private FitnessOptions fitnessOptions= FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .build();

    public GoogleSignInAccount getGoogleAccount()
    {
        //Returns the currently signed in google account
        return GoogleSignIn.getAccountForExtension(this,fitnessOptions);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        Log.d("Solent", "Page established");

        accessGoogleFit();

    }

    private void accessGoogleFit() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(this, getGoogleAccount())
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG, "Successfully Subscribed");
                        }
                        else
                        {
                            Log.d(TAG, "Error with subscription");
                        }
                    }
                });


        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, -30);
        long startTime = cal.getTimeInMillis();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();

        GoogleSignInAccount account = GoogleSignIn
                .getAccountForExtension(this, fitnessOptions);

        Fitness.getHistoryClient(this, account)
                .readData(readRequest)
                .addOnSuccessListener(response -> {
                    // Use response data here

                    Log.d(TAG, "OnSuccess()");
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "OnFailure()", e);
                });

    }
    public void updateStepCount(View v)
    {
        Log.d(TAG, "Updating Step Count");
        Fitness.getHistoryClient(this,getGoogleAccount())
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener( dataSet ->
                {
                    Integer total;
                    if (dataSet.isEmpty()) {
                        Log.d(TAG, "Data set empty, setting to 0");
                        total = 0;
                    } else {
                        Log.d(TAG, "Data available");
                        total = dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                    }
                    TextView myTextView = (TextView)findViewById(R.id.stepResult);
                    String mytext = total.toString();
                    myTextView.setText(mytext);
                })
                .addOnFailureListener( e ->
                {
                    Log.d(TAG, "There was an error getting the step count");
                });
    }
}