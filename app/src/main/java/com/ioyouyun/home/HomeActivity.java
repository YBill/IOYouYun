package com.ioyouyun.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.home.adapter.HomeViewPagerAdapter;
import com.ioyouyun.home.fragment.ContactsFragment;
import com.ioyouyun.home.fragment.GroupFragment;
import com.ioyouyun.home.fragment.MessageFragment;
import com.ioyouyun.home.presenter.HomePresenter;
import com.ioyouyun.home.view.HomeView;
import com.ioyouyun.settings.VersionActivity;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.PushSharedUtil;

public class HomeActivity extends BaseActivity<HomeView, HomePresenter>
        implements HomeView, NavigationView.OnNavigationItemSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private HomeViewPagerAdapter adapter;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView userNameText;
    private TextView uidText;
    private MenuItem homeMemu;
    private MenuItem soundMemu;
    private MenuItem vibrateMemu;
    private CheckBox soundCb;
    private CheckBox vibrateCb;
    private TextView pushTextView;

    @Override
    protected HomePresenter initPresenter() {
        return new HomePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(FunctionUtil.nickname);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void initView() {
        setToolBar();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        userNameText = (TextView) view.findViewById(R.id.tv_user_name);
        uidText = (TextView) view.findViewById(R.id.tv_uid);

        Menu menu = navigationView.getMenu();
        homeMemu = menu.findItem(R.id.menu_home);
        MenuItem pushMenu = menu.findItem(R.id.menu_push_time);
        View pushView = MenuItemCompat.getActionView(pushMenu);
        pushTextView = (TextView) pushView.findViewById(R.id.tv_push_time);
        soundMemu = menu.findItem(R.id.menu_push_sound);
        View soundView = MenuItemCompat.getActionView(soundMemu);
        soundCb = (CheckBox) soundView.findViewById(R.id.checkbox);
        vibrateMemu = menu.findItem(R.id.menu_push_vibrate);
        View vibrateView = MenuItemCompat.getActionView(vibrateMemu);
        vibrateCb = (CheckBox) vibrateView.findViewById(R.id.checkbox);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);

    }

    @Override
    protected void setListener() {
        navigationView.setNavigationItemSelectedListener(this);
        soundCb.setOnClickListener(this);
        vibrateCb.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        userNameText.setText(FunctionUtil.nickname);
        uidText.setText(FunctionUtil.uid);

        vibrateCb.setChecked(PushSharedUtil.getInstance().getVibration());
        soundCb.setChecked(PushSharedUtil.getInstance().getSound());

        setupViewPager(viewPager);
    }

    @Override
    public void widgetClick(View v) {
        if(v == soundCb){
            PushSharedUtil.getInstance().setSound(soundCb.isChecked());
        }else if(v == vibrateCb){
            PushSharedUtil.getInstance().setVibration(vibrateCb.isChecked());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getPushInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new HomeViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MessageFragment.newInstance("message"), "消息");
        adapter.addFragment(ContactsFragment.newInstance("contacts"), "联系人");
        adapter.addFragment(GroupFragment.newInstance("group"), "群组");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private int startTime = 0;
    private int endTime = 24;

    private void showPushPicker() {
        View view = View.inflate(this, R.layout.layout_push_picker, null);
        final NumberPicker startTimePicker = (NumberPicker) view.findViewById(R.id.np_start_time);
        final NumberPicker endTimePicker = (NumberPicker) view.findViewById(R.id.np_end_time);

        startTimePicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        startTimePicker.setMaxValue(24);
        startTimePicker.setMinValue(0);
        startTimePicker.setValue(startTime);
        startTimePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                startTimePicker.setValue(newVal);
            }
        });

        endTimePicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        endTimePicker.setMaxValue(24);
        endTimePicker.setMinValue(0);
        endTimePicker.setValue(endTime);
        endTimePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                endTimePicker.setValue(newVal);
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.push_alert_time)).setView(view)
                .setPositiveButton(getResources().getString(R.string.string_setting), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.setPushInfo(startTimePicker.getValue(), endTimePicker.getValue());
                    }
                })
                .setNegativeButton(getResources().getString(R.string.string_cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.
        switch (menuItem.getItemId()) {
            case R.id.menu_home:
                menuItem.setChecked(true);
                drawer.closeDrawer(navigationView);
                break;
            case R.id.menu_push_sound:
                menuItem.setChecked(true);
                boolean isSound = !soundCb.isChecked();
                soundCb.setChecked(isSound);
                PushSharedUtil.getInstance().setSound(isSound);
                break;
            case R.id.menu_push_vibrate:
                menuItem.setChecked(true);
                boolean isVibrate = !vibrateCb.isChecked();
                vibrateCb.setChecked(isVibrate);
                PushSharedUtil.getInstance().setVibration(isVibrate);
                break;
            case R.id.menu_push_time:
                menuItem.setChecked(true);
                showPushPicker();
//                drawer.closeDrawer(navigationView);
                break;
            case R.id.menu_version:
                menuItem.setChecked(true);
                drawer.closeDrawer(navigationView);

                Intent intent = new Intent(this, VersionActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void setPushTime(int startTime, int endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        String start, end;
        if (startTime < 10)
            start = "0" + startTime;
        else
            start = String.valueOf(startTime);
        if (endTime < 10)
            end = "0" + endTime;
        else
            end = String.valueOf(endTime);
        start += ":00";
        end += ":00";
        pushTextView.setText(start + "-" + end);
    }
}
