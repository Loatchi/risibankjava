package org.flower.risibank.user;

import com.google.gson.reflect.TypeToken;
import org.flower.risibank.Utils;
import org.flower.risibank.exceptions.RisibankException;
import org.flower.risibank.media.Media;
import org.flower.risibank.media.MediaCategory;
import org.flower.risibank.media.MediaPartial;
import org.flower.risibank.media.MediaSourceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class UserMediaFlow implements Iterator<List<MediaPartial>> {

    UserPartial owner;

    boolean hasNext = true;
    int page = 0;

    public UserMediaFlow(UserPartial owner){
        this.owner = owner;
    }

    public UserPartial getOwner() {
        return owner;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public @NotNull List<MediaPartial> next() {
        URL url;

        page++;

        try {
            url = new URL("https://risibank.fr/api/v1/users/" + owner.id + "/medias?page=" + page);
        } catch (MalformedURLException e) {
            throw new NoSuchElementException();
        }

        Map<String, Object> json;

        try {
            //noinspection unchecked
            json = (Map<String, Object>) Utils.getJson(url, new TypeToken<Map<String, Object>>(){}.getType());

            if(json == null)
                throw new NoSuchElementException();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> medias = (List<Map<String, Object>>) json.get("medias");

            List<MediaPartial> nextMediaPartials = new ArrayList<>();

            for(Map<String, Object> map : medias){
                MediaPartial mediaPartial = new MediaPartial(
                        ((Double) map.get("id")).longValue(),
                        ((Double) map.get("user_id")).longValue(),
                        new URL((String) map.get("cache_url")),
                        MediaCategory.valueOf(((String)map.get("category")).toUpperCase()),
                        (String) map.get("slug"),
                        ((Double) map.get("interact_count")).intValue(),
                        ((Double) map.get("score")).intValue(),
                        LocalDateTime.parse( (String) map.get("created_at"), Utils.RISI_DATE_FORMAT),
                        new URL((String) map.get("source_url")),
                        MediaSourceType.valueOf(((String) map.get("source_type")).toUpperCase()),
                        (Boolean) map.get("source_exists"),
                        (Boolean) map.get("is_deleted")
                );
                nextMediaPartials.add(mediaPartial);
            }

            hasNext = (Boolean) json.get("has_more");

            return nextMediaPartials;

        } catch (RisibankException | MalformedURLException e) {
            throw new NoSuchElementException();
        }

    }
}
