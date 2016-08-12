package com.ioyouyun.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ioyouyun.R;

import java.util.List;

/**
 * Created by 卫彪 on 2016/6/3.
 */
public class ExpressionGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<String> emojiList;

    public ExpressionGridViewAdapter(Context context, List<String> emojiList){
        this.context = context;
        this.emojiList = emojiList;
    }

    @Override
    public int getCount() {
        return emojiList.size();
    }

    @Override
    public String getItem(int position) {
        return emojiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String emoji = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.adapter_row_expression, null);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_expression);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(emoji);

        return convertView;
    }

    private class ViewHolder{
        TextView textView;
    }

}
