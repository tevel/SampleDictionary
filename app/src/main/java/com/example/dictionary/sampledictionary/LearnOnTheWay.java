package com.example.dictionary.sampledictionary;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;

public class LearnOnTheWay {

    public static final String PACKAGE = "info.tevel.phrases";

    public static boolean isInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void openInPlayStore(Context context) {
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PACKAGE));
        context.startActivity(it);
    }

    public static void addNewPhrase(Context context, String text1, String language1, String text2, String language2) {
        if (!isInstalled(context)) {
            openInPlayStore(context);
            return;
        }

        Intent it = new Intent(Intent.ACTION_MAIN);
        it.setComponent(new ComponentName(PACKAGE, PACKAGE + ".AddPhraseActivity"));
        it.putExtra("text1", text1);
        it.putExtra("language1", language1);
        it.putExtra("text2", text2);
        it.putExtra("language2", language2);
        context.startActivity(it);
    }

    public static void handleServiceIntent(LearnOnTheWayService service, Intent intent) {
        if (intent == null || service == null)
            return;

        final String ACTION_TRANSLATE = service.getPackageName()+".TRANSLATE";
        final String ACTION_DETECT_LANGUAGE = service.getPackageName()+".DETECT_LANGUAGE";

        final String action = intent.getAction();

        if (ACTION_TRANSLATE.equals(action)) {
            final String text = intent.getStringExtra("text");
            final String languageFrom = intent.getStringExtra("language_from");
            final String languageTo = intent.getStringExtra("language_to");
            final ResultReceiver receiver = intent.getParcelableExtra("receiver");
            if (receiver == null || text == null || languageFrom == null || languageTo == null)
                return;
            String translated_text = "";
            try {
                service.translateText(text, languageFrom, languageTo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bundle bundle = new Bundle();
            bundle.putString("command", "translate");
            bundle.putString("text", text);
            bundle.putString("language_from", languageFrom);
            bundle.putString("translated_to", languageTo);
            bundle.putString("translated_text", translated_text);
            receiver.send(0, bundle);
            return;
        }

        if (ACTION_DETECT_LANGUAGE.equals(action)) {
            final String text = intent.getStringExtra("text");
            final String language1 = intent.getStringExtra("language1");
            final String language2 = intent.getStringExtra("language2");
            final ResultReceiver receiver = intent.getParcelableExtra("receiver");
            if (receiver == null || text == null || language1 == null || language2 == null)
                return;
            String detectedLanguage = "";
            try {
                detectedLanguage = service.detectTextLanguage(text, language1, language2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bundle bundle = new Bundle();
            bundle.putString("command", "detect_language");
            bundle.putString("text", text);
            bundle.putString("language1", language1);
            bundle.putString("language2", language2);
            bundle.putString("detected_language", detectedLanguage);
            receiver.send(0, bundle);
            return;
        }
    }
}
