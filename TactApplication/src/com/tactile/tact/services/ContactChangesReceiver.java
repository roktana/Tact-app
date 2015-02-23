package com.tactile.tact.services;

import android.database.ContentObserver;

import com.tactile.tact.utils.Log;
/**
 * Created by sebafonseca on 12/8/14.
 */

public class ContactChangesReceiver extends ContentObserver {
    public final static String TAG = ContactChangesReceiver.class.getSimpleName();

    public ContactChangesReceiver() {
        super(null);
    }

    @Override
    public void onChange(boolean selfChange) {
        Log.i(TAG, "Contacs change");
        super.onChange(selfChange);
        //new AsyncContactChangesReceiver().execute();
    }

    /*public class AsyncContactChangesReceiver extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params){
            try {
                long current_time = Calendar.getInstance().getTimeInMillis();
                long window_time = 1000*15; //15 seconds

                ContentResolver resolver = TactApplication.getInstance().getContentResolver();

                //Get Contacts updated after timestamp
                List<String> updated_contacts = ContactAPI.getContactsIds(resolver, Long.toString(current_time - window_time), Long.toString(current_time));

                //Get all Local Contacts
                List<String> local_contacts = ContactAPI.getContactsIds(resolver);

                //Get Local Synced DB Contacts
                List<String> db_contacts = DatabaseManager.getLocalContacts(resolver);

                Set<String> set_updated_contacts = new HashSet<String>(updated_contacts);
                Set<String> set_local_contacts = new HashSet<String>(local_contacts);
                Set<String> set_db_contacts = new HashSet<String>(db_contacts);

                //Added     ---> (set local contacts) - (set db contacts)
                Set<String> added = Sets.difference(set_local_contacts, set_db_contacts);
                if (added.size() > 0){

                }

                //Updated   ---> (set updated contacts) - (Added), because updated retrieves updated and created
                Set<String> updated = Sets.difference(set_updated_contacts, added);
                if (updated.size() > 0){
                    for (String id: updated){
                        ContactAPI.ContactInfo contact = ContactAPI.getContactInfo(TactApplication.getInstance().getApplicationContext(),
                                TactApplication.getInstance().getContentResolver(), id);
                        if (DatabaseManager.updateContact(contact)){
                            Log.w(TAG, "Contact updated ok: " + contact.getId());
                        }
                        else {
                            Log.w(TAG, "Contact NOT updated: " + contact.getId());
                        }
                    }
                }

                //Deleted   ---> (set db contacts) - (set local contacts)
                Set<String> deleted = Sets.difference(set_db_contacts, set_local_contacts);
                if (deleted.size() > 0){

                }
            }
            catch (Exception e){
                Log.w(TAG, e.getMessage());
            }
            return null;
        }

        private String[] getIds(Set<String> ids){
            String[] elems = new String[ids.size()];
            int i = 0;
            for (String id : ids){
                elems[i] = id;
                i++;
            }
            return elems;
        }
    }*/
}