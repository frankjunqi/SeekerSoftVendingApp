package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by kjh08490 on 2016/11/25.
 */

public class InsertGoodsNumActivity extends AppCompatActivity {

    private Button btn_goto_read_card;
    private Button btn_return_mainpage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertgoodsnum);

        btn_goto_read_card = (Button) findViewById(R.id.btn_goto_read_card);

        btn_goto_read_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToReadCard();
            }
        });
        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsertGoodsNumActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }


    private void goToReadCard() {
        Toast.makeText(InsertGoodsNumActivity.this, "确定，刷卡...", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(InsertGoodsNumActivity.this, CardReadActivity.class));
    }
}
