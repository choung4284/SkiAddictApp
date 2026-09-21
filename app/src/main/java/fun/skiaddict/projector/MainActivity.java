package fun.skiaddict.projector;

import android.app.Activity;
import android.content.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private final List<Button> navButtons=new ArrayList<>();
    private LinearLayout content;
    private TextView projectorStatus;
    private ProjectorDisplayHost projectorHost;
    private String active="home";
    private CourseStore.Category selected=CourseStore.Category.ALPINE;
    private CourseStore.Course currentCourse;
    private int[] courseValues;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(buildShell());
        projectorHost=new ProjectorDisplayHost(this,(on,name)->runOnUiThread(()->updateProjectorStatus(on)));
        showHome();
    }

    @Override protected void onResume(){ super.onResume(); if(projectorHost!=null)projectorHost.start(); }
    @Override protected void onPause(){ if(projectorHost!=null)projectorHost.stop(); super.onPause(); }

    private int dp(int v){return Ui.dp(this,v);}
    private int widthDp(){return getResources().getConfiguration().screenWidthDp;}
    private int navWidth(){return Math.max(112,Math.min(165,Math.round(widthDp()*.145f)));}
    private int paramWidth(){return Math.max(250,Math.min(390,Math.round(widthDp()*.34f)));}

    private View buildShell(){
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Ui.BG);

        root.addView(new BrandHeaderView(this),new LinearLayout.LayoutParams(-1,dp(82)));

        LinearLayout body=new LinearLayout(this);
        body.setOrientation(LinearLayout.HORIZONTAL);
        root.addView(body,new LinearLayout.LayoutParams(-1,0,1));

        body.addView(buildSidebar(),new LinearLayout.LayoutParams(dp(navWidth()),-1));

        content=new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(12),dp(10),dp(12),dp(10));
        body.addView(content,new LinearLayout.LayoutParams(0,-1,1));
        return root;
    }

    private View buildSidebar(){
        LinearLayout bar=new LinearLayout(this);bar.setOrientation(LinearLayout.VERTICAL);
        bar.setPadding(dp(8),dp(14),dp(8),dp(12));bar.setBackgroundColor(Color.WHITE);

        bar.addView(nav("⌂", "home", "home", this::showHome));
        bar.addView(nav("▥", "interactive", "interactive", this::showInteractive));
        bar.addView(nav("▤", "projector_setup", "projector", this::showProjectorSetup));
        bar.addView(nav("▦", "saved_presets", "presets", this::showPresets));
        bar.addView(nav("⚙", "settings", "settings", this::showSettings));

        View spacer=new View(this);bar.addView(spacer,new LinearLayout.LayoutParams(1,0,1));

        TextView footer=Ui.text(this,"INDOOR\nSKI CLUB\n\nMore Runs\nA Brighter You.",10,Ui.MUTED,false);
        footer.setPadding(dp(8),0,0,0);bar.addView(footer);
        return bar;
    }

    private Button nav(String icon,String key,String tag,Runnable run){
        Button b=new Button(this);b.setAllCaps(false);b.setText(icon+"   "+I18n.t(this,key));b.setTextSize(11);b.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
        b.setPadding(dp(10),0,dp(4),0);b.setTag(tag);b.setOnClickListener(v->{active=tag;styleNav();run.run();});
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(52));lp.bottomMargin=dp(6);b.setLayoutParams(lp);navButtons.add(b);return b;
    }

    private void styleNav(){
        for(Button b:navButtons){
            boolean on=active.equals(b.getTag());
            b.setTextColor(on?Color.WHITE:Ui.NAVY);
            b.setTypeface(Typeface.DEFAULT,on?Typeface.BOLD:Typeface.NORMAL);
            b.setBackground(Ui.round(this,on?Ui.RED:Color.TRANSPARENT,14,Color.TRANSPARENT));
        }
    }

    private LinearLayout pageHeader(String title,String subtitle){
        LinearLayout wrap=new LinearLayout(this);wrap.setOrientation(LinearLayout.HORIZONTAL);wrap.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout texts=new LinearLayout(this);texts.setOrientation(LinearLayout.VERTICAL);
        texts.addView(Ui.text(this,title,25,Ui.NAVY,true));
        texts.addView(Ui.text(this,subtitle,11,Color.rgb(82,105,154),false));
        wrap.addView(texts,new LinearLayout.LayoutParams(0,dp(62),1));
        projectorStatus=Ui.text(this,"● "+I18n.t(this,"projector_status")+"\n"+I18n.t(this,"not_connected"),9,Ui.MUTED,true);
        projectorStatus.setGravity(Gravity.CENTER);projectorStatus.setBackground(Ui.round(this,Color.WHITE,12,Ui.BORDER));
        wrap.addView(projectorStatus,new LinearLayout.LayoutParams(dp(150),dp(46)));
        return wrap;
    }

    private void clear(){
        content.removeAllViews();
        styleNav();
    }

    private void updateProjectorStatus(boolean on){
        if(projectorStatus==null)return;
        projectorStatus.setText("● "+I18n.t(this,"projector_status")+"\n"+I18n.t(this,on?"connected":"not_connected"));
        projectorStatus.setTextColor(on?Ui.GREEN:Ui.MUTED);
    }

    private LinearLayout card(){
        LinearLayout c=Ui.card(this);c.setPadding(dp(12),dp(10),dp(12),dp(10));return c;
    }

    private TextView small(String s){return Ui.text(this,s,10,Ui.MUTED,false);}
    private TextView title(String s){return Ui.text(this,s,14,Ui.NAVY,true);}

    // ---------------- HOME ----------------

    private void showHome(){
        active="home";clear();
        content.addView(pageHeader(I18n.t(this,"welcome"),I18n.t(this,"same_layout")),new LinearLayout.LayoutParams(-1,dp(64)));

        LinearLayout hero=card();hero.setBackground(Ui.round(this,Color.rgb(250,253,255),18,Ui.BORDER));
        hero.addView(Ui.text(this,I18n.th(this)?"เลือกคอร์สสำหรับการฝึกบนเครื่อง Indoor Ski":"Choose a training course for your Indoor Ski session",15,Ui.NAVY,true));
        hero.addView(Ui.text(this,I18n.th(this)?"Projector พร้อมแสดง Course Animation แยกจากหน้าจอควบคุม":"The projector runs course animation independently from the controller screen.",10,Ui.MUTED,false));
        LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,dp(68));hp.bottomMargin=dp(8);content.addView(hero,hp);

        LinearLayout cats=card();cats.setPadding(dp(10),dp(7),dp(10),dp(7));
        cats.addView(title(I18n.t(this,"category")));
        HorizontalScrollView hsv=new HorizontalScrollView(this);hsv.setHorizontalScrollBarEnabled(false);
        LinearLayout row=new LinearLayout(this);row.setPadding(0,dp(5),0,0);hsv.addView(row);
        for(CourseStore.Category cat:CourseStore.Category.values()){
            boolean on=cat==selected;
            LinearLayout cc=Ui.card(this);cc.setPadding(dp(10),dp(7),dp(10),dp(7));
            cc.setBackground(Ui.round(this,on?Color.rgb(255,244,246):Color.WHITE,15,on?Ui.RED:Ui.BORDER));
            cc.addView(Ui.text(this,cat.icon,19,cat==CourseStore.Category.KIDS?Ui.GOLD:Ui.RED,true));
            cc.addView(Ui.text(this,I18n.category(this,cat),11,Ui.NAVY,true));
            cc.addView(Ui.text(this,CourseStore.byCategory(cat).size()+" "+(I18n.th(this)?"คอร์ส":"courses"),9,Ui.MUTED,false));
            cc.setOnClickListener(v->{selected=cat;showHome();});
            LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(dp(Math.max(125,Math.min(165,widthDp()/6))),dp(78));cp.rightMargin=dp(7);row.addView(cc,cp);
        }
        cats.addView(hsv,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout.LayoutParams catp=new LinearLayout.LayoutParams(-1,dp(126));catp.bottomMargin=dp(8);content.addView(cats,catp);

        LinearLayout list=card();
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);
        top.addView(title(I18n.t(this,"courses")),new LinearLayout.LayoutParams(0,dp(28),1));
        top.addView(Ui.text(this,I18n.t(this,"tap_course"),9,Ui.RED,true));list.addView(top);
        ScrollView sv=new ScrollView(this);LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);sv.addView(box);
        for(CourseStore.Course c:CourseStore.byCategory(selected)){
            LinearLayout item=Ui.card(this);item.setPadding(dp(10),dp(6),dp(8),dp(6));item.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout line=new LinearLayout(this);line.setGravity(Gravity.CENTER_VERTICAL);
            TextView ic=Ui.text(this,c.category.icon,20,c.category==CourseStore.Category.KIDS?Ui.GOLD:Ui.RED,true);line.addView(ic,new LinearLayout.LayoutParams(dp(38),dp(38)));
            LinearLayout txt=new LinearLayout(this);txt.setOrientation(LinearLayout.VERTICAL);txt.addView(Ui.text(this,I18n.courseTitle(this,c),12,Ui.NAVY,true));txt.addView(Ui.text(this,I18n.courseSummary(this,c),9,Ui.MUTED,false));
            LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(0,-2,1);tp.leftMargin=dp(5);line.addView(txt,tp);
            Button open=Ui.button(this,I18n.t(this,"open"),true);line.addView(open,new LinearLayout.LayoutParams(dp(72),dp(34)));item.addView(line);
            View.OnClickListener listener=v->showCourse(c);item.setOnClickListener(listener);open.setOnClickListener(listener);
            LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(-1,dp(60));ip.bottomMargin=dp(6);box.addView(item,ip);
        }
        list.addView(sv,new LinearLayout.LayoutParams(-1,0,1));content.addView(list,new LinearLayout.LayoutParams(-1,0,1));
    }

    // ---------------- COURSE ----------------

    private void showCourse(CourseStore.Course course){
        currentCourse=course;courseValues=course.defaults.clone();clear();
        content.addView(pageHeader(I18n.courseTitle(this,course),I18n.category(this,course.category)+"  /  "+I18n.t(this,"course_detail")),new LinearLayout.LayoutParams(-1,dp(64)));

        LinearLayout meta=card();meta.setOrientation(LinearLayout.HORIZONTAL);meta.setGravity(Gravity.CENTER_VERTICAL);
        meta.addView(metric("⌁",I18n.th(this)?"ประเภทคอร์ส":"Course Type",I18n.category(this,course.category)),new LinearLayout.LayoutParams(0,-1,1));
        meta.addView(metric("▮",I18n.th(this)?"ระดับทักษะ":"Skill Level",I18n.t(this,"beginner")),new LinearLayout.LayoutParams(0,-1,1));
        meta.addView(metric("▱",I18n.th(this)?"จำนวนสเตจ":"Stages","5"),new LinearLayout.LayoutParams(0,-1,1));
        meta.addView(metric("◷",I18n.th(this)?"ระยะเวลา":"Duration","10–20 min"),new LinearLayout.LayoutParams(0,-1,1));
        LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(-1,dp(68));mp.bottomMargin=dp(8);content.addView(meta,mp);

        LinearLayout body=new LinearLayout(this);body.setOrientation(LinearLayout.HORIZONTAL);content.addView(body,new LinearLayout.LayoutParams(-1,0,1));

        LinearLayout live=card();live.addView(Ui.text(this,"▣  "+I18n.t(this,"live_view"),15,Ui.NAVY,true));
        LivePreviewView preview=new LivePreviewView(this);preview.setData(course,courseValues);live.addView(preview,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout controls=new LinearLayout(this);
        Button st=Ui.button(this,"▶  "+I18n.t(this,"start"),true);st.setOnClickListener(v->ProjectorSession.get().play());controls.addView(st,new LinearLayout.LayoutParams(0,dp(40),1));
        LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(0,dp(40),1);pp.leftMargin=dp(6);Button pa=Ui.button(this,"Ⅱ  "+I18n.t(this,"pause"),false);pa.setOnClickListener(v->ProjectorSession.get().pause());controls.addView(pa,pp);
        LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(0,dp(40),1);rp.leftMargin=dp(6);Button re=Ui.button(this,"↻  "+I18n.t(this,"reset"),false);re.setOnClickListener(v->ProjectorSession.get().reset());controls.addView(re,rp);
        LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(0,dp(40),1);fp.leftMargin=dp(6);Button back=Ui.button(this,I18n.th(this)?"← คอร์ส":"← Courses",false);back.setOnClickListener(v->showHome());controls.addView(back,fp);
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,dp(40));cp.topMargin=dp(6);live.addView(controls,cp);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-1,1);lp.rightMargin=dp(8);body.addView(live,lp);

        LinearLayout params=card();
        TextView ph=Ui.text(this,I18n.t(this,"parameters"),14,Color.WHITE,true);ph.setGravity(Gravity.CENTER_VERTICAL);ph.setPadding(dp(12),0,0,0);ph.setBackground(Ui.round(this,Ui.RED,13,Color.TRANSPARENT));params.addView(ph,new LinearLayout.LayoutParams(-1,dp(42)));
        ScrollView psv=new ScrollView(this);LinearLayout pbox=new LinearLayout(this);pbox.setOrientation(LinearLayout.VERTICAL);psv.addView(pbox);
        for(int i=0;i<course.parameterNames.length;i++){final int idx=i;slider(pbox,I18n.param(this,course.parameterNames[i]),course.min[i],course.max[i],courseValues[i],v->{courseValues[idx]=v;preview.setData(course,courseValues);ProjectorSession.get().updateValues(courseValues);});}
        params.addView(psv,new LinearLayout.LayoutParams(-1,0,1));
        body.addView(params,new LinearLayout.LayoutParams(dp(paramWidth()),-1));
        ProjectorSession.get().selectCourse(course,courseValues);
    }

    private LinearLayout metric(String icon,String label,String value){
        LinearLayout m=new LinearLayout(this);m.setGravity(Gravity.CENTER_VERTICAL);m.setPadding(dp(10),0,dp(10),0);
        TextView i=Ui.text(this,icon,20,Color.rgb(45,70,125),true);m.addView(i,new LinearLayout.LayoutParams(dp(34),dp(40)));
        LinearLayout t=new LinearLayout(this);t.setOrientation(LinearLayout.VERTICAL);t.addView(Ui.text(this,label,9,Color.rgb(76,98,147),false));t.addView(Ui.text(this,value,11,Ui.NAVY,true));m.addView(t);return m;
    }

    // ---------------- PROJECTOR ----------------

    private void showProjectorSetup(){
        active="projector";clear();
        content.addView(pageHeader(I18n.t(this,"projector_setup"),I18n.th(this)?"ปรับภาพโปรเจคเตอร์ให้ตรงกับพรมและบันทึกเป็น Preset":"Calibrate the projector to the ski mat and save machine presets."),new LinearLayout.LayoutParams(-1,dp(64)));

        final ProjectorConfig[] cfg={ProjectorConfig.active(this)};
        LinearLayout preset=card();preset.setOrientation(LinearLayout.HORIZONTAL);preset.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout left=new LinearLayout(this);left.setOrientation(LinearLayout.VERTICAL);left.addView(title("▦  "+I18n.t(this,"machine_preset")));left.addView(small(I18n.th(this)?"เลือก L หรือ XL และปรับค่าแยกกัน":"Choose L or XL; each stores its own calibration."));
        preset.addView(left,new LinearLayout.LayoutParams(0,-1,.34f));
        Spinner sp=new Spinner(this);String[] opts={"L  (4.8 × 8.5 m)","XL  (4.8 × 10.5 m)"};sp.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,opts));sp.setSelection("XL".equals(cfg[0].preset)?1:0);
        preset.addView(sp,new LinearLayout.LayoutParams(0,dp(42),.36f));
        Button apply=Ui.button(this,"✓  "+I18n.t(this,"apply"),true);preset.addView(apply,new LinearLayout.LayoutParams(0,dp(42),.24f));
        LinearLayout.LayoutParams prp=new LinearLayout.LayoutParams(-1,dp(84));prp.bottomMargin=dp(8);content.addView(preset,prp);

        LinearLayout body=new LinearLayout(this);body.setOrientation(LinearLayout.HORIZONTAL);content.addView(body,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout previewCard=card();previewCard.addView(title("▣  "+I18n.t(this,"projector_preview")));
        CalibrationPreviewView preview=new CalibrationPreviewView(this);preview.setConfig(cfg[0]);previewCard.addView(preview,new LinearLayout.LayoutParams(-1,0,1));
        TextView size=Ui.text(this,"L = 4.8 × 8.5 m     |     XL = 4.8 × 10.5 m",10,Ui.MUTED,true);size.setGravity(Gravity.CENTER);previewCard.addView(size,new LinearLayout.LayoutParams(-1,dp(32)));
        LinearLayout.LayoutParams pv=new LinearLayout.LayoutParams(0,-1,.40f);pv.rightMargin=dp(8);body.addView(previewCard,pv);

        ScrollView sv=new ScrollView(this);LinearLayout controls=new LinearLayout(this);controls.setOrientation(LinearLayout.VERTICAL);sv.addView(controls);body.addView(sv,new LinearLayout.LayoutParams(0,-1,.60f));

        Runnable rebuild=()->{controls.removeAllViews();addProjectorControls(controls,cfg[0],preview);};
        rebuild.run();
        sp.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){
            public void onItemSelected(android.widget.AdapterView<?> a,View v,int pos,long id){String p=pos==1?"XL":"L";if(!p.equals(cfg[0].preset)){cfg[0]=ProjectorConfig.load(MainActivity.this,p);preview.setConfig(cfg[0]);rebuild.run();}}
            public void onNothingSelected(android.widget.AdapterView<?> a){}
        });
        apply.setOnClickListener(v->{cfg[0].save(this);ProjectorSession.get().reset();Toast.makeText(this,(I18n.th(this)?"บันทึกและใช้งาน ":"Applied ")+cfg[0].preset,Toast.LENGTH_SHORT).show();});
    }

    private void addProjectorControls(LinearLayout out,ProjectorConfig c,CalibrationPreviewView preview){
        LinearLayout row1=new LinearLayout(this);row1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout mat=miniCard(I18n.t(this,"mat_size"));line(mat,I18n.t(this,"width"),"4.8 m");line(mat,I18n.t(this,"length"),c.length+" m");seek(mat,I18n.t(this,"safe_margin"),0,15,(int)c.safeMargin,v->{c.safeMargin=v;preview.setConfig(c);});row1.addView(mat,new LinearLayout.LayoutParams(0,-2,1));
        LinearLayout align=miniCard(I18n.t(this,"alignment"));seek(align,I18n.t(this,"x_position"),-100,100,(int)c.x,v->{c.x=v;preview.setConfig(c);});seek(align,I18n.t(this,"y_position"),-100,100,(int)c.y,v->{c.y=v;preview.setConfig(c);});seek(align,I18n.t(this,"scale_x"),70,130,(int)c.scaleX,v->{c.scaleX=v;preview.setConfig(c);});seek(align,I18n.t(this,"scale_y"),70,130,(int)c.scaleY,v->{c.scaleY=v;preview.setConfig(c);});seek(align,I18n.t(this,"rotation"),-15,15,(int)c.rotation,v->{c.rotation=v;preview.setConfig(c);});LinearLayout.LayoutParams a=new LinearLayout.LayoutParams(0,-2,1);a.leftMargin=dp(7);row1.addView(align,a);out.addView(row1);

        LinearLayout row2=new LinearLayout(this);row2.setOrientation(LinearLayout.HORIZONTAL);row2.setPadding(0,dp(7),0,0);
        LinearLayout key=miniCard(I18n.t(this,"keystone"));seek(key,I18n.t(this,"top_left")+" X",-30,30,(int)c.tlx,v->{c.tlx=v;preview.setConfig(c);});seek(key,I18n.t(this,"top_right")+" X",-30,30,(int)c.trx,v->{c.trx=v;preview.setConfig(c);});seek(key,I18n.t(this,"bottom_left")+" X",-30,30,(int)c.blx,v->{c.blx=v;preview.setConfig(c);});seek(key,I18n.t(this,"bottom_right")+" X",-30,30,(int)c.brx,v->{c.brx=v;preview.setConfig(c);});row2.addView(key,new LinearLayout.LayoutParams(0,-2,1));
        LinearLayout persp=miniCard(I18n.t(this,"perspective"));seek(persp,I18n.t(this,"perspective_strength"),0,100,(int)c.perspective,v->{c.perspective=v;preview.setConfig(c);});seek(persp,I18n.t(this,"horizon_height"),0,50,(int)c.horizon,v->{c.horizon=v;preview.setConfig(c);});seek(persp,I18n.t(this,"object_scale"),60,160,(int)c.objectScale,v->{c.objectScale=v;preview.setConfig(c);});LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(0,-2,1);pp.leftMargin=dp(7);row2.addView(persp,pp);out.addView(row2);

        LinearLayout row3=new LinearLayout(this);row3.setOrientation(LinearLayout.HORIZONTAL);row3.setPadding(0,dp(7),0,0);
        LinearLayout image=miniCard(I18n.t(this,"image"));seek(image,I18n.t(this,"brightness"),30,100,(int)c.brightness,v->c.brightness=v);seek(image,I18n.t(this,"contrast"),50,150,(int)c.contrast,v->c.contrast=v);line(image,I18n.th(this)?"พื้นหลัง":"Background",I18n.t(this,"object_only"));row3.addView(image,new LinearLayout.LayoutParams(0,-2,1));
        LinearLayout cal=miniCard(I18n.t(this,"calibration_tools"));toggle(cal,I18n.t(this,"show_grid"),c.grid,v->{c.grid=v;preview.setConfig(c);});toggle(cal,I18n.t(this,"show_corners"),c.corners,v->{c.corners=v;preview.setConfig(c);});toggle(cal,I18n.t(this,"show_boundary"),c.boundary,v->{c.boundary=v;preview.setConfig(c);});toggle(cal,I18n.t(this,"show_center"),c.center,v->{c.center=v;preview.setConfig(c);});LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(0,-2,1);cp.leftMargin=dp(7);row3.addView(cal,cp);out.addView(row3);
    }

    // ---------------- SETTINGS ----------------

    private void showSettings(){
        active="settings";clear();
        content.addView(pageHeader(I18n.t(this,"settings"),I18n.th(this)?"ปรับแต่งการใช้งาน ระบบ โปรเจคเตอร์ และโหมด Developer":"Application, display, projector, training and developer settings."),new LinearLayout.LayoutParams(-1,dp(64)));

        ScrollView scroll=new ScrollView(this);LinearLayout all=new LinearLayout(this);all.setOrientation(LinearLayout.VERTICAL);scroll.addView(all);content.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));

        LinearLayout r1=new LinearLayout(this);r1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout general=miniCard(I18n.t(this,"general"));general.addView(small(I18n.t(this,"language")));LinearLayout lang=new LinearLayout(this);Button th=Ui.button(this,"ไทย",I18n.th(this));Button en=Ui.button(this,"English",!I18n.th(this));th.setOnClickListener(v->{I18n.setLanguage(this,"th");showSettings();});en.setOnClickListener(v->{I18n.setLanguage(this,"en");showSettings();});lang.addView(th,new LinearLayout.LayoutParams(0,dp(38),1));LinearLayout.LayoutParams ep=new LinearLayout.LayoutParams(0,dp(38),1);ep.leftMargin=dp(6);lang.addView(en,ep);general.addView(lang);line(general,I18n.t(this,"units"),I18n.t(this,"metric"));line(general,I18n.t(this,"theme"),I18n.t(this,"light"));r1.addView(general,new LinearLayout.LayoutParams(0,-2,1));
        LinearLayout display=miniCard(I18n.t(this,"display_graphics"));line(display,I18n.t(this,"resolution"),"1920 × 1080");line(display,I18n.t(this,"frame_rate"),"60 FPS");line(display,I18n.t(this,"ui_brightness"),"100%");toggle(display,I18n.th(this)?"ภาพเคลื่อนไหวลื่นไหล":"Smooth Animation",true,null);LinearLayout.LayoutParams dlp=new LinearLayout.LayoutParams(0,-2,1);dlp.leftMargin=dp(7);r1.addView(display,dlp);all.addView(r1);

        LinearLayout r2=new LinearLayout(this);r2.setOrientation(LinearLayout.HORIZONTAL);r2.setPadding(0,dp(7),0,0);
        LinearLayout projector=miniCard(I18n.t(this,"projector_hardware"));toggle(projector,I18n.t(this,"auto_connect"),true,null);toggle(projector,I18n.t(this,"auto_apply_preset"),true,null);line(projector,I18n.t(this,"machine_preset"),ProjectorConfig.activePreset(this));r2.addView(projector,new LinearLayout.LayoutParams(0,-2,1));
        LinearLayout training=miniCard(I18n.t(this,"course_training"));line(training,I18n.t(this,"default_course"),I18n.courseTitle(this,CourseStore.byId("basic_gates")));line(training,I18n.t(this,"default_difficulty"),I18n.t(this,"beginner"));toggle(training,I18n.t(this,"save_history"),true,null);LinearLayout.LayoutParams tlp=new LinearLayout.LayoutParams(0,-2,1);tlp.leftMargin=dp(7);r2.addView(training,tlp);all.addView(r2);

        LinearLayout r3=new LinearLayout(this);r3.setOrientation(LinearLayout.HORIZONTAL);r3.setPadding(0,dp(7),0,0);
        LinearLayout dev=miniCard("</>  "+I18n.t(this,"developer_mode"));dev.setBackground(Ui.round(this,Color.rgb(255,247,248),16,Color.rgb(255,174,184)));dev.addView(small(I18n.t(this,"developer_note")));
        toggle(dev,I18n.t(this,"enable_developer"),DeveloperPrefs.enabled(this),DeveloperPrefs::setEnabled);
        toggle(dev,I18n.t(this,"projector_mirror"),DeveloperPrefs.mirror(this),DeveloperPrefs::setMirror);
        toggle(dev,I18n.t(this,"debug_overlay"),DeveloperPrefs.overlay(this),DeveloperPrefs::setOverlay);
        toggle(dev,I18n.t(this,"safe_area"),DeveloperPrefs.safeArea(this),DeveloperPrefs::setSafeArea);
        r3.addView(dev,new LinearLayout.LayoutParams(0,-2,1));
        LinearLayout about=miniCard(I18n.t(this,"about"));line(about,"Ski Addict","Indoor Ski Club");line(about,I18n.t(this,"app_version"),"0.8 Partner Demo");line(about,I18n.th(this)?"บิลด์":"Build","2026.09.21");LinearLayout.LayoutParams alp=new LinearLayout.LayoutParams(0,-2,1);alp.leftMargin=dp(7);r3.addView(about,alp);all.addView(r3);
    }

    // ---------------- PLACEHOLDERS ----------------

    private void showInteractive(){active="interactive";clear();content.addView(pageHeader(I18n.t(this,"interactive"),I18n.th(this)?"พื้นที่สำหรับ Camera / Sensor interaction ในเฟสถัดไป":"Camera and sensor interaction workspace for the next phase."),new LinearLayout.LayoutParams(-1,dp(64)));LinearLayout c=card();c.addView(Ui.text(this,I18n.th(this)?"Interactive Engine":"Interactive Engine",24,Ui.NAVY,true));c.addView(Ui.text(this,I18n.th(this)?"เตรียมไว้สำหรับ Reaction Lights, Balloon Pop, Scoring และ Camera Tracking":"Prepared for Reaction Lights, Balloon Pop, scoring and camera tracking.",12,Ui.MUTED,false));content.addView(c,new LinearLayout.LayoutParams(-1,0,1));}
    private void showPresets(){active="presets";clear();content.addView(pageHeader(I18n.t(this,"saved_presets"),I18n.th(this)?"เก็บค่าคอร์สและโปรเจคเตอร์สำหรับการใช้งานจริง":"Saved course and projector configurations."),new LinearLayout.LayoutParams(-1,dp(64)));LinearLayout c=card();c.addView(title("L  •  4.8 × 8.5 m"));c.addView(small(I18n.th(this)?"พรีเซ็ตเครื่องขนาด L พร้อมค่าปรับเทียบโปรเจคเตอร์":"L machine projector calibration preset"));c.addView(space(8));c.addView(title("XL  •  4.8 × 10.5 m"));c.addView(small(I18n.th(this)?"พรีเซ็ตเครื่องขนาด XL พร้อมค่าปรับเทียบโปรเจคเตอร์":"XL machine projector calibration preset"));content.addView(c,new LinearLayout.LayoutParams(-1,0,1));}

    // ---------------- UI HELPERS ----------------

    private View space(int h){View v=new View(this);v.setLayoutParams(new LinearLayout.LayoutParams(1,dp(h)));return v;}
    private LinearLayout miniCard(String h){LinearLayout c=card();c.addView(Ui.text(this,h,12,Ui.NAVY,true));return c;}
    private void line(LinearLayout c,String a,String b){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.addView(Ui.text(this,a,9,Ui.MUTED,false),new LinearLayout.LayoutParams(0,dp(30),1));r.addView(Ui.text(this,b,9,Ui.NAVY,true));c.addView(r);}
    interface IntChange{void set(int v);}
    private void slider(LinearLayout p,String name,int min,int max,int val,IntChange f){
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(dp(8),dp(5),dp(8),dp(4));box.setBackground(Ui.round(this,Color.rgb(250,252,254),11,Ui.BORDER));
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);top.addView(Ui.text(this,name,10,Ui.NAVY,true),new LinearLayout.LayoutParams(0,dp(24),1));TextView num=Ui.text(this,String.valueOf(val),11,Ui.RED,true);top.addView(num);box.addView(top);
        SeekBar s=new SeekBar(this);s.setMax(max-min);s.setProgress(val-min);s.setProgressTintList(ColorStateList.valueOf(Ui.RED));s.setThumbTintList(ColorStateList.valueOf(Ui.RED));s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int x,boolean from){int v=min+x;num.setText(String.valueOf(v));f.set(v);}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}});box.addView(s,new LinearLayout.LayoutParams(-1,dp(30)));
        LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,dp(66));bp.topMargin=dp(5);p.addView(box,bp);
    }
    private void seek(LinearLayout c,String name,int min,int max,int val,IntChange f){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.addView(Ui.text(this,name,9,Ui.MUTED,false),new LinearLayout.LayoutParams(dp(115),dp(30)));SeekBar s=new SeekBar(this);s.setMax(max-min);s.setProgress(val-min);s.setProgressTintList(ColorStateList.valueOf(Ui.RED));s.setThumbTintList(ColorStateList.valueOf(Ui.RED));TextView n=Ui.text(this,String.valueOf(val),9,Ui.NAVY,true);s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int x,boolean from){int v=min+x;n.setText(String.valueOf(v));f.set(v);}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}});r.addView(s,new LinearLayout.LayoutParams(0,dp(30),1));r.addView(n,new LinearLayout.LayoutParams(dp(42),dp(30)));c.addView(r);}
    interface BoolChange{void set(boolean b);}
    private void toggle(LinearLayout c,String name,boolean on,BoolChange f){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.addView(Ui.text(this,name,9,Ui.MUTED,false),new LinearLayout.LayoutParams(0,dp(32),1));Switch s=new Switch(this);s.setChecked(on);if(f!=null)s.setOnCheckedChangeListener((b,v)->f.set(v));r.addView(s);c.addView(r);}
    interface CtxBoolChange{void set(android.content.Context c,boolean b);}
    private void toggle(LinearLayout c,String name,boolean on,CtxBoolChange f){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.addView(Ui.text(this,name,9,Ui.MUTED,false),new LinearLayout.LayoutParams(0,dp(32),1));Switch s=new Switch(this);s.setChecked(on);if(f!=null)s.setOnCheckedChangeListener((b,v)->f.set(MainActivity.this,v));r.addView(s);c.addView(r);}
}
