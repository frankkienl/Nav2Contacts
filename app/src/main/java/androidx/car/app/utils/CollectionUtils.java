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

package androidx.car.app.utils;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Assorted collection utilities.
 *
 * @hide
 */
@RestrictTo(LIBRARY)
public final class CollectionUtils {
    /** Returns the input {@code list} if not {@code null}, or an empty list otherwise. */
    @NonNull
    public static <T> List<T> emptyIfNull(@Nullable List<T> list) {
        return list != null ? list : Collections.emptyList();
    }

    /**
     * Returns a copy of the input {@link Map} if it is not {@code null}, or {@code null} if the
     * input {@link Map} is {@code null}.
     */
    @Nullable
    public static <K, V> Map<K, V> unmodifiableCopy(@Nullable Map<K, V> map) {
        return map == null ? null : Collections.unmodifiableMap(new HashMap<>(map));
    }

    /**
     * Returns a copy of the input {@link List} if it is not {@code null}, or {@code null} if the
     * input {@link List} is {@code null}.
     */
    @Nullable
    public static <T> List<T> unmodifiableCopy(@Nullable List<T> list) {
        return list == null ? null : Collections.unmodifiableList(new ArrayList<>(list));
    }

    private CollectionUtils() {
    }
}
