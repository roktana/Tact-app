package com.tactile.tact.views;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tactile.tact.R;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemDetailView extends LinearLayout {

    public static final String TAG = ItemDetailView.class.getSimpleName();

    public enum PresetOnClickListenerType {
        DIALER,
        EMAIL,
        WEBSITE,
        FACEBOOK,
        TWITTER,
        LINKED_IN,
        MAP,
    }

    private Context context = null;
    private String title = null;
    private String value = null;

    private TextView titleView = null;
    private TextView valueView = null;
    private View dividerView = null;

    /**
     * Builder for ItemDetailView
     * @param context the current context
     * @param title the title of the view
     * @param value the value for the value view
     */
    public ItemDetailView(Context context, String title, String value) {
        super(context);
        this.context = context;
        this.title = title;
        this.value = value;
        initViews(context);
    }

    /**
     * Builder for ItemDetailView
     * @param context the current context
     * @param title the title of the view
     * @param value the value for the value view
     * @param presetOnClickListenerType the type of the listener
     */
    public ItemDetailView(Context context, String title, String value, PresetOnClickListenerType presetOnClickListenerType) {
        this(context, title, value);
        this.context = context;
        if(presetOnClickListenerType != null) {
            usePresetOnClickListener(presetOnClickListenerType);
        }
    }

    /**
     * Builder for ItemDetailView
     * @param context the current context
     */
    public ItemDetailView(Context context) {
        super(context);
        this.context = context;
        initViews(context);
    }

    /**
     * Builder for ItemDetailView
     * @param context the current context
     * @param attrs the set of attributes of the item detail view
     */
    public ItemDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initViews(context);
    }

    /**
     * Builder for ItemDetailView
     * @param context the current context
     * @param attrs the set of attributes of the item detail view
     * @param defStyle int
     */
    public ItemDetailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initViews(context);
    }

    /**
     * Set the title for the view
     * @param title the title of the view
     */
    public void setTitle(String title) {
        this.title = title;
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    /**
     * Get the current title of the view
     * @return the current title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Set the value for the value view
     * @param value the value for the value view
     */
    public void setValue(String value) {
        this.value = value;
        if (valueView != null) {
            this.valueView.setSingleLine(!this.value.contains("\n"));
            valueView.setText(value);
        }
    }

    /**
     * Get the value of the value view
     * @return the value of the value view
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Set visibility for the divider
     * @param visible true/false if we want to display it
     */
    public void setDividerVisible(boolean visible) {
        dividerView.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    /**
     * Initialize the elements of the view
     * @param context the current context
     */
    private void initViews(Context context) {
        View.inflate(context, R.layout.single_contact_detail, this);
        this.titleView      = (TextView) findViewById(R.id.contact_details_item_title);
        this.valueView      = (TextView) findViewById(R.id.contact_details_item_detail);
        this.dividerView    = findViewById(R.id.contact_details_item_divider);
        if (this.titleView != null) {
            this.titleView.setText(this.title);
        }
        if (this.valueView != null) {
            this.valueView.setSingleLine(!this.value.contains("\n"));
            this.valueView.setText(this.value);
        }
    }

    /**
     * Set the on click listener for the element specified as type
     * @param type DIALER, EMAIL, WEBSITE, FACEBOOK, TWITTER, LINKED_IN, MAP
     */
    public void usePresetOnClickListener(PresetOnClickListenerType type) {
        switch (type) {
            case DIALER:
                setDialerOnClickListener();
                break;
            case EMAIL:
                setEmailOnClickListener();
                break;
            case WEBSITE:
                setWebsiteOnClickListener();
                break;
            case FACEBOOK:
                setFacebookOnClickListener();
                break;
            case TWITTER:
                setTwitterOnClickListener();
                break;
            case LINKED_IN:
                setLinkedInOnClickListener();
                break;
            case MAP:
                setMapOnClickListener();
                break;
        }
    }

    /**
     * Set the onclick listener for dialer option
     */
    private void setDialerOnClickListener() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + value));
                context.startActivity(intent);
            }
        });
    }

    /**
     * Set the onclick listener for email option
     */
    private void setEmailOnClickListener() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{value});
                context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.send_email)));
            }
        });
    }

    /**
     * Set the onclick listener for website option
     */
    private void setWebsiteOnClickListener() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = value;
                if (!url.startsWith("http")) {
                    url = "http://" + url;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            }
        });
    }

    /**
     * Set the onclick listener for facebook option
     */
    private void setFacebookOnClickListener() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean useFaceBookApp = false;

                // If the facebook app is installed, use it. Otherwise, use the browser
                if (context.getPackageManager() != null) {
                    try {
                        context.getPackageManager().getApplicationInfo("com.facebook.katana", 0);
                        useFaceBookApp = true;
                    } catch (PackageManager.NameNotFoundException e) {
                        useFaceBookApp = false;
                    }
                }
                if (useFaceBookApp) {
                    // Get the id for the account before launching the app
                    RequestQueue requestQueue = TactApplication.getInstance().getRequestQueue();
                    requestQueue.add(new JsonObjectRequest("https://graph.facebook.com/" + value, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    String id = null;
                                    try {
                                        id = (String) jsonObject.get("id");
                                    } catch (JSONException e) {
                                        Log.e(TAG, "Facebook user doesn't exist");
                                        e.printStackTrace();
                                    }
                                    if (id != null) {
                                        launchApp(id);
                                    } else {
                                        launchBrowser(value);
                                    }
                                }

                                private void launchApp(String id) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + id));
                                    context.startActivity(intent);
                                }

                                private void launchBrowser(String username) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/" + username));
                                    context.startActivity(intent);
                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/" + value));
                            context.startActivity(intent);
                        }
                    }
                    ));
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/" + value));
                    context.startActivity(intent);
                }
            }
        });
    }

    /**
     * Set the onclick listener for twitter option
     */
    private void setTwitterOnClickListener() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean useTwitterApp = false;

                // If the twitter app is installed, use it. Otherwise, use the browser
                if (context.getPackageManager() != null) {
                    try {
                        context.getPackageManager().getPackageInfo("com.twitter.android", 0);
                        useTwitterApp = true;
                    } catch (PackageManager.NameNotFoundException e) {
                        useTwitterApp = false;
                    }
                }

                Intent intent;
                if (useTwitterApp) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + value));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    // no Twitter app, revert to browser
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + value));
                }
                context.startActivity(intent);
            }
        });
    }

    /**
     * Set the onclick listener for linkedin option
     */
    private void setLinkedInOnClickListener() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
    }

    /**
     * Set the onclick listener for map option
     */
    private void setMapOnClickListener() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "geo:0,0?q=" + value.replace("\n", "+").replace(" ", "+");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent);
            }
        });
    }
}
