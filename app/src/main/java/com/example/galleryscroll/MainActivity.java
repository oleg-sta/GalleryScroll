package com.example.galleryscroll;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.GridView;

import com.example.galleryscroll.util.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final GridView gridView = findViewById(R.id.gridview);
        final GridAdapter adapter = new GridAdapter(this, Utils.getAllShownImagesPath(this));
        gridView.setAdapter(adapter);
    }
}