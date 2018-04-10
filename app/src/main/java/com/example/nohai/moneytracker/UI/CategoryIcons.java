package com.example.nohai.moneytracker.UI;

import android.arch.persistence.room.Room;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.nohai.moneytracker.AppDatabase;
import com.example.nohai.moneytracker.R;
import com.example.nohai.moneytracker.dao.CategoryDao;

public class CategoryIcons extends AppCompatActivity {
    AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_icons);

        db = Room.databaseBuilder(this.getApplicationContext(), AppDatabase.class,"Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        byte[] blob = db.categoryIconDao().getFirst(1);

        Bitmap bmp= BitmapFactory.decodeByteArray(blob,0,blob.length);
        ImageView image = findViewById(R.id.imgIcon);
        image.setImageBitmap(bmp);
    }
}
