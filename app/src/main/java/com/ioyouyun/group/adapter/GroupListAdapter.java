package com.ioyouyun.group.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.group.model.GroupInfoEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫彪 on 2016/8/9.
 */
public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupViewHolder> {

    private LayoutInflater inflater;
    private OnItemClickLitener onItemClickLitener;
    private List<GroupInfoEntity> groupList = new ArrayList<>();

    public GroupListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setGroupList(List<GroupInfoEntity> groupList) {
        this.groupList = groupList;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_users, parent, false);
        GroupViewHolder myViewHolder = new GroupViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final GroupViewHolder holder, int position) {
        GroupInfoEntity entity = groupList.get(position);
        holder.groupNameText.setText(entity.getName());
        holder.groipIdText.setText(entity.getGid());
        String role = "已加入";
        if(entity.getRole() == 4){
            role = "已创建";
        }
        holder.createOrAddText.setText(role);

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
        return groupList.size();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener onItemClickLitener) {
        this.onItemClickLitener = onItemClickLitener;
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {

        TextView groupNameText;
        TextView groipIdText;
        TextView createOrAddText;
        TextView notifyText;

        public GroupViewHolder(View itemView) {
            super(itemView);
            groupNameText = (TextView) itemView.findViewById(R.id.tv_nickname);
            groipIdText = (TextView) itemView.findViewById(R.id.tv_message);
            createOrAddText = (TextView) itemView.findViewById(R.id.tv_time);
            createOrAddText.setVisibility(View.VISIBLE);
            notifyText = (TextView) itemView.findViewById(R.id.tv_notify);
        }
    }
}
