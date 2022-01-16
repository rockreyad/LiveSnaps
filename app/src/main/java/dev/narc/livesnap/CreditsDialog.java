package dev.narc.livesnap;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

public class CreditsDialog {


    public CreditsDialog(Activity activity, String msg) {


        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.credits_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        TextView text = (TextView) dialog.findViewById(R.id.txt_file_path);
        text.setText(msg);
        dialog.show();

    }
}
