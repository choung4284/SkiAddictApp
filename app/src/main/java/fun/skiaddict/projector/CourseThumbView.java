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

public final class CourseThumbView extends View {
    public static final int ALPINE=1, SCURVE=2, KIDS=3, OBSTACLE=4, BASIC=5, PARALLEL=6, FUN=7;
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint s=new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mode;

    public CourseThumbView(Context c,int mode){super(c);this.mode=mode;s.setStyle(Paint.Style.STROKE);s.setStrokeCap(Paint.Cap.ROUND);}

    @Override protected void onDraw(Canvas c){
        int w=getWidth(),h=getHeight();
        p.setShader(new LinearGradient(0,0,0,h,Color.rgb(189,224,250),Color.rgb(248,250,252),Shader.TileMode.CLAMP));c.drawRect(0,0,w,h,p);p.setShader(null);

        // mountains
        p.setColor(Color.rgb(211,229,242));
        Path m=new Path();m.moveTo(0,h*.55f);m.lineTo(w*.18f,h*.22f);m.lineTo(w*.32f,h*.43f);m.lineTo(w*.48f,h*.16f);m.lineTo(w*.62f,h*.48f);m.lineTo(w*.78f,h*.25f);m.lineTo(w,h*.5f);m.lineTo(w,h);m.lineTo(0,h);m.close();c.drawPath(m,p);
        p.setColor(Color.WHITE);
        Path snow=new Path();snow.moveTo(w*.18f,h*.22f);snow.lineTo(w*.24f,h*.34f);snow.lineTo(w*.32f,h*.43f);snow.lineTo(w*.21f,h*.38f);snow.close();c.drawPath(snow,p);
        snow.reset();snow.moveTo(w*.48f,h*.16f);snow.lineTo(w*.55f,h*.33f);snow.lineTo(w*.62f,h*.48f);snow.lineTo(w*.50f,h*.36f);snow.close();c.drawPath(snow,p);

        if(mode==BASIC){drawGate(c,w*.36f,h*.62f,h*.42f,Ui.RED);drawGate(c,w*.68f,h*.48f,h*.28f,Color.rgb(25,83,225));}
        else if(mode==FUN){drawGate(c,w*.34f,h*.56f,h*.32f,Ui.RED);drawSkier(c,w*.66f,h*.60f,h*.42f,Color.rgb(34,116,230));drawObstacle(c,w*.84f,h*.73f,h*.20f);}
        else if(mode==OBSTACLE){drawSkier(c,w*.56f,h*.55f,h*.44f,Ui.RED);drawGate(c,w*.18f,h*.67f,h*.24f,Color.rgb(24,82,220));drawGate(c,w*.84f,h*.66f,h*.28f,Ui.RED);}
        else if(mode==KIDS){drawSkier(c,w*.52f,h*.56f,h*.48f,Color.rgb(255,205,31));}
        else if(mode==SCURVE || mode==PARALLEL){drawSkier(c,w*.52f,h*.58f,h*.48f,mode==SCURVE?Color.rgb(42,82,125):Color.rgb(20,30,45));if(mode==SCURVE)drawGate(c,w*.82f,h*.69f,h*.25f,Ui.RED);}
        else {drawSkier(c,w*.52f,h*.57f,h*.50f,Ui.RED);}
    }

    private void drawSkier(Canvas c,float x,float y,float sz,int jacket){
        p.setColor(jacket);c.drawOval(new RectF(x-sz*.12f,y-sz*.25f,x+sz*.12f,y+sz*.08f),p);
        p.setColor(Color.rgb(24,35,50));c.drawCircle(x,y-sz*.34f,sz*.09f,p);
        s.setColor(Color.rgb(24,35,50));s.setStrokeWidth(Math.max(3,sz*.035f));
        c.drawLine(x-sz*.04f,y,x-sz*.18f,y+sz*.23f,s);c.drawLine(x+sz*.04f,y,x+sz*.18f,y+sz*.22f,s);
        c.drawLine(x-sz*.07f,y-sz*.06f,x-sz*.26f,y+sz*.08f,s);c.drawLine(x+sz*.08f,y-sz*.06f,x+sz*.27f,y+sz*.07f,s);
        s.setStrokeWidth(Math.max(2,sz*.025f));c.drawLine(x-sz*.28f,y+sz*.28f,x+sz*.06f,y+sz*.24f,s);c.drawLine(x-sz*.06f,y+sz*.31f,x+sz*.29f,y+sz*.26f,s);
    }

    private void drawGate(Canvas c,float x,float y,float sz,int color){
        s.setColor(color);s.setStrokeWidth(Math.max(3,sz*.045f));c.drawLine(x-sz*.22f,y-sz*.3f,x-sz*.22f,y+sz*.28f,s);c.drawLine(x+sz*.22f,y-sz*.3f,x+sz*.22f,y+sz*.28f,s);
        p.setColor(color);c.drawRoundRect(new RectF(x-sz*.22f,y-sz*.15f,x+sz*.22f,y+sz*.04f),4,4,p);
    }
    private void drawObstacle(Canvas c,float x,float y,float sz){
        p.setColor(Color.rgb(250,190,20));c.drawRoundRect(new RectF(x-sz*.28f,y-sz*.18f,x+sz*.28f,y+sz*.18f),sz*.15f,sz*.15f,p);
        p.setColor(Color.rgb(70,75,80));c.drawOval(new RectF(x-sz*.15f,y-sz*.12f,x+sz*.15f,y+sz*.12f),p);
    }
}
