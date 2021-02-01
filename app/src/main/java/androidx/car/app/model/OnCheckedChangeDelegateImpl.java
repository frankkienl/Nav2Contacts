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

package androidx.car.app.model;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.annotation.SuppressLint;
import android.os.RemoteException;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.car.app.IOnDoneCallback;
import androidx.car.app.OnDoneCallback;
import androidx.car.app.model.Toggle.OnCheckedChangeListener;
import androidx.car.app.utils.RemoteUtils;

/**
 * Implementation class for {@link OnCheckedChangeDelegate}.
 *
 * @hide
 */
@RestrictTo(LIBRARY)
public class OnCheckedChangeDelegateImpl implements OnCheckedChangeDelegate {

    @Keep
    private final IOnCheckedChangeListener mStub;

    @Override
    public void sendCheckedChange(boolean isChecked, @NonNull OnDoneCallback callback) {
        try {
            mStub.onCheckedChange(isChecked,
                    RemoteUtils.createOnDoneCallbackStub(callback));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private OnCheckedChangeDelegateImpl(@NonNull OnCheckedChangeListener listener) {
        mStub = new OnCheckedChangeListenerStub(listener);
    }

    /** For serialization. */
    private OnCheckedChangeDelegateImpl() {
        mStub = null;
    }

    @NonNull
    // This listener relates to UI event and is expected to be triggered on the main thread.
    @SuppressLint("ExecutorRegistration")
    static OnCheckedChangeDelegate create(@NonNull OnCheckedChangeListener listener) {
        return new OnCheckedChangeDelegateImpl(listener);
    }

    @Keep // We need to keep these stub for Bundler serialization logic.
    private static class OnCheckedChangeListenerStub extends IOnCheckedChangeListener.Stub {
        private final OnCheckedChangeListener mListener;

        OnCheckedChangeListenerStub(OnCheckedChangeListener listener) {
            mListener = listener;
        }

        @Override
        public void onCheckedChange(boolean isChecked, IOnDoneCallback callback) {
            RemoteUtils.dispatchHostCall(
                    () -> mListener.onCheckedChange(isChecked), callback,
                    "onCheckedChange");
        }
    }
}
