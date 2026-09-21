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

    public LivePreviewView(Context c){ super(c); stroke.setStyle(Paint.Style.STROKE); }

    public void setData(CourseStore.Course c,int[] v){ course=c; values=v.clone(); invalidate(); }

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        int w=getWidth(),h=getHeight();
        p.setShader(new LinearGradient(0,0,0,h,Color.rgb(223,229,234),Color.rgb(245,246,247),Shader.TileMode.CLAMP));
        c.drawRect(0,0,w,h,p); p.setShader(null);

        float topY=h*.17f, bottomY=h*.94f;
        float topL=w*.28f, topR=w*.72f, botL=w*.06f, botR=w*.94f;

        p.setColor(Color.rgb(35,39,44));
        Path room=new Path(); room.moveTo(0,0); room.lineTo(w,0); room.lineTo(botR,bottomY); room.lineTo(botL,bottomY); room.close(); c.drawPath(room,p);

        p.setColor(Color.rgb(238,239,240));
        Path mat=new Path(); mat.moveTo(topL,topY); mat.lineTo(topR,topY); mat.lineTo(botR,bottomY); mat.lineTo(botL,bottomY); mat.close(); c.drawPath(mat,p);

        p.setColor(Color.rgb(22,25,29));
        c.drawRect(w*.20f,h*.03f,w*.80f,h*.17f,p);
        p.setTextAlign(Paint.Align.CENTER); p.setFakeBoldText(true); p.setTextSize(Math.max(18,h*.07f)); p.setColor(Color.WHITE);
        c.drawText("Ski Addict",w*.5f,h*.115f,p); p.setFakeBoldText(false);
        p.setTextSize(Math.max(8,h*.018f)); c.drawText("INDOOR SKI CLUB",w*.5f,h*.145f,p);

        p.setColor(Color.rgb(62,67,72));
        stroke.setColor(Color.rgb(165,171,176)); stroke.setStrokeWidth(3);
        c.drawLine(botL,bottomY,topL,topY,stroke); c.drawLine(botR,bottomY,topR,topY,stroke);
        c.drawLine(w*.10f,bottomY,w*.30f,topY,stroke); c.drawLine(w*.90f,bottomY,w*.70f,topY,stroke);

        if(course!=null){
            if(course.category==CourseStore.Category.ALPINE) drawGates(c,w,h,topY,bottomY,topL,topR,botL,botR);
            else drawGeneric(c,w,h,topY,bottomY,topL,topR,botL,botR);
        }
    }

    private float lerp(float a,float b,float t){return a+(b-a)*t;}
    private float perspectiveX(float normX,float t,float topL,float topR,float botL,float botR){
        float l=lerp(topL,botL,t), r=lerp(topR,botR,t); return l+(r-l)*normX;
    }

    private void drawGates(Canvas c,int w,int h,float topY,float bottomY,float topL,float topR,float botL,float botR){
        float[] ts={.10f,.22f,.36f,.53f,.72f,.88f};
        for(int i=0;i<ts.length;i++){
            float t=ts[i], side=(i%2==0)?.36f:.64f;
            float x=perspectiveX(side,t,topL,topR,botL,botR), y=lerp(topY,bottomY,t);
            float s=(h*.035f+h*.10f*t)*(values==null?1f:(.75f+values[3]/80f));
            gate(c,x,y,s,i%2==0?Ui.RED:Color.rgb(23,92,226));
        }
    }

    private void drawGeneric(Canvas c,int w,int h,float topY,float bottomY,float topL,float topR,float botL,float botR){
        for(int i=0;i<6;i++){
            float t=.12f+i*.145f;
            float x=perspectiveX(.5f+(float)Math.sin(i*1.7)*.16f,t,topL,topR,botL,botR);
            float y=lerp(topY,bottomY,t), s=h*(.025f+.07f*t);
            p.setColor(i%2==0?Ui.RED:Color.rgb(23,92,226));
            c.drawCircle(x,y,s,p);
        }
    }

    private void gate(Canvas c,float x,float y,float s,int color){
        stroke.setStrokeWidth(Math.max(3,s*.08f)); stroke.setColor(color);
        c.drawLine(x-s*.46f,y-s*.55f,x-s*.46f,y+s*.45f,stroke);
        c.drawLine(x+s*.46f,y-s*.55f,x+s*.46f,y+s*.45f,stroke);
        p.setColor(color);
        RectF r=new RectF(x-s*.46f,y-s*.28f,x+s*.46f,y+s*.10f); c.drawRoundRect(r,s*.04f,s*.04f,p);
        p.setTextAlign(Paint.Align.CENTER); p.setTextSize(Math.max(5,s*.18f)); p.setColor(Color.WHITE); p.setFakeBoldText(true);
        c.drawText("Ski Addict",x,y-s*.02f,p); p.setFakeBoldText(false);
    }
}
