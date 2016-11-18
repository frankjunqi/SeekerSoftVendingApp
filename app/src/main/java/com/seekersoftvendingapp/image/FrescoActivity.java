package com.seekersoftvendingapp.image;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.drawee.view.SimpleDraweeView;
import com.seekersoftvendingapp.R;

/**
 * Created by kjh08490 on 2016/11/18.
 */

public class FrescoActivity extends AppCompatActivity {
    private SimpleDraweeView iv_icon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fresco_layout);
        iv_icon = (SimpleDraweeView) findViewById(R.id.iv_icon);
        Uri uri = Uri.parse("https://pic4.40017.cn/scenery/destination/2016/07/18/18/wqULcl.jpg");
        iv_icon.setImageURI(uri);
    }
}
