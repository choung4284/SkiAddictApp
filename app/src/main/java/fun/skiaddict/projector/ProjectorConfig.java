package fun.skiaddict.projector;

import android.content.Context;
import android.content.SharedPreferences;

public final class ProjectorConfig {
    public String preset="L";
    public float width=4.8f, length=8.5f, safeMargin=5f;
    public float x=0f,y=0f,scaleX=100f,scaleY=100f,rotation=0f;
    public float perspective=45f,horizon=18f,vanishX=50f,vanishY=0f;
    public float brightness=100f,contrast=100f,objectScale=100f;
    public float tlx=0f,tly=0f,trx=0f,tryy=0f,blx=0f,bly=0f,brx=0f,bry=0f;
    public boolean grid=false,corners=false,boundary=false,center=false;

    private static String k(String preset,String key){ return "proj_"+preset+"_"+key; }

    public static ProjectorConfig load(Context c,String preset){
        SharedPreferences p=c.getSharedPreferences("skiaddict_projector",Context.MODE_PRIVATE);
        ProjectorConfig d=new ProjectorConfig();
        d.preset=preset;
        d.width=4.8f;
        d.length="XL".equals(preset)?10.5f:8.5f;
        d.safeMargin=p.getFloat(k(preset,"safe"),5f);
        d.x=p.getFloat(k(preset,"x"),0f); d.y=p.getFloat(k(preset,"y"),0f);
        d.scaleX=p.getFloat(k(preset,"sx"),100f); d.scaleY=p.getFloat(k(preset,"sy"),100f);
        d.rotation=p.getFloat(k(preset,"rot"),0f);
        d.perspective=p.getFloat(k(preset,"persp"),45f); d.horizon=p.getFloat(k(preset,"horizon"),18f);
        d.vanishX=p.getFloat(k(preset,"vx"),50f); d.vanishY=p.getFloat(k(preset,"vy"),0f);
        d.brightness=p.getFloat(k(preset,"bright"),100f); d.contrast=p.getFloat(k(preset,"contrast"),100f);
        d.objectScale=p.getFloat(k(preset,"obj"),100f);
        d.tlx=p.getFloat(k(preset,"tlx"),0f); d.tly=p.getFloat(k(preset,"tly"),0f);
        d.trx=p.getFloat(k(preset,"trx"),0f); d.tryy=p.getFloat(k(preset,"try"),0f);
        d.blx=p.getFloat(k(preset,"blx"),0f); d.bly=p.getFloat(k(preset,"bly"),0f);
        d.brx=p.getFloat(k(preset,"brx"),0f); d.bry=p.getFloat(k(preset,"bry"),0f);
        d.grid=p.getBoolean(k(preset,"grid"),false); d.corners=p.getBoolean(k(preset,"corners"),false);
        d.boundary=p.getBoolean(k(preset,"boundary"),false); d.center=p.getBoolean(k(preset,"center"),false);
        return d;
    }

    public void save(Context c){
        SharedPreferences.Editor e=c.getSharedPreferences("skiaddict_projector",Context.MODE_PRIVATE).edit();
        e.putString("active_preset",preset);
        e.putFloat(k(preset,"safe"),safeMargin);
        e.putFloat(k(preset,"x"),x).putFloat(k(preset,"y"),y);
        e.putFloat(k(preset,"sx"),scaleX).putFloat(k(preset,"sy"),scaleY).putFloat(k(preset,"rot"),rotation);
        e.putFloat(k(preset,"persp"),perspective).putFloat(k(preset,"horizon"),horizon);
        e.putFloat(k(preset,"vx"),vanishX).putFloat(k(preset,"vy"),vanishY);
        e.putFloat(k(preset,"bright"),brightness).putFloat(k(preset,"contrast"),contrast).putFloat(k(preset,"obj"),objectScale);
        e.putFloat(k(preset,"tlx"),tlx).putFloat(k(preset,"tly"),tly).putFloat(k(preset,"trx"),trx).putFloat(k(preset,"try"),tryy);
        e.putFloat(k(preset,"blx"),blx).putFloat(k(preset,"bly"),bly).putFloat(k(preset,"brx"),brx).putFloat(k(preset,"bry"),bry);
        e.putBoolean(k(preset,"grid"),grid).putBoolean(k(preset,"corners"),corners).putBoolean(k(preset,"boundary"),boundary).putBoolean(k(preset,"center"),center);
        e.apply();
    }

    public static String activePreset(Context c){
        return c.getSharedPreferences("skiaddict_projector",Context.MODE_PRIVATE).getString("active_preset","L");
    }

    public static ProjectorConfig active(Context c){ return load(c,activePreset(c)); }
}
