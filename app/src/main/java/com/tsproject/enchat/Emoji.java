package com.tsproject.enchat;

import android.app.Application;

import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

public class Emoji extends Application {
    public void onCreate()
    {
        super.onCreate();
        EmojiManager.install(new GoogleEmojiProvider());
    }
}
