package com.example.dontworry.mobilephonetest;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.example.dontworry.mobilephonetest.base.BaseFragment;
import com.example.dontworry.mobilephonetest.fragment.LocalAudioFragment;
import com.example.dontworry.mobilephonetest.fragment.LocalVideoFragment;
import com.example.dontworry.mobilephonetest.fragment.NetAudioFragment;
import com.example.dontworry.mobilephonetest.fragment.NetVideoFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rg_main;

    private ArrayList<BaseFragment> fragments;


    private int position;
    private Fragment tempFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_main = (RadioGroup) findViewById(R.id.rg_main);
        initFragment();
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_main.check(R.id.rb_local_video);
    }

    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new LocalAudioFragment());
        fragments.add(new LocalVideoFragment());
        fragments.add(new NetAudioFragment());
        fragments.add(new NetVideoFragment());

    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            switch (i) {
                case R.id.rb_local_audio:
                    position = 0;
                    break;
                case R.id.rb_local_video:

                    position = 1;
                    break;
                case R.id.rb_net_audio:

                    position = 2;
                    break;
                case R.id.rb_net_video:

                    position = 3;
                    break;
            }
            BaseFragment currentFragment = fragments.get(position);
            addFragment(currentFragment);
        }
    }

    private void addFragment(Fragment currentFragment) {
        if (tempFragment != currentFragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (!currentFragment.isAdded()) {
                if (tempFragment != null) {
                    ft.hide(tempFragment);
                }
                ft.add(R.id.fl_content, currentFragment);
            } else {
                if (tempFragment != null) {
                    ft.hide(tempFragment);
                }
                ft.show(currentFragment);
            }
            ft.commit();
            tempFragment = currentFragment;
        }
    }
}
