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
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class LandingActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mRevealView;
    private boolean hidden = true;
    private ImageButton add_day, profile, share, invite, preferences, backup;
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

        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);

        add_day = (ImageButton) findViewById(R.id.add_day);
        profile = (ImageButton) findViewById(R.id.profile);
        share = (ImageButton) findViewById(R.id.share);
        invite = (ImageButton) findViewById(R.id.invite);
        preferences = (ImageButton) findViewById(R.id.preferences);
        backup = (ImageButton) findViewById(R.id.backup);

        add_day.setOnClickListener(this);
        profile.setOnClickListener(this);
        share.setOnClickListener(this);
        invite.setOnClickListener(this);
        preferences.setOnClickListener(this);
        backup.setOnClickListener(this);

        spinnerProfile = (Spinner) findViewById(R.id.spinner_profile);
        spinnerReportType = (Spinner) findViewById(R.id.spinner_report);

        ArrayAdapter<String> profileAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, profileArray);
        spinnerProfile.setAdapter(profileAdapter);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, reportTypeArray);
        spinnerReportType.setAdapter(typeAdapter);
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
                /*int cx = (mRevealView.getLeft() + mRevealView.getRight());
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
                return true;*/
// finding X and Y co-ordinates
                int cx = (mRevealView.getLeft() + mRevealView.getRight());
                int cy = (mRevealView.getTop());

                // to find  radius when icon is tapped for showing layout
                int startradius=0;
                int endradius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

                // performing circular reveal when icon will be tapped
                Animator animator = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    animator = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, startradius, endradius);
                }
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(400);

                //reverse animation
                // to find radius when icon is tapped again for hiding layout
                //  starting radius will be the radius or the extent to which circular reveal animation is to be shown

                int reverse_startradius = Math.max(mRevealView.getWidth(),mRevealView.getHeight());

                //endradius will be zero
                int reverse_endradius=0;

                // performing circular reveal for reverse animation
                Animator animate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    animate = ViewAnimationUtils.createCircularReveal(mRevealView,cx,cy,reverse_startradius,reverse_endradius);
                }
                if(hidden){

                    // to show the layout when icon is tapped
                    mRevealView.setVisibility(View.VISIBLE);
                    animator.start();
                    hidden = false;
                }
                else {
                    mRevealView.setVisibility(View.VISIBLE);

                    // to hide layout on animation end
                    animate.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mRevealView.setVisibility(View.INVISIBLE);
                            hidden = true;
                        }
                    });
                    animate.start();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        hideRevealView();
        switch (v.getId()) {
            case R.id.add_day:
                startActivity(new Intent(getBaseContext(), AddDayActivity.class));
                break;
            case R.id.profile:
                startActivity(new Intent(getBaseContext(), AddProfileActivity.class));
                break;
            case R.id.preferences:
                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                break;
            case R.id.share:

                break;
            case R.id.invite:

                break;
            case R.id.backup:

                break;
        }
    }
}
