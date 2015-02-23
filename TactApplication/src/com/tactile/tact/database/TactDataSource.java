package com.tactile.tact.database;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.database.comparators.FrozenFeedItemComparator;
import com.tactile.tact.database.comparators.TaskComparator;
import com.tactile.tact.database.dao.FrozenFeedItemDao;
import com.tactile.tact.database.entities.FrozenFeedItem;
import com.tactile.tact.database.entities.GlobalSyncStatus;
import com.tactile.tact.database.model.Bounds;
import com.tactile.tact.database.model.ContactField;
import com.tactile.tact.database.model.EmailName;
import com.tactile.tact.database.model.FeedItem;
import com.tactile.tact.database.model.FeedItemDumb;
import com.tactile.tact.database.model.FeedItemEmail;
import com.tactile.tact.database.model.FeedItemEvent;
import com.tactile.tact.database.model.FeedItemLog;
import com.tactile.tact.database.model.FeedItemNote;
import com.tactile.tact.database.model.FeedItemTask;
import com.tactile.tact.database.model.SyncCUDLPacket;
import com.tactile.tact.database.model.SyncCUDLPacketMetadata;
import com.tactile.tact.database.model.SyncContactsPackageMetadata;
import com.tactile.tact.services.network.response.DeviceContactsUploadDataSourceResponse;
import com.tactile.tact.utils.CalendarAPI;
import com.tactile.tact.utils.ContactAPI;
import com.tactile.tact.utils.Log;
import com.tactile.tact.utils.Utils;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by leyan on 8/26/14.
 */
public class TactDataSource {

    public static class TactDataSourceConstants {
        public static Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        public static final Type stringListType = new TypeToken<ArrayList<String>>(){}.getType();
        public static final Type emailNameListType = new TypeToken<Map<String, EmailName>>() {}.getType();
        public static final Type emailAttachmentsType = new TypeToken<ArrayList<FeedItemEmail.FeedItemAttachment>>(){}.getType();
        public static final int MILLISECONDS_IN_A_DAY = 86400000;
    }

    // ******************************** GET ITEMS LIST METHODS ******************************\\
    /**
     * Get emails based on the state requested (Inbox or Sent)
     * @return FrozenFeedItem list
     */
    public static ArrayList<FrozenFeedItem> getAllEmails(TactConst.EmailCurrentState state) {
        if (state == TactConst.EmailCurrentState.InBoxView) {
            return TactDataSource.getInboxEmails();
        } else {
            return TactDataSource.getSentEmails();
        }
    }

    /**
     * Get emails in Inbox
     * @return FrozenFeedItem list
     */
    public static ArrayList<FrozenFeedItem> getInboxEmails() {
        try {
            //This grant that emails that are sended and also in inbox will show in both screens
//            ArrayList<FrozenFeedItem> items = (ArrayList<FrozenFeedItem>)TactApplication.daoSession.getFrozenFeedItemDao().queryBuilder().where(FrozenFeedItemDao.Properties.ZTYPE.eq("OriginalEmail"), FrozenFeedItemDao.Properties.ZLABEL.eq("Inbox")).list();
            ArrayList<FrozenFeedItem> items = (ArrayList<FrozenFeedItem>)TactApplication.daoSession.getFrozenFeedItemDao().queryBuilder().where(FrozenFeedItemDao.Properties.Type.in("OriginalEmail", "Email"), FrozenFeedItemDao.Properties.Label.like("%Inbox%"), FrozenFeedItemDao.Properties.IsDeleted.eq(0)).list();
            if (items == null) {
                items = new ArrayList<FrozenFeedItem>();
            }
            Collections.sort(items, new FrozenFeedItemComparator());
            return items;
        } catch (Exception e) {
            return new ArrayList<FrozenFeedItem>();
        }
    }

    /**
     * Get emails in Sent
     * @return FrozenFeedItem list
     */
    public static ArrayList<FrozenFeedItem> getSentEmails() {
        try {
            //This grant that emails that are sended and also in inbox will show in both screens
//            ArrayList<FrozenFeedItem> items = (ArrayList<FrozenFeedItem>)TactApplication.daoSession.getFrozenFeedItemDao().queryBuilder().where(FrozenFeedItemDao.Properties.ZTYPE.eq("OriginalEmail"), FrozenFeedItemDao.Properties.ZLABEL.eq("Sent")).list();
            ArrayList<FrozenFeedItem> items = (ArrayList<FrozenFeedItem>)TactApplication.daoSession.getFrozenFeedItemDao().queryBuilder().where(FrozenFeedItemDao.Properties.Type.in("OriginalEmail", "Email"), FrozenFeedItemDao.Properties.Label.like("%Sent%"), FrozenFeedItemDao.Properties.IsDeleted.eq(0)).list();
            if (items == null) {
                items = new ArrayList<FrozenFeedItem>();
            }
            Collections.sort(items, new FrozenFeedItemComparator());
            return items;
        } catch (Exception e) {
            return new ArrayList<FrozenFeedItem>();
        }
    }

