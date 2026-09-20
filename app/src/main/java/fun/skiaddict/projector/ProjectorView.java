package fun.skiaddict.projector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.View;

import java.util.Random;

public final class ProjectorView extends View implements ProjectorSession.Listener {
    private final ProjectorSession session;
    private final Paint fill=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke=new Paint(Paint.ANTI_ALIAS_FLAG);
    private long lastMs=0;
    private float offset=0;
    private long lastReset=-1;

    public ProjectorView(Context c,ProjectorSession session){
        super(c);
        this.session=session;
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeCap(Paint.Cap.ROUND);
        session.addListener(this);
        setBackgroundColor(Color.WHITE);
    }

    @Override protected void onDetachedFromWindow(){
        session.removeListener(this);
        super.onDetachedFromWindow();
    }

    @Override public void onProjectorStateChanged(){ invalidate(); }

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        long now=SystemClock.uptimeMillis();
        if(lastMs==0) lastMs=now;
        float dt=Math.min(.05f,(now-lastMs)/1000f);
        lastMs=now;

        long reset=session.resetToken();
        if(reset!=lastReset){ offset=0; lastReset=reset; }

        CourseStore.Course course=session.course();
        int[] v=session.values();
        if(session.isPlaying()) offset+=dt*(90f+v[0]*18f);

        drawMat(c);
        switch(course.category){
            case ALPINE: drawAlpine(c,course,v); break;
            case S_CURVE: drawCurve(c,course,v); break;
            case STRAIGHT: drawStraight(c,course,v); break;
            case KIDS: drawKids(c,course,v); break;
            case OBSTACLES: drawObstacles(c,course,v); break;
        }

