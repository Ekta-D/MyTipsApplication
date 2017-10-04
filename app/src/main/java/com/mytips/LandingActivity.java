package com.mytips;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class LandingActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mRevealView;
    private boolean hidden = true;
    private ImageButton add_profile, share, invite, setting, user_profile;
    Spinner spinnerProfile, spinnerReportType;
    String[] profileArray = new String[]{"[none]"};
    String[] reportTypeArray = new String[]{"Daily", "Weekly", "Bi-Weekly", "Monthly", "Yearly"};
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        mRevealView = (LinearLayout) findViewById(R.id.reveal_layout);
        mRevealView.setVisibility(View.INVISIBLE);

        add_profile = (ImageButton) findViewById(R.id.add_profile_icon);
        share = (ImageButton) findViewById(R.id.share);
        invite = (ImageButton) findViewById(R.id.invite);
        setting = (ImageButton) findViewById(R.id.settings);
        user_profile = (ImageButton) findViewById(R.id.profile);


        add_profile.setOnClickListener(this);
        user_profile.setOnClickListener(this);

//        spinnerProfile = (Spinner) findViewById(R.id.spinner_profile);
//        spinnerReportType = (Spinner) findViewById(R.id.spinner_report);
//
//        ArrayAdapter<String> profileAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, profileArray);
//        spinnerProfile.setAdapter(profileAdapter);
//
//        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, reportTypeArray);
//        spinnerReportType.setAdapter(typeAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void hideRevealView() {
        if (mRevealView.getVisibility() == View.VISIBLE) {
            mRevealView.setVisibility(View.GONE);
            hidden = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                int cx = (mRevealView.getLeft() + mRevealView.getRight());
                int cy = mRevealView.getTop();
                int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

                //Below Android LOLIPOP Version
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    SupportAnimator animator =
                            ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(700);

                    SupportAnimator animator_reverse = animator.reverse();

                    if (hidden) {
                        mRevealView.setVisibility(View.VISIBLE);
                        animator.start();
                        hidden = false;
                    } else {
                        animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                            @Override
                            public void onAnimationStart() {

                            }

                            @Override
                            public void onAnimationEnd() {
                                mRevealView.setVisibility(View.INVISIBLE);
                                hidden = true;

                            }

                            @Override
                            public void onAnimationCancel() {

                            }

                            @Override
                            public void onAnimationRepeat() {

                            }
                        });
                        animator_reverse.start();
                    }
                }
                // Android LOLIPOP And ABOVE Version
                else {
                    if (hidden) {
                        Animator anim = android.view.ViewAnimationUtils.
                                createCircularReveal(mRevealView, cx, cy, 0, radius);
                        mRevealView.setVisibility(View.VISIBLE);
                        anim.start();
                        hidden = false;
                    } else {
                        Animator anim = android.view.ViewAnimationUtils.
                                createCircularReveal(mRevealView, cx, cy, radius, 0);
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mRevealView.setVisibility(View.INVISIBLE);
                                hidden = true;
                            }
                        });
                        anim.start();
                    }
                }
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        hideRevealView();
        switch (v.getId()) {

            case R.id.add_profile_icon:
                Toast.makeText(this, "Add Profile is here", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LandingActivity.this, SplashActivity.class));
                break;

        }
    }
}