    /**
     * Get feed item list
     * @return FrozenFeedItem list
     */
    public static ArrayList<FrozenFeedItem> getFeedItemList(String ...typeList) {
        try {
            ArrayList<FrozenFeedItem> items = (ArrayList<FrozenFeedItem>)TactApplication.daoSession.getFrozenFeedItemDao().queryBuilder().where(FrozenFeedItemDao.Properties.Type.in(typeList)).list();
            if (items == null) {
                items = new ArrayList<FrozenFeedItem>();
            }
            Collections.sort(items, new FrozenFeedItemComparator());
            return items;
        } catch (Exception e) {
            return new ArrayList<FrozenFeedItem>();
        }
    }

    public static ArrayList<FrozenFeedItem> getItemsNeedSync() {
        try {
            //Related items need to be in the same package for now get the 100 first
            ArrayList<FrozenFeedItem> itemsNeedSync = (ArrayList<FrozenFeedItem>)TactApplication.daoSession.getFrozenFeedItemDao().queryBuilder().where(FrozenFeedItemDao.Properties.NeedSync.eq(1)).list();
            //if there is no items to sync
            if (itemsNeedSync == null || itemsNeedSync.size() == 0) {
                return new ArrayList<FrozenFeedItem>();
            } else {
                if (itemsNeedSync.size() > 100) {
                    //get the related items that need to sync and put it in the same package
                    ArrayList<FrozenFeedItem> items = new ArrayList<FrozenFeedItem>();
                    for (FrozenFeedItem item : itemsNeedSync) {
                        ArrayList<FrozenFeedItem> relateds = item.getRelatedNeedSyncItems();
                        //remove duplicate items
                        items.removeAll(relateds);
                        //add all new items
                        items.addAll(relateds);
                        //if the package is already 100 items return it
                        if (items.size() >= 100) {
                            break;
                        }
                    }
                    return items;
                } else {
                    return itemsNeedSync;
                }
            }

        } catch(Exception e) {
            return new ArrayList<FrozenFeedItem>();
        }
    }



