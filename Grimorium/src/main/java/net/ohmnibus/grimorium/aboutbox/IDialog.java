package net.ohmnibus.grimorium.aboutbox;

import androidx.appcompat.app.AppCompatActivity;

public interface IDialog {
    void open(AppCompatActivity activity, String url, String tag);
}
