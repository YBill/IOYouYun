package com.ioyouyun.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.chat.ChatBigImageActivity;
import com.ioyouyun.chat.biz.VoicePlay;
import com.ioyouyun.chat.model.ChatMsgEntity;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by 卫彪 on 2016/6/13.
 */
public class ChatMsgAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<ChatMsgEntity>  msgEntityList = new ArrayList<>();
//    private SimpleDateFormat simpleDateFormat;
    private PrettyTime prettyTime;

    public ChatMsgAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
//        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        prettyTime = new PrettyTime();
    }

    public void setMsgEntityList(List<ChatMsgEntity> list){
        if(list != null)
            this.msgEntityList = list;
    }

    /**
     * 消息类型
     */
    public interface MsgTypeInterface{
        int CHAT_TYPE_RECV_MSG = 0; // 接收消息类型
        int CHAT_TYPE_SEND_MSG = 1; // 发送消息类型
    }

    /**
     * 得到Item的类型
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        ChatMsgEntity entity = getItem(position);
        if (entity.isDirect()){
            return MsgTypeInterface.CHAT_TYPE_RECV_MSG;
        }else {
            return MsgTypeInterface.CHAT_TYPE_SEND_MSG;
        }

    }

    /**
     * Item类型的总数
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return msgEntityList.size();
    }

    @Override
    public ChatMsgEntity getItem(int position) {
        return msgEntityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatMsgEntity entity = getItem(position);
        final ChatMsgEntity.Chat_Msg_Type msgType = entity.getMsgType();
        boolean direct = entity.isDirect();
        ViewHolder holder;
        if(convertView == null){
            if(direct)
                convertView = inflater.inflate(R.layout.adapter_chat_item_msg_left, null);
            else
                convertView = inflater.inflate(R.layout.adapter_chat_item_msg_right, null);
            holder = new ViewHolder();
            holder.dataText = (TextView)convertView.findViewById(R.id.tv_sendtime);
            holder.contentText = (TextView)convertView.findViewById(R.id.tv_chatcontent);
            holder.audioTimeText = (TextView)convertView.findViewById(R.id.tv_time);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.dataText.setText(prettyTime.format(new Date(entity.getTimestamp())));

        switch (msgType){
            case TYPE_TEXT:
                holder.contentText.setText(entity.getText());
                holder.audioTimeText.setText("");
                holder.contentText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                break;
            case TYPE_AUDIO:
                holder.contentText.setText("");
                holder.audioTimeText.setText(entity.getAudioTime());
                if(direct)
                    holder.contentText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.chatfrom_voice_playing, 0, 0, 0);
                else
                    holder.contentText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
                break;
            case TYPE_IMAGE:
                if(!TextUtils.isEmpty(entity.getImgThumbnail())){
                    Bitmap bitmap = BitmapFactory.decodeFile(entity.getImgThumbnail());
                    Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
                    holder.contentText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                    holder.contentText.setText("");
                    holder.audioTimeText.setText("");
                }
                break;
        }

        holder.contentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (msgType){
                    case TYPE_AUDIO:
                        VoicePlay.playVoice(entity.getText());
                        break;
                    case TYPE_IMAGE:
                        ChatBigImageActivity.startActivity(context, entity);
                        break;
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder{
        TextView dataText;
        TextView contentText;
        TextView audioTimeText;

    }

}
