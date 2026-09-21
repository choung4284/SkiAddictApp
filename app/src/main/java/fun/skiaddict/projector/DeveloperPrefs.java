package fun.skiaddict.projector;

import android.content.Context;
import android.content.SharedPreferences;

public final class DeveloperPrefs {
    private static final String PREF="skiaddict_developer";
    public static final String KEY_ENABLED="developer_enabled";
    public static final String KEY_MIRROR="projector_mirror";
    public static final String KEY_OVERLAY="debug_overlay";
    public static final String KEY_SAFE_AREA="safe_area";

    private DeveloperPrefs(){}

    public static SharedPreferences prefs(Context c){
        return c.getSharedPreferences(PREF,Context.MODE_PRIVATE);
    }

    public static boolean enabled(Context c){ return prefs(c).getBoolean(KEY_ENABLED,false); }
    public static boolean mirror(Context c){ return prefs(c).getBoolean(KEY_MIRROR,true); }
    public static boolean overlay(Context c){ return prefs(c).getBoolean(KEY_OVERLAY,true); }
    public static boolean safeArea(Context c){ return prefs(c).getBoolean(KEY_SAFE_AREA,true); }

    public static void setEnabled(Context c,boolean v){ prefs(c).edit().putBoolean(KEY_ENABLED,v).apply(); }
    public static void setMirror(Context c,boolean v){ prefs(c).edit().putBoolean(KEY_MIRROR,v).apply(); }
    public static void setOverlay(Context c,boolean v){ prefs(c).edit().putBoolean(KEY_OVERLAY,v).apply(); }
    public static void setSafeArea(Context c,boolean v){ prefs(c).edit().putBoolean(KEY_SAFE_AREA,v).apply(); }
}
