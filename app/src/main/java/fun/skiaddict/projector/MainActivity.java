package fun.skiaddict.projector;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
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
import java.util.Locale;

public class MainActivity extends Activity {
    private final List<Button> navButtons=new ArrayList<>();
    private DeviceProfile device;
    private LinearLayout content;
    private ScrollView contentScroll;
    private TextView projectorStatus;
    private ProjectorDisplayHost projectorHost;
    private String active="home";
    private CourseStore.Category selected=CourseStore.Category.ALPINE;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);device=DeviceProfile.detect(this);setContentView(buildShell());
        projectorHost=new ProjectorDisplayHost(this,(on,name)->runOnUiThread(()->updateProjectorStatus(on)));
        showHome();
    }
    @Override protected void onResume(){super.onResume();if(projectorHost!=null)projectorHost.start();}
    @Override protected void onPause(){if(projectorHost!=null)projectorHost.stop();super.onPause();}

    private int dp(int v){return Ui.dp(this,v);}
    private int s(int v){return Ui.dp(this,device.scaled(v));}
    private int font(int v){return Math.max(8,Math.round(v*device.scale));}

    private View buildShell(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Ui.BG);
        root.addView(new BrandHeaderView(this,device),new LinearLayout.LayoutParams(-1,s(device.type.equals("Phone")?74:88)));
        LinearLayout body=new LinearLayout(this);body.setOrientation(LinearLayout.HORIZONTAL);root.addView(body,new LinearLayout.LayoutParams(-1,0,1));

        ScrollView sidebar=new ScrollView(this);sidebar.setFillViewport(true);sidebar.setVerticalScrollBarEnabled(true);sidebar.addView(buildSidebar(),new ScrollView.LayoutParams(-1,-2));
        body.addView(sidebar,new LinearLayout.LayoutParams(dp(device.navWidth()),-1));

        contentScroll=new ScrollView(this);contentScroll.setFillViewport(false);contentScroll.setVerticalScrollBarEnabled(true);
        content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);content.setPadding(s(12),s(9),s(12),s(18));
        contentScroll.addView(content,new ScrollView.LayoutParams(-1,-2));body.addView(contentScroll,new LinearLayout.LayoutParams(0,-1,1));
        return root;
    }

    private View buildSidebar(){
        LinearLayout bar=new LinearLayout(this);bar.setOrientation(LinearLayout.VERTICAL);bar.setPadding(s(7),s(12),s(7),s(18));bar.setBackgroundColor(Color.WHITE);
        bar.addView(nav("⌂","home","home",this::showHome));bar.addView(nav("▥","interactive","interactive",this::showInteractive));
        bar.addView(nav("▤","projector_setup","projector",this::showProjectorSetup));bar.addView(nav("▦","saved_presets","presets",this::showPresets));bar.addView(nav("⚙","settings","settings",this::showSettings));
        TextView footer=Ui.text(this,"\nINDOOR\nSKI CLUB\n\nMore Runs\nA Brighter You.",font(10),Ui.MUTED,false);footer.setPadding(s(8),s(16),0,s(18));bar.addView(footer);return bar;
    }

    private Button nav(String icon,String key,String tag,Runnable run){
        Button b=new Button(this);b.setAllCaps(false);b.setText(icon+"  "+I18n.t(this,key));b.setTextSize(font(11));b.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
        b.setPadding(s(9),0,s(3),0);b.setTag(tag);b.setOnClickListener(v->{active=tag;styleNav();run.run();});
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,s(50));lp.bottomMargin=s(5);b.setLayoutParams(lp);navButtons.add(b);return b;
    }

    private void styleNav(){for(Button b:navButtons){boolean on=active.equals(b.getTag());b.setTextColor(on?Color.WHITE:Ui.NAVY);b.setTypeface(Typeface.DEFAULT,on?Typeface.BOLD:Typeface.NORMAL);b.setBackground(Ui.round(this,on?Ui.RED:Color.TRANSPARENT,14,Color.TRANSPARENT));}}
    private void clear(){content.removeAllViews();styleNav();contentScroll.scrollTo(0,0);}

    private LinearLayout pageHeader(String title,String subtitle){
        LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout txt=new LinearLayout(this);txt.setOrientation(LinearLayout.VERTICAL);txt.addView(Ui.text(this,title,font(24),Ui.NAVY,true));txt.addView(Ui.text(this,subtitle,font(10),Color.rgb(82,105,154),false));row.addView(txt,new LinearLayout.LayoutParams(0,s(64),1));
        projectorStatus=Ui.text(this,"● "+I18n.t(this,"projector_status")+"\n"+I18n.t(this,"not_connected"),font(9),Ui.MUTED,true);projectorStatus.setGravity(Gravity.CENTER);projectorStatus.setBackground(Ui.round(this,Color.WHITE,12,Ui.BORDER));row.addView(projectorStatus,new LinearLayout.LayoutParams(s(145),s(44)));
        return row;
    }

    private LinearLayout card(){LinearLayout c=Ui.card(this);c.setPadding(s(11),s(8),s(11),s(8));return c;}
    private LinearLayout miniCard(String h){LinearLayout c=card();c.addView(Ui.text(this,h,font(12),Ui.NAVY,true));return c;}
    private TextView small(String x){return Ui.text(this,x,font(9),Ui.MUTED,false);}
    private void updateProjectorStatus(boolean on){if(projectorStatus==null)return;projectorStatus.setText("● "+I18n.t(this,"projector_status")+"\n"+I18n.t(this,on?"connected":"not_connected"));projectorStatus.setTextColor(on?Ui.GREEN:Ui.MUTED);}

    private void showHome(){
        active="home";clear();content.addView(pageHeader(I18n.t(this,"welcome"),I18n.th(this)?"เลือกคอร์สที่คุณต้องการเริ่มฝึก":"Choose a course to start training"),new LinearLayout.LayoutParams(-1,s(66)));
        LinearLayout cats=card();cats.addView(Ui.text(this,I18n.t(this,"category"),font(14),Ui.NAVY,true));HorizontalScrollView hsv=new HorizontalScrollView(this);hsv.setHorizontalScrollBarEnabled(false);LinearLayout row=new LinearLayout(this);row.setPadding(0,s(5),0,s(2));hsv.addView(row);
        for(CourseStore.Category cat:CourseStore.Category.values()){boolean on=cat==selected;LinearLayout cc=Ui.card(this);cc.setPadding(s(10),s(7),s(10),s(7));cc.setBackground(Ui.round(this,on?Color.rgb(255,244,246):Color.WHITE,15,on?Ui.RED:Ui.BORDER));cc.addView(Ui.text(this,cat.icon,font(18),cat==CourseStore.Category.KIDS?Ui.GOLD:Ui.RED,true));cc.addView(Ui.text(this,I18n.category(this,cat),font(11),Ui.NAVY,true));cc.addView(Ui.text(this,CourseStore.byCategory(cat).size()+" "+(I18n.th(this)?"คอร์ส":"courses"),font(8),Ui.MUTED,false));cc.setOnClickListener(v->{selected=cat;showHome();});LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(s(device.type.equals("Phone")?135:165),s(84));cp.rightMargin=s(7);row.addView(cc,cp);}
        cats.addView(hsv,new LinearLayout.LayoutParams(-1,s(94)));LinearLayout.LayoutParams catLp=new LinearLayout.LayoutParams(-1,-2);catLp.bottomMargin=s(8);content.addView(cats,catLp);

        LinearLayout list=card();LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);top.addView(Ui.text(this,I18n.t(this,"courses"),font(15),Ui.NAVY,true),new LinearLayout.LayoutParams(0,s(28),1));top.addView(Ui.text(this,I18n.t(this,"tap_course"),font(8),Ui.RED,true));list.addView(top);
        for(CourseStore.Course c:CourseStore.byCategory(selected)){LinearLayout item=Ui.card(this);item.setPadding(s(9),s(6),s(8),s(6));LinearLayout line=new LinearLayout(this);line.setGravity(Gravity.CENTER_VERTICAL);line.addView(Ui.text(this,c.category.icon,font(20),c.category==CourseStore.Category.KIDS?Ui.GOLD:Ui.RED,true),new LinearLayout.LayoutParams(s(38),s(38)));LinearLayout tx=new LinearLayout(this);tx.setOrientation(LinearLayout.VERTICAL);tx.addView(Ui.text(this,I18n.courseTitle(this,c),font(12),Ui.NAVY,true));tx.addView(Ui.text(this,I18n.courseSummary(this,c),font(8),Ui.MUTED,false));LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(0,-2,1);tp.leftMargin=s(5);line.addView(tx,tp);Button open=Ui.button(this,I18n.t(this,"open"),true);line.addView(open,new LinearLayout.LayoutParams(s(70),s(34)));item.addView(line);View.OnClickListener l=v->showCourse(c);item.setOnClickListener(l);open.setOnClickListener(l);LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(-1,s(62));ip.bottomMargin=s(6);list.addView(item,ip);}
        content.addView(list,new LinearLayout.LayoutParams(-1,-2));
    }

    private void showCourse(CourseStore.Course course){
        clear();int[] values=course.defaults.clone();ProjectorSession.get().selectCourse(course,values);
        content.addView(pageHeader(I18n.courseTitle(this,course),I18n.category(this,course.category)+" / "+I18n.t(this,"course_detail")),new LinearLayout.LayoutParams(-1,s(66)));
        LinearLayout meta=card();meta.setOrientation(LinearLayout.HORIZONTAL);meta.addView(metric("⌁",I18n.th(this)?"ประเภทคอร์ส":"Course Type",I18n.category(this,course.category)),new LinearLayout.LayoutParams(0,s(58),1));meta.addView(metric("▮",I18n.th(this)?"ระดับทักษะ":"Skill Level",I18n.t(this,"beginner")),new LinearLayout.LayoutParams(0,s(58),1));meta.addView(metric("▱",I18n.th(this)?"จำนวนสเตจ":"Stages","5"),new LinearLayout.LayoutParams(0,s(58),1));meta.addView(metric("◷",I18n.th(this)?"ระยะเวลา":"Duration","10–20 min"),new LinearLayout.LayoutParams(0,s(58),1));LinearLayout.LayoutParams mlp=new LinearLayout.LayoutParams(-1,-2);mlp.bottomMargin=s(8);content.addView(meta,mlp);

        LinearLayout split=new LinearLayout(this);split.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout live=card();live.addView(Ui.text(this,"▣  "+I18n.t(this,"live_view"),font(15),Ui.NAVY,true));LivePreviewView preview=new LivePreviewView(this);preview.setData(course,values);live.addView(preview,new LinearLayout.LayoutParams(-1,s(device.type.equals("Phone")?350:455)));LinearLayout ctl=new LinearLayout(this);Button st=Ui.button(this,"▶ "+I18n.t(this,"start"),true);st.setOnClickListener(v->ProjectorSession.get().play());ctl.addView(st,new LinearLayout.LayoutParams(0,s(40),1));Button pa=Ui.button(this,"Ⅱ "+I18n.t(this,"pause"),false);pa.setOnClickListener(v->ProjectorSession.get().pause());LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(0,s(40),1);pp.leftMargin=s(5);ctl.addView(pa,pp);Button re=Ui.button(this,"↻ "+I18n.t(this,"reset"),false);re.setOnClickListener(v->ProjectorSession.get().reset());LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(0,s(40),1);rp.leftMargin=s(5);ctl.addView(re,rp);live.addView(ctl,new LinearLayout.LayoutParams(-1,s(44)));

        LinearLayout params=card();TextView ph=Ui.text(this,I18n.t(this,"parameters"),font(14),Color.WHITE,true);ph.setGravity(Gravity.CENTER_VERTICAL);ph.setPadding(s(10),0,0,0);ph.setBackground(Ui.round(this,Ui.RED,12,Color.TRANSPARENT));params.addView(ph,new LinearLayout.LayoutParams(-1,s(42)));
        for(int i=0;i<course.parameterNames.length;i++){final int idx=i;slider(params,I18n.param(this,course.parameterNames[i]),course.min[i],course.max[i],values[i],v->{values[idx]=v;preview.setData(course,values);ProjectorSession.get().updateValues(values);});}
        LinearLayout.LayoutParams llp=new LinearLayout.LayoutParams(0,-2,.62f);llp.rightMargin=s(8);split.addView(live,llp);split.addView(params,new LinearLayout.LayoutParams(0,-2,.38f));content.addView(split,new LinearLayout.LayoutParams(-1,-2));
    }

    private LinearLayout metric(String ic,String a,String b){LinearLayout m=new LinearLayout(this);m.setGravity(Gravity.CENTER_VERTICAL);m.setPadding(s(7),0,s(7),0);m.addView(Ui.text(this,ic,font(18),Color.rgb(45,70,125),true),new LinearLayout.LayoutParams(s(30),s(38)));LinearLayout t=new LinearLayout(this);t.setOrientation(LinearLayout.VERTICAL);t.addView(Ui.text(this,a,font(8),Color.rgb(76,98,147),false));t.addView(Ui.text(this,b,font(10),Ui.NAVY,true));m.addView(t);return m;}

    private void showProjectorSetup(){
        active="projector";clear();final ProjectorConfig[] cfg={ProjectorConfig.active(this)};
        content.addView(pageHeader(I18n.t(this,"projector_setup"),I18n.th(this)?"ปรับภาพโปรเจคเตอร์ให้ตรงกับพรมจริง และบันทึก Preset L / XL":"Calibrate the projector to the real ski mat and save L / XL presets."),new LinearLayout.LayoutParams(-1,s(66)));
        LinearLayout preset=card();preset.setOrientation(LinearLayout.HORIZONTAL);preset.setGravity(Gravity.CENTER_VERTICAL);LinearLayout ptxt=new LinearLayout(this);ptxt.setOrientation(LinearLayout.VERTICAL);ptxt.addView(Ui.text(this,I18n.t(this,"machine_preset"),font(13),Ui.NAVY,true));ptxt.addView(small("L = 4.8 × 8.5 m   •   XL = 4.8 × 10.5 m"));preset.addView(ptxt,new LinearLayout.LayoutParams(0,s(56),.46f));Spinner sp=new Spinner(this);String[] options={"L  (4.8 × 8.5 m)","XL  (4.8 × 10.5 m)"};sp.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,options));sp.setSelection("XL".equals(cfg[0].preset)?1:0);preset.addView(sp,new LinearLayout.LayoutParams(0,s(42),.30f));Button apply=Ui.button(this,"✓ "+I18n.t(this,"apply"),true);preset.addView(apply,new LinearLayout.LayoutParams(0,s(42),.24f));LinearLayout.LayoutParams plp=new LinearLayout.LayoutParams(-1,-2);plp.bottomMargin=s(8);content.addView(preset,plp);

        LinearLayout top=new LinearLayout(this);top.setOrientation(LinearLayout.HORIZONTAL);LinearLayout pv=card();pv.addView(Ui.text(this,"▣  "+I18n.t(this,"projector_preview"),font(13),Ui.NAVY,true));CalibrationPreviewView preview=new CalibrationPreviewView(this);preview.setConfig(cfg[0]);pv.addView(preview,new LinearLayout.LayoutParams(-1,s(device.type.equals("Phone")?330:470)));pv.addView(Ui.text(this,I18n.th(this)?"พรมจริงจะถูกแมปด้วย Alignment + Keystone + Perspective":"Real mat mapping uses Alignment + Keystone + Perspective",font(8),Ui.MUTED,false));LinearLayout.LayoutParams pvp=new LinearLayout.LayoutParams(0,-2,.42f);pvp.rightMargin=s(8);top.addView(pv,pvp);LinearLayout controls=new LinearLayout(this);controls.setOrientation(LinearLayout.VERTICAL);top.addView(controls,new LinearLayout.LayoutParams(0,-2,.58f));content.addView(top,new LinearLayout.LayoutParams(-1,-2));
        Runnable rebuild=()->{controls.removeAllViews();projectorControls(controls,cfg[0],preview);};rebuild.run();sp.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onItemSelected(android.widget.AdapterView<?> a,View v,int pos,long id){String p=pos==1?"XL":"L";if(!p.equals(cfg[0].preset)){cfg[0]=ProjectorConfig.load(MainActivity.this,p);preview.setConfig(cfg[0]);rebuild.run();}}public void onNothingSelected(android.widget.AdapterView<?> a){}});apply.setOnClickListener(v->{cfg[0].save(this);ProjectorSession.get().reset();Toast.makeText(this,(I18n.th(this)?"ใช้งานพรีเซ็ต ":"Applied ")+cfg[0].preset,Toast.LENGTH_SHORT).show();});
    }

    private void projectorControls(LinearLayout out,ProjectorConfig c,CalibrationPreviewView preview){
        LinearLayout r1=new LinearLayout(this);r1.setOrientation(LinearLayout.HORIZONTAL);LinearLayout mat=miniCard(I18n.t(this,"mat_size"));line(mat,I18n.t(this,"width"),"4.8 m");line(mat,I18n.t(this,"length"),c.length+" m");seek(mat,I18n.t(this,"safe_margin"),0,15,(int)c.safeMargin,v->{c.safeMargin=v;preview.setConfig(c);});r1.addView(mat,new LinearLayout.LayoutParams(0,-2,1));LinearLayout align=miniCard(I18n.t(this,"alignment"));seek(align,I18n.t(this,"x_position"),-100,100,(int)c.x,v->{c.x=v;preview.setConfig(c);});seek(align,I18n.t(this,"y_position"),-100,100,(int)c.y,v->{c.y=v;preview.setConfig(c);});seek(align,I18n.t(this,"scale_x"),70,130,(int)c.scaleX,v->{c.scaleX=v;preview.setConfig(c);});seek(align,I18n.t(this,"scale_y"),70,130,(int)c.scaleY,v->{c.scaleY=v;preview.setConfig(c);});LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(0,-2,1);ap.leftMargin=s(7);r1.addView(align,ap);out.addView(r1);
        LinearLayout r2=new LinearLayout(this);r2.setOrientation(LinearLayout.HORIZONTAL);r2.setPadding(0,s(7),0,0);LinearLayout key=miniCard(I18n.t(this,"keystone"));seek(key,I18n.t(this,"top_left")+" X",-30,30,(int)c.tlx,v->{c.tlx=v;preview.setConfig(c);});seek(key,I18n.t(this,"top_right")+" X",-30,30,(int)c.trx,v->{c.trx=v;preview.setConfig(c);});seek(key,I18n.t(this,"bottom_left")+" X",-30,30,(int)c.blx,v->{c.blx=v;preview.setConfig(c);});seek(key,I18n.t(this,"bottom_right")+" X",-30,30,(int)c.brx,v->{c.brx=v;preview.setConfig(c);});r2.addView(key,new LinearLayout.LayoutParams(0,-2,1));LinearLayout per=miniCard(I18n.t(this,"perspective"));seek(per,I18n.t(this,"perspective_strength"),0,100,(int)c.perspective,v->{c.perspective=v;preview.setConfig(c);});seek(per,I18n.t(this,"horizon_height"),0,50,(int)c.horizon,v->{c.horizon=v;preview.setConfig(c);});seek(per,I18n.t(this,"object_scale"),60,160,(int)c.objectScale,v->{c.objectScale=v;preview.setConfig(c);});LinearLayout.LayoutParams perlp=new LinearLayout.LayoutParams(0,-2,1);perlp.leftMargin=s(7);r2.addView(per,perlp);out.addView(r2);
        LinearLayout r3=new LinearLayout(this);r3.setOrientation(LinearLayout.HORIZONTAL);r3.setPadding(0,s(7),0,0);LinearLayout image=miniCard(I18n.t(this,"image"));seek(image,I18n.t(this,"brightness"),30,100,(int)c.brightness,v->c.brightness=v);seek(image,I18n.t(this,"contrast"),50,150,(int)c.contrast,v->c.contrast=v);line(image,I18n.th(this)?"พื้นหลัง":"Background",I18n.t(this,"object_only"));r3.addView(image,new LinearLayout.LayoutParams(0,-2,1));LinearLayout cal=miniCard(I18n.t(this,"calibration_tools"));toggle(cal,I18n.t(this,"show_grid"),c.grid,v->{c.grid=v;preview.setConfig(c);});toggle(cal,I18n.t(this,"show_corners"),c.corners,v->{c.corners=v;preview.setConfig(c);});toggle(cal,I18n.t(this,"show_boundary"),c.boundary,v->{c.boundary=v;preview.setConfig(c);});toggle(cal,I18n.t(this,"show_center"),c.center,v->{c.center=v;preview.setConfig(c);});LinearLayout.LayoutParams clp=new LinearLayout.LayoutParams(0,-2,1);clp.leftMargin=s(7);r3.addView(cal,clp);out.addView(r3);
    }

    private void showSettings(){
        active="settings";clear();content.addView(pageHeader(I18n.t(this,"settings"),I18n.th(this)?"การตั้งค่าแอป โปรเจคเตอร์ การฝึก และโหมด Developer":"App, projector, training and developer settings"),new LinearLayout.LayoutParams(-1,s(66)));
        LinearLayout r1=new LinearLayout(this);r1.setOrientation(LinearLayout.HORIZONTAL);LinearLayout general=miniCard(I18n.t(this,"general"));general.addView(small(I18n.t(this,"language")));LinearLayout langs=new LinearLayout(this);Button th=Ui.button(this,"ไทย",I18n.th(this));Button en=Ui.button(this,"English",!I18n.th(this));th.setOnClickListener(v->{I18n.setLanguage(this,"th");showSettings();});en.setOnClickListener(v->{I18n.setLanguage(this,"en");showSettings();});langs.addView(th,new LinearLayout.LayoutParams(0,s(36),1));LinearLayout.LayoutParams enlp=new LinearLayout.LayoutParams(0,s(36),1);enlp.leftMargin=s(5);langs.addView(en,enlp);general.addView(langs);line(general,I18n.t(this,"units"),I18n.t(this,"metric"));line(general,I18n.t(this,"theme"),I18n.t(this,"light"));r1.addView(general,new LinearLayout.LayoutParams(0,-2,1));LinearLayout display=miniCard(I18n.t(this,"display_graphics"));line(display,I18n.t(this,"resolution"),device.widthPx+" × "+device.heightPx);line(display,I18n.t(this,"frame_rate"),"60 FPS");line(display,I18n.th(this)?"อุปกรณ์":"Device",device.type);line(display,"Responsive Profile",device.profile);LinearLayout.LayoutParams dlp=new LinearLayout.LayoutParams(0,-2,1);dlp.leftMargin=s(7);r1.addView(display,dlp);content.addView(r1);
        LinearLayout r2=new LinearLayout(this);r2.setOrientation(LinearLayout.HORIZONTAL);r2.setPadding(0,s(7),0,0);LinearLayout proj=miniCard(I18n.t(this,"projector_hardware"));toggle(proj,I18n.t(this,"auto_connect"),true,null);toggle(proj,I18n.t(this,"auto_apply_preset"),true,null);line(proj,I18n.t(this,"machine_preset"),ProjectorConfig.activePreset(this));r2.addView(proj,new LinearLayout.LayoutParams(0,-2,1));LinearLayout train=miniCard(I18n.t(this,"course_training"));line(train,I18n.t(this,"default_course"),I18n.courseTitle(this,CourseStore.byId("basic_gates")));line(train,I18n.t(this,"default_difficulty"),I18n.t(this,"beginner"));toggle(train,I18n.t(this,"save_history"),true,null);LinearLayout.LayoutParams trlp=new LinearLayout.LayoutParams(0,-2,1);trlp.leftMargin=s(7);r2.addView(train,trlp);content.addView(r2);
        LinearLayout r3=new LinearLayout(this);r3.setOrientation(LinearLayout.HORIZONTAL);r3.setPadding(0,s(7),0,0);LinearLayout dev=miniCard("</>  "+I18n.t(this,"developer_mode"));dev.setBackground(Ui.round(this,Color.rgb(255,247,248),16,Color.rgb(255,174,184)));dev.addView(small(I18n.t(this,"developer_note")));toggle(dev,I18n.t(this,"enable_developer"),DeveloperPrefs.enabled(this),v->DeveloperPrefs.setEnabled(this,v));toggle(dev,I18n.t(this,"projector_mirror"),DeveloperPrefs.mirror(this),v->DeveloperPrefs.setMirror(this,v));toggle(dev,I18n.t(this,"debug_overlay"),DeveloperPrefs.overlay(this),v->DeveloperPrefs.setOverlay(this,v));toggle(dev,I18n.t(this,"safe_area"),DeveloperPrefs.safeArea(this),v->DeveloperPrefs.setSafeArea(this,v));r3.addView(dev,new LinearLayout.LayoutParams(0,-2,1));LinearLayout info=miniCard(I18n.th(this)?"ข้อมูลอุปกรณ์":"Device Detection");line(info,"Type",device.type);line(info,"Profile",device.profile);line(info,"dp",device.widthDp+" × "+device.heightDp+" / sw"+device.smallestDp);line(info,"px",device.widthPx+" × "+device.heightPx);line(info,"Aspect",String.format(Locale.US,"%.2f",device.aspect));LinearLayout.LayoutParams ilp=new LinearLayout.LayoutParams(0,-2,1);ilp.leftMargin=s(7);r3.addView(info,ilp);content.addView(r3);
        LinearLayout about=miniCard(I18n.t(this,"about"));line(about,"Ski Addict","Indoor Ski Club");line(about,I18n.t(this,"app_version"),"0.9 Responsive Demo");line(about,I18n.th(this)?"โลโก้ผลิตภัณฑ์":"Product logo","Official Ski Addict logo");LinearLayout.LayoutParams abp=new LinearLayout.LayoutParams(-1,-2);abp.topMargin=s(7);content.addView(about,abp);
    }

    private void showInteractive(){active="interactive";clear();content.addView(pageHeader(I18n.t(this,"interactive"),I18n.th(this)?"พื้นที่สำหรับ Camera / Sensor interaction":"Camera / sensor interaction workspace"),new LinearLayout.LayoutParams(-1,s(66)));LinearLayout c=card();c.addView(Ui.text(this,"Interactive Engine",font(22),Ui.NAVY,true));c.addView(small(I18n.th(this)?"เตรียมไว้สำหรับ Reaction Lights, Balloon Pop, Scoring และ Camera Tracking":"Prepared for Reaction Lights, Balloon Pop, scoring and camera tracking."));content.addView(c);}
    private void showPresets(){active="presets";clear();content.addView(pageHeader(I18n.t(this,"saved_presets"),I18n.th(this)?"พรีเซ็ตเครื่องและการตั้งค่าโปรเจคเตอร์":"Machine and projector presets"),new LinearLayout.LayoutParams(-1,s(66)));LinearLayout c=card();c.addView(Ui.text(this,"L  •  4.8 × 8.5 m",font(15),Ui.NAVY,true));c.addView(small(I18n.th(this)?"บันทึก Alignment / Keystone / Perspective แยกเฉพาะเครื่อง L":"Stores L alignment, keystone and perspective."));c.addView(space(12));c.addView(Ui.text(this,"XL  •  4.8 × 10.5 m",font(15),Ui.NAVY,true));c.addView(small(I18n.th(this)?"บันทึก Alignment / Keystone / Perspective แยกเฉพาะเครื่อง XL":"Stores XL alignment, keystone and perspective."));content.addView(c);}

    private View space(int h){View v=new View(this);v.setLayoutParams(new LinearLayout.LayoutParams(1,s(h)));return v;}
    private void line(LinearLayout c,String a,String b){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.addView(Ui.text(this,a,font(8),Ui.MUTED,false),new LinearLayout.LayoutParams(0,s(30),1));r.addView(Ui.text(this,b,font(9),Ui.NAVY,true));c.addView(r);}
    interface IntChange{void set(int v);}
    private void slider(LinearLayout p,String name,int min,int max,int value,IntChange f){LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(s(7),s(4),s(7),s(3));box.setBackground(Ui.round(this,Color.rgb(250,252,254),10,Ui.BORDER));LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);top.addView(Ui.text(this,name,font(9),Ui.NAVY,true),new LinearLayout.LayoutParams(0,s(22),1));TextView n=Ui.text(this,String.valueOf(value),font(10),Ui.RED,true);top.addView(n);box.addView(top);SeekBar sb=new SeekBar(this);sb.setMax(max-min);sb.setProgress(value-min);sb.setProgressTintList(ColorStateList.valueOf(Ui.RED));sb.setThumbTintList(ColorStateList.valueOf(Ui.RED));sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int x,boolean q){int v=min+x;n.setText(String.valueOf(v));f.set(v);}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}});box.addView(sb,new LinearLayout.LayoutParams(-1,s(28)));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,s(60));lp.topMargin=s(5);p.addView(box,lp);}
    private void seek(LinearLayout c,String name,int min,int max,int value,IntChange f){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.addView(Ui.text(this,name,font(8),Ui.MUTED,false),new LinearLayout.LayoutParams(s(105),s(28)));SeekBar sb=new SeekBar(this);sb.setMax(max-min);sb.setProgress(value-min);sb.setProgressTintList(ColorStateList.valueOf(Ui.RED));sb.setThumbTintList(ColorStateList.valueOf(Ui.RED));TextView n=Ui.text(this,String.valueOf(value),font(8),Ui.NAVY,true);sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int x,boolean q){int v=min+x;n.setText(String.valueOf(v));f.set(v);}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}});r.addView(sb,new LinearLayout.LayoutParams(0,s(28),1));r.addView(n,new LinearLayout.LayoutParams(s(38),s(28)));c.addView(r);}
    interface BoolChange{void set(boolean v);}
    private void toggle(LinearLayout c,String name,boolean on,BoolChange f){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.addView(Ui.text(this,name,font(8),Ui.MUTED,false),new LinearLayout.LayoutParams(0,s(30),1));Switch sw=new Switch(this);sw.setChecked(on);if(f!=null)sw.setOnCheckedChangeListener((b,v)->f.set(v));r.addView(sw);c.addView(r);}
}
