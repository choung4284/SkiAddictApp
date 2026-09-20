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

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(build());
        updateCategories();
        renderCourses();
    }

    private int widthDp(){ return getResources().getConfiguration().screenWidthDp; }
    private int clamp(int v,int min,int max){ return Math.max(min,Math.min(max,v)); }
    private int navWidth(){ return clamp(Math.round(widthDp()*0.14f),90,155); }

    private View build(){
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Ui.BG);
        root.setPadding(Ui.dp(this,10),Ui.dp(this,7),Ui.dp(this,10),Ui.dp(this,8));

        root.addView(header(),new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,60)));

        LinearLayout body=new LinearLayout(this);
        body.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams blp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1f);
        blp.topMargin=Ui.dp(this,7);
        root.addView(body,blp);

        body.addView(Ui.sidebar(this),new LinearLayout.LayoutParams(Ui.dp(this,navWidth()),ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout center=new LinearLayout(this);
        center.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams clp=new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.MATCH_PARENT,1f);
        clp.leftMargin=Ui.dp(this,8);
        body.addView(center,clp);

        center.addView(categoryPanel(),new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,145)));
        LinearLayout.LayoutParams courseLp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1f);
        courseLp.topMargin=Ui.dp(this,8);
        center.addView(coursePanel(),courseLp);
        return root;
    }

    private View header(){
        LinearLayout h=new LinearLayout(this);
        h.setGravity(Gravity.CENTER_VERTICAL);
        TextView brand=Ui.text(this,"Ski Addict",clamp(widthDp()/30,22,30),Ui.RED,true);
        h.addView(brand,new LinearLayout.LayoutParams(Ui.dp(this,clamp(widthDp()/4,170,250)),ViewGroup.LayoutParams.MATCH_PARENT));
        TextView sub=Ui.text(this,"HOME  •  21 COURSES  •  PHONE / TABLET SAME LAYOUT",clamp(widthDp()/70,10,14),Ui.NAVY,true);
        h.addView(sub,new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1f));
        LinearLayout status=Ui.card(this);
        status.setGravity(Gravity.CENTER);
        status.addView(Ui.text(this,"● Projector",11,Ui.NAVY,true));
        status.addView(Ui.text(this,"Ready",10,Color.rgb(0,150,80),false));
        h.addView(status,new LinearLayout.LayoutParams(Ui.dp(this,clamp(widthDp()/6,95,145)),Ui.dp(this,48)));
        return h;
    }

    private View categoryPanel(){
        LinearLayout panel=Ui.card(this);
        LinearLayout title=new LinearLayout(this);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setPadding(Ui.dp(this,12),Ui.dp(this,4),Ui.dp(this,10),0);
        title.addView(Ui.text(this,"CATEGORY",17,Ui.NAVY,true),new LinearLayout.LayoutParams(0,Ui.dp(this,34),1f));
        title.addView(Ui.text(this,"เลือกด้านบน → Course ด้านล่างเปลี่ยนตาม",10,Ui.MUTED,false));
        panel.addView(title);

        HorizontalScrollView hsv=new HorizontalScrollView(this);
        hsv.setHorizontalScrollBarEnabled(false);
        LinearLayout row=new LinearLayout(this);
        row.setPadding(Ui.dp(this,8),Ui.dp(this,4),Ui.dp(this,8),Ui.dp(this,7));
        hsv.addView(row);

        for(CourseStore.Category cat:CourseStore.Category.values()){
            LinearLayout c=Ui.card(this);
            c.setPadding(Ui.dp(this,10),Ui.dp(this,7),Ui.dp(this,10),Ui.dp(this,6));
            c.addView(Ui.text(this,cat.icon,22,cat==CourseStore.Category.KIDS?Ui.GOLD:Ui.RED,true));
            c.addView(Ui.text(this,cat.title,12,Ui.NAVY,true));
            c.addView(Ui.text(this,CourseStore.byCategory(cat).size()+" courses",9,Ui.MUTED,false));
            c.setOnClickListener(v->{ selected=cat; updateCategories(); renderCourses(); });
            int cardW=clamp(widthDp()/6,125,180);
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(Ui.dp(this,cardW),Ui.dp(this,82));
            p.rightMargin=Ui.dp(this,6);
            row.addView(c,p);
            categoryCards.add(c);
        }
        panel.addView(hsv,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1f));
        return panel;
    }

    private View coursePanel(){
        LinearLayout panel=Ui.card(this);
        LinearLayout title=new LinearLayout(this);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setPadding(Ui.dp(this,12),Ui.dp(this,4),Ui.dp(this,10),0);
        title.addView(Ui.text(this,"COURSES",17,Ui.NAVY,true),new LinearLayout.LayoutParams(0,Ui.dp(this,34),1f));
        title.addView(Ui.text(this,"Tap to open LIVE VIEW + course parameters",10,Ui.RED,true));
        panel.addView(title);

        ScrollView sv=new ScrollView(this);
        courseBox=new LinearLayout(this);
        courseBox.setOrientation(LinearLayout.VERTICAL);
        courseBox.setPadding(Ui.dp(this,8),Ui.dp(this,2),Ui.dp(this,8),Ui.dp(this,8));
        sv.addView(courseBox);
        panel.addView(sv,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1f));
        return panel;
    }

    private void updateCategories(){
        CourseStore.Category[] cats=CourseStore.Category.values();
        for(int i=0;i<categoryCards.size()&&i<cats.length;i++){
            boolean on=cats[i]==selected;
            categoryCards.get(i).setBackground(Ui.round(this,on?Color.rgb(255,245,246):Color.WHITE,18,on?Ui.RED:Ui.BORDER));
        }
    }

    private void renderCourses(){
        if(courseBox==null)return;
        courseBox.removeAllViews();
        for(CourseStore.Course course:CourseStore.byCategory(selected)){
            LinearLayout card=Ui.card(this);
            card.setPadding(Ui.dp(this,9),Ui.dp(this,7),Ui.dp(this,9),Ui.dp(this,7));

            LinearLayout top=new LinearLayout(this);
            top.setGravity(Gravity.CENTER_VERTICAL);
            top.addView(Ui.text(this,course.category.icon,22,course.category==CourseStore.Category.KIDS?Ui.GOLD:Ui.RED,true),
                    new LinearLayout.LayoutParams(Ui.dp(this,34),Ui.dp(this,34)));

            LinearLayout texts=new LinearLayout(this);
            texts.setOrientation(LinearLayout.VERTICAL);
            texts.addView(Ui.text(this,course.title,13,Ui.NAVY,true));
            texts.addView(Ui.text(this,course.subtitle+" • "+course.category.title,10,Ui.MUTED,false));
            LinearLayout.LayoutParams tlp=new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1f);
            tlp.leftMargin=Ui.dp(this,6);
            top.addView(texts,tlp);

            top.addView(Ui.button(this,"Open",true),new LinearLayout.LayoutParams(Ui.dp(this,72),Ui.dp(this,34)));
            card.addView(top);

            TextView desc=Ui.text(this,course.summary,10,Ui.MUTED,false);
            desc.setPadding(Ui.dp(this,40),Ui.dp(this,3),0,0);
            card.addView(desc);

            card.setOnClickListener(v->{
                Intent it=new Intent(this,CourseDetailActivity.class);
                it.putExtra("course_id",course.id);
                startActivity(it);
            });

            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,68));
            lp.bottomMargin=Ui.dp(this,5);
            courseBox.addView(card,lp);
        }
    }
}
