package com.example.dictionary.sampledictionary;

import android.app.IntentService;
import android.content.Intent;

/**
 * Service to receive and handle intents from LearnOnTheWay app
 * TODO: move this file to your package!
 * TODO: add to AndroidManifest.xml line: <service android:name=".LearnOnTheWayService" android:exported="false" />
 */
public class LearnOnTheWayService extends IntentService {

    @Override
    public void onCreate() {
        // TODO: Connect to your dictionary database
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO: Disconnect from your dictionary database
        super.onDestroy();
    }

    public String translateText(String text, String languageFrom, String languageTo) {
        // TODO: Translate provided text
        return "not implemented";
    }

    public String detectTextLanguage(String text, String language1, String language2) {
        // TODO: Detect language of text: language1 or language2?
        return language1;
    }

    public LearnOnTheWayService() {
        super("DictionaryIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LearnOnTheWay.handleServiceIntent(this, intent);
    }

}
