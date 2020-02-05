package com.example.memorableplacesupdated;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    static ArrayList<String> places = new ArrayList<String>();
    static ArrayList<LatLng> location  = new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        ArrayList<String> deserialize_lat = new ArrayList<>();
        ArrayList<String> deserialize_lon = new ArrayList<>();

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.memorableplacesupdated", Context.MODE_PRIVATE);
        try {
            places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places5", ObjectSerializer.serialize(new ArrayList<String>())));
            deserialize_lat = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lat5", ObjectSerializer.serialize(new ArrayList<String>())));
            deserialize_lon = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lon5", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("lat size check");
        System.out.println(deserialize_lat.size());
        System.out.println(places.size());
        if(places.size()>0 && deserialize_lat.size()>0 && deserialize_lon.size()>0){
            System.out.println("Inside 1st stage");
            if(places.size()==deserialize_lat.size()&& places.size()==deserialize_lon.size()){
                System.out.println("Inside 2nd stage");
                for (int i =0; i<deserialize_lat.size();i++){
                    location.add(new LatLng(Double.parseDouble(deserialize_lat.get(i)),Double.parseDouble(deserialize_lon.get(i))));
                }
            }
        } else {

            try {
                places.set(0, "Add a place...");
                location.set(0, new LatLng(0, 0));
            } catch (Exception e) {
                places.add("Add a place...");
                location.add(new LatLng(0, 0));
                e.printStackTrace();
            }
        }
        System.out.println(places);
        System.out.println(location);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,places);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("placeNumber",i);
                startActivity(intent);
            }
        });

    }
}
