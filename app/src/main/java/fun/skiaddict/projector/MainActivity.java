package fun.skiaddict.projector;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;\nimport android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private final List<Button> navButtons=new ArrayList<>();
    private DeviceProfile device;
    private LinearLayout content;
    private ScrollView contentScroll;
    private FrameLayout rootFrame;
    private View normalShell;
    private HomeMasterView homeMaster;
    private TextView projectorStatus;
    private ProjectorDisplayHost projectorHost;
    private String active="home";

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        device=DeviceProfile.detect(this);
        setContentView(buildShell());
        projectorHost=new ProjectorDisplayHost(this,(on,name)->runOnUiThread(()->updateProjectorStatus(on)));
        showHome();
    }

    @Override protected void onResume(){super.onResume();if(projectorHost!=null)projectorHost.start();}
    @Override protected void onPause(){if(projectorHost!=null)projectorHost.stop();super.onPause();}

    private int s(int v){return Ui.dp(this,device.scaled(v));}
    private int font(int v){return Math.max(8,Math.round(v*device.scale));}

    private View buildShell(){
        rootFrame=new FrameLayout(this);
        normalShell=buildNormalShell();
        rootFrame.addView(normalShell,new FrameLayout.LayoutParams(-1,-1));

        homeMaster=new HomeMasterView(this,new HomeMasterView.Actions(){
            public void interactive(){showInteractive();}
            public void projector(){showProjectorSetup();}
            public void presets(){showPresets();}
            public void settings(){showSettings();}
            public void basicGates(){showCourse(CourseStore.byId("basic_gates"));}
            public void sCurve(){showCourse(CourseStore.byId("s_curve"));}
            public void parallelTurns(){showCourse(CourseStore.byId("wide_turn"));}
            public void obstacles(){showCourse(CourseStore.byId("obstacles"));}
        });
        rootFrame.addView(homeMaster,new FrameLayout.LayoutParams(-1,-1));
        return rootFrame;
    }

    private View buildNormalShell(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Ui.BG);
        root.addView(new BrandHeaderView(this,device),new LinearLayout.LayoutParams(-1,s(device.type.equals("Phone")?70:82)));

        LinearLayout body=new LinearLayout(this);body.setOrientation(LinearLayout.HORIZONTAL);root.addView(body,new LinearLayout.LayoutParams(-1,0,1));

        ScrollView sideScroll=new ScrollView(this);sideScroll.setFillViewport(true);sideScroll.setVerticalScrollBarEnabled(true);sideScroll.setBackgroundColor(Color.WHITE);
        sideScroll.addView(buildSidebar(),new ScrollView.LayoutParams(-1,-2));body.addView(sideScroll,new LinearLayout.LayoutParams(Ui.dp(this,device.navWidth()),-1));

        contentScroll=new ScrollView(this);contentScroll.setFillViewport(false);contentScroll.setVerticalScrollBarEnabled(true);
        content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);content.setPadding(s(10),s(7),s(10),s(14));
        contentScroll.addView(content,new ScrollView.LayoutParams(-1,-2));body.addView(contentScroll,new LinearLayout.LayoutParams(0,-1,1));
        return root;
    }

    private View buildSidebar(){
        LinearLayout bar=new LinearLayout(this);bar.setOrientation(LinearLayout.VERTICAL);bar.setPadding(s(6),s(8),s(6),s(18));
        bar.addView(nav("⌂","home","home",this::showHome));
        bar.addView(nav("▥","interactive","interactive",this::showInteractive));
        bar.addView(nav("▤","projector_setup","projector",this::showProjectorSetup));
        bar.addView(nav("▦","saved_presets","presets",this::showPresets));
        bar.addView(nav("⚙","settings","settings",this::showSettings));
        TextView foot=Ui.text(this,"\nINDOOR\nSKI CLUB\n\nMore Runs\nA Brighter You.",font(9),Ui.MUTED,false);foot.setPadding(s(8),s(12),0,s(20));bar.addView(foot);
        return bar;
    }

    private Button nav(String icon,String key,String tag,Runnable action){
        Button b=new Button(this);b.setAllCaps(false);b.setText(icon+"   "+I18n.t(this,key));b.setTextSize(font(10));b.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
        b.setPadding(s(9),0,s(3),0);b.setTag(tag);b.setOnClickListener(v->{active=tag;styleNav();action.run();});
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,s(48));lp.bottomMargin=s(4);b.setLayoutParams(lp);navButtons.add(b);return b;
    }

    private void styleNav(){
        for(Button b:navButtons){boolean on=active.equals(b.getTag());b.setTextColor(on?Color.WHITE:Ui.NAVY);b.setTypeface(Typeface.DEFAULT,on?Typeface.BOLD:Typeface.NORMAL);b.setBackground(Ui.round(this,on?Ui.RED:Color.TRANSPARENT,13,Color.TRANSPARENT));}
    }

    private void clear(){
        if(normalShell!=null)normalShell.setVisibility(View.VISIBLE);
        if(homeMaster!=null)homeMaster.setVisibility(View.GONE);
        content.removeAllViews();styleNav();contentScroll.scrollTo(0,0);
    }
    private LinearLayout card(){LinearLayout c=Ui.card(this);c.setPadding(s(9),s(7),s(9),s(7));return c;}
    private LinearLayout miniCard(String title){LinearLayout c=card();c.addView(Ui.text(this,title,font(11),Ui.NAVY,true));return c;}
    private TextView muted(String t){return Ui.text(this,t,font(8),Ui.MUTED,false);}

    private LinearLayout pageHeader(String title,String subtitle){
        LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout tx=new LinearLayout(this);tx.setOrientation(LinearLayout.VERTICAL);tx.addView(Ui.text(this,title,font(22),Ui.NAVY,true));tx.addView(Ui.text(this,subtitle,font(9),Color.rgb(79,101,148),false));row.addView(tx,new LinearLayout.LayoutParams(0,s(55),1));
        projectorStatus=Ui.text(this,"● "+I18n.t(this,"projector_status")+"\n"+I18n.t(this,"not_connected"),font(8),Ui.MUTED,true);projectorStatus.setGravity(Gravity.CENTER);projectorStatus.setBackground(Ui.round(this,Color.WHITE,11,Ui.BORDER));row.addView(projectorStatus,new LinearLayout.LayoutParams(s(135),s(40)));
        return row;
    }

    private void updateProjectorStatus(boolean on){if(projectorStatus==null)return;projectorStatus.setText("● "+I18n.t(this,"projector_status")+"\n"+I18n.t(this,on?"connected":"not_connected"));projectorStatus.setTextColor(on?Ui.GREEN:Ui.MUTED);}

    // HOME module: approved master image rendered 1:1 and uniformly scaled
    private void showHome(){
        active="home";
        styleNav();
        if(normalShell!=null)normalShell.setVisibility(View.GONE);
        if(homeMaster!=null)homeMaster.setVisibility(View.VISIBLE);
    }

    private LinearLayout categoryCard(int mode,String title,String sub,boolean activeCard){
        LinearLayout c=card();c.setPadding(s(5),s(5),s(5),s(5));c.setBackground(Ui.round(this,Color.WHITE,14,activeCard?Ui.RED:Ui.BORDER));
        CourseThumbView im=new CourseThumbView(this,mode);c.addView(im,new LinearLayout.LayoutParams(-1,s(device.type.equals("Phone")?70:82)));
        LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);LinearLayout tx=new LinearLayout(this);tx.setOrientation(LinearLayout.VERTICAL);tx.addView(Ui.text(this,title,font(10),Ui.NAVY,true));tx.addView(Ui.text(this,sub,font(7),Color.rgb(91,105,128),false));r.addView(tx,new LinearLayout.LayoutParams(0,-2,1));r.addView(Ui.text(this,"›",font(18),Ui.NAVY,true));c.addView(r,new LinearLayout.LayoutParams(-1,0,1));return c;
    }
    private LinearLayout courseCard(int mode,String title,String sub,Runnable action){
        LinearLayout c=card();c.setPadding(s(5),s(5),s(5),s(5));CourseThumbView im=new CourseThumbView(this,mode);c.addView(im,new LinearLayout.LayoutParams(-1,s(device.type.equals("Phone")?84:98)));
        LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);LinearLayout tx=new LinearLayout(this);tx.setOrientation(LinearLayout.VERTICAL);tx.addView(Ui.text(this,title,font(9),Ui.NAVY,true));tx.addView(Ui.text(this,sub,font(7),Color.rgb(91,105,128),false));r.addView(tx,new LinearLayout.LayoutParams(0,-2,1));r.addView(Ui.text(this,"›",font(18),Ui.NAVY,true));c.addView(r,new LinearLayout.LayoutParams(-1,0,1));c.setOnClickListener(v->action.run());return c;
    }

    // COURSE
    private void showCourse(CourseStore.Course course){
        clear();int[] vals=course.defaults.clone();ProjectorSession.get().selectCourse(course,vals);
        content.addView(pageHeader("BASIC GATES",I18n.th(this)?"Alpine / คอร์สสำหรับผู้เริ่มต้น":"Alpine / Beginner Course"),new LinearLayout.LayoutParams(-1,s(58)));

        LinearLayout meta=card();meta.setOrientation(LinearLayout.HORIZONTAL);
        meta.addView(metric("⌁",I18n.th(this)?"ประเภทคอร์ส":"Course Type","Alpine"),weight(1));meta.addView(metric("▮",I18n.th(this)?"ระดับทักษะ":"Skill Level",I18n.t(this,"beginner")),weight(1));meta.addView(metric("▱",I18n.th(this)?"จำนวนสเตจ":"Stages","5"),weight(1));meta.addView(metric("◷",I18n.th(this)?"ระยะเวลา":"Duration","10–20 min"),weight(1));
        LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(-1,s(58));mp.bottomMargin=s(7);content.addView(meta,mp);

        LinearLayout split=new LinearLayout(this);split.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout live=card();live.addView(Ui.text(this,"▣  "+(I18n.th(this)?"มุมมองสด (LIVE VIEW)":"LIVE VIEW"),font(13),Ui.NAVY,true));
        LivePreviewView preview=new LivePreviewView(this);preview.setData(course,vals);live.addView(preview,new LinearLayout.LayoutParams(-1,s(device.type.equals("Phone")?320:390)));
        LinearLayout ctl=new LinearLayout(this);Button st=Ui.button(this,"▶  "+I18n.t(this,"start"),true);st.setOnClickListener(v->ProjectorSession.get().play());ctl.addView(st,weightHeight(1,38));Button pa=Ui.button(this,"Ⅱ  "+I18n.t(this,"pause"),false);pa.setOnClickListener(v->ProjectorSession.get().pause());LinearLayout.LayoutParams p1=weightHeight(1,38);p1.leftMargin=s(5);ctl.addView(pa,p1);Button rs=Ui.button(this,"↻  "+I18n.t(this,"reset"),false);rs.setOnClickListener(v->ProjectorSession.get().reset());LinearLayout.LayoutParams p2=weightHeight(1,38);p2.leftMargin=s(5);ctl.addView(rs,p2);Button fs=Ui.button(this,"⌗  "+(I18n.th(this)?"เต็มจอ":"Full"),false);LinearLayout.LayoutParams p3=weightHeight(1,38);p3.leftMargin=s(5);ctl.addView(fs,p3);live.addView(ctl,new LinearLayout.LayoutParams(-1,s(42)));

        LinearLayout params=card();TextView tab=Ui.text(this,"☷   "+I18n.t(this,"parameters"),font(12),Color.WHITE,true);tab.setGravity(Gravity.CENTER_VERTICAL);tab.setPadding(s(10),0,0,0);tab.setBackground(Ui.round(this,Ui.RED,11,Color.TRANSPARENT));params.addView(tab,new LinearLayout.LayoutParams(-1,s(38)));
        String[] names={"Speed","Gate Width","Gate Spacing","Gate Size","Difficulty","Stage Count"};int[] mins={0,1,2,50,1,3},maxs={100,4,12,150,5,10},def={70,2,8,100,1,5};
        for(int i=0;i<names.length;i++){final int idx=i;slider(params,I18n.param(this,names[i]),mins[i],maxs[i],def[i],v->{if(idx<vals.length){vals[idx]=v;preview.setData(course,vals);ProjectorSession.get().updateValues(vals);}});}
        LinearLayout.LayoutParams l=new LinearLayout.LayoutParams(0,-2,.62f);l.rightMargin=s(7);split.addView(live,l);split.addView(params,new LinearLayout.LayoutParams(0,-2,.38f));content.addView(split);
        content.addView(gapVertical(12));
    }

    private LinearLayout metric(String icon,String a,String b){LinearLayout m=new LinearLayout(this);m.setGravity(Gravity.CENTER_VERTICAL);m.setPadding(s(7),0,s(7),0);m.addView(Ui.text(this,icon,font(17),Color.rgb(45,70,125),true),new LinearLayout.LayoutParams(s(28),s(36)));LinearLayout tx=new LinearLayout(this);tx.setOrientation(LinearLayout.VERTICAL);tx.addView(Ui.text(this,a,font(7),Color.rgb(78,100,146),false));tx.addView(Ui.text(this,b,font(9),Ui.NAVY,true));m.addView(tx);return m;}

    // PROJECTOR
    private void showProjectorSetup(){
        active="projector";clear();final ProjectorConfig[] cfg={ProjectorConfig.active(this)};
        content.addView(pageHeader(I18n.t(this,"projector_setup"),I18n.th(this)?"ปรับตำแหน่งและขนาดภาพให้พอดีกับลานสกี":"Calibrate image position and size to the ski mat"),new LinearLayout.LayoutParams(-1,s(58)));

        LinearLayout split=new LinearLayout(this);split.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout left=miniCard(I18n.th(this)?"ตั้งค่าโปรเจคเตอร์":"Projector Settings");
        Spinner preset=new Spinner(this);String[] opts={"L  4.8 × 8.5 m","XL  4.8 × 10.5 m"};preset.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,opts));preset.setSelection("XL".equals(cfg[0].preset)?1:0);left.addView(preset,new LinearLayout.LayoutParams(-1,s(36)));
        line(left,I18n.th(this)?"สถานะโปรเจคเตอร์":"Projector Status",I18n.t(this,"connected"));line(left,I18n.th(this)?"ความละเอียด":"Resolution","1920 × 1080");
        seek(left,I18n.t(this,"x_position"),-100,100,(int)cfg[0].x,v->cfg[0].x=v);seek(left,I18n.t(this,"y_position"),-100,100,(int)cfg[0].y,v->cfg[0].y=v);seek(left,I18n.t(this,"scale_x"),70,130,(int)cfg[0].scaleX,v->cfg[0].scaleX=v);seek(left,I18n.t(this,"scale_y"),70,130,(int)cfg[0].scaleY,v->cfg[0].scaleY=v);seek(left,I18n.t(this,"rotation"),-15,15,(int)cfg[0].rotation,v->cfg[0].rotation=v);seek(left,I18n.t(this,"perspective"),0,100,(int)cfg[0].perspective,v->cfg[0].perspective=v);seek(left,I18n.t(this,"brightness"),30,100,(int)cfg[0].brightness,v->cfg[0].brightness=v);seek(left,I18n.t(this,"object_scale"),60,160,(int)cfg[0].objectScale,v->cfg[0].objectScale=v);seek(left,I18n.t(this,"safe_margin"),0,15,(int)cfg[0].safeMargin,v->cfg[0].safeMargin=v);

        LinearLayout right=miniCard(I18n.th(this)?"Keystone 4 มุม":"4-Corner Keystone");CalibrationPreviewView pv=new CalibrationPreviewView(this);pv.setConfig(cfg[0]);right.addView(pv,new LinearLayout.LayoutParams(-1,s(device.type.equals("Phone")?250:330)));
        LinearLayout keyGrid=new LinearLayout(this);keyGrid.setOrientation(LinearLayout.VERTICAL);
        seek(keyGrid,I18n.t(this,"top_left")+" X",-30,30,(int)cfg[0].tlx,v->{cfg[0].tlx=v;pv.setConfig(cfg[0]);});seek(keyGrid,I18n.t(this,"top_right")+" X",-30,30,(int)cfg[0].trx,v->{cfg[0].trx=v;pv.setConfig(cfg[0]);});seek(keyGrid,I18n.t(this,"bottom_left")+" X",-30,30,(int)cfg[0].blx,v->{cfg[0].blx=v;pv.setConfig(cfg[0]);});seek(keyGrid,I18n.t(this,"bottom_right")+" X",-30,30,(int)cfg[0].brx,v->{cfg[0].brx=v;pv.setConfig(cfg[0]);});right.addView(keyGrid);
        LinearLayout buttons=new LinearLayout(this);Button test=Ui.button(this,I18n.th(this)?"ทดสอบภาพ":"Test",false);buttons.addView(test,weightHeight(1,38));Button reset=Ui.button(this,I18n.t(this,"reset"),false);LinearLayout.LayoutParams rb=weightHeight(1,38);rb.leftMargin=s(5);buttons.addView(reset,rb);Button save=Ui.button(this,I18n.th(this)?"บันทึกพรีเซ็ต":"Save Preset",false);LinearLayout.LayoutParams sb=weightHeight(1,38);sb.leftMargin=s(5);buttons.addView(save,sb);Button apply=Ui.button(this,"✓  "+I18n.t(this,"apply"),true);LinearLayout.LayoutParams ab=weightHeight(1,38);ab.leftMargin=s(5);buttons.addView(apply,ab);right.addView(buttons,new LinearLayout.LayoutParams(-1,s(42)));
        apply.setOnClickListener(v->{cfg[0].save(this);Toast.makeText(this,I18n.t(this,"apply"),Toast.LENGTH_SHORT).show();});
        preset.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onItemSelected(android.widget.AdapterView<?> a,View v,int pos,long id){String p=pos==1?"XL":"L";if(!p.equals(cfg[0].preset)){cfg[0]=ProjectorConfig.load(MainActivity.this,p);showProjectorSetup();}}public void onNothingSelected(android.widget.AdapterView<?> a){}});

        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,.46f);lp.rightMargin=s(7);split.addView(left,lp);split.addView(right,new LinearLayout.LayoutParams(0,-2,.54f));content.addView(split);content.addView(gapVertical(12));
    }

    // SETTINGS
    private void showSettings(){
        active="settings";clear();content.addView(pageHeader(I18n.t(this,"settings"),I18n.th(this)?"การตั้งค่าระบบ โปรเจคเตอร์ เสียง คอร์ส และนักพัฒนา":"System, projector, sound, course and developer settings"),new LinearLayout.LayoutParams(-1,s(58)));

        LinearLayout r1=new LinearLayout(this);r1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout general=miniCard("⚙  "+I18n.t(this,"general"));general.addView(muted(I18n.t(this,"language")));Spinner lang=new Spinner(this);lang.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"ไทย","English"}));lang.setSelection(I18n.th(this)?0:1);lang.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onItemSelected(android.widget.AdapterView<?> a,View v,int pos,long id){boolean th=pos==0;if(th!=I18n.th(MainActivity.this)){I18n.setLanguage(MainActivity.this,th?"th":"en");showSettings();}}public void onNothingSelected(android.widget.AdapterView<?> a){}});general.addView(lang,new LinearLayout.LayoutParams(-1,s(34)));line(general,I18n.t(this,"units"),I18n.t(this,"metric"));line(general,I18n.t(this,"theme"),I18n.t(this,"light"));r1.addView(general,weight(1));
        LinearLayout display=miniCard("▣  "+I18n.t(this,"display_graphics"));line(display,I18n.t(this,"resolution"),"1920 × 1080");line(display,I18n.t(this,"frame_rate"),"60 FPS");line(display,I18n.t(this,"ui_brightness"),"100%");r1.addView(gap(7));r1.addView(display,weight(1));
        LinearLayout sound=miniCard("◖  "+(I18n.th(this)?"เสียง":"Sound"));seek(sound,I18n.th(this)?"ความดังหลัก":"Master Volume",0,100,80,v->{});seek(sound,I18n.th(this)?"เสียงเอฟเฟกต์":"Effects",0,100,70,v->{});toggle(sound,I18n.th(this)?"เปิดเสียง":"Enable Sound",true,null);r1.addView(gap(7));r1.addView(sound,weight(1));content.addView(r1);

        LinearLayout r2=new LinearLayout(this);r2.setOrientation(LinearLayout.HORIZONTAL);r2.setPadding(0,s(7),0,0);
        LinearLayout projector=miniCard("▣  "+I18n.t(this,"projector_hardware"));toggle(projector,I18n.t(this,"auto_connect"),true,null);line(projector,I18n.th(this)?"โปรเจคเตอร์เริ่มต้น":"Default Projector","HDMI 1");line(projector,I18n.th(this)?"หน่วงเวลา":"Delay","5 min");r2.addView(projector,weight(1));
        LinearLayout training=miniCard("⌁  "+I18n.t(this,"course_training"));line(training,I18n.t(this,"default_course"),"BASIC GATES");line(training,I18n.t(this,"default_difficulty"),I18n.t(this,"beginner"));toggle(training,I18n.t(this,"save_history"),true,null);r2.addView(gap(7));r2.addView(training,weight(1));
        LinearLayout data=miniCard("▦  "+(I18n.th(this)?"ข้อมูลและบัญชี":"Data & Account"));toggle(data,I18n.th(this)?"บันทึกข้อมูลอัตโนมัติ":"Auto Save",true,null);line(data,I18n.th(this)?"สำรองข้อมูล":"Backup","Ready");line(data,I18n.th(this)?"รีเซ็ตข้อมูล":"Reset Data","—");r2.addView(gap(7));r2.addView(data,weight(1));content.addView(r2);

        LinearLayout r3=new LinearLayout(this);r3.setOrientation(LinearLayout.HORIZONTAL);r3.setPadding(0,s(7),0,0);
        LinearLayout maint=miniCard("⌘  "+I18n.t(this,"maintenance"));line(maint,I18n.t(this,"diagnostics"),I18n.th(this)?"พร้อมใช้งาน":"Ready");line(maint,I18n.th(this)?"ปรับเทียบ":"Calibration","Projector Setup");r3.addView(maint,weight(1));
        LinearLayout about=miniCard("ⓘ  "+I18n.t(this,"about"));line(about,I18n.t(this,"app_version"),"1.0 Master UI");line(about,I18n.th(this)?"วันที่":"Date","2026.09.21");r3.addView(gap(7));r3.addView(about,weight(1));
        LinearLayout dev=miniCard("</>  "+I18n.t(this,"developer_mode"));dev.setBackground(Ui.round(this,Color.rgb(255,247,248),14,Color.rgb(255,174,184)));toggle(dev,I18n.t(this,"enable_developer"),DeveloperPrefs.enabled(this),v->DeveloperPrefs.setEnabled(this,v));toggle(dev,I18n.t(this,"projector_mirror"),DeveloperPrefs.mirror(this),v->DeveloperPrefs.setMirror(this,v));toggle(dev,I18n.t(this,"debug_overlay"),DeveloperPrefs.overlay(this),v->DeveloperPrefs.setOverlay(this,v));toggle(dev,I18n.t(this,"safe_area"),DeveloperPrefs.safeArea(this),v->DeveloperPrefs.setSafeArea(this,v));line(dev,I18n.th(this)?"อุปกรณ์":"Device",device.type+" / "+device.profile);r3.addView(gap(7));r3.addView(dev,weight(1));content.addView(r3);content.addView(gapVertical(14));
    }

    private void showInteractive(){active="interactive";clear();content.addView(pageHeader(I18n.t(this,"interactive"),I18n.th(this)?"พื้นที่สำหรับ Sensor / Camera interaction":"Sensor / Camera interaction"),new LinearLayout.LayoutParams(-1,s(58)));LinearLayout c=card();c.addView(Ui.text(this,"Interactive Engine",font(20),Ui.NAVY,true));c.addView(muted(I18n.th(this)?"เตรียมสำหรับ Reaction Lights, Balloon Pop และ Camera Tracking":"Prepared for Reaction Lights, Balloon Pop and Camera Tracking"));content.addView(c);}
    private void showPresets(){active="presets";clear();content.addView(pageHeader(I18n.t(this,"saved_presets"),I18n.th(this)?"พรีเซ็ตเครื่อง L และ XL":"L and XL machine presets"),new LinearLayout.LayoutParams(-1,s(58)));LinearLayout c=card();c.addView(Ui.text(this,"L  4.8 × 8.5 m",font(14),Ui.NAVY,true));c.addView(muted("Alignment / Keystone / Perspective"));c.addView(gapVertical(10));c.addView(Ui.text(this,"XL  4.8 × 10.5 m",font(14),Ui.NAVY,true));c.addView(muted("Alignment / Keystone / Perspective"));content.addView(c);}

    private LinearLayout.LayoutParams weight(float w){return new LinearLayout.LayoutParams(0,-1,w);}
    private LinearLayout.LayoutParams weightHeight(float w,int h){return new LinearLayout.LayoutParams(0,s(h),w);}
    private View gap(int v){View x=new View(this);x.setLayoutParams(new LinearLayout.LayoutParams(s(v),1));return x;}
    private View gapVertical(int v){View x=new View(this);x.setLayoutParams(new LinearLayout.LayoutParams(1,s(v)));return x;}
    private void line(LinearLayout c,String a,String b){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.addView(Ui.text(this,a,font(8),Ui.MUTED,false),new LinearLayout.LayoutParams(0,s(28),1));r.addView(Ui.text(this,b,font(8),Ui.NAVY,true));c.addView(r);}
    interface IntChange{void set(int v);}
    private void slider(LinearLayout p,String name,int min,int max,int value,IntChange f){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.setPadding(0,s(2),0,s(2));r.addView(Ui.text(this,name,font(8),Ui.NAVY,true),new LinearLayout.LayoutParams(s(100),s(28)));SeekBar sb=new SeekBar(this);sb.setMax(max-min);sb.setProgress(Math.max(0,Math.min(max-min,value-min)));sb.setProgressTintList(ColorStateList.valueOf(Ui.RED));sb.setThumbTintList(ColorStateList.valueOf(Ui.RED));TextView n=Ui.text(this,String.valueOf(value),font(8),Ui.NAVY,true);sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int x,boolean z){int v=min+x;n.setText(String.valueOf(v));f.set(v);}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}});r.addView(sb,new LinearLayout.LayoutParams(0,s(30),1));r.addView(n,new LinearLayout.LayoutParams(s(42),s(28)));p.addView(r);}
    private void seek(LinearLayout p,String name,int min,int max,int value,IntChange f){slider(p,name,min,max,value,f);}
    interface BoolChange{void set(boolean v);}
    private void toggle(LinearLayout c,String name,boolean on,BoolChange f){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.addView(Ui.text(this,name,font(8),Ui.MUTED,false),new LinearLayout.LayoutParams(0,s(30),1));Switch sw=new Switch(this);sw.setChecked(on);if(f!=null)sw.setOnCheckedChangeListener((b,v)->f.set(v));r.addView(sw);c.addView(r);}
}
