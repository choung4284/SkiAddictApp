package fun.skiaddict.projector;

import android.app.Activity;
import android.app.Presentation;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public final class ProjectorPresentation extends Presentation implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final Activity sourceActivity;
    private FrameLayout root;
    private ProjectorView courseView;
    private ProjectorMirrorView mirrorView;

    public ProjectorPresentation(Activity activity,Display display){
        super(activity,display);
        sourceActivity=activity;
    }

    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Window w=getWindow();
        if(w!=null){
            w.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            w.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        root=new FrameLayout(getContext());
        courseView=new ProjectorView(getContext(),ProjectorSession.get());
        mirrorView=new ProjectorMirrorView(sourceActivity);
        root.addView(courseView,new FrameLayout.LayoutParams(-1,-1));
        root.addView(mirrorView,new FrameLayout.LayoutParams(-1,-1));
        setContentView(root);

        DeveloperPrefs.prefs(sourceActivity).registerOnSharedPreferenceChangeListener(this);
        updateMode();
    }

    private void updateMode(){
        boolean mirror=DeveloperPrefs.enabled(sourceActivity) && DeveloperPrefs.mirror(sourceActivity);
        if(courseView!=null) courseView.setVisibility(mirror?View.GONE:View.VISIBLE);
        if(mirrorView!=null) mirrorView.setVisibility(mirror?View.VISIBLE:View.GONE);
        if(mirrorView!=null) mirrorView.invalidate();
    }

    @Override public void onSharedPreferenceChanged(SharedPreferences prefs,String key){
        updateMode();
    }

    @Override public void dismiss(){
        try{ DeveloperPrefs.prefs(sourceActivity).unregisterOnSharedPreferenceChangeListener(this); }catch(Throwable ignored){}
        super.dismiss();
    }
}
