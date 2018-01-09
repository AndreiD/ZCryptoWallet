package com.androidadvance.zcryptowallet.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import com.androidadvance.zcryptowallet.BaseFragment;
import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.data.local.PreferencesHelper;
import com.androidadvance.zcryptowallet.data.remote.ExchangeRatesAPI;
import com.androidadvance.zcryptowallet.data.remote.TheAPI;
import com.androidadvance.zcryptowallet.utils.DUtils;
import com.androidadvance.zcryptowallet.utils.DialogFactory;
import com.androidadvance.zcryptowallet.utils.SecurityHolder;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.socks.library.KLog;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends BaseFragment {

  @BindView(R.id.textView_fragmentHome_balance_public) TextView textView_fragmentHome_balance_public;
  @BindView(R.id.textView_fragmentHome_balance_private) TextView textView_fragmentHome_balance_private;
  @BindView(R.id.textView_fragmentHome_status) TextView textView_fragmentHome_status;
  @BindView(R.id.textView_fragmentHome_greeting) TextView textView_fragmentHome_greeting;
  @BindView(R.id.textView_fragmentHome_date) TextView textView_fragmentHome_date;
  @BindView(R.id.home_line_chart) LineChart home_line_chart;
  private boolean isjustcreated;

  public HomeFragment() {
  }

  public static HomeFragment newInstance(boolean isjustcreated) {
    HomeFragment fragment = new HomeFragment();
    Bundle args = new Bundle();
    args.putBoolean("isjustcreated", isjustcreated);
    fragment.setRetainInstance(true);
    fragment.setArguments(args);
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

    isjustcreated = getArguments().getBoolean("isjustcreated", false);

    if (!isjustcreated) {
      updateBalances();
    } else {
      textView_fragmentHome_balance_public.setText("0 ZEN");
      textView_fragmentHome_balance_private.setText("0 ZEN");
      textView_fragmentHome_status.setText("Thank you for creating a wallet with us.");
    }

    setupChart();
  }

  private void setupChart() {
    home_line_chart.setDrawGridBackground(false);
    home_line_chart.getDescription().setEnabled(false);
    home_line_chart.setTouchEnabled(false);
    home_line_chart.setDragEnabled(false);
    home_line_chart.setScaleEnabled(false);
    home_line_chart.setPinchZoom(false);
    home_line_chart.setBackgroundColor(Color.WHITE);
    home_line_chart.setDrawGridBackground(false);
    home_line_chart.setDrawBorders(false);

    ArrayList<Entry> values = new ArrayList<>();

    ExchangeRatesAPI exchangeRatesAPI = ExchangeRatesAPI.Factory.getIstance(getActivity());
    exchangeRatesAPI.getHistoryData().enqueue(new Callback<JsonObject>() {
      @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        if(response.code() > 299){
          KLog.e("faild to get historical data for the chart");
          home_line_chart.setVisibility(View.GONE);
          return;
        }

        JsonObject jsonObject = response.body();
        JsonArray jArrayData = jsonObject.get("Data").getAsJsonArray();
        for(int i=0; i<jArrayData.size();i++){
          try {
            JsonObject dataJsonObject = jArrayData.get(i).getAsJsonObject();
            //long time = dataJsonObject.get("time").getAsLong();
            double close = dataJsonObject.get("close").getAsDouble();
            values.add(new Entry(i, (float) close));
          }catch (Exception ignored){}
        }

        if(values.size() == 0){
          return;
        }

        LineDataSet zenToUSDSet = new LineDataSet(values, "ZEN - last 3 months");
        zenToUSDSet.setDrawIcons(false);
        zenToUSDSet.setColor(Color.BLACK);
        zenToUSDSet.setLineWidth(2f);
        zenToUSDSet.setFormLineWidth(1f);
        zenToUSDSet.setDrawCircles(false);
        zenToUSDSet.setDrawValues(false);
        zenToUSDSet.setDrawCircleHole(false);
        zenToUSDSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // remove axis
        YAxis rightAxis = home_line_chart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = home_line_chart.getAxisLeft();
        leftAxis.setTextColor(Color.parseColor("#df7b00"));
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setValueFormatter((value, axis) -> "$"+String.valueOf((int) value));

        XAxis xAxis = home_line_chart.getXAxis();
        xAxis.setEnabled(false);


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(zenToUSDSet);
        LineData data = new LineData(dataSets);

        // set data
        home_line_chart.setData(data);
        home_line_chart.invalidate();

      }

      @Override public void onFailure(Call<JsonObject> call, Throwable t) {
        KLog.e(t.getLocalizedMessage());
        home_line_chart.setVisibility(View.GONE);
      }
    });



  }

  private void updateBalances() {

    TheAPI theAPI = TheAPI.Factory.getIstance(getActivity());
    theAPI.getBalance(DUtils.getUniqueID()).enqueue(new Callback<JsonObject>() {
      @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

        if (response.code() > 299) {
          textView_fragmentHome_status.setText("Failed to get balances for your account.");
          textView_fragmentHome_status.setTextColor(getResources().getColor(R.color.material_red));
          return;
        }

        JsonObject balanceJsonObject = response.body();
        String confirmed_balance_public = balanceJsonObject.get("confirmed_balance_public").getAsString();
        String confirmed_balance_private = balanceJsonObject.get("confirmed_balance_private").getAsString();
        String unconfirmed_balance_public = balanceJsonObject.get("unconfirmed_balance_public").getAsString();
        String unconfirmed_balance_private = balanceJsonObject.get("unconfirmed_balance_private").getAsString();

        confirmed_balance_public = new DecimalFormat("#.#######").format(Double.valueOf(confirmed_balance_public));
        confirmed_balance_private = new DecimalFormat("#.#######").format(Double.valueOf(confirmed_balance_private));
        unconfirmed_balance_public =
            new DecimalFormat("#.#######").format(Double.valueOf(unconfirmed_balance_public) - Double.valueOf(confirmed_balance_public));
        unconfirmed_balance_private =
            new DecimalFormat("#.#######").format(Double.valueOf(unconfirmed_balance_private) - Double.valueOf(confirmed_balance_private));

        SecurityHolder.current_balance_public = Double.valueOf(confirmed_balance_public);
        SecurityHolder.current_balance_private = Double.valueOf(confirmed_balance_private);

        //public balances are equals
        if ((confirmed_balance_public.equals(unconfirmed_balance_public)) || (Double.valueOf(unconfirmed_balance_public) == 0)) {
          textView_fragmentHome_balance_public.setText(confirmed_balance_public + " ZEN");
        } else {
          //there are unconfirmed balances!
          textView_fragmentHome_balance_public.setText(confirmed_balance_public + " ZEN\nPending " + unconfirmed_balance_public + " ZEN");
        }

        //private balances are equals
        if (confirmed_balance_private.equals(unconfirmed_balance_private) || (Double.valueOf(unconfirmed_balance_private) == 0)) {
          textView_fragmentHome_balance_private.setText(confirmed_balance_private + " ZEN");
        } else {
          //there are unconfirmed balances!
          textView_fragmentHome_balance_private.setText(confirmed_balance_private + " ZEN\nPending " + unconfirmed_balance_private + " ZEN");
        }

        textView_fragmentHome_status.setText("all looks good.");
      }

      @Override public void onFailure(Call<JsonObject> call, Throwable t) {
        KLog.e(t.getLocalizedMessage());
        DialogFactory.error_toast(getActivity(), "We couldn't get the balance for this account.").show();
        textView_fragmentHome_status.setText("Failed to get balances for your account.");
        textView_fragmentHome_status.setTextColor(getResources().getColor(R.color.material_red));
      }
    });
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