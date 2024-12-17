package net.ohmnibus.grimorium.helper.iab;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;

/**
 * Copyright (C) 2023 by Ohmnibus.
 */

public class BillingClientHelper {

   public interface Products {
      boolean isDisabledAds();
   }

   public interface PurchaseStatusListener {
      /** Not an error */
      int ERROR_NONE = 0;
      /** User canceled or item consumed */
      int ERROR_CANCELED = 1;
      /** Item alreasy owned */
      int ERROR_ITEM_OWNED = 2;
      /** Network error */
      int ERROR_NETWORK = 3;
      /** Another async operation in progress */
      int ERROR_BUSY = 4;
      /** API not supported or item unavailable */
      int ERROR_NOT_SUPPORTED = 5;
      /** Fatal error without purchase. */
      int ERROR_FATAL = 6;
      /** Fatal error */
      int ERROR_REFUND = 7;
      /** Error during setup */
      int ERROR_SETUP = 8;
      /** Billing Unavailable */
      int ERROR_NOT_AVAILABLE = 9;

      void onPurchasesStatus(int status, Products products);
   }

   private static final Products mProducts = () -> true;

   public static void initialize(@NonNull Application application) {
      //NOOP
   }

   public static void getPurchasesAsync(@NonNull PurchaseStatusListener listener) {
      listener.onPurchasesStatus(PurchaseStatusListener.ERROR_NONE, mProducts);
   }

   public static void launchDisableAdsPurchaseFlow(@NonNull Activity activity, @NonNull PurchaseStatusListener listener) {
      listener.onPurchasesStatus(PurchaseStatusListener.ERROR_NONE, mProducts);
   }
}
