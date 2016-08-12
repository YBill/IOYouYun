package com.ioyouyun.media;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ioyouyun.R;
import com.weimi.media.WMedia;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 卫彪 on 2016/7/22.
 */
public class BeInviteActivity extends AppCompatActivity {

    @BindView(R.id.tv_invite_user)
    TextView tvInviteUser;
    private String invitedFrom;
    private String invitedRoomId;
    private String invitedRoomKey;
    private String invitedGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_beinvite);
        ButterKnife.bind(this);
        Log.v("Bill", "onCreate");
        getIntentExtra();
        initData();
    }

    private void initData() {
        String msg = String.format(getResources().getString(R.string.conference_invite_msg), invitedFrom);
        tvInviteUser.setText(msg);
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            invitedFrom = intent.getStringExtra("invitedFrom");
            invitedRoomId = intent.getStringExtra("invitedRoomId");
            invitedRoomKey = intent.getStringExtra("invitedRoomKey");
            invitedGroupId = intent.getStringExtra("invitedGroupId");
        }
    }

    @OnClick({R.id.tv_refuse, R.id.tv_accept})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_refuse:
                finish();
                break;
            case R.id.tv_accept:
                WMedia.getInstance().callGroup(invitedRoomId, invitedRoomKey);

                Intent intent = new Intent(this, ConferenceActivity.class);
                intent.putExtra("invitedRoomId", invitedRoomId);
                intent.putExtra("invitedRoomKey", invitedRoomKey);
                intent.putExtra("invitedGroupId", invitedGroupId);
                startActivity(intent);
                finish();
                break;
        }
    }
}
