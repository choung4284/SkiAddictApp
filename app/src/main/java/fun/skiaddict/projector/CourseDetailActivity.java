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
    private TextView projectorStatus;
    private ProjectorDisplayHost projectorHost;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        course=CourseStore.byId(getIntent().getStringExtra("course_id"));
        values=course.defaults.clone();
        setContentView(build());
        preview.setData(course,values);
        ProjectorSession.get().selectCourse(course,values);
        projectorHost=new ProjectorDisplayHost(this,(connected,name)->runOnUiThread(()->updateProjectorStatus(connected,name)));
    }

    @Override protected void onResume(){
        super.onResume();
        ProjectorSession.get().selectCourse(course,values);
        if(projectorHost!=null) projectorHost.start();
    }

    @Override protected void onPause(){
        if(projectorHost!=null) projectorHost.stop();
        super.onPause();
    }

    private int widthDp(){ return getResources().getConfiguration().screenWidthDp; }
    private int clamp(int v,int min,int max){ return Math.max(min,Math.min(max,v)); }
    private int navWidth(){ return clamp(Math.round(widthDp()*0.14f),90,155); }
    private int paramWidth(){ return clamp(Math.round(widthDp()*0.31f),210,380); }

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
        clp.rightMargin=Ui.dp(this,8);
        body.addView(center,clp);

        center.addView(info(),new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,82)));
        LinearLayout.LayoutParams liveLp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1f);
        liveLp.topMargin=Ui.dp(this,7);
        center.addView(live(),liveLp);

        body.addView(parameters(),new LinearLayout.LayoutParams(Ui.dp(this,paramWidth()),ViewGroup.LayoutParams.MATCH_PARENT));
        return root;
    }

    private View header(){
        LinearLayout h=new LinearLayout(this);
        h.setGravity(Gravity.CENTER_VERTICAL);
        h.addView(Ui.text(this,"Ski Addict",clamp(widthDp()/30,22,30),Ui.RED,true),
                new LinearLayout.LayoutParams(Ui.dp(this,clamp(widthDp()/4,170,230)),ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout t=new LinearLayout(this);
        t.setOrientation(LinearLayout.VERTICAL);
        t.addView(Ui.text(this,course.title,clamp(widthDp()/45,17,24),Ui.NAVY,true));
        t.addView(Ui.text(this,course.category.title+" • "+course.subtitle,10,Ui.MUTED,false));
        h.addView(t,new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1f));

        projectorStatus=Ui.text(this,"● HDMI\nNot connected",9,Ui.MUTED,true);
        projectorStatus.setGravity(Gravity.CENTER);
        projectorStatus.setBackground(Ui.round(this,Color.WHITE,12,Ui.BORDER));
        h.addView(projectorStatus,new LinearLayout.LayoutParams(Ui.dp(this,118),Ui.dp(this,40)));

        View back=Ui.button(this,"← HOME",false);
        back.setOnClickListener(v->finish());
        LinearLayout.LayoutParams backLp=new LinearLayout.LayoutParams(Ui.dp(this,88),Ui.dp(this,38));
        backLp.leftMargin=Ui.dp(this,5);
        h.addView(back,backLp);
        return h;
    }

    private void updateProjectorStatus(boolean connected,String name){
        if(projectorStatus==null)return;
        projectorStatus.setText(connected?"● HDMI\nConnected":"● HDMI\nNot connected");
        projectorStatus.setTextColor(connected?Color.rgb(0,150,80):Ui.MUTED);
    }

    private View info(){
        LinearLayout p=Ui.card(this);
        p.setPadding(Ui.dp(this,10),Ui.dp(this,7),Ui.dp(this,10),Ui.dp(this,6));
        p.addView(Ui.text(this,"COURSE DETAIL",10,Ui.RED,true));
        p.addView(Ui.text(this,course.summary,12,Ui.NAVY,true));
        p.addView(Ui.text(this,"LIVE VIEW left • Parameters right • same arrangement on phone and tablet",9,Ui.MUTED,false));
        return p;
    }

    private View live(){
        LinearLayout p=Ui.card(this);
        p.setPadding(Ui.dp(this,8),Ui.dp(this,7),Ui.dp(this,8),Ui.dp(this,8));

        LinearLayout title=new LinearLayout(this);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.addView(Ui.text(this,"LIVE VIEW",16,Ui.RED,true),new LinearLayout.LayoutParams(0,Ui.dp(this,32),1f));
        title.addView(Ui.text(this,"TOP VIEW",9,Ui.NAVY,true));
        p.addView(title);

        preview=new Preview(this);
        p.addView(preview,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1f));

        LinearLayout controls=new LinearLayout(this);
        View start=Ui.button(this,"▶ Start",true);
        start.setOnClickListener(v->ProjectorSession.get().play());
        controls.addView(start,new LinearLayout.LayoutParams(0,Ui.dp(this,38),1f));
        LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(0,Ui.dp(this,38),1f);
        pp.leftMargin=Ui.dp(this,5);
        View pause=Ui.button(this,"Ⅱ Pause",false);
        pause.setOnClickListener(v->ProjectorSession.get().pause());
        controls.addView(pause,pp);
        LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(0,Ui.dp(this,38),1f);
        rp.leftMargin=Ui.dp(this,5);
        View reset=Ui.button(this,"↻ Reset",false);
        reset.setOnClickListener(v->ProjectorSession.get().reset());
        controls.addView(reset,rp);

        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,38));
        cp.topMargin=Ui.dp(this,5);
        p.addView(controls,cp);
        return p;
    }

    private View parameters(){
        LinearLayout p=Ui.card(this);
        p.setPadding(Ui.dp(this,8),Ui.dp(this,7),Ui.dp(this,8),Ui.dp(this,7));

        TextView head=Ui.text(this,"PARAMETERS",15,Color.WHITE,true);
        head.setGravity(Gravity.CENTER_VERTICAL);
        head.setPadding(Ui.dp(this,10),0,0,0);
        head.setBackground(Ui.round(this,Ui.RED,14,Color.TRANSPARENT));
        p.addView(head,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,42)));

        ScrollView sv=new ScrollView(this);
        LinearLayout list=new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        sv.addView(list);

        for(int i=0;i<course.parameterNames.length;i++){
            final int idx=i;
            addSlider(list,course.parameterNames[i],course.min[i],course.max[i],values[i],v->{
                values[idx]=v;
                preview.setData(course,values);
                ProjectorSession.get().updateValues(values);
            });
        }
        LinearLayout.LayoutParams slp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1f);
        slp.topMargin=Ui.dp(this,5);
        p.addView(sv,slp);
        return p;
    }

    private interface Changed{ void set(int v); }

    private void addSlider(LinearLayout parent,String name,int min,int max,int initial,Changed changed){
        LinearLayout box=new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(Ui.dp(this,8),Ui.dp(this,5),Ui.dp(this,8),Ui.dp(this,4));
        box.setBackground(Ui.round(this,Color.rgb(250,251,252),12,Ui.BORDER));

        LinearLayout top=new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.addView(Ui.text(this,name,10,Ui.NAVY,true),new LinearLayout.LayoutParams(0,Ui.dp(this,22),1f));
        TextView value=Ui.text(this,String.valueOf(initial),12,Ui.RED,true);
        top.addView(value);
        box.addView(top);

        SeekBar s=new SeekBar(this);
        s.setMax(max-min);
        s.setProgress(initial-min);
        s.setProgressTintList(ColorStateList.valueOf(Ui.RED));
        s.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar b,int progress,boolean fromUser){
                int v=min+progress;
                value.setText(String.valueOf(v));
                changed.set(v);
            }
            public void onStartTrackingTouch(SeekBar b){}
            public void onStopTrackingTouch(SeekBar b){}
        });
        box.addView(s,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,30)));

        LinearLayout range=new LinearLayout(this);
        range.addView(Ui.text(this,String.valueOf(min),8,Ui.MUTED,false),new LinearLayout.LayoutParams(0,Ui.dp(this,14),1f));
        range.addView(Ui.text(this,String.valueOf(max),8,Ui.MUTED,false));
        box.addView(range);

        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Ui.dp(this,82));
        lp.topMargin=Ui.dp(this,5);
        parent.addView(box,lp);
    }

    static final class Preview extends View {
        private final Paint fill=new Paint(Paint.ANTI_ALIAS_FLAG), stroke=new Paint(Paint.ANTI_ALIAS_FLAG);
        private CourseStore.Course course;
        private int[] v;

        Preview(android.content.Context c){
            super(c);
            stroke.setStyle(Paint.Style.STROKE);
            stroke.setStrokeCap(Paint.Cap.ROUND);
        }

        void setData(CourseStore.Course c,int[] values){
            course=c; v=values; invalidate();
        }

        @Override protected void onDraw(Canvas c){
            super.onDraw(c);
            int w=getWidth(),h=getHeight();
            fill.setColor(Color.rgb(248,250,252));
            c.drawRect(0,0,w,h,fill);
            fill.setColor(Color.rgb(42,45,50));
            c.drawRect(0,0,w*.055f,h,fill);
            c.drawRect(w*.945f,0,w,h,fill);
            fill.setColor(Color.rgb(230,234,238));
            for(int i=0;i<12;i++){
                float y=i*h/11f;
                c.drawRect(w*.06f,y,w*.94f,y+1.2f,fill);
            }

            if(course==null)return;
            switch(course.category){
                case ALPINE: drawAlpine(c,w,h); break;
                case S_CURVE: drawCurve(c,w,h); break;
                case STRAIGHT: drawStraight(c,w,h); break;
                case KIDS: drawKids(c,w,h); break;
                case OBSTACLES: drawObstacles(c,w,h); break;
            }
        }

        private float size(int w,int h){
            return Math.min(w,h)*(.035f+(v[3]-10)/600f);
        }

        private void drawAlpine(Canvas c,int w,int h){
            if(course.id.equals("wide_turn")){ drawCurve(c,w,h); return; }
            if(course.id.equals("figure_8")){ drawFigure8(c,w,h); return; }

            float s=size(w,h);
            float lateral=w*(.12f+v[1]/500f);
            float gap=h*(.10f+v[2]/550f);
            for(int i=0;i<7;i++){
                float y=h*.09f+i*gap;
                float rand=course.id.equals("random_gates")?(float)Math.sin(i*2.1)*.55f:((i%2==0)?-1f:1f);
                float x=w*.5f+rand*lateral*.45f;
                gate(c,x,y,s,i%2==0?Ui.RED:Color.rgb(35,110,235));
            }
        }

        private void drawCurve(Canvas c,int w,int h){
            float amp=w*(.08f+v[1]/420f);
            int color=course.id.equals("candy_trail")?Color.rgb(244,130,190):Color.rgb(80,180,240);
            stroke.setColor(color);
            stroke.setStrokeWidth(Math.max(12f,size(w,h)*1.4f));
            Path p=new Path();
            for(int y=0;y<=h;y+=8){
                float x=w*.5f+(float)Math.sin(y*(.006+v[2]/8000.0))*amp;
                if(y==0)p.moveTo(x,y); else p.lineTo(x,y);
            }
            c.drawPath(p,stroke);

            if(course.id.equals("snowman_trail")){
                for(int i=0;i<4;i++) snowman(c,w*(.30f+.14f*(i%3)),h*(.18f+i*.2f),size(w,h));
            } else if(course.id.equals("follow_leader")){
                arrow(c,w*.5f,h*.26f,size(w,h),Ui.RED);
                arrow(c,w*.58f,h*.57f,size(w,h),Ui.RED);
            } else if(course.id.equals("candy_trail")){
                for(int i=0;i<5;i++) candy(c,w*(.28f+.12f*(i%3)),h*(.16f+i*.16f),size(w,h));
            }
        }

        private void drawFigure8(Canvas c,int w,int h){
            float a=w*.18f;
            stroke.setColor(Color.rgb(80,180,240));
            stroke.setStrokeWidth(Math.max(12f,size(w,h)*1.25f));
            Path p=new Path();
            for(int i=0;i<=180;i++){
                double t=i*Math.PI*2/180.0;
                float x=w*.5f+(float)Math.sin(t)*a;
                float y=h*.5f+(float)Math.sin(t)* (float)Math.cos(t)*h*.38f;
                if(i==0)p.moveTo(x,y); else p.lineTo(x,y);
            }
            c.drawPath(p,stroke);
        }

        private void drawStraight(Canvas c,int w,int h){
            float x=w*.5f;
            stroke.setColor(Color.rgb(40,150,235));
            stroke.setStrokeWidth(Math.max(4f,size(w,h)*.2f));
            c.drawLine(x,h*.07f,x,h*.93f,stroke);

            if(course.id.equals("reaction_lights")){
                int[] colors={Color.RED,Color.GREEN,Color.LTGRAY,Color.LTGRAY,Color.LTGRAY};
                for(int i=0;i<5;i++){
                    float y=h*(.15f+i*.17f);
                    fill.setColor(colors[(i+1)%colors.length]);
                    c.drawCircle(x,y,size(w,h)*.42f,fill);
                }
            } else {
                for(int i=0;i<6;i++){
                    float y=h*(.13f+i*.14f);
                    fill.setColor(i%2==0?Color.rgb(70,185,110):Color.rgb(150,220,180));
                    c.drawRoundRect(new RectF(x-w*.11f,y-size(w,h)*.22f,x+w*.11f,y+size(w,h)*.22f),8,8,fill);
                }
            }
        }

        private void drawKids(Canvas c,int w,int h){
            float s=size(w,h);
            float[] xs={.28f,.62f,.42f,.72f,.25f,.58f};
            float[] ys={.16f,.28f,.42f,.54f,.68f,.78f};

            for(int i=0;i<xs.length;i++){
                float x=w*xs[i],y=h*ys[i];
                if(course.id.contains("penguin")) penguin(c,x,y,s);
                else if(course.id.contains("coin")) coin(c,x,y,s);
                else if(course.id.contains("color")) colorTarget(c,x,y,s,i);
                else if(course.id.contains("combo")) {
                    if(i%3==0) balloon(c,x,y,s,Ui.RED);
                    else if(i%3==1) star(c,x,y,s*.55f);
                    else coin(c,x,y,s);
                }
                else if(course.id.contains("animal")) paw(c,x,y,s,i);
                else balloon(c,x,y,s,i%2==0?Ui.RED:Color.rgb(35,110,235));
            }
        }

        private void drawObstacles(Canvas c,int w,int h){
            float s=size(w,h);
            if(course.id.contains("maze")){
                stroke.setColor(Color.rgb(110,190,240));
                stroke.setStrokeWidth(s*1.1f);
                Path p=new Path();
                p.moveTo(w*.3f,h*.18f);
                p.lineTo(w*.72f,h*.18f);
                p.lineTo(w*.72f,h*.38f);
                p.lineTo(w*.45f,h*.38f);
                p.lineTo(w*.45f,h*.58f);
                p.lineTo(w*.76f,h*.58f);
                p.lineTo(w*.76f,h*.8f);
                c.drawPath(p,stroke);
                return;
            }

            float[] xs={.28f,.65f,.42f,.72f,.25f,.58f};
            float[] ys={.18f,.28f,.42f,.54f,.68f,.78f};
            for(int i=0;i<xs.length;i++){
                if(course.id.contains("auto_difficulty") && i>2) rock(c,w*xs[i],h*ys[i],s);
                else cone(c,w*xs[i],h*ys[i],s);
            }
        }

        private void gate(Canvas c,float x,float y,float s,int color){
            stroke.setColor(color);
            stroke.setStrokeWidth(Math.max(4f,s*.12f));
            c.drawLine(x-s*.55f,y-s*.55f,x-s*.55f,y+s*.55f,stroke);
            c.drawLine(x+s*.55f,y-s*.55f,x+s*.55f,y+s*.55f,stroke);
            fill.setColor(color);
            c.drawRoundRect(new RectF(x-s*.55f,y-s*.22f,x+s*.55f,y+s*.13f),s*.08f,s*.08f,fill);
        }

        private void balloon(Canvas c,float x,float y,float s,int color){
            fill.setColor(color);
            c.drawOval(new RectF(x-s*.35f,y-s*.5f,x+s*.35f,y+s*.25f),fill);
            stroke.setColor(Color.GRAY); stroke.setStrokeWidth(2f);
            c.drawLine(x,y+s*.25f,x,y+s*.7f,stroke);
        }

        private void star(Canvas c,float x,float y,float r){
            Path p=new Path();
            for(int i=0;i<10;i++){
                double a=-Math.PI/2+i*Math.PI/5;
                float rr=i%2==0?r:r*.42f;
                float px=x+(float)Math.cos(a)*rr, py=y+(float)Math.sin(a)*rr;
                if(i==0)p.moveTo(px,py); else p.lineTo(px,py);
            }
            p.close(); fill.setColor(Ui.GOLD); c.drawPath(p,fill);
        }

        private void penguin(Canvas c,float x,float y,float s){
            fill.setColor(Ui.NAVY);
            c.drawOval(new RectF(x-s*.36f,y-s*.5f,x+s*.36f,y+s*.42f),fill);
            fill.setColor(Color.WHITE);
            c.drawOval(new RectF(x-s*.23f,y-s*.22f,x+s*.23f,y+s*.34f),fill);
            fill.setColor(Ui.GOLD);
            c.drawCircle(x,y-s*.03f,s*.08f,fill);
        }

        private void cone(Canvas c,float x,float y,float s){
            Path p=new Path();
            p.moveTo(x,y-s*.5f); p.lineTo(x-s*.3f,y+s*.32f); p.lineTo(x+s*.3f,y+s*.32f); p.close();
            fill.setColor(Color.rgb(255,105,30)); c.drawPath(p,fill);
        }

        private void rock(Canvas c,float x,float y,float s){
            Path p=new Path();
            p.moveTo(x-s*.45f,y+s*.28f); p.lineTo(x-s*.28f,y-s*.3f); p.lineTo(x+s*.1f,y-s*.45f);
            p.lineTo(x+s*.42f,y-s*.12f); p.lineTo(x+s*.48f,y+s*.28f); p.close();
            fill.setColor(Color.rgb(100,110,120)); c.drawPath(p,fill);
        }

        private void arrow(Canvas c,float x,float y,float s,int color){
            stroke.setColor(color); stroke.setStrokeWidth(Math.max(4f,s*.12f));
            c.drawLine(x,y+s*.35f,x,y-s*.28f,stroke);
            c.drawLine(x,y-s*.28f,x-s*.22f,y-s*.05f,stroke);
            c.drawLine(x,y-s*.28f,x+s*.22f,y-s*.05f,stroke);
        }

        private void snowman(Canvas c,float x,float y,float s){
            fill.setColor(Color.WHITE);
            c.drawCircle(x,y+s*.15f,s*.28f,fill); c.drawCircle(x,y-s*.18f,s*.20f,fill);
            stroke.setColor(Color.LTGRAY); stroke.setStrokeWidth(2f); c.drawCircle(x,y+s*.15f,s*.28f,stroke);
            fill.setColor(Color.rgb(255,120,20)); c.drawCircle(x+s*.13f,y-s*.18f,s*.04f,fill);
        }

        private void candy(Canvas c,float x,float y,float s){
            fill.setColor(iColor((int)(x+y)));
            c.drawCircle(x,y,s*.22f,fill);
            fill.setColor(Color.WHITE);
            c.drawRect(x-s*.04f,y-s*.2f,x+s*.04f,y+s*.2f,fill);
        }

        private int iColor(int seed){
            int m=Math.abs(seed)%3;
            if(m==0)return Color.rgb(245,90,120);
            if(m==1)return Color.rgb(90,200,120);
            return Color.rgb(255,190,60);
        }

        private void coin(Canvas c,float x,float y,float s){
            fill.setColor(Color.rgb(255,185,35)); c.drawCircle(x,y,s*.3f,fill);
            fill.setColor(Color.rgb(255,225,100)); c.drawCircle(x,y,s*.18f,fill);
        }

        private void colorTarget(Canvas c,float x,float y,float s,int i){
            int[] colors={Color.RED,Color.BLUE,Color.GREEN,Color.rgb(255,190,20)};
            stroke.setColor(colors[i%colors.length]); stroke.setStrokeWidth(Math.max(4f,s*.12f));
            c.drawCircle(x,y,s*.28f,stroke);
        }

        private void paw(Canvas c,float x,float y,float s,int i){
            int[] colors={Color.rgb(40,140,245),Color.rgb(255,110,50),Color.rgb(245,110,180)};
            fill.setColor(colors[i%colors.length]);
            c.drawCircle(x,y+s*.08f,s*.18f,fill);
            c.drawCircle(x-s*.16f,y-s*.12f,s*.07f,fill);
            c.drawCircle(x,y-s*.17f,s*.07f,fill);
            c.drawCircle(x+s*.16f,y-s*.12f,s*.07f,fill);
        }
    }
}
