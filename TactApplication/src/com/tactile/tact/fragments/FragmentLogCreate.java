package com.tactile.tact.fragments;

import android.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.tactile.tact.R;

import com.tactile.tact.activities.HomeActivity;
import com.tactile.tact.activities.LogContactLazyLoadListActivity;
import com.tactile.tact.activities.LogContactListActivity;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.TactDataSource;
import com.tactile.tact.database.entities.Contact;
import com.tactile.tact.database.entities.FrozenFeedItem;
import com.tactile.tact.database.entities.Opportunity;
import com.tactile.tact.database.model.FeedItem;
import com.tactile.tact.database.model.FeedItemEvent;
import com.tactile.tact.database.model.FeedItemLog;
import com.tactile.tact.database.model.FeedItemLogRelated;
import com.tactile.tact.database.model.FeedItemMetadata;
import com.tactile.tact.services.events.EventGoBackHomeActivity;
import com.tactile.tact.services.events.EventHideActivityActionBar;
import com.tactile.tact.services.events.EventOptionButtonPressed;
import com.tactile.tact.utils.CalendarAPI;
import com.tactile.tact.utils.Log;
import com.tactile.tact.utils.ObservableScrollView;
import com.tactile.tact.utils.TactCustomSpinner;
import com.tactile.tact.utils.TactDialogHandler;
import com.tactile.tact.utils.TactParallaxScrollView;
import com.tactile.tact.utils.Utils;
import com.tactile.tact.utils.interfaces.ScrollViewListener;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;


/**
 * Created by ismael on 11/20/14.
 */
public class FragmentLogCreate extends FragmentTactBase {

    //****************************  CLASS VARIABLES *********************************\\

    // Private class variables
    private TactCustomSpinner subjectSpinner;
    private TactCustomSpinner due_date_picker;
    private TactParallaxScrollView log_scrollbar;
    private RelativeLayout floating_log_button;
    private LinearLayout log_action_bar;
    private LinearLayout log_title;
    private LinearLayout log_to_field;
    private TextView log_subject_field;
    private LinearLayout log_icon_field;
    private TextView subject_log_call;
    private TextView subject_log_email;
    private TextView subject_log_note;
    private TextView subject_log_log;
    private EditText subject_edit_text;
    private LinearLayout log_description_layout;
    private InputMethodManager imm;
    private EditText description_edit_text;
    private TextView titleActionBar;
    private LinearLayout actionBar;
    private TextView titleText;
    private TextView contactSelectName;
    private TextView relatedSelectName;
    private TextView contactSoft;
    private TextView relatedSoft;
    private TextView description_text_view;
    private TextView descriptionSoft;
    private TextView dueDateSoft;
    private TextView subject_title_soft;
    private TextView subject_text_view;
    final Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    private RelativeLayout.LayoutParams buttonParams;

    TextView contactRequiredError;
    TextView relatedRequiredError;

    private enum FloatingButtonState {
        BOTTOM,
        TOP
    }

    private FloatingButtonState floatingButtonState;

