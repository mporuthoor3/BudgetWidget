package e.manx.mealplanwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

/**
 * Implementation of App Widget functionality.
 */
public class MealPlanWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //Init Paper
        Paper.init(context);

        double totalBalance = Paper.book().read("total");
        double dailyBalance = Paper.book().read("daily");
        double weeklyBalance;

        Date curr, end;
        curr = new Date();
        Calendar cal = Calendar.getInstance();
        cal.set(2018,11,16,0,0,0);
        end = cal.getTime();
        long diff = end.getTime() - curr.getTime();
        double dist = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        if (dailyBalance == 0.0 && curr.getHours() < 7) {
            weeklyBalance = (dist%7) * (totalBalance/(dist-1));
            Paper.book().write("weekly", weeklyBalance);
        }
        else {
            dailyBalance = totalBalance/dist;
            Paper.book().write("daily", dailyBalance);
            weeklyBalance = (dist%7) * dailyBalance;
            Paper.book().write("weekly", weeklyBalance);
        }

        NumberFormat nForm = NumberFormat.getCurrencyInstance();

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.meal_plan_widget);
        views.setTextViewText(R.id.dailyBalanceWidgetView, nForm.format(dailyBalance));
        views.setTextViewText(R.id.weeklyBalanceWidgetView, nForm.format(weeklyBalance));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.meal_plan_widget);
        // When we click the widget, we want to open our main activity.
        Intent launchActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchActivity, 0);
        remoteViews.setOnClickPendingIntent(R.id.widgetView, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

