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
    private final ProjectorSession session; private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); private final Paint stroke=new Paint(Paint.ANTI_ALIAS_FLAG);
    private long last=0,lastReset=-1; private float offset=0;

    public ProjectorView(Context c,ProjectorSession s){super(c);session=s;stroke.setStyle(Paint.Style.STROKE);stroke.setStrokeCap(Paint.Cap.ROUND);session.addListener(this);setBackgroundColor(Color.BLACK);}
    @Override protected void onDetachedFromWindow(){session.removeListener(this);super.onDetachedFromWindow();}
    @Override public void onProjectorStateChanged(){invalidate();}

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);c.drawColor(Color.BLACK);
        long now=SystemClock.uptimeMillis();if(last==0)last=now;float dt=Math.min(.05f,(now-last)/1000f);last=now;
        if(lastReset!=session.resetToken()){offset=0;lastReset=session.resetToken();}
        CourseStore.Course course=session.course();int[] v=session.values();ProjectorConfig cfg=ProjectorConfig.active(getContext());
        if(session.isPlaying())offset+=dt*(.10f+v[0]*.012f);
        c.save();applyGlobal(c,cfg);
        if(course.category==CourseStore.Category.ALPINE)alpine(c,course,v,cfg);else if(course.category==CourseStore.Category.S_CURVE)curve(c,course,v,cfg);else if(course.category==CourseStore.Category.STRAIGHT)straight(c,course,v,cfg);else if(course.category==CourseStore.Category.KIDS)kids(c,course,v,cfg);else obstacles(c,course,v,cfg);
        c.restore();if(session.isPlaying())postInvalidateOnAnimation();
    }

    private void applyGlobal(Canvas c,ProjectorConfig cfg){
        float w=getWidth(),h=getHeight();c.translate(w*(cfg.x/200f),h*(cfg.y/200f));c.rotate(cfg.rotation,w*.5f,h*.5f);c.scale(cfg.scaleX/100f,cfg.scaleY/100f,w*.5f,h*.5f);
    }
    private float frac(float x){return x-(float)Math.floor(x);}
    private float depthScale(float z,ProjectorConfig cfg){float near=.55f+cfg.perspective/110f;float far=.13f;return (far+(near-far)*z)*(cfg.objectScale/100f);}
    private float yFor(float z,ProjectorConfig cfg){float top=getHeight()*(.04f+cfg.horizon/250f);return top+(getHeight()-top*.9f)*z;}
    private float xFor(float lane,float z,ProjectorConfig cfg){float center=getWidth()*(cfg.vanishX/100f);float half=getWidth()*(.10f+.34f*z);return center+lane*half;}

    private void alpine(Canvas c,CourseStore.Course course,int[] v,ProjectorConfig cfg){
        for(int i=0;i<8;i++){float z=frac(i/8f+offset);float lane=(i%2==0?-.48f:.48f);if(course.id.equals("random_gates"))lane=(float)Math.sin(i*2.2)*.55f;if(course.id.equals("slalom_standard"))lane*=.72f;float s=Math.min(getWidth(),getHeight())*.16f*depthScale(z,cfg)*(v[3]/30f);gate(c,xFor(lane,z,cfg),yFor(z,cfg),s,i%2==0?Ui.RED:Color.rgb(20,80,255));}
    }
    private void curve(Canvas c,CourseStore.Course course,int[] v,ProjectorConfig cfg){
        stroke.setColor(course.id.equals("candy_trail")?Color.rgb(255,80,180):Color.rgb(40,150,255));stroke.setStrokeWidth(Math.max(8,getHeight()*.02f));Path path=new Path();
        for(int j=0;j<=80;j++){float z=j/80f;float lane=(float)Math.sin((z+offset)*Math.PI*4)*.48f;float x=xFor(lane,z,cfg),y=yFor(z,cfg);if(j==0)path.moveTo(x,y);else path.lineTo(x,y);}c.drawPath(path,stroke);
        for(int i=0;i<6;i++){float z=frac(i/6f+offset*.75f);float lane=(float)Math.sin((z+offset)*Math.PI*4)*.48f;float s=Math.min(getWidth(),getHeight())*.12f*depthScale(z,cfg);if(course.id.equals("snowman_trail"))snow(c,xFor(lane,z,cfg),yFor(z,cfg),s);else if(course.id.equals("follow_leader"))arrow(c,xFor(lane,z,cfg),yFor(z,cfg),s);else if(course.id.equals("candy_trail"))candy(c,xFor(lane,z,cfg),yFor(z,cfg),s,i);}
    }
    private void straight(Canvas c,CourseStore.Course course,int[] v,ProjectorConfig cfg){
        for(int i=0;i<7;i++){float z=frac(i/7f+offset);float s=Math.min(getWidth(),getHeight())*.13f*depthScale(z,cfg);float x=xFor(0,z,cfg),y=yFor(z,cfg);if(course.id.equals("reaction_lights")){int m=(i+(int)(offset*20))%3;p.setColor(m==0?Color.RED:m==1?Color.GREEN:Color.rgb(80,90,100));c.drawCircle(x,y,s*.4f,p);}else{p.setColor(Color.rgb(50,190,120));c.drawRoundRect(new RectF(x-s,y-s*.18f,x+s,y+s*.18f),8,8,p);}}
    }
    private void kids(Canvas c,CourseStore.Course course,int[] v,ProjectorConfig cfg){
        for(int i=0;i<9;i++){float z=frac(i/9f+offset);Random r=new Random(i*7717L+course.id.hashCode());float lane=(r.nextFloat()*2-1)*.65f;float s=Math.min(getWidth(),getHeight())*.16f*depthScale(z,cfg);float x=xFor(lane,z,cfg),y=yFor(z,cfg);if(course.id.contains("penguin"))penguin(c,x,y,s);else if(course.id.contains("coin"))coin(c,x,y,s);else if(course.id.contains("color"))target(c,x,y,s,i);else if(course.id.contains("animal"))paw(c,x,y,s,i);else if(course.id.contains("combo")){if(i%3==0)balloon(c,x,y,s,Ui.RED);else if(i%3==1)star(c,x,y,s*.55f);else coin(c,x,y,s);}else balloon(c,x,y,s,i%2==0?Ui.RED:Color.rgb(30,100,255));}
    }
    private void obstacles(Canvas c,CourseStore.Course course,int[] v,ProjectorConfig cfg){
        if(course.id.contains("maze")){stroke.setColor(Color.rgb(40,160,255));stroke.setStrokeWidth(Math.max(8,getHeight()*.018f));Path path=new Path();for(int j=0;j<=60;j++){float z=j/60f;float lane=(j/10)%2==0?-.45f:.45f;float x=xFor(lane,z,cfg),y=yFor(z,cfg);if(j==0)path.moveTo(x,y);else path.lineTo(x,y);}c.drawPath(path,stroke);return;}
        for(int i=0;i<9;i++){float z=frac(i/9f+offset);Random r=new Random(i*3319L+course.id.hashCode());float lane=(r.nextFloat()*2-1)*.65f;float s=Math.min(getWidth(),getHeight())*.16f*depthScale(z,cfg);float x=xFor(lane,z,cfg),y=yFor(z,cfg);if(course.id.contains("auto_difficulty")&&i%3==1)rock(c,x,y,s);else cone(c,x,y,s);}
    }

    private void gate(Canvas c,float x,float y,float s,int color){stroke.setColor(color);stroke.setStrokeWidth(Math.max(3,s*.07f));c.drawLine(x-s*.47f,y-s*.5f,x-s*.47f,y+s*.5f,stroke);c.drawLine(x+s*.47f,y-s*.5f,x+s*.47f,y+s*.5f,stroke);p.setColor(color);c.drawRect(x-s*.47f,y-s*.25f,x+s*.47f,y+s*.08f,p);}
    private void balloon(Canvas c,float x,float y,float s,int color){p.setColor(color);c.drawOval(new RectF(x-s*.35f,y-s*.5f,x+s*.35f,y+s*.25f),p);}
    private void coin(Canvas c,float x,float y,float s){p.setColor(Color.rgb(255,190,20));c.drawCircle(x,y,s*.3f,p);p.setColor(Color.YELLOW);c.drawCircle(x,y,s*.18f,p);}
    private void target(Canvas c,float x,float y,float s,int i){int[] cs={Color.RED,Color.BLUE,Color.GREEN,Color.YELLOW};stroke.setColor(cs[i%4]);stroke.setStrokeWidth(Math.max(4,s*.1f));c.drawCircle(x,y,s*.3f,stroke);}
    private void cone(Canvas c,float x,float y,float s){Path q=new Path();q.moveTo(x,y-s*.5f);q.lineTo(x-s*.28f,y+s*.32f);q.lineTo(x+s*.28f,y+s*.32f);q.close();p.setColor(Color.rgb(255,100,20));c.drawPath(q,p);}
    private void rock(Canvas c,float x,float y,float s){p.setColor(Color.rgb(120,125,130));c.drawOval(new RectF(x-s*.4f,y-s*.3f,x+s*.4f,y+s*.3f),p);}
    private void penguin(Canvas c,float x,float y,float s){p.setColor(Color.rgb(20,35,55));c.drawOval(new RectF(x-s*.32f,y-s*.48f,x+s*.32f,y+s*.42f),p);p.setColor(Color.WHITE);c.drawOval(new RectF(x-s*.2f,y-s*.18f,x+s*.2f,y+s*.3f),p);}
    private void paw(Canvas c,float x,float y,float s,int i){int[] cs={Color.CYAN,Color.rgb(255,100,50),Color.MAGENTA};p.setColor(cs[i%3]);c.drawCircle(x,y+s*.06f,s*.18f,p);c.drawCircle(x-s*.16f,y-s*.12f,s*.07f,p);c.drawCircle(x,y-s*.17f,s*.07f,p);c.drawCircle(x+s*.16f,y-s*.12f,s*.07f,p);}
    private void star(Canvas c,float x,float y,float r){Path q=new Path();for(int i=0;i<10;i++){double a=-Math.PI/2+i*Math.PI/5;float rr=i%2==0?r:r*.42f;float px=x+(float)Math.cos(a)*rr,py=y+(float)Math.sin(a)*rr;if(i==0)q.moveTo(px,py);else q.lineTo(px,py);}q.close();p.setColor(Color.YELLOW);c.drawPath(q,p);}
    private void arrow(Canvas c,float x,float y,float s){stroke.setColor(Ui.RED);stroke.setStrokeWidth(Math.max(4,s*.1f));c.drawLine(x,y+s*.35f,x,y-s*.28f,stroke);c.drawLine(x,y-s*.28f,x-s*.2f,y-s*.05f,stroke);c.drawLine(x,y-s*.28f,x+s*.2f,y-s*.05f,stroke);}
    private void candy(Canvas c,float x,float y,float s,int i){int[] cs={Color.MAGENTA,Color.GREEN,Color.YELLOW};p.setColor(cs[i%3]);c.drawCircle(x,y,s*.22f,p);}
    private void snow(Canvas c,float x,float y,float s){p.setColor(Color.WHITE);c.drawCircle(x,y+s*.12f,s*.27f,p);c.drawCircle(x,y-s*.18f,s*.19f,p);}
}
