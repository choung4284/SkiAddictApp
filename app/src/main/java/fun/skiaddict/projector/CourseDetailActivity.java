package fun.skiaddict.projector;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

public class CourseDetailActivity extends Activity {
    private CourseStore.Course course;
    private int[] values;
    private Preview preview;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        course=CourseStore.byId(getIntent().getStringExtra("course_id"));
        values=course.defaults.clone();
        setContentView(build());
        preview.setData(course,values);
    }
    private boolean compact(){ return getResources().getConfiguration().screenWidthDp<900; }

    private View build(){
        if(compact()){
            ScrollView sv=new ScrollView(this); sv.setFillViewport(true); sv.setBackgroundColor(Ui.BG);
            LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
            root.setPadding(Ui.dp(this,12),Ui.dp(this,10),Ui.dp(this,12),Ui.dp(this,16)); sv.addView(root);
            root.addView(header(),new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,78)));
            root.addView(info(),top(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,110),8));
            root.addView(live(),top(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,390),10));
            root.addView(parameters(false),top(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,650),10));
            return sv;
        }
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Ui.BG);
        root.setPadding(Ui.dp(this,18),Ui.dp(this,12),Ui.dp(this,18),Ui.dp(this,12));
        root.addView(header(),new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,80)));
        LinearLayout body=new LinearLayout(this); body.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams blp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1f); blp.topMargin=Ui.dp(this,10); root.addView(body,blp);
        body.addView(Ui.sidebar(this),new LinearLayout.LayoutParams(Ui.dp(this,155),ViewGroup.LayoutParams.MATCH_PARENT));
        LinearLayout center=new LinearLayout(this); center.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams clp=new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.MATCH_PARENT,1f); clp.leftMargin=Ui.dp(this,12); clp.rightMargin=Ui.dp(this,12);
        body.addView(center,clp);
        center.addView(info(),new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,108)));
        center.addView(live(),top(ViewGroup.LayoutParams.MATCH_PARENT,0,12,1f));
        body.addView(parameters(true),new LinearLayout.LayoutParams(Ui.dp(this,380),ViewGroup.LayoutParams.MATCH_PARENT));
        return root;
    }

    private View header(){
        LinearLayout h=new LinearLayout(this); h.setGravity(Gravity.CENTER_VERTICAL);
        h.addView(Ui.text(this,"Ski Addict",30,Ui.RED,true),new LinearLayout.LayoutParams(Ui.dp(this,230),ViewGroup.LayoutParams.MATCH_PARENT));
        LinearLayout t=new LinearLayout(this); t.setOrientation(LinearLayout.VERTICAL);
        t.addView(Ui.text(this,course.title,26,Ui.NAVY,true));
        t.addView(Ui.text(this,course.category.title+"  •  "+course.subtitle,12,Ui.MUTED,false));
        h.addView(t,new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1f));
        View back=Ui.button(this,"← HOME",false); back.setOnClickListener(v->finish()); h.addView(back,new LinearLayout.LayoutParams(Ui.dp(this,110),Ui.dp(this,44)));
        return h;
    }

    private View info(){
        LinearLayout p=Ui.card(this); p.setPadding(Ui.dp(this,14),Ui.dp(this,12),Ui.dp(this,14),Ui.dp(this,12));
        p.addView(Ui.text(this,"COURSE DETAIL",12,Ui.RED,true));
        p.addView(Ui.text(this,course.title+"  —  "+course.summary,16,Ui.NAVY,true));
        p.addView(Ui.text(this,"LIVE VIEW uses a top-down synthetic ski-mat preview. Parameters are stored independently per course in this prototype.",11,Ui.MUTED,false));
        return p;
    }

    private View live(){
        LinearLayout p=Ui.card(this); p.setPadding(Ui.dp(this,12),Ui.dp(this,10),Ui.dp(this,12),Ui.dp(this,12));
        LinearLayout title=new LinearLayout(this); title.setGravity(Gravity.CENTER_VERTICAL);
        title.addView(Ui.text(this,"LIVE VIEW",20,Ui.RED,true),new LinearLayout.LayoutParams(0,Ui.dp(this,40),1f));
        title.addView(Ui.text(this,"TOP VIEW",11,Ui.NAVY,true)); p.addView(title);
        preview=new Preview(this);
        p.addView(preview,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1f));
        LinearLayout controls=new LinearLayout(this);
        controls.addView(Ui.button(this,"▶ Start",true),new LinearLayout.LayoutParams(0,Ui.dp(this,46),1f));
        LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(0,Ui.dp(this,46),1f); pp.leftMargin=Ui.dp(this,6); controls.addView(Ui.button(this,"Ⅱ Pause",false),pp);
        LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(0,Ui.dp(this,46),1f); rp.leftMargin=Ui.dp(this,6); controls.addView(Ui.button(this,"↻ Reset",false),rp);
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,46)); cp.topMargin=Ui.dp(this,8); p.addView(controls,cp);
        return p;
    }

    private View parameters(boolean internalScroll){
        LinearLayout p=Ui.card(this); p.setPadding(Ui.dp(this,12),Ui.dp(this,10),Ui.dp(this,12),Ui.dp(this,10));
        TextView head=Ui.text(this,"PARAMETERS",19,Color.WHITE,true); head.setGravity(Gravity.CENTER_VERTICAL); head.setPadding(Ui.dp(this,14),0,0,0);
        head.setBackground(Ui.round(this,Ui.RED,16,Color.TRANSPARENT)); p.addView(head,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,54)));
        LinearLayout list=new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL);
        for(int i=0;i<course.parameterNames.length;i++){ final int idx=i; addSlider(list,course.parameterNames[i],course.min[i],course.max[i],values[i],v->{values[idx]=v; preview.setData(course,values);}); }
        if(internalScroll){ ScrollView sv=new ScrollView(this); sv.addView(list); p.addView(sv,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1f));}
        else p.addView(list);
        return p;
    }

    private interface Changed{ void set(int v); }
    private void addSlider(LinearLayout parent,String name,int min,int max,int initial,Changed changed){
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(Ui.dp(this,10),Ui.dp(this,8),Ui.dp(this,10),Ui.dp(this,6));
        box.setBackground(Ui.round(this,Color.rgb(250,251,252),14,Ui.BORDER));
        LinearLayout top=new LinearLayout(this); top.setGravity(Gravity.CENTER_VERTICAL);
        top.addView(Ui.text(this,name,12,Ui.NAVY,true),new LinearLayout.LayoutParams(0,Ui.dp(this,26),1f));
        TextView value=Ui.text(this,String.valueOf(initial),15,Ui.RED,true); top.addView(value); box.addView(top);
        SeekBar s=new SeekBar(this); s.setMax(max-min); s.setProgress(initial-min); s.setProgressTintList(ColorStateList.valueOf(Ui.RED)); s.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ public void onProgressChanged(SeekBar b,int progress,boolean fromUser){int v=min+progress; value.setText(String.valueOf(v)); changed.set(v);} public void onStartTrackingTouch(SeekBar b){} public void onStopTrackingTouch(SeekBar b){} });
        box.addView(s,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,38)));
        LinearLayout range=new LinearLayout(this); range.addView(Ui.text(this,String.valueOf(min),10,Ui.MUTED,false),new LinearLayout.LayoutParams(0,Ui.dp(this,18),1f)); range.addView(Ui.text(this,String.valueOf(max),10,Ui.MUTED,false)); box.addView(range);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,106)); lp.topMargin=Ui.dp(this,7); parent.addView(box,lp);
    }

    private LinearLayout.LayoutParams top(int w,int h,int margin){ LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(w,h); p.topMargin=Ui.dp(this,margin); return p; }
    private LinearLayout.LayoutParams top(int w,int h,int margin,float weight){ LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(w,h,weight); p.topMargin=Ui.dp(this,margin); return p; }

    static final class Preview extends View {
        private final Paint fill=new Paint(Paint.ANTI_ALIAS_FLAG), stroke=new Paint(Paint.ANTI_ALIAS_FLAG);
        private CourseStore.Course course; private int[] v;
        Preview(android.content.Context c){ super(c); stroke.setStyle(Paint.Style.STROKE); stroke.setStrokeCap(Paint.Cap.ROUND); }
        void setData(CourseStore.Course c,int[] values){ course=c; v=values; invalidate(); }
        @Override protected void onDraw(Canvas c){
            super.onDraw(c); int w=getWidth(),h=getHeight();
            fill.setColor(Color.rgb(248,250,252)); c.drawRect(0,0,w,h,fill);
            fill.setColor(Color.rgb(42,45,50)); c.drawRect(0,0,w*.055f,h,fill); c.drawRect(w*.945f,0,w,h,fill);
            fill.setColor(Color.rgb(230,234,238)); for(int i=0;i<12;i++){float y=i*h/11f;c.drawRect(w*.06f,y,w*.94f,y+1.2f,fill);}
            if(course==null)return;
            switch(course.category){
                case ALPINE: drawGates(c,w,h); break;
                case S_CURVE: drawCurve(c,w,h); break;
                case STRAIGHT: drawStraight(c,w,h); break;
                case KIDS: drawKids(c,w,h); break;
                case OBSTACLES: drawObstacles(c,w,h); break;
            }
            fill.setColor(Ui.NAVY); float sw=Math.min(w,h)*.025f; c.drawRoundRect(new RectF(w*.48f,h*.86f,w*.49f+sw,h*1.02f),sw*.2f,sw*.2f,fill); c.drawRoundRect(new RectF(w*.52f-sw,h*.86f,w*.52f,h*1.02f),sw*.2f,sw*.2f,fill);
        }
        private float size(int w,int h){ return Math.min(w,h)*(.035f+(v[3]-10)/600f); }
        private void drawGates(Canvas c,int w,int h){ float s=size(w,h), lateral=w*(.12f+v[1]/500f), gap=h*(.10f+v[2]/550f); for(int i=0;i<6;i++){float y=h*.12f+i*gap;float x=w*.5f+((i%2==0)?-1:1)*lateral*.45f;gate(c,x,y,s,i%2==0?Ui.RED:Color.rgb(35,110,235));}}
        private void drawCurve(Canvas c,int w,int h){float amp=w*(.08f+v[1]/420f); stroke.setColor(Color.rgb(80,180,240)); stroke.setStrokeWidth(Math.max(14f,size(w,h)*1.4f)); Path p=new Path(); for(int y=0;y<=h;y+=10){float x=w*.5f+(float)Math.sin(y*(.006+v[2]/8000.0))*amp;if(y==0)p.moveTo(x,y);else p.lineTo(x,y);}c.drawPath(p,stroke);}
        private void drawStraight(Canvas c,int w,int h){stroke.setColor(Color.rgb(40,160,95));stroke.setStrokeWidth(Math.max(5f,size(w,h)*.22f));float x=w*.5f;for(float y=h*.08f;y<h*.9f;y+=h*.12f)c.drawLine(x,y,x,y+h*.055f,stroke);}
        private void drawKids(Canvas c,int w,int h){float s=size(w,h);float[] xs={.28f,.62f,.42f,.72f,.25f,.58f};float[] ys={.16f,.28f,.42f,.54f,.68f,.78f};for(int i=0;i<xs.length;i++){float x=w*xs[i],y=h*ys[i]; if(course.id.contains("penguin"))penguin(c,x,y,s); else if(course.id.contains("coin")||course.id.contains("color")||course.id.contains("combo"))star(c,x,y,s*.55f); else balloon(c,x,y,s,i%2==0?Ui.RED:Color.rgb(35,110,235));}}
        private void drawObstacles(Canvas c,int w,int h){float s=size(w,h);float[] xs={.28f,.65f,.42f,.72f,.25f,.58f};float[] ys={.18f,.28f,.42f,.54f,.68f,.78f};if(course.id.contains("maze")){stroke.setColor(Color.rgb(110,190,240));stroke.setStrokeWidth(s*1.1f);Path p=new Path();p.moveTo(w*.3f,h*.18f);p.lineTo(w*.72f,h*.18f);p.lineTo(w*.72f,h*.38f);p.lineTo(w*.45f,h*.38f);p.lineTo(w*.45f,h*.58f);p.lineTo(w*.76f,h*.58f);p.lineTo(w*.76f,h*.8f);c.drawPath(p,stroke);return;}for(int i=0;i<xs.length;i++)cone(c,w*xs[i],h*ys[i],s);}
        private void gate(Canvas c,float x,float y,float s,int color){stroke.setColor(color);stroke.setStrokeWidth(Math.max(4f,s*.12f));c.drawLine(x-s*.55f,y-s*.55f,x-s*.55f,y+s*.55f,stroke);c.drawLine(x+s*.55f,y-s*.55f,x+s*.55f,y+s*.55f,stroke);fill.setColor(color);c.drawRoundRect(new RectF(x-s*.55f,y-s*.22f,x+s*.55f,y+s*.13f),s*.08f,s*.08f,fill);}
        private void balloon(Canvas c,float x,float y,float s,int color){fill.setColor(color);c.drawOval(new RectF(x-s*.35f,y-s*.5f,x+s*.35f,y+s*.25f),fill);stroke.setColor(Color.GRAY);stroke.setStrokeWidth(2f);c.drawLine(x,y+s*.25f,x,y+s*.7f,stroke);}
        private void star(Canvas c,float x,float y,float r){Path p=new Path();for(int i=0;i<10;i++){double a=-Math.PI/2+i*Math.PI/5;float rr=i%2==0?r:r*.42f;float px=x+(float)Math.cos(a)*rr,py=y+(float)Math.sin(a)*rr;if(i==0)p.moveTo(px,py);else p.lineTo(px,py);}p.close();fill.setColor(Ui.GOLD);c.drawPath(p,fill);}
        private void penguin(Canvas c,float x,float y,float s){fill.setColor(Ui.NAVY);c.drawOval(new RectF(x-s*.36f,y-s*.5f,x+s*.36f,y+s*.42f),fill);fill.setColor(Color.WHITE);c.drawOval(new RectF(x-s*.23f,y-s*.22f,x+s*.23f,y+s*.34f),fill);}
        private void cone(Canvas c,float x,float y,float s){Path p=new Path();p.moveTo(x,y-s*.5f);p.lineTo(x-s*.3f,y+s*.32f);p.lineTo(x+s*.3f,y+s*.32f);p.close();fill.setColor(Color.rgb(255,105,30));c.drawPath(p,fill);}
    }
}
