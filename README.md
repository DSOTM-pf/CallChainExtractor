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

This method will filter out the Call Chain whose ending node does not belong to the method defined by the application. You can refer to the following example to help understand.

#### Example APK

#### Example Outputs

### Logs

The current version main logic for extracting call chain is to traverse all the methods in the APK. For a single method, go back up to find the caller until the ```dummy main``` method is found.

The result of this is that the algorithm will take a lot of time, but the final result can be accurately obtained.

This algorithm is currently being optimized to make the time cost of finding the call chains lower.