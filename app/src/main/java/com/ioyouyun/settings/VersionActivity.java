package com.ioyouyun.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.utils.FunctionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VersionActivity extends AppCompatActivity {

    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_msg)
    TextView tvMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void $setToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView(){
        $setToolBar();
        tvMsg.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void initData() {
        tvVersion.setText(FunctionUtil.getVersion());

        tvMsg.append(getResources().getString(R.string.version_1));
        tvMsg.append("\n");
        tvMsg.append("\n");
        tvMsg.append(getResources().getString(R.string.version_2));
        tvMsg.append("\n");
        tvMsg.append("\n");
        tvMsg.append(getResources().getString(R.string.version_3));
        tvMsg.append("\n");
        tvMsg.append("\n");
        tvMsg.append(getResources().getString(R.string.version_4));
    }
}
