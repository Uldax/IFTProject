package ca.udes.bonc.ift_project.communication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Helper class that retrieves HTML or text from an HTTP address and lets you specify two event handlers, a method
 * that is called on a separate thread to parse the HTML/text and a method that is called on the UI thread to
 * update the user interface  UI updated method.
 */
public class HttpHelper {

    public static final String LOCALHOST = "http://10.0.3.2:8080";

    /**
     * Read all HTML or text from the input stream using the specified text encoding
     * @param input The stream to read text from
     * @param encoding The encoding of the stream
     * @return All text read from the stream
     */
    public static String readAll(InputStream input, String encoding) {
        try {
            InputStreamReader reader = new InputStreamReader(input, encoding);
            StringBuilder result = new StringBuilder();
            char[] buffer = new char[8192];
            int len;
            while ((len = reader.read(buffer, 0, buffer.length)) > 0) {
                result.append(buffer, 0, len);
            }
            reader.close();
            return result.toString();
        }
        catch (IOException ignored) {
        }
        return null;
    }

    public static JSONObject readAllJSON(InputStream input, String encoding) throws JSONException{
        //Convert to JSON
        String html =  readAll(input, encoding);
       return new JSONObject(html);
    }


    /**
     * Find out and return what type of text encoding is specified by the server
     * @param conn The opened HTTP connection to fetch the encoding for
     * @return The string name of the encoding. utf-8 is the default.
     */
    public static String getEncoding(HttpURLConnection conn) {
        String encoding = "utf-8";
        String contentType = conn.getHeaderField("Content-Type").toLowerCase();
        if (contentType.contains("charset=")) {
            int found = contentType.indexOf("charset=");
            encoding = contentType.substring(found + 8, contentType.length()).trim();
        }
        else if (conn.getContentEncoding() != null) {
            encoding = conn.getContentEncoding();
        }
        return encoding;
    }
}

