package com.lokeshmudgal06.vinsolproject;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {

            Fragment fragment = MeetingListFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

    }


    public void addFragmentToBackStack(Fragment fragment) {
        addFragmentToBackStack(fragment, fragment.getClass().getSimpleName());
    }

    public void addFragmentToBackStack(Fragment fragment, String tag) {


        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f != null && f.getClass() == fragment.getClass()) {

        } else {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, tag)
                    .addToBackStack(null)
                    .commit();
        }
    }

}
