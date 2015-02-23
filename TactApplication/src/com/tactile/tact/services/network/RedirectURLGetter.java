package com.tactile.tact.services.network;

import android.os.AsyncTask;

import com.tactile.tact.activities.LogContactListActivity;
import com.tactile.tact.fragments.FragmentContacts;
import com.tactile.tact.fragments.FragmentEventDetails;
import com.tactile.tact.fragments.FragmentNoteDetails;
import com.tactile.tact.views.ProfileIconView;


public class RedirectURLGetter extends AsyncTask<String, Void, String> {

    private Object context;
    private Object _response;
    private boolean completed;
    private String recordID;
    private ProfileIconView view;
    private String initials;
    private Boolean isCompany;

    public RedirectURLGetter(Object context, String recordID) {
        this.context = context;
        this.completed = false;
        this.recordID = recordID;
    }

    public RedirectURLGetter(Object context, ProfileIconView view, String initials, Boolean isCompany){
        this.context = context;
        this.completed = false;
        this.view = view;
        this.initials = initials;
        this.isCompany = isCompany;
    }

    @Override
    protected String doInBackground(String... urls) {
        return TactNetworking.getTactImageUrl(urls[0]);
    }

    @Override
    protected void onPostExecute(String response) {
        completed = true;
        _response = response;
        notifyActivityTaskCompleted();
    }

    private void notifyActivityTaskCompleted() {
        if (null != context) {
            if (context instanceof FragmentContacts) {
                ((FragmentContacts)context).notifyURLGetterTaskCompleted((String) _response, recordID);
            }
            else if (context instanceof LogContactListActivity) {
                ((LogContactListActivity)context).notifyURLGetterTaskCompleted((String) _response, recordID);
            }
            else if (context instanceof FragmentEventDetails){
                ((FragmentEventDetails)context).notifyURLGetterTaskCompleted((String) _response, view, initials, isCompany);
            }
            else if (context instanceof ProfileIconView){
                ((ProfileIconView)context).setImageUrl((String) _response);
            }
            else if (context instanceof FragmentNoteDetails){
                ((FragmentNoteDetails)context).notifyURLGetterTaskCompleted((String) _response, view, initials, isCompany);
            }
            //if we need to notify in other context we added here
        }
    }

    public void setActivity(Object context) {
        this.context = context;
        if (completed) {
            notifyActivityTaskCompleted();
        }
    }

    public String getRecordID(){
        return this.recordID;
    }
}