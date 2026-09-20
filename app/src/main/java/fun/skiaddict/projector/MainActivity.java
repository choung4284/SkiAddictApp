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
    private boolean compact(){ return getResources().getConfiguration().screenWidthDp<900; }

    private View build(){
        if(compact()){
            ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true); scroll.setBackgroundColor(Ui.BG);
            LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
            root.setPadding(Ui.dp(this,12),Ui.dp(this,10),Ui.dp(this,12),Ui.dp(this,16));
            scroll.addView(root);
            root.addView(header(),new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,74)));
            root.addView(categoryPanel(),lpTop(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,165),8));
            root.addView(coursePanel(),lpTop(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,620),10));
            return scroll;
        }
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Ui.BG);
        root.setPadding(Ui.dp(this,18),Ui.dp(this,12),Ui.dp(this,18),Ui.dp(this,12));
        root.addView(header(),new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,80)));
        LinearLayout body=new LinearLayout(this); body.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams blp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1f); blp.topMargin=Ui.dp(this,10);
        root.addView(body,blp);
        body.addView(Ui.sidebar(this),new LinearLayout.LayoutParams(Ui.dp(this,155),ViewGroup.LayoutParams.MATCH_PARENT));
        LinearLayout center=new LinearLayout(this); center.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams clp=new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.MATCH_PARENT,1f); clp.leftMargin=Ui.dp(this,12);
        body.addView(center,clp);
        center.addView(categoryPanel(),new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,190)));
        center.addView(coursePanel(),lpTop(ViewGroup.LayoutParams.MATCH_PARENT,0,12,1f));
        return root;
    }

    private View header(){
        LinearLayout h=new LinearLayout(this); h.setGravity(Gravity.CENTER_VERTICAL);
        TextView brand=Ui.text(this,"Ski Addict",32,Ui.RED,true);
        h.addView(brand,new LinearLayout.LayoutParams(Ui.dp(this,260),ViewGroup.LayoutParams.MATCH_PARENT));
        TextView sub=Ui.text(this,"HOME  •  25 COURSES  •  RESPONSIVE",14,Ui.NAVY,true);
        h.addView(sub,new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1f));
        LinearLayout status=Ui.card(this); status.setGravity(Gravity.CENTER);
        status.addView(Ui.text(this,"● Projector",12,Ui.NAVY,true));
        status.addView(Ui.text(this,"Ready for HDMI",11,Color.rgb(0,150,80),false));
        h.addView(status,new LinearLayout.LayoutParams(Ui.dp(this,145),Ui.dp(this,58)));
        return h;
    }

    private View categoryPanel(){
        LinearLayout panel=Ui.card(this);
        LinearLayout title=new LinearLayout(this); title.setGravity(Gravity.CENTER_VERTICAL);
        title.setPadding(Ui.dp(this,16),Ui.dp(this,8),Ui.dp(this,12),0);
        title.addView(Ui.text(this,"CATEGORY",21,Ui.NAVY,true),new LinearLayout.LayoutParams(0,Ui.dp(this,44),1f));
        title.addView(Ui.text(this,"Choose a category; courses below update",12,Ui.MUTED,false));
        panel.addView(title);
        HorizontalScrollView hsv=new HorizontalScrollView(this); hsv.setHorizontalScrollBarEnabled(false);
        LinearLayout row=new LinearLayout(this); row.setPadding(Ui.dp(this,12),Ui.dp(this,6),Ui.dp(this,12),Ui.dp(this,12));
        hsv.addView(row);
        for(CourseStore.Category cat:CourseStore.Category.values()){
            LinearLayout c=Ui.card(this); c.setPadding(Ui.dp(this,14),Ui.dp(this,10),Ui.dp(this,14),Ui.dp(this,10));
            c.addView(Ui.text(this,cat.icon,28,cat==CourseStore.Category.KIDS?Ui.GOLD:Ui.RED,true));
            c.addView(Ui.text(this,cat.title,15,Ui.NAVY,true));
            c.addView(Ui.text(this,CourseStore.byCategory(cat).size()+" courses",11,Ui.MUTED,false));
            c.setOnClickListener(v->{ selected=cat; updateCategories(); renderCourses(); });
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(Ui.dp(this,compact()?165:190),Ui.dp(this,102)); p.rightMargin=Ui.dp(this,8);
            row.addView(c,p); categoryCards.add(c);
        }
        panel.addView(hsv,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1f));
        return panel;
    }

    private View coursePanel(){
        LinearLayout panel=Ui.card(this);
        LinearLayout title=new LinearLayout(this); title.setGravity(Gravity.CENTER_VERTICAL);
        title.setPadding(Ui.dp(this,16),Ui.dp(this,8),Ui.dp(this,12),0);
        title.addView(Ui.text(this,"COURSES",21,Ui.NAVY,true),new LinearLayout.LayoutParams(0,Ui.dp(this,44),1f));
        title.addView(Ui.text(this,"Tap a course to open LIVE VIEW + Parameters",12,Ui.RED,true));
        panel.addView(title);
        ScrollView sv=new ScrollView(this); courseBox=new LinearLayout(this); courseBox.setOrientation(LinearLayout.VERTICAL);
        courseBox.setPadding(Ui.dp(this,12),Ui.dp(this,4),Ui.dp(this,12),Ui.dp(this,12)); sv.addView(courseBox);
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
        if(courseBox==null)return; courseBox.removeAllViews();
        for(CourseStore.Course course:CourseStore.byCategory(selected)){
            LinearLayout card=Ui.card(this); card.setPadding(Ui.dp(this,12),Ui.dp(this,10),Ui.dp(this,12),Ui.dp(this,10));
            LinearLayout top=new LinearLayout(this); top.setGravity(Gravity.CENTER_VERTICAL);
            top.addView(Ui.text(this,course.category.icon,26,course.category==CourseStore.Category.KIDS?Ui.GOLD:Ui.RED,true),
                    new LinearLayout.LayoutParams(Ui.dp(this,40),Ui.dp(this,40)));
            LinearLayout texts=new LinearLayout(this); texts.setOrientation(LinearLayout.VERTICAL);
            texts.addView(Ui.text(this,course.title,16,Ui.NAVY,true));
            texts.addView(Ui.text(this,course.subtitle+"  •  "+course.category.title,12,Ui.MUTED,false));
            LinearLayout.LayoutParams tlp=new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1f); tlp.leftMargin=Ui.dp(this,8);
            top.addView(texts,tlp);
            top.addView(Ui.button(this,"Open",true),new LinearLayout.LayoutParams(Ui.dp(this,90),Ui.dp(this,40)));
            card.addView(top);
            TextView desc=Ui.text(this,course.summary,12,Ui.MUTED,false); desc.setPadding(Ui.dp(this,48),Ui.dp(this,5),0,0); card.addView(desc);
            card.setOnClickListener(v->{ Intent it=new Intent(this,CourseDetailActivity.class); it.putExtra("course_id",course.id); startActivity(it); });
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,86)); lp.bottomMargin=Ui.dp(this,8);
            courseBox.addView(card,lp);
        }
    }

    private LinearLayout.LayoutParams lpTop(int w,int h,int top){
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(w,h); lp.topMargin=Ui.dp(this,top); return lp;
    }
    private LinearLayout.LayoutParams lpTop(int w,int h,int top,float weight){
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(w,h,weight); lp.topMargin=Ui.dp(this,top); return lp;
    }
}
