package net.ohmnibus.grimorium.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
 * Created by Ohmnibus on 18/06/2017.
 */

public class ProgressFragmentDialog extends DialogFragment {

	private static final String NAME = ProgressFragmentDialog.class.getName();
	private static final String KEY_TITLE = NAME + "_title";
	private static final String KEY_MESSAGE = NAME + "_message";
	private static final String KEY_INDETERMINATE = NAME + "_indeterminate";
	private static final String KEY_CANCELABLE = NAME + "_cancelable";
	private static final String KEY_CANCELED_ON_TOUCH_OUTSIDE = NAME + "_canceled_on_touch_outside";

	private ProgressDialog mDialog;

	public static ProgressFragmentDialog showMessageDialog(
			FragmentActivity activity,
			@StringRes int title,
			@StringRes int message,
			boolean indeterminate,
			boolean cancelable,
			boolean canceledOnTouchOutside,
			@Nullable String tag) {

		return showMessageDialog(
				activity,
				title != 0 ? activity.getString(title) : null,
				activity.getString(message),
				indeterminate,
				cancelable,
				canceledOnTouchOutside,
				tag);
	}

	public static ProgressFragmentDialog showMessageDialog(
			FragmentActivity activity,
			@Nullable CharSequence title,
			CharSequence message,
			boolean indeterminate,
			boolean cancelable,
			boolean canceledOnTouchOutside,
			@Nullable String tag) {

		final FragmentManager manager = activity.getSupportFragmentManager();
		if (manager == null || manager.isDestroyed()) {
			return null;
		}
		final ProgressFragmentDialog dialogFragment = new ProgressFragmentDialog();
		final Bundle args = new Bundle();
		args.putCharSequence(KEY_TITLE, title);
		args.putCharSequence(KEY_MESSAGE, message);
		args.putBoolean(KEY_INDETERMINATE, indeterminate);
		args.putBoolean(KEY_CANCELABLE, cancelable);
		args.putBoolean(KEY_CANCELED_ON_TOUCH_OUTSIDE, canceledOnTouchOutside);

		dialogFragment.setArguments(args);
		dialogFragment.show(manager, tag);

		return dialogFragment;
	}


	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Bundle args = getArguments() == null ? Bundle.EMPTY : getArguments();

		mDialog = new ProgressDialog(getContext());
		mDialog.setTitle(args.getCharSequence(KEY_TITLE));
		mDialog.setMessage(args.getCharSequence(KEY_MESSAGE));
		mDialog.setIndeterminate(args.getBoolean(KEY_INDETERMINATE));
		/* mDialog.*/ setCancelable(args.getBoolean(KEY_CANCELABLE));
		mDialog.setCanceledOnTouchOutside(args.getBoolean(KEY_CANCELED_ON_TOUCH_OUTSIDE));

		return mDialog;
	}

	public void setMessage(CharSequence message) {
		mDialog.setMessage(message);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if (getActivity() instanceof DialogInterface.OnDismissListener) {
			((DialogInterface.OnDismissListener) getActivity()).onDismiss(dialog);
		}
	}
}
