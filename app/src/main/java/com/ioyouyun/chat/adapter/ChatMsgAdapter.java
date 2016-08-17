package com.ioyouyun.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.chat.ChatBigImageActivity;
import com.ioyouyun.chat.VoicePlay;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.chat.widget.MaskView;
import com.ioyouyun.utils.FunctionUtil;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by 卫彪 on 2016/6/13.
 */
public class ChatMsgAdapter extends BaseAdapter {

    public final int screenWidth1p3 = FunctionUtil.mScreenWidth / 3;
    public final int screenWidth1p4 = FunctionUtil.mScreenWidth / 4;
    private Context context;
    private LayoutInflater inflater;
    private List<ChatMsgEntity> msgEntityList = new ArrayList<>();
    private PrettyTime prettyTime;

    public ChatMsgAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        prettyTime = new PrettyTime();
    }

    public void setMsgEntityList(List<ChatMsgEntity> list) {
        if (list != null)
            this.msgEntityList = list;
    }

    /**
     * 得到Item的类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        ChatMsgEntity entity = getItem(position);
       /* int viewType = IGNORE_ITEM_VIEW_TYPE;
        if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_RECV_TEXT) {
            viewType = 0;
        } else if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_TEXT) {
            viewType = 1;
        } else if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_RECV_AUDIO) {
            viewType = 2;
        } else if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_AUDIO) {
            viewType = 3;
        } else if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_RECV_IMAGE) {
            viewType = 4;
        } else if (entity.getMsgType() == ChatMsgEntity.CHAT_TYPE_SEND_IMAGE) {
            viewType = 5;
        }*/
        return entity.getMsgType();
    }

    /**
     * Item类型的总数
     *
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 6;
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
        int viewType = getItemViewType(position);
        if (viewType == ChatMsgEntity.CHAT_TYPE_RECV_TEXT) {
            TextHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof TextHolder)) {
                holder = new TextHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_text_left, null);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.contentText = (TextView) convertView.findViewById(R.id.tv_chat_content);
                convertView.setTag(holder);
            } else {
                holder = (TextHolder) convertView.getTag();
            }
            if (entity.isShowTime()) {
                holder.dataText.setVisibility(View.VISIBLE);
                holder.dataText.setText(prettyTime.format(new Date(entity.getTimestamp())));
            } else {
                holder.dataText.setVisibility(View.GONE);
            }
            holder.contentText.setText(entity.getText());

        } else if (viewType == ChatMsgEntity.CHAT_TYPE_SEND_TEXT) {
            TextHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof TextHolder)) {
                holder = new TextHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_text_right, null);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.contentText = (TextView) convertView.findViewById(R.id.tv_chat_content);
                convertView.setTag(holder);
            } else {
                holder = (TextHolder) convertView.getTag();
            }
            if (entity.isShowTime()) {
                holder.dataText.setVisibility(View.VISIBLE);
                holder.dataText.setText(prettyTime.format(new Date(entity.getTimestamp())));
            } else {
                holder.dataText.setVisibility(View.GONE);
            }
            holder.contentText.setText(entity.getText());
        } else if (viewType == ChatMsgEntity.CHAT_TYPE_RECV_AUDIO) {
            AudioHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof AudioHolder)) {
                holder = new AudioHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_audio_left, null);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.audioText = (TextView) convertView.findViewById(R.id.tv_chat_audio);
                holder.durationText = (TextView) convertView.findViewById(R.id.tv_duration);
                convertView.setTag(holder);
            } else {
                holder = (AudioHolder) convertView.getTag();
            }
            setItemClickListener(holder.audioText, position);
            if (entity.isAudioPlaying()) {
                holder.audioText.setSelected(true);
                setAudioDrawableAnim(holder.audioText, R.drawable.chating_audio_anim_left, 1);
            } else {
                holder.audioText.setSelected(false);
                setAudioDrawableUnAnim(holder.audioText, R.drawable.chat_icon_speech_left3, 1);
            }
            if (entity.isShowTime()) {
                holder.dataText.setVisibility(View.VISIBLE);
                holder.dataText.setText(prettyTime.format(new Date(entity.getTimestamp())));
            } else {
                holder.dataText.setVisibility(View.GONE);
            }
            int viewWidth = 0;
            try {
                viewWidth = FunctionUtil.calAudioViewWidth(Integer.parseInt(entity.getAudioTime()));
            } catch (NumberFormatException e) {
                e.printStackTrace();

            }
            holder.audioText.setWidth(viewWidth);
            holder.durationText.setText(entity.getAudioTime() + "\"");
        } else if (viewType == ChatMsgEntity.CHAT_TYPE_SEND_AUDIO) {
            AudioHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof AudioHolder)) {
                holder = new AudioHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_audio_right, null);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.audioText = (TextView) convertView.findViewById(R.id.tv_chat_audio);
                holder.durationText = (TextView) convertView.findViewById(R.id.tv_duration);
                convertView.setTag(holder);
            } else {
                holder = (AudioHolder) convertView.getTag();
            }
            setItemClickListener(holder.audioText, position);
            if (entity.isAudioPlaying()) {
                holder.audioText.setSelected(true);
                setAudioDrawableAnim(holder.audioText, R.drawable.chating_audio_anim_right, 2);
            } else {
                holder.audioText.setSelected(false);
                setAudioDrawableUnAnim(holder.audioText, R.drawable.chat_icon_speech_right3, 2);
            }
            if (entity.isShowTime()) {
                holder.dataText.setVisibility(View.VISIBLE);
                holder.dataText.setText(prettyTime.format(new Date(entity.getTimestamp())));
            } else {
                holder.dataText.setVisibility(View.GONE);
            }
            int viewWidth = FunctionUtil.calAudioViewWidth(Integer.parseInt(entity.getAudioTime()));
            holder.audioText.setWidth(viewWidth);
            holder.durationText.setText(entity.getAudioTime() + "\"");
        } else if (viewType == ChatMsgEntity.CHAT_TYPE_RECV_IMAGE) {
            ImageHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof ImageHolder)) {
                holder = new ImageHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_image_left, null);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.imgParent = (ViewGroup) convertView.findViewById(R.id.img_parent);
                holder.imageLayout = convertView.findViewById(R.id.image_layout);
                convertView.setTag(holder);
            } else {
                holder = (ImageHolder) convertView.getTag();
            }
            setItemClickListener(holder.imageLayout, position);
            if (entity.isShowTime()) {
                holder.dataText.setVisibility(View.VISIBLE);
                holder.dataText.setText(prettyTime.format(new Date(entity.getTimestamp())));
            } else {
                holder.dataText.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(entity.getImgThumbnail())) {
                Bitmap bitmap = BitmapFactory.decodeFile(entity.getImgThumbnail());
                MaskView imgView = new MaskView(
                        context, bitmap,
                        (NinePatchDrawable) context.getResources().getDrawable(R.drawable.chat_img_left_mask),
                        screenWidth1p3, screenWidth1p3,
                        screenWidth1p4, screenWidth1p4);
                holder.imgParent.removeAllViews();
                holder.imgParent.addView(imgView);
                ViewGroup.LayoutParams layoutParams = imgView.getLayoutParams();
                layoutParams.height = imgView.getMaskViewSize() != null ?
                        imgView.getMaskViewSize().viewHeight : layoutParams.height;
                layoutParams.width = imgView.getMaskViewSize() != null ?
                        imgView.getMaskViewSize().viewWidth : layoutParams.width;
            }

        } else if (viewType == ChatMsgEntity.CHAT_TYPE_SEND_IMAGE) {
            ImageHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof ImageHolder)) {
                holder = new ImageHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_image_right, null);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.imgParent = (ViewGroup) convertView.findViewById(R.id.img_parent);
                holder.imageLayout = convertView.findViewById(R.id.image_layout);
                convertView.setTag(holder);
            } else {
                holder = (ImageHolder) convertView.getTag();
            }
            setItemClickListener(holder.imageLayout, position);
            if (entity.isShowTime()) {
                holder.dataText.setVisibility(View.VISIBLE);
                holder.dataText.setText(prettyTime.format(new Date(entity.getTimestamp())));
            } else {
                holder.dataText.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(entity.getImgThumbnail())) {
                Bitmap bitmap = BitmapFactory.decodeFile(entity.getImgThumbnail());
                MaskView imgView = new MaskView(
                        context, bitmap,
                        (NinePatchDrawable) context.getResources().getDrawable(R.drawable.chat_img_right_mask),
                        screenWidth1p3, screenWidth1p3,
                        screenWidth1p4, screenWidth1p4);
                holder.imgParent.removeAllViews();
                holder.imgParent.addView(imgView);
                ViewGroup.LayoutParams layoutParams = imgView.getLayoutParams();
                layoutParams.height = imgView.getMaskViewSize() != null ?
                        imgView.getMaskViewSize().viewHeight : layoutParams.height;
                layoutParams.width = imgView.getMaskViewSize() != null ?
                        imgView.getMaskViewSize().viewWidth : layoutParams.width;
            }

        } else {
            // 不兼容的消息
            SysMsgHolder holder;
            if (convertView == null) {
                holder = new SysMsgHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_sysmsg_line, null);
                holder.sysMsgText = (TextView) convertView.findViewById(R.id.tv_sys_msg);
                convertView.setTag(holder);
            } else {
                holder = (SysMsgHolder) convertView.getTag();
            }
        }
        return convertView;
    }

    private void setItemClickListener(final View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int infoType = getItemViewType(position);
                final ChatMsgEntity entity = getItem(position);
                if (infoType == ChatMsgEntity.CHAT_TYPE_RECV_AUDIO || infoType == ChatMsgEntity.CHAT_TYPE_SEND_AUDIO) {
                    VoicePlay.playVoice(entity.getText(), new VoicePlay.OnPlayListener() {
                        @Override
                        public void audioPlay() {
                            entity.setAudioPlaying(true);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void audioStop() {
                            entity.setAudioPlaying(false);
                            notifyDataSetChanged();
                        }
                    });
                } else if (infoType == ChatMsgEntity.CHAT_TYPE_RECV_IMAGE || infoType == ChatMsgEntity.CHAT_TYPE_SEND_IMAGE) {
                    ChatBigImageActivity.startActivity(context, entity, position);
                }
            }
        });
    }

    private void setAudioDrawableAnim(TextView tv, int res, int direction) {
        AnimationDrawable animationDrawable = (AnimationDrawable) context.getResources().getDrawable(res);
        animationDrawable.setBounds(0, 0, animationDrawable.getMinimumWidth(), animationDrawable.getMinimumHeight());
        switch (direction) {
            case 1:// left
                tv.setCompoundDrawables(animationDrawable, null, null, null);
                break;
            case 2:// right
                tv.setCompoundDrawables(null, null, animationDrawable, null);
                break;
        }
        animationDrawable.start();
    }

    public void setAudioDrawableUnAnim(TextView tv, int res, int direction) {
        Drawable drawable = context.getResources().getDrawable(res);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        switch (direction) {
            case 1:// left
                tv.setCompoundDrawables(drawable, null, null, null);
                break;
            case 2:// right
                tv.setCompoundDrawables(null, null, drawable, null);
                break;
        }
    }

    private static class TextHolder {
        TextView dataText;
        TextView contentText;
    }

    private static class AudioHolder {
        TextView dataText;
        TextView audioText;
        private TextView durationText;
    }

    private static class ImageHolder {
        TextView dataText;
        ViewGroup imgParent;
        View imageLayout;
    }

    private static class SysMsgHolder {
        TextView sysMsgText;
    }

}
