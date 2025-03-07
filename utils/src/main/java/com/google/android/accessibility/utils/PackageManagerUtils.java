/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.accessibility.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import com.google.android.libraries.accessibility.utils.log.LogUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

/** Utilities for interacting with the {@link PackageManager}. */
public class PackageManagerUtils {
  private static final String TAG = "PackageManagerUtils";

  /** Invalid version code for a package. */
  public static final int INVALID_VERSION_CODE = -1;

  /** talkback-package-name constants */
  public static final String TALKBACK_PACKAGE = BuildConfig.TALKBACK_APPLICATION_ID;

  /** TalkBack service name constant */
  public static final String TALKBACK_SERVICE_NAME =
      "com.google.android.marvin.talkback.TalkBackService";

  /** Returns the package version code. */
  public static long getVersionCode(Context context) {
    return getVersionCodeCompat(context, context.getPackageName());
  }

  /**
   * Returns the package version code or {@link #INVALID_VERSION_CODE} if the package does not
   * exist.
   */
  public static long getVersionCodeCompat(Context context, @Nullable CharSequence packageName) {
    if (TextUtils.isEmpty(packageName)) {
      return INVALID_VERSION_CODE;
    }

    final PackageInfo packageInfo = getPackageInfo(context, packageName);

    if (packageInfo == null) {
      LogUtils.e(TAG, "Could not find package: %s", packageName);
      return INVALID_VERSION_CODE;
    }
    return FeatureSupport.supportLongVersionCode()
        ? packageInfo.getLongVersionCode()
        : packageInfo.versionCode;
  }

  /** Returns the package version name. */
  public static String getVersionName(Context context) {
    return getVersionName(context, context.getPackageName());
  }

  private static @Nullable String getVersionName(Context context, CharSequence packageName) {
    if (TextUtils.isEmpty(packageName)) {
      return null;
    }

    final PackageInfo packageInfo = getPackageInfo(context, packageName);

    if (packageInfo == null) {
      LogUtils.e(TAG, "Could not find package: %s", packageName);
      return null;
    }

    return packageInfo.versionName;
  }

  /** Returns whether the package is installed on the device. */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public static boolean hasPackage(Context context, String packageName) {
    return (getPackageInfo(context, packageName) != null);
  }

  /** Returns {@code true} if the platform has GMS core package */
  public static boolean hasGmsCorePackage(Context context) {
    return false;
  }

  /** Returns {@code true} if the package is Talkback package */
  public static boolean isTalkBackPackage(@Nullable CharSequence packageName) {
    return TextUtils.equals(packageName, TALKBACK_PACKAGE);
  }

  /** Returns {@code true} if the package supports help and feedback. */
  public static boolean supportsHelpAndFeedback(Context context) {
    return false;
  }

  private static @Nullable PackageInfo getPackageInfo(Context context, CharSequence packageName) {
    if (packageName == null) {
      return null;
    }

    final PackageManager packageManager = context.getPackageManager();

    try {
      return packageManager.getPackageInfo(packageName.toString(), 0);
    } catch (NameNotFoundException e) {
      return null;
    }
  }
}
