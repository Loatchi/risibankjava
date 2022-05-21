package org.flower.risibank.user;

import com.google.gson.reflect.TypeToken;
import org.flower.risibank.Utils;
import org.flower.risibank.exceptions.RisibankException;
import org.flower.risibank.exceptions.UserDoesNotExistException;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <p>
 *   A class representing a Risibank User.
 * </p>
 * <p>
 *   It gives access to all data the risibank api provides for a User.
 *   To get a user you should use {@link org.flower.risibank.user.User#byUsername(String)}.
 * </p>
 *
 * @author Loatchi
 */
public class User {

    /**
     * The regex the (custom) username must match to be valid.
     */
    public static Pattern USERNAME_REGEX = Pattern.compile("^[a-zA-Z\\d\\]\\[_-]*$");

    /**
     * Max length for a valid username.
     */
    public static int USERNAME_MAX_LEN = 30;

    /**
     * Min length for a valid username.
     */
    public static int USERNAME_MIN_LEN = 2;

    long id;
    String customUsername;
    boolean isMod;
    boolean isAdmin;
    String username;
    LocalDateTime creationDate;
    LocalDateTime lastConnectionDate;
    String collection;
    UserStat stats;
    public User(long id,
                 @NotNull String customUsername,
                 boolean isMod,
                 boolean isAdmin,
                 @NotNull String username,
                 @NotNull LocalDateTime createDate,
                 @NotNull LocalDateTime lastConnectionDate,
                 String collection, //todo collection
                 UserStat stats
                 ){
        this.id = id;
        this.customUsername = customUsername;
        this.isMod = isMod;
        this.isAdmin = isAdmin;
        this.username = username;
        this.creationDate = createDate;
        this.lastConnectionDate = lastConnectionDate;
        this.collection = collection;
        this.stats = stats;
    }

    public @NotNull Long getId() {
        return id;
    }
    public @NotNull LocalDateTime getCreationDate() {
        return creationDate;
    }
    public @NotNull LocalDateTime getLastConnectionDate() {
        return lastConnectionDate;
    }
    public String getCollection() {
        return collection;
    }
    public @NotNull String getCustomUsername() {
        return customUsername;
    }
    public UserStat getStats() {
        return stats;
    }
    public @NotNull String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return String.format("User{username=%s}", getUsername());
    }

    /**
     * Fetch a user by its username.
     *
     * @param username a username to fetch for
     * @return a user
     * @throws RisibankException if an exception happened
     *  <p>
     *      RisibankException wraps:
     *      <p>{@code IllegalArgumentException} if the username is not valid</p>
     *      <p>{@code MalformedURLException} if the url is not valid (should not happen)</p>
     *      <p>{@code UserDoesNotExistException} if the username does not link to a user</p>
     *  </p>
     */
    public static @NotNull User byUsername(String username) throws RisibankException {

        if(username.length() > USERNAME_MAX_LEN || username.length() < USERNAME_MIN_LEN)
            throw new RisibankException(
                    new IllegalArgumentException("Username : \"" + username + "\"" +
                            " should be of length L: 2 <= L <= 30"), null);

        if(!USERNAME_REGEX.matcher(username).matches())
            throw new RisibankException(
                    new IllegalArgumentException("Username \"" + username + "\" does not match the username regex: "
                            + USERNAME_REGEX.toString())
            , null);

        URL url;

        try {
            url = new URL("https://risibank.fr/api/v1/users/by-username/" + username);
        } catch (MalformedURLException e) {
            throw new RisibankException(e, null);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> json= (Map<String, Object>) Utils.getJson(url, new TypeToken<Map<String, Object>>(){}.getType());

        if(json == null)
            throw new RisibankException(new UserDoesNotExistException(username), url);

        json.put("id", ((Double)json.get("id")).longValue());

        @SuppressWarnings("unchecked")
        Map<String, Object> tmp = (Map<String, Object>) json.get("stats");
        UserStat stats = new UserStat(
                (Integer) tmp.get("media_count"),
                (Integer) tmp.get("interact_count"),
                (Integer) tmp.get("score")
        );

        return new User(
                (long) json.get("id"),
                (String) json.get("username_custom"),
                (boolean) json.get("is_mod"),
                (boolean) json.get("is_admin"),
                (String) json.get("username"),
                LocalDateTime.parse( (String) json.get("created_at"), Utils.RISI_DATE_FORMAT),
                LocalDateTime.parse( (String) json.get("last_seen_at"), Utils.RISI_DATE_FORMAT),
                "",
                stats
                );
    }

    /**
     * Fetch the top users of the risibank.
     * The data from the risibank is a sorted list that is implemented here
     * as a sorted map. Each key represents a username that can be fetched
     * with {@link User#byUsername(String)}. UserStat is the given stat for
     * each username.
     *
     * @return the map of the top users from the risibank
     * @throws RisibankException if an exception happened
     *  <p>
     *      RisibankException wraps:
     *      <p>{@code MalformedURLException} if the url is not valid (should not happen)</p>
     *      <p>{@code NullPointerException} if the json received from the risibank is null</p>
     *  </p>
     */
    public static @NotNull LinkedHashMap<String, UserStat> topUsers() throws RisibankException{

        URL url;

        try {
            url = new URL("https://risibank.fr/api/v1/users/top");
        } catch (MalformedURLException e) {
            throw new RisibankException(e, null);
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> json = (List<Map<String, Object>>)
                Utils.getJson(url, new TypeToken<List<Map<String, Object>>>(){}.getType());

        if(json == null)
            throw new RisibankException(new NullPointerException("Couldn't get the top users."), url);

        LinkedHashMap<String, UserStat> top = new LinkedHashMap<>();

        for(Map<String, Object> map : json){
            String username = (String) map.get("username_custom");
            UserStat stats = new UserStat(
                    ((Double) map.get("media_count")).intValue(),
                    ((Double) map.get("interact_count")).intValue(),
                    ((Double) map.get("score")).intValue());
            top.put(username, stats);
        }

        return top;
    }

    /**
     * Fetch a user from an id.
     * It uses 2 requests as this is not directly implemented by the risibank api.
     *
     * @param id a user id
     * @return the user matching the id
     * @throws RisibankException The same as {@link User#byUsername(String)}
     */
    public static @NotNull User byId(Long id) throws RisibankException {

        URL url;

        try {
            url = new URL("https://risibank.fr/api/v1/users/" + id + "/collections");
        } catch (MalformedURLException e) {
            throw new RisibankException(e, null);
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> json= (List<Map<String, Object>>)
                Utils.getJson(url, new TypeToken<List<Map<String, Object>>>(){}.getType());

        @SuppressWarnings("unchecked")
        String username = (String) ((Map<String, Object>)(json.get(0).get("user"))).get("username_custom");
        return User.byUsername(username);
    }
}
