package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;
// import com.example.myapplication.compose.ProfileComposeHost;
import com.example.myapplication.viewmodel.ProfileViewModel;

public class ProfileTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 创建ComposeView
        ComposeView composeView = new ComposeView(this);
        setContentView(composeView);
        
        // 设置内容
                            // ProfileComposeHost.attach(composeView);
    }
}
