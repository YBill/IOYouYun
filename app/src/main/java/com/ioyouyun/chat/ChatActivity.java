package com.ioyouyun.chat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.chat.adapter.ChatMsgAdapter;
import com.ioyouyun.chat.adapter.ExpressionGridViewAdapter;
import com.ioyouyun.chat.adapter.ExpressionPagerAdapter;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.group.activity.GroupSettingActivity;
import com.ioyouyun.home.widgets.ScrollChildSwipeRefreshLayout;
import com.ioyouyun.utils.FileUtil;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.wchat.message.ConvType;
import com.ioyouyun.widgets.ExpandGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫彪 on 2016/6/3.
 */
public class ChatActivity extends BaseActivity<ChatView, ChatPresenter> implements ChatView {

    public static final int REQUEST_CODE_SETTING = 1003;
    public static final int REQUEST_CODE_CAMERA = 1001;
    public static final int REQUEST_CODE_LOCAL = 1002;
    private Button buttonSetModeVoice; // 左侧语音键盘按钮
    private View buttonPressToSpeak; // 按住说话按钮
    private TextView textPressToSpeck; // 按住说话文字
    private RelativeLayout editLayout; // 输入框外层View,包括EditText和emoji表情按钮
    private EditText mEditTextContent; // 输入框
    private ImageView emojiIcon; // emoji图标
    private Button btnMore; // 右侧更多按钮(加号)
    private View buttonSend; // 发送按钮,跟当btnMore隐藏时显示
    private View modeView; // 底部更多View,包括表情或拍照等按钮
    private ViewPager expressionViewPager; // 表情ViewPager
    private View emojiIconContainer; // 底部表情View
    private View btnContainer; // 底部扩展按钮,拍照和相册等
    private TextView photoGalleryBtn; // 相册
    private TextView takePhotoBtn; // 拍照
    private ListView chatListView; // 聊天界面
    private TextView topTitleText; // title
    private TextView clearBtn; // 清空
    private View recordingContainer; // 录音提示框
    private ImageView micImage;
    private TextView recordingHint;
    private ScrollChildSwipeRefreshLayout swipeRefreshLayout;

    private ChatMsgAdapter chatMsgAdapter; // Chat Adapter

