package fun.skiaddict.projector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

public final class LivePreviewView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke=new Paint(Paint.ANTI_ALIAS_FLAG);
    private CourseStore.Course course;
    private int[] values;

    public LivePreviewView(Context c){ super(c); stroke.setStyle(Paint.Style.STROKE); stroke.setStrokeCap(Paint.Cap.ROUND); }
    public void setData(CourseStore.Course c,int[] v){course=c;values=v.clone();invalidate();}

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);int w=getWidth(),h=getHeight();

        p.setShader(new LinearGradient(0,0,0,h,Color.rgb(228,233,238),Color.rgb(247,247,247),Shader.TileMode.CLAMP));c.drawRect(0,0,w,h,p);p.setShader(null);

        // ceiling / rear wall
        p.setColor(Color.rgb(30,33,37));c.drawRect(0,0,w,h*.24f,p);
        p.setColor(Color.rgb(50,54,58));c.drawRect(0,h*.20f,w,h*.26f,p);

        // ceiling light strips
        p.setColor(Color.rgb(245,246,248));
        for(int i=0;i<5;i++){float x=w*(.12f+i*.19f);c.drawRoundRect(new RectF(x,h*.035f,x+w*.11f,h*.055f),5,5,p);}

        // wall logo
        p.setTextAlign(Paint.Align.CENTER);p.setColor(Color.WHITE);p.setTextSize(Math.max(22,h*.075f));p.setFakeBoldText(true);c.drawText("Ski Addict",w*.5f,h*.145f,p);p.setFakeBoldText(false);p.setTextSize(Math.max(8,h*.022f));c.drawText("INDOOR SKI CLUB",w*.5f,h*.185f,p);

        // mat trapezoid
        float topY=h*.25f,bottomY=h*.95f,topL=w*.29f,topR=w*.71f,botL=w*.055f,botR=w*.945f;
        p.setShader(new LinearGradient(0,topY,0,bottomY,Color.rgb(238,239,240),Color.rgb(250,250,250),Shader.TileMode.CLAMP));
        Path mat=new Path();mat.moveTo(topL,topY);mat.lineTo(topR,topY);mat.lineTo(botR,bottomY);mat.lineTo(botL,bottomY);mat.close();c.drawPath(mat,p);p.setShader(null);

        // dark side rails
        p.setColor(Color.rgb(38,41,45));
        Path l=new Path();l.moveTo(0,h*.23f);l.lineTo(topL,topY);l.lineTo(botL,bottomY);l.lineTo(0,bottomY);l.close();c.drawPath(l,p);
        Path r=new Path();r.moveTo(w,h*.23f);r.lineTo(topR,topY);r.lineTo(botR,bottomY);r.lineTo(w,bottomY);r.close();c.drawPath(r,p);

        // rail highlights
        stroke.setColor(Color.rgb(125,132,140));stroke.setStrokeWidth(3);
        c.drawLine(topL,topY,botL,bottomY,stroke);c.drawLine(topR,topY,botR,bottomY,stroke);
        c.drawLine(w*.22f,h*.27f,w*.02f,h*.93f,stroke);c.drawLine(w*.78f,h*.27f,w*.98f,h*.93f,stroke);

        // subtle mat texture
        stroke.setColor(Color.rgb(224,226,229));stroke.setStrokeWidth(1);
        for(int i=1;i<8;i++){float t=i/8f;float y=topY+(bottomY-topY)*t;float ll=topL+(botL-topL)*t;float rr=topR+(botR-topR)*t;c.drawLine(ll,y,rr,y,stroke);}

        if(course!=null){
            if(course.category==CourseStore.Category.ALPINE) gates(c,w,h,topY,bottomY,topL,topR,botL,botR);
            else generic(c,w,h,topY,bottomY,topL,topR,botL,botR);
        }
    }

    private float lerp(float a,float b,float t){return a+(b-a)*t;}
    private float px(float norm,float t,float tl,float tr,float bl,float br){float l=lerp(tl,bl,t),r=lerp(tr,br,t);return l+(r-l)*norm;}

    private void gates(Canvas c,int w,int h,float ty,float by,float tl,float tr,float bl,float br){
        float[] t={.08f,.19f,.32f,.47f,.65f,.85f};
        for(int i=0;i<t.length;i++){float z=t[i],norm=i%2==0?.37f:.63f;if(course.id.equals("random_gates"))norm=.5f+(float)Math.sin(i*2.1)*.22f;float x=px(norm,z,tl,tr,bl,br),y=lerp(ty,by,z);float scale=.35f+.95f*z;float s=Math.min(w,h)*.10f*scale*(values==null?1f:(.72f+values[3]/75f));gate(c,x,y,s,i%2==0?Ui.RED:Color.rgb(20,85,230));}
    }

    private void generic(Canvas c,int w,int h,float ty,float by,float tl,float tr,float bl,float br){
        for(int i=0;i<7;i++){float z=.08f+i*.13f;float x=px(.5f+(float)Math.sin(i*1.45)*.18f,z,tl,tr,bl,br),y=lerp(ty,by,z);float s=Math.min(w,h)*(.025f+.07f*z);p.setColor(i%2==0?Ui.RED:Color.rgb(23,92,226));c.drawCircle(x,y,s,p);}
    }

    private void gate(Canvas c,float x,float y,float s,int color){
        stroke.setStrokeWidth(Math.max(3,s*.075f));stroke.setColor(color);
        c.drawLine(x-s*.45f,y-s*.60f,x-s*.45f,y+s*.50f,stroke);c.drawLine(x+s*.45f,y-s*.60f,x+s*.45f,y+s*.50f,stroke);
        p.setColor(color);c.drawRoundRect(new RectF(x-s*.45f,y-s*.28f,x+s*.45f,y+s*.10f),5,5,p);
        p.setTextAlign(Paint.Align.CENTER);p.setTextSize(Math.max(5,s*.17f));p.setColor(Color.WHITE);p.setFakeBoldText(true);c.drawText("Ski Addict",x,y-s*.02f,p);p.setFakeBoldText(false);p.setTextAlign(Paint.Align.LEFT);
    }
}
