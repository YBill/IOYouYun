package com.ioyouyun.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.ioyouyun.R;
import com.ioyouyun.chat.biz.ChatRequest;
import com.ioyouyun.chat.biz.ChatRequestImpl;
import com.ioyouyun.chat.biz.OnChatListener;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.chat.model.ChatPicInfo;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.observer.MessageEvent;
import com.ioyouyun.receivemsg.BroadCastCenter;
import com.ioyouyun.utils.FileUtil;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.wchat.message.ConvType;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by 卫彪 on 2016/6/20.
 */
public class ChatBigImageActivity extends Activity {

    private final static String CHAT_IMG_ENTITY = "chat_img_entity";
    private final static String CHAT_POSITION = "chat_position";
    private ImageView imageView;
    private ChatRequest request;
    private MyInnerReceiver receiver;
    private String downloadPath;
    private ChatPicInfo chatPicInfo;
    private ChatMsgEntity chatMsgEntity;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_big_image);
        imageView = (ImageView) findViewById(R.id.iv_bigimg);

        registerReceiver();
        request = new ChatRequestImpl();

        getIntentExtra();

        chatPicInfo = ChatPicInfo.getInfo(chatMsgEntity.getImgMsg());
        showImg(chatPicInfo);
    }

    private void setChatPicInfo(String imgPath){
        chatPicInfo.local = imgPath;
        chatMsgEntity.setImgMsg(ChatPicInfo.getJsonStr(chatPicInfo));
        notifyChatList();
        String toid = chatMsgEntity.getFromId();
        if(ConvType.group == chatMsgEntity.getConvType())
            toid = chatMsgEntity.getToId();
        String name = FunctionUtil.jointTableName(toid);
        YouyunDbManager.getIntance().updateChatImageMsg(chatMsgEntity.getImgMsg(), chatMsgEntity.getMsgId(), name);
    }

    /**
     * 通知聊天数据更新
     */
    private void notifyChatList(){
        MessageEvent.DownloadImageEvent event = new MessageEvent.DownloadImageEvent();
        event.position = position;
        event.chatMsgEntity = chatMsgEntity;
        EventBus.getDefault().post(event);
    }

    private void loadLocalImage(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmap);
    }

    private void showImg(ChatPicInfo chatPicInfo) {
        if(TextUtils.isEmpty(chatPicInfo.local)){
            downLoadImg(chatPicInfo);
        }else{
            Bitmap bitmap = BitmapFactory.decodeFile(chatPicInfo.local);
            imageView.setImageBitmap(bitmap);
        }
    }

    private void downLoadImg(ChatPicInfo info){
        downloadPath = FileUtil.getChatImagePath(info.fileId) + ".png";
        request.downloadImage(info.fileId, downloadPath, info.fileLength, info.pieceSize, new OnChatListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFaild() {
                FunctionUtil.toastMessage("下载失败");
            }
        });
    }

    public static void startActivity(Context activity, ChatMsgEntity entity, int position){
        Intent intent = new Intent(activity, ChatBigImageActivity.class);
        intent.putExtra(CHAT_IMG_ENTITY, entity);
        intent.putExtra(CHAT_POSITION, position);
        activity.startActivity(intent);
    }

    private void getIntentExtra(){
        Intent intent = getIntent();
        if(intent != null){
            chatMsgEntity = (ChatMsgEntity) intent.getSerializableExtra(CHAT_IMG_ENTITY);
            position = intent.getIntExtra(CHAT_POSITION, 0);
        }
    }

    /**
     * 注册本地广播
     */
    private void registerReceiver(){
        receiver = new MyInnerReceiver();
        BroadCastCenter.getInstance().registerReceiver(receiver, FunctionUtil.MSG_TYPE_DOWNLOAD_IMAGE);
    }

    /**
     * 注销广播
     */
    private void unregisterReceiver(){
        if(receiver != null)
            BroadCastCenter.getInstance().unregisterReceiver(receiver);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    class MyInnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(FunctionUtil.MSG_TYPE_DOWNLOAD_IMAGE.equals(action)){
                String fileId = intent.getStringExtra(FunctionUtil.DOWNLOAD_FILEID);
                int progress = intent.getIntExtra(FunctionUtil.DOWNLOAD_PROGRESS, 0);
                String filePath = FileUtil.getChatImagePath(fileId) + ".png";
                Logger.v("progress:" + progress);
                if(progress == 100){
                    // 下载完成
                    Logger.v("下载完成");
                    if(filePath.equals(downloadPath)){
                        setChatPicInfo(filePath);
                        loadLocalImage(filePath);
                    }
                }
            }
        }
    }
}
