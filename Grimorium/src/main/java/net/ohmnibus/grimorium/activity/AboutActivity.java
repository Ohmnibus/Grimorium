package net.ohmnibus.grimorium.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.danielstone.materialaboutlibrary.MaterialAboutActivity;

import net.ohmnibus.grimorium.R;

/**
 * Created by Ohmnibus on 20/06/2017.
 */

public abstract class AboutActivity extends MaterialAboutActivity {
//	@NonNull
//	@Override
//	protected MaterialAboutList getMaterialAboutList(@NonNull Context context) {
//
//		final AboutBoxUtils.BuildType buildType = AboutBoxUtils.BuildType.GOOGLE;
//		final String packageName = getApplicationContext().getPackageName();
//
//		MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();
//
//		//App name
//		appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
//				.text(R.string.app_name)
//				.desc(R.string.app_copy)
//				.icon(R.drawable.ic_launcher)
//				.build());
//
//		//Version
//		PackageInfo pInfo = null;
//		try {
//			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//			appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
//					.text(R.string.about_version)
//					.subText(pInfo.versionName)
//					.build()
//			);
//		} catch (PackageManager.NameNotFoundException e) {
//			e.printStackTrace();
//		}
//
//		//Changelog
//		appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
//				.text(R.string.about_changelog)
//				//.icon(android.R.drawable.ic_dialog_info)
//				.setOnClickAction(ConvenienceBuilder.createWebViewDialogOnClickAction(this, "Releases", "https://github.com/daniel-stoneuk/material-about-library/releases", true, false))
//				.build());
//
//
//		MaterialAboutCard.Builder reviewShareCardBuilder = new MaterialAboutCard.Builder();
//
//		//Review
//		reviewShareCardBuilder.addItem(new MaterialAboutActionItem.Builder()
//				.text(R.string.about_review)
//				.icon(android.R.drawable.ic_dialog_info)
//				.setOnClickAction(new MaterialAboutItemOnClickAction() {
//									  @Override
//									  public void onClick() {
//											AboutBoxUtils.openApp(AboutActivity.this, buildType, packageName);
//									  }
//								  }
//				)
//				.build()
//		);
//
//		//Share
//		reviewShareCardBuilder.addItem(new MaterialAboutActionItem.Builder()
//				.text(R.string.about_share)
//				.icon(android.R.drawable.ic_dialog_info)
//				.setOnClickAction(new MaterialAboutItemOnClickAction() {
//									  @Override
//									  public void onClick() {
//											AboutBoxUtils.share(
//													AboutActivity.this,
//													AboutActivity.this.getString(R.string.about_share),
//													AboutActivity.this.getString(R.string.about_share_message) //TODO: Fix this message
//											);
//									  }
//								  }
//				)
//				.build()
//		);
//
//
//		MaterialAboutCard.Builder contactCardBuilder = new MaterialAboutCard.Builder();
//		contactCardBuilder.addItem(new MaterialAboutActionItem.Builder()
//				.text(R.string.about_contact)
//				.icon(android.R.drawable.ic_dialog_info)
//				.build()
//		);
//
//		MaterialAboutCard.Builder authorCardBuilder = new MaterialAboutCard.Builder();
//		authorCardBuilder.title("Author");
//
//		authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
//				.text("")
//				.icon(android.R.drawable.ic_dialog_info)
//				.build()
//		);
//
//
//		//Name
//		//Version
//		//Changelog
//
//		//Review
//		//Share
//
//		//Contact
//
//		//-Ohmnibus Prod.-
//		//Other App
//		//Facebook
//		//Twitter
//		//Web
//
//		return new MaterialAboutList(appCardBuilder.build());
//	}
//
//	@Nullable
//	@Override
//	protected CharSequence getActivityTitle() {
//		return null;
//	}
}
