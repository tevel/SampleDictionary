// TODO: write here your package!
package com.example.dictionary.sampledictionary;

import android.content.Context;
import android.content.Intent;

import com.example.dictionary.sampledictionary.LearnOnTheWay.TranslationResult;

/**
 * Receiver to handle intents from LearnOnTheWay app
 * TODO: add to AndroidManifest.xml lines in Application tag:
     <receiver android:name=".TranslationReceiver" >
         <intent-filter>
            <action android:name="info.tevel.TRANSLATION_REQUEST" />
         </intent-filter>
     </receiver>
 */
public class TranslationReceiver extends android.content.BroadcastReceiver {

    public void translateText(final TranslationResult translationResult) {

        if (translationResult.languageFrom == null) {
            // TODO: Detect language of provided text
            translationResult.languageFrom = "english";
        }

        if (translationResult.languageTo != null) {
            // TODO: Translate provided text from translationResult.languageFrom to translationResult.languageTo
            translationResult.resultTranslations = new TranslationResult.Translation[1];
            translationResult.resultTranslations[0] = new TranslationResult.Translation();
            translationResult.resultTranslations[0].translation = "not supported";
        }

        // send result to learn on the way app
        LearnOnTheWay.sendTranslationResult(this, translationResult);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LearnOnTheWay.handleReceiverIntent(context, this, intent);
    }

}
