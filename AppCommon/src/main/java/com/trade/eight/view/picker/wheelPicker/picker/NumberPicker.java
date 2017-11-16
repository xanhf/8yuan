package com.trade.eight.view.picker.wheelPicker.picker;

import android.app.Activity;

/**
 * 数字选择器
 *
 * @author
 * @since 2015/10/24
 */
public class NumberPicker extends SinglePicker<Number> {

    public NumberPicker(Activity activity) {
        super(activity, new Number[]{});
    }

    /**
     * 设置数字范围，递增量为1
     */
    public void setRange(int startNumber, int endNumber) {
        setRange(startNumber, endNumber, 1);
    }

    /**
     * 设置数字范围及递增量
     */
    public void setRange(int startNumber, int endNumber, int step) {
        for (int i = startNumber; i <= endNumber; i = i + step) {
            items.add(i);
        }
    }

    /**
     * 设置数字范围及递增量
     */
    public void setRange(double startNumber, double endNumber, double step) {
        for (double i = startNumber; i <= endNumber; i = i + step) {
            items.add(i);
        }
    }

    /**
     * 设置默认选中的数字
     */
    public void setSelectedItem(int number) {
        super.setSelectedItem(number);
    }

    /**
     * 设置默认选中的数字
     */
    public void setSelectedItem(double number) {
        super.setSelectedItem(number);
    }

    public void setOnNumberPickListener(OnNumberPickListener listener) {
        super.setOnItemPickListener(listener);
    }

    public interface OnNumberPickListener extends OnItemPickListener<Number> {

    }

}
