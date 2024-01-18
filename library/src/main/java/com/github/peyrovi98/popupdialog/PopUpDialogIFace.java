package com.github.peyrovi98.popupdialog;

import android.graphics.Bitmap;

public interface PopUpDialogIFace {
    void prepareBackground(Bitmap bitmap);
    void prepareItemSelected(Bitmap bitmap, float x, float y);
    void preparePopupDialog(float x, float y);
}
