/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.car.app;

import androidx.car.app.IOnDoneCallback;

/** @hide */
interface IStartCarApp {
   /**
   * Starts the car app on the car screen.
   *
   * <p>Use this method if the app has received a broadcast due to a notification action.
   */
   void startCarApp(in Intent startCarAppIntent) = 1;
}
