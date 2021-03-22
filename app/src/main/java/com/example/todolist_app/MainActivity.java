package com.example.todolist_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

//    private CardView budgetCardView, todayCardView;
    private ImageView weekBtnImageView,todayBtnImageView,analyticsBtnImageView,monthBtnImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        weekBtnImageView = findViewById(R.id.weekBtnImageView);
        todayBtnImageView = findViewById(R.id.todayBtnImageView);
        analyticsBtnImageView = findViewById(R.id.analyticsBtnImageView);
        monthBtnImageView = findViewById(R.id.monthBtnImageView);

//        budgetCardView = findViewById(R.id.budgetCardView);
//        todayCardView = findViewById(R.id.todayCardView);

        analyticsBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AnalyticsActivity.class);
                startActivity(intent);
            }
        });
        todayBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TodaySpendingActivity.class);
                startActivity(intent);
            }
        });
        weekBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,WeekSpendingActivity.class);
                intent.putExtra("type","week");
                startActivity(intent);
            }
        });
        monthBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,WeekSpendingActivity.class);
                intent.putExtra("type","month");
                startActivity(intent);
            }
        });
    }



    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}