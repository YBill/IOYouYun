package com.ioyouyun.media.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ioyouyun.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫彪 on 2016/7/22.
 */
public class MemberListAdapter extends BaseAdapter {

    private List<String> memberList = new ArrayList<>();
    private LayoutInflater mInflater;

    public MemberListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setMemberList(List<String> memberList) {
        this.memberList = memberList;
    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    @Override
    public String getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_conference_member, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvNumber = (TextView) convertView.findViewById(R.id.tv_conf_number);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_conf_nickName);
            viewHolder.tvUid = (TextView) convertView.findViewById(R.id.tv_conf_uid);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvNumber.setText(String.valueOf(position + 1));
        viewHolder.tvName.setText(getItem(position));
        viewHolder.tvUid.setText(getItem(position));

        return convertView;
    }

    private static class ViewHolder {
        TextView tvNumber;
        TextView tvName;
        TextView tvUid;
    }

}
