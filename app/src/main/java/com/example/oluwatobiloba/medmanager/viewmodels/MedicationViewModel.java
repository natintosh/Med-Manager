package com.example.oluwatobiloba.medmanager.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.oluwatobiloba.medmanager.database.AppDatabase;
import com.example.oluwatobiloba.medmanager.models.Medication;

import java.util.List;

public class MedicationViewModel extends AndroidViewModel {

    private final LiveData<List<Medication>> medicationList;

    private AppDatabase database;

    public MedicationViewModel(@NonNull Application application) {
        super(application);

        database = AppDatabase.getAppDatabase(this.getApplication());

        medicationList = database.medicationDao().getAll();
    }

    public LiveData<List<Medication>> getMedicationList() {
        return medicationList;
    }

    public void deleteItem(Medication medication) {
        new deleteAsyncTask(database).execute(medication);
    }

    private static class deleteAsyncTask extends AsyncTask<Medication, Void, Void> {

        private AppDatabase database;

        deleteAsyncTask(AppDatabase database) {
            this.database = database;
        }

        @Override
        protected Void doInBackground(Medication... medications) {
            database.medicationDao().delete(medications[0]);
            return null;
        }
    }
}
