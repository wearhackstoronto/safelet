/*
 * Copyright (C) 2014 Thalmic Labs Inc.
 * Distributed under the Myo SDK license agreement. See LICENSE.txt for details.
 */

package com.thalmic.android.sample.helloworld;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;

import android.telephony.SmsManager;
/*import android.telephony.SmsMessage;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;*/

import org.w3c.dom.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HelloWorldActivity extends Activity {

    private TextView mLockStateView;
    private TextView mTextView;
    private int FSMstarted=0;
    private float prevValueRoll=1000;
    private float prevValuePitch=1000;

    private String previousAction;
    private String prevprevAction;


    String requester="4167054985";
    private static final String SENT_ACTION="SENT_SMS";
    private static final String DELIVERED_ACTION="DELIVERED_SMS";
    String reply="blank txt";

    LocationManager myLocationManager;
    String PROVIDER = LocationManager.GPS_PROVIDER;



    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {

        // onConnect() is called whenever a Myo has been connected.
        @Override
        public void onConnect(Myo myo, long timestamp) {
            // Set the text color of the text view to cyan when a Myo connects.
            mTextView.setTextColor(Color.CYAN);
        }

        // onDisconnect() is called whenever a Myo has been disconnected.
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            // Set the text color of the text view to red when a Myo disconnects.
            mTextView.setTextColor(Color.RED);
        }

        // onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
        // arm. This lets Myo know which arm it's on and which way it's facing.
        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
           // mTextView.setText(myo.getArm() == Arm.LEFT ? R.string.arm_left : R.string.arm_right);
        }

        // onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
        // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
        // when Myo is moved around on the arm.
        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
            mTextView.setText(R.string.hello_world);
        }

        // onUnlock() is called whenever a synced Myo has been unlocked. Under the standard locking
        // policy, that means poses will now be delivered to the listener.
        @Override
        public void onUnlock(Myo myo, long timestamp) {
            mLockStateView.setText(R.string.unlocked);
        }

        // onLock() is called whenever a synced Myo has been locked. Under the standard locking
        // policy, that means poses will no longer be delivered to the listener.
        @Override
        public void onLock(Myo myo, long timestamp) {
            mLockStateView.setText(R.string.locked);
        }

        // onOrientationData() is called whenever a Myo provides its current orientation,
        // represented as a quaternion.
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {


            // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
            float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
            float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
            float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));

            // Adjust roll and pitch for the orientation of the Myo on the arm.
            if (myo.getXDirection() == XDirection.TOWARD_ELBOW) {
                roll *= -1;
                pitch *= -1;
            }

            // Next, we apply a rotation to the text view using the roll, pitch, and yaw.
            mTextView.setRotation(roll);
            mTextView.setRotationX(pitch);
            mTextView.setRotationY(yaw);

     //      Log.e("pitch ", Float.toString(pitch));


            if (roll > 70 && FSMstarted == 0) {
                mLockStateView.setText("Clockwise Rotation Complete");
                FSMstarted = 1;
                prevValueRoll = roll;
                myo.vibrate(Myo.VibrationType.SHORT);

            }
            if (prevValueRoll > roll && FSMstarted == 1) {
                FSMstarted = 1;

            }
            if (roll < -20 && FSMstarted == 1) {
                mLockStateView.setText("ANTIclockwise Rotation Complete");
                FSMstarted = 2;
                if (!(pitch < -35))
                    prevValuePitch = pitch;
                else FSMstarted = 3;
                //  Toast.makeText(, Float.toString(roll), Toast.LENGTH_SHORT).show();
                myo.vibrate(Myo.VibrationType.SHORT);

            }
            if (prevValuePitch > pitch && FSMstarted == 2)
                mLockStateView.setText("Moving Upwards");

            if (pitch < -35 && FSMstarted == 2) {
                FSMstarted = 3;
                mLockStateView.setText("WE're done");
                myo.vibrate(Myo.VibrationType.SHORT);

            }
            if (FSMstarted == 3) {
                //respond();
                //sendWearableMsg();

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("4167054985", null, "I might be in trouble. Current Coordinates :43.659913N , -79.38863900000001 S", null, null);

                //SmsManager smsManager1 = SmsManager.getDefault();
                smsManager.sendTextMessage("6478348212", null, "I might be in trouble. Current Coordinates :43.659913N , -79.38863900000001 S", null, null);


                FSMstarted=0;

            }
        }

        // onPose() is called whenever a Myo provides a new pose.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Handle the cases of the Pose enumeration, and change the text of the text view
            // based on the pose we receive.
            mLockStateView.setText("fist4");


           switch (pose) {
               case FIST:
                   mTextView.setText("fist1");

                   if(prevprevAction.equals("FIST") && previousAction.equals("FIST")){
                       mTextView.setText("fist2");
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage("4167054985", null, "I am in trouble. Current Coordinates :43.659913N , -79.38863900000001 S", null, null);
                        previousAction="Random";
                        prevprevAction="Random";
                              break;
                    }
                   mTextView.setText("fist3");
                   prevprevAction=previousAction;

                   previousAction="FIST";
                   break;
               default:
                   prevprevAction=previousAction;
                   previousAction="Random";
                   break;
           }
                    //mTextView.setText(getString(R.string.pose_rest));
               //case DOUBLE_TAP:
                 //   mTextView.setText(getString(R.string.pose_doubletap));
                  //  break;
            /*    case DOUBLE_TAP:
                    int restTextId = R.string.hello_world;
                    switch (myo.getArm()) {
                        case LEFT:
                            restTextId = R.string.arm_left;
                            break;
                        case RIGHT:
                            restTextId = R.string.arm_right;
                            break;
                    }
                    mTextView.setText(getString(restTextId));
                    break;
                case FIST:
                    mTextView.setText(getString(R.string.pose_fist));
                    break;
                case WAVE_IN:
                    mTextView.setText(getString(R.string.pose_wavein));
                    break;
                case WAVE_OUT:
                    mTextView.setText(getString(R.string.pose_waveout));
                    break;
                case FINGERS_SPREAD:
                    mTextView.setText(getString(R.string.pose_fingersspread));
                    break;
            }

            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
                // Tell the Myo to stay unlocked until told otherwise. We do that here so you can
                // hold the poses without the Myo becoming locked.
                myo.unlock(Myo.UnlockType.HOLD);

                // Notify the Myo that the pose has resulted in an action, in this case changing
                // the text on the screen. The Myo will vibrate.
                myo.notifyUserAction();
            } else {
                // Tell the Myo to stay unlocked only for a short period. This allows the Myo to
                // stay unlocked while poses are being performed, but lock after inactivity.
                myo.unlock(Myo.UnlockType.HOLD);
            }*/
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);

        mLockStateView = (TextView) findViewById(R.id.lock_state);
        mTextView = (TextView) findViewById(R.id.text);




        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            Hub.getInstance().attachToAdjacentMyo();
            if (!hub.init(this, getPackageName())) {
                // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
                Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);
        hub.setLockingPolicy(Hub.LockingPolicy.NONE);

        FSMstarted=0;
        prevValueRoll=1000;
        prevValuePitch=1000;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Activity is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);

        if (isFinishing()) {
            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
            Hub.getInstance().shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_scan == id) {
            onScanActionSelected();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }






}
