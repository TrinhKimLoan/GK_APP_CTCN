package com.example.chartctcn;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnOpenBudget = findViewById(R.id.btnOpenBudget);
        btnOpenBudget.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
            startActivity(intent);
        });
    }
}