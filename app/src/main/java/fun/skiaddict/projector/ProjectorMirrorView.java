package fun.skiaddict.projector;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public final class ProjectorMirrorView extends View {
    private final Activity sourceActivity;
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);

    public ProjectorMirrorView(Activity activity){
        super(activity);
        sourceActivity=activity;
        setBackgroundColor(Color.BLACK);
    }

    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);

        View source=sourceActivity.getWindow().getDecorView();
        int sw=source.getWidth(), sh=source.getHeight();
        if(sw<=0 || sh<=0){
            postInvalidateOnAnimation();
            return;
        }

        float sx=getWidth()/(float)sw;
        float sy=getHeight()/(float)sh;
        float scale=Math.min(sx,sy);
        float dx=(getWidth()-sw*scale)/2f;
        float dy=(getHeight()-sh*scale)/2f;

        canvas.save();
        canvas.translate(dx,dy);
        canvas.scale(scale,scale);
        try{ source.draw(canvas); }catch(Throwable ignored){}
        canvas.restore();

        if(DeveloperPrefs.safeArea(getContext())){
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(3f,getWidth()*.002f));
            paint.setColor(Color.rgb(242,31,47));
            float mx=getWidth()*.035f, my=getHeight()*.055f;
            canvas.drawRoundRect(new RectF(mx,my,getWidth()-mx,getHeight()-my),18,18,paint);
            paint.setStyle(Paint.Style.FILL);
        }

        if(DeveloperPrefs.overlay(getContext())){
            paint.setColor(Color.argb(205,15,20,28));
            RectF box=new RectF(18,18,Math.min(getWidth()-18,460),94);
            canvas.drawRoundRect(box,14,14,paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(Math.max(18,getHeight()*.025f));
            paint.setFakeBoldText(true);
            canvas.drawText("DEV MIRROR • "+sourceActivity.getClass().getSimpleName(),34,50,paint);
            paint.setFakeBoldText(false);
            paint.setTextSize(Math.max(14,getHeight()*.018f));
            canvas.drawText(sw+"×"+sh+" → "+getWidth()+"×"+getHeight(),34,78,paint);
        }

        postInvalidateOnAnimation();
    }
}
