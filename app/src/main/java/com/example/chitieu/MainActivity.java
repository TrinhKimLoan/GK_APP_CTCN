package com.example.chitieu;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnOpenCategory = findViewById(R.id.btnOpenCategory);
        btnOpenCategory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
            startActivity(intent);
        });
    }
}
