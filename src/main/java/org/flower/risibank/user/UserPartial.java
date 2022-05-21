package org.flower.risibank.user;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UserPartial {

    long id;
    String customUsername;
    boolean isMod;
    boolean isAdmin;

    public UserPartial(long id,
                       @NotNull String customUsername,
                       boolean isMod,
                       boolean isAdmin){
        this.id = id;
        this.customUsername = customUsername;
        this.isMod = isMod;
        this.isAdmin = isAdmin;
    }

    public long getId() {
        return id;
    }

    public String getCustomUsername() {
        return customUsername;
    }

    public boolean isMod() {
        return isMod;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPartial)) return false;
        UserPartial user = (UserPartial) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isPartial(){
        return !(this instanceof User);
    }
}
