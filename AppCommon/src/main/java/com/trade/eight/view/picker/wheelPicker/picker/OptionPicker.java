package com.trade.eight.view.picker.wheelPicker.picker;

import android.app.Activity;

import java.util.List;

/**
 * 单项文本选择器
 *
 * @author
 * @since 2015/9/29
 */
public class OptionPicker extends SinglePicker<String> {

    public OptionPicker(Activity activity, String[] items) {
        super(activity, items);
    }

    public OptionPicker(Activity activity, List<String> items) {
        super(activity, items);
    }

    public void setOnOptionPickListener(OnOptionPickListener listener) {
        super.setOnItemPickListener(listener);
    }

    public interface OnOptionPickListener extends OnItemPickListener<String> {

    }

}