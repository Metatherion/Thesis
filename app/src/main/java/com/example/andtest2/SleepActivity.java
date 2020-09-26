package com.example.andtest2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResponse;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SleepActivity extends AppCompatActivity {

    private static final String TAG = "Solent";
    private FitnessOptions fitnessOptions= FitnessOptions.builder()
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT)
            .build();
    public GoogleSignInAccount getGoogleAccount()
    {
        //Returns the currently signed in google account
        return GoogleSignIn.getAccountForExtension(this,fitnessOptions);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        Log.d(TAG, "Page established");

        accessGoogleFit();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void accessGoogleFit()
    {
        Log.d(TAG, "Acessing Sleep Data");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR, -24);
        long startTime = cal.getTimeInMillis();

        SessionReadRequest request = new SessionReadRequest.Builder()
                .readSessionsFromAllApps()
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .setTimeInterval(startTime,endTime, TimeUnit.MILLISECONDS)
                .build();

        Task<SessionReadResponse> task = Fitness.getSessionsClient(this, getGoogleAccount()).readSession(request);

        task.addOnSuccessListener(response ->
        {
            Log.d(TAG, "On Success");
            // Filter the resulting list of sessions to just those that are sleep.
            List<Session> sleepSessions = response.getSessions().stream()
                    .filter(s -> s.getActivity().equals(FitnessActivities.SLEEP))
                    .collect(Collectors.toList());

            Log.d(TAG, "All Sleep Sessions");

            if (sleepSessions.isEmpty())
            {
                Log.d(TAG, "No sessions");
            }
            for (Session session : sleepSessions)
            {
                Log.d(TAG, "---");
                Log.d(TAG, String.format("Sleep Between %d and %d" , session.getStartTime(TimeUnit.SECONDS), session.getEndTime(TimeUnit.SECONDS)));

                // If the sleep session has finer granularity sub-components, extract them
                List<DataSet> dataSets = response.getDataSet(session);
                for (DataSet dataset : dataSets)
                {
                    for (DataPoint point : dataset.getDataPoints())
                    {
                        //The Activity defines whether this segment is light, deep, REM or awake
                        String sleepStage = point.getValue(Field.FIELD_ACTIVITY).asActivity();
                        long start = point.getStartTime(TimeUnit.MINUTES);
                        long end = point.getEndTime(TimeUnit.MINUTES);
                        Log.d(TAG, String.format("\t* %s for %d minutes", sleepStage, end-start));
                    }
                }
            }
        });

    }


















}