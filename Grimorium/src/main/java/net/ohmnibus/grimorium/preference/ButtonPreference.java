package net.ohmnibus.grimorium.preference;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.Preference;

/**
 * Copyright (C) 2023 by Ohmnibus.
 */

public class ButtonPreference extends Preference {

   public ButtonPreference(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onClick() {
      super.onClick();

      callChangeListener(true);
   }

}
