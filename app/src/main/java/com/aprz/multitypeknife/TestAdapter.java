package com.aprz.multitypeknife;


import android.view.View;

import androidx.annotation.NonNull;

import com.aprz.multitypeknife.annotation.ItemBinder;
import com.aprz.multitypeknife.annotation.ItemLayoutId;
import com.aprz.multitypeknife.api.BaseViewHolder;

import me.drakeet.multitype.MultiTypeAdapter;

public class TestAdapter extends MultiTypeAdapter {

    public TestAdapter() {
        register(Bean.class, new TestItemViewBinder());
    }

    @ItemBinder(name = "TestItemViewBinder")
    static class TestViewHolder extends BaseViewHolder<Bean> {

        @ItemLayoutId
        public static int layoutId = R.layout.activity_main;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void bindView(Bean bean) {

        }


    }

}
