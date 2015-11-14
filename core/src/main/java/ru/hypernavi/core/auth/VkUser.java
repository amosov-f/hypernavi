package ru.hypernavi.core.auth;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Created by amosov-f on 14.11.15.
 */
public final class VkUser {
    private final int uid;
    @NotNull
    private final String firstName;
    @NotNull
    private final String lastName;
    @NotNull
    private final String photo;
    @NotNull
    private final String photoRec;

    private VkUser(@NotNull final Builder builder) {
        this.uid = builder.uid;
        this.firstName = Objects.requireNonNull(builder.firstName, "No first name!");
        this.lastName = Objects.requireNonNull(builder.lastName, "No last name!");
        this.photo = Objects.requireNonNull(builder.photo, "No photo!");
        this.photoRec = Objects.requireNonNull(builder.photoRec, "No photo rec!");
    }

    public int getUid() {
        return uid;
    }

    @NotNull
    public String getFirstName() {
        return firstName;
    }

    @NotNull
    public String getLastName() {
        return lastName;
    }

    @NotNull
    public String getPhoto() {
        return photo;
    }

    @NotNull
    public String getPhotoRec() {
        return photoRec;
    }

    public static final class Builder {
        private final int uid;
        @Nullable
        private String firstName;
        @Nullable
        private String lastName;
        @Nullable
        private String photo;
        @Nullable
        private String photoRec;

        public Builder(final int uid) {
            this.uid = uid;
        }

        @NotNull
        public Builder firstName(@NotNull final String firstName) {
            this.firstName = firstName;
            return this;
        }

        @NotNull
        public Builder lastName(@NotNull final String lastName) {
            this.lastName = lastName;
            return this;
        }

        @NotNull
        public Builder photo(@NotNull final String photo) {
            this.photo = photo;
            return this;
        }

        @NotNull
        public Builder photoRec(@NotNull final String photoRec) {
            this.photoRec = photoRec;
            return this;
        }

        @NotNull
        public VkUser build() {
            return new VkUser(this);
        }
    }
}
