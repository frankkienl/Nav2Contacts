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

import static java.util.Objects.requireNonNull;

import android.text.style.ForegroundColorSpan;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.car.app.model.constraints.CarColorConstraints;

import java.util.Objects;

/**
 * A span that changes the color of the text to which the span is attached.
 *
 * <p>For example, to set a green text color to a span of a string:
 *
 * <pre>{@code
 * SpannableString string = new SpannableString("Text with a foreground color span");
 * string.setSpan(ForegroundCarColorSpan.create(CarColor.GREEN),
 *     12, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
 * }</pre>
 *
 * <p>The host may ignore the color specified in the {@link ForegroundCarColorSpan} and instead use
 * a default color unless support for {@link ForegroundCarColorSpan} is explicitly documented in the
 * API that takes the string. The host may use a default color if the color in the span does not
 * pass the contrast requirements.
 *
 * @see CarColor
 * @see ForegroundColorSpan
 */
public final class ForegroundCarColorSpan extends CarSpan {
    @Keep
    private final CarColor mCarColor;

    /** Returns the {@link CarColor} associated with this span or {@code null} if not set. */
    @Nullable
    public CarColor getColor() {
        return mCarColor;
    }

    /**
     * Creates a {@link ForegroundColorSpan} from a {@link CarColor}.
     *
     * <p>Custom colors created with {@link CarColor#createCustom} are not supported in text spans.
     *
     * @throws IllegalArgumentException if {@code carColor} contains a custom color
     * @throws NullPointerException     if {@code carColor} is {@code null}
     */
    @NonNull
    public static ForegroundCarColorSpan create(@NonNull CarColor carColor) {
        CarColorConstraints.STANDARD_ONLY.validateOrThrow(carColor);
        return new ForegroundCarColorSpan(requireNonNull(carColor));
    }

    @Override
    @NonNull
    public String toString() {
        return "[color: " + mCarColor + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mCarColor);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ForegroundCarColorSpan)) {
            return false;
        }
        ForegroundCarColorSpan otherSpan = (ForegroundCarColorSpan) other;

        return Objects.equals(mCarColor, otherSpan.mCarColor);
    }

    private ForegroundCarColorSpan(CarColor carColor) {
        mCarColor = carColor;
    }

    private ForegroundCarColorSpan() {
        mCarColor = CarColor.DEFAULT;
    }
}
