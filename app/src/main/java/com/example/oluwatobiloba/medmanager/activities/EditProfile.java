package com.example.oluwatobiloba.medmanager.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oluwatobiloba.medmanager.R;
import com.example.oluwatobiloba.medmanager.database.AppDatabase;
import com.example.oluwatobiloba.medmanager.models.User;
import com.example.oluwatobiloba.medmanager.ui.DatePickerFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import dmax.dialog.SpotsDialog;
import lib.kingja.switchbutton.SwitchMultiButton;

public class EditProfile extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Void>, DatePickerDialog.OnDateSetListener {

    private static final int RC_PHOTO_PICKER_INTENT = 1001;
    private static final String TAG = EditProfile.class.getSimpleName();
    private static final int RC_LOADER_EDIT = 1002;
    private String mName, mEmail, mGender, mPhoneNumber;
    int mAge;
    long mDateOfBirth;

    ImageView mEditProfileImage;
    EditText mNameEditText, mPhoneNumberEditText, mAgeEditText, mDateOfBirthEditText;
    TextView mEmailTexView;
    SwitchMultiButton mGenderButton;
    Calendar mCalendar;
    FloatingActionButton continueFab;

    FirebaseUser mFirebaseUser;
    FirebaseFirestore mFirebaseFirestore;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageRef;
    private SpotsDialog mProgressDialog;
    private Uri mLocalImageUri;

    LoaderManager loaderManager;

    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        database = AppDatabase.getAppDatabase(this);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mStorageRef = mFirebaseStorage.getReference();

        initialiseVariables();

        mGender = "Male";
        mGenderButton.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {
                mGender = tabText;
            }
        });

        mEditProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RC_PHOTO_PICKER_INTENT);
            }
        });

        continueFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isErrorInInput()) {
                    loaderManager = getSupportLoaderManager();
                    Loader<String> loader = loaderManager.getLoader(RC_LOADER_EDIT);
                    if (loader == null) {
                        loaderManager.initLoader(RC_LOADER_EDIT, null, EditProfile.this);
                    } else {
                        loaderManager.restartLoader(RC_LOADER_EDIT, null, EditProfile.this);
                    }
                }
            }
        });

    }

    private void initialiseVariables() {
        mEditProfileImage = findViewById(R.id.edit_profile_image);
        mNameEditText = findViewById(R.id.edit_name);
        mEmailTexView = findViewById(R.id.edit_email);
        mGenderButton = findViewById(R.id.gender_multiswitch_button);
        mPhoneNumberEditText = findViewById(R.id.edit_phone);
        mAgeEditText = findViewById(R.id.edit_age);
        mDateOfBirthEditText = findViewById(R.id.edit_dob);
        continueFab = findViewById(R.id.continue_fab);

        mProgressDialog = new SpotsDialog(EditProfile.this);

        mName = mFirebaseUser.getDisplayName();
        mEmail = mFirebaseUser.getEmail();
        mNameEditText.setText(mName);
        if (mEmail != null) {
            mEmailTexView.setText(mFirebaseUser.getEmail());
        }


        mDateOfBirthEditText.setFocusable(false);
        mDateOfBirthEditText.setInputType(InputType.TYPE_NULL);

        mDateOfBirthEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "datepicker");
            }
        });

        if (mFirebaseUser.getPhotoUrl() != null) {
            try {
                final Uri imageUri = mFirebaseUser.getPhotoUrl();
                Picasso.get().load(imageUri).into(mEditProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) mEditProfileImage.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                        mEditProfileImage.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError(Exception e) {
                        mEditProfileImage.setImageResource(R.drawable.default_profile);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "onActivityResult: ", e);
            }
        }
    }

    public boolean isErrorInInput() {
        mName = mNameEditText.getText().toString();
        String age = mAgeEditText.getText().toString();
        mAge = (age.isEmpty() ? 0 : Integer.parseInt(age));
        mPhoneNumber = mPhoneNumberEditText.getText().toString();

        if (mName.isEmpty()) {
            mNameEditText.setError("Enter a name");
            return true;
        } else if (mAge == 0) {
            mAgeEditText.setError("Enter your age");
            return true;
        } else if (mDateOfBirth == 0) {
            mDateOfBirthEditText.setError("Enter your date of birth");
            return true;
        }
        return false;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCalendar = Calendar.getInstance();
        mCalendar.set(year, month, dayOfMonth);
        String date = DateFormat.getDateFormat(this).format(mCalendar.getTime());
        mDateOfBirthEditText.setText(date);
        mDateOfBirth = mCalendar.getTimeInMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case RC_PHOTO_PICKER_INTENT:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        mLocalImageUri = imageUri;
                        Picasso.get().load(imageUri).into(mEditProfileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap imageBitmap = ((BitmapDrawable) mEditProfileImage.getDrawable()).getBitmap();
                                RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                imageDrawable.setCircular(true);
                                imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                mEditProfileImage.setImageDrawable(imageDrawable);
                            }

                            @Override
                            public void onError(Exception e) {
                                mEditProfileImage.setImageResource(R.drawable.default_profile);
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "onActivityResult: ", e);
                    }
                }
        }
    }


    private void updateUserAndAddUser() {

        String imageLabel = "IMG_" + mFirebaseUser.getUid();
        StorageReference profileImagesRef = mStorageRef.child("images/" + imageLabel);

        if (mLocalImageUri != null) {
            UploadTask mUploadTask = profileImagesRef.putFile(mLocalImageUri);
            mUploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    mProgressDialog.cancel();
                    Toast.makeText(EditProfile.this, "Error occured while updating profile", Toast.LENGTH_LONG)
                            .show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    updateUserProfile(downloadUrl);
                }
            });
        }

        updateUserProfile(null);
    }

    private void updateUserProfile(final Uri profileImageUri) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(mName)
                .setPhotoUri(profileImageUri)
                .build();

        mFirebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            User user = new User(mFirebaseUser.getUid(), mName, mEmail, mGender, mDateOfBirth, mAge, mPhoneNumber);
                            mFirebaseFirestore.collection("Users").document(mFirebaseUser.getUid())
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
                        }
                    }
                });

    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Void> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Void>(this) {
            @Nullable
            @Override
            public Void loadInBackground() {
                updateUserAndAddUser();
                User user = new User();
                user.setId(mFirebaseUser.getUid());
                user.setName(mName);
                user.setEmail(mEmail);
                user.setGender(mGender);
                user.setDateOfBirth(mDateOfBirth);
                user.setAge(mAge);
                user.setPhoneNumber(mPhoneNumber);
                database.userDao().insertUser(user);
                return null;
            }

            @Override
            protected void onStartLoading() {
                mProgressDialog.show();
                mProgressDialog.setCancelable(false);
                forceLoad();
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Void> loader, Void data) {
        Toast.makeText(EditProfile.this, "Profile registration completed", Toast.LENGTH_SHORT)
                .show();
        mProgressDialog.cancel();
        Intent intent = new Intent(EditProfile.this, DashBoard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Void> loader) {

    }
}
