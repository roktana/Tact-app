package com.tactile.tact.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.tactile.tact.R;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.services.network.RedirectURLGetter;
import com.tactile.tact.services.network.TactNetworking;
import com.tactile.tact.utils.Utils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.net.URL;

public class ProfileIconView extends RelativeLayout {

    public static enum IconState {
        INITIALS,
        GENERIC_ICON,
        PHOTO_ICON,
        COMPANY_ICON
    }

    private static final int
            ICON_SIZE_SMALL = 0,
            ICON_SIZE_MEDIUM = 1,
            ICON_SIZE_LARGE = 2;

    private int iconSize;

    private View genericBackgroundView = null;
    private TextView initialsView = null;
    private ImageView genericIconView = null;
    private RoundImageView photoIconView = null;


    public ProfileIconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributes(context, attrs);
        initViews(context);
    }

    public ProfileIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
        initViews(context);
    }

    public ProfileIconView(Context context) {
        super(context);
        this.iconSize = ICON_SIZE_SMALL;
        initViews(context);
    }

    /**
     *
     * @param context
     * @param attrs
     */
    protected void initAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProfileIconView);
        if (typedArray != null) {
            this.iconSize = typedArray.getInt(R.styleable.ProfileIconView_iconSize, 0);
            typedArray.recycle();
        }
    }

    /**
     *
     * @param context
     */
    private void initViews(Context context) {
        View itemView;
        switch (this.iconSize) {
            case ICON_SIZE_MEDIUM:
                itemView = View.inflate(context, R.layout.contact_icon_generic_medium, this);
                break;
            case ICON_SIZE_SMALL:
                itemView = View.inflate(context, R.layout.contact_icon_generic_small, this);
                break;
            case ICON_SIZE_LARGE:
                itemView = View.inflate(context, R.layout.contact_icon_generic_large, this);
                break;
            default:
                itemView = View.inflate(context, R.layout.contact_icon_generic_small, this);
                break;
        }

        if(!itemView.isInEditMode()){
            genericBackgroundView   = findViewById(R.id.contact_icon_generic_background_circle);
            initialsView            = (TextView) findViewById(R.id.contact_icon_initials);
            genericIconView         = (ImageView) findViewById(R.id.contact_icon_generic);
            photoIconView           = (RoundImageView) findViewById(R.id.contact_icon_photo);
        }



    }

    /**
     *
     * @param resourceID
     */
    public void setImageResource(int resourceID) {
        genericIconView.setImageResource(resourceID);
    }

    /**
     *
     * @param initials
     */
    public void setInitials(String initials) {
        initialsView.setText(initials);
    }

    /**
     *
     * @param state
     */
    public void setIconState(IconState state) {
        switch (state) {
            case INITIALS:
                genericBackgroundView.setVisibility(VISIBLE);
                initialsView.setVisibility(VISIBLE);
                genericIconView.setVisibility(GONE);
                photoIconView.setVisibility(GONE);
                genericBackgroundView.setBackground(getResources().getDrawable(R.drawable.contacts_generic_icon_background));
                break;
            case GENERIC_ICON:
                genericBackgroundView.setVisibility(VISIBLE);
                initialsView.setVisibility(GONE);
                genericIconView.setVisibility(VISIBLE);
                photoIconView.setVisibility(GONE);
                genericBackgroundView.setBackground(getResources().getDrawable(R.drawable.contacts_generic_icon_background));
                break;
            case PHOTO_ICON:
                genericBackgroundView.setVisibility(GONE);
                initialsView.setVisibility(GONE);
                genericIconView.setVisibility(GONE);
                photoIconView.setVisibility(VISIBLE);
                break;
            case COMPANY_ICON:
                genericBackgroundView.setVisibility(VISIBLE);
                initialsView.setVisibility(GONE);
                genericIconView.setVisibility(GONE);
                photoIconView.setVisibility(GONE);
                genericBackgroundView.setBackground(getResources().getDrawable(R.drawable.office_icon_circle));
                break;
        }
    }

    private void setInitialsLabel(String initials){
        if (initials != null && !initials.isEmpty()) {
            if (Utils.hasValidInitials(initials.substring(0, 1), initials.substring(1, 2))) {
                this.setIconState(IconState.INITIALS);
                this.setInitials(initials);
            }
            else {
                this.setIconState(IconState.GENERIC_ICON);
            }
        }
        else {
            this.setIconState(IconState.GENERIC_ICON);
        }
    }

    public void loadImage(String url, String initials, Boolean company){
        if (!company) {
            if (url != null) {
                if (url.startsWith("http")) {
                    setImageUrl(url);
                }
                else {
                    new RedirectURLGetter(this, null).execute(TactNetworking.getURL() + "/photo_store?photo_url=" + url);
                }
            }
            else {
                setInitialsLabel(initials);
            }
        }
        else {
            this.setIconState(IconState.COMPANY_ICON);
        }
    }

    public void loadImage(Bitmap localContactImage, String initials){
        if (localContactImage != null) {
            Bitmap roundBitmap = RoundImageView.getCroppedBitmap(localContactImage, localContactImage.getWidth()*2);
            genericIconView.setImageBitmap(roundBitmap);
            setIconState(IconState.GENERIC_ICON);
        }
        else {
            setInitialsLabel(initials);
        }
    }

    public void setImageUrl(String realPhotoUrl){
        photoIconView.setImageUrl(realPhotoUrl, TactApplication.getInstance().getImageLoader());
        setIconState(IconState.PHOTO_ICON);
    }

    /**
     *
     * @return
     */
    public Drawable getDrawable() {
        if (photoIconView.getVisibility() == VISIBLE) {
            return photoIconView.getDrawable();
        }
        return null;
    }

}
