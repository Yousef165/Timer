package com.application.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.application.timer.support.Helper;

import java.util.concurrent.atomic.AtomicInteger;


public class TimerActivity extends AppCompatActivity {

    // Declare all the variables
    Button workOutTimeStartBtn, restTimeStartBtn, workOutTimePauseBtn, exitBtn;
    EditText totalTimeHr, totalTimeMin, totalTimeSec, resValue;
    TextView workOutTimeValue, restTimeValue, workOutTimeViewValue, restTimeViewValue;
    Helper helper;
    SharedPreferences sharedPreferences;
    CountDownTimer count, restCount;
    public long counter;
    public long cTime;
    public long runCounter = 1000;
    public static final String CHANNEL_ID = "CHANNEL_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        // Initialize all the variables
        workOutTimeStartBtn = (Button) findViewById(R.id.workOutTimeStartBtn);
        restTimeStartBtn = (Button) findViewById(R.id.restTimeStartBtn);
        exitBtn = (Button) findViewById(R.id.exitBtn);
        totalTimeHr = (EditText) findViewById(R.id.totalTimeHr);
        totalTimeMin = (EditText) findViewById(R.id.totalTimeMin);
        totalTimeSec = (EditText) findViewById(R.id.totalTimeSec);
        workOutTimeValue = (TextView) findViewById(R.id.workOutTimeValue);
        restTimeValue = (TextView) findViewById(R.id.restTimeValue);
        workOutTimeViewValue = (TextView) findViewById(R.id.workOutTimeViewValue);
        restTimeViewValue = (TextView) findViewById(R.id.restTimeViewValue);
        resValue = (EditText) findViewById(R.id.resValue);

        // Create a shared preference
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        // Create a helper class object
        helper = new Helper();

        resValue.setText("0");

        // Create a notification channel
        helper.createNotificationChannel(this);

        // Set the onClickListener for the buttons WorkOutTimeStartBtn
        workOutTimeStartBtn.setOnClickListener(v -> {
            workOut(0);
        });

        // Set the onClickListener for the buttons RestTimeStartBtn
        restTimeStartBtn.setOnClickListener(v -> {
            restFun(0);
        });

        // Set the onClickListener for the buttons ExitBtn
        exitBtn.setOnClickListener(v -> {
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putLong("counter", 0);
            myEdit.commit();
            finish();
        });

    }

    // Create a rest function method
    private void restFun(int status) {

        // Cancel the restCount if the status is 2
        if (status == 2) {
            restCount.cancel();
            return;
        }

        // Create a shared preference
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        // Get the value from the shared preference
        long rTime = sharedPreferences.getLong("counter", 0);
        long cTime = 0;

        // Check the value is 0 or not
        if (rTime == 0) {
            cTime = 2000000000;
        } else {
            cTime = rTime;
        }

        // Create a count down timer
        restCount = new CountDownTimer(cTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                runCounter = runCounter+1000;
                System.out.println("runCounter : " + runCounter);
                long Hr = runCounter / 3600000;
                long Min = (runCounter % 3600000) / 60000;
                long Sec = ((runCounter % 3600000) % 60000) / 1000;

                // Set the text to the text view
                restTimeValue.setText(String.format("%02d:%02d:%02d", Hr, Min, Sec));
                restTimeViewValue.setText(String.format("%02d:%02d:%02d", Hr, Min, Sec));

                // Update the value to the shared preference
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putLong("counter", millisUntilFinished);
                myEdit.commit();

            }

            @Override
            public void onFinish() {

                // Update the value to the shared preference
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putLong("counter", 0);
                myEdit.commit();
            }
        }.start();

    }


    // Create a workOut function method
    private void workOut(long lTime) {

        // Initialize the notification id
        AtomicInteger notificationId = new AtomicInteger(0);

        // Check the total time is empty or not
        if (totalTimeHr.getText().toString().isEmpty() || totalTimeMin.getText().toString().isEmpty() || totalTimeSec.getText().toString().isEmpty()) {
            helper.alert("Alert !", "Please enter the time", this);
        }
        if (totalTimeHr.getText().toString().equals("00") && totalTimeMin.getText().toString().equals("00") && totalTimeSec.getText().toString().equals("00")) {
            helper.alert("Alert !", "Please enter the time", this);
        } else {

            // Get the value from the edit text
            int totalHr = Integer.parseInt(totalTimeHr.getText().toString());
            int totalMin = Integer.parseInt(totalTimeMin.getText().toString());
            int totalSec = Integer.parseInt(totalTimeSec.getText().toString());

            // Calculate the total time
            long totalWorkOutTime = (totalHr * 3600000) + (totalMin * 60000) + (totalSec * 1000);

            //check the value is 0 or not
            if (lTime == 0) {
                cTime = totalWorkOutTime;
            } else {
                cTime = lTime;
            }

            // Create a count down timer
            count = new CountDownTimer(cTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long Hr = millisUntilFinished / 3600000;
                    long Min = (millisUntilFinished % 3600000) / 60000;
                    long Sec = ((millisUntilFinished % 3600000) % 60000) / 1000;

                    // Set the text to the text view
                    workOutTimeValue.setText(String.format("%02d:%02d:%02d", Hr, Min, Sec));
                    long remainingTime = (totalHr * 3600000) + (totalMin * 60000) + (totalSec * 1000) - millisUntilFinished;
                    counter = millisUntilFinished;
                    workOutTimeViewValue.setText(String.format("%02d:%02d:%02d", remainingTime / 3600000, (remainingTime % 3600000) / 60000, ((remainingTime % 3600000) % 60000) / 1000));

                }

                @Override
                public void onFinish() {
                    // set Notification
                    helper.ring(TimerActivity.this);
                    helper.sendNotification("Workout Completed", 00000555, TimerActivity.this);
                    workOutTimeValue.setText("00:00:00");
                    counter = 0;

                    // Set the text to the text view
                    workOutTimeStartBtn.setText("Start");
                    totalTimeHr.setText("00");
                    totalTimeMin.setText("00");
                    totalTimeSec.setText("00");

                    // Update the value to the shared preference
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putLong("counter", 0);
                    myEdit.commit();


                }

            }.start();

            // Set the onClickListener for the buttons WorkOutTimeStartBtn
            workOutTimeStartBtn.setText("Pause");
            workOutTimeStartBtn.setOnClickListener(v -> {
                // Cancel the count down timer and start the rest function
                this.restFun(0);
                count.cancel();
                workOutTimeStartBtn.setText("Start");
                workOutTimeStartBtn.setOnClickListener(v1 -> {
                    // Start the workOut function and cancel the rest function
                    this.restFun(2);
                    workOut(counter);
                });
            });

        }

    }

}