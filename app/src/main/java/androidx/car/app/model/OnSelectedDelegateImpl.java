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
import androidx.car.app.model.ItemList.OnSelectedListener;
import androidx.car.app.utils.RemoteUtils;

/**
 * Implementation class for {@link OnSelectedDelegate}.
 *
 * @hide
 */
@RestrictTo(LIBRARY)
public class OnSelectedDelegateImpl implements OnSelectedDelegate {

    @Keep
    private final IOnSelectedListener mStub;

    @Override
    public void sendSelected(int selectedIndex, @NonNull OnDoneCallback callback) {
        try {
            mStub.onSelected(selectedIndex,
                    RemoteUtils.createOnDoneCallbackStub(callback));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private OnSelectedDelegateImpl(@NonNull OnSelectedListener listener) {
        mStub = new OnSelectedListenerStub(listener);
    }

    /** For serialization. */
    private OnSelectedDelegateImpl() {
        mStub = null;
    }

    @NonNull
    // This listener relates to UI event and is expected to be triggered on the main thread.
    @SuppressLint("ExecutorRegistration")
    static OnSelectedDelegate create(@NonNull OnSelectedListener listener) {
        return new OnSelectedDelegateImpl(listener);
    }

    @Keep // We need to keep these stub for Bundler serialization logic.
    private static class OnSelectedListenerStub extends IOnSelectedListener.Stub {
        private final OnSelectedListener mListener;

        OnSelectedListenerStub(OnSelectedListener listener) {
            mListener = listener;
        }

        @Override
        public void onSelected(int index, IOnDoneCallback callback) {
            RemoteUtils.dispatchHostCall(
                    () -> mListener.onSelected(index), callback, "onSelectedListener");
        }
    }
}
