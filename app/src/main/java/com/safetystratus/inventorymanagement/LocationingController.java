package com.safetystratus.inventorymanagement;

import android.os.AsyncTask;
import android.util.Log;

import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.START_TRIGGER_TYPE;
import com.safetystratus.inventorymanagement.asciitohex;

import static com.safetystratus.inventorymanagement.RFIDHandler.isInventoryAborted;
import static com.safetystratus.inventorymanagement.RFIDHandler.isLocatingTag;
import static com.safetystratus.inventorymanagement.RFIDHandler.isLocationingAborted;
import static com.safetystratus.inventorymanagement.RFIDHandler.mIsInventoryRunning;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class LocationingController {
    private static final String TAG = "LocationingController";

    Lock locateLock = new Lock() {
        private boolean isLocked = false;

        public synchronized void lock() {
            while(isLocked){
                try {
                    wait();
                } catch (InterruptedException e) {
                    Log.d(TAG,  "Returned SDK Exception");
                }
            }
            isLocked = true;
        }

        public synchronized void unlock(){
            isLocked = false;
            notify();
        }
        @Override
        public void lockInterruptibly() throws InterruptedException {

        }

        @Override
        public boolean tryLock() {
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }



        @Override
        public Condition newCondition() {
            return null;
        }
    };

    protected LocationingController() {
    }

    public void locationing(final String locateTag, final RfidListeners rfidListeners) {

        if (RFIDHandler.reader != null && RFIDHandler.reader.isConnected()) {
            if (!RFIDHandler.isLocatingTag) {
                RFIDHandler.currentLocatingTag = locateTag;
                RFIDHandler.TagProximityPercent = 0;
                if (locateTag != null && !locateTag.isEmpty()) {
                    RFIDHandler.isLocatingTag = true;
                    new AsyncTask<Void, Void, Boolean>() {

                        private InvalidUsageException invalidUsageException;
                        private OperationFailureException operationFailureException;

                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            locateLock.lock();
                            try {
                                if (RFIDHandler.asciiMode) {
                                    RFIDHandler.reader.Actions.TagLocationing.Perform(asciitohex.convert(locateTag), null, null);
                                    RFIDHandler.isLocatingTag = true;
                                }else {
                                    RFIDHandler.reader.Actions.TagLocationing.Perform(locateTag, null, null);
                                    RFIDHandler.isLocatingTag = true;
                                }
                            } catch (InvalidUsageException e) {
                                Log.d(TAG,  "Returned SDK Exception");
                                invalidUsageException = e;
                            } catch (OperationFailureException e) {
                                Log.d(TAG,  "Returned SDK Exception");
                                operationFailureException = e;
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                            locateLock.unlock();
                            RFIDHandler.isLocatingTag = true;
                            if (invalidUsageException != null) {
                                RFIDHandler.currentLocatingTag = null;
                                RFIDHandler.isLocatingTag = false;
                                rfidListeners.onFailure(invalidUsageException);
                            } else if (operationFailureException != null) {
                                RFIDHandler.currentLocatingTag = null;
                                RFIDHandler.isLocatingTag = false;
                                rfidListeners.onFailure(operationFailureException);


                            } else
                                rfidListeners.onSuccess(null);
                        }
                    }.execute();
                } else {
                    Log.d(RFIDHandler.TAG, Constants.TAG_EMPTY);
                    rfidListeners.onFailure(Constants.TAG_EMPTY);
                }

            } else {
                isLocationingAborted = false;
                mIsInventoryRunning = false;
                isLocatingTag = false;
                isInventoryAborted = false;
                new AsyncTask<Void, Void, Boolean>() {
                    private InvalidUsageException invalidUsageException;
                    private OperationFailureException operationFailureException;

                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        locateLock.lock();
                        try {
                            RFIDHandler.reader.Actions.TagLocationing.Stop();
                            /*if (((RFIDHandler.settings_startTrigger != null && (RFIDHandler.settings_startTrigger.getTriggerType() == START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE)))
                                    || (RFIDHandler.isBatchModeInventoryRunning != null && RFIDHandler.isBatchModeInventoryRunning))
                                ConnectionController.operationHasAborted(rfidListeners);*/
                        } catch (InvalidUsageException e) {
                            invalidUsageException = e;
                            Log.d(TAG,  "Returned SDK Exception");
                        } catch (OperationFailureException e) {
                            operationFailureException = e;
                            Log.d(TAG,  "Returned SDK Exception");
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        locateLock.unlock();
                        RFIDHandler.isLocatingTag = false;
                        RFIDHandler.currentLocatingTag = null;
                        if (invalidUsageException != null) {
                            rfidListeners.onFailure(invalidUsageException);

                        } else if (operationFailureException != null) {
                            rfidListeners.onFailure(operationFailureException);
                        } else
                            rfidListeners.onSuccess(null);
                    }
                }.execute();
            }
        } else
            rfidListeners.onFailure("No Active Connection with Reader");
    }


}
