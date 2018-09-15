package e.manx.mealplanwidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements ManualUpdateDialog.UpdateDialogListener {

    private TextView totalBalanceView;
    private TextView dailyBalanceView;
    private TextView weeklyBalanceView;
    private double totalBalance;
    private double dailyBalance;
    private double weeklyBalance;
    private int dist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);
        try {
            totalBalance = Paper.book().read("total");
        } catch (Exception e) {
            Paper.book().write("total", 0.0);
        }

        try {
            dailyBalance = Paper.book().read("daily");
        } catch (Exception e) {
            Paper.book().write("daily", 0.0);
        }

        try {
            weeklyBalance = Paper.book().read("weekly");
        } catch (Exception e) {
            Paper.book().write("weekly", 0.0);
        }

        totalBalanceView = (TextView) findViewById(R.id.totalBalanceView);
        dailyBalanceView = (TextView) findViewById(R.id.dailyBalanceView);
        weeklyBalanceView = (TextView) findViewById(R.id.weeklyBalanceView);

        NumberFormat nForm = NumberFormat.getCurrencyInstance();
        totalBalanceView.setText(nForm.format(totalBalance));
        dailyBalanceView.setText(nForm.format(dailyBalance));
        weeklyBalanceView.setText(nForm.format(weeklyBalance));

        TextView dateView;
        dateView = (TextView) findViewById(R.id.dateView);

        Button autoButton, manualButton;
        autoButton = (Button) findViewById(R.id.aButton);
        manualButton = (Button) findViewById(R.id.mButton);

        Date curr, end;
        DateFormat dForm = DateFormat.getDateInstance(DateFormat.LONG);
        curr = new Date();
        dateView.setText(dForm.format(curr));
        Calendar cal = Calendar.getInstance();
        cal.set(2018,11,16,0,0,0);
        end = cal.getTime();
        long diff = end.getTime() - curr.getTime();
        dist = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        Context context = this;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.meal_plan_widget);
        ComponentName thisWidget = new ComponentName(context, MealPlanWidget.class);
        remoteViews.setTextViewText(R.id.dailyBalanceWidgetView, nForm.format(dailyBalance));
        remoteViews.setTextViewText(R.id.weeklyBalanceWidgetView, nForm.format(weeklyBalance));
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "//TODO: add auto update functionality",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    public void openDialog() {
        ManualUpdateDialog updateDialog = new ManualUpdateDialog();
        Bundle args = new Bundle();
        args.putDouble("total", totalBalance);
        updateDialog.setArguments(args);
        updateDialog.show(getSupportFragmentManager(), "update dialog");
    }

    @Override
    public void applyUpdate(double balance, boolean eaten) {
        totalBalance = balance;
        Paper.book().write("total", balance);
        if (eaten) {
            dailyBalance = 0.0;
            Paper.book().write("daily", dailyBalance);
            weeklyBalance = (dist%7) * (balance/(dist-1));
            Paper.book().write("weekly", weeklyBalance);
        }
        else {
            dailyBalance = balance/dist;
            Paper.book().write("daily", dailyBalance);
            weeklyBalance = (dist%7) * dailyBalance;
            Paper.book().write("weekly", weeklyBalance);
        }
        NumberFormat nForm = NumberFormat.getCurrencyInstance();
        totalBalanceView.setText(nForm.format(totalBalance));
        dailyBalanceView.setText(nForm.format(dailyBalance));
        weeklyBalanceView.setText(nForm.format(weeklyBalance));

        Context context = this;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.meal_plan_widget);
        ComponentName thisWidget = new ComponentName(context, MealPlanWidget.class);
        remoteViews.setTextViewText(R.id.dailyBalanceWidgetView, nForm.format(dailyBalance));
        remoteViews.setTextViewText(R.id.weeklyBalanceWidgetView, nForm.format(weeklyBalance));
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }
}






























