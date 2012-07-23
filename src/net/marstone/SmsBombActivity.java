package net.marstone;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SmsBombActivity extends Activity {
	
    EditText editPhone, editMessage, editInterval, editLast;
    Button buttonStart, buttonStop, buttonSend, buttonTest;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        this.editPhone = (EditText)findViewById(R.id.editTextPhone);
        this.editMessage = (EditText)findViewById(R.id.editTextMessage);
        this.editInterval = (EditText)findViewById(R.id.editTextInterval);
        this.editLast = (EditText)findViewById(R.id.editTextLast);

        long unixTime = System.currentTimeMillis() / 1000L;

        editPhone.setText("106980095533");
        editMessage.setText("MSSH");
        // editPhone.setText("13774240308");
        // editMessage.setText(Long.valueOf(unixTime).toString());
        editInterval.setText("500");
        editLast.setText("1000");
        
        buttonStart = (Button)findViewById(R.id.buttonStart);
		buttonStop = (Button)findViewById(R.id.buttonStop);
		buttonSend = (Button)findViewById(R.id.buttonSend);
		buttonTest = (Button)findViewById(R.id.buttonTest);

        buttonStart.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) 
            {                
                // sendOne();
            	if (mStartTime == 0L) {
                    mStartTime = System.currentTimeMillis();
                    mHandlerUpdate.removeCallbacks(mUpdateTimeTask);
                    mHandlerUpdate.postDelayed(mUpdateTimeTask, 100);
               }
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) 
            {                
                sendOne();
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) 
            {                
                Toast.makeText(getBaseContext(), "Stop.", Toast.LENGTH_SHORT).show();
                mHandler.removeCallbacks(mUpdateTimeTask);
                mStartTime = 0L;
            }
        });

        buttonTest.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) 
            {                
            	Log.v("Test", "test " + System.currentTimeMillis() / 1000L);
                Toast.makeText(getBaseContext(), "Test.", Toast.LENGTH_SHORT).show();
            }
        });
        
        // sendOne();
    	if (mStartUpdateTime == 0L) {
    		mStartUpdateTime = System.currentTimeMillis();
            mHandler.removeCallbacks(this.mUpdateCurrentTimeTask);
            mHandler.postDelayed(mUpdateCurrentTimeTask, 100);
       }
        
    }

    public void onBackPressed() {
	   Log.i("HA", "Finishing");
	   //Intent intent = new Intent(Intent.ACTION_MAIN);
	   //intent.addCategory(Intent.CATEGORY_HOME);
	   //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	   //startActivity(intent);
       Toast.makeText(getBaseContext(), "Quit.", Toast.LENGTH_SHORT).show();
	   this.finish();
	 }

    

    private Handler mHandler = new Handler();
    private Handler mHandlerUpdate = new Handler();
    private long mStartTime = 0L;
    private long mStartUpdateTime = 0L;
    
    private Runnable mUpdateTimeTask = new Runnable() {
	   public void run() {
	       final long start = mStartTime;
	       // long millis = SystemClock.uptimeMillis() - start;
	       long millis = System.currentTimeMillis() - start;
	       int msecs = (int) (millis % 1000);
	       int seconds = (int) (millis / 1000);
	       int minutes = seconds / 60;
	       seconds     = seconds % 60;

	       //Log.v("Test", "min=" + Integer.valueOf(minutes).toString() + ",sec=" + seconds + ",ms=" + msecs);
	       
	       if (seconds < 10) {
	    	   Resources standardResources = getResources();
	    	   // AssetManager assets = standardResources.getAssets();
	    	   String text = standardResources.getString(R.string.button_start);
	    	   buttonStart.setText(text + "(" + Integer.valueOf(msecs).toString() + ")");
	           //mTimeLabel.setText("" + minutes + ":0" + seconds);
	       } else {
	           //mTimeLabel.setText("" + minutes + ":" + seconds);            
	       }
	     
	       sendOne();
	       
	       // long next = start + (((minutes * 60) + seconds + 1) * 1000);
	       // Log.v("Test", "next=" + next);
	       // mHandler.postAtTime(this, next);
	       mHandler.postDelayed(this, 100);
	   }
	};

    private Runnable mUpdateCurrentTimeTask = new Runnable() {
	   public void run() {
		   String now = currentTime();
    	   String text = getResources().getString(R.string.last);
    	   editLast.setText(text + "(" + now + ")");
	       mHandler.postDelayed(this, 50);
	   }
	};

    private void sendOne() 
    {
    	String phoneNo = editPhone.getText().toString();
        String message = editMessage.getText().toString();                 
        if (phoneNo.length()>0 && message.length()>0) 
        {            
            sendSMS(phoneNo, message);
            String display = currentTime();
            // String now = new Date().toString("HH:mm:ss");
            Log.v("SmsBomb", "sending(" + display + ")... phoneNo=" +  phoneNo + ",message=" + message);
        } 
        else
            Toast.makeText(getBaseContext(), 
                "Please enter both phone number and message.", 
                Toast.LENGTH_SHORT).show();
    }
    
    private String currentTime()
    {
        long nowMillis = System.currentTimeMillis();
        long now = nowMillis / 1000;
        String display = String.format("%02d:%02d:%02d", (now % 3600) / 60, (now % 60), nowMillis % 1000);
        return display;
    }
    
    @SuppressWarnings("unused")
	private void sendSMS1(String phoneNumber, String message)
    {        
        Log.v("phoneNumber",phoneNumber);
        Log.v("Message",message);
        PendingIntent pi = PendingIntent.getActivity(this, 0,
            new Intent(this, DummyClass.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);        
    }  
    
    //---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message)
    {        
    	Log.v("phoneNumber",phoneNumber);
        Log.v("Message",message);
        
    	String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
 
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);
 
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
            new Intent(DELIVERED), 0);
 
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", 
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));
 
        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        }, new IntentFilter(DELIVERED));        
 
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);        
    }
}