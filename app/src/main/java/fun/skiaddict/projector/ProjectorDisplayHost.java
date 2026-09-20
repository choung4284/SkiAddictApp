package fun.skiaddict.projector;

import android.app.Activity;
import android.hardware.display.DisplayManager;
import android.view.Display;

public final class ProjectorDisplayHost implements DisplayManager.DisplayListener {
    public interface StatusListener { void onProjectorStatus(boolean connected,String displayName); }

    private final Activity activity;
    private final DisplayManager displayManager;
    private final StatusListener listener;
    private ProjectorPresentation presentation;
    private boolean registered=false;

    public ProjectorDisplayHost(Activity activity,StatusListener listener){
        this.activity=activity;
        this.listener=listener;
        this.displayManager=(DisplayManager)activity.getSystemService(Activity.DISPLAY_SERVICE);
    }

    public void start(){
        if(!registered){
            displayManager.registerDisplayListener(this,null);
            registered=true;
        }
        refresh();
    }

    public void stop(){
        if(registered){
            displayManager.unregisterDisplayListener(this);
            registered=false;
        }
        dismiss();
    }

    private void refresh(){
        Display[] displays=displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
        if(displays.length==0){
            dismiss();
            if(listener!=null) listener.onProjectorStatus(false,"");
            return;
        }

        Display target=displays[0];
        if(presentation==null || presentation.getDisplay().getDisplayId()!=target.getDisplayId()){
            dismiss();
            try{
                presentation=new ProjectorPresentation(activity,target);
                presentation.show();
            }catch(Exception e){
                presentation=null;
            }
        }

        if(listener!=null) listener.onProjectorStatus(presentation!=null,target.getName());
    }

    private void dismiss(){
        if(presentation!=null){
            try{ presentation.dismiss(); }catch(Exception ignored){}
            presentation=null;
        }
    }

    @Override public void onDisplayAdded(int displayId){ refresh(); }
    @Override public void onDisplayRemoved(int displayId){ refresh(); }
    @Override public void onDisplayChanged(int displayId){ refresh(); }
}
