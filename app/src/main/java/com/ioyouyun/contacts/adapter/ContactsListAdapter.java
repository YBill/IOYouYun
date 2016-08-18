package com.ioyouyun.contacts.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.contacts.model.NearbyUserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫彪 on 2016/6/25.
 */
public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ContactsViewHolder> {

    private OnItemClickLitener onItemClickLitener;
    private List<NearbyUserEntity> contactsList = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    private boolean isShowCheck = false;

    public ContactsListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setContactsList(List<NearbyUserEntity> contactsList) {
        this.contactsList = contactsList;
    }

    /**
     * @param flag true：显示CheckBox
     */
    public void setFlags(boolean flag) {
        isShowCheck = flag;
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_users, parent, false);
        ContactsViewHolder myViewHolder = new ContactsViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final ContactsViewHolder holder, int position) {
        final NearbyUserEntity entity = contactsList.get(position);
        holder.nicknameText.setText(entity.getNickname());
        holder.uidText.setText(entity.getId());

        if (isShowCheck) {
            holder.userCb.setVisibility(View.VISIBLE);
            holder.userCb.setChecked(entity.isChecked());
        }

        holder.userCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entity.setChecked(holder.userCb.isChecked());
            }
        });

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
        return contactsList.size();
    }

    public NearbyUserEntity getItem(int position){
        return contactsList.get(position);
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener onItemClickLitener) {
        this.onItemClickLitener = onItemClickLitener;
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView nicknameText;
        TextView uidText;
        CheckBox userCb;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            nicknameText = (TextView) itemView.findViewById(R.id.tv_nickname);
            uidText = (TextView) itemView.findViewById(R.id.tv_message);
            userCb = (CheckBox) itemView.findViewById(R.id.cb_user);
        }
    }
}