    /**
     * true: 显示键盘图标，按住说话按钮
     * false: 显示语音图标，输入框
     */
    private boolean isSpeck = false;
    private InputMethodManager manager;
    private PowerManager.WakeLock wakeLock;
    private Integer[] micImages = new Integer[]{
            R.drawable.record_animate_01,
            R.drawable.record_animate_02,
            R.drawable.record_animate_03,
            R.drawable.record_animate_04,
            R.drawable.record_animate_05,
            R.drawable.record_animate_06,
            R.drawable.record_animate_07,
            R.drawable.record_animate_08,
            R.drawable.record_animate_09,
            R.drawable.record_animate_10,
            R.drawable.record_animate_11,
            R.drawable.record_animate_12,
            R.drawable.record_animate_13,
            R.drawable.record_animate_14,
    };
    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
            micImage.setImageResource(micImages[msg.what]);
        }
    };

    private File cameraFile;
    private String toUId;
    private String nickName;
    private ConvType convType;

    @Override
    protected ChatPresenter initPresenter() {
        return new ChatPresenter();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void $setToolBar() {
        super.$setToolBar();
    }

    @Override
    protected void initView() {
        buttonSetModeVoice = $findViewById(R.id.btn_set_mode_voice);
        buttonPressToSpeak = $findViewById(R.id.btn_press_to_speak);
        textPressToSpeck = $findViewById(R.id.tv_press_to_speck);
        editLayout = $findViewById(R.id.edittext_layout);
        editLayout.setBackgroundResource(R.drawable.input_bar_bg_normal);
        editLayout.requestFocus();
        mEditTextContent = $findViewById(R.id.et_sendmessage);
        emojiIcon = $findViewById(R.id.iv_emoticons_normal);
        btnMore = $findViewById(R.id.btn_more);
        buttonSend = $findViewById(R.id.btn_send);
        modeView = $findViewById(R.id.ll_more);
        expressionViewPager = $findViewById(R.id.vp_emoji);
        emojiIconContainer = $findViewById(R.id.ll_face_container);
        btnContainer = $findViewById(R.id.ll_btn_container);
        photoGalleryBtn = $findViewById(R.id.tv_picture);
        takePhotoBtn = $findViewById(R.id.tv_take_photo);
        chatListView = $findViewById(R.id.lv_chat);
        topTitleText = $findViewById(R.id.tv_title);
        clearBtn = $findViewById(R.id.btn_right);
        recordingContainer = $findViewById(R.id.recording_container);
        micImage = (ImageView) findViewById(R.id.mic_image);
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        swipeRefreshLayout = $findViewById(R.id.refresh_layout);
        $setToolBar();
    }

    @Override
    protected void setListener() {
        buttonSetModeVoice.setOnClickListener(this);
        buttonPressToSpeak.setOnTouchListener(new PressToSpeakListener());
        mEditTextContent.setOnClickListener(this);
        emojiIcon.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        buttonSend.setOnClickListener(this);
        takePhotoBtn.setOnClickListener(this);
        photoGalleryBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        chatListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                modeView.setVisibility(View.GONE);
                return false;
            }
        });
        mEditTextContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editLayout.setBackgroundResource(R.drawable.input_bar_bg_active);
                } else {
                    editLayout.setBackgroundResource(R.drawable.input_bar_bg_normal);
                }
            }
        });
        // 监听文字框
        mEditTextContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btnMore.setVisibility(View.GONE);
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    btnMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getHistory(toUId, convType);
            }
        });
    }

    @Override
    protected void initData() {
        //启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "youyun");

        getIntentExtra();

        // init adapter
        chatMsgAdapter = new ChatMsgAdapter(this);
        chatListView.setAdapter(chatMsgAdapter);

        if (ConvType.group == convType)
            clearBtn.setText(getResources().getString(R.string.string_setting));
        else
            clearBtn.setText(getResources().getString(R.string.btn_clear));
        clearBtn.setVisibility(View.VISIBLE);
        topTitleText.setText(nickName);
        topTitleText.setVisibility(View.VISIBLE);

        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(chatListView);
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set_mode_voice:
                isSpeck = !isSpeck;
                if (isSpeck) {
                    hideKeyboard();
                    buttonSetModeVoice.setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn_normal);
                    editLayout.setVisibility(View.GONE);
                    buttonPressToSpeak.setVisibility(View.VISIBLE);
                    btnMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
                } else {
                    buttonSetModeVoice.setBackgroundResource(R.drawable.icon_chat_voice);
                    buttonPressToSpeak.setVisibility(View.GONE);
                    editLayout.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(mEditTextContent.getText().toString())) {
                        btnMore.setVisibility(View.VISIBLE);
                        buttonSend.setVisibility(View.GONE);
                    } else {
                        btnMore.setVisibility(View.GONE);
                        buttonSend.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.iv_emoticons_normal:
                hideKeyboard();
                showEmojiView();
                modeView.setVisibility(View.VISIBLE);
                emojiIconContainer.setVisibility(View.VISIBLE);
                btnContainer.setVisibility(View.GONE);
                break;
            case R.id.btn_more:
                hideKeyboard();
                modeView.setVisibility(View.VISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_right:
                if (ConvType.group == convType) {
                    // 设置
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_GID, toUId);
                    $startActivityForResult(GroupSettingActivity.class, bundle, REQUEST_CODE_SETTING);
                } else
                    presenter.clearLocalData(toUId);
                break;
            case R.id.tv_take_photo:
                selectPicFromCamera();
                break;
            case R.id.tv_picture:
                selectPicFromLocal();
                break;
            case R.id.et_sendmessage:
                editLayout.setBackgroundResource(R.drawable.input_bar_bg_active);
                modeView.setVisibility(View.GONE);
                break;
            case R.id.btn_send:
                String text = mEditTextContent.getText().toString();
                if(!TextUtils.isEmpty(text))
                    presenter.sendText(toUId, text, nickName, convType);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.refreshLocalData(toUId);
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            toUId = intent.getStringExtra("toUid");
            nickName = intent.getStringExtra("nickName");
            int type = intent.getIntExtra("chatType", 0); // 0:单聊 1：群聊
            if (type == 1)
                convType = ConvType.group;
            else
                convType = ConvType.single;

        }
    }

    private String getEmojiStringByUnicode(int unicodeJoy) {
        return new String(Character.toChars(unicodeJoy));
    }

    private List<View> emojiLists;

    private void showEmojiView() {
        if (emojiLists == null) {
            emojiLists = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                emojiLists.add(getEmojiItemViews(i));
            }
            expressionViewPager.setAdapter(new ExpressionPagerAdapter(emojiLists));
        }
    }

    /**
     * Emoji Item 系统共79个常用表情
     *
     * @param page
     * @return
     */
    private View getEmojiItemViews(int page) {
        List<String> list = new ArrayList<>();
        int start = 21 * page;
        int length;
        if (page == 3)
            length = 79;
        else
            length = (page + 1) * 21;
        for (int i = start; i < length; i++) {
            list.add(getEmojiStringByUnicode(0x1F601 + i));
        }
        View view = View.inflate(ChatActivity.this, R.layout.layout_expression_gridview, null);
        ExpandGridView gridView = (ExpandGridView) view.findViewById(R.id.gridview);
        final ExpressionGridViewAdapter gridViewAdapter = new ExpressionGridViewAdapter(ChatActivity.this, list);
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEditTextContent.append(gridViewAdapter.getItem(position));
            }
        });

        return view;
    }

    /**
     * 刷新adapter
     *
     * @param list
     */
    private void refreshAdapter(List<ChatMsgEntity> list) {
        chatMsgAdapter.setMsgEntityList(list);
        chatMsgAdapter.notifyDataSetChanged();
    }

    /**
     * 隐藏键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 拍照
     */
    private void selectPicFromCamera() {
        if (!FunctionUtil.isExitsSdcard()) {
            FunctionUtil.toastMessage("SD卡不存在，不能拍照");
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        long ts = System.currentTimeMillis();
        String mImagePath = FileUtil.getCameraPath() + ts + ".jpg";
        cameraFile = new File(mImagePath);
        Uri uri = Uri.fromFile(cameraFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * 相册
     */
    private void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    /**
     * 选择相册图片后处理
     *
     * @param data
     */
    private void sendLocalImage(Intent data) {
        Uri uri = data.getData();
        if (uri != null) {
            String picturePath = "";
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                picturePath = cursor.getString(columnIndex);
                cursor.close();
                cursor = null;
            } else {
                File file = new File(uri.getPath());
                if (file.exists()) {
                    picturePath = file.getAbsolutePath();
                }
            }
            if (picturePath == null || picturePath.equals("null") || "".equals(picturePath)) {
                FunctionUtil.toastMessage("找不到图片");
                return;
            }
            presenter.sendImage(toUId, nickName, picturePath, picturePath.substring(picturePath.lastIndexOf("/") + 1), convType);
        }
    }

    /**
     * 按住说话
     */
    private class PressToSpeakListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!FunctionUtil.isExitsSdcard()) {
                        FunctionUtil.toastMessage("发送语音需要sdcard支持！");
                        return false;
                    }
                    try {
                        v.setPressed(true); // 更换背景, chat_press_speak_btn.xml
                        wakeLock.acquire();
                        textPressToSpeck.setText(getResources().getString(R.string.button_loosen_end));
                        recordingContainer.setVisibility(View.VISIBLE);
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        presenter.startRealTimeRecord(toUId, nickName, convType, micImageHandler);
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        recordingContainer.setVisibility(View.GONE);
                        FunctionUtil.toastMessage(R.string.recoding_fail);
                        return false;
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    textPressToSpeck.setText(getResources().getString(R.string.button_pushtotalk));
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        // discard the recorded audio.
                        presenter.discardRecording(true);
                    } else {
                        // stop recording and send voice file
                        presenter.discardRecording(false);
                    }
                    try {
                        presenter.stopRealTimeRecord();
                    } catch (Exception e) {
                        e.printStackTrace();
                        FunctionUtil.toastMessage("发送失败，请检测服务器是否连接");
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (event.getY() < 0) {
                        recordingHint.setText(getString(R.string.release_to_cancel));
                        recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
                    } else {
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                    }
                    return true;
                default:
                    recordingContainer.setVisibility(View.INVISIBLE);
                    return false;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                if (cameraFile != null && cameraFile.exists()) {
                    String path = cameraFile.getAbsolutePath();
                    presenter.sendImage(toUId, nickName, path, path.substring(path.lastIndexOf("/") + 1), convType);
                }
            } else if (requestCode == REQUEST_CODE_LOCAL) {
                if (data != null) {
                    sendLocalImage(data);
                }
            } else if(requestCode == REQUEST_CODE_SETTING){
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wakeLock.isHeld())
            wakeLock.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestory();
    }

    @Override
    public void showChatMsgList(List<ChatMsgEntity> list) {
        refreshAdapter(list);
    }

    @Override
    public void setChatSelection(int position) {
        chatListView.setSelection(position);
    }

    @Override
    public void clearChatContent() {
        mEditTextContent.setText("");
    }

    @Override
    public void onCompleteLoad() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