    private Object who;
    private Object what;
    private Object parent;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == TactConst.CONTACT_PICK_RESULT_OK) {
            if (requestCode == TactConst.CONTACT_PICK_REQUEST_WHO) {
                who = data.getSerializableExtra("object");
                if (who != null) {
                    contactSelectName.setText(((Contact) who).getDisplayName());
                    contactSelectName.setTextColor(getResources().getColor(R.color.dark_grey));
                    contactSoft.setVisibility(View.VISIBLE);
                } else {
                    contactSelectName.setText(getActivity().getString(R.string.contact));
                    contactSelectName.setTextColor(getResources().getColor(R.color.mid_grey));
                    contactSoft.setVisibility(View.INVISIBLE);
                }
            } else if (requestCode == TactConst.CONTACT_PICK_REQUEST_WHAT) {
                what = data.getSerializableExtra("object");
                if (what != null) {
                    relatedSelectName.setText(((Contact) what).getDisplayName());
                    relatedSelectName.setTextColor(getResources().getColor(R.color.dark_grey));
                    relatedSoft.setVisibility(View.VISIBLE);
                } else {
                    relatedSelectName.setText(getActivity().getString(R.string.related_to));
                    relatedSelectName.setTextColor(getResources().getColor(R.color.mid_grey));
                    relatedSoft.setVisibility(View.INVISIBLE);
                }
            }
        }
        if (who != null || what != null) {
            //At least one of the relations must exist
            contactRequiredError.setVisibility(View.INVISIBLE);
            relatedRequiredError.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isAdHoc() {
        return parent == null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The height of your fully expanded header view (same than in the xml layout)
        final int headerHeight = getResources().getDimensionPixelSize(R.dimen.standard_action_bar_2x);


        View logView = inflater.inflate(R.layout.log_layout, container, false);

        EventBus.getDefault().post(new EventHideActivityActionBar());
        actionBar = (LinearLayout) logView.findViewById(R.id.log_create_action_bar);
        actionBar.findViewById(R.id.fragment_actionBar_layout).setBackgroundColor(getResources().getColor(R.color.tact_light_blue));
        ((ImageView)actionBar.findViewById(R.id.fragment_menu_button)).setImageDrawable(getResources().getDrawable(R.drawable.back_arrow_white));
        ((ImageView)actionBar.findViewById(R.id.fragment_option_menu_btn)).setImageDrawable(getResources().getDrawable(R.drawable.settings_white));

        LinearLayout backButton  = (LinearLayout) actionBar.findViewById(R.id.fragment_actionBar_menu_button_layout);
        LinearLayout optionButton = (LinearLayout) actionBar.findViewById(R.id.fragment_actionBar_option_button_layout);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventGoBackHomeActivity());
            }
        });

        optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventOptionButtonPressed());
            }
        });

        actionBar.findViewById(R.id.fragment_actionBar_layout).getBackground().setAlpha(0);
        titleActionBar = (TextView) actionBar.findViewById(R.id.fragment_actionBar_title_text);
        titleActionBar.setAlpha(0);
        titleActionBar.setTextColor(getResources().getColor(R.color.white));
        titleActionBar.setText(getResources().getString(R.string.log_to_salesforce));

        Bundle bundle = getArguments();
        if (bundle != null){
            parent = bundle.getSerializable("parent");
        }

        floatingButtonState = FloatingButtonState.TOP;
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        log_subject_field = (TextView) logView.findViewById(R.id.log_subject_textview);
        log_icon_field = (LinearLayout) logView.findViewById(R.id.log_icon_field);
        log_to_field = (LinearLayout) logView.findViewById(R.id.log_to_field);
        log_action_bar = (LinearLayout) logView.findViewById(R.id.log_action_bar);
        log_title = (LinearLayout) logView.findViewById(R.id.log_title);
        titleText = (TextView) log_title.findViewById(R.id.log_title_textview);
        due_date_picker = (TactCustomSpinner) logView.findViewById(R.id.log_due_date_spinner);
        subjectSpinner = (TactCustomSpinner) logView.findViewById(R.id.log_subject_spinner);
        log_scrollbar = (TactParallaxScrollView) logView.findViewById(R.id.log_scroll_view_layout);
        floating_log_button = (RelativeLayout) logView.findViewById(R.id.floating_log_button);
        subject_log_call = (TextView) subjectSpinner.getPopupLayout().findViewById(R.id.subject_log_call);
        subject_log_email = (TextView) subjectSpinner.getPopupLayout().findViewById(R.id.subject_log_email);
        subject_log_note = (TextView) subjectSpinner.getPopupLayout().findViewById(R.id.subject_log_note);
        subject_log_log = (TextView) subjectSpinner.getPopupLayout().findViewById(R.id.subject_log_log);
        subject_edit_text = (EditText) logView.findViewById(R.id.subject_edit_text);
        contactSoft = (TextView) logView.findViewById(R.id.contact_title_soft);
        relatedSoft = (TextView) logView.findViewById(R.id.related_to_title_soft);
        descriptionSoft = (TextView) logView.findViewById(R.id.description_title_soft);
        dueDateSoft = (TextView) logView.findViewById(R.id.due_date_title_soft);
        log_description_layout = (LinearLayout) logView.findViewById(R.id.log_description_layout);
        description_edit_text = (EditText) logView.findViewById(R.id.description_edit_text);
        buttonParams = (RelativeLayout.LayoutParams) floating_log_button.getLayoutParams();
        description_text_view = (TextView) logView.findViewById(R.id.description_text_view);
        subject_text_view = (TextView) logView.findViewById(R.id.subject_text_view);
        subject_title_soft = (TextView) logView.findViewById(R.id.subject_title_soft);

        LinearLayout contactSelectField = (LinearLayout) logView.findViewById(R.id.log_contact_layout);
        LinearLayout relatedSelectField = (LinearLayout) logView.findViewById(R.id.log_related_to_layout);
        contactSelectName = (TextView) logView.findViewById(R.id.log_contact_name_text_view);
        relatedSelectName = (TextView) logView.findViewById(R.id.log_relate_to_name_text_view);

        TextView headerTitle = (TextView)logView.findViewById(R.id.log_title_textview);
        TextView headerSubtitle1 = (TextView)logView.findViewById(R.id.log_dest_textview);
        TextView headerSubtitle2 = (TextView)logView.findViewById(R.id.log_subject_textview);

        contactRequiredError = (TextView)logView.findViewById(R.id.log_contact_required_txt);
        relatedRequiredError = (TextView)logView.findViewById(R.id.log_related_to_required_txt);

        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));

        final TextView dateTextView = (TextView)logView.findViewById(R.id.log_date_textview);

        description_edit_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && description_edit_text.getText().toString().isEmpty()) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    descriptionSoft.setVisibility(View.INVISIBLE);
                    description_edit_text.setVisibility(View.GONE);
                    description_text_view.setVisibility(View.VISIBLE);
                }
            }
        });


        subject_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subject_text_view.setVisibility(View.GONE);
                subject_title_soft.setVisibility(View.VISIBLE);
                subject_edit_text.setVisibility(View.VISIBLE);
                subject_edit_text.requestFocus();
                imm.showSoftInput(subject_edit_text, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        subject_edit_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && subject_edit_text.getText().toString().isEmpty()) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    subject_title_soft.setVisibility(View.INVISIBLE);
                    subject_edit_text.setVisibility(View.GONE);
                    subject_text_view.setVisibility(View.VISIBLE);
                }
            }
        });


        log_scrollbar.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView observableScrollView, int x, int y, int oldx, int oldy) {

            }

            @Override
            public void onParallaxScrollChanged(ParallaxScrollView observableScrollView, int x, int y, int oldx, int oldy) {
                if (y>0 && y<headerHeight) {
                    float ratio = 1 - Math.max((float) (headerHeight - y) / headerHeight, 0f);
                    int backgAlpha = Math.round(ratio*100);
                    actionBar.findViewById(R.id.fragment_actionBar_layout).getBackground().setAlpha(backgAlpha);
                    titleActionBar.setAlpha(0);
                    log_icon_field.setAlpha(1-ratio);
                    log_title.setPadding(Math.round(y / 6), 0, 0, 0);
                    titleText.setTextSize(-y * 3);
                } else if (y==0) {
                    actionBar.findViewById(R.id.fragment_actionBar_layout).getBackground().setAlpha(0);
                    titleActionBar.setAlpha(0);
                    log_title.setPadding(0, 0, 0, 0);
                    log_icon_field.setAlpha(1);

                } else if (y>=headerHeight) {
                    actionBar.findViewById(R.id.fragment_actionBar_layout).getBackground().setAlpha(255);
                    titleActionBar.setAlpha(1);
                }
            }
        });



        if (who != null && who instanceof Contact) {
            contactSelectName.setText(((Contact) who).getDisplayName());
            contactSelectName.setTextColor(getResources().getColor(R.color.dark_grey));
            contactSoft.setVisibility(View.VISIBLE);
        } else {
            contactSelectName.setText(getActivity().getString(R.string.contact));
            contactSelectName.setTextColor(getResources().getColor(R.color.mid_grey));
            contactSoft.setVisibility(View.INVISIBLE);
        }
        if (what != null && what instanceof Contact) {
            relatedSelectName.setText(((Contact) who).getDisplayName());
            relatedSelectName.setTextColor(getResources().getColor(R.color.dark_grey));
            relatedSoft.setVisibility(View.VISIBLE);
        } else if (what != null && what instanceof Opportunity) {

        } else {
            relatedSelectName.setText(getActivity().getString(R.string.related_to));
            relatedSelectName.setTextColor(getResources().getColor(R.color.mid_grey));
            relatedSoft.setVisibility(View.INVISIBLE);
        }

        floating_log_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (who == null && what == null) {
                        //At least one of the relations must exist
                        contactRequiredError.setVisibility(View.VISIBLE);
                        relatedRequiredError.setVisibility(View.VISIBLE);
                    } else {
                        ArrayList<String> relatedContacts = new ArrayList<String>();
                        //Create new log
                        FeedItemLog log = new FeedItemLog();
                        //Set data from screen
                        log.setSubject(subject_edit_text.getText().toString());
                        log.setDate(calendar.getTimeInMillis() / 1000);
                        log.setDescription(description_edit_text.getText().toString());
                        if (who != null) {
                            log.setContact(new FeedItemLogRelated("Contact", ((Contact) who).getRecordID()));
                            relatedContacts.add(((Contact) who).getRecordID());
                        }
                        if (what != null) {
                            log.setRelated(new FeedItemLogRelated("Account", ((Contact) what).getRecordID()));
                            relatedContacts.add(((Contact) what).getRecordID());
                        }
                        //generate new ids
                        String remoteId = TactDataSource.getNewRemoteId();
                        log.setServerId(TactDataSource.generateFeedItemId(DatabaseManager.getAccountId("tactapp").toString(), remoteId));
                        log.setRemoteId(remoteId);
                        log.setSourceType("tactapp");
                        log.setSourceId(Integer.parseInt(DatabaseManager.getAccountId("tactapp").toString()));
                        log.setTimestamp(calendar.getTimeInMillis());
                        log.setVersion(0);
                        //set the metadata
                        FeedItemMetadata metadata = new FeedItemMetadata();
                        metadata.setType("Log");
                        ArrayList pushToSources = new ArrayList();
                        try {
                            pushToSources.add(Integer.parseInt(DatabaseManager.getAccountId("salesforce").toString()));
                        } catch (Exception e) {

                        }
                        metadata.setPushToSources(pushToSources);
                        metadata.setOpportunityId(null);
                        metadata.setUnpushFromSources(new ArrayList<Integer>());
                        metadata.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
                        metadata.setPushedSources(new ArrayList<Integer>());
                        metadata.setCreatedAt(Calendar.getInstance().getTimeInMillis());
                        metadata.setIsActivity(true);
                        metadata.setRelatedContacts(relatedContacts);
                        metadata.setRelatedItems(new ArrayList<String>());
                        //Get the relations if tit is not adhoc
                        if (parent != null) {
                            //if it is a event
                            if (parent instanceof FeedItemEvent) {
                                FrozenFeedItem frozenFeedItem = ((FeedItemEvent) parent).getParent();
                                //get if the parent is a master already
                                if (frozenFeedItem.getIsMaster()) {
                                    FeedItem feedItem = (FeedItemEvent)frozenFeedItem.getFeedItem();
                                    metadata.setMasterId(feedItem.getServerId());
                                    feedItem.getMetadata().getRelatedItems().add(((FeedItemEvent) parent).getServerId());
                                    frozenFeedItem.setFeedItem(feedItem, 1);
                                    TactApplication.daoSession.update(frozenFeedItem);
                                    frozenFeedItem.updateRelations();
                                } else {
                                    FrozenFeedItem masterFrozenFeedItem = frozenFeedItem.getMaster();
                                    if (masterFrozenFeedItem == null) {
                                        //*********Create a new master
                                        FeedItemEvent newMaster = ((FeedItemEvent) parent).clone();
                                        masterFrozenFeedItem = new FrozenFeedItem();
                                        String masterRemoteId = TactDataSource.getNewRemoteId();
                                        newMaster.setServerId(TactDataSource.generateFeedItemId(String.valueOf(newMaster.getSourceId()), masterRemoteId));
                                        newMaster.setRemoteId(remoteId);
                                        newMaster.getMetadata().setType("MasterEvent");
                                        newMaster.getMetadata().setOriginalId(((FeedItemEvent) parent).getServerId());
                                        if (newMaster.getMetadata().getRelatedItems() == null) {
                                            newMaster.getMetadata().setRelatedItems(new ArrayList<String>());
                                        }
                                        newMaster.getMetadata().getRelatedItems().add(((FeedItemEvent) parent).getServerId());
                                        newMaster.getMetadata().getRelatedItems().add(log.getServerId());

                                        newMaster.getMetadata().setMasterId(null);
                                        masterFrozenFeedItem.setFeedItem(newMaster, 1);

                                        TactApplication.daoSession.insert(masterFrozenFeedItem);

                                        ((FeedItemEvent) parent).getMetadata().setMasterId(newMaster.getServerId());
//                                        if (((FeedItemEvent) parent).getMetadata().getRelatedItems() == null) {
                                            ((FeedItemEvent) parent).getMetadata().setRelatedItems(new ArrayList<String>());
//                                        }
                                        ((FeedItemEvent) parent).getMetadata().getRelatedItems().add(newMaster.getServerId());
                                        ((FeedItemEvent) parent).getMetadata().setOriginalId(null);
                                        ((FeedItemEvent) parent).getParent().setFeedItem(parent, 1);
                                        TactApplication.daoSession.update(((FeedItemEvent) parent).getParent());
                                        ((FeedItemEvent) parent).getParent().updateRelations();


                                        masterFrozenFeedItem.updateRelations();

                                        metadata.setMasterId(newMaster.getServerId());
                                        metadata.setOriginalId(null);
                                    } else {

                                        FeedItem feedItem = (FeedItem)masterFrozenFeedItem.getFeedItem();
                                        feedItem.getMetadata().getRelatedItems().add(log.getServerId());
                                        masterFrozenFeedItem.setFeedItem(feedItem, 1);
                                        TactApplication.daoSession.update(masterFrozenFeedItem);
                                        metadata.setMasterId(((FeedItem)masterFrozenFeedItem.getFeedItem()).getServerId());
                                    }
                                }
                                metadata.getRelatedItems().add(metadata.getMasterId());
                            }
                        }

                        log.setMetadata(metadata);
                        FrozenFeedItem logFrozenFeedItem = new FrozenFeedItem();
                        logFrozenFeedItem.setFeedItem(log, 1);
                        TactApplication.daoSession.insert(logFrozenFeedItem);
                        logFrozenFeedItem.updateRelations();

                        EventBus.getDefault().post(new EventGoBackHomeActivity());
                    }
                } catch (Exception e){
                    Log.w("SAVING LOG", e.getMessage());
                    TactDialogHandler dialogHandler = new TactDialogHandler(getActivity());
                    dialogHandler.showErrorButtonClose("An error occurs trying to save the log.");
                }
            }
        });

        contactSelectField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LogContactLazyLoadListActivity.class);
                i.putExtra("person", true);
                if (who != null) {
                    if (who instanceof Contact) {
                        i.putExtra("object", (Contact)who);
                    }
                }
                startActivityForResult(i, TactConst.CONTACT_PICK_REQUEST_WHO);
            }
        });

        relatedSelectField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LogContactLazyLoadListActivity.class);
                i.putExtra("person", false);
                if (what != null) {
                    if (what instanceof Contact) {
                        i.putExtra("object", (Contact)what);
                    }
                }
                startActivityForResult(i, TactConst.CONTACT_PICK_REQUEST_WHAT);
            }
        });

        headerTitle.setText(getActivity().getString(R.string.logging_salesforce_title));
        if (isAdHoc()) {
            headerSubtitle1.setVisibility(View.INVISIBLE);
            headerSubtitle2.setVisibility(View.INVISIBLE);
        } else {
            if (parent instanceof FeedItemEvent) {
                if (((FeedItemEvent)parent).getOrganizerToShow() != null) {
                    if (((FeedItemEvent)parent).getOrganizerToShow().getContact() != null) {
                        headerSubtitle1.setText(((FeedItemEvent)parent).getOrganizerToShow().getContact().getDisplayName());
                    } else if (((FeedItemEvent)parent).getOrganizerToShow().getName() != null && !((FeedItemEvent)parent).getOrganizerToShow().getEmail().isEmpty()) {
                        headerSubtitle1.setText(((FeedItemEvent)parent).getOrganizerToShow().getName());
                    } else if (((FeedItemEvent)parent).getOrganizerToShow().getEmail() != null && ! ((FeedItemEvent)parent).getOrganizerToShow().getEmail().isEmpty()) {
                        headerSubtitle1.setText(((FeedItemEvent)parent).getOrganizerToShow().getEmail());
                    } else {
                        headerSubtitle1.setVisibility(View.INVISIBLE);
                    }
                }
                headerSubtitle2.setText(((FeedItemEvent)parent).getSubject());

                subject_title_soft.setVisibility(View.VISIBLE);
                subject_text_view.setVisibility(View.GONE);
                subject_edit_text.setText("Log: " + ((FeedItemEvent)parent).getSubject());
                subject_edit_text.setVisibility(View.VISIBLE);

                if (((FeedItemEvent)parent).getStartAt() != null) {
                    calendar.setTimeInMillis(Utils.covertDateInGMT(((FeedItemEvent)parent).getStartAt()));
                }

            }
        }
        due_date_picker.getPickerOkBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.set(Calendar.YEAR, due_date_picker.getDatePicker().getYear());
                calendar.set(Calendar.MONTH, due_date_picker.getDatePicker().getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, due_date_picker.getDatePicker().getDayOfMonth());
                dateTextView.setText(Utils.getDateStringFormated(calendar, "MMM dd, yyyy"));
                dateTextView.setTextColor(getResources().getColor(R.color.dark_grey));
                dueDateSoft.setVisibility(View.VISIBLE);
                due_date_picker.dialogDismiss();
            }
        });
        due_date_picker.getDatePicker().init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
        dateTextView.setText(Utils.getDateStringFormated(calendar, "MMM dd, yyyy"));
        dateTextView.setTextColor(getResources().getColor(R.color.dark_grey));
        dueDateSoft.setVisibility(View.VISIBLE);


        subjectSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subjectSpinner.clickEvent();
            }
        });

        due_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                due_date_picker.clickEvent();
            }
        });

        subject_log_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubjectTypeSelect(subject_log_call.getText().toString());
                subjectSpinner.getPopupMenu().dismiss();
            }
        });

        subject_log_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubjectTypeSelect(subject_log_email.getText().toString());
                subjectSpinner.getPopupMenu().dismiss();
            }
        });

        subject_log_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubjectTypeSelect(subject_log_note.getText().toString());
                subjectSpinner.getPopupMenu().dismiss();
            }
        });

        subject_log_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubjectTypeSelect(subject_log_log.getText().toString());
                subjectSpinner.getPopupMenu().dismiss();
            }
        });

        log_description_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description_text_view.setVisibility(View.GONE);
                descriptionSoft.setVisibility(View.VISIBLE);
                description_edit_text.setVisibility(View.VISIBLE);
                description_edit_text.requestFocus();
                imm.showSoftInput(description_edit_text, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        return logView;
    }

    public void onSubjectTypeSelect (String text) {
        String subjectText = subject_edit_text.getText().toString();
        String newText;

        if (subjectText.indexOf(":") != -1) {
            int range = subjectText.indexOf(":");
            String stringAux = subjectText.substring(0, range);
            newText = subjectText.replaceAll(stringAux, text);
        } else {
            newText = text + ":" + subjectText;
        }

        subject_edit_text.setText(newText);
    }
}
