package naamakahvi.android.utils;

import naamakahvi.android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

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
	public static AlertDialog.Builder errorDialog(final Context con, final String message) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(con);
		builder.setCancelable(false);
		builder.setMessage(message);
		builder.setTitle("Error");

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int which) {
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
	public static AlertDialog.Builder errorDialog(final Context con, final String message, final Activity finishThis) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(con);
		builder.setCancelable(false);
		builder.setMessage(message);
		builder.setTitle("Error");

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.dismiss();
				finishThis.finish();
			}
		});
		return builder;
	}

	/**
	 * Returns a toast
	 * 
	 * @param con
	 *            context for the toast
	 * @param resId
	 *            Resource Id of stirng to display
	 * @return toast object
	 */
	public static Toast makeToast(final Context con, final int resId) {
		final LayoutInflater i = LayoutInflater.from(con);
		final TextView txt = (TextView) i.inflate(R.layout.toast_bigtext, null);
		txt.setText(con.getString(resId));
		final Toast t = Toast.makeText(con, "", Toast.LENGTH_LONG);
		t.setView(txt);
		return t;
	}

	/**
	 * Returns a toast
	 * 
	 * @param con
	 *            context for the toast
	 * @param s
	 *            Message to show
	 * @return toast object
	 */
	public static Toast makeToast(final Context con, final String s) {
		final LayoutInflater i = LayoutInflater.from(con);
		final TextView txt = (TextView) i.inflate(R.layout.toast_bigtext, null);
		txt.setText(s);
		final Toast t = Toast.makeText(con, "", Toast.LENGTH_LONG);
		t.setView(txt);
		return t;
	}
}
