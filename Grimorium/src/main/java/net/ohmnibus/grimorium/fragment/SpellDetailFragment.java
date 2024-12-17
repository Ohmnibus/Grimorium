package net.ohmnibus.grimorium.fragment;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import net.ohmnibus.grimorium.BuildConfig;
import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.database.SpellProfileDbAdapter;
import net.ohmnibus.grimorium.databinding.FragmentSpellDetailBinding;
import net.ohmnibus.grimorium.entity.Spell;
import net.ohmnibus.grimorium.entity.SpellProfile;
import net.ohmnibus.grimorium.helper.SpellFormatter;
import net.ohmnibus.grimorium.helper.Utils;
import net.ohmnibus.grimorium.entity.ads.MobileAdsWrapper;

public class SpellDetailFragment extends Fragment
		implements CompoundButton.OnCheckedChangeListener {

	private static final String TAG = "SpellDetailFragment";
	private static final String ARG_SPELL = "spellProfile";
	private static final String ARG_SHOW_TITLE = "showTitle";
	private static final String ARG_SHOW_ADS = "showAds";

	private SpellProfile mSpellProfile;
	private boolean mStarred;
	private boolean mShowTitle;
	private boolean mShowAds;
	private FragmentSpellDetailBinding mBinding;

	private OnFragmentInteractionListener mListener;

	public static SpellDetailFragment newInstance(SpellProfile spellProfile, boolean showTitle, boolean showAds) {

		SpellDetailFragment fragment = new SpellDetailFragment();

		Bundle args = new Bundle();
		args.putParcelable(ARG_SPELL, spellProfile);
		args.putBoolean(ARG_SHOW_TITLE, showTitle);
		args.putBoolean(ARG_SHOW_ADS, showAds);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);

		if (getArguments() != null) {
			mSpellProfile = getArguments().getParcelable(ARG_SPELL);
			mShowTitle = getArguments().getBoolean(ARG_SHOW_TITLE);
			mShowAds = getArguments().getBoolean(ARG_SHOW_ADS);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_spell_detail, container, false);

		initViews(root);

		return root;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.getClass().getSimpleName()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.menu_spell_detail, menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.mnuSpellShare) {
			shareSpell();
		} else {
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void initViews(View root) {

		//mWebView = (WebView) root.findViewById(R.id.webView);
		mBinding = DataBindingUtil.bind(root);

		refreshSpell();
	}

	private void refreshSpell() {
		if (mSpellProfile != null) {

			SpellFormatter spellFmt = new SpellFormatter(getContext(), mSpellProfile);

			//Star
			mBinding.cbPreferred.setOnCheckedChangeListener(this);
			showStar(mSpellProfile.isStarred());

			//Title
			if (mShowTitle) {
				mBinding.tvTitle.setText(spellFmt.getName());
				mBinding.tvTitle.setSelected(true); //For marquee effect
				mBinding.tvTitle.setVisibility(View.VISIBLE);
			} else {
				mBinding.tvTitle.setVisibility(View.GONE);
			}

			//Level, reversibility
			mBinding.tvLevel.setText(spellFmt.getLevelLong());
			if (mSpellProfile.isReversible()) {
				mBinding.tvReversible.setVisibility(View.VISIBLE);
			} else {
				mBinding.tvReversible.setVisibility(View.GONE);
			}

			//Schools
			mBinding.tvSchool.setText(spellFmt.getSchool(true));
			mBinding.tvSchool.setSelected(true); //For marquee effect

			//Spheres
			if (mSpellProfile.getType() == Spell.TYPE_CLERICS) {
				mBinding.tvSphere.setText(spellFmt.getSphere());
				mBinding.tvSphere.setSelected(true); //For marquee effect
				mBinding.tvSphere.setVisibility(View.VISIBLE);
				mBinding.tvSphereLabel.setVisibility(View.VISIBLE);
			} else {
				mBinding.tvSphere.setVisibility(View.GONE);
				mBinding.tvSphereLabel.setVisibility(View.GONE);
			}

			mBinding.tvRange.setText(spellFmt.getRange());
			mBinding.tvRange.setSelected(true); //For marquee effect
			mBinding.tvCompo.setText(spellFmt.getCompos());
			mBinding.tvCompo.setSelected(true); //For marquee effect
			mBinding.tvDuration.setText(spellFmt.getDuration());
			mBinding.tvDuration.setSelected(true); //For marquee effect
			mBinding.tvCastTime.setText(spellFmt.getCastingTime());
			mBinding.tvCastTime.setSelected(true); //For marquee effect
			mBinding.tvAreaEffect.setText(spellFmt.getArea());
			mBinding.tvAreaEffect.setSelected(true); //For marquee effect
			mBinding.tvSaving.setText(spellFmt.getSaving());
			mBinding.tvSaving.setSelected(true); //For marquee effect
			mBinding.tvBook.setText(spellFmt.getBook(true));
			mBinding.tvBook.setSelected(true); //For marquee effect

			Utils.initWebView(mBinding.clContainer.getContext(), mBinding.wvBody);

			mBinding.wvBody.loadDataWithBaseURL("", spellFmt.getDescription(), "text/html", "UTF-8", "");

			if (! TextUtils.isEmpty(mSpellProfile.getAuthor())) {
				mBinding.tvAuthor.setText(spellFmt.getAuthor(true));
				mBinding.tvAuthor.setVisibility(View.VISIBLE);
			} else {
				mBinding.tvAuthor.setVisibility(View.GONE);
			}

			if (BuildConfig.ADS_ON_DETAILS && mShowAds) {

				mBinding.flAds.setVisibility(View.VISIBLE);

				ViewGroup container = mBinding.flAds;

				MobileAdsWrapper.setAdView(container, MobileAdsWrapper.ADVIEW_TYPE_AUTO);
			} else {
				mBinding.flAds.setVisibility(View.GONE);
			}
		} else {
			mBinding.clContainer.setVisibility(View.GONE);
		}
	}

	public void setSpell(Spell spell) {
		Bundle args = new Bundle();
		args.putParcelable(ARG_SPELL, spell);
		args.putBoolean(ARG_SHOW_TITLE, mShowTitle);
		args.putBoolean(ARG_SHOW_ADS, mShowAds);
		setArguments(args);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Log.d(TAG, "onCheckedChanged: " + isChecked + "; pressed: " + buttonView.isPressed());
		if (buttonView.isPressed()) {
			//User interaction
			updateStar(isChecked);
		} else if (isChecked != mStarred){
			//Hack: Force right value in case of ghost onCheckedChanged
			showStar(mStarred);
		}
	}

	public void updateStar(boolean starred) {
		//Change DB value
		Log.d(TAG, "updateStar: " + starred);
		long profileId = mSpellProfile.getProfileId();
		long spellId = mSpellProfile.getId();
		SpellProfileDbAdapter adapter = SpellProfileDbAdapter.getInstance();
		adapter.setStar(profileId, spellId, starred);

		//Notify
		mListener.onSpellDetailStarred(profileId, spellId, starred);
	}

	public void showStar(boolean starred) {
		Log.d(TAG, "showStar: " + starred);
		mStarred = starred;
		if (mBinding.cbPreferred.isChecked() != starred) {

			//Do not unregister setOnCheckedChangeListener:
			//it is needed to trap ghost onCheckedChanged

			mBinding.cbPreferred.setChecked(starred);
		}
	}

	private void shareSpell() {

		if (mSpellProfile == null)
			return;

		SpellFormatter spellFmt = new SpellFormatter(getContext(), mSpellProfile);

		StringBuilder sb = new StringBuilder();
		sb.append("<h3>").append(spellFmt.getName()).append("</h3>");
		sb.append("<p>").append(spellFmt.getLevelLong());
		if (mSpellProfile.isReversible()) {
			//lbl_spell_reversible
			sb.append(" <i>").append(getString(R.string.lbl_spell_reversible)).append("</i>");
		}
		sb.append("</p>");
		sb.append("<p>").append(spellFmt.getSchool(true)).append("</p>");

		if (mSpellProfile.getType() == Spell.TYPE_CLERICS) {
			sb.append("<p><b>").append(getString(R.string.lbl_spell_sphere)).append("</b>: ")
					.append(spellFmt.getSphere()).append("</p>");
		}
		sb.append("<p><b>").append(getString(R.string.lbl_spell_range)).append("</b>: ")
				.append(spellFmt.getRange()).append("</p>");
		sb.append("<p><b>").append(getString(R.string.lbl_spell_compo)).append("</b>: ")
				.append(spellFmt.getCompos()).append("</p>");
		sb.append("<p><b>").append(getString(R.string.lbl_spell_duration)).append("</b>: ")
				.append(spellFmt.getDuration()).append("</p>");
		sb.append("<p><b>").append(getString(R.string.lbl_spell_cast_time)).append("</b>: ")
				.append(spellFmt.getCastingTime()).append("</p>");
		sb.append("<p><b>").append(getString(R.string.lbl_spell_area_effect)).append("</b>: ")
				.append(spellFmt.getArea()).append("</p>");
		sb.append("<p><b>").append(getString(R.string.lbl_spell_saving)).append("</b>: ")
				.append(spellFmt.getSaving()).append("</p>");
		sb.append("<p>").append(spellFmt.getBook(true)).append("</p>");

		sb.append(spellFmt.getDescription());

		if (!TextUtils.isEmpty(mSpellProfile.getAuthor())) {
			sb.append("<p>").append(spellFmt.getAuthor(true)).append("</p>");
		}

		String fullSpell = sb.toString();

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TITLE, spellFmt.getName());
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, spellFmt.getName());
		sendIntent.putExtra(Intent.EXTRA_TEXT, HtmlCompat.fromHtml(fullSpell, HtmlCompat.FROM_HTML_MODE_COMPACT));
		sendIntent.putExtra(Intent.EXTRA_HTML_TEXT, fullSpell);
		sendIntent.setType("text/html");

		Intent shareIntent = Intent.createChooser(sendIntent, null);
		startActivity(shareIntent);
	}

	public interface OnFragmentInteractionListener {
		void onSpellDetailStarred(long profileId, long spellId, boolean isStarred);
	}
}
