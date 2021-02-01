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

import static androidx.car.app.model.constraints.ActionsConstraints.ACTIONS_CONSTRAINTS_HEADER;
import static androidx.car.app.model.constraints.ActionsConstraints.ACTIONS_CONSTRAINTS_SIMPLE;
import static androidx.car.app.model.constraints.RowListConstraints.ROW_LIST_CONSTRAINTS_FULL_LIST;

import static java.util.Objects.requireNonNull;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.car.app.Screen;
import androidx.car.app.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A template representing a list of items.
 *
 * <h4>Template Restrictions</h4>
 *
 * In regards to template refreshes, as described in {@link Screen#onGetTemplate()}, this
 * template is considered a refresh of a previous one if:
 *
 * <ul>
 *   <li>The template title has not changed, and
 *   <li>The previous template is in a loading state (see {@link Builder#setLoading}}, or the
 *       {@link ItemList} structure between the templates have not changed. This means that if the
 *       previous template has multiple {@link ItemList} sections, the new template must have the
 *       same number of sections with the same headers. Further, the number of rows and the string
 *       contents (title, texts, not counting spans) of each row must not have changed.
 *   <li>For rows that contain a {@link Toggle}, updates to the title or texts are also allowed if
 *       the toggle state has changed between the previous and new templates.
 * </ul>
 */
public final class ListTemplate implements Template {
    @Keep
    private final boolean mIsLoading;
    @Keep
    @Nullable
    private final CarText mTitle;
    @Keep
    @Nullable
    private final Action mHeaderAction;
    @Keep
    @Nullable
    private final ItemList mSingleList;
    @Keep
    private final List<SectionedItemList> mSectionedLists;
    @Keep
    @Nullable
    private final ActionStrip mActionStrip;

    /**
     * Returns the title of the template or {@code null} if not set.
     *
     * @see Builder#setTitle(CharSequence)
     */
    @Nullable
    public CarText getTitle() {
        return mTitle;
    }

    /**
     * Returns the {@link Action} that is set to be displayed in the header of the template, or
     * {@code null} if not set.
     *
     * @see Builder#setHeaderAction(Action)
     */
    @Nullable
    public Action getHeaderAction() {
        return mHeaderAction;
    }

    /**
     * Returns the {@link ActionStrip} for this template or {@code null} if not set.
     *
     * @see Builder#setActionStrip(ActionStrip)
     */
    @Nullable
    public ActionStrip getActionStrip() {
        return mActionStrip;
    }

    /**
     * Returns whether the template is loading.
     *
     * @see Builder#setLoading(boolean)
     */
    public boolean isLoading() {
        return mIsLoading;
    }

    /**
     * Returns the {@link ItemList} instance containing the list of items to display or {@code
     * null} if one hasn't been set.
     *
     * @see Builder#setSingleList(ItemList)
     */
    @Nullable
    public ItemList getSingleList() {
        return mSingleList;
    }

    /**
     * Returns the list of {@link SectionedItemList} instances to be displayed in the template.
     *
     * @see Builder#addSectionedList(SectionedItemList)
     */
    @NonNull
    public List<SectionedItemList> getSectionedLists() {
        return CollectionUtils.emptyIfNull(mSectionedLists);
    }

    @NonNull
    @Override
    public String toString() {
        return "ListTemplate";
    }

    @Override
    public int hashCode() {
        return Objects.hash(mIsLoading, mTitle, mHeaderAction, mSingleList, mSectionedLists,
                mActionStrip);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ListTemplate)) {
            return false;
        }
        ListTemplate otherTemplate = (ListTemplate) other;

        return mIsLoading == otherTemplate.mIsLoading
                && Objects.equals(mTitle, otherTemplate.mTitle)
                && Objects.equals(mHeaderAction, otherTemplate.mHeaderAction)
                && Objects.equals(mSingleList, otherTemplate.mSingleList)
                && Objects.equals(mSectionedLists, otherTemplate.mSectionedLists)
                && Objects.equals(mActionStrip, otherTemplate.mActionStrip);
    }

    ListTemplate(Builder builder) {
        mIsLoading = builder.mIsLoading;
        mTitle = builder.mTitle;
        mHeaderAction = builder.mHeaderAction;
        mSingleList = builder.mSingleList;
        mSectionedLists = CollectionUtils.unmodifiableCopy(builder.mSectionedLists);
        mActionStrip = builder.mActionStrip;
    }

    /** Constructs an empty instance, used by serialization code. */
    private ListTemplate() {
        mIsLoading = false;
        mTitle = null;
        mHeaderAction = null;
        mSingleList = null;
        mSectionedLists = Collections.emptyList();
        mActionStrip = null;
    }

    /** A builder of {@link ListTemplate}. */
    public static final class Builder {
        boolean mIsLoading;
        @Nullable
        ItemList mSingleList;
        final List<SectionedItemList> mSectionedLists = new ArrayList<>();
        @Nullable
        CarText mTitle;
        @Nullable
        Action mHeaderAction;
        @Nullable
        ActionStrip mActionStrip;
        boolean mHasSelectableList;

        /**
         * Sets whether the template is in a loading state.
         *
         * <p>If set to {@code true}, the UI will display a loading indicator where the list content
         * would be otherwise. The caller is expected to call {@link
         * androidx.car.app.Screen#invalidate()} and send the new template content
         * to the host once the data is ready.
         *
         * <p>If set to {@code false}, the UI will display the contents of the {@link ItemList}
         * instance(s) added via {@link #setSingleList} or {@link #addSectionedList}.
         */
        @NonNull
        public Builder setLoading(boolean isLoading) {
            mIsLoading = isLoading;
            return this;
        }

        /**
         * Sets the {@link Action} that will be displayed in the header of the template, or
         * {@code null} to not display an action.
         *
         * <p>Unless set with this method, the template will not have a header action.
         *
         * <h4>Requirements</h4>
         *
         * This template only supports either one of {@link Action#APP_ICON} and
         * {@link Action#BACK} as a header {@link Action}.
         *
         * @throws IllegalArgumentException if {@code headerAction} does not meet the template's
         *                                  requirements
         * @throws NullPointerException     if {@code headerAction} is {@code null}
         */
        @NonNull
        public Builder setHeaderAction(@NonNull Action headerAction) {
            ACTIONS_CONSTRAINTS_HEADER.validateOrThrow(
                    Collections.singletonList(requireNonNull(headerAction)));
            mHeaderAction = headerAction;
            return this;
        }

        /**
         * Sets the title of the template.
         *
         * <p>Unless set with this method, the template will not have a title.
         *
         * @throws NullPointerException if {@code title} is null
         */
        @NonNull
        public Builder setTitle(@NonNull CharSequence title) {
            mTitle = CarText.create(requireNonNull(title));
            return this;
        }

        /**
         * Sets a single {@link ItemList} to show in the template.
         *
         * <p>Note that this list cannot be mixed with others added via {@link #addSectionedList}
         * . If multiple lists were previously added, they will be cleared.
         *
         * @throws NullPointerException if {@code list} is null
         */
        @NonNull
        public Builder setSingleList(@NonNull ItemList list) {
            mSingleList = requireNonNull(list);
            mSectionedLists.clear();
            mHasSelectableList = false;
            return this;
        }

        /**
         * Adds an {@link SectionedItemList} to display in the template.
         *
         * <p>Use this method to add multiple lists to the template. Each
         * {@link SectionedItemList} will be grouped under its header. These lists cannot be
         * mixed with an {@link ItemList} added via {@link #setSingleList}. If a single list was
         * previously added, it will be cleared.
         *
         * <p>If the added {@link SectionedItemList} contains a
         * {@link ItemList.OnSelectedListener}, then it cannot be added alongside other
         * {@link SectionedItemList}(s).
         *
         * @throws NullPointerException     if {@code list} or {@code header} is {@code null}
         * @throws IllegalArgumentException if {@code list} is empty, if {@code list}'s {@link
         *                                  ItemList.OnItemVisibilityChangedListener} is set, if
         *                                  {@code header} is empty, or if a selectable list is
         *                                  added alongside other lists
         */
        @NonNull
        public Builder addSectionedList(@NonNull SectionedItemList list) {
            if (requireNonNull(list).getHeader().toString().length() == 0) {
                throw new IllegalArgumentException("Header cannot be empty");
            }

            ItemList itemList = list.getItemList();
            boolean isSelectableList = itemList.getOnSelectedDelegate() != null;
            if (mHasSelectableList || (isSelectableList && !mSectionedLists.isEmpty())) {
                throw new IllegalArgumentException(
                        "A selectable list cannot be added alongside any other lists");
            }
            mHasSelectableList = isSelectableList;

            if (itemList.getItems().isEmpty()) {
                throw new IllegalArgumentException("List cannot be empty");
            }

            if (itemList.getOnItemVisibilityChangedDelegate() != null) {
                throw new IllegalArgumentException(
                        "OnItemVisibilityChangedListener in the list is disallowed");
            }

            mSingleList = null;
            mSectionedLists.add(list);
            return this;
        }

        /**
         * Sets the {@link ActionStrip} for this template or {@code null} to not display an {@link
         * ActionStrip}.
         *
         * <p>Unless set with this method, the template will not have an action strip.
         *
         * <h4>Requirements</h4>
         *
         * This template allows up to 2 {@link Action}s in its {@link ActionStrip}. Of the 2 allowed
         * {@link Action}s, one of them can contain a title as set via
         * {@link Action.Builder#setTitle}. Otherwise, only {@link Action}s with icons are allowed.
         *
         * @throws IllegalArgumentException if {@code actionStrip} does not meet the requirements
         * @throws NullPointerException     if {@code actionStrip} is {@code null}
         */
        @NonNull
        public Builder setActionStrip(@NonNull ActionStrip actionStrip) {
            ACTIONS_CONSTRAINTS_SIMPLE.validateOrThrow(requireNonNull(actionStrip).getActions());
            mActionStrip = actionStrip;
            return this;
        }

        /**
         * Constructs the template defined by this builder.
         *
         * <h4>Requirements</h4>
         *
         * This template allows up to 6 {@link Row}s total in the {@link ItemList}(s). The host will
         * ignore any items over that limit. Each {@link Row}s can add up to 2 lines of texts via
         * {@link Row.Builder#addText}.
         *
         * <p>Either a header {@link Action} or the title must be set on the template.
         *
         * @throws IllegalStateException    if the template is in a loading state but there are
         *                                  lists added or vice versa, or if the template does
         *                                  not have either a title or header {@link Action} set
         * @throws IllegalArgumentException if the added {@link ItemList}(s) do not meet the
         *                                  template's requirements
         */
        @NonNull
        public ListTemplate build() {
            boolean hasList = mSingleList != null || !mSectionedLists.isEmpty();
            if (mIsLoading == hasList) {
                throw new IllegalStateException(
                        "Template is in a loading state but lists are added, or vice versa");
            }

            if (hasList) {
                if (!mSectionedLists.isEmpty()) {
                    ROW_LIST_CONSTRAINTS_FULL_LIST.validateOrThrow(mSectionedLists);
                } else if (mSingleList != null) {
                    ROW_LIST_CONSTRAINTS_FULL_LIST.validateOrThrow(mSingleList);
                }
            }

            if (CarText.isNullOrEmpty(mTitle) && mHeaderAction == null) {
                throw new IllegalStateException("Either the title or header action must be set");
            }

            return new ListTemplate(this);
        }

        /** Returns an empty {@link Builder} instance. */
        public Builder() {
        }
    }
}
