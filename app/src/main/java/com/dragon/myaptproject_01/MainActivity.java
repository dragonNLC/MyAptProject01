package com.dragon.myaptproject_01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.dragon.apt.annotation.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(id = R.id.tv_content, onClick = true)
    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
