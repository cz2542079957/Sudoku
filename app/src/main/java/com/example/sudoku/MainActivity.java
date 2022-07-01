package com.example.sudoku;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.sudoku.config.GameConfig;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                System.out.println(id);
                String dif = "菜鸟";
                switch ((int) id)
                {
                    case 1:
                        dif = "入门";
                        break;
                    case 2:
                        dif = "初级";
                        break;
                    case 3:
                        dif = "中级";
                        break;
                    case 4:
                        dif = "高级";
                        break;
                    case 5:
                        dif = "大师";
                        break;
                }
                Toast.makeText(MainActivity.this, "您设置当前难度为:" + dif, Toast.LENGTH_SHORT).show();
                GameConfig.difficultCoefficient = (int) id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        Button start = findViewById(R.id.start);
        start.setOnClickListener(v ->
        {
            System.out.println("跳转");
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }
}