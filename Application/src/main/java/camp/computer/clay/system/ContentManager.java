package camp.computer.clay.system;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContentManager implements ContentManagerInterface {

    private static String firebaseUri = "https://clay.firebaseio.com/";

    private Firebase rootRef = null;

    private Clay clay;

    public ContentManager(Clay clay) {
        this.clay = clay;

        this.enableFirebase ();
        this.startFirebase();
    }

    private void enableFirebase () {
        Firebase.setAndroidContext(Clay.getContext());
    }

    private void startFirebase () {
        this.rootRef = new Firebase (firebaseUri);
    }

    public Clay getClay () {
        return this.clay;
    }

    public void storeUnit (Unit unit) {
        Firebase unitRef = rootRef.child("units");
        unitRef.push().setValue(unit);
    }

    /**
     * Retrieves the single unit with the specified UUID, if any, from the remote database and
     * updates the locally cached object corresponding to the unit.
     * @param unitUuid
     */
    public void storeOrRestoreUnit(final UUID unitUuid) {

        final String unitUuidString = unitUuid.toString();

        // Create query to database
        Firebase unitsRef = rootRef.child("units");
        Query unitQueryRef = unitsRef.orderByChild ("uuid").equalTo(unitUuidString).limitToFirst(1);

        // Create query response handler. Integrates the received data into the local cache.
        unitQueryRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() == 0) {

                    Log.v("Restore_Unit_DB", "There is no unit with UUID equal to " + unitUuidString + ".");

                    // There was no unit object found in the remote repository with the specified
                    // UUID. Since no such unit object was found, one is created here. The code
                    // below gets the object in the local cache corresponding to the unit with the
                    // specified UUID, then stores the object in the remote repository.
                    if (getClay().hasUnitByUuid(unitUuid)) {
                        Unit unit = getClay().getUnitByUuid(unitUuid);
                        storeUnit(unit);
                        storeOrRestoreTimeline(unit.getTimeline());
                        Log.v("Restore_Unit_DB", "Saved unit to database.");
                    } else {
                        Log.v("Restore_Unit_DB", "No unit was found with the specified UUID in the " +
                                "local cache, so it was not stored in the remote repository.");
                    }

                } else if (dataSnapshot.getChildrenCount() > 0) {

                    // The query returned a unit object with the specified UUID from the remote
                    // repository.
                    // Store unit in the local cache.
                    for (DataSnapshot unitSnapshot : dataSnapshot.getChildren()) {

                        // Create behavior object from database.
                        Unit restoredUnit = unitSnapshot.getValue(Unit.class);
                        Log.v("Restore_Unit_DB", "Restored unit (UUID: " + restoredUnit.getUuid() + ").");

//                        // Add unit to present (i.e., local cache).
//                        getClay().addUnit(restoredUnit);

                        // Update cached unit from database.
                        Unit unit = getClay().getUnitByUuid(unitUuid);
                        if (unit != null) {

                            // TODO: Update the locally cached unit with information from the unit retrieved from the database.

                            // TODO: Restore the unit's timeline (and in turn, the timeline's behaviors)
                            restoreUnitTimeline(unit, restoredUnit.getTimelineUuid());

                            Log.v("Restore_Unit_DB", "Updating unit in local cache.");
                        } else {
                            Log.v("Restore_Unit_DB", "Failed to save unit to database. This entails undefined problems.");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("Restore_Unit_DB", "The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public void storeTimeline (Timeline timeline) {
        Firebase timelinesRef = rootRef.child("timelines");

        Firebase timelineRef = timelinesRef.push().getRef();
        timelineRef.setValue(timeline);

        /*
        // Add event behaviors and behavior states
        Firebase timelineEventsRef = timelineRef.child("events");
        for (Event event : timeline.getEvents()) {
            timelineEventsRef.push().setValue(event);
        }
        */
    }

    // TODO: updateTimeline (Timeline timeline)

    /**
     * Retrieves the timeline with the specified UUID, if any, from the remote database and
     * updates the locally cached object corresponding to the timeline.
     * @param timeline
     */
    public void storeOrRestoreTimeline(final Timeline timeline) {

        Log.v("CM_Log", "storeOrRestoreTimeline");

        final String timelineUuidString = timeline.getUuid().toString();

        // Create query to database
        Firebase timelinesRef = rootRef.child("timelines");
        Query timelineQueryRef = timelinesRef.orderByChild ("uuid").equalTo(timelineUuidString).limitToFirst(1);

        // Create query response handler. Integrates the received data into the local cache.
        timelineQueryRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() == 0) {

                    Log.v("CM_Log", "\tThere is no timeline with UUID equal to " + timelineUuidString + ".");

                    // There was no unit object found in the remote repository with the specified
                    // UUID. Since no such unit object was found, one is created here. The code
                    // below gets the object in the local cache corresponding to the unit with the
                    // specified UUID, then stores the object in the remote repository.
//                    if (getClay().hasUnitByUuid(unitUuid)) {
//                        Unit unit = getClay().getUnitByUuid(unitUuid);
//                        storeUnit(unit);
                    storeTimeline(timeline);
                    Log.v("CM_Log", "\tSaved timeline to database.");
//                    } else {
//                        Log.v("Restore_Unit_DB", "No unit was found with the specified UUID in the " +
//                                "local cache, so it was not stored in the remote repository.");
//                    }

                } else if (dataSnapshot.getChildrenCount() > 0) {

                    // The query returned a unit object with the specified UUID from the remote
                    // repository.
                    // Store unit in the local cache.
                    for (DataSnapshot timelineSnapshot : dataSnapshot.getChildren()) {

                        // Create behavior object from database.
                        Timeline restoredTimeline = timelineSnapshot.getValue(Timeline.class);
                        Log.v("CM_Log", "\tRestored timeline (UUID: " + restoredTimeline.getUuid() + ").");

                        // Update cached unit from database.
                        // TODO: Find the timeline (search through units?) and update it

//                        Unit unit = getClay().getUnitByUuid(unitUuid);
//                        if (unit != null) {
//
//                            // TODO: Update the locally cached unit with information from the unit retrieved from the database.
//
//                            // TODO: Restore the unit's timeline (and in turn, the timeline's behaviors)
//
//                            Log.v("Restore_Unit_DB", "Updating unit in local cache.");
//                        } else {
//                            Log.v("Restore_Unit_DB", "Failed to save unit to database. This entails undefined problems.");
//                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("Restore_Unit_DB", "The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public void restoreUnitTimeline (final Unit unit, UUID timelineUuid) {

        Log.v ("CM_Log", "restoreUnitTimeline");

        Firebase timelineRef = rootRef.child("timelines");
        Query queryRef = timelineRef.orderByChild("uuid").equalTo(timelineUuid.toString()).limitToFirst(1);

        Log.v ("CM_Log", "\tRestoring timeline " + timelineUuid);
        Log.v ("CM_Log", "\t\tfor unit " + unit.getUuid());

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Store behaviors in the local cache.
                for (DataSnapshot timelineSnapshot : dataSnapshot.getChildren()) {
                    // Create behavior object from database.
                    Timeline timeline = timelineSnapshot.getValue(Timeline.class);
                    Log.v("CM_Log", "\tRestored unit timeline (UUID: " + timeline.getUuid() + ")");

                    unit.setTimeline(timeline);

//                    // <HACK>
//                    getClay().getView(0).refreshListViewFromData(unit);
//                    // </HACK>

                    // Get reference to the events on the unit's timeline.
                    // TODO: restoreTimelineBehaviors (or BehaviorEvents)
//                    Firebase eventsRef = timelineSnapshot.getRef();
                    // TODO: Iterate through events and reconstruct objects (if store as separate entity in the data store).

                    Log.v("CM_Log", "\t\tNumber of events: " + timeline.getEvents().size());

                    int i = 0;
                    for (Event event : timeline.getEvents()) {

                        // Reconstruct Clay object model
                        Behavior behavior = getClay().getBehavior(event.getBehaviorUuid().toString());

                        // <HACK>
                        // TODO: Reconstruct behavior state
                        BehaviorState behaviorState = behavior.getState();
                        // </HACK>

                        Log.v("CM_Log", "\t\t" + i + ": behavior: " + event.getBehavior());
                        Log.v("CM_Log", "\t\t" + i + ": behaviorState" + event.getBehaviorState());
                        i++;

                        event.setBehavior(behavior, behaviorState);
                    }

                    // <HACK>
//                    getClay().getView(0).refreshListViewFromData(unit);
                    // </HACK>

                    // <HACK>
                    // Must be called after setting the timeline?
//                    getClay().getUnits().add(unit);
                    // </HACK>

                    getClay().addUnitView(unit);


//                    timeline.setState(behaviorState);
//
//                    Log.v("CM_Log", "\t\tbehavior.getState = " + behavior.getState());
//                    Log.v("CM_Log", "\t\tbehavior.getState.getTag = " + behavior.getState().getTag());
//                    Log.v("CM_Log", "\t\tbehavior.getState.getState = " + behavior.getState().getState());

//                    Log.v("Clay_Behavior_Repo", "Adding behavior " + behavior.getTag() + " (UUID: " + behavior.getUuid() + ")");
//                    getClay ().getBehaviorCacheManager().storeBehavior (behavior);
                }

//                // Add the basic behaviors if they do not exist.
//                getClay().getBehaviorCacheManager().setupRepository();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("CM_Log", "\tThe read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void storeBehavior(Behavior behavior) {

        Firebase behaviorRef = rootRef.child("behaviors");
        behaviorRef.push ().setValue(behavior);

    }

    public void updateBehavior(final Behavior behavior) {
        Log.v("CM_Log", "updateBehavior");

        final String behaviorUuid = behavior.getUuid().toString();

        Firebase behaviorsRef = rootRef.child("behaviors");
        Query behaviorQueryRef = behaviorsRef.orderByChild ("uuid").equalTo(behaviorUuid).limitToFirst(1);

        behaviorQueryRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() == 0) {
                    Log.v("CM_Log", "\tThere is no behavior with UUID equal to " + behaviorUuid + ".");
                } else if (dataSnapshot.getChildrenCount() > 0) {

                    // Store unit in the local cache.
                    for (DataSnapshot behaviorSnapshot : dataSnapshot.getChildren()) {

                        // Update values.
                        // First, get a reference to the behavior's state. Then create a map object
                        // with the updated values. Then commit the update to the database.
                        Firebase ref = behaviorSnapshot.getRef();
                        //Firebase ref = behaviorSnapshot.child("state").getRef();

                        Map<String, Object> updatedValues = new HashMap<String, Object>();
                        updatedValues.put("stateUuid", behavior.getStateUuid());

                        ref.updateChildren(updatedValues, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError != null) {
                                    Log.v("CM_Log", "\t\tData could not be saved. " + firebaseError.getMessage());
                                } else {
                                    Log.v("CM_Log", "\t\tData saved successfully (behavior " + behaviorUuid + ")");
                                    System.out.println("");
                                }
                            }
                        });


//                        // Create behavior object from database.
//                        Behavior retrievedBehavior = behaviorSnapshot.getValue(Behavior.class);
//                        Log.v("CM_Log", "Retrieved unit (UUID: " + retrievedBehavior.getUuid() + ").");
//
//                        // Update cached unit from database.
//                        if (!getClay().getBehaviorCacheManager().hasBehavior(retrievedBehavior.getUuid().toString())) {
//
//                            getClay().getBehaviorCacheManager().cacheBehavior(retrievedBehavior);
//                            Log.v("CM_Log", "Cached the behavior.");
//
//                        } else {
//
//                            // TODO: Updated the cached unit with information from the unit retrieved from the database.
//                            Log.v("CM_Log", "Updated cached behavior.");
//
//                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("CM_Log", "\tThe read failed: " + firebaseError.getMessage());
            }
        });
    }

    /**
     * Retrieves behaviors from the remote repository, creates corresponding behavior objects
     * locally, and places them into the local cache.
     */
    public void restoreBehaviors() {
        Log.v("CM_Log", "restoreBehaviors");

        Firebase behaviorRef = rootRef.child("behaviors");

        // "The value event is used to read a static snapshot of the contents at a given path, as
        // they existed at the time of the event. It is triggered once with the initial data and
        // again every time the data changes. The event callback is passed a snapshot containing
        // all data at that location, including child data. In our code example above, onDataChange
        // returned all of the blog posts in our app. Every time a new blog post is added, the
        // listener function will return all of the posts."
        // - Source: https://www.firebase.com/docs/android/guide/retrieving-data.html#section-types
        //
        // "In some cases it may be useful for a callback to be called once and then immediately
        // removed. We can use addListenerForSingleValueEvent() to make this easy. It is triggered
        // one time and then will not be triggered again."
        // - Source: https://www.firebase.com/docs/android/guide/retrieving-data.html#section-reading-once
        behaviorRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("CM_Log", "\tbehaviorRef.addListenerForSingleValueEvent onDataChange");

                // onDataChange returns all of the behaviors. Every time a new behavior is added,
                // the listener function will return all of the behaviors.

                // Store behaviors in the local cache.
                // Iterate through all available behaviors and cache some of them.
                for (DataSnapshot behaviorSnapshot : dataSnapshot.getChildren()) {

                    // Create behavior object
                    Behavior behavior = behaviorSnapshot.getValue(Behavior.class);

                    // Place the object into the local cache
                    getClay().cacheBehavior(behavior);
                    Log.v("CM_Log", "\t\tAdding behavior (UUID: " + behavior.getUuid() + ")");
//                    Log.v("CM_Log", "\t\tAdding behavior state " + behavior.getState().getState() + " (UUID: " + behavior.getState().getUuid() + ")");

                    // Restore behavior's state.
                    restoreBehaviorState (behavior, behavior.getStateUuid());

//                    getClay().getContentManager().getBehaviorState(behavior, behavior.);
                }

                // Add the basic behaviors if they do not exist.
//                getClay().getBehaviorCacheManager().setupRepository();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("CM_Log", "\tThe read failed: " + firebaseError.getMessage());
            }
        });

        // "The onChildAdded event is typically used when retrieving a list of items in the
        // Firebase database. Unlike the value event which returns the entire contents of the
        // location, the onChildAdded event is triggered once for each existing child and then
        // again every time a new child is added to the specified path. The listener is passed a
        // snapshot containing the new child's data."
        // - Source: https://www.firebase.com/docs/android/guide/retrieving-data.html#section-types
        behaviorRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.v("CM_Log", "behaviorRef.addChildEventListener onChildAdded");

                Behavior behavior = dataSnapshot.getValue(Behavior.class);
