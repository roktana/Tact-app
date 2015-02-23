package com.tactile.tact.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.tactile.tact.R;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.entities.Contact;
import com.tactile.tact.database.entities.ContactDetail;
import com.tactile.tact.database.model.FeedItemLog;
import com.tactile.tact.database.model.FragmentMoveHandler;
import com.tactile.tact.services.events.EventGoBackHomeActivity;
import com.tactile.tact.services.events.EventHideActivityActionBar;
import com.tactile.tact.services.events.EventMoveToFragment;
import com.tactile.tact.services.events.EventOptionButtonPressed;
import com.tactile.tact.utils.ContactAPI;
import com.tactile.tact.utils.ObservableScrollView;
import com.tactile.tact.utils.TactParallaxScrollView;
import com.tactile.tact.utils.interfaces.ScrollViewListener;
import com.tactile.tact.views.ProfileIconView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * Created by ismael on 12/5/14.
 */
public class FragmentContactDetails extends FragmentTactBase {

    //    private RelativeLayout simple_floating_button;
    private TactParallaxScrollView contactDetailsScrollView;
    private ProfileIconView contact_details_header_icon;
    private TextView contact_details_header_name;
    private TextView titleActionBar;
    private LinearLayout actionBar;
    private Contact contact;

    private static enum Icon {
        PHONES,
        EMAILS,
        TITLE,
        ADDRESS,
        NOTES,
        CUSTOM_FIELDS
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            contact = (Contact)bundle.getSerializable("contact");
            DatabaseManager.setVisitedContact(contact);
        }
        else {
            contact = null;
        }

        // The height of your fully expanded header view (same than in the xml layout)
        final int headerHeight = getResources().getDimensionPixelSize(R.dimen.standard_action_bar_3x);

        View contactDetailsView = inflater.inflate(R.layout.contact_details_layout, container, false);
        EventBus.getDefault().post(new EventHideActivityActionBar());
        actionBar = (LinearLayout) contactDetailsView.findViewById(R.id.contact_details_action_bar);
        actionBar.findViewById(R.id.fragment_actionBar_layout).setBackgroundColor(getResources().getColor(R.color.clear_blue_gray));
        ((ImageView)actionBar.findViewById(R.id.fragment_menu_button)).setImageDrawable(getResources().getDrawable(R.drawable.back_arrow_white));
        ((ImageView)actionBar.findViewById(R.id.fragment_option_menu_btn)).setImageDrawable(getResources().getDrawable(R.drawable.settings_white));
        actionBar.findViewById(R.id.fragment_actionBar_layout).getBackground().setAlpha(0);

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

        contactDetailsScrollView = (TactParallaxScrollView) contactDetailsView.findViewById(R.id.contact_details_scroll_view);
        titleActionBar = (TextView) actionBar.findViewById(R.id.fragment_actionBar_title_text);
        titleActionBar.setAlpha(0);
        titleActionBar.setTextColor(getResources().getColor(R.color.white));
        contact_details_header_icon = (ProfileIconView) contactDetailsView.findViewById(R.id.contact_details_header_icon);
        contact_details_header_name = (TextView) contactDetailsView.findViewById(R.id.contact_details_header_name);
        titleActionBar.setText(contact.getDisplayName());
//        simple_floating_button = (RelativeLayout) contactDetailsView.findViewById(R.id.floating_log_button);
//        simple_floating_button.setBackground(getResources().getDrawable(R.drawable.fab_plus));

