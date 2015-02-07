package com.example.dictionary.sampledictionary;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;

public class LearnOnTheWay {

    public static final String TRANSLATE_BROADCAST_INTENT = "info.tevel.TRANSLATION_REQUEST";
    public static final String PACKAGE = "info.tevel.phrases";

    public static class TranslationResult {
        public static class Translation {
            public String translation;
            public String reverseTranslation;
        }

        public String textToTranslate;
        public String languageFrom; // may be null, this means auto detection
        public String languageTo; // may be null, this means no translation required, only language detection

        public Translation[] resultTranslations;

        public ResultReceiver resultReceiver;
    }

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

    public static void sendTranslationResult(final TranslationReceiver receiver, TranslationResult translationResult) {
        Bundle bundle = new Bundle();
        bundle.putString("command", "translate");
        bundle.putString("text", translationResult.textToTranslate);
        bundle.putString("language_from", translationResult.languageFrom);
        bundle.putString("language_to", translationResult.languageTo);
        if (translationResult.resultTranslations != null) {
            String[] translations = new String[translationResult.resultTranslations.length];
            String[] reverseTranslations = new String[translationResult.resultTranslations.length];
            for (int i = 0; i < translationResult.resultTranslations.length; i++) {
                translations[i] = translationResult.resultTranslations[i].translation;
                reverseTranslations[i] = translationResult.resultTranslations[i].reverseTranslation;
            }
            bundle.putStringArray("translations", translations);
            bundle.putStringArray("reverse_translations", reverseTranslations);
        }
        bundle.putString("package", receiver.getClass().getPackage().getName());
        translationResult.resultReceiver.send(0, bundle);
    }

    public static void handleReceiverIntent(final Context context, final TranslationReceiver receiver, final Intent intent) {
        if (intent == null || receiver == null)
            return;

        final String action = intent.getAction();

        if (TRANSLATE_BROADCAST_INTENT.equals(action)) {
            final TranslationResult translationResult = new TranslationResult();

            try {
                Context context2 = context.createPackageContext(PACKAGE, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
                ClassLoader cl = context2.getClassLoader();
                Bundle bundle = intent.getExtras();
                bundle.setClassLoader(cl);

                translationResult.textToTranslate = bundle.getString("text");
                translationResult.languageFrom = bundle.getString("language_from");
                translationResult.languageTo = bundle.getString("language_to");
                translationResult.resultReceiver = bundle.getParcelable("receiver");
                translationResult.resultTranslations = null;

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return;
            }

            if (translationResult.resultReceiver == null
                    || translationResult.textToTranslate == null)
                return;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    receiver.translateText(translationResult);
                }
            }).start();

            return;
        }

    }
}
