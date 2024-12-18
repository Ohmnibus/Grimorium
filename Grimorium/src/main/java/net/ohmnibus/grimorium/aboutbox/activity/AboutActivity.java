package net.ohmnibus.grimorium.aboutbox.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import android.text.TextUtils;

import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;

import net.ohmnibus.grimorium.aboutbox.AboutBoxUtils;
import net.ohmnibus.grimorium.aboutbox.AboutConfig;
import net.ohmnibus.grimorium.aboutbox.IAnalytic;
import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.aboutbox.share.EmailUtil;
import net.ohmnibus.grimorium.aboutbox.share.ShareUtil;

public class AboutActivity extends MaterialAboutActivity {

	private static final String BUNDLE_ABOUT_LICENSES = "BUNDLE_ABOUT_LICENSES";

	static {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
	}

	public static void launch(Activity activity) {
		Intent intent = new Intent(activity, AboutActivity.class);
		activity.startActivity(intent);
	}

	public static void launchLicenses(Activity activity) {
		Intent intent = new Intent(activity, AboutActivity.class);
		intent.putExtra(BUNDLE_ABOUT_LICENSES, true);
		activity.startActivity(intent);
	}

	@NonNull
	@Override
	protected MaterialAboutList getMaterialAboutList(@NonNull Context context) {

		final AboutConfig config = AboutConfig.getInstance();

		if (getIntent() != null && getIntent().getBooleanExtra(BUNDLE_ABOUT_LICENSES, false)) {
			return buildLicensesAboutList(config);
		}

		MaterialAboutList.Builder builder = new MaterialAboutList.Builder()
				.addCard(buildGeneralInfoCard(config))
				.addCard(buildSupportCard(config))
				.addCard(buildShareCard(config))
				.addCard(buildAboutCard(config))
				.addCard(buildSocialNetworksCard(config))
				.addCard(buildPrivacyCard(config));

		return builder.build();
	}

	@NonNull
	private MaterialAboutCard buildGeneralInfoCard(AboutConfig config) {
		MaterialAboutCard.Builder generalInfoCardBuilder = new MaterialAboutCard.Builder();

		generalInfoCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
				.text(config.appName)
				.icon(config.appIcon)
				.build());

		if (config.version != null && config.version.length() > 0) {
			generalInfoCardBuilder.addItem(new MaterialAboutActionItem.Builder()
					.text(R.string.egab_version)
					.subText(config.version)
					.build());
		}

		generalInfoCardBuilder.addItem(new MaterialAboutActionItem.Builder()
				.text(R.string.egab_changelog)
				//.icon(R.drawable.ic_help_green)
				.setOnClickAction(() -> {
					if (config.dialog == null) {
						AboutBoxUtils.openHTMLPage(AboutActivity.this, config.changelogHtmlPath);
					} else {
						config.dialog.open(AboutActivity.this, config.changelogHtmlPath, getString(R.string.egab_changelog));
					}
				})
				.build());

