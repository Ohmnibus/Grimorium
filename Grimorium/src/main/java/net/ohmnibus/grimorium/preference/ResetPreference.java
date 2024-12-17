package net.ohmnibus.grimorium.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.preference.Preference;
import android.util.AttributeSet;

import net.ohmnibus.grimorium.R;

/**
 * Created by Ohmnibus on 05/08/2016.
 */
public class ResetPreference extends Preference implements DialogInterface.OnClickListener {
	public ResetPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onClick() {
		super.onClick();
		AlertDialog.Builder bld = new AlertDialog.Builder(getContext());
		bld.setTitle(R.string.pref_reset_title);
		bld.setMessage(R.string.pref_reset_message);
		bld.setPositiveButton(R.string.lbl_ok, this);
		bld.setNegativeButton(R.string.lbl_cancel, null);
		bld.show();
	}

	@Override
	public void onClick(DialogInterface dialogInterface, int button) {
		//notifyChanged();
		callChangeListener(true);
	}

}
