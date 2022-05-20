package org.flower.risibank;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.flower.risibank.exceptions.RisibankException;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

/**
 * Some tools.
 */
public class Utils {
    public static DateTimeFormatter RISI_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSS]");

    public static @Nullable Object getJson(URL url, Type type) throws RisibankException {

        HttpURLConnection connection = null;
        Object json;

        try{
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            json = gson.fromJson(jsonText, type);
            connection.disconnect();

        } catch (IOException | JsonSyntaxException e) {
            if(connection != null)
                connection.disconnect();
            throw new RisibankException(e, url);
        }

        return json;

    }

    private static String readAll(BufferedReader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}
