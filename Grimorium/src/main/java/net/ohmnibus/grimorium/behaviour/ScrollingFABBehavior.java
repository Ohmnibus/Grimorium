package net.ohmnibus.grimorium.behaviour;

import android.content.Context;
import com.google.android.material.appbar.AppBarLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import net.ohmnibus.grimorium.helper.Utils;

public class ScrollingFABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
	private final int toolbarHeight;

	public ScrollingFABBehavior(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.toolbarHeight = Utils.getToolbarHeight(context);
	}

	@Override
	public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull FloatingActionButton fab, @NonNull View dependency) {
		return dependency instanceof AppBarLayout;
	}

	@Override
	public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull FloatingActionButton fab, @NonNull View dependency) {
		if (dependency instanceof AppBarLayout) {
			CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
			int fabBottomMargin = lp.bottomMargin;
			int distanceToScroll = fab.getHeight() + fabBottomMargin;
			float ratio = dependency.getY()/(float)toolbarHeight;
			fab.setTranslationY(-distanceToScroll * ratio);
		}
		return true;
	}
}
