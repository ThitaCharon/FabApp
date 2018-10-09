package com.example.thita.fabapp;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.text.DateFormat;
import java.util.Date;

public class MyJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
        Log.d("CURRENT", currentDateTime);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
