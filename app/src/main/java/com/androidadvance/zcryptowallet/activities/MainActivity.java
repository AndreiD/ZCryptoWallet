package com.androidadvance.zcryptowallet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.androidadvance.zcryptowallet.BaseActivity;
import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.fragments.AddressBookFragment;
import com.androidadvance.zcryptowallet.fragments.HomeFragment;
import com.androidadvance.zcryptowallet.fragments.ReceiveFragment;
import com.androidadvance.zcryptowallet.fragments.SampleFragment;
import com.androidadvance.zcryptowallet.fragments.SendFragment;
import com.roughike.bottombar.BottomBar;
import com.socks.library.KLog;
import hotchemi.android.rate.AppRate;
import org.greenrobot.eventbus.util.ErrorDialogManager;

public class MainActivity extends BaseActivity {

  @BindView(R.id.relayout_main) RelativeLayout relayout_main;

  @BindView(R.id.theToolbar) Toolbar theToolbar;

  @BindView(R.id.bottomBar) BottomBar bottomBar;

  private MainActivity mContext;
  private boolean isjustcreated = false;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    setSupportActionBar(theToolbar);
    getSupportActionBar().setElevation(0);
    mContext = MainActivity.this;

    Bundle bundle = getIntent().getExtras();
    if((bundle != null) && bundle.containsKey("isjustcreated")) {
      isjustcreated = bundle.getBoolean("isjustcreated", false);
    }

    bottomBar.setDefaultTab(R.id.tab_home);
    bottomBar.setOnTabSelectListener(tabId -> {
      if (tabId == R.id.tab_send) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.contentContainer, SendFragment.newInstance(isjustcreated));
        t.addToBackStack("fragment_send");
        t.commit();
      }
      if (tabId == R.id.tab_home) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.contentContainer, HomeFragment.newInstance(isjustcreated));
        t.addToBackStack("fragment_home");
        t.commit();
      }
      if (tabId == R.id.tab_receive) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.contentContainer, ReceiveFragment.newInstance());
        t.addToBackStack("fragment_receive");
        t.commit();
      }
      if (tabId == R.id.tab_address_book) {
               FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.contentContainer, AddressBookFragment.newInstance());
        t.addToBackStack("fragment_home");
        t.commit();
      }
    });

    rate_this_app_logic();
  }

  private void rate_this_app_logic() {
    AppRate.with(this).setInstallDays(10).setLaunchTimes(10).setRemindInterval(2).setShowLaterButton(false).setDebug(false).monitor();
    AppRate.showRateDialogIfMeetsConditions(this);
  }

  public void navigateToHome() {
    bottomBar.selectTabAtPosition(1);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      //case R.id.action_address_book:
      //  FragmentTransaction t = getSupportFragmentManager().beginTransaction();
      //  t.replace(R.id.contentContainer, AddressBookFragment.newInstance());
      //  t.addToBackStack("fragment_address_book");
      //  t.commit();
      //  break;
      case R.id.action_exit:
        finish();
        break;
      case R.id.action_settings:
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        break;
      default:
        break;
    }
    return true;
  }
}