//                updateBehavior(behavior);

                // TODO: Consider caching the behavior (or child).
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.v("CM_Log", "behaviorRef.addChildEventListener onChildChanged");

                // TODO: Update the child that was changed in the local cache if it's in the local cache.

                // Note: This can get called for an object that was first updated locally, so it would essentially verify that the changes match (or perhaps update only if a timestamp is later? or if the change source is not this device?).
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.v("CM_Log", "behaviorRef.addChildEventListener onChildRemoved");
                // TODO?: Unhandled case? Remove from the local cache?
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.v("CM_Log", "behaviorRef.addChildEventListener onChildMoved");
                // TODO?: Unhandled case?
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("CM_Log", "behaviorRef.addChildEventListener onCancelled");
            }
        });
    }

    /**
     * Retrieves the behavior with the specified UUID from the remote database and adds it to the
     * local cache.
     * @param behaviorUuid
     */
    public void restoreBehavior(final String behaviorUuid) {

        Firebase behaviorsRef = rootRef.child("behaviors");
        Query behaviorQueryRef = behaviorsRef.orderByChild ("uuid").equalTo(behaviorUuid).limitToFirst(1);

        behaviorQueryRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() == 0) {
                    Log.v("Behavior_DB", "There is no behavior with UUID equal to " + behaviorUuid + ".");
                } else if (dataSnapshot.getChildrenCount() > 0) {

                    // Store unit in the local cache.
                    for (DataSnapshot behaviorSnapshot : dataSnapshot.getChildren()) {

                        // Create behavior object from database.
                        Behavior retrievedBehavior = behaviorSnapshot.getValue(Behavior.class);
                        Log.v("Behavior_DB", "Retrieved unit (UUID: " + retrievedBehavior.getUuid() + ").");

                        // Update cached unit from database.
                        if (!getClay().getBehaviorCacheManager().hasBehavior(retrievedBehavior.getUuid().toString())) {

                            getClay().getBehaviorCacheManager().cacheBehavior(retrievedBehavior);
                            Log.v("Behavior_DB", "Cached the behavior.");

                        } else {

                            // TODO: Updated the cached unit with information from the unit retrieved from the database.
                            Log.v("Behavior_DB", "Updated cached behavior.");

                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("Behavior_DB", "The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void storeBehaviorState(BehaviorState behaviorState) {

        Firebase behaviorStateRef = rootRef.child("behaviorStates");
        behaviorStateRef.push ().setValue(behaviorState);

    }

    public void restoreBehaviorState (final Behavior behavior, UUID stateUuid) {

        Log.v ("CM_Log", "restoreBehaviorState");

        Firebase behaviorStateRef = rootRef.child ("behaviorStates");
        Query queryRef = behaviorStateRef.orderByChild("uuid").equalTo(stateUuid.toString()).limitToFirst(1);

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Store behaviors in the local cache.
                for (DataSnapshot behaviorSnapshot : dataSnapshot.getChildren()) {
                    // Create behavior object from database.
                    BehaviorState behaviorState = behaviorSnapshot.getValue(BehaviorState.class);
                    Log.v("CM_Log", "\tgot behavior state " + behaviorState);
                    behavior.setState(behaviorState);

                    Log.v("CM_Log", "\t\tbehavior.getState = " + behavior.getState());
                    Log.v("CM_Log", "\t\tbehavior.getState.getTag = " + behavior.getState().getTag());
                    Log.v("CM_Log", "\t\tbehavior.getState.getState = " + behavior.getState().getState());

//                    Log.v("Clay_Behavior_Repo", "Adding behavior " + behavior.getTag() + " (UUID: " + behavior.getUuid() + ")");
//                    getClay ().getBehaviorCacheManager().storeBehavior (behavior);
                }

//                // Add the basic behaviors if they do not exist.
//                getClay().getBehaviorCacheManager().setupRepository();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("CM_Log", "\tThe read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void updateTimeline(final Timeline timeline) {
        Log.v("CM_Log", "updateTimeline");

        final String timelineUuid = timeline.getUuid().toString();

        Firebase timelinesRef = rootRef.child("timelines");
        Query timelineQueryRef = timelinesRef.orderByChild ("uuid").equalTo(timelineUuid).limitToFirst(1);

        timelineQueryRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() == 0) {
                    Log.v("CM_Log", "\tThere is no timeline with UUID equal to " + timelineUuid + ".");
                } else if (dataSnapshot.getChildrenCount() > 0) {

                    Log.v("CM_Log", "\tGot timeline:");

                    // Store unit in the local cache.
                    for (DataSnapshot timelineSnapshot : dataSnapshot.getChildren()) {

                        Timeline timeline2 = timelineSnapshot.getValue(Timeline.class);
                        Log.v("CM_Log", "\t\tUUID " + timeline2.getUuid());

                        // Update values.
                        // First, get a reference to the behavior's state. Then create a map object
                        // with the updated values. Then commit the update to the database.
                        Firebase ref = timelineSnapshot.getRef();

//                        Map<String, Object> updatedValues = new HashMap<String, Object>();
//                        updatedValues.put("events", timeline.getEvents());
//                        updatedValues.put("events", updatedValues);

//                        ref.updateChildren(updatedValues, new Firebase.CompletionListener() {
//                            @Override
//                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
//                                if (firebaseError != null) {
//                                    Log.v("CM_Log", "\t\tTimeline could not be saved. " + firebaseError.getMessage());
//                                } else {
//                                    Log.v("CM_Log", "\t\tTimeline saved successfully (UUID " + timelineUuid + ")");
//                                    System.out.println("");
//                                }
//                            }
//                        });

                        ref.setValue(timeline, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError != null) {
                                    Log.v("CM_Log", "\t\tTimeline could not be saved. " + firebaseError.getMessage());
                                } else {
                                    Log.v("CM_Log", "\t\tTimeline saved successfully (UUID " + timelineUuid + ")");
                                    System.out.println("");
                                }
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("CM_Log", "\tThe read failed: " + firebaseError.getMessage());
            }
        });
    }
}
