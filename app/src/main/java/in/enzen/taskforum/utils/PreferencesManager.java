package in.enzen.taskforum.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Rupesh on 05-01-2018.
 */
@SuppressWarnings("ALL")
public class PreferencesManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int PRIVATE_MODE = 0;

    public PreferencesManager(Context context) {
        pref = context.getSharedPreferences(context.getPackageName(), PRIVATE_MODE);
        editor = pref.edit();

    }

    public String getString(String key) {
        return pref.getString(key, "");
    }

    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        return pref.getBoolean(key, false);
    }

    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getLoggedIn() {
        return pref.getBoolean("isLoggedIn", false);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.commit();

    }

    public String getEmail() {
        return pref.getString("email", "");

    }

    public void setEmail(String email) {
        editor.putString("email", email);
        editor.commit();
    }

}
