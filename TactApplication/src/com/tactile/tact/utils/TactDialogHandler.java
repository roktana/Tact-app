package com.tactile.tact.utils;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tactile.tact.R;

/**
 * Class to handle error messages, progress screen and others modal message screen in application
 * @author labreu
 *
 */
public class TactDialogHandler extends Dialog {

	private static Context mContext;
	public View.OnClickListener mDismissClickListener;
    public View mView;
    private EditText email;
	
	private static final int PROGRESS_DIALOG 	= 0;
	private static final int INFO_DIALOG        = 1;
	private static final int CONFIRM_DIALOG 	= 2;
	private static final int CONFIRM_3B_DIALOG  = 3;
    private static final int RESET_PWD_DIALOG   = 4;

    public TactDialogHandler(final Context context)
    {
		super(context, R.style.CustomDialogThemeTrasparent);
		mContext = context;
		mDismissClickListener = new View.OnClickListener() {
			public void onClick(View v) {
                TactDialogHandler.this.dismiss();
			}
		};
	}
	
	/**
	 * Set the layout based in parameters
	 */
	private void setTheme(int pType, String pMessage, String pTitle,
                          String pButtonOKText,  String pButtonCancelText, String pButtonConfirmationText, String pButtonNotOkText,
                          View.OnClickListener pButtonOKOnclickListener,  View.OnClickListener pButtonCancelOnclickListener,
                          View.OnClickListener pButtonConfiramtionOnclickListener, View.OnClickListener pButtonNotOkOnclickListener, boolean transparent)
    {
		LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = layoutInflater.inflate(R.layout.custom_dialog_view, null);

        Button btnOk            = (Button)mView.findViewById(R.id.btnOk);
        Button btnCancel        = (Button)mView.findViewById(R.id.cancelButton);
        Button btnConfirmation  = (Button)mView.findViewById(R.id.confirmButton);
        Button btnOk3           = (Button)mView.findViewById(R.id.ok3Button);
        Button btnNotOk3        = (Button)mView.findViewById(R.id.notOk3Button);
        Button btnCancel3       = (Button)mView.findViewById(R.id.cancel3Button);

        LinearLayout contentLayout      = (LinearLayout)mView.findViewById(R.id.content_layout);
        LinearLayout progressBarLayout  = (LinearLayout)mView.findViewById(R.id.progress_layout);
        LinearLayout oneButtonLayout    = (LinearLayout)mView.findViewById(R.id.one_button_layout);
        LinearLayout twoButtonsLayout   = (LinearLayout)mView.findViewById(R.id.two_buttons_layout);
        LinearLayout threeButtonsLayout = (LinearLayout)mView.findViewById(R.id.three_buttons_layout);
        LinearLayout mainLayout = (LinearLayout)mView.findViewById(R.id.custom_dialog_view);

        TextView title      = (TextView)mView.findViewById(R.id.content_title);
        TextView message    = (TextView)mView.findViewById(R.id.content_text);
        TextView big_text   = (TextView)mView.findViewById(R.id.big_text);
        email               = (EditText)mView.findViewById(R.id.email);


        switch (pType) {
			case PROGRESS_DIALOG:
			{
                if (transparent) {
                    mainLayout.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_grey));
                } else {
                    mainLayout.setBackgroundColor(mContext.getResources().getColor(R.color.on_boarding_dialog_bg));
                }
				contentLayout.setVisibility(View.GONE);
				progressBarLayout.setVisibility(View.VISIBLE);
				break;
			}
            case INFO_DIALOG:
            {
                title.setText(pTitle);
                title.setVisibility(View.VISIBLE);

                message.setText(pMessage);

                btnConfirmation.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);

                btnOk.setVisibility(View.VISIBLE);
                btnOk.setText(pButtonOKText);
                if (pButtonOKOnclickListener != null)
                    btnOk.setOnClickListener(pButtonOKOnclickListener);

