package fun.skiaddict.projector;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
    private ProjectorDisplayHost projectorHost;
    private TextView projectorStatus;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(build());
        projectorHost=new ProjectorDisplayHost(this,(on,name)->runOnUiThread(()->setProjectorStatus(on)));
    }

    @Override protected void onResume(){ super.onResume(); if(projectorHost!=null)projectorHost.start(); }
    @Override protected void onPause(){ if(projectorHost!=null)projectorHost.stop(); super.onPause(); }
    @Override public void finish(){ super.finish(); overridePendingTransition(0,0); }

    private int wdp(){return getResources().getConfiguration().screenWidthDp;}
    private int clamp(int v,int a,int b){return Math.max(a,Math.min(b,v));}
    private int nav(){return clamp(Math.round(wdp()*.14f),88,145);}

    private View build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Ui.BG);root.setPadding(Ui.dp(this,10),Ui.dp(this,6),Ui.dp(this,10),Ui.dp(this,8));
        projectorStatus=Ui.text(this,"● "+I18n.t(this,"projector_status")+"\n"+I18n.t(this,"not_connected"),9,Ui.MUTED,true);
        root.addView(Ui.header(this,projectorStatus),new LinearLayout.LayoutParams(-1,Ui.dp(this,58)));

        LinearLayout body=new LinearLayout(this);body.setOrientation(LinearLayout.HORIZONTAL);LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,0,1);bp.topMargin=Ui.dp(this,6);root.addView(body,bp);
        body.addView(Ui.sidebar(this,"settings"),new LinearLayout.LayoutParams(Ui.dp(this,nav()),-1));

        ScrollView sv=new ScrollView(this);LinearLayout center=new LinearLayout(this);center.setOrientation(LinearLayout.VERTICAL);center.setPadding(Ui.dp(this,8),0,0,0);sv.addView(center);body.addView(sv,new LinearLayout.LayoutParams(0,-1,1));
        center.addView(Ui.text(this,I18n.t(this,"settings"),22,Ui.NAVY,true),new LinearLayout.LayoutParams(-1,Ui.dp(this,46)));

        LinearLayout row1=new LinearLayout(this);row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.addView(general(),new LinearLayout.LayoutParams(0,-2,1));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1);p.leftMargin=Ui.dp(this,7);row1.addView(display(),p);center.addView(row1);

        LinearLayout row2=new LinearLayout(this);row2.setOrientation(LinearLayout.HORIZONTAL);row2.setPadding(0,Ui.dp(this,7),0,0);
        row2.addView(projector(),new LinearLayout.LayoutParams(0,-2,1));
        LinearLayout.LayoutParams q=new LinearLayout.LayoutParams(0,-2,1);q.leftMargin=Ui.dp(this,7);row2.addView(training(),q);center.addView(row2);

        LinearLayout row3=new LinearLayout(this);row3.setOrientation(LinearLayout.HORIZONTAL);row3.setPadding(0,Ui.dp(this,7),0,0);
        row3.addView(developer(),new LinearLayout.LayoutParams(0,-2,1));
        LinearLayout.LayoutParams m=new LinearLayout.LayoutParams(0,-2,1);m.leftMargin=Ui.dp(this,7);row3.addView(maintenance(),m);center.addView(row3);

        LinearLayout.LayoutParams aboutLp=new LinearLayout.LayoutParams(-1,-2);aboutLp.topMargin=Ui.dp(this,7);center.addView(about(),aboutLp);
        return root;
    }

    private LinearLayout section(String title){
        LinearLayout c=Ui.card(this);c.setPadding(Ui.dp(this,10),Ui.dp(this,7),Ui.dp(this,10),Ui.dp(this,7));c.addView(Ui.text(this,title,13,Ui.NAVY,true));return c;
    }

    private LinearLayout general(){
        LinearLayout c=section(I18n.t(this,"general"));
        c.addView(Ui.text(this,I18n.t(this,"language"),10,Ui.MUTED,false));
        LinearLayout buttons=new LinearLayout(this);
        View th=Ui.button(this,I18n.t(this,"thai"),I18n.th(this));
        View en=Ui.button(this,I18n.t(this,"english"),!I18n.th(this));
        th.setOnClickListener(v->change("th"));en.setOnClickListener(v->change("en"));
        buttons.addView(th,new LinearLayout.LayoutParams(0,Ui.dp(this,36),1));
        LinearLayout.LayoutParams ep=new LinearLayout.LayoutParams(0,Ui.dp(this,36),1);ep.leftMargin=Ui.dp(this,5);buttons.addView(en,ep);c.addView(buttons);
        line(c,I18n.t(this,"units"),I18n.t(this,"metric"));line(c,I18n.t(this,"theme"),I18n.t(this,"light"));return c;
    }

    private LinearLayout display(){
        LinearLayout c=section(I18n.t(this,"display_graphics"));
        line(c,I18n.t(this,"resolution"),"1920 × 1080");line(c,I18n.t(this,"frame_rate"),"60 FPS");line(c,I18n.t(this,"ui_brightness"),"100%");
        toggle(c,I18n.th(this)?"ภาพเคลื่อนไหวลื่นไหล":"Smooth Animation",true,null);
        return c;
    }

    private LinearLayout projector(){
        LinearLayout c=section(I18n.t(this,"projector_hardware"));
        toggle(c,I18n.t(this,"auto_connect"),true,null);toggle(c,I18n.t(this,"auto_apply_preset"),true,null);
        line(c,I18n.th(this)?"พรีเซ็ตเครื่อง":"Machine Preset",ProjectorConfig.activePreset(this));
        return c;
    }

    private LinearLayout training(){
        LinearLayout c=section(I18n.t(this,"course_training"));
        line(c,I18n.t(this,"default_course"),I18n.courseTitle(this,CourseStore.byId("basic_gates")));
        line(c,I18n.t(this,"default_difficulty"),I18n.t(this,"beginner"));
        toggle(c,I18n.t(this,"save_history"),true,null);return c;
    }

    private LinearLayout developer(){
        LinearLayout c=section(I18n.t(this,"developer_mode"));
        c.setBackground(Ui.round(this,Color.rgb(255,247,248),16,Color.rgb(255,174,184)));
        TextView note=Ui.text(this,I18n.t(this,"developer_note"),9,Ui.MUTED,false);
        note.setPadding(0,Ui.dp(this,3),0,Ui.dp(this,4));c.addView(note);

        toggle(c,I18n.t(this,"enable_developer"),DeveloperPrefs.enabled(this),v->{
            DeveloperPrefs.setEnabled(this,v);
            Toast.makeText(this,I18n.t(this,v?"developer_on":"developer_off"),Toast.LENGTH_SHORT).show();
        });
        toggle(c,I18n.t(this,"projector_mirror"),DeveloperPrefs.mirror(this),v->DeveloperPrefs.setMirror(this,v));
        toggle(c,I18n.t(this,"debug_overlay"),DeveloperPrefs.overlay(this),v->DeveloperPrefs.setOverlay(this,v));
        toggle(c,I18n.t(this,"safe_area"),DeveloperPrefs.safeArea(this),v->DeveloperPrefs.setSafeArea(this,v));
        return c;
    }

    private LinearLayout maintenance(){
        LinearLayout c=section(I18n.t(this,"maintenance"));
        line(c,I18n.t(this,"diagnostics"),I18n.th(this)?"พร้อมใช้งาน":"Ready");
        line(c,I18n.th(this)?"รีเซ็ตค่าทั้งหมด":"Reset All Settings","—");
        return c;
    }

    private LinearLayout about(){
        LinearLayout c=section(I18n.t(this,"about"));
        line(c,"Ski Addict","Indoor Ski Club");line(c,I18n.t(this,"app_version"),"0.7");line(c,I18n.th(this)?"บิลด์":"Build","2026.09.21");
        return c;
    }

    private void line(LinearLayout c,String a,String b){
        LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);
        r.addView(Ui.text(this,a,9,Ui.MUTED,false),new LinearLayout.LayoutParams(0,Ui.dp(this,31),1));
        r.addView(Ui.text(this,b,9,Ui.NAVY,true));c.addView(r);
    }

    interface BoolChange{void set(boolean v);}
    private void toggle(LinearLayout c,String name,boolean on,BoolChange action){
        LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);
        r.addView(Ui.text(this,name,9,Ui.MUTED,false),new LinearLayout.LayoutParams(0,Ui.dp(this,32),1));
        Switch s=new Switch(this);s.setChecked(on);
        if(action!=null)s.setOnCheckedChangeListener((b,v)->action.set(v));
        r.addView(s);c.addView(r);
    }

    private void change(String lang){I18n.setLanguage(this,lang);recreate();}
    private void setProjectorStatus(boolean on){
        if(projectorStatus==null)return;
        projectorStatus.setText("● "+I18n.t(this,"projector_status")+"\n"+I18n.t(this,on?"connected":"not_connected"));
        projectorStatus.setTextColor(on?Ui.GREEN:Ui.MUTED);
    }
}