		return generalInfoCardBuilder.build();
	}

	@NonNull
	private MaterialAboutCard buildSupportCard(final AboutConfig config) {
		MaterialAboutCard.Builder card = new MaterialAboutCard.Builder();

		if (!TextUtils.isEmpty(config.guideHtmlPath)) {
			card.addItem(itemHelper(R.string.egab_guide, R.drawable.ic_help_green,
					() -> {
						if (config.dialog == null) {
							AboutBoxUtils.openHTMLPage(AboutActivity.this, config.guideHtmlPath);
						} else {
							config.dialog.open(AboutActivity.this, config.guideHtmlPath, getString(R.string.egab_guide));
						}
						logUIEventName(config.analytics, config.logUiEventName, getString(R.string.egab_guide));
					})
			);
		}
		card.addItem(itemHelper(R.string.egab_contact_support, R.drawable.ic_email_black,
				() ->  {
					EmailUtil.contactUs(AboutActivity.this);
					logUIEventName(config.analytics, config.logUiEventName, getString(R.string.egab_contact_log_event));
				}));

		return card.build();
	}

	@NonNull
	private MaterialAboutCard buildShareCard(final AboutConfig config) {
		MaterialAboutCard.Builder card = new MaterialAboutCard.Builder();
		if (config.buildType != AboutConfig.BuildType.FREE && !TextUtils.isEmpty(config.packageName)) {
			card.addItem(itemHelper(R.string.egab_leave_review, R.drawable.ic_review,
					() -> {
						AboutBoxUtils.openApp(AboutActivity.this, config.buildType, config.packageName);
						logUIEventName(config.analytics, config.logUiEventName, getString(R.string.egab_review_log_event));
					}));
		}
		card.addItem(itemHelper(R.string.egab_share, R.drawable.ic_share_black,
				() -> {
					if (config.share == null) {
						ShareUtil.share(AboutActivity.this);
					} else {
						config.share.share(AboutActivity.this);
					}
					logUIEventName(config.analytics, config.logUiEventName, getString(R.string.egab_share_log_event));
				}));
		return card.build();
	}

	@NonNull
	private MaterialAboutCard buildAboutCard(final AboutConfig config) {
		MaterialAboutCard.Builder card = new MaterialAboutCard.Builder();
		if (config.buildType != AboutConfig.BuildType.FREE && !TextUtils.isEmpty(config.appPublisher) && !TextUtils.isEmpty(config.packageName)) {
			card.addItem(itemHelper(R.string.egab_try_other_apps, R.drawable.ic_try_other_apps,
					() -> {
						AboutBoxUtils.openPublisher(AboutActivity.this, config.buildType,
								config.appPublisher, config.packageName);
						logUIEventName(config.analytics, config.logUiEventName, getString(R.string.egab_try_other_app_log_event));
					}));
		}
		if (!TextUtils.isEmpty(config.companyHtmlPath)) {
			card.addItem(new MaterialAboutActionItem.Builder()
					.text(config.aboutLabelTitle)
					.icon(R.drawable.ic_about_black)
					.setOnClickAction(() -> {
						if (config.dialog == null) {
							AboutBoxUtils.openHTMLPage(AboutActivity.this, config.companyHtmlPath);
						} else {
							config.dialog.open(AboutActivity.this, config.companyHtmlPath, config.aboutLabelTitle);
						}
						logUIEventName(config.analytics, config.logUiEventName, config.aboutLabelTitle);
					})
					.build());
		}
		return card.build();
	}

	@NonNull
	private MaterialAboutCard buildSocialNetworksCard(final AboutConfig config) {
		MaterialAboutCard.Builder card = new MaterialAboutCard.Builder();
		if (!TextUtils.isEmpty(config.facebookUserName)) {
			card.addItem(new MaterialAboutActionItem.Builder()
					.text(R.string.egab_facebook_label)
					.subText(config.facebookUserName)
					.icon(R.drawable.ic_facebook_24)
					.setOnClickAction(() -> {
						AboutBoxUtils.getOpenFacebookIntent(AboutActivity.this, config.facebookUserName);
						logUIEventName(config.analytics, config.logUiEventName, getString(R.string.egab_facebook_log_event));
					})
					.build());
		}
		if (!TextUtils.isEmpty(config.twitterUserName)) {
			card.addItem(new MaterialAboutActionItem.Builder()
					.text(R.string.egab_twitter_label)
					.subText(config.twitterUserName)
					.icon(R.drawable.ic_twitter_24dp)
					.setOnClickAction(() -> {
						AboutBoxUtils.startTwitter(AboutActivity.this, config.twitterUserName);
						logUIEventName(config.analytics, config.logUiEventName, getString(R.string.egab_twitter_log_event));
					})
					.build());
		}
		if (!TextUtils.isEmpty(config.webHomePage)) {
			Uri uri = Uri.parse(config.webHomePage);
			card.addItem(new MaterialAboutActionItem.Builder()
					.text(R.string.egab_web_label)
					.subText(uri.getHost())
					.icon(R.drawable.ic_web_black_24dp)
					.setOnClickAction(() -> {
						AboutBoxUtils.openHTMLPage(AboutActivity.this, config.webHomePage);
						logUIEventName(config.analytics, config.logUiEventName, getString(R.string.egab_website_log_event));
					})
					.build());
		}
		return card.build();
	}

	@NonNull
	private MaterialAboutCard buildPrivacyCard(final AboutConfig config) {
		MaterialAboutCard.Builder card = new MaterialAboutCard.Builder();
		if (!TextUtils.isEmpty(config.privacyHtmlPath)) {
			card.addItem(itemHelper(R.string.egab_privacy_policy, R.drawable.ic_privacy,
					() -> {
						if (config.dialog == null) {
							AboutBoxUtils.openHTMLPage(AboutActivity.this, config.privacyHtmlPath);
						} else {
							config.dialog.open(AboutActivity.this, config.privacyHtmlPath, getString(R.string.egab_privacy_policy));
						}

						logUIEventName(config.analytics, config.logUiEventName, getString(R.string.egab_privacy_log_event));
					})
			);
		}
		if (!TextUtils.isEmpty(config.acknowledgmentHtmlPath)) {
			card.addItem(itemHelper(R.string.egab_acknowledgements, R.drawable.ic_acknowledgements,
					() -> {
						if (config.dialog == null) {
							AboutBoxUtils.openHTMLPage(AboutActivity.this, config.acknowledgmentHtmlPath);
						} else {
							config.dialog.open(AboutActivity.this, config.acknowledgmentHtmlPath, getString(R.string.egab_acknowledgements));
						}
						logUIEventName(config.analytics, config.logUiEventName, getString(R.string.egab_acknowledgements_log_event));
					})
			);
		}
		if (config.licenses.size() > 0) {
			card.addItem(new MaterialAboutActionItem.Builder()
					.text(R.string.egab_licenses)
					.icon(R.drawable.ic_book_24)
					.setOnClickAction(() -> launchLicenses(AboutActivity.this))
					.build());
		}
		return card.build();
	}

	@NonNull
	private MaterialAboutList buildLicensesAboutList(final AboutConfig config) {
		MaterialAboutList.Builder builder = new MaterialAboutList.Builder();

		for (AboutConfig.License license : config.licenses) {
			MaterialAboutActionItem licenseItem = new MaterialAboutActionItem.Builder()
					.icon(R.drawable.ic_book_24)
					.setIconGravity(MaterialAboutActionItem.GRAVITY_TOP)
					.text(license.mLibrary)
					.subText(getString(license.mType.getResourceId(), license.mYear, license.mAuthor))
					.build();

			MaterialAboutCard card = new MaterialAboutCard.Builder().addItem(licenseItem).build();

			builder.addCard(card);
		}

		return builder.build();
	}

	private MaterialAboutActionItem itemHelper(int name, int icon, MaterialAboutItemOnClickAction listener) {
		return new MaterialAboutActionItem.Builder()
				.text(name)
				.icon(icon)
				.setOnClickAction(listener)
				.build();
	}


	@Override
	protected CharSequence getActivityTitle() {
		return getString(R.string.egab_about_screen_title);
	}

	private void logUIEventName(IAnalytic analytics, String eventType, String eventValue) {
		if (analytics != null) {
			analytics.logUiEvent(eventType, eventValue);
		}
	}
}
