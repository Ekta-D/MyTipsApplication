/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mytips;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 * <p>
 * <p>This class is used by the {@link } and {@link
 * ScreenSlideActivity} samples.</p>
 */
public class ScreenSlidePageFragment extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;
    double tab_size = 0;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(int pageNumber) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_screen_slide_page, container, false);

        TypedArray images = getResources().obtainTypedArray(R.array.onboarding_images);

        int[] rainbow = getActivity().getResources().getIntArray(R.array.intro_rainbow);

        LinearLayout layoutBack = (LinearLayout) rootView.findViewById(R.id.layout_back);
        ImageView view = rootView.findViewById(R.id.imageView);
        Drawable drawable = compressImage(getResources().getDrawable(images.getResourceId(mPageNumber, -1)));
        view.setImageDrawable(drawable);


        boolean isTab = isTablet();
        if (isTab) {
            int paddingDp = 0;
            if (tab_size > 6 && tab_size < 7) {
                paddingDp = 50;
            } else {
                paddingDp = 100;
            }

            float density = getActivity().getResources().getDisplayMetrics().density;
            int paddingPixel = (int) (paddingDp * density);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(paddingPixel, 0, paddingPixel, 0);
            view.setLayoutParams(layoutParams);
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        System.out.println("metrics: "+metrics.density);
                int paddingDp = 20;
                float density = getActivity().getResources().getDisplayMetrics().density;
                int paddingPixel = (int) (paddingDp * density);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                switch(metrics.densityDpi) {
                    case DisplayMetrics.DENSITY_420:
                        layoutParams.setMargins(paddingPixel, 0, paddingPixel, 0);
                        view.setLayoutParams(layoutParams);
                        break;
                    case DisplayMetrics.DENSITY_XXHIGH:

                        layoutParams.setMargins(paddingPixel, 0, paddingPixel, 0);
                        view.setLayoutParams(layoutParams);
                        break;

                    case DisplayMetrics.DENSITY_XXXHIGH:

                break;

        }

        layoutBack.setBackgroundColor(rainbow[mPageNumber]);

        images.recycle();
        // Set the title view to show the page number.
        /*((TextView) rootView.findViewById(android.R.id.text1)).setText(
                getString(R.string.title_template_step, mPageNumber + 1));*/

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    public Drawable compressImage(Drawable drawable) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width-60, height, false);

        return new BitmapDrawable(getResources(), bitmapResized);
    }

    public boolean isTablet() {
        try {
            // Compute screen size
            Context context = getActivity();
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            float screenWidth = dm.widthPixels / dm.xdpi;
            float screenHeight = dm.heightPixels / dm.ydpi;
            double size = Math.sqrt(Math.pow(screenWidth, 2) +
                    Math.pow(screenHeight, 2));
            // Tablet devices have a screen size greater than 6 inches
            tab_size = size;
            return size >= 6;
        } catch (Throwable t) {
            return false;
        }
    }
}


