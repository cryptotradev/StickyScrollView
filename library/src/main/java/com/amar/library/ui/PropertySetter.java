package com.amar.library.ui;

import androidx.core.view.ViewCompat;
import android.view.View;

/**
 * Created by Amar Jain on 28/03/17.
 */

public class PropertySetter {
    public static void setTranslationZ(View view, float translationZ) {
        ViewCompat.setTranslationZ(view, translationZ);
    }
}
