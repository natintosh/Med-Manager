package com.example.oluwatobiloba.medmanager.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.oluwatobiloba.medmanager.R;
import com.example.oluwatobiloba.medmanager.adapters.MedicationListAdapter;
import com.example.oluwatobiloba.medmanager.database.AppDatabase;
import com.example.oluwatobiloba.medmanager.models.Medication;
import com.example.oluwatobiloba.medmanager.models.User;
import com.example.oluwatobiloba.medmanager.utils.ConnectionUtils;
import com.example.oluwatobiloba.medmanager.utils.Months;
import com.example.oluwatobiloba.medmanager.utils.RecyclerTouchListener;
import com.example.oluwatobiloba.medmanager.viewmodels.MedicationViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DashBoard extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Void>, SearchView.OnQueryTextListener, AdapterView.OnItemSelectedListener {

    private static final int RC_INSERT_USER = 1003;

    private static final String TAG = DashBoard.class.getSimpleName();
    AppDatabase database;
    FirebaseUser mFirebaseUser;
    FirebaseFirestore mFirestore;


    MedicationListAdapter mMedicationListAdapter;
    List<Medication> mMedicationList;
    List<Medication> mMedCopy;
    MedicationViewModel mMedicationViewModel;
    List<String> monthsList;

    RecyclerView mRecyclerView;
    ConstraintLayout mConstraintLayout;
    FloatingActionButton mAddNewFab;
    Spinner mMonthSpinner;

    private LoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        database = AppDatabase.getAppDatabase(this);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();

        initializingVariables();

        monthsList = new ArrayList<>();
        for (Months month : Months.values()) {
            monthsList.add(month.getMonthName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, monthsList);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMonthSpinner.setAdapter(dataAdapter);

        if (!isUserSignIn()) {
            launchIntent(new Intent(DashBoard.this, SplashScreen.class));
        }

        User user = database.userDao().findById(mFirebaseUser.getUid());

        if (user == null) {
            if (ConnectionUtils.isConnected(this)) {
                loaderManager = getSupportLoaderManager();
                Loader<String> loader = loaderManager.getLoader(RC_INSERT_USER);
                if (loader == null) {
                    loaderManager.initLoader(RC_INSERT_USER, null, DashBoard.this);
                } else {
                    loaderManager.restartLoader(RC_INSERT_USER, null, DashBoard.this);
                }
            } else {
                loadRecyclerView();
            }
        } else {
            loadRecyclerView();
        }

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            // Do nothing for move since am not implementing anything that has to do with drags
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                long id = (long) viewHolder.itemView.getTag();
                Medication medication = database.medicationDao().loadById(id);

                database.medicationDao().delete(medication);
                mMedicationListAdapter.notifyDataSetChanged();

                Snackbar snackbar = Snackbar.make(mConstraintLayout,
                        medication.getName() + " removed", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    private void loadRecyclerView() {
        mRecyclerView = findViewById(R.id.medication_list_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);

        mMedicationViewModel = ViewModelProviders.of(this).get(MedicationViewModel.class);

        mMedicationViewModel.getMedicationList().observe(this, new Observer<List<Medication>>() {
            @Override
            public void onChanged(@Nullable List<Medication> medications) {
                mMedicationList = medications;
                mMedCopy = medications;
                mMedicationListAdapter.addMedications(medications);
            }
        });

        mMedicationList = database.medicationDao().getAllMedication();
        mMedCopy = mMedicationList;
        mMedicationListAdapter = new MedicationListAdapter(mMedicationList);

        mRecyclerView.setAdapter(mMedicationListAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Medication medication = mMedicationList.get(position);
                Intent intent = new Intent(DashBoard.this, MedicationDetail.class);
                intent.putExtra(MedicationDetail.EXTRA_MEDICATION_ID, String.valueOf(medication.getId()));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void initializingVariables() {
        mAddNewFab = findViewById(R.id.add_fab);
        mConstraintLayout = findViewById(R.id.dashboard_constraintlayout);
        mMonthSpinner = findViewById(R.id.month_spinner);

        mMonthSpinner.setOnItemSelectedListener(this);

        mAddNewFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoard.this, AddMedication.class));
            }
        });
    }

    private boolean isUserSignIn() {
        return mFirebaseUser != null;
    }

    private void launchIntent(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @NonNull
    @Override
    public Loader<Void> onCreateLoader(final int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Void>(this) {
            @Nullable
            @Override
            public Void loadInBackground() {
                if (id == RC_INSERT_USER) {
                    mFirestore.collection("Users").document(mFirebaseUser.getUid())
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            database.userDao().insertUser(user);
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });
                }
                return null;
            }

            @Override
            protected void onStartLoading() {
                forceLoad();
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Void> loader, Void data) {
        int id = loader.getId();
        if (id == RC_INSERT_USER) {
            Toast.makeText(DashBoard.this, "Profile updated ", Toast.LENGTH_SHORT)
                    .show();
            loadRecyclerView();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Void> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_options_menu, menu);


        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_signout) {
            NavUtils.navigateUpFromSameTask(this);
            FirebaseAuth.getInstance().signOut();
            finish();
        } else if (id == R.id.action_account) {
            Intent intent = new Intent(DashBoard.this, UserProfile.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        mMedicationListAdapter.filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        mMedicationListAdapter.filter(newText);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mMedicationList.clear();
        if (position == 0) {
            mMedicationList = database.medicationDao().getAllMedication();
            mMedicationListAdapter.addMedications(mMedicationList);
            mMedicationListAdapter.notifyDataSetChanged();
        } else {
            mMedCopy = database.medicationDao().getAllMedication();
            for (Medication med : mMedCopy) {
                long startDate = med.getStartDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(startDate);
                int monthId = calendar.get(Calendar.MONTH) + 1;
                if (monthId == position) {
                    mMedicationList.add(med);
                }
            }
            mMedicationListAdapter.addMedications(mMedicationList);
            mMedicationListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mMedicationList = database.medicationDao().getAllMedication();
        mMedicationListAdapter.addMedications(mMedicationList);
        mMedicationListAdapter.notifyDataSetChanged();
    }
}
