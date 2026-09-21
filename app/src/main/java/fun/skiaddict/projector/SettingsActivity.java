package fun.skiaddict.projector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends Activity {
    @Override protected void onCreate(Bundle b){super.onCreate(b);setContentView(build());}

    private int wdp(){return getResources().getConfiguration().screenWidthDp;} private int clamp(int v,int a,int b){return Math.max(a,Math.min(b,v));}
    private int nav(){return clamp(Math.round(wdp()*.14f),88,145);}

    private View build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Ui.BG);root.setPadding(Ui.dp(this,10),Ui.dp(this,6),Ui.dp(this,10),Ui.dp(this,8));
        root.addView(Ui.header(this,null),new LinearLayout.LayoutParams(-1,Ui.dp(this,58)));
        LinearLayout body=new LinearLayout(this);body.setOrientation(LinearLayout.HORIZONTAL);LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,0,1);bp.topMargin=Ui.dp(this,6);root.addView(body,bp);
        body.addView(Ui.sidebar(this,"settings"),new LinearLayout.LayoutParams(Ui.dp(this,nav()),-1));
        ScrollView sv=new ScrollView(this);LinearLayout center=new LinearLayout(this);center.setOrientation(LinearLayout.VERTICAL);center.setPadding(Ui.dp(this,8),0,0,0);sv.addView(center);body.addView(sv,new LinearLayout.LayoutParams(0,-1,1));
        center.addView(Ui.text(this,I18n.t(this,"settings"),22,Ui.NAVY,true),new LinearLayout.LayoutParams(-1,Ui.dp(this,46)));

        LinearLayout row1=new LinearLayout(this);row1.setOrientation(LinearLayout.HORIZONTAL);row1.addView(general(),new LinearLayout.LayoutParams(0,-2,1));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1);p.leftMargin=Ui.dp(this,7);row1.addView(display(),p);center.addView(row1);
        LinearLayout row2=new LinearLayout(this);row2.setOrientation(LinearLayout.HORIZONTAL);row2.setPadding(0,Ui.dp(this,7),0,0);row2.addView(projector(),new LinearLayout.LayoutParams(0,-2,1));LinearLayout.LayoutParams q=new LinearLayout.LayoutParams(0,-2,1);q.leftMargin=Ui.dp(this,7);row2.addView(training(),q);center.addView(row2);
        LinearLayout row3=new LinearLayout(this);row3.setOrientation(LinearLayout.HORIZONTAL);row3.setPadding(0,Ui.dp(this,7),0,0);row3.addView(maintenance(),new LinearLayout.LayoutParams(0,-2,1));LinearLayout.LayoutParams r=new LinearLayout.LayoutParams(0,-2,1);r.leftMargin=Ui.dp(this,7);row3.addView(about(),r);center.addView(row3);
        return root;
    }

    private LinearLayout section(String title){LinearLayout c=Ui.card(this);c.setPadding(Ui.dp(this,10),Ui.dp(this,7),Ui.dp(this,10),Ui.dp(this,7));c.addView(Ui.text(this,title,13,Ui.NAVY,true));return c;}
    private LinearLayout general(){LinearLayout c=section(I18n.t(this,"general"));TextView l=Ui.text(this,I18n.t(this,"language"),10,Ui.MUTED,false);c.addView(l);LinearLayout buttons=new LinearLayout(this);View th=Ui.button(this,I18n.t(this,"thai"),I18n.th(this));View en=Ui.button(this,I18n.t(this,"english"),!I18n.th(this));th.setOnClickListener(v->change("th"));en.setOnClickListener(v->change("en"));buttons.addView(th,new LinearLayout.LayoutParams(0,Ui.dp(this,36),1));LinearLayout.LayoutParams ep=new LinearLayout.LayoutParams(0,Ui.dp(this,36),1);ep.leftMargin=Ui.dp(this,5);buttons.addView(en,ep);c.addView(buttons);line(c,I18n.t(this,"units"),I18n.t(this,"metric"));line(c,I18n.t(this,"theme"),I18n.t(this,"light"));return c;}
    private LinearLayout display(){LinearLayout c=section(I18n.t(this,"display_graphics"));line(c,I18n.t(this,"resolution"),"1920 × 1080");line(c,I18n.t(this,"frame_rate"),"60 FPS");line(c,I18n.t(this,"ui_brightness"),"100%");toggle(c,I18n.th(this)?"ภาพเคลื่อนไหวลื่นไหล":"Smooth Animation",true);return c;}
    private LinearLayout projector(){LinearLayout c=section(I18n.t(this,"projector_hardware"));toggle(c,I18n.t(this,"auto_connect"),true);toggle(c,I18n.t(this,"auto_apply_preset"),true);line(c,I18n.th(this)?"พรีเซ็ตเครื่อง":"Machine Preset",ProjectorConfig.activePreset(this));return c;}
    private LinearLayout training(){LinearLayout c=section(I18n.t(this,"course_training"));line(c,I18n.t(this,"default_course"),I18n.courseTitle(this,CourseStore.byId("basic_gates")));line(c,I18n.t(this,"default_difficulty"),I18n.t(this,"beginner"));toggle(c,I18n.t(this,"save_history"),true);return c;}
    private LinearLayout maintenance(){LinearLayout c=section(I18n.t(this,"maintenance"));line(c,I18n.t(this,"diagnostics"),I18n.th(this)?"พร้อมใช้งาน":"Ready");line(c,I18n.th(this)?"รีเซ็ตค่าทั้งหมด":"Reset All Settings","—");return c;}
    private LinearLayout about(){LinearLayout c=section(I18n.t(this,"about"));line(c,"Ski Addict","Indoor Ski Club");line(c,I18n.t(this,"app_version"),"0.6");line(c,I18n.th(this)?"บิลด์":"Build","2026.09.21");return c;}
    private void line(LinearLayout c,String a,String b){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.addView(Ui.text(this,a,9,Ui.MUTED,false),new LinearLayout.LayoutParams(0,Ui.dp(this,31),1));r.addView(Ui.text(this,b,9,Ui.NAVY,true));c.addView(r);}
    private void toggle(LinearLayout c,String name,boolean on){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.addView(Ui.text(this,name,9,Ui.MUTED,false),new LinearLayout.LayoutParams(0,Ui.dp(this,32),1));Switch s=new Switch(this);s.setChecked(on);r.addView(s);c.addView(r);}
    private void change(String lang){I18n.setLanguage(this,lang);recreate();}
}
