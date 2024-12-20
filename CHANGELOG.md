# Change log

## [0.9.3.0] (28)
- Removed ADs and In-App purchase
- Removed support for legacy profiles
- Dropped support for Android 4.3 and lower

## 0.9.2.1 (27)
- Fix import bug (thanks to Todd)

## 0.9.2.0 (26)
- Add share spell detail
- Add share starred spell list
- Moved ADs to spell detail screen
- Fix in-app purchase
- Fix "ghost text" when rotating spell detail
- Fix "Ok, nevermind" text after successfull spell library import

## 0.9.1.12 (25)
- Disabled ADs while inspecting on In-App purchase issues

## 0.9.1.11 (24)
- Fix compatibility issues with Android 13

## 0.9.1.10 (23)
- Fix compatibility issues with "Oreo" (hopefully)

## 0.9.1.9 (22)
- Fix compatibility issues with "Oreo"

## 0.9.1.8 (21)
- Add privacy policy in about dialog
- Upgrade build target to 28

## 0.9.1.7 (20)
- Fix SpellDetailFragment.setSpell (not reproduced)
- Add missing School "Mentalism"
- Show spheres in place of schools for prayers (main list)

## 0.9.1.6 (19)
- Add missing Schools from Spells & Magic
- Fix IabHelper
- Fix SourceListActivity.hideProgressDialog (not reproduced)
- Fix SpellFormatter.formatBody
- Fix SpellListActivity.onActivityResult (not reproduced)

## 0.9.1.5 (18)
- Fix ads size
- Fix screen rotation with open MultiSelectList pref.
- Migrate to official MultiSelectList pref.
- Migrate Crash Reporting to Crashlytics
- Fix duplicate spells after ads

## 0.9.1.4 (17)
- Fix ads

## 0.9.1.3 (16)
- Fix missing reference books in spell filter
- Fix selection of newly added magic user
- Fix select all spell levels
- Migrate Native Ads (discontinued) to Banner Ads
- Clean up some code, remove unused resources
- Remove reference to "FirebaseCrash" class in "free" flavour
- Fix NRE on DBManager instance
- Fix "FragmentManagerImpl.checkStateLoss" (maybe)
- Fix "No view found for id 0x7f09004d (net.ohmnibus.grimorium.adnd:id/fragment_container)"

## 0.9.1.2 (15)
- Add Firebase crash report
- Fix IllegalStateException in SpellListActivity.java
- Fix IllegalStateException in SourceListActivity.java
- Fix refresh of sources list in spell filter

## 0.9.1 (14)
- Fix share function on aboutbox
- Fix minor flaws in iab
- Add AboutBox
- Fix first tap on empty spell list
- Add loaders in adapters