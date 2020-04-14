package com.dragon.myaptproject_01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.dragon.apt.annotation.BindView;
import com.dragon.apt.annotation.Factory;


//编译时注解处理器不像反射，直接调用方法时无效的，需要生成对应的java文件才可以在运行时执行。
//@Factory(type = Factory.class, name = "main")
public class MainActivity extends AppCompatActivity {

    @BindView(id = R.id.tv_content, onClick = true)
    TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //tvContent.setText("修改内容");
        CalculateFactory factory = new CalculateFactory();
        Calculate c = factory.create("add");
        c.operation(10, 20);
    }

}
