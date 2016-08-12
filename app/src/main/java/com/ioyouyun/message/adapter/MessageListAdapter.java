package com.ioyouyun.message.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.chat.model.ChatMsgEntity;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 卫彪 on 2016/6/21.
 */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {

    private LayoutInflater inflater;
    private OnItemClickLitener onItemClickLitener;
    private List<ChatMsgEntity> messageList = new ArrayList<>();
    private PrettyTime prettyTime;

    public MessageListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        prettyTime = new PrettyTime();
    }

    public void setMessageList(List<ChatMsgEntity> messageList) {
        this.messageList = messageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_users, parent, false);
        MessageViewHolder myViewHolder = new MessageViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        ChatMsgEntity entity = messageList.get(position);
        ChatMsgEntity.Chat_Msg_Type msgType = entity.getMsgType();
        if (entity.getUnreadMsgNum() > 0) {
            holder.notifyText.setText("" + entity.getUnreadMsgNum());
            holder.notifyText.setVisibility(View.VISIBLE);
        } else
            holder.notifyText.setVisibility(View.GONE);
        holder.nicknameText.setText(entity.getName());
        holder.timeText.setText(prettyTime.format(new Date(entity.getTimestamp())));

        switch (msgType) {
            case TYPE_TEXT:
                holder.messageText.setText(entity.getText());
                break;
            case TYPE_AUDIO:
                holder.messageText.setText("[语音]");
                break;
            case TYPE_IMAGE:
                holder.messageText.setText("[图片]");
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickLitener != null) {
                    int position = holder.getLayoutPosition();
                    onItemClickLitener.onItemClick(holder.itemView, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener onItemClickLitener) {
        this.onItemClickLitener = onItemClickLitener;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView nicknameText;
        TextView messageText;
        TextView timeText;
        TextView notifyText;

        public MessageViewHolder(View itemView) {
            super(itemView);
            nicknameText = (TextView) itemView.findViewById(R.id.tv_nickname);
            messageText = (TextView) itemView.findViewById(R.id.tv_message);
            timeText = (TextView) itemView.findViewById(R.id.tv_time);
            timeText.setVisibility(View.VISIBLE);
            notifyText = (TextView) itemView.findViewById(R.id.tv_notify);
        }
    }
}
