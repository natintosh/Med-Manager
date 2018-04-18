package com.example.oluwatobiloba.medmanager.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oluwatobiloba.medmanager.R;
import com.example.oluwatobiloba.medmanager.database.AppDatabase;
import com.example.oluwatobiloba.medmanager.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class UserProfile extends AppCompatActivity {


    private static String TAG = UserProfile.class.getSimpleName();

    String userId;

    ImageView profileImageView;
    TextView mNameText;
    TextView mEmailText;
    TextView mGenderText;
    TextView mPhoneNumberText;
    TextView mAgeText;
    TextView mDobText;

    String name;
    String email;
    String gender;
    String phoneNumber;
    String age;
    long dob;

    AppDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        database = AppDatabase.getAppDatabase(this);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        User user = database.userDao().findById(userId);

        profileImageView = findViewById(R.id.full_details_imageview);
        mNameText = findViewById(R.id.full_details_username);
        mEmailText = findViewById(R.id.full_details_email);
        mGenderText = findViewById(R.id.full_details_gender);
        mPhoneNumberText = findViewById(R.id.full_details_phonenumber);
        mAgeText = findViewById(R.id.full_details_age);
        mDobText = findViewById(R.id.full_details_dob);

        name = user.getName();
        email = user.getEmail();
        gender = user.getGender();
        phoneNumber = user.getPhoneNumber();
        age = String.valueOf(user.getAge());
        dob = user.getDateOfBirth();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dob);
        String date = DateFormat.getDateFormat(this).format(calendar.getTime());

        mNameText.setText(name);
        mEmailText.setText(email);
        mGenderText.setText(gender);
        mPhoneNumberText.setText(phoneNumber);
        mAgeText.setText(age);
        mDobText.setText(date);

        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser.getPhotoUrl() != null) {
            Picasso.get().load(mFirebaseUser.getPhotoUrl())
                    .resize(120, 120)
                    .into(profileImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) profileImageView.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            profileImageView.setImageDrawable(imageDrawable);
                        }

                        @Override
                        public void onError(Exception e) {
                            profileImageView.setImageResource(R.drawable.default_profile);
                        }
                    });
        } else {
            profileImageView.setImageResource(R.drawable.default_profile);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        } else if (id == R.id.action_signout) {
            NavUtils.navigateUpFromSameTask(this);
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
