package fun.skiaddict.projector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public final class CalibrationPreviewView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke=new Paint(Paint.ANTI_ALIAS_FLAG);
    private ProjectorConfig cfg;

    public CalibrationPreviewView(Context c){super(c);stroke.setStyle(Paint.Style.STROKE);}
    public void setConfig(ProjectorConfig c){cfg=c;invalidate();}

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        int w=getWidth(),h=getHeight();
        p.setColor(Color.rgb(30,33,37));c.drawRect(0,0,w,h,p);
        p.setColor(Color.rgb(58,61,65));c.drawRect(0,0,w,h*.22f,p);
        p.setColor(Color.WHITE);p.setTextAlign(Paint.Align.CENTER);p.setTextSize(Math.max(18,h*.055f));p.setFakeBoldText(true);c.drawText("Ski Addict",w*.5f,h*.13f,p);p.setFakeBoldText(false);

        float topY=h*.27f, bottomY=h*.90f;
        float topL=w*.27f,topR=w*.73f,botL=w*.06f,botR=w*.94f;
        if(cfg!=null){
            topL+=w*(cfg.tlx/200f);topY+=h*(cfg.tly/200f);
            topR+=w*(cfg.trx/200f);
            botL+=w*(cfg.blx/200f);botR+=w*(cfg.brx/200f);
            bottomY+=h*((cfg.bly+cfg.bry)/400f);
        }
        p.setColor(Color.rgb(241,242,243));
        Path mat=new Path();mat.moveTo(topL,topY);mat.lineTo(topR,topY);mat.lineTo(botR,bottomY);mat.lineTo(botL,bottomY);mat.close();c.drawPath(mat,p);

        stroke.setColor(Ui.RED);stroke.setStrokeWidth(4);
        c.drawPath(mat,stroke);
        if(cfg!=null && cfg.grid){
            stroke.setColor(Color.argb(120,255,255,255));stroke.setStrokeWidth(2);
            for(int i=1;i<5;i++){
                float t=i/5f;
                float l=topL+(botL-topL)*t,r=topR+(botR-topR)*t,y=topY+(bottomY-topY)*t;
                c.drawLine(l,y,r,y,stroke);
            }
        }
        if(cfg==null || cfg.center){
            stroke.setColor(Color.WHITE);stroke.setStrokeWidth(2);c.drawLine(w*.5f,topY,w*.5f,bottomY,stroke);
        }
        p.setColor(Ui.RED);
        c.drawCircle(topL,topY,8,p);c.drawCircle(topR,topY,8,p);c.drawCircle(botL,bottomY,8,p);c.drawCircle(botR,bottomY,8,p);
    }
}
