package net.ohmnibus.grimorium.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import net.ohmnibus.grimorium.R;

/**
 * Created by Ohmnibus on 02/09/2016.
 */
public class InputTextFragmentDialog extends DialogFragment
implements DialogInterface.OnClickListener {

	//private static final String ARG_ACTION = "ARG_ACTION";
	private static final String ARG_TITLE_RES_ID = "ARG_TITLE_RES_ID";
	private static final String ARG_MESSAGE_RES_ID = "ARG_MESSAGE_RES_ID";
	private static final String ARG_HINT_RES_ID = "ARG_HINT_RES_ID";
	private static final String ARG_DEFAULT_VALUE = "ARG_DEFAULT_VALUE";

	int mRequestCode;
	int mTitleResId;
	int mMessageResId;
	int mHintResId;
	String mDefaultValue;

	OnFragmentInteractionListener mListener;
	TextView mMessage;
	EditText mEdit;

	public static InputTextFragmentDialog newInstance(
			//int action,
			int titleResId,
			int messageResId,
			int hintResId,
			String defaultValue) {
		InputTextFragmentDialog fragment = new InputTextFragmentDialog();
		Bundle args = new Bundle();
		//args.putInt(ARG_ACTION, action);
		args.putInt(ARG_TITLE_RES_ID, titleResId);
		args.putInt(ARG_MESSAGE_RES_ID, messageResId);
		args.putInt(ARG_HINT_RES_ID, hintResId);
		args.putString(ARG_DEFAULT_VALUE, defaultValue);
		fragment.setArguments(args);
		return fragment;
	}

	public InputTextFragmentDialog() {}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
//		if (context instanceof OnFragmentInteractionListener) {
//			mListener = (OnFragmentInteractionListener) context;
//		} else {
//			throw new RuntimeException(context.toString()
//					+ " must implement OnListFragmentInteractionListener");
//		}
		if (getTargetFragment() instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) getTargetFragment();
			mRequestCode = getTargetRequestCode();
		} else if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
			//mRequestCode = -1;
		} else {
			if (getTargetFragment() == null) {
				throw new RuntimeException(context.toString()
						+ " must implement OnListFragmentInteractionListener -or- define a target fragment");
			} else {
				throw new RuntimeException(getTargetFragment().toString()
					+ " must implement OnListFragmentInteractionListener");
			}
		}
//		} else if (getTargetFragment() == null) {
//			throw new RuntimeException("Must define a target fragment");
//		} else {
//			throw new RuntimeException(getTargetFragment().toString()
//					+ " must implement OnListFragmentInteractionListener");
//		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		if (args != null) {
			//mRequestCode = args.getInt(ARG_ACTION);
			mTitleResId = args.getInt(ARG_TITLE_RES_ID);
			mMessageResId = args.getInt(ARG_MESSAGE_RES_ID);
			mHintResId = args.getInt(ARG_HINT_RES_ID);
			mDefaultValue = args.getString(ARG_DEFAULT_VALUE);
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		//return super.onCreateDialog(savedInstanceState);
		LayoutInflater inflater;
		if (getActivity() == null) {
			inflater = LayoutInflater.from(getContext());
		} else {
			inflater = getActivity().getLayoutInflater();
		}

		AlertDialog.Builder bld = new AlertDialog.Builder(getContext());

		bld.setView(createView(inflater, null, null));
		if (mTitleResId > 0) {
			bld.setTitle(mTitleResId);
		}
		if (mMessageResId > 0) {
			mMessage.setText(mMessageResId);
			mMessage.setVisibility(View.VISIBLE);
		} else {
			mMessage.setVisibility(View.GONE);
		}
		if (mHintResId > 0) {
			mEdit.setHint(mHintResId);
		}
		if (mDefaultValue != null) {
			mEdit.setText(mDefaultValue);
		}
		bld.setPositiveButton(R.string.lbl_ok, this);
		bld.setNegativeButton(R.string.lbl_cancel, this);

		return bld.create();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View retVal;
		if (getShowsDialog()) {
			retVal = super.onCreateView(inflater, container, savedInstanceState);
		} else {
			retVal = createView(inflater, container, savedInstanceState);
		}
		return retVal;
	}

	@SuppressWarnings("unused")
	protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_input, container, false);

		mEdit = view.findViewById(R.id.edit);
		mMessage = view.findViewById(R.id.message);

		return view;
	}

	@Override
	public void onClick(DialogInterface dialogInterface, int whichButton) {
		if (whichButton == Dialog.BUTTON_POSITIVE) {
			if (mListener != null) {
				mListener.onTextEntered(mRequestCode, mEdit.getText().toString());
			}
		}
	}

//	public void setRequestCode(int requestCode) {
//		mRequestCode = requestCode;
//	}

	public interface OnFragmentInteractionListener {
		void onTextEntered(int requestCode, String text);
	}
}
