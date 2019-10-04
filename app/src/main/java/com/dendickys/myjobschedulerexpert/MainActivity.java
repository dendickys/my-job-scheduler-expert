package com.dendickys.myjobschedulerexpert;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnStart, btnCancel;
    private static final int JOB_ID = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btn_start);
        btnCancel = findViewById(R.id.btn_cancel);

        btnStart.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                startJob();
                break;
            case R.id.btn_cancel:
                cancelJob();
                break;
        }
    }

    private void startJob() {
        if (isJobRunning(this)) {
            Toast.makeText(this, "Job Service is already schedule", Toast.LENGTH_SHORT).show();
            return;
        }

        ComponentName mServiceComponent = new ComponentName(this, GetCurrentWeatherJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, mServiceComponent);

        // Kondisi network,
        // NETWORK_TYPE_ANY, berarti tidak ada ketentuan tertentu
        // NETWORK_TYPE_UNMETERED, adalah network yang tidak dibatasi misalnya wifi
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

        // Kondisi device, secara default sudah pada false
        // false, berarti device tidak perlu idle ketika job ke trigger
        // true, berarti device perlu dalam kondisi idle ketika job ke trigger
        builder.setRequiresDeviceIdle(false);

        // Kondisi charging
        // false, berarti device tidak perlu di charge
        // true, berarti device perlu dicharge
        builder.setRequiresCharging(false);

        // Periode interval sampai ke trigger
        // Dalam milisecond, 1000ms = 1 detik
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setPeriodic(900000); // 15 menit
        } else {
            builder.setPeriodic(180000); // 3 menit
        }
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert jobScheduler != null;
        jobScheduler.schedule(builder.build());

        Toast.makeText(this, "Job Service started", Toast.LENGTH_SHORT).show();
    }

    private void cancelJob() {
        JobScheduler tm = (JobScheduler)getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert tm != null;
        tm.cancel(JOB_ID);
        Toast.makeText(this, "Job Service canceled", Toast.LENGTH_SHORT).show();
    }

    private boolean isJobRunning(Context context) {
        boolean isSchedule = false;

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        if (scheduler != null) {
            for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
                if (jobInfo.getId() == JOB_ID) {
                    isSchedule = true;
                    break;
                }
            }
        }

        return isSchedule;
    }
}
