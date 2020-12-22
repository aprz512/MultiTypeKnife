package com.aprz.mylibrary;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LibActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib);

        RecyclerView recyclerView = findViewById(R.id.rv);

        List<Item> datas = new ArrayList<>();
        for (int i=0; i<20; i++) {
            datas.add(new Item());
        }
        LibTestAdapter libTestAdapter = new LibTestAdapter(datas);
        recyclerView.setAdapter(libTestAdapter);
        libTestAdapter.notifyDataSetChanged();
    }

}
