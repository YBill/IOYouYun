package com.ioyouyun.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseFragment;
import com.ioyouyun.group.activity.CreateGroupActivity;
import com.ioyouyun.group.model.GroupInfoEntity;
import com.ioyouyun.group.presenter.GroupPresenter;
import com.ioyouyun.group.view.GroupView;
import com.ioyouyun.group.adapter.GroupListAdapter;
import com.ioyouyun.home.widgets.DividerItemDecoration;
import com.ioyouyun.home.widgets.ScrollChildSwipeRefreshLayout;

import java.util.List;

/**
 * Created by 卫彪 on 2016/8/9.
 */
public class GroupFragment extends BaseFragment<GroupView, GroupPresenter> implements GroupView {

    private ScrollChildSwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private GroupListAdapter groupAdapter;

    public static final String ARGUMENT_HOME_ID = "HOME_ID";

    @Override
    protected GroupPresenter initPresenter() {
        return new GroupPresenter(getActivity());
    }

    public static GroupFragment newInstance(String homeId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_HOME_ID, homeId);
        GroupFragment fragment = new GroupFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public GroupFragment() {
        // Required empty public constructor
        Log.v("Bill", "GroupFragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Bill", "GroupFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("Bill", "GroupFragment onCreateView");
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_group);
        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupAdapter = new GroupListAdapter(getActivity());
        recyclerView.setAdapter(groupAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        groupAdapter.setOnItemClickLitener(new GroupListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                presenter.onItemClick(position);
            }
        });

        swipeRefreshLayout = (ScrollChildSwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(recyclerView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getGroupList();
            }
        });

        FloatingActionButton fab =
                (FloatingActionButton) view.findViewById(R.id.fab_add_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.getGroupList();
    }

    /**
     * 刷新List
     *
     * @param list
     */
    private void refreshAdapter(List<GroupInfoEntity> list) {
        if (groupAdapter != null) {
            groupAdapter.setGroupList(list);
            groupAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setListView(List<GroupInfoEntity> list) {
        swipeRefreshLayout.setRefreshing(false);
        refreshAdapter(list);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
