package ly.count.android.sdk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class CountlyCordova extends CordovaPlugin {
    private boolean isTestDevice = false;

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Context context = this.cordova.getActivity().getApplicationContext();

        if ("init".equals(action)) {
            String serverUrl = args.getString(0);
            String appKey = args.getString(1);

            String deviceId = null;
            DeviceId.Type deviceIdType = DeviceId.Type.OPEN_UDID;

            ArrayList<String> features = new ArrayList<>();

            features.add(Countly.CountlyFeatureNames.sessions);
            features.add(Countly.CountlyFeatureNames.crashes);
            features.add(Countly.CountlyFeatureNames.events);
            features.add(Countly.CountlyFeatureNames.users);
            features.add(Countly.CountlyFeatureNames.views);

            Countly.sharedInstance().setRequiresConsent(true);

            if (!args.isNull(2)) {
                JSONObject options = args.getJSONObject(2);

                if (options.has("isTestDevice")) {
                    features.add(Countly.CountlyFeatureNames.push);
                    this.isTestDevice = options.getBoolean("isTestDevice");
                }

                if (args.length() == 3 && options.has("customDeviceId")) {
                    deviceId = options.getString("customDeviceId");
                }

                if (options.has("enableRemoteConfig")) {
                    Countly.sharedInstance().setRemoteConfigAutomaticDownload(
                            options.getBoolean("enableRemoteConfig"), null);
                }
            }

            Countly.sharedInstance()
                    .setConsent(features.toArray(new String[0]), true);
            Countly.sharedInstance()
                    .init(context, serverUrl, appKey, deviceId, deviceIdType);
            Countly.sharedInstance().onStart(this.cordova.getActivity());

            callbackContext.success("initialized!");
            return true;
        }

        else if ("changeDeviceId".equals(action)){
            String newDeviceID = args.getString(0);
            Countly.sharedInstance().changeDeviceId(newDeviceID);
            callbackContext.success("changeDeviceId success!");
            return true;
        }
        else if ("setHttpPostForced".equals(action)){
            int isEnabled = Integer.parseInt(args.getString(0));
            if(isEnabled == 1){
                Countly.sharedInstance().setHttpPostForced(true);
            }else{
                Countly.sharedInstance().setHttpPostForced(false);
            }
            callbackContext.success("setHttpPostForced This method doesn't exists!");
            return true;
        }else if("enableParameterTamperingProtection".equals(action)){
            String salt = args.getString(0);
            Countly.sharedInstance().enableParameterTamperingProtection(salt);
            callbackContext.success("enableParameterTamperingProtection success!");
            return true;
        }else if("setLocation".equals(action)){
            String latitude = args.getString(0);
            String longitude = args.getString(1);
            String latlng = (latitude +"," +longitude);
            Countly.sharedInstance().setLocation(null, null, latlng, null);
            callbackContext.success("setLocation success!");
            return true;
        }else if("enableCrashReporting".equals(action)){
            Countly.sharedInstance().enableCrashReporting();
            callbackContext.success("enableCrashReporting success!");
            return true;
        }else if("addCrashLog".equals(action)){
            String record = args.getString(0);
            Countly.sharedInstance().addCrashLog(record);
            callbackContext.success("addCrashLog success!");
            return true;
        } else if("logException".equals(action)) {
            Exception exception = new Exception(args.getString(0));
            Boolean isFatal = args.getBoolean(1);
            JSONObject segments = args.isNull(2) ? null : args.getJSONObject(2);

            this.logException(exception, isFatal, segments, callbackContext);
            return true;
        }
        else if ("start".equals(action)) {
            Countly.sharedInstance().onStart(this.cordova.getActivity());
            callbackContext.success("started!");
            return true;
        }
        else if ("stop".equals(action)) {
            Countly.sharedInstance().onStop();
            callbackContext.success("stoped!");
            return true;
        }else if("startEvent".equals(action)){
            String startEvent = args.getString(0);
            Countly.sharedInstance().startEvent(startEvent);
            return true;
        }else if("endEvent".equals(action)){
            String eventType = args.getString(0);
            if("event".equals(eventType)){
                String eventName = args.getString(1);
                Countly.sharedInstance().endEvent(eventName);
                callbackContext.success("event sent");
            }
            else if ("eventWithSegment".equals(eventType)) {
                String eventName = args.getString(1);
                HashMap<String, String> segmentation = new HashMap<String, String>();
                for(int i=4,il=args.length();i<il;i+=2){
                    segmentation.put(args.getString(i), args.getString(i+1));
                }
                Countly.sharedInstance().endEvent(eventName, segmentation, 1,0);
                callbackContext.success("eventWithSumSegment sent");
            }
            else if ("eventWithSum".equals(eventType)) {
                String eventName = args.getString(1);
                int eventCount= Integer.parseInt(args.getString(2));
                float eventSum= new Float(args.getString(3)).floatValue();
                HashMap<String, String> segmentation = new HashMap<String, String>();
                for(int i=4,il=args.length();i<il;i+=2){
                    segmentation.put(args.getString(i), args.getString(i+1));
                }
                Countly.sharedInstance().endEvent(eventName, segmentation, eventCount,eventSum);
                callbackContext.success("eventWithSumSegment sent");
            }
            else if ("eventWithSumSegment".equals(eventType)) {
                String eventName = args.getString(1);
                int eventCount= Integer.parseInt(args.getString(2));
                float eventSum= new Float(args.getString(3)).floatValue();
                HashMap<String, String> segmentation = new HashMap<String, String>();
                for(int i=4,il=args.length();i<il;i+=2){
                    segmentation.put(args.getString(i), args.getString(i+1));
                }
                Countly.sharedInstance().endEvent(eventName, segmentation, eventCount,eventSum);
                callbackContext.success("eventWithSumSegment sent");
            }
            else{
                callbackContext.success("event sent");
            }
            return true;
        }
        else if ("event".equals(action)) {
            String eventType = args.getString(0);
            if("event".equals(eventType)){
                String eventName = args.getString(1);
                int eventCount= Integer.parseInt(args.getString(2));
                Countly.sharedInstance().recordEvent(eventName, eventCount);
                callbackContext.success("event sent");
            }
            else if ("eventWithSum".equals(eventType)) {
                String eventName = args.getString(1);
                int eventCount= Integer.parseInt(args.getString(2));
                float eventSum= new Float(args.getString(3)).floatValue();
                Countly.sharedInstance().recordEvent(eventName, eventCount, eventSum);
                callbackContext.success("eventWithSum sent");
            }
            else if ("eventWithSegment".equals(eventType)) {
                String eventName = args.getString(1);
                int eventCount= Integer.parseInt(args.getString(2));
                //int eventSum= Integer.parseInt(args.getString(3));
                HashMap<String, String> segmentation = new HashMap<String, String>();
                for(int i=3,il=args.length();i<il;i+=2){
                    segmentation.put(args.getString(i), args.getString(i+1));
                }
                Countly.sharedInstance().recordEvent(eventName, segmentation, eventCount);
                callbackContext.success("eventWithSegment sent");
            }
            else if ("eventWithSumSegment".equals(eventType)) {
                String eventName = args.getString(1);
                int eventCount= Integer.parseInt(args.getString(2));
                float eventSum= new Float(args.getString(3)).floatValue();
                HashMap<String, String> segmentation = new HashMap<String, String>();
                for(int i=4,il=args.length();i<il;i+=2){
                    segmentation.put(args.getString(i), args.getString(i+1));
                }
                Countly.sharedInstance().recordEvent(eventName, segmentation, eventCount,eventSum);
                callbackContext.success("eventWithSumSegment sent");
            }
            else{
                callbackContext.success("event sent");
            }
            return true;
        }
        else if ("setloggingenabled".equals(action)) {
            Countly.sharedInstance().setLoggingEnabled(true);
            callbackContext.success("setloggingenabled success!");
            return true;
        }


        else if ("setuserdata".equals(action)) {
            // Bundle bundle = new Bundle();

            Map<String, String> bundle = new HashMap<String, String>();

            bundle.put("name", args.getString(0));
            bundle.put("username", args.getString(1));
            bundle.put("email", args.getString(2));
            bundle.put("organization", args.getString(3));
            bundle.put("phone", args.getString(4));
            bundle.put("picture", args.getString(5));
            bundle.put("picturePath", args.getString(6));
            bundle.put("gender", args.getString(7));
            bundle.put("byear", String.valueOf(args.getInt(8)));

            Countly.userData.setUserData(bundle);
            Countly.userData.save();

            // Countly.sharedInstance().setUserData(bundle);
            // Countly.sharedInstance().setUserData(bundle);
            callbackContext.success("setuserdata success");
            return true;
        }
        else if ("userData_setProperty".equals(action)) {
            String keyName = args.getString(0);
            String keyValue = args.getString(1);
            Countly.userData.setProperty(keyName, keyValue);
            Countly.userData.save();
            callbackContext.success("userData_setProperty success!");
            return true;
        }
        else if ("userData_increment".equals(action)) {
            String keyName = args.getString(0);
            Countly.userData.increment(keyName);
            Countly.userData.save();
            callbackContext.success("userData_increment success!");
            return true;
        }
        else if ("userData_incrementBy".equals(action)) {
            String keyName = args.getString(0);
            int keyIncrement = Integer.parseInt(args.getString(1));
            Countly.userData.incrementBy(keyName, keyIncrement);
            Countly.userData.save();
            callbackContext.success("userData_incrementBy success!");
            return true;
        }
        else if ("userData_multiply".equals(action)) {
            String keyName = args.getString(0);
            int multiplyValue = Integer.parseInt(args.getString(1));
            Countly.userData.multiply(keyName, multiplyValue);
            Countly.userData.save();
            callbackContext.success("userData_multiply success!");
            return true;
        }
        else if ("userData_saveMax".equals(action)) {
            String keyName = args.getString(0);
            int maxScore = Integer.parseInt(args.getString(1));
            Countly.userData.saveMax(keyName, maxScore);
            Countly.userData.save();
            callbackContext.success("userData_saveMax success!");
            return true;
        }
        else if ("userData_saveMin".equals(action)) {
            String keyName = args.getString(0);
            int minScore = Integer.parseInt(args.getString(1));
            Countly.userData.saveMin(keyName, minScore);
            Countly.userData.save();
            callbackContext.success("userData_saveMin success!");
            return true;
        }
        else if ("userData_setOnce".equals(action)) {
            String keyName = args.getString(0);
            String minScore = args.getString(1);
            Countly.userData.setOnce(keyName, minScore);
            Countly.userData.save();
            callbackContext.success("userData_setOnce success!");
            return true;
        } else if ("onregistrationid".equals(action)) {
            String registrationId = args.getString(0);

            Countly.CountlyMessagingMode messagingMode = this.isTestDevice ?
                    Countly.CountlyMessagingMode.TEST :
                    Countly.CountlyMessagingMode.PRODUCTION;

            Countly.sharedInstance().onRegistrationId(registrationId, messagingMode);
            callbackContext.success("initMessaging success");

            return true;
        }
        else if("recordView".equals(action)){
            String viewName = args.getString(0);
            Countly.sharedInstance().recordView(viewName);
            callbackContext.success("View name sent: "+viewName);
            return true;
        }
        else if("setOptionalParametersForInitialization".equals(action)){
            String city = args.getString(0);
            String country = args.getString(1);
            String latitude = args.getString(2);
            String longitude = args.getString(3);
            String ipAddress = args.getString(4);

            Countly.sharedInstance().setLocation(country, city, latitude +"," +longitude, ipAddress);

            callbackContext.success("setOptionalParametersForInitialization sent.");
            return true;
        } else if ("getDeviceIdentifier".equals(action)) {
            if(!Countly.sharedInstance().isInitialized()) {
                callbackContext.error("Countly has not been initialized yet.");
                return true;
            }

            callbackContext.success(Countly.sharedInstance().getDeviceID());
            return true;
        } else if ("getRemoteConfigValueForKey".equals(action)){
            String key = args.getString(0);
            Object remoteConfigValue = Countly.sharedInstance().getRemoteConfigValueForKey(key);

            if(remoteConfigValue == null) {
                callbackContext.success();
            }

            callbackContext.success(remoteConfigValue.toString());
            return true;
        }
        else{
            return false;
        }
    }

    private void logException(
            Exception exception, Boolean isFatal, JSONObject segments,
            CallbackContext callbackContext) throws JSONException {

        HashMap<String, String> segmentsMap = new HashMap<>();

        if(segments != null) {
            Iterator <String> segmentsKeys = segments.keys();

            while(segmentsKeys.hasNext()) {
                String key = segmentsKeys.next();

                segmentsMap.put(key, segments.getString(key));
            }
        }

        if(!segmentsMap.isEmpty()) {
                Countly.sharedInstance().setCustomCrashSegments(segmentsMap);
        }

        if(isFatal) {
            Countly.sharedInstance().recordUnhandledException(exception);
        } else {
            Countly.sharedInstance().recordHandledException(exception);
        }

        callbackContext.success();
    }
}