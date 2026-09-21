package fun.skiaddict.projector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private CourseStore.Category selected=CourseStore.Category.ALPINE;
    private final List<View> categoryCards=new ArrayList<>();
    private LinearLayout courseBox;
    private TextView projectorStatus;
    private ProjectorDisplayHost projectorHost;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b); setContentView(build()); updateCategories(); renderCourses();
        projectorHost=new ProjectorDisplayHost(this,(connected,name)->runOnUiThread(()->status(connected)));
    }
    @Override protected void onResume(){ super.onResume(); if(projectorHost!=null)projectorHost.start(); }
    @Override protected void onPause(){ if(projectorHost!=null)projectorHost.stop(); super.onPause(); }

    private int wdp(){return getResources().getConfiguration().screenWidthDp;}
    private int clamp(int v,int a,int b){return Math.max(a,Math.min(b,v));}
    private int nav(){return clamp(Math.round(wdp()*.14f),88,150);}

    private View build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Ui.BG);root.setPadding(Ui.dp(this,10),Ui.dp(this,6),Ui.dp(this,10),Ui.dp(this,8));
        projectorStatus=Ui.text(this,"● "+I18n.t(this,"projector_status")+"\n"+I18n.t(this,"not_connected"),9,Ui.MUTED,true);
        root.addView(Ui.header(this,projectorStatus),new LinearLayout.LayoutParams(-1,Ui.dp(this,58)));

        LinearLayout body=new LinearLayout(this);body.setOrientation(LinearLayout.HORIZONTAL);LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,0,1);bp.topMargin=Ui.dp(this,6);root.addView(body,bp);
        body.addView(Ui.sidebar(this,"home"),new LinearLayout.LayoutParams(Ui.dp(this,nav()),-1));

        LinearLayout center=new LinearLayout(this);center.setOrientation(LinearLayout.VERTICAL);LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(0,-1,1);cp.leftMargin=Ui.dp(this,8);body.addView(center,cp);

        LinearLayout hero=Ui.card(this);hero.setPadding(Ui.dp(this,14),Ui.dp(this,8),Ui.dp(this,14),Ui.dp(this,8));
        hero.addView(Ui.text(this,I18n.t(this,"welcome"),20,Ui.NAVY,true));
        hero.addView(Ui.text(this,I18n.t(this,"select_course"),11,Ui.MUTED,false));
        center.addView(hero,new LinearLayout.LayoutParams(-1,Ui.dp(this,70)));

        LinearLayout.LayoutParams catLp=new LinearLayout.LayoutParams(-1,Ui.dp(this,126));catLp.topMargin=Ui.dp(this,7);center.addView(categories(),catLp);
        LinearLayout.LayoutParams courseLp=new LinearLayout.LayoutParams(-1,0,1);courseLp.topMargin=Ui.dp(this,7);center.addView(courses(),courseLp);
        return root;
    }

    private View categories(){
        LinearLayout panel=Ui.card(this);panel.setPadding(Ui.dp(this,10),Ui.dp(this,4),Ui.dp(this,10),Ui.dp(this,6));
        panel.addView(Ui.text(this,I18n.t(this,"category"),15,Ui.NAVY,true));
        HorizontalScrollView hsv=new HorizontalScrollView(this);hsv.setHorizontalScrollBarEnabled(false);LinearLayout row=new LinearLayout(this);hsv.addView(row);
        for(CourseStore.Category cat:CourseStore.Category.values()){
            LinearLayout card=Ui.card(this);card.setPadding(Ui.dp(this,9),Ui.dp(this,5),Ui.dp(this,9),Ui.dp(this,5));
            card.addView(Ui.text(this,cat.icon,18,cat==CourseStore.Category.KIDS?Ui.GOLD:Ui.RED,true));
            card.addView(Ui.text(this,I18n.category(this,cat),11,Ui.NAVY,true));
            card.addView(Ui.text(this,CourseStore.byCategory(cat).size()+" "+I18n.t(this,"courses").toLowerCase(),8,Ui.MUTED,false));
            card.setOnClickListener(v->{selected=cat;updateCategories();renderCourses();});
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(Ui.dp(this,clamp(wdp()/6,115,165)),Ui.dp(this,78));p.rightMargin=Ui.dp(this,6);row.addView(card,p);categoryCards.add(card);
        }
        panel.addView(hsv,new LinearLayout.LayoutParams(-1,0,1));return panel;
    }

    private View courses(){
        LinearLayout panel=Ui.card(this);panel.setPadding(Ui.dp(this,10),Ui.dp(this,4),Ui.dp(this,10),Ui.dp(this,6));
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);top.addView(Ui.text(this,I18n.t(this,"courses"),15,Ui.NAVY,true),new LinearLayout.LayoutParams(0,Ui.dp(this,28),1));
        top.addView(Ui.text(this,I18n.t(this,"tap_course"),9,Ui.RED,true));panel.addView(top);
        ScrollView sv=new ScrollView(this);courseBox=new LinearLayout(this);courseBox.setOrientation(LinearLayout.VERTICAL);sv.addView(courseBox);panel.addView(sv,new LinearLayout.LayoutParams(-1,0,1));return panel;
    }

    private void renderCourses(){
        if(courseBox==null)return;courseBox.removeAllViews();
        for(CourseStore.Course c:CourseStore.byCategory(selected)){
            LinearLayout card=Ui.card(this);card.setPadding(Ui.dp(this,8),Ui.dp(this,6),Ui.dp(this,8),Ui.dp(this,6));
            LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);
            row.addView(Ui.text(this,c.category.icon,20,c.category==CourseStore.Category.KIDS?Ui.GOLD:Ui.RED,true),new LinearLayout.LayoutParams(Ui.dp(this,34),Ui.dp(this,34)));
            LinearLayout txt=new LinearLayout(this);txt.setOrientation(LinearLayout.VERTICAL);txt.addView(Ui.text(this,I18n.courseTitle(this,c),12,Ui.NAVY,true));txt.addView(Ui.text(this,I18n.courseSummary(this,c),9,Ui.MUTED,false));LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(0,-2,1);tp.leftMargin=Ui.dp(this,5);row.addView(txt,tp);
            row.addView(Ui.button(this,I18n.t(this,"open"),true),new LinearLayout.LayoutParams(Ui.dp(this,68),Ui.dp(this,32)));card.addView(row);
            card.setOnClickListener(v->{Intent i=new Intent(this,CourseDetailActivity.class);i.putExtra("course_id",c.id);startActivity(i);});
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,Ui.dp(this,58));lp.bottomMargin=Ui.dp(this,5);courseBox.addView(card,lp);
        }
    }

    private void updateCategories(){CourseStore.Category[] cs=CourseStore.Category.values();for(int i=0;i<categoryCards.size();i++){boolean on=cs[i]==selected;categoryCards.get(i).setBackground(Ui.round(this,on?Color.rgb(255,244,246):Color.WHITE,16,on?Ui.RED:Ui.BORDER));}}
    private void status(boolean on){projectorStatus.setText("● "+I18n.t(this,"projector_status")+"\n"+I18n.t(this,on?"connected":"not_connected"));projectorStatus.setTextColor(on?Ui.GREEN:Ui.MUTED);}
}