    public static FrozenFeedItem getItemInDB(FrozenFeedItem item) {
        try {
            return TactApplication.getInstance().daoSession.getFrozenFeedItemDao().queryBuilder().where(FrozenFeedItemDao.Properties.ServerId.eq(item.getServerId())).list().get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<FrozenFeedItem> getLogsOfMaster(FrozenFeedItem master) {
        try {
            ArrayList<FrozenFeedItem> items = new ArrayList<FrozenFeedItem>();
            items = (ArrayList<FrozenFeedItem>) TactApplication.daoSession.getFrozenFeedItemDao()
                    .queryBuilder().where(
                            FrozenFeedItemDao.Properties.Type.in("Log"),
                            FrozenFeedItemDao.Properties.IsDeleted.notEq(1),
                            FrozenFeedItemDao.Properties.MasterId.eq(master.getId())
                    ).list();
            Collections.sort(items, new TaskComparator());
            return items;
        } catch (Exception e) {
            return new ArrayList<FrozenFeedItem>();
        }
    }

    public static ArrayList<FrozenFeedItem> getNotesOfMaster(FrozenFeedItem master) {
        try {
            ArrayList<FrozenFeedItem> items = new ArrayList<FrozenFeedItem>();
            items = (ArrayList<FrozenFeedItem>) TactApplication.daoSession.getFrozenFeedItemDao()
                    .queryBuilder().where(
                            FrozenFeedItemDao.Properties.Type.in("Note"),
                            FrozenFeedItemDao.Properties.IsDeleted.notEq(1),
                            FrozenFeedItemDao.Properties.MasterId.eq(master.getId())
                    ).list();
            Collections.sort(items, new TaskComparator());
            return items;
        } catch (Exception e) {
            return new ArrayList<FrozenFeedItem>();
        }
    }

    public static ArrayList<FrozenFeedItem> getFeedItemLogs() {
        try {
            ArrayList<FrozenFeedItem> items = new ArrayList<FrozenFeedItem>();
            items = (ArrayList<FrozenFeedItem>) TactApplication.daoSession.getFrozenFeedItemDao()
                        .queryBuilder().where(
                                FrozenFeedItemDao.Properties.Type.in("Log"),
                                FrozenFeedItemDao.Properties.IsDeleted.notEq(1)
                        ).list();
            Collections.sort(items, new TaskComparator());
            return items;
        } catch (Exception e) {
            return new ArrayList<FrozenFeedItem>();
        }
    }

    /**
     * Get feed item list
     * @return FrozenFeedItem list
     */
    public static ArrayList<FrozenFeedItem> getFeedItemTactAgendaItem(Long[] date) {
        try {
            ArrayList<FrozenFeedItem> items =
                    (ArrayList<FrozenFeedItem>)TactApplication.daoSession.getFrozenFeedItemDao()
                            .queryBuilder().where(
                                    FrozenFeedItemDao.Properties.Type.in("Event", "Task"),
                                    FrozenFeedItemDao.Properties.IsDeleted.notEq(1),
                                    FrozenFeedItemDao.Properties.Timestamp.between(CalendarAPI.getDateOnly(date[0]),
                                            CalendarAPI.getDateOnly(date[0]) + TactDataSourceConstants.MILLISECONDS_IN_A_DAY - 1)
                            ).list();
            Collections.sort(items, new TaskComparator());
            return items;
        } catch (Exception e) {
            return new ArrayList<FrozenFeedItem>();
        }
    }

    public static FrozenFeedItem getLocalEventFromDatabase(FeedItemEvent feedItemEvent) {
        try {
            ArrayList<FrozenFeedItem> itemsInDB = (ArrayList<FrozenFeedItem>) TactApplication.daoSession.getFrozenFeedItemDao().queryBuilder().where(FrozenFeedItemDao.Properties.Type.eq("Event"), FrozenFeedItemDao.Properties.SourceName.eq("tactapp")).list();
            if (itemsInDB != null && itemsInDB.size() > 0) {

                for (FrozenFeedItem frozenFeedItem : itemsInDB) {
                    FeedItemEvent event = (FeedItemEvent)frozenFeedItem.getFeedItem();
                    if (event.getAndroidCalendarId().equals(feedItemEvent.getAndroidCalendarId()) &&
                            event.getAndroidEventId().equals(feedItemEvent.getAndroidEventId()) &&
                            event.getTimestamp() == feedItemEvent.getTimestamp()) {
                        return frozenFeedItem;
                    }
                }
                return null;
            } else return null;
        } catch (Exception e) {
            return  null;
        }
    }

    /**
     * Get feed item list
     * @return FeedItemEvent list
     */
    public static ArrayList<FeedItemEvent> getLocalFeedItemTactAgendaItem(long minTimestamp, long maxTimestamp, Integer SourceId) {
        try {
            ArrayList<FeedItemEvent> events = new ArrayList<FeedItemEvent>();
            ArrayList<FrozenFeedItem> items =
                    (ArrayList<FrozenFeedItem>)TactApplication.daoSession.getFrozenFeedItemDao()
                            .queryBuilder().where(
                                    FrozenFeedItemDao.Properties.Type.in("Event"),
                                    FrozenFeedItemDao.Properties.Timestamp.between(minTimestamp, maxTimestamp),
                                    FrozenFeedItemDao.Properties.SourceId.eq(SourceId),
                                    FrozenFeedItemDao.Properties.IsDeleted.notEq(1)
                            ).list();
            Collections.sort(items, new TaskComparator());
            for (FrozenFeedItem item: items){
                events.add((FeedItemEvent)item.getFeedItem());
            }
            return events;
        } catch (Exception e) {
            return new ArrayList<FeedItemEvent>();
        }
    }

    public static ArrayList<FeedItemEvent> getRecurrentFeedItemEvent(Long timestamp) {
        try {
            ArrayList<FeedItemEvent> events = new ArrayList<FeedItemEvent>();
            ArrayList<FrozenFeedItem> items =
                    (ArrayList<FrozenFeedItem>)TactApplication.daoSession.getFrozenFeedItemDao()
                            .queryBuilder().where(
                                    FrozenFeedItemDao.Properties.Type.in("Event"),
                                    FrozenFeedItemDao.Properties.Timestamp.eq(timestamp),
                                    FrozenFeedItemDao.Properties.IsDeleted.notEq(1)
                            ).list();
            Collections.sort(items, new TaskComparator());
            for (FrozenFeedItem item: items){
                events.add((FeedItemEvent)item.getFeedItem());
            }
            return events;
        } catch (Exception e) {
            return new ArrayList<FeedItemEvent>();
        }
    }

    // ******************************** PARSERS ******************************\\

    /**
     * Insert the initials activities that came in the initialActivities.json file in the synchronization
     */
    public static void getInitialActivities() {
        try {
            //Get the initial activities inserted
            StringBuilder json = new StringBuilder();
            File initialActivitiesFile = new File(TactApplication.getDatabasePath() + "/initialActivities.json");
            if (initialActivitiesFile != null && initialActivitiesFile.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(initialActivitiesFile));
                String line;
                while ((line = br.readLine()) != null) {
                   json.append(line);
                }
                br.close();
                if (json != null && !json.equals("")) {
                    ArrayList<FrozenFeedItem> items = new ArrayList<FrozenFeedItem>();
                    JSONObject jObject = new JSONObject(json.toString());
                    if (jObject.has("objects")) {
                        JSONArray jItems = jObject.getJSONArray("objects");
                        if (jItems != null  && jItems.length() > 0) {
                            for (int i = 0; i < jItems.length(); i++) {
                                //Parse every ZFROZENITEM
                                FrozenFeedItem item = TactDataSource.parseFrozenFeedItemJson(jItems.get(i).toString());
                                if (item != null) {
                                    items.add(item);
                                }
                            }
                        }
                    }
                    if (items != null && items.size() > 0) {
                        for (FrozenFeedItem item : items) {
                            if (item != null && TactApplication.daoSession != null && !item.itemExist()) {
                                TactApplication.daoSession.insert(item);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.w("INIT_ACT", "Error trying to insert initial activities with message: " + e.getMessage());
        }
    }

    /**
     * Parse the cudl_object pusher response to a list of FrozenFeedItem
     * @param pJson - cudl_object pusher response
     * @return list of FrozenFeedItem
     */
    public static SyncCUDLPacket parseCudlObjects(String pJson) {
        try {
            SyncCUDLPacket syncCUDLPacket = new SyncCUDLPacket();
            //get the json with the objects list and parse it
            if (pJson != null && !pJson.equals("")) {
                JSONObject jObject = new JSONObject(pJson);
                //parse the objects
                if (jObject.has("objects")) {
                    JSONArray jItems = jObject.getJSONArray("objects");
                    if (jItems != null  && jItems.length() > 0) {
                        for (int i = 0; i < jItems.length(); i++) {
                            //Parse every ZFROZENITEM
                            FrozenFeedItem item = TactDataSource.parseFrozenFeedItemJson(jItems.get(i).toString());
                            if (item != null) {
                                syncCUDLPacket.addFrozenFeedItem(item);
                                syncCUDLPacket.checkLatestTimestamp(item.getTimestamp());
                            }
                        }
                    }
                }
            }
            return syncCUDLPacket;
        } catch (Exception e) {
            Log.w("PARSECUDLOBJECT", "Error parsing CUDLOBJECT with exception: " + e.getMessage());
            return null;
        }
    }

    public static SyncCUDLPacketMetadata parseSyncResponseMetadata(String pJson) {
        try {
            if (pJson != null && !pJson.equals("")) {
                JSONObject jObject = new JSONObject(pJson);
                SyncCUDLPacketMetadata syncCUDLPacketMetadata = new SyncCUDLPacketMetadata(jObject.getInt("top_version"),
                        TactDataSourceConstants.gson.fromJson(jObject.getJSONObject("bounds").toString(), Bounds.class),
                        jObject.getString("request_id"),
                        jObject.getInt("total_packet_count"),
                        jObject.getInt("packet_number"));
                return syncCUDLPacketMetadata;
            }
            return null;
        } catch (Exception e) {
            Log.w("SYNC", "Error parsing sync metadata with exception: " + e.getMessage());
            return null;
        }
    }

    public static SyncContactsPackageMetadata parseSyncMetadata(String pJson) {
        try {
            if (pJson != null && !pJson.equals("")) {
                return TactDataSourceConstants.gson.fromJson(pJson, SyncContactsPackageMetadata.class);
            }
            return null;
        } catch (Exception e) {
            Log.w("SYNC", "Error parsing sync metadata with exception: " + e.getMessage());
            return null;
        }
    }

    public static ContactField parseField(String pJson) {
        try {
            if (pJson != null && !pJson.equals("")) {
                ContactField contactField = TactDataSourceConstants.gson.fromJson(pJson, ContactField.class);
                contactField.matchFields(contactField.getServerName());
                return contactField;
            }
            return null;
        } catch (Exception e) {
            Log.w("SYNC", "Error parsing field with exception: " + e.getMessage());
            return null;
        }
    }

    public static Map<String, ArrayList<ContactField>> parseContactSync(String pJson, Map<String, ArrayList<ContactField>> fields) {
        try {
            if (pJson != null && !pJson.equals("")) {
                JSONObject jsonObject = new JSONObject(pJson);
                if (jsonObject.has("fields")) {
                    return TactDataSource.parseFields(jsonObject.getJSONArray("fields").toString(), fields);
                }
            }
            return null;
        } catch(Exception e) {
            return null;
        }
    }

    public static Map<String, ArrayList<ContactField>> parseFields(String pJson, Map<String, ArrayList<ContactField>> fields) {
        try {
            if (pJson != null && !pJson.equals("")) {
                JSONArray jFields = new JSONArray(pJson);
                if (jFields != null && jFields.length() > 0) {
                    for (int i = 0; i < jFields.length(); i++) {
                        ContactField contactField = TactDataSource.parseField(jFields.get(i).toString());
                        if (contactField != null) {
                            if (fields.containsKey(contactField.getRecordId())) {
                                fields.get(contactField.getRecordId()).add(contactField);
                            } else {
                                ArrayList<ContactField> newContactFieldsArray = new ArrayList<>();
                                newContactFieldsArray.add(contactField);
                                fields.put(contactField.getRecordId(), newContactFieldsArray);
                            }
                        }
                    }
                }
                return fields;
            }
            return null;
        } catch (Exception e) {
            Log.w("SYNC", "Error parsing fields with exception: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parse a FrozenFeedItem object from a JSOn string representation
     * @param pJson - JSON string representation
     * @return - FrozenFeedItem object
     */
    public static FrozenFeedItem parseFrozenFeedItemJson(String pJson) {
        try {
            FrozenFeedItem frozenFeedItem = new FrozenFeedItem();
            FeedItem feedItem = new FeedItem();
            if (pJson != null && !pJson.equals("")) {
                feedItem = TactDataSourceConstants.gson.fromJson(pJson, FeedItem.class);
                JSONObject jObject = new JSONObject(pJson);
                if (jObject.has("data")) {
                    if (feedItem.getMetadata() != null && feedItem.getMetadata().getType() != null) {
                        if (feedItem.getMetadata().getType().contains("Event")) {
                            frozenFeedItem.setFeedItem(TactDataSource.parseFeedItemEventJson(jObject.getJSONObject("data"), feedItem), 0);
                        } else if (feedItem.getMetadata().getType().contains("Log")) {
                            frozenFeedItem.setFeedItem(TactDataSource.parseFeedItemLogJson(jObject.getJSONObject("data"), feedItem), 0);
                        }  else if (feedItem.getMetadata().getType().contains("Task")) {
                            frozenFeedItem.setFeedItem(TactDataSource.parseFeedItemTaskJson(jObject.getJSONObject("data"), feedItem), 0);
                        }  else if (feedItem.getMetadata().getType().contains("Note")) {
                            frozenFeedItem.setFeedItem(TactDataSource.parseFeedItemNoteJson(jObject.getJSONObject("data"), feedItem), 0);
                        } else {
                            frozenFeedItem.setFeedItem(TactDataSource.parseFeedItemTaskJson(jObject.getJSONObject("data"), feedItem), 0);
                        };
                    }
                }
                return frozenFeedItem;
            } else return null;
        } catch (Exception e) {
            Log.w("PARSEZFROZENITEM", "Error parsing ZFROZENITEM with exception: " + e.getMessage() + " and json is: " + pJson);
            return null;
        }
    }

    /**
     * Parse a FeedItemEmail object from a json string
     * @param pJson - JSON string representation
     * @return - FeedItemEmail object
     */
    public static FeedItemEmail parseFeedItemEmailJson(JSONObject pJson, FeedItem feedItem) {
        try {
            FeedItemEmail feedItemEmail = TactDataSourceConstants.gson.fromJson(pJson.toString(), FeedItemEmail.class);
            if (pJson.has("email_labels")) {
                feedItemEmail.setEmailLabels(
                        (ArrayList<String>) TactDataSourceConstants.gson.fromJson(pJson.getString("email_labels"),
                                TactDataSourceConstants.stringListType)
                );
            }
            if (pJson.has("flags")) {
                feedItemEmail.setFlags(
                        (ArrayList<String>) TactDataSourceConstants.gson.fromJson(pJson.getString("flags"),
                                TactDataSourceConstants.stringListType)
                );
            }
            if (pJson.has("email_names")) {
                feedItemEmail.setEmailNames(TactDataSource.getEmailNames(pJson.getJSONObject("email_names")));
            }
            if (pJson.has("attachments")) {
                feedItemEmail.setAttacchements((ArrayList<FeedItemEmail.FeedItemAttachment>)TactDataSourceConstants.gson.fromJson(pJson.getString("attachments"),
                        TactDataSourceConstants.emailAttachmentsType));
            }

            feedItemEmail.setValues(feedItem);

            return feedItemEmail;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<EmailName> getEmailNames(JSONObject jsonObject) {
        Map<String, EmailName> emailNamesMap = TactDataSourceConstants.gson.fromJson(jsonObject.toString(), TactDataSourceConstants.emailNameListType);
        ArrayList<EmailName> emailListName = new ArrayList<EmailName>();
        for (Map.Entry<String, EmailName> emailData: emailNamesMap.entrySet()) {
            emailListName.add(emailData.getValue());
        }
        return  emailListName;
    }

//    public static JSONObject getEmailsNamesJson(ArrayList<EmailName> emailNames) {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            if (emailNames != null && emailNames.size() > 0) {
//                Gson gson = new Gson();
//                for (EmailName emailName : emailNames) {
//                    jsonObject.put(emailName.getEmail(), new JSONObject(gson.toJson(emailName)));
//                }
//            }
//        } catch (Exception e) {
//            //HANDLE
//        }
//        return jsonObject;
//
//    }
//
    public static FeedItemEvent parseFeedItemEventJson(JSONObject pJson, FeedItem feedItem) {
        try {
            FeedItemEvent feedItemEvent = TactDataSourceConstants.gson.fromJson(pJson.toString(), FeedItemEvent.class);
            feedItemEvent.setValues(feedItem);
            return feedItemEvent;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parse Log item
     * @param pJson
     * @return
     */
    public static FeedItemLog parseFeedItemLogJson(JSONObject pJson, FeedItem feedItem) {
        try {
            FeedItemLog feedItemLog = TactDataSourceConstants.gson.fromJson(pJson.toString(), FeedItemLog.class);
            feedItemLog.setValues(feedItem);
            return feedItemLog;
        } catch (Exception e) {
            return null;
        }
    }

    public static FeedItemTask parseFeedItemTaskJson(JSONObject pJson, FeedItem feedItem) {
        try {
            FeedItemTask feedItemTask = TactDataSourceConstants.gson.fromJson(pJson.toString(), FeedItemTask.class);
            feedItemTask.setValues(feedItem);
            return feedItemTask;
        } catch (Exception e) {
            return null;
        }
    }

    public static FeedItemNote parseFeedItemNoteJson(JSONObject pJson, FeedItem feedItem) {
        try {
            FeedItemNote feedItemNote = TactDataSourceConstants.gson.fromJson(pJson.toString(), FeedItemNote.class);
            feedItemNote.setValues(feedItem);
            return feedItemNote;
        } catch (Exception e) {
            return null;
        }
    }

    public static FeedItemDumb parseFeedItemDumbJson(JSONObject pJson, FeedItem feedItem) {
        try {
            FeedItemDumb feedItemDumb = TactDataSourceConstants.gson.fromJson(pJson.toString(), FeedItemDumb.class);
            feedItemDumb.setValues(feedItem);
            return feedItemDumb;
        } catch (Exception e) {
            return null;
        }
    }

    // ******************************* UTILS ****************************\\

    public static JSONObject getHotSyncRequest() {
        try {
            JSONObject jObject = new JSONObject();

            GlobalSyncStatus syncStatus = TactDataSource.getGlobalSyncStatus();
            if (syncStatus != null) {
                jObject.put("latest", syncStatus.getTimestamp());
                jObject.put("from_version", syncStatus.getVersion());
            } else {
                jObject.put("latest", 0);
                jObject.put("from_version", 0);
            }
            return jObject;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * Return the current sync status of app, like a row in
     * @return
     */
    public static GlobalSyncStatus getGlobalSyncStatus() {
        List<GlobalSyncStatus> syncStatus = TactApplication.daoSession.getGlobalSyncStatusDao().queryBuilder().limit(1).list();
        if (syncStatus != null && syncStatus.size() > 0) {
            return syncStatus.get(0);
        } else {
            return null;
        }
    }

    /**
     * Get the request for the sync web service call
     * @return JSON object with the request
     */
    public static JSONObject getSyncRequest() {
        try {
            HashMap<String, Integer> syncRevision = DatabaseManager.getSyncRevision();
            JSONObject jObject = new JSONObject();
            jObject.put(TactConst.SYNC_OPPORTUNITIES_REVISION, syncRevision.get(TactConst.SYNC_OPPORTUNITIES_REVISION));
            jObject.put(TactConst.SYNC_REVISION, syncRevision.get(TactConst.SYNC_REVISION));
            return jObject;
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONObject getFeedItemsSyncRequest(ArrayList<FrozenFeedItem> items) {
        try {
            JSONObject jObject = TactDataSource.getHotSyncRequest();
            if (items != null && items.size() > 0) {
                jObject.put("objects", TactDataSource.getObjectsItems(items));
            } else {
                jObject.put("objects", new JSONArray());
            }
            return jObject;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the JSON request for the temp login
     * @return - JSONObject
     * @throws Exception
     */
    public static JSONObject getTempLoginRequest() throws  Exception {
        try {
            String tempUsername    = UUID.randomUUID().toString() + "@onboarding.tactapp.com";
            String tempPassword     = UUID.randomUUID().toString();
            TactSharedPrefController.storeTempCredentials(tempUsername, tempPassword);
            JSONObject jsonTempLoginRequest = new JSONObject();
            jsonTempLoginRequest.put("email", tempUsername);
            jsonTempLoginRequest.put("password", tempPassword);
            return jsonTempLoginRequest;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Get the json request for the reset password web service
     * @param email
     * @return
     * @throws Exception
     */
    public static JSONObject getResetPasswordRequest(String email) throws Exception {
        try {
            JSONObject jsonResetPasswordRequest = new JSONObject();
            jsonResetPasswordRequest.put("email", email);
            return jsonResetPasswordRequest;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Get the JSON for the synchronize/FeedItems request
     * @param items - Items to upload
     * @return - Json for request
     * @throws Exception
     */
    public static JSONArray getObjectsItems(ArrayList<FrozenFeedItem> items) throws Exception{
        try {
            JSONArray objects = new JSONArray();
            for (FrozenFeedItem item : items) {
                String json = ((FeedItem)item.getFeedItem()).toJson();
                if (json != null && !json.isEmpty()) {
                    objects.put(new JSONObject(json));
                }
            }
            return objects;
        } catch (Exception e) {
            Log.w("OBJITEMS", "There was an error getting objects items with message: " + e.getMessage());
            throw e;
        }
    }

    public static JSONObject getAddSourceRequest(String source, Object ...args){
        JSONObject jsonObject = new JSONObject();
        try {
            if (source.equals("salesforce")) {
                jsonObject.put("_type",         "salesforce");
                jsonObject.put("id",            (String)args[0]);
                jsonObject.put("access_token",  (String)args[1]);
                jsonObject.put("refresh_token", (String)args[2]);
                jsonObject.put("instance_url",  (String)args[3]);
            }
            else if (source.equals("exchange")){
                jsonObject.put("_type",             "exchange");
                jsonObject.put("domain",            (String)args[0]);
                jsonObject.put("hostname",          (String)args[1]);
                jsonObject.put("email",             (String)args[2]);
                jsonObject.put("username",          (String)args[3]);
                jsonObject.put("password",          (String)args[4]);
                jsonObject.put("skip_ssl_verify",   ((Boolean)args[5]).booleanValue());
            }
        }
        catch (Exception e){
            return null;
        }
        Log.w("add source", source + ": " + jsonObject.toString());
        return jsonObject;
    }

    public static JSONObject getDeviceInformationRequest(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", Build.MANUFACTURER + Build.PRODUCT);
            jsonObject.put("uuid", Build.ID);
            jsonObject.put("_type", "device");
            jsonObject.put("model", Build.MODEL);
        }
        catch (Exception e){
            return null;
        }
        return  jsonObject;
    }

    public static MultipartEntity getContactsUploadDetailsAmazonRequest(String files_with_path, DeviceContactsUploadDataSourceResponse data){
        final File file = new File(files_with_path);
        file.setReadable(true, false);
        file.setWritable(true, false);
        file.setExecutable(true, false);

        Utils.Log("File exists " + file.exists());
        FileBody fb = new FileBody(file,"contacts.zip","application/zip", null);

        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        try {

            entity.addPart("AWSAccessKeyId", new StringBody(data.getAccessKeyId()));
            entity.addPart("success_action_status", new StringBody(data.getSuccessActionStatus()));
            entity.addPart("signature", new StringBody(data.getSignature()));
            entity.addPart("key", new StringBody(data.getKey()));
            entity.addPart("policy", new StringBody(data.getPolicy()));
            entity.addPart("Secure", new StringBody(data.getSecure()));
            if(data.getSecurityToken() != null && !data.getSecurityToken().isEmpty())
                entity.addPart("x-amz-security-token",   new StringBody(data.getSecurityToken()));
            entity.addPart("file",                   fb);
        }
        catch (Exception e){
            Utils.Log("Exception in callContactsUploadDetailsAmazon - Getting multipart");
        }
        return entity;
    }

    public static JSONObject getUpdateRegistration(String user, String pass){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", user);
            if (pass != null)
                jsonObject.put("password", pass);
        }
        catch (Exception e){}
        return jsonObject;
    }

    /**
     * Get all local contacts info relevant for tact databases
     * @return a json with the local contacts info
     */
    public static JSONArray getContactsJson(Context context, ContentResolver resolver) {
        JSONArray contacts_json = new JSONArray();
        for (String contact_id : ContactAPI.getContactsIds(resolver)){
            try {
                ContactAPI.ContactInfo contact = ContactAPI.getContactInfo(context, resolver, contact_id);

                JSONObject type_person_json = new JSONObject();
                type_person_json.put("type", "Person");

                JSONObject contact_json         = new JSONObject();
                JSONObject contact_data_json    = new JSONObject();

                //put the metadata
                contact_json.put("metadata", type_person_json);
                //put the remote id
                contact_json.put("remote_id", contact_id);

                contact_data_json.put("first_name", contact.getFirstName());

                contact_data_json.put("last_name", contact.getLastName());

                contact_data_json.put("business_address_city", contact.getBusinessCity());

                contact_data_json.put("business_address_state", contact.getBusinessState());

                contact_data_json.put("business_address_country", contact.getBusinessCountry());

                contact_data_json.put("business_address_postal_code", contact.getBusinessPpostalCode());

                //business_address_street
                String business_address_street  = "";
                String address              = contact.getBusinessAddress();
                if (address != null) {
                    business_address_street = address;
                }
                String street               = contact.getBusinessStreet();
                if (street != null) {
                    if (business_address_street.isEmpty()) {
                        business_address_street = street;
                    } else {
                        business_address_street += "\n" + street;
                    }
                }
                if (business_address_street != null) {
                    contact_data_json.put("business_address_street", business_address_street);
                }

                contact_data_json.put("company_name", contact.getCompanyName());

                contact_data_json.put("job_title", contact.getJobTitle());

                contact_data_json.put("department", contact.getDepartment());

                contact_data_json.put("website", contact.getWebsite());

                contact_data_json.put("notes", contact.getNotes());

                contact_data_json.put("personal_email", contact.getPersonalEmail());

                contact_data_json.put("business_email", contact.getBusinessEmail());

                contact_data_json.put("other_email", contact.getOtherEmail());

                contact_data_json.put("home_phone", contact.getHomePhone());

                contact_data_json.put("business_phone", contact.getBusinessPhone());

                contact_data_json.put("mobile_phone", contact.getMobilePhone());


                //tct_display_name && tct_full_name && tct_sort && tct_section
                if (contact_data_json.has("first_name") && contact_data_json.has("last_name")) {
                    String first_name   = (String) contact_data_json.get("first_name");
                    String last_name    = (String) contact_data_json.get("last_name");
                    contact_data_json.put("tct_full_name", first_name + " " + last_name);
                    contact_data_json.put("tct_display_name", contact_data_json.get("tct_full_name"));
                    contact_data_json.put("tct_section", last_name.substring(0, 1));
                    String sort         = last_name.substring(0, 1) + last_name + first_name;
                    contact_data_json.put("tct_sort", sort.toUpperCase());
                }
                else {
                    continue;
                }

                //put the data
                contact_json.put("data", contact_data_json);
                if (contact_data_json.length() > 0) {
                    //add to the result only if data has any type of data
                    contacts_json.put(contact_json);
                }

            }
            catch (Exception e){
                Utils.Log("Exception in getting data from local contact: " + contact_id);
                continue;
            }
        }
        return contacts_json;
    }

    public static String getNewRemoteId() {
        return UUID.randomUUID().toString();
    }

    public static String generateFeedItemId(String sourceId, String remoteId) {
        return Base64.encodeToString((sourceId + ":" + remoteId).getBytes(), Base64.NO_WRAP);
    }

    public static JSONArray getCalendars(String sourceName, Integer sourceId, Context ctx, String[] calendars){
        ArrayList<FeedItemEvent> events = CalendarAPI.getCalendarEventList(sourceName, sourceId, ctx, CalendarAPI.QueryFilter.ByCalendarId, calendars);
        JSONArray calendarsJson = new JSONArray();
        for (FeedItemEvent feedItem: events){
            try {
                calendarsJson.put(new JSONObject(feedItem.toJSON()));
            }
            catch (Exception e){ Utils.Log("Exception in getCalendars: " + e.getMessage());}
        }
        return calendarsJson;
    }

    public static void clearAllDatabaseData() {
        TactApplication.daoSession.getRelatedFeedItemDao().deleteAll();
        TactApplication.daoSession.getOpportunityFIDao().deleteAll();
        TactApplication.daoSession.getGlobalSyncStatusDao().deleteAll();
        TactApplication.daoSession.getContactFIDao().deleteAll();
        TactApplication.daoSession.getContactFeedItemDao().deleteAll();
        TactApplication.daoSession.getFrozenFeedItemDao().deleteAll();
    }

}
