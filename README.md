### Overview

This module is used to extract the call chain of methods in APK.The module relies on [```Flow Droid```]() to implement this function. You can specify the APK path to start the analysis.

### Requirements

To use this module, in addition to the APK path you want to parse, you also need to provide the ```Android.jar``` corresponding to the APK’s ```targetsdkversion```.

You can download ```Android.jar``` here : [Download Android Jar](https://github.com/CirQ/android-platforms)

### Example code

##### Get all call chains of APK

```java
Set<CallChain> resultsOfALl = CallChainMain.getAllChains(Paths.get("apks/mapbox.apk"),Paths.get("D:\\AndroidEnviorment\\androidJAR"));
```

###### All Chains :

If you use this method, you can get all the calling paths of the APK, including calls to APP and Libraries.

##### Get application call chains of APK

```javascript
Set<CallChain> resultsOfAPP = CallChainMain.getAppChains(Paths.get("apks/mapbox.apk"),Paths.get("D:\\AndroidEnviorment\\androidJAR"));
```

###### Application Chains:

If you use this method to obtain the calling path of the APK, it is equivalent to filtering the results obtained in the previous method.

This method will filter out the Call Chain whose ending node does not belong to the method defined by the application. 

#### Example APK

- **Code**

  ```java
      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);
  
          // Show user location (purposely not in follow mode)
          if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                  (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
              ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
          } else {
              mv.setMyLocationEnabled(true);
          }
      }
  ```

- **Location**

  [mapbox.apk](https://github.com/wowhhh/CallChainExtractor/blob/master/apks/mapbox.apk)

#### Example Outputs

- **Outputs of ALL Chains**

  ```mv.setMyLocationEnabled(true);``` This API is encapsulated in the mapbox library, so the internal structure is relatively complicated. 

  Take one of the extracted Call Chains as an example :

  ```
  com.example.d049mapbox.MainActivity.onCreate(android.os.Bundle)void
   com.mapbox.mapboxsdk.views.MapView.setMyLocationEnabled(boolean)void
    com.mapbox.mapboxsdk.views.UserLocationView.setEnabled(boolean)void
     com.mapbox.mapboxsdk.views.UserLocationView.toggleGps(boolean)void
       ......
       java.lang.Exception.<init>()void
  ```

  

- **Outputs of Application Chains**

  The code is the same as above, and the output below is different from the above:

  ```
  com.example.d049mapbox.MainActivity.onCreate(android.os.Bundle)void
   com.mapbox.mapboxsdk.views.MapView.setMyLocationEnabled(boolean)void
    com.mapbox.mapboxsdk.views.UserLocationView.setEnabled(boolean)void
     com.mapbox.mapboxsdk.views.UserLocationView.toggleGps(boolean)void
       ......
       com.squareup.okhttp.internal.framed.HeadersMode.<clinit>()void
  ```

  Using this method will filter out that the end node does not belong to the method defined by the application . ```java.lang.Exception.<init>()void``` belongs to the one provided in jdk.

### Logs

The current version main logic for extracting call chain is to traverse all the methods in the APK. For a single method, go back up to find the caller until the ```dummy main``` method is found.

The result of this is that the algorithm will take a lot of time, but the final result can be accurately obtained.

This algorithm is currently being optimized to make the time cost of finding the call chains lower.