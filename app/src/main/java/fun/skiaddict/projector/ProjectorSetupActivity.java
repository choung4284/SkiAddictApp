package fun.skiaddict.projector;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ProjectorSetupActivity extends Activity {
    private ProjectorConfig cfg; private CalibrationPreviewView preview; private TextView status; private ProjectorDisplayHost host; private LinearLayout controlsRoot;

    @Override protected void onCreate(Bundle b){super.onCreate(b);cfg=ProjectorConfig.active(this);setContentView(build());host=new ProjectorDisplayHost(this,(on,name)->runOnUiThread(()->setStatus(on)));}
    @Override protected void onResume(){super.onResume();if(host!=null)host.start();}
    @Override protected void onPause(){if(host!=null)host.stop();super.onPause();}

    private int wdp(){return getResources().getConfiguration().screenWidthDp;} private int clamp(int v,int a,int b){return Math.max(a,Math.min(b,v));}
    private int nav(){return clamp(Math.round(wdp()*.14f),88,145);}

    private View build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Ui.BG);root.setPadding(Ui.dp(this,10),Ui.dp(this,6),Ui.dp(this,10),Ui.dp(this,8));
        status=Ui.text(this,"● "+I18n.t(this,"projector_status")+"\n"+I18n.t(this,"not_connected"),9,Ui.MUTED,true);root.addView(Ui.header(this,status),new LinearLayout.LayoutParams(-1,Ui.dp(this,58)));
        LinearLayout body=new LinearLayout(this);body.setOrientation(LinearLayout.HORIZONTAL);LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,0,1);bp.topMargin=Ui.dp(this,6);root.addView(body,bp);
        body.addView(Ui.sidebar(this,"projector"),new LinearLayout.LayoutParams(Ui.dp(this,nav()),-1));

        LinearLayout center=new LinearLayout(this);center.setOrientation(LinearLayout.VERTICAL);LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(0,-1,1);cp.leftMargin=Ui.dp(this,8);body.addView(center,cp);

        LinearLayout title=new LinearLayout(this);title.setGravity(Gravity.CENTER_VERTICAL);title.addView(Ui.text(this,I18n.t(this,"projector_setup"),22,Ui.NAVY,true),new LinearLayout.LayoutParams(0,Ui.dp(this,48),1));center.addView(title);

        LinearLayout preset=Ui.card(this);preset.setPadding(Ui.dp(this,10),Ui.dp(this,6),Ui.dp(this,10),Ui.dp(this,6));
        preset.addView(Ui.text(this,I18n.t(this,"machine_preset"),13,Ui.NAVY,true));
        LinearLayout pr=new LinearLayout(this);pr.setGravity(Gravity.CENTER_VERTICAL);
        Spinner sp=new Spinner(this);String[] items={"L  (4.8 × 8.5 m)","XL  (4.8 × 10.5 m)"};ArrayAdapter<String> ad=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,items);sp.setAdapter(ad);sp.setSelection("XL".equals(cfg.preset)?1:0);
        pr.addView(sp,new LinearLayout.LayoutParams(0,Ui.dp(this,38),1));
        View save=Ui.button(this,I18n.t(this,"save_preset"),false);save.setOnClickListener(v->{cfg.save(this);Toast.makeText(this,I18n.t(this,"save_preset"),Toast.LENGTH_SHORT).show();});LinearLayout.LayoutParams svp=new LinearLayout.LayoutParams(Ui.dp(this,120),Ui.dp(this,36));svp.leftMargin=Ui.dp(this,6);pr.addView(save,svp);
        View apply=Ui.button(this,I18n.t(this,"apply"),true);apply.setOnClickListener(v->apply());LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(Ui.dp(this,100),Ui.dp(this,36));ap.leftMargin=Ui.dp(this,6);pr.addView(apply,ap);
        preset.addView(pr);center.addView(preset,new LinearLayout.LayoutParams(-1,Ui.dp(this,82)));
        sp.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onItemSelected(android.widget.AdapterView<?> p,View v,int pos,long id){String np=pos==1?"XL":"L";if(!np.equals(cfg.preset)){cfg=ProjectorConfig.load(ProjectorSetupActivity.this,np);rebuildControls();}}public void onNothingSelected(android.widget.AdapterView<?> p){}});

        LinearLayout content=new LinearLayout(this);content.setOrientation(LinearLayout.HORIZONTAL);LinearLayout.LayoutParams conp=new LinearLayout.LayoutParams(-1,0,1);conp.topMargin=Ui.dp(this,7);center.addView(content,conp);

        LinearLayout left=Ui.card(this);left.setPadding(Ui.dp(this,8),Ui.dp(this,6),Ui.dp(this,8),Ui.dp(this,6));left.addView(Ui.text(this,I18n.t(this,"projector_preview"),13,Ui.NAVY,true));preview=new CalibrationPreviewView(this);preview.setConfig(cfg);left.addView(preview,new LinearLayout.LayoutParams(-1,0,1));TextView size=Ui.text(this,(I18n.th(this)?"ขนาดพรม ":"Mat Size ")+cfg.preset+"  •  4.8 × "+cfg.length+" m",10,Ui.MUTED,true);size.setGravity(Gravity.CENTER);left.addView(size,new LinearLayout.LayoutParams(-1,Ui.dp(this,30)));content.addView(left,new LinearLayout.LayoutParams(0,-1,.42f));

        ScrollView scroll=new ScrollView(this);controlsRoot=new LinearLayout(this);controlsRoot.setOrientation(LinearLayout.VERTICAL);controlsRoot.setPadding(Ui.dp(this,7),0,0,0);scroll.addView(controlsRoot);content.addView(scroll,new LinearLayout.LayoutParams(0,-1,.58f));
        rebuildControls();
        return root;
    }

    private void rebuildControls(){
        if(controlsRoot==null)return;controlsRoot.removeAllViews();if(preview!=null)preview.setConfig(cfg);
        controlsRoot.addView(matCard());controlsRoot.addView(alignmentCard());controlsRoot.addView(keystoneCard());controlsRoot.addView(perspectiveCard());controlsRoot.addView(imageCard());controlsRoot.addView(calibrationCard());
    }

    private View matCard(){LinearLayout c=card(I18n.t(this,"mat_size"));rowText(c,I18n.t(this,"width"),"4.8 m");rowText(c,I18n.t(this,"length"),cfg.length+" m");seek(c,I18n.t(this,"safe_margin"),0,15,(int)cfg.safeMargin,v->cfg.safeMargin=v);return c;}
    private View alignmentCard(){LinearLayout c=card(I18n.t(this,"alignment"));seek(c,I18n.t(this,"x_position"),-100,100,(int)cfg.x,v->cfg.x=v);seek(c,I18n.t(this,"y_position"),-100,100,(int)cfg.y,v->cfg.y=v);seek(c,I18n.t(this,"scale_x"),70,130,(int)cfg.scaleX,v->cfg.scaleX=v);seek(c,I18n.t(this,"scale_y"),70,130,(int)cfg.scaleY,v->cfg.scaleY=v);seek(c,I18n.t(this,"rotation"),-15,15,(int)cfg.rotation,v->cfg.rotation=v);return c;}
    private View keystoneCard(){LinearLayout c=card(I18n.t(this,"keystone"));TextView hint=Ui.text(this,I18n.th(this)?"ปรับตำแหน่ง X/Y ของแต่ละมุม":"Adjust X/Y for each corner",9,Ui.MUTED,false);c.addView(hint);seek(c,I18n.t(this,"top_left")+" X",-30,30,(int)cfg.tlx,v->cfg.tlx=v);seek(c,I18n.t(this,"top_left")+" Y",-30,30,(int)cfg.tly,v->cfg.tly=v);seek(c,I18n.t(this,"top_right")+" X",-30,30,(int)cfg.trx,v->cfg.trx=v);seek(c,I18n.t(this,"top_right")+" Y",-30,30,(int)cfg.tryy,v->cfg.tryy=v);seek(c,I18n.t(this,"bottom_left")+" X",-30,30,(int)cfg.blx,v->cfg.blx=v);seek(c,I18n.t(this,"bottom_left")+" Y",-30,30,(int)cfg.bly,v->cfg.bly=v);seek(c,I18n.t(this,"bottom_right")+" X",-30,30,(int)cfg.brx,v->cfg.brx=v);seek(c,I18n.t(this,"bottom_right")+" Y",-30,30,(int)cfg.bry,v->cfg.bry=v);return c;}
    private View perspectiveCard(){LinearLayout c=card(I18n.t(this,"perspective"));seek(c,I18n.t(this,"perspective_strength"),0,100,(int)cfg.perspective,v->cfg.perspective=v);seek(c,I18n.t(this,"horizon_height"),0,50,(int)cfg.horizon,v->cfg.horizon=v);seek(c,I18n.t(this,"vanishing_x"),0,100,(int)cfg.vanishX,v->cfg.vanishX=v);seek(c,I18n.t(this,"vanishing_y"),-50,50,(int)cfg.vanishY,v->cfg.vanishY=v);return c;}
    private View imageCard(){LinearLayout c=card(I18n.t(this,"image"));seek(c,I18n.t(this,"brightness"),30,100,(int)cfg.brightness,v->cfg.brightness=v);seek(c,I18n.t(this,"contrast"),50,150,(int)cfg.contrast,v->cfg.contrast=v);seek(c,I18n.t(this,"object_scale"),60,160,(int)cfg.objectScale,v->cfg.objectScale=v);rowText(c,I18n.th(this)?"โหมดพื้นหลัง":"Background",I18n.t(this,"object_only"));return c;}
    private View calibrationCard(){LinearLayout c=card(I18n.t(this,"calibration_tools"));toggle(c,I18n.t(this,"show_grid"),cfg.grid,(b)->cfg.grid=b);toggle(c,I18n.t(this,"show_corners"),cfg.corners,(b)->cfg.corners=b);toggle(c,I18n.t(this,"show_boundary"),cfg.boundary,(b)->cfg.boundary=b);toggle(c,I18n.t(this,"show_center"),cfg.center,(b)->cfg.center=b);View reset=Ui.button(this,I18n.t(this,"reset_calibration"),false);reset.setOnClickListener(v->{cfg=ProjectorConfig.load(this,cfg.preset);cfg.x=cfg.y=cfg.rotation=cfg.tlx=cfg.tly=cfg.trx=cfg.tryy=cfg.blx=cfg.bly=cfg.brx=cfg.bry=0;cfg.scaleX=cfg.scaleY=100;rebuildControls();});c.addView(reset,new LinearLayout.LayoutParams(-1,Ui.dp(this,34)));return c;}

    private LinearLayout card(String title){LinearLayout c=Ui.card(this);c.setPadding(Ui.dp(this,8),Ui.dp(this,6),Ui.dp(this,8),Ui.dp(this,6));c.addView(Ui.text(this,title,12,Ui.NAVY,true));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=Ui.dp(this,6);c.setLayoutParams(lp);return c;}
    private void rowText(LinearLayout c,String a,String b){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.addView(Ui.text(this,a,9,Ui.MUTED,false),new LinearLayout.LayoutParams(0,Ui.dp(this,24),1));r.addView(Ui.text(this,b,10,Ui.NAVY,true));c.addView(r);}
    interface V{void set(int v);} private void seek(LinearLayout c,String name,int min,int max,int val,V fn){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);TextView l=Ui.text(this,name,9,Ui.MUTED,false);r.addView(l,new LinearLayout.LayoutParams(Ui.dp(this,130),Ui.dp(this,28)));SeekBar s=new SeekBar(this);s.setMax(max-min);s.setProgress(val-min);s.setProgressTintList(ColorStateList.valueOf(Ui.RED));TextView n=Ui.text(this,String.valueOf(val),9,Ui.NAVY,true);s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int p,boolean f){int x=min+p;n.setText(String.valueOf(x));fn.set(x);if(preview!=null)preview.setConfig(cfg);}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}});r.addView(s,new LinearLayout.LayoutParams(0,Ui.dp(this,28),1));r.addView(n,new LinearLayout.LayoutParams(Ui.dp(this,42),Ui.dp(this,28)));c.addView(r);}
    interface B{void set(boolean b);} private void toggle(LinearLayout c,String name,boolean on,B fn){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.addView(Ui.text(this,name,9,Ui.MUTED,false),new LinearLayout.LayoutParams(0,Ui.dp(this,30),1));Switch sw=new Switch(this);sw.setChecked(on);sw.setOnCheckedChangeListener((button,is)->{fn.set(is);if(preview!=null)preview.setConfig(cfg);});r.addView(sw);c.addView(r);}
    private void apply(){cfg.save(this);ProjectorSession.get().reset();Toast.makeText(this,I18n.t(this,"apply")+" "+cfg.preset,Toast.LENGTH_SHORT).show();}
    private void setStatus(boolean on){status.setText("● "+I18n.t(this,"projector_status")+"\n"+I18n.t(this,on?"connected":"not_connected"));status.setTextColor(on?Ui.GREEN:Ui.MUTED);}
}
