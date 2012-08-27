package naamakahvi.android.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogHelper {
	/**
	 * Returns an error dialog with the specified message.
	 * 
	 * @param con
	 *            Dialog context
	 * @param message
	 *            Message to show
	 * @return Dialog builder
	 */
	public static AlertDialog.Builder errorDialog(Context con, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(con);
		builder.setCancelable(false);
		builder.setMessage(message);
		builder.setTitle("Error");

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return builder;
	}

	/**
	 * Returns an error dialog with the specified message and finishes the
	 * specified activity when closing the dialog
	 * 
	 * @param con
	 *            Dialog context
	 * @param message
	 *            Message to show
	 * @param finishThis
	 *            Activity to finish
	 * @return Dialog builder
	 */
	public static AlertDialog.Builder errorDialog(Context con, String message, final Activity finishThis) {
		AlertDialog.Builder builder = new AlertDialog.Builder(con);
		builder.setCancelable(false);
		builder.setMessage(message);
		builder.setTitle("Error");

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finishThis.finish();
			}
		});
		return builder;
	}

}