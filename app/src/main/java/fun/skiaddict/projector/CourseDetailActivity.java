package fun.skiaddict.projector;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

public class CourseDetailActivity extends Activity {
    private CourseStore.Course course; private int[] values; private LivePreviewView preview; private ProjectorDisplayHost host; private TextView status;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);course=CourseStore.byId(getIntent().getStringExtra("course_id"));values=course.defaults.clone();setContentView(build());preview.setData(course,values);ProjectorSession.get().selectCourse(course,values);
        host=new ProjectorDisplayHost(this,(on,name)->runOnUiThread(()->updateStatus(on)));
    }
    @Override protected void onResume(){super.onResume();ProjectorSession.get().selectCourse(course,values);if(host!=null)host.start();}
    @Override protected void onPause(){if(host!=null)host.stop();super.onPause();}

    private int wdp(){return getResources().getConfiguration().screenWidthDp;} private int clamp(int v,int a,int b){return Math.max(a,Math.min(b,v));}
    private int nav(){return clamp(Math.round(wdp()*.14f),88,145);} private int param(){return clamp(Math.round(wdp()*.34f),235,390);}

    private View build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Ui.BG);root.setPadding(Ui.dp(this,10),Ui.dp(this,6),Ui.dp(this,10),Ui.dp(this,8));
        status=Ui.text(this,"● "+I18n.t(this,"projector_status")+"\n"+I18n.t(this,"not_connected"),9,Ui.MUTED,true);root.addView(Ui.header(this,status),new LinearLayout.LayoutParams(-1,Ui.dp(this,58)));
        LinearLayout body=new LinearLayout(this);body.setOrientation(LinearLayout.HORIZONTAL);LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,0,1);bp.topMargin=Ui.dp(this,6);root.addView(body,bp);
        body.addView(Ui.sidebar(this,""),new LinearLayout.LayoutParams(Ui.dp(this,nav()),-1));

        LinearLayout center=new LinearLayout(this);center.setOrientation(LinearLayout.VERTICAL);LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(0,-1,1);cp.leftMargin=Ui.dp(this,8);cp.rightMargin=Ui.dp(this,8);body.addView(center,cp);

        LinearLayout info=Ui.card(this);info.setPadding(Ui.dp(this,10),Ui.dp(this,6),Ui.dp(this,10),Ui.dp(this,6));
        LinearLayout title=new LinearLayout(this);title.setGravity(Gravity.CENTER_VERTICAL);title.addView(Ui.text(this,I18n.courseTitle(this,course),20,Ui.NAVY,true),new LinearLayout.LayoutParams(0,-2,1));View home=Ui.button(this,I18n.t(this,"back_home"),false);home.setOnClickListener(v->finish());title.addView(home,new LinearLayout.LayoutParams(Ui.dp(this,94),Ui.dp(this,34)));info.addView(title);info.addView(Ui.text(this,I18n.category(this,course.category)+" • "+I18n.courseSummary(this,course),9,Ui.MUTED,false));center.addView(info,new LinearLayout.LayoutParams(-1,Ui.dp(this,70)));

        LinearLayout live=Ui.card(this);live.setPadding(Ui.dp(this,8),Ui.dp(this,5),Ui.dp(this,8),Ui.dp(this,7));live.addView(Ui.text(this,I18n.t(this,"live_view"),15,Ui.NAVY,true));
        preview=new LivePreviewView(this);live.addView(preview,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout controls=new LinearLayout(this);
        View st=Ui.button(this,"▶ "+I18n.t(this,"start"),true);st.setOnClickListener(v->ProjectorSession.get().play());controls.addView(st,new LinearLayout.LayoutParams(0,Ui.dp(this,36),1));
        LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(0,Ui.dp(this,36),1);pp.leftMargin=Ui.dp(this,5);View pa=Ui.button(this,"Ⅱ "+I18n.t(this,"pause"),false);pa.setOnClickListener(v->ProjectorSession.get().pause());controls.addView(pa,pp);
        LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(0,Ui.dp(this,36),1);rp.leftMargin=Ui.dp(this,5);View re=Ui.button(this,"↻ "+I18n.t(this,"reset"),false);re.setOnClickListener(v->ProjectorSession.get().reset());controls.addView(re,rp);
        LinearLayout.LayoutParams ctl=new LinearLayout.LayoutParams(-1,Ui.dp(this,36));ctl.topMargin=Ui.dp(this,5);live.addView(controls,ctl);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,0,1);lp.topMargin=Ui.dp(this,7);center.addView(live,lp);

        body.addView(params(),new LinearLayout.LayoutParams(Ui.dp(this,param()),-1));return root;
    }

    private View params(){
        LinearLayout p=Ui.card(this);p.setPadding(Ui.dp(this,8),Ui.dp(this,7),Ui.dp(this,8),Ui.dp(this,7));
        TextView h=Ui.text(this,I18n.t(this,"parameters"),14,Color.WHITE,true);h.setGravity(Gravity.CENTER_VERTICAL);h.setPadding(Ui.dp(this,10),0,0,0);h.setBackground(Ui.round(this,Ui.RED,12,Color.TRANSPARENT));p.addView(h,new LinearLayout.LayoutParams(-1,Ui.dp(this,40)));
        ScrollView sv=new ScrollView(this);LinearLayout list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);sv.addView(list);
        for(int i=0;i<course.parameterNames.length;i++){final int idx=i;slider(list,I18n.param(this,course.parameterNames[i]),course.min[i],course.max[i],values[i],v->{values[idx]=v;preview.setData(course,values);ProjectorSession.get().updateValues(values);});}
        LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,0,1);sp.topMargin=Ui.dp(this,5);p.addView(sv,sp);return p;
    }

    interface Ch{void v(int x);}
    private void slider(LinearLayout parent,String name,int min,int max,int val,Ch ch){
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(Ui.dp(this,7),Ui.dp(this,4),Ui.dp(this,7),Ui.dp(this,3));box.setBackground(Ui.round(this,Color.rgb(250,252,254),10,Ui.BORDER));
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);top.addView(Ui.text(this,name,10,Ui.NAVY,true),new LinearLayout.LayoutParams(0,-2,1));TextView num=Ui.text(this,String.valueOf(val),11,Ui.RED,true);top.addView(num);box.addView(top);
        SeekBar s=new SeekBar(this);s.setMax(max-min);s.setProgress(val-min);s.setProgressTintList(ColorStateList.valueOf(Ui.RED));s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int p,boolean f){int x=min+p;num.setText(String.valueOf(x));ch.v(x);}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}});box.addView(s,new LinearLayout.LayoutParams(-1,Ui.dp(this,30)));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,Ui.dp(this,62));lp.topMargin=Ui.dp(this,5);parent.addView(box,lp);
    }
    private void updateStatus(boolean on){status.setText("● "+I18n.t(this,"projector_status")+"\n"+I18n.t(this,on?"connected":"not_connected"));status.setTextColor(on?Ui.GREEN:Ui.MUTED);}
}
