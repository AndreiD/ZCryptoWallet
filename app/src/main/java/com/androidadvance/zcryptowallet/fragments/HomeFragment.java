package com.androidadvance.zcryptowallet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import com.androidadvance.zcryptowallet.BaseFragment;
import com.androidadvance.zcryptowallet.R;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends BaseFragment {

  @BindView(R.id.textView_fragmentHome_balance_public) TextView textView_fragmentHome_balance_public;
  @BindView(R.id.textView_fragmentHome_balance_private) TextView textView_fragmentHome_balance_private;
  @BindView(R.id.textView_fragmentHome_status) TextView textView_fragmentHome_status;
  @BindView(R.id.textView_fragmentHome_greeting) TextView textView_fragmentHome_greeting;
  @BindView(R.id.textView_fragmentHome_date) TextView textView_fragmentHome_date;

  public HomeFragment() {
  }

  public static HomeFragment newInstance() {
    HomeFragment fragment = new HomeFragment();
    fragment.setRetainInstance(true);
    return fragment;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_home, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    textView_fragmentHome_balance_public.setText("-");
    textView_fragmentHome_balance_private.setText("-");

    textView_fragmentHome_status.setText("Updating...");
    showGreeting();
  }

  private void showGreeting() {
    Calendar c = Calendar.getInstance();
    int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

    if (timeOfDay >= 1 && timeOfDay < 12) {
      textView_fragmentHome_greeting.setText("Good Morning");
    } else if (timeOfDay >= 12 && timeOfDay < 16) {
      textView_fragmentHome_greeting.setText("Good Afternoon");
    } else if (timeOfDay >= 16 && timeOfDay < 23) {
      textView_fragmentHome_greeting.setText("Good Evening");
    } else if (timeOfDay >= 23 && timeOfDay < 1) {
      textView_fragmentHome_greeting.setText("Good Night");
    }

    DateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault());
    textView_fragmentHome_date.setText(formatter.format(c.getTime()));
  }
}