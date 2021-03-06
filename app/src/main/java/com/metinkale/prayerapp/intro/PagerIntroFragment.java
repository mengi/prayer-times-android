/*
 * Copyright (c) 2013-2017 Metin Kale
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

package com.metinkale.prayerapp.intro;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Scroller;
import android.widget.TextView;

import com.metinkale.prayer.R;
import com.metinkale.prayerapp.MainActivity;
import com.metinkale.prayerapp.settings.Prefs;
import com.metinkale.prayerapp.vakit.fragments.VakitFragment;
import com.metinkale.prayerapp.vakit.times.Times;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.lang.reflect.Field;

/**
 * Created by metin on 17.07.2017.
 */

public class PagerIntroFragment extends IntroFragment {

    private ViewPager mPager;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.intro_pager, container, false);


        Toolbar toolbar = mView.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.appName);
        toolbar.setNavigationIcon(MaterialDrawableBuilder.with(getActivity())
                .setIcon(MaterialDrawableBuilder.IconValue.MENU)
                .setColor(Color.WHITE)
                .setToActionbarSize()
                .build());


        return mView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Fragment frag = new VakitFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.basecontent, frag).commit();
    }

    private Runnable mAction = new Runnable() {
        @Override
        public void run() {
            if (mPager == null) {
                if (getView() == null) return;
                mPager = getView().findViewById(R.id.pager);
                if (mPager == null) return;
                else {
                    trySlowDownViewPager(mPager);


                }
            }

            int item = (mPager.getCurrentItem() + 1) % mPager.getAdapter().getCount();
            mPager.setCurrentItem(item, true);
            mPager.postDelayed(mAction, item == 0 ? 5000 : 2000);
        }
    };

    private static void trySlowDownViewPager(ViewPager pager) {
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            scroller.set(pager, new Scroller(pager.getContext(), (android.view.animation.Interpolator) interpolator.get(pager)) {
                @Override
                public void startScroll(int startX, int startY, int dx, int dy,
                                        int duration) {
                    super.startScroll(startX, startY, dx, dy, duration * 10);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            //ignore
        }
    }

    @Override
    protected void onSelect() {
        if (mView != null) {
            mView.removeCallbacks(mAction);
            mView.postDelayed(mAction, 500);
        }
    }

    @Override
    protected void onEnter() {
    }

    @Override
    protected void onExit() {
        if (mView != null)
            mView.removeCallbacks(mAction);

    }

    @Override
    protected boolean shouldShow() {
        return Prefs.showIntro();
    }
}
