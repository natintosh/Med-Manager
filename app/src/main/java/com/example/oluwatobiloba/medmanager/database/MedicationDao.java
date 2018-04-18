package com.example.oluwatobiloba.medmanager.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.oluwatobiloba.medmanager.models.Medication;

import java.util.List;

@Dao
public interface MedicationDao {

    @Query("SELECT * FROM medication")
    LiveData<List<Medication>> getAll();

    @Query("SELECT * FROM medication")
    List<Medication> getAllMedication();

    @Query("SELECT * FROM medication WHERE medicationId IN (:medicationIds)")
    List<Medication> loadAllByIds(int[] medicationIds);

    @Query("SELECT * FROM medication WHERE medicationId = :medicationId LIMIT 1")
    Medication loadById(long medicationId);

    @Query("SELECT * FROM medication WHERE name LIKE :name LIMIT 1")
    Medication findByName(String name);

    @Query("SELECT * FROM medication ORDER BY start_date ASC")
    List<Medication> sortMedicationByStartDate();

    @Insert
    long insertMedication(Medication medication);

    @Insert
    void insertMultipleMedications(Medication... medications);

    @Delete
    void delete(Medication medication);
}
