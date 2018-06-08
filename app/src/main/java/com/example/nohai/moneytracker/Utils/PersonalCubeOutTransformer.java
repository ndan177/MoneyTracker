package com.example.nohai.moneytracker.Utils;

import android.view.View;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;

public class PersonalCubeOutTransformer extends ABaseTransformer {

    @Override
    protected void onTransform(View view, float position) {
        view.setPivotX(position < 0f ? view.getWidth() : 0f);
        view.setPivotY(view.getHeight() * 0.5f);
        view.setRotationY(45f * position);
    }

    @Override
    public boolean isPagingEnabled() {
        return true;
    }

}