        if(!session.isPlaying()) drawState(c,offset==0?"READY":"PAUSED");
        if(session.isPlaying()) postInvalidateOnAnimation();
    }

    private void drawMat(Canvas c){
        int w=getWidth(),h=getHeight();
        fill.setColor(Color.rgb(250,253,255)); c.drawRect(0,0,w,h,fill);
        fill.setColor(Color.rgb(32,36,42)); c.drawRect(0,0,w*.035f,h,fill); c.drawRect(w*.965f,0,w,h,fill);
        fill.setColor(Color.rgb(232,238,243));
        for(int i=0;i<12;i++){ float y=i*h/11f; c.drawRect(w*.04f,y,w*.96f,y+1.3f,fill); }
    }

    private float obj(int[] v){ return Math.min(getWidth(),getHeight())*(.03f+(v[3]-10)/600f); }
    private float mod(float a,float b){ float r=a%b; return r<0?r+b:r; }

    private void drawAlpine(Canvas c,CourseStore.Course course,int[] v){
        if(course.id.equals("wide_turn")){ drawMovingCurve(c,v,false); return; }
        if(course.id.equals("figure_8")){ drawFigure8(c,v); return; }

        int w=getWidth(),h=getHeight(); float s=obj(v);
        float gap=h*(.12f+v[2]/650f); float lat=w*(.10f+v[1]/520f);
        int count=(int)(h/gap)+4;
        int first=(int)Math.floor(offset/gap)-2;
        for(int n=first;n<first+count;n++){
            float y=mod(n*gap+offset,h+gap)-gap;
            float side=course.id.equals("random_gates")?(float)Math.sin(n*2.17):((n&1)==0?-1f:1f);
            if(course.id.equals("slalom_standard")) side*=.75f;
            float x=w*.5f+side*lat*.43f;
            gate(c,x,y,s,(n&1)==0?Ui.RED:Color.rgb(35,110,235));
        }
    }

    private void drawCurve(Canvas c,CourseStore.Course course,int[] v){
        drawMovingCurve(c,v,course.id.equals("candy_trail"));
        int w=getWidth(),h=getHeight(); float s=obj(v); float gap=h*.20f;
        int first=(int)Math.floor(offset/gap)-1;
        for(int n=first;n<first+7;n++){
            float y=mod(n*gap+offset*.7f,h+gap)-gap;
            float x=w*.5f+(float)Math.sin((y+offset)*.009)*w*.18f;
            if(course.id.equals("snowman_trail")) snowman(c,x,y,s);
            else if(course.id.equals("follow_leader")) arrow(c,x,y,s,Ui.RED);
            else if(course.id.equals("candy_trail")) candy(c,x,y,s,n);
        }
    }

    private void drawMovingCurve(Canvas c,int[] v,boolean pink){
        int w=getWidth(),h=getHeight(); float amp=w*(.08f+v[1]/450f);
        stroke.setColor(pink?Color.rgb(244,130,190):Color.rgb(80,180,240));
        stroke.setStrokeWidth(Math.max(14f,obj(v)*1.35f));
        Path p=new Path();
        for(int y=-30;y<=h+30;y+=10){
            float x=w*.5f+(float)Math.sin((y+offset)*(.007+v[2]/11000.0))*amp;
            if(y==-30)p.moveTo(x,y); else p.lineTo(x,y);
        }
        c.drawPath(p,stroke);
    }

    private void drawFigure8(Canvas c,int[] v){
        int w=getWidth(),h=getHeight(); float a=w*.2f;
        stroke.setColor(Color.rgb(80,180,240)); stroke.setStrokeWidth(Math.max(14f,obj(v)*1.25f));
        Path p=new Path();
        float phase=offset*.004f;
        for(int i=0;i<=200;i++){
            double t=i*Math.PI*2/200.0+phase;
            float x=w*.5f+(float)Math.sin(t)*a;
            float y=h*.5f+(float)(Math.sin(t)*Math.cos(t))*h*.42f;
            if(i==0)p.moveTo(x,y); else p.lineTo(x,y);
        }
        c.drawPath(p,stroke);
    }

    private void drawStraight(Canvas c,CourseStore.Course course,int[] v){
        int w=getWidth(),h=getHeight(); float x=w*.5f; float s=obj(v);
        stroke.setColor(Color.rgb(40,150,235)); stroke.setStrokeWidth(Math.max(5f,s*.18f));
        c.drawLine(x,h*.04f,x,h*.96f,stroke);
        float gap=h*.16f;
        int first=(int)Math.floor(offset/gap)-1;
        for(int n=first;n<first+8;n++){
            float y=mod(n*gap+offset,h+gap)-gap;
            if(course.id.equals("reaction_lights")){
                int m=Math.floorMod(n+(int)(offset/100),4);
                fill.setColor(m==0?Color.RED:m==1?Color.GREEN:Color.rgb(190,205,215));
                c.drawCircle(x,y,s*.38f,fill);
            } else {
                fill.setColor((n&1)==0?Color.rgb(70,185,110):Color.rgb(150,220,180));
                c.drawRoundRect(new RectF(x-w*.10f,y-s*.2f,x+w*.10f,y+s*.2f),8,8,fill);
            }
        }
    }

    private void drawKids(Canvas c,CourseStore.Course course,int[] v){
        int w=getWidth(),h=getHeight(); float s=obj(v); float gap=h*(.14f+v[2]/900f);
        int first=(int)Math.floor(offset/gap)-2;
        int count=(int)(h/gap)+5;
        for(int n=first;n<first+count;n++){
            float y=mod(n*gap+offset,h+gap)-gap;
            Random r=new Random(n*7919L+course.id.hashCode());
            float x=w*.5f+(r.nextFloat()*2f-1f)*w*(.12f+v[4]/500f);
            if(course.id.contains("penguin")) penguin(c,x,y,s);
            else if(course.id.contains("coin")) coin(c,x,y,s);
            else if(course.id.contains("color")) colorTarget(c,x,y,s,n);
            else if(course.id.contains("combo")){
                int type=Math.floorMod(n,3);
                if(type==0) balloon(c,x,y,s,Ui.RED); else if(type==1) star(c,x,y,s*.55f); else coin(c,x,y,s);
            } else if(course.id.contains("animal")) paw(c,x,y,s,n);
            else balloon(c,x,y,s,(n&1)==0?Ui.RED:Color.rgb(35,110,235));
        }
    }

    private void drawObstacles(Canvas c,CourseStore.Course course,int[] v){
        int w=getWidth(),h=getHeight(); float s=obj(v);
        if(course.id.contains("maze")){
            stroke.setColor(Color.rgb(110,190,240)); stroke.setStrokeWidth(s*1.05f);
            Path p=new Path(); float dy=mod(offset*.35f,h*.25f);
            p.moveTo(w*.28f,h*.05f+dy); p.lineTo(w*.72f,h*.05f+dy); p.lineTo(w*.72f,h*.25f+dy);
            p.lineTo(w*.43f,h*.25f+dy); p.lineTo(w*.43f,h*.48f+dy); p.lineTo(w*.76f,h*.48f+dy);
            p.lineTo(w*.76f,h*.74f+dy); p.lineTo(w*.34f,h*.74f+dy); c.drawPath(p,stroke); return;
        }

        float gap=h*(.14f+v[3]/900f);
        int first=(int)Math.floor(offset/gap)-2;
        for(int n=first;n<first+10;n++){
            float y=mod(n*gap+offset,h+gap)-gap;
            Random r=new Random(n*3571L+course.id.hashCode());
            float x=w*.5f+(r.nextFloat()*2f-1f)*w*(.10f+v[4]/520f);
            if(course.id.contains("auto_difficulty") && Math.floorMod(n,3)==1) rock(c,x,y,s);
            else cone(c,x,y,s);
        }
    }

    private void drawState(Canvas c,String text){
        int w=getWidth(),h=getHeight();
        fill.setColor(Color.argb(150,23,34,49));
        RectF r=new RectF(w*.40f,h*.44f,w*.60f,h*.56f); c.drawRoundRect(r,24,24,fill);
        fill.setColor(Color.WHITE); fill.setTextAlign(Paint.Align.CENTER); fill.setTextSize(Math.max(28,h*.05f));
        fill.setFakeBoldText(true); c.drawText(text,w*.5f,h*.52f,fill); fill.setFakeBoldText(false); fill.setTextAlign(Paint.Align.LEFT);
    }

    private void gate(Canvas c,float x,float y,float s,int color){ stroke.setColor(color); stroke.setStrokeWidth(Math.max(4f,s*.11f)); c.drawLine(x-s*.55f,y-s*.55f,x-s*.55f,y+s*.55f,stroke); c.drawLine(x+s*.55f,y-s*.55f,x+s*.55f,y+s*.55f,stroke); fill.setColor(color); c.drawRoundRect(new RectF(x-s*.55f,y-s*.22f,x+s*.55f,y+s*.13f),s*.08f,s*.08f,fill); }
    private void balloon(Canvas c,float x,float y,float s,int color){ fill.setColor(color); c.drawOval(new RectF(x-s*.35f,y-s*.5f,x+s*.35f,y+s*.25f),fill); stroke.setColor(Color.GRAY); stroke.setStrokeWidth(2f); c.drawLine(x,y+s*.25f,x,y+s*.68f,stroke); }
    private void star(Canvas c,float x,float y,float r){ Path p=new Path(); for(int i=0;i<10;i++){double a=-Math.PI/2+i*Math.PI/5;float rr=i%2==0?r:r*.42f;float px=x+(float)Math.cos(a)*rr,py=y+(float)Math.sin(a)*rr;if(i==0)p.moveTo(px,py);else p.lineTo(px,py);}p.close();fill.setColor(Ui.GOLD);c.drawPath(p,fill); }
    private void penguin(Canvas c,float x,float y,float s){ fill.setColor(Ui.NAVY); c.drawOval(new RectF(x-s*.36f,y-s*.5f,x+s*.36f,y+s*.42f),fill); fill.setColor(Color.WHITE); c.drawOval(new RectF(x-s*.23f,y-s*.22f,x+s*.23f,y+s*.34f),fill); fill.setColor(Ui.GOLD); c.drawCircle(x,y-s*.03f,s*.08f,fill); }
    private void cone(Canvas c,float x,float y,float s){ Path p=new Path();p.moveTo(x,y-s*.5f);p.lineTo(x-s*.3f,y+s*.32f);p.lineTo(x+s*.3f,y+s*.32f);p.close();fill.setColor(Color.rgb(255,105,30));c.drawPath(p,fill);fill.setColor(Color.WHITE);c.drawRect(x-s*.2f,y-s*.02f,x+s*.2f,y+s*.08f,fill); }
    private void rock(Canvas c,float x,float y,float s){ Path p=new Path();p.moveTo(x-s*.45f,y+s*.28f);p.lineTo(x-s*.28f,y-s*.3f);p.lineTo(x+s*.1f,y-s*.45f);p.lineTo(x+s*.42f,y-s*.12f);p.lineTo(x+s*.48f,y+s*.28f);p.close();fill.setColor(Color.rgb(100,110,120));c.drawPath(p,fill); }
    private void arrow(Canvas c,float x,float y,float s,int color){ stroke.setColor(color);stroke.setStrokeWidth(Math.max(4f,s*.12f));c.drawLine(x,y+s*.35f,x,y-s*.28f,stroke);c.drawLine(x,y-s*.28f,x-s*.22f,y-s*.05f,stroke);c.drawLine(x,y-s*.28f,x+s*.22f,y-s*.05f,stroke); }
    private void snowman(Canvas c,float x,float y,float s){ fill.setColor(Color.WHITE);c.drawCircle(x,y+s*.15f,s*.28f,fill);c.drawCircle(x,y-s*.18f,s*.20f,fill);stroke.setColor(Color.LTGRAY);stroke.setStrokeWidth(2f);c.drawCircle(x,y+s*.15f,s*.28f,stroke);fill.setColor(Color.rgb(255,120,20));c.drawCircle(x+s*.13f,y-s*.18f,s*.04f,fill); }
    private void candy(Canvas c,float x,float y,float s,int n){ int[] cs={Color.rgb(245,90,120),Color.rgb(90,200,120),Color.rgb(255,190,60)};fill.setColor(cs[Math.floorMod(n,3)]);c.drawCircle(x,y,s*.22f,fill);fill.setColor(Color.WHITE);c.drawRect(x-s*.04f,y-s*.2f,x+s*.04f,y+s*.2f,fill); }
    private void coin(Canvas c,float x,float y,float s){ fill.setColor(Color.rgb(255,185,35));c.drawCircle(x,y,s*.3f,fill);fill.setColor(Color.rgb(255,225,100));c.drawCircle(x,y,s*.18f,fill); }
    private void colorTarget(Canvas c,float x,float y,float s,int i){ int[] cs={Color.RED,Color.BLUE,Color.GREEN,Color.rgb(255,190,20)};stroke.setColor(cs[Math.floorMod(i,cs.length)]);stroke.setStrokeWidth(Math.max(4f,s*.12f));c.drawCircle(x,y,s*.28f,stroke); }
    private void paw(Canvas c,float x,float y,float s,int i){ int[] cs={Color.rgb(40,140,245),Color.rgb(255,110,50),Color.rgb(245,110,180)};fill.setColor(cs[Math.floorMod(i,cs.length)]);c.drawCircle(x,y+s*.08f,s*.18f,fill);c.drawCircle(x-s*.16f,y-s*.12f,s*.07f,fill);c.drawCircle(x,y-s*.17f,s*.07f,fill);c.drawCircle(x+s*.16f,y-s*.12f,s*.07f,fill); }
}