                oneButtonLayout.setVisibility(View.VISIBLE);
                break;
            }
            case CONFIRM_DIALOG:
			{
				title.setText(pTitle);
                title.setVisibility(View.VISIBLE);

				message.setText(pMessage);

                btnOk.setVisibility(View.GONE);

                btnCancel.setVisibility(View.VISIBLE);
                btnConfirmation.setVisibility(View.VISIBLE);

                btnCancel.setText(pButtonCancelText);
                btnConfirmation.setText(pButtonConfirmationText);

                if (pButtonCancelOnclickListener != null)
					btnCancel.setOnClickListener(pButtonCancelOnclickListener);
				if (pButtonConfiramtionOnclickListener != null)
					btnConfirmation.setOnClickListener(pButtonConfiramtionOnclickListener);

                twoButtonsLayout.setVisibility(View.VISIBLE);
				break;
			}
			case CONFIRM_3B_DIALOG:
            {
				title.setText(pTitle);
                title.setVisibility(View.VISIBLE);

				message.setText(pMessage);

                btnOk3.setText(pButtonOKText);
                btnNotOk3.setText(pButtonNotOkText);
                btnCancel3.setText(pButtonCancelText);

                if (pButtonOKOnclickListener != null)
                    btnOk3.setOnClickListener(pButtonOKOnclickListener);
				if (pButtonCancelOnclickListener != null)
                    btnNotOk3.setOnClickListener(pButtonNotOkOnclickListener);
                if (pButtonCancelOnclickListener != null){
                    btnCancel3.setOnClickListener(pButtonCancelOnclickListener);
                }

                threeButtonsLayout.setVisibility(View.VISIBLE);
				break;
			}
            case RESET_PWD_DIALOG:
            {
                big_text.setVisibility(View.VISIBLE);
                email.setVisibility(View.VISIBLE);
                btnConfirmation.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                twoButtonsLayout.setVisibility(View.VISIBLE);

                big_text.setGravity(Gravity.LEFT);
                big_text.setText(R.string.reset_password);

                message.setText(R.string.reset_password_legend);
                message.setGravity(Gravity.LEFT);

                btnConfirmation.setText(pButtonOKText);
                btnConfirmation.setOnClickListener(pButtonOKOnclickListener);
                btnCancel.setText(pButtonCancelText);
                btnCancel.setOnClickListener(pButtonCancelOnclickListener);
                break;
            }
		}

        this.setContentView(mView);

	}

    /**
     * Show information dialog with "OK" button and dismiss dialog button function
     * @param pMessage - message to show
     */
	public void showInformation(String pMessage)
    {
		this.setTheme(INFO_DIALOG, pMessage, mContext.getString(R.string.message_title_info), mContext.getString(R.string.message_button_ok),
                null, null, null, mDismissClickListener, null, null, null, false);
		this.setCancelable(false);
		this.show();
	}

    /**
     * Show information dialog with "OK" button and custom dialog button functionality
     * @param pMessage - Message to show
     * @param pDismissClickListener - Button functionality
     */
	public void showInformation(String pMessage, View.OnClickListener pDismissClickListener)
    {
		this.setTheme(INFO_DIALOG, pMessage, mContext.getString(R.string.message_title_info), mContext.getString(R.string.message_button_ok),
                "", "", "", pDismissClickListener, null, null, null, false);
		this.setCancelable(false);
		this.show();
	}

	/**
	 * Show confirmation message with 2 buttons 
	 * @param pMessage - Message to show
	 * @param pButtonOKText - button confirm text
	 * @param pButtonCancelText - Button cancel text
	 * @param pButtonOKClickListener -
	 * @param pButtonCancelClickListener
	 */
	public void showConfirmation(String pMessage, String pButtonOKText, 
								 String pButtonCancelText, 
								 View.OnClickListener pButtonOKClickListener,
								 View.OnClickListener pButtonCancelClickListener)
    {
		this.setTheme(CONFIRM_DIALOG, pMessage, mContext.getString(R.string.message_title_confirm), "", pButtonCancelText,
                pButtonOKText, "", null, pButtonCancelClickListener, pButtonOKClickListener, null, false);
		this.setCancelable(true);
		this.show();
	}

    /**
     * Show confirmation message with 2 buttons
     * @param pMessage - Message to show
     * @param pButtonOKText - button confirm text
     * @param pButtonCancelText - Button cancel text
     * @param pButtonOKClickListener -
     * @param pTitle -
     */
    public void showConfirmation(String pMessage, String pTitle, String pButtonOKText,
                                 String pButtonCancelText,
                                 View.OnClickListener pButtonOKClickListener)
    {
        this.setTheme(CONFIRM_DIALOG, pMessage, pTitle, "", pButtonCancelText,
                pButtonOKText, "", null, mDismissClickListener, pButtonOKClickListener, null, false);
        this.setCancelable(true);
        this.show();
    }

    /**
     * Show confirmation message with 2 buttons
     * @param pMessage - Message to show
     * @param pTitle - The title text
     * @param pButtonOKText - button confirm text
     * @param pButtonCancelText - Button cancel text
     * @param pButtonOKClickListener -
     * @param pButtonCancelClickListener
     */
    public void showConfirmation(String pMessage, String pTitle, String pButtonOKText,
                                 String pButtonCancelText,
                                 View.OnClickListener pButtonOKClickListener,
                                 View.OnClickListener pButtonCancelClickListener)
    {
        this.setTheme(CONFIRM_DIALOG, pMessage, pTitle, "", pButtonCancelText, pButtonOKText,
                "", null, pButtonCancelClickListener, pButtonOKClickListener, null, false);
        this.setCancelable(true);
        this.show();
    }

	/**
	 * Show a generic app error for internal exceptions management
	 */
	public void showGenericError()
	{
		this.showErrorButtonClose(mContext.getString(R.string.message_app_internal_error));
	}
	
	/**
	 * Show the progress screen
	 */
	public void showProgress(boolean transparent)
    {
		this.setTheme(PROGRESS_DIALOG, "", "", "", "", "", "", null, null, null, null, transparent);
		this.setCancelable(false);
		this.show();
	}
	
	/**
	 * Show error message with one button
	 * @param pMessage
	 * @param pButtonText
	 * @param pButtonClickListener
	 */
	public void showError(String pMessage, String pButtonText, View.OnClickListener pButtonClickListener)
	{
		this.setTheme(INFO_DIALOG, pMessage, mContext.getString(R.string.message_title_error), pButtonText, "", "", "",
                pButtonClickListener, null, null, null, false);
		this.setCancelable(false);
		this.show();
	}
	
	/**
	 * Show error message with one button with text "Close" and close dialog action
	 * @param pMessage
	 */
	public void showErrorButtonClose(String pMessage)
	{
		this.setTheme(INFO_DIALOG, pMessage, mContext.getString(R.string.message_title_error), mContext.getString(R.string.message_button_close),
                "", "", "", mDismissClickListener, null, null, null, false);
		this.setCancelable(false);
		this.show();
	}
	
	/**
	 * Show error message with one button with text "OK" and close dialog action
	 * @param pMessage
	 */
	public void showErrorButtonOK(String pMessage)
	{
		this.setTheme(INFO_DIALOG, pMessage, mContext.getString(R.string.message_title_error), mContext.getString(R.string.message_button_ok),
                "", "", "", mDismissClickListener, null, null, null, false);
		this.setCancelable(false);
		this.show();
	}
	
	/**
	 * Show error message with one button with text "OK" and close dialog action
	 * @param pMessage
	 */
	public void showQuestionMessage(String pMessage, View.OnClickListener okButtonOnClickListener)
	{
		this.setTheme(INFO_DIALOG, pMessage, mContext.getString(R.string.message_title_confirm), mContext.getString(R.string.cancel),
                "", "", "", okButtonOnClickListener, mDismissClickListener, null, null, false);
		this.setCancelable(false);
		this.show();
	}
	
	/**
     * Set the Reset Password Dialog
     * @param pResetOnClickListener Listener for "RESET" button
     */
    public void resetPassword(View.OnClickListener pResetOnClickListener){
        this.setTheme(RESET_PWD_DIALOG, "", "", mContext.getString(R.string.reset), mContext.getString(R.string.cancel),
                "", "", pResetOnClickListener, mDismissClickListener, null, null, false);
        this.setCancelable(false);
        this.show();

    }

    /**
     * Show the Progressbar View when the Dialog is open
     */
    public void showProcessCallback(){
        ((LinearLayout) mView.findViewById(R.id.progress_layout)).setVisibility(View.VISIBLE);
        ((LinearLayout) mView.findViewById(R.id.content_layout)).setVisibility(View.GONE);
    }

    /**
     * Hide the Progressbar View when the Dialog is open
     */
    public void hideProcessCallback(){
        ((LinearLayout) mView.findViewById(R.id.progress_layout)).setVisibility(View.GONE);
        ((LinearLayout) mView.findViewById(R.id.content_layout)).setVisibility(View.VISIBLE);
    }

    @Override
    public void show(){
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width  = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        this.getWindow().setAttributes(lp);
        super.show();
    }

    /**
     * Return the value of the EditText "email"
     * @return email text
     */
    public String getResetEmail(){
        if(email != null){
            return email.getText().toString();
        }
        return "";
    }


    /**
     * Show an error message in dialog
     * @param errorMsg error message
     */
    public void showErrorMessage(String errorMsg){
        TextView error_container = (TextView) mView.findViewById(R.id.error_message);
        error_container.setText(errorMsg);
        error_container.setVisibility(View.VISIBLE);
    }

    /**
     * Show a success message in dialog
     * @param successMsg success message
     */
    public void showSuccessMessage(String successMsg){
        TextView error_container = (TextView) mView.findViewById(R.id.error_message);
        error_container.setTextColor(mContext.getResources().getColor(R.color.green));
        error_container.setText(successMsg);
        error_container.setVisibility(View.VISIBLE);
    }
	
}
