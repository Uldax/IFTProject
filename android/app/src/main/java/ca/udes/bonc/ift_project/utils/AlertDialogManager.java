package ca.udes.bonc.ift_project.utils;

/**
 * Created by pcontat on 29/10/2015.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogManager {
    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     *               - pass null if you don't want icon
     * */
    public void showAlertDialogWithOk(Context context, String title, String message,
                                Boolean status) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);


        // Setting Dialog Title
        alertDialogBuilder.setTitle(title);
        // Setting Dialog Message
        alertDialogBuilder


                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }


    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.
                setTitle(title)
                .setMessage(message);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


}