        contactDetailsScrollView.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView observableScrollView, int x, int y, int oldx, int oldy) {

            }

            @Override
            public void onParallaxScrollChanged(ParallaxScrollView observableScrollView, int x, int y, int oldx, int oldy) {
                if (y>0 && y<headerHeight) {
                    float ratio = 1 - Math.max((float) (headerHeight - y) / headerHeight, 0f);
                    int backgAlpha = Math.round(ratio*100);
                    actionBar.findViewById(R.id.fragment_actionBar_layout).getBackground().setAlpha(backgAlpha);
                    titleActionBar.setAlpha(ratio);
                    contact_details_header_name.setAlpha(1-ratio);
                    contact_details_header_icon.setAlpha(1-ratio);
                } else if (y==0) {
                    actionBar.findViewById(R.id.fragment_actionBar_layout).getBackground().setAlpha(0);
                    titleActionBar.setAlpha(0);
                    contact_details_header_name.setAlpha(1);
                    contact_details_header_icon.setAlpha(1);
                } else if (y>=headerHeight) {
                    actionBar.findViewById(R.id.fragment_actionBar_layout).getBackground().setAlpha(255);
                    titleActionBar.setAlpha(1);
                }
            }
        });

        setViewElements(contactDetailsView, inflater);
        return contactDetailsView;
    }

    private void setViewElements(View view, LayoutInflater inflater){
        TextView name = (TextView) view.findViewById(R.id.contact_details_header_name);
        ProfileIconView image = (ProfileIconView) view.findViewById(R.id.contact_details_header_icon);

        if (contact != null) {
            name.setText(contact.getDisplayName());
            if (contact.isLocal() && contact.getContactDetails().size() > 0){
                try {
                    Bitmap bitmap = ContactAPI.getContactImage(Integer.parseInt(contact.getContactDetails().get(0).getRemoteId()));
                    image.loadImage(bitmap, contact.getInitials());
                }
                catch (Exception e){
                    Log.w("LOAD IMAGE", e.getMessage());
                }
            }
            else {
                if (contact.getRealPhotoUrl() != null && contact.getRealPhotoUrl().startsWith("http")) {
                    image.loadImage(contact.getRealPhotoUrl(), contact.getInitials(), contact.isCompany());
                } else {
                    image.loadImage(contact.getPhotoUrl(), contact.getInitials(), contact.isCompany());
                }
            }
            LinearLayout listPhones = (LinearLayout) view.findViewById(R.id.contact_details_total_fields_container);
            listPhones.removeAllViews();

            if (contact.getContactDetails() != null) {

                ArrayList<Contact> contacts = contact.getChilds();
                if (contacts.size() == 0){
                    contacts = new ArrayList<>();
                    contacts.add(contact);
                }

                //PHONES
                Map<String, ArrayList<String>> phones = new HashMap<>();
                for (Contact contact: contacts) {
                    if (contact.getContactDetails() != null) {
                        for (ContactDetail detail : contact.getContactDetails()) {
                            if (detail.getHomePhone() != null && !detail.getHomePhone().isEmpty()) {
                                addValue(phones, "HOME", detail.getHomePhone());
                            }
                            if (detail.getBusinessPhone() != null && !detail.getBusinessPhone().isEmpty()) {
                                addValue(phones, "WORK", detail.getBusinessPhone());
                            }
                            if (detail.getMobilePhone() != null && !detail.getMobilePhone().isEmpty()) {
                                addValue(phones, "MOBILE", detail.getMobilePhone());
                            }
                            if (detail.getBusinessFax() != null && !detail.getBusinessFax().isEmpty()) {
                                addValue(phones, "FAX", detail.getBusinessFax());
                            }
                        }
                    }
                }
                if (phones.size() > 0) {
                    setMultipleRows(inflater, listPhones, phones.entrySet(), Icon.PHONES);
                }

                //Emails
                Map<String, ArrayList<String>> emails = new HashMap<>();
                for (Contact contact: contacts) {
                    if (contact.getContactDetails() != null) {
                        for (ContactDetail detail : contact.getContactDetails()) {
                            if (detail.getPersonalEmail() != null && !detail.getPersonalEmail().isEmpty()) {
                                addValue(emails, "PERSONAL", detail.getPersonalEmail());
                            }
                            if (detail.getOtherEmail() != null && !detail.getOtherEmail().isEmpty()) {
                                addValue(emails, "OTHER", detail.getOtherEmail());
                            }
                            if (detail.getBusinessMail() != null && !detail.getBusinessMail().isEmpty()) {
                                addValue(emails, "WORK", detail.getBusinessMail());
                            }
                        }
                    }
                }
                if (emails.size() > 0) {
                    setMultipleRows(inflater, listPhones, emails.entrySet(), Icon.EMAILS);
                }

                //job title
                for (Contact contact: contacts) {
                    if (contact.getJobTitle() != null && !contact.getJobTitle().isEmpty()) {
                        setSingleRow(inflater, listPhones, "TITLE", contact.getJobTitle(), Icon.TITLE);
                    }
                }

                //address and notes
                for (Contact contact: contacts) {
                    if (contact.getContactDetails() != null && contact.getContactDetails().size() > 0) {
                        String address = "";
                        ContactDetail detail = contact.getContactDetails().get(0); //get the first because we don't wont to merge different addresses
                        if (detail.getBusinessStreet() != null && !detail.getBusinessStreet().isEmpty()) {
                            address += detail.getBusinessStreet() + "\n";
                        }
                        if (detail.getBusinessCity() != null && !detail.getBusinessCity().isEmpty() &&
                                detail.getBusinessState() != null && !detail.getBusinessState().isEmpty()) {
                            address += detail.getBusinessCity() + ", " + detail.getBusinessState() + "\n";
                        } else if (detail.getBusinessCity() != null && !detail.getBusinessCity().isEmpty()) {
                            address += detail.getBusinessCity() + "\n";
                        } else if (detail.getBusinessState() != null && !detail.getBusinessState().isEmpty()) {
                            address += detail.getBusinessState() + "\n";
                        }
                        if (detail.getBusinessPostalCode() != null && !detail.getBusinessPostalCode().isEmpty() &&
                                detail.getBusinessCountry() != null && !detail.getBusinessCountry().isEmpty()) {
                            address += detail.getBusinessPostalCode() + " " + detail.getBusinessCountry();
                        } else if (detail.getBusinessPostalCode() != null && !detail.getBusinessPostalCode().isEmpty()) {
                            address += detail.getBusinessPostalCode();
                        } else if (detail.getBusinessCountry() != null && !detail.getBusinessCountry().isEmpty()) {
                            address += detail.getBusinessCountry();
                        }
                        if (!address.isEmpty()) {
                            if (address.endsWith("\n")) {
                                int ind = address.lastIndexOf("\n");
                                address = new StringBuilder(address).replace(ind, ind + 1, "").toString();
                            }
                            setSingleRow(inflater, listPhones, "ADDRESS", address, Icon.ADDRESS);
                        }
                    }
                }

                for (Contact contact: contacts){
                    if (contact.getContactDetails() != null) {
                        for (ContactDetail contactDetail : contact.getContactDetails()) {
                            if (contactDetail.getNotes() != null && !contactDetail.getNotes().isEmpty()) {
                                setSingleRow(inflater, listPhones, "NOTES", contactDetail.getNotes(), Icon.NOTES);
                                break;
                            }
                        }
                    }
                }

                //Custom Fields
                Map<String, ArrayList<String>> customFields = new HashMap<>();
                for (Contact contact: contacts) {
                    //Default custom fields
                    for(Map.Entry<String, String> field: contact.getCustomFields().entrySet()){
                        addValue(customFields, field.getKey(), field.getValue());
                    }
                    //Particular custom fields
                    for (ContactDetail detail : contact.getContactDetails()) {
                        if (detail.getWebsite() != null && !detail.getWebsite().isEmpty()) {
                            addValue(customFields, "website", detail.getWebsite());
                        }
                        if (detail.getIndustry() != null && !detail.getIndustry().isEmpty()) {
                            addValue(customFields, "industry", detail.getIndustry());
                        }
                        if (detail.getAnnualRevenue() != null && !detail.getAnnualRevenue().isEmpty()) {
                            addValue(customFields, "annual revenue", detail.getAnnualRevenue());
                        }
                        if (detail.getNumberOfEmployees() != null && !detail.getNumberOfEmployees().isEmpty()) {
                            addValue(customFields, "number of employees", detail.getNumberOfEmployees());
                        }
                        if (detail.getTickerSymbol() != null && !detail.getTickerSymbol().isEmpty()) {
                            addValue(customFields, "ticker symbol", detail.getTickerSymbol());
                        }
                    }
                }
                if (customFields.size() > 0) {
                    setMultipleRows(inflater, listPhones, customFields.entrySet(), Icon.CUSTOM_FIELDS);
                }
                ////////////////////////

                //Logs
                boolean addLogDivider = false;
                LinearLayout logLayout = null;
                if (!contacts.contains(contact)){
                    contacts.add(contact);
                }
                for (Contact contactChild: contacts) {
                    ArrayList<FeedItemLog> logs = contactChild.getLogsFromContact();
                    if (logs != null && logs.size() > 0) {
                        View logContainer = inflater.inflate(R.layout.contact_details_fields, null, false);
                        logLayout = (LinearLayout) logContainer.findViewById(R.id.contact_field_container_layout);
                        ImageView icon = (ImageView) logContainer.findViewById(R.id.detail_icon);
                        icon.setImageResource(R.drawable.log_activity_in_details);
                        logLayout.removeAllViews();
                        for (final FeedItemLog log : logs) {
                            View logView = inflater.inflate(R.layout.log_list_item, null, false);
                            TextView log_list_item_title_text_view = (TextView) logView.findViewById(R.id.log_list_item_title_text_view);
                            TextView log_list_item_contact_text_view = (TextView) logView.findViewById(R.id.log_list_item_contact_text_view);
                            ImageView log_list_item_image = (ImageView) logView.findViewById(R.id.log_list_item_image);

                            log_list_item_title_text_view.setText(log.getSubject());
                            if (log.getContact() != null && log.getContact().getIdentifier() != null) {
                                Contact contact = DatabaseManager.getContactByRecordId(log.getContact().getIdentifier());
                                if (contact != null) {
                                    log_list_item_contact_text_view.setText(contact.getDisplayName());
                                }
                            } else if (log.getRelated() != null && log.getRelated().getIdentifier() != null && log.getRelated().getType().equals("Company")) {
                                Contact contact = DatabaseManager.getContactByRecordId(log.getRelated().getIdentifier());
                                if (contact != null) {
                                    log_list_item_contact_text_view.setText(contact.getDisplayName());
                                }
                            }

                            log_list_item_image.setImageDrawable(getResources().getDrawable(R.drawable.logdetail_log));

                            logView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setBackStack(getFragmentMoveHandler());
                                    EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_LOG_DETAIL_TAG, 0, 0, log, 0));
                                }
                            });

                            logLayout.addView(logView);
                            addLogDivider = true;
                        }
                        listPhones.addView(logContainer);
                    }
                }
                if (logLayout != null && addLogDivider){
                    setDivider(inflater, logLayout);
                }

                // Employes (if is a company)
                ArrayList<Contact> companyEmployes = contact.getCompanyEmployes();
                if (companyEmployes.size()>0) {

                    TextView title = new TextView(getActivity().getApplicationContext());
                    title.setTextColor(getResources().getColor(R.color.action_bar_orange));
                    title.setText(getResources().getString(R.string.employes));
                    int padd = getResources().getDimensionPixelOffset(R.dimen.padding_standard);
                    title.setPadding(padd, padd, padd, 0);

                    listPhones.addView(title);

                    for (final Contact c: companyEmployes) {
                        View employed_item = inflater.inflate(R.layout.meeting_invitees_layout, null, false);
                        TextView contact_name = (TextView) employed_item.findViewById(R.id.contact_name_text_view);
                        ProfileIconView profileIconView = (ProfileIconView) employed_item.findViewById(R.id.contact_details_icon_small);
                        TextView company_name_text_view = (TextView) employed_item.findViewById(R.id.company_name_text_view);
                        company_name_text_view.setVisibility(View.GONE);

                        if (c != null){
                            contact_name.setText(c.getDisplayName());
                            profileIconView.loadImage(c.getPhotoUrl(), c.getInitials(), c.isCompany());
                            employed_item.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setBackStack(getFragmentMoveHandler());
                                    EventBus.getDefault().post(new EventMoveToFragment(TactConst.FRAGMENT_CONTACT_DETAIL_TAG, 0, 0, c, 0));
                                }
                            });
                        }
                        else if (c.getFullName() != null) {
                            contact_name.setText(c.getFullName());
                        }
                        listPhones.addView(employed_item);
                    }
                }
                
                
                //Remove the Last Line Divider
                if (listPhones.getChildCount() > 0){
                    try {
                        View parentView = listPhones.getChildAt(listPhones.getChildCount() - 1);
                        LinearLayout layout = (LinearLayout) parentView.findViewById(R.id.contact_field_container_layout);
                        if (layout.getChildCount() > 0) {
                            layout.removeViewAt(layout.getChildCount() - 1);
                        }
                    }
                    catch (Exception e){}
                }
            }
        }
    }

    private void addValue(Map<String, ArrayList<String>> elements, String label, String value){
        if (!elements.containsKey(label)){
            elements.put(label, new ArrayList<String>());
        }
        if (!elements.get(label).contains(value)) {
            elements.get(label).add(value);
        }
    }

    private void setMultipleRows(LayoutInflater inflater, LinearLayout listElements, Set<Map.Entry<String, ArrayList<String>>> fields, Icon type){
        View container = inflater.inflate(R.layout.contact_details_fields, null, false);
        LinearLayout customFieldsLayout = (LinearLayout) container.findViewById(R.id.contact_field_container_layout);
        ImageView icon = (ImageView)container.findViewById(R.id.detail_icon);
        setIcon(icon, type);
        customFieldsLayout.removeAllViews();
        for (final Map.Entry<String, ArrayList<String>> field : fields) {
            for (final String value: field.getValue()) {
                View customFieldView = inflater.inflate(R.layout.contact_details_data_in_field, null, false);
                TextView description = (TextView) customFieldView.findViewById(R.id.phone_number);
                TextView label = (TextView) customFieldView.findViewById(R.id.phone_description);
                try {
                    if (value.contains(".") && (int) Float.parseFloat(value) != Integer.MAX_VALUE) {
                        description.setText(String.valueOf((int) Float.parseFloat(value)));
                    }
                    else {
                        description.setText(value);
                    }
                } catch (Exception e) {
                    description.setText(value);
                }
                if (type == Icon.PHONES) {
                    customFieldView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String uri = "tel:" + value.trim();
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse(uri));
                            startActivity(intent);
                        }
                    });
                }
                label.setText(field.getKey().toUpperCase());
                customFieldsLayout.addView(customFieldView);
            }
        }
        setDivider(inflater, customFieldsLayout);
        listElements.addView(container);
    }

    private void setSingleRow(LayoutInflater inflater, LinearLayout listElements, String title, String description, Icon type){
        View container = inflater.inflate(R.layout.contact_details_fields, null, false);
        LinearLayout rowLayout = (LinearLayout) container.findViewById(R.id.contact_field_container_layout);
        ImageView icon = (ImageView)container.findViewById(R.id.detail_icon);
        setIcon(icon, type);
        rowLayout.removeAllViews();
        View addressView = inflater.inflate(R.layout.contact_details_data_in_field, null, false);
        TextView descriptionText = (TextView) addressView.findViewById(R.id.phone_number);
        TextView titleText = (TextView) addressView.findViewById(R.id.phone_description);
        descriptionText.setText(description);
        titleText.setText(title);
        rowLayout.addView(addressView);
        setDivider(inflater, rowLayout);
        listElements.addView(container);
    }

    private void setIcon(ImageView icon, Icon type){
        switch (type){
            case PHONES:
                icon.setImageResource(R.drawable.phone_in_details);
                break;
            case EMAILS:
                icon.setImageResource(R.drawable.mail_in_details);
                break;
            case TITLE:
                icon.setImageResource(R.drawable.title_in_details);
                break;
            case ADDRESS:
                icon.setImageResource(R.drawable.address_in_details);
                break;
            case NOTES:
                icon.setImageResource(R.drawable.notes_in_details);
                break;
            case CUSTOM_FIELDS:
                icon.setImageResource(R.drawable.custom_fields_in_details);
                break;
        }
    }

    private FragmentMoveHandler getFragmentMoveHandler() {
        FragmentMoveHandler fragmentMoveHandler = new FragmentMoveHandler();
        fragmentMoveHandler.setFragmentTag(TactConst.FRAGMENT_CONTACT_DETAIL_TAG);
        fragmentMoveHandler.setObject(contact);
        return  fragmentMoveHandler;
    }

    private void setDivider(LayoutInflater inflater, LinearLayout layout){
        View divider = inflater.inflate(R.layout.divider_layout, null, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 16;
        params.bottomMargin = 16;
        layout.addView(divider, params);
    }
}
