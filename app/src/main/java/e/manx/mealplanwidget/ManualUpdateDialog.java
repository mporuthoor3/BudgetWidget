package e.manx.mealplanwidget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

public class ManualUpdateDialog extends AppCompatDialogFragment {

    private EditText balanceInputText;
    private Switch eatenTodaySwitch;
    private UpdateDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setTitle("Manual Update")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        double balance = Double.parseDouble(balanceInputText.getText().toString());
                        boolean eaten = eatenTodaySwitch.isChecked();
                        listener.applyUpdate(balance, eaten);
                    }
                });

        balanceInputText = view.findViewById(R.id.balanceInput);
        eatenTodaySwitch = view.findViewById(R.id.eatenSwitch);
        Bundle args = getArguments();
        double total = args.getDouble("total");
        String tmp = total + "";
        balanceInputText.setText(tmp);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (UpdateDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement UpdateDialogListener");

        }

    }

    public interface UpdateDialogListener {
        void applyUpdate(double balance, boolean eaten);
    }

}
