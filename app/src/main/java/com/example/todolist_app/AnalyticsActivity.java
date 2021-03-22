package com.example.todolist_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsActivity extends AppCompatActivity {
    private AnyChartView anyChartView;
    private Map<String,Long> myDataList;
    private FirebaseAuth mAuth;
    private String onlineUserId= "";
    private DatabaseReference expensesRef;
    private TextView titleChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        anyChartView = findViewById(R.id.any_chart_view);
        titleChart= findViewById(R.id.titleChart);
        myDataList = new HashMap<>();

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        readMonthSpendingItems();
    }

    private void readMonthSpendingItems() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = expensesRef.orderByChild("month").equalTo(months.getMonths());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    Map<String,Object> map = (Map<String, Object>) ds.getValue();
                    Long amount = Math.round(Double.parseDouble(map.get("amount").toString()));
                    if (!myDataList.containsKey(map.get("item"))){
                        myDataList.put(map.get("item").toString(), amount);
                    }else{
                        Long currentAmount = myDataList.get(map.get("item"));
                        myDataList.put((String)map.get("item"),currentAmount+amount);
                    }
                }
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
                String month_name = month_date.format(cal.getTime());
                titleChart.setText("Analysis in " + month_name);
                setupPieChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setupPieChart() {
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntryList = new ArrayList<>();

        for (Map.Entry<String,Long> data : myDataList.entrySet()){
            dataEntryList.add(new ValueDataEntry(data.getKey(),data.getValue()));
        }
        pie.data(dataEntryList);
        anyChartView.setChart(pie);
    }
}