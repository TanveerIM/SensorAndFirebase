package com.tanveerm.sensorandfirebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tanveerm.sensorandfirebase.activities.FirebaseUploadPhoto;
import com.tanveerm.sensorandfirebase.activities.SensorActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {

        CardView cardViewFirebase = findViewById(R.id.cardView_firebase);
        CardView cardViewSensor = findViewById(R.id.cardView_sensor);

        cardViewFirebase.setOnClickListener(this);
        cardViewSensor.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.cardView_firebase:
                Intent intent = new Intent(this, FirebaseUploadPhoto.class);
                startActivity(intent);
                break;
            case R.id.cardView_sensor:
                Intent intent1 = new Intent(this, SensorActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
