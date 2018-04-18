package com.example.oluwatobiloba.medmanager.adapters;

import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.oluwatobiloba.medmanager.R;
import com.example.oluwatobiloba.medmanager.models.Medication;

import java.util.ArrayList;
import java.util.List;

public class MedicationListAdapter extends RecyclerView.Adapter<MedicationListAdapter.ViewHolder> {

    private List<Medication> mMedicationList;
    private List<Medication> mMedCopy;

    public MedicationListAdapter(List<Medication> medicationList) {
        this.mMedicationList = medicationList;
        mMedCopy = new ArrayList<>();
        mMedCopy.addAll(mMedicationList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medication_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medication medication = mMedicationList.get(position);
        String name = medication.getName();
        String description = medication.getDescription();
        String iconText = String.valueOf(name.charAt(0)).toUpperCase();
        int color = Integer.parseInt(medication.getColor());
        holder.mMedicationName.setText(name);
        holder.mMedicationDescription.setText(description);

        ((GradientDrawable) holder.mIconTextview.getBackground()).setColor(color);
        holder.mIconTextview.setText(iconText);

        long id = medication.getId();
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return mMedicationList.size();
    }

    public void addMedications(List<Medication> medications) {
        mMedicationList = medications;
        notifyDataSetChanged();
    }

    public void filter(String newText) {

        mMedicationList.clear();
        String text = newText.toLowerCase();

        if (text.isEmpty()) {
            mMedicationList.addAll(mMedCopy);
        } else {
            for (Medication med : mMedCopy) {
                String medName = med.getName().toLowerCase();
                if (medName.contains(text) || medName.equals(text)) {
                    mMedicationList.add(med);
                }
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mMedicationName, mMedicationDescription, mIconTextview;

        ViewHolder(View itemView) {
            super(itemView);
            mMedicationName = itemView.findViewById(R.id.medication_list_name);
            mMedicationDescription = itemView.findViewById(R.id.medication_list_description);
            mIconTextview = itemView.findViewById(R.id.medication_details_icon);
        }
    }
}
