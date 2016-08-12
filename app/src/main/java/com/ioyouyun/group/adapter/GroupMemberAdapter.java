package com.ioyouyun.group.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.group.model.GroupMemberEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 卫彪 on 2016/7/8.
 */
public class GroupMemberAdapter extends BaseAdapter {

    private List<GroupMemberEntity> groupMemberEntityList = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    public GroupMemberAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setMemberList(List<GroupMemberEntity> groupMemberEntityList) {
        if (groupMemberEntityList != null)
            this.groupMemberEntityList = groupMemberEntityList;
    }

    @Override
    public int getCount() {
        return groupMemberEntityList.size();
    }

    @Override
    public GroupMemberEntity getItem(int position) {
        return groupMemberEntityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GroupMemberEntity entity = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_users, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        String role = "";
        if(entity.getRole() == 4)
            role = "群主";
        else if(entity.getRole() == 3)
            role = "管理员";
        else if(entity.getRole() == 1)
            role = "成员";

        holder.tvNickname.setText(entity.getNickname() + "\t" + "[" + role + "]");
        holder.tvMessage.setText(entity.getId());

        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.tv_nickname)
        TextView tvNickname;
        @BindView(R.id.tv_message)
        TextView tvMessage;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
