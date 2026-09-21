package fun.skiaddict.projector;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

public final class DeviceProfile {
    public final String type, profile;
    public final int widthDp, heightDp, smallestDp, widthPx, heightPx;
    public final float aspect, scale;

    private DeviceProfile(String type,String profile,int wdp,int hdp,int sdp,int wpx,int hpx,float aspect,float scale){
        this.type=type;this.profile=profile;this.widthDp=wdp;this.heightDp=hdp;this.smallestDp=sdp;
        this.widthPx=wpx;this.heightPx=hpx;this.aspect=aspect;this.scale=scale;
    }

    public static DeviceProfile detect(Activity a){
        Configuration c=a.getResources().getConfiguration();
        DisplayMetrics m=a.getResources().getDisplayMetrics();
        int wdp=c.screenWidthDp, hdp=c.screenHeightDp, sdp=c.smallestScreenWidthDp;
        int wpx=m.widthPixels, hpx=m.heightPixels;
        boolean tablet=sdp>=600;
        String type=tablet?"Tablet":"Phone";
        String profile; float scale;
        if(!tablet){profile="PHONE_LANDSCAPE";scale=.82f;}
        else if(sdp<720){profile="SMALL_TABLET";scale=.92f;}
        else{profile="LARGE_TABLET";scale=1f;}
        return new DeviceProfile(type,profile,wdp,hdp,sdp,wpx,hpx,hpx==0?1f:wpx/(float)hpx,scale);
    }

    public int scaled(int base){return Math.max(1,Math.round(base*scale));}
    public int navWidth(){return Math.max(96,Math.min(175,Math.round(widthDp*(type.equals("Phone")?.13f:.145f))));}
}
