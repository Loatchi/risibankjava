package org.flower.risibank.collection;

import org.flower.risibank.user.User;
import org.jetbrains.annotations.Nullable;

import java.io.ObjectStreamException;
import java.time.LocalDateTime;
import java.util.List;

public class Collection {

    long id;
    User owner;
    String name;
    CollectionType collectionType;
    LocalDateTime creationDate;

    List<Object> medias;

    public Collection(
            long id,
            @Nullable User owner,
            String name,
            CollectionType type,
            LocalDateTime creationDate,
            @Nullable List<Object> medias //todo medias
    ){
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.collectionType = type;
        this.creationDate = creationDate;
    }

    public long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public CollectionType getCollectionType() {
        return collectionType;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }


}
