package com.tactile.tact.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.tactile.tact.R;

import java.util.Calendar;

/**
 * Created by ismael on 11/26/14.
 */
public class TactCustomSpinner extends LinearLayout implements View.OnClickListener{

    public static LayoutInflater inflater;

    private PopupWindow popupWindow;
    private Dialog dialog;
    private LinearLayout popupLayout;
    private int layoutId;
    private Point coordinates;
    private int windowGravity;
    private Context context;
    private DatePicker datePicker;
    private Button pickerOkBtn;


    //TODO: make this spinner more independent

    @Override
    public void onClick(View v) {
        clickEvent();
    }

    public void clickEvent() {
        switch (spinnerType) {
            case POPUP_WINDOW:
                if (!popupWindow.isShowing()) {
                    popupWindow.showAtLocation(popupLayout, windowGravity, coordinates.x, coordinates.y);
//                    popupWindow.showAsDropDown(this, -391, -420, Gravity.NO_GRAVITY);
                } else {
                    popupWindow.dismiss();
                }
                break;
            case DIALOG:
                if (!dialog.isShowing()) {
                    dialog.show();
                }
        }

    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public void setDatePicker(DatePicker datePicker) {
        this.datePicker = datePicker;
    }

    public Button getPickerOkBtn() {
        return pickerOkBtn;
    }

    public static enum PopupWindowStyle {
        BOTTOM_MENU,
        TOP_MENU,
        IN_PLACE_MENU
    }

    public static enum SpinnerType {
        POPUP_WINDOW,
        DIALOG,
        DROPDOWN_MENU
    }

    private PopupWindowStyle windowType;
    private SpinnerType spinnerType;

    public TactCustomSpinner(Context context) {
        super(context);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public TactCustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TactCustomSpinner,
                0, 0);

        try {
            int position = a.getInteger(R.styleable.TactCustomSpinner_popupWindowPosition, 0);

            switch (position) {
                case 0:
                    windowType = PopupWindowStyle.BOTTOM_MENU;
                    break;

                case 1:
                    windowType = PopupWindowStyle.TOP_MENU;
                    break;

                case 2:
                    windowType = PopupWindowStyle.IN_PLACE_MENU;
                    break;
            }
            layoutId = a.getResourceId(R.styleable.TactCustomSpinner_windowLayout, 0);
            int type = a.getInteger(R.styleable.TactCustomSpinner_spinnerType, -1);

            switch (type) {
                case 0:
                    spinnerType = SpinnerType.DIALOG;
                    setDialogWindow();
                    break;
                case 1:
                    spinnerType = SpinnerType.POPUP_WINDOW;
                    setPopupLayout();
                    break;
                case 2:
                    spinnerType = SpinnerType.DROPDOWN_MENU;
                    //TODO
            }
            setPopupLayout();

        } finally {
            a.recycle();
        }

    }

    public TactCustomSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public PopupWindow getPopupMenu() {
        return popupWindow;
    }

    public void setPopupLayout() {
        popupWindow = new PopupWindow();
        if (layoutId != 0) {
            popupLayout = (LinearLayout) inflater.inflate(layoutId, null);
            popupWindow.setContentView(popupLayout);
            popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());

            switch (windowType) {
                case BOTTOM_MENU:
                    setDisplayPosition(0,0, Gravity.BOTTOM|Gravity.LEFT);
                    //TODO x and y positions
                    break;
                case IN_PLACE_MENU:
                    //TODO
                    break;
                case TOP_MENU:
                    //TODO
                    break;
            }
        }
    }

    public void dialogDismiss() {
        dialog.dismiss();
    }

    public void setDialogWindow() {
        dialog = new Dialog(this.context, R.style.TactDialogFromBottom);
        if (layoutId != 0) {
            popupLayout = (LinearLayout) inflater.inflate(layoutId, null);
            dialog.setContentView(popupLayout);
            dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            pickerOkBtn         = (Button) dialog.getWindow().findViewById(R.id.date_dialog_done);
            setDatePicker((DatePicker) dialog.getWindow().findViewById(R.id.date_dialog_date));

            Button cancel       = (Button) dialog.getWindow().findViewById(R.id.date_dialog_cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
    }

    public void setDisplayPosition(int x, int y, int gravity) {
        coordinates = new Point();
        coordinates.x = x;
        coordinates.y = y;
        windowGravity = gravity;
    }

    public static LayoutInflater getInflater() {
        return inflater;
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public LinearLayout getPopupLayout() {
        return popupLayout;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public int getWindowGravity() {
        return windowGravity;
    }

    public PopupWindowStyle getWindowType() {
        return windowType;
    }

    public SpinnerType getSpinnerType() {
        return spinnerType;
    }
}
