package com.github.peyrovi98.popupdialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PopUpModel {
    private final ExecutorService service;

    public PopUpModel() {
        this.service = Executors.newSingleThreadExecutor();
    }

    public LiveData<Void> timeDelay(long timestamp) {
        MutableLiveData<Void> liveData = new MutableLiveData<>();
        service.execute(() -> {
            try {
                Thread.sleep(timestamp);
                liveData.postValue(null);
            } catch (InterruptedException e) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }
}
