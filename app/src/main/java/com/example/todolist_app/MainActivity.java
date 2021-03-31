package com.example.todolist_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ImageView weekBtnImageView,todayBtnImageView,analyticsBtnImageView,monthBtnImageView;
    private FirebaseAuth mAuth;
    private String onlineUserId= "";
    private DatabaseReference expensesRef,balanceRef;
    private DecimalFormat df ;
    private TextView cashTotal;
    private ProgressDialog loader;

    private Double oldBalance;
    private Double newBalance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();

        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        balanceRef = FirebaseDatabase.getInstance().getReference("balance").child(onlineUserId);

        loader = new ProgressDialog(this);

        df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        weekBtnImageView = findViewById(R.id.weekBtnImageView);
        todayBtnImageView = findViewById(R.id.todayBtnImageView);
        analyticsBtnImageView = findViewById(R.id.analyticsBtnImageView);
        monthBtnImageView = findViewById(R.id.monthBtnImageView);

        cashTotal = findViewById(R.id.cashTotal);


        cashTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBalance();
            }
        });


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


    private void updateBalance(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);
        oldBalance = 0.0;
        newBalance = 0.0;
        loader.setMessage("Updating.... ( ͡° ͜ʖ ͡°)");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
        Query query = balanceRef.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    Map<String,Object> map = (Map<String, Object>) ds.getValue();
                    String totalString = map.get("amount").toString();
                    oldBalance = Double.valueOf(totalString);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query2 = expensesRef.orderByChild("month").equalTo(months.getMonths());
        query2.addValueEventListener(new ValueEventListener() {
            Double total = 0.0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    Map<String,Object> map = (Map<String, Object>) ds.getValue();
                    String totalString = map.get("amount").toString();
                      total = Double.valueOf(totalString);
                    newBalance += total;
                }
                newBalance = oldBalance-newBalance;
                cashTotal.setText(df.format(newBalance)+"");
                loader.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void changeBalance(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.balance_layout,null);
        myDialog.setView(myView);
        final Button cancel = myView.findViewById(R.id.btnCancel);
        final Button update = myView.findViewById(R.id.btnUpdate);
        final AlertDialog dialog = myDialog.create();
        final TextView currentBalance = myView.findViewById(R.id.currentBalance);
        final TextView beginningBalance = myView.findViewById(R.id.beginningBalance);
        final EditText beginningAmount = myView.findViewById(R.id.beginningAmount);
        dialog.show();
        beginningBalance.setText("Beginning Balance: "+df.format(oldBalance));
        currentBalance.setText("Current Balance: "+df.format(newBalance));
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountString = beginningAmount.getText().toString();
                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Months months = Months.monthsBetween(epoch, now);
                if (TextUtils.isEmpty(amountString)){
                    beginningAmount.setError("It's empty");
                }else{
                    balanceRef.removeValue();
                    String id = balanceRef.push().getKey();
                    Data data = new Data(null,null,id,null,Double.parseDouble(amountString),months.getMonths(),-1);
                    balanceRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Updated beginning balance successfully", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();

                        }
                    });
                    dialog.dismiss();
                    updateBalance();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBalance();
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}