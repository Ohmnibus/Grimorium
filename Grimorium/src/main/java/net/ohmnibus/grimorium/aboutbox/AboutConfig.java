package net.ohmnibus.grimorium.aboutbox;

import com.danielstone.materialaboutlibrary.util.OpenSourceLicense;

import java.util.ArrayList;

public class AboutConfig {

    public enum BuildType {AMAZON, GOOGLE, FREE}

    //    general info
    public String appName;
    public int appIcon;
    public String version;
    public String aboutLabelTitle;
    public String logUiEventName;
    public String facebookUserName;
    public String twitterUserName;
    public String webHomePage;
    public String changelogHtmlPath;
    public String guideHtmlPath;
    public String appPublisher;
    public String companyHtmlPath;
    public String privacyHtmlPath;
    public String acknowledgmentHtmlPath;
    public BuildType buildType;
    public String packageName;

    //    custom analytics, dialog and share
    public IAnalytic analytics;
    public IDialog dialog;
    public IShare share;

    //    email
    public String emailAddress;
    public String emailSubject;
    public String emailBody;

    //    share
    public String shareMessage;
    public String sharingTitle;

    // License
    public ArrayList<License> licenses = new ArrayList<>();

    public void addLicense(String library, String year, String author, OpenSourceLicense type) {
        License license = new License();
        license.mLibrary = library;
        license.mYear = year;
        license.mAuthor = author;
        license.mType = type;
        licenses.add(license);
    }

    private static class SingletonHolder {
        public static final AboutConfig HOLDER_INSTANCE = new AboutConfig();
    }

    public static AboutConfig getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    public static class License {
        public String mLibrary;
        public String mYear;
        public String mAuthor;
        public OpenSourceLicense mType;
    }

    private AboutConfig() { }
}
