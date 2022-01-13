package com.hamfer.ferlauncher;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import android.content.pm.PackageInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import android.util.Log;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import java.lang.StringBuilder;
import com.facebook.react.bridge.Promise;
import android.content.Intent;

public class InstalledAppsModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  InstalledAppsModule(ReactApplicationContext context) {
    super(context);
    this.reactContext = context;
  }

  @Override
  public String getName() {
    return "InstalledAppsModule";
  }

  @ReactMethod
  public void test(String arg) {
    Log.d("HamferModule", "Create event called with name: " + arg);
  }

  @ReactMethod
  public void getAllApps(Promise promise) {
    try {
      List<AppDetail> apps = new ArrayList<>();
      List<PackageInfo> packages = this.reactContext
          .getPackageManager()
          .getInstalledPackages(0);
      // StringBuilder str = new StringBuilder();
      // str.append("[");
      // Boolean first = true;
      for (final PackageInfo p : packages) {
        if (this.reactContext.getPackageManager().getLaunchIntentForPackage(p.packageName) != null) {
          AppDetail app = new AppDetail();
          // String app = p.packageName;
          app.label = p.applicationInfo.loadLabel(this.reactContext.getPackageManager());
          app.name = p.packageName;
          app.icon = p.applicationInfo.loadIcon(this.reactContext.getPackageManager());
          apps.add(app);
          // if (!first) {
          // str.append(",");
          // } else {
          // first = false;
          // }
          // str.append("{\"packageName\":\"" + app + "\"}");
        }
      }
      // str.append("]");
      promise.resolve(apps.toString());
    } catch (Exception e) {
      promise.reject("Error", e);
    }
  }

  @ReactMethod
  private void launchApplication(String packageName) {
    Intent launchIntent = this.reactContext.getPackageManager().getLaunchIntentForPackage(packageName);
    if (launchIntent != null) {
      this.reactContext.startActivity(launchIntent);// null pointer check in case package name was not found
    }
  }

  private class AppDetail {
    CharSequence label;
    CharSequence name;
    Drawable icon;

    public String toString() {
      Bitmap icon;
      if (this.icon.getIntrinsicWidth() <= 0 || this.icon.getIntrinsicHeight() <= 0) {
        icon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
      } else {
        icon = Bitmap.createBitmap(this.icon.getIntrinsicWidth(), this.icon.getIntrinsicHeight(),
            Bitmap.Config.ARGB_8888);
      }
      final Canvas canvas = new Canvas(icon);
      this.icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
      this.icon.draw(canvas);

      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      icon.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
      byte[] byteArray = byteArrayOutputStream.toByteArray();
      String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);

      return "{\"label\":\"" + this.label + "\",\"name\":\"" + this.name + "\",\"icon\":\"" + encoded + "\"}";
    }
  }
}
