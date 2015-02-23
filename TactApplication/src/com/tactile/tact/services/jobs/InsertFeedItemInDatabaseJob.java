package com.tactile.tact.services.jobs;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.database.TactDataSource;
import com.tactile.tact.database.entities.FrozenFeedItem;
import com.tactile.tact.database.model.FeedItemEvent;
import com.tactile.tact.utils.CalendarAPI;
import com.tactile.tact.utils.Log;

/**
 * Created by leyan on 1/22/15.
 */
public class InsertFeedItemInDatabaseJob extends Job {

    public static final int PRIORITY = 1;
    private FrozenFeedItem frozenFeedItem;

    public InsertFeedItemInDatabaseJob(FrozenFeedItem frozenFeedItem) {
        // This job should be persisted in case the application exits before job is completed.
        super(new Params(PRIORITY).persist());
        this.frozenFeedItem = frozenFeedItem;
    }

    @Override
    protected void onCancel() {

    }

    @Override
    public void onRun() throws Throwable {
        if (frozenFeedItem != null && TactApplication.daoSession != null) {
            //If is an Event from the device check if already in the database and update it with server data
            if (frozenFeedItem.getType().equals("Event") && frozenFeedItem.getSourceId() == Integer.parseInt(DatabaseManager.getAccountId("tactapp"))) {
                FrozenFeedItem exitingEvent = TactDataSource.getLocalEventFromDatabase((FeedItemEvent)frozenFeedItem.getFeedItem());
                if (exitingEvent != null) {
                    exitingEvent.setServerId(frozenFeedItem.getServerId());
                    exitingEvent.setNeedSync(1);
                    TactApplication.daoSession.update(exitingEvent);
                }
            } else if (!frozenFeedItem.itemExist()) {
                //Id is a new item that does not exist in the database insert it
                frozenFeedItem.setNeedSync(0);
                frozenFeedItem.setId(TactApplication.daoSession.insert(frozenFeedItem));
                frozenFeedItem.updateRelations();
            } else {
                //If the item already exist in database update it
                FrozenFeedItem itemDB = TactDataSource.getItemInDB(frozenFeedItem);
                if (itemDB != null) {
                    frozenFeedItem.setId(itemDB.getId());
                    frozenFeedItem.__setDaoSession(TactApplication.getInstance().daoSession);
                    frozenFeedItem.setNeedSync(0);
                    TactApplication.getInstance().daoSession.update(frozenFeedItem);
                    frozenFeedItem.updateRelations();
                }
            }
//            if (frozenFeedItem.getType().equals("Event") && !dates.contains(CalendarAPI.getDateOnly(item.getTimestamp()))){
//                dates.add(CalendarAPI.getDateOnly(item.getTimestamp()));
//            }
        }
    }

    @Override
    public void onAdded() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
