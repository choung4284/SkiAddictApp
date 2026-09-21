package fun.skiaddict.projector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public final class HomeMasterView extends View {
    public interface Actions {
        void interactive();
        void projector();
        void presets();
        void settings();
        void basicGates();
        void sCurve();
        void parallelTurns();
        void obstacles();
    }

    private final Bitmap master;
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final RectF dst=new RectF();
    private final Actions actions;

    public HomeMasterView(Context c,Actions a){
        super(c);
        actions=a;
        master=BitmapFactory.decodeResource(getResources(),R.drawable.home_master_reference);
        setBackgroundColor(Color.rgb(239,246,250));
        setFocusable(true);
    }

    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(master==null)return;
        float vw=getWidth(),vh=getHeight();
        float scale=Math.min(vw/master.getWidth(),vh/master.getHeight());
        float dw=master.getWidth()*scale, dh=master.getHeight()*scale;
        float left=(vw-dw)/2f, top=(vh-dh)/2f;
        dst.set(left,top,left+dw,top+dh);
        canvas.drawBitmap(master,null,dst,paint);
    }

    private float[] map(float x,float y){
        if(master==null || !dst.contains(x,y))return null;
        float ix=(x-dst.left)/dst.width()*master.getWidth();
        float iy=(y-dst.top)/dst.height()*master.getHeight();
        return new float[]{ix,iy};
    }

    private boolean in(float x,float y,float l,float t,float r,float b){return x>=l&&x<=r&&y>=t&&y<=b;}

    @Override public boolean onTouchEvent(MotionEvent e){
        if(e.getAction()!=MotionEvent.ACTION_UP)return true;
        float[] q=map(e.getX(),e.getY()); if(q==null)return true;
        float x=q[0],y=q[1];

        // Sidebar hit areas from the approved master reference (713 x 401)
        if(in(x,y,8,86,122,130)){ return true; }
        if(in(x,y,8,130,122,173)){ actions.interactive(); return true; }
        if(in(x,y,8,173,122,217)){ actions.projector(); return true; }
        if(in(x,y,8,217,122,259)){ actions.presets(); return true; }
        if(in(x,y,8,259,122,306)){ actions.settings(); return true; }

        // Top gear
        if(in(x,y,321,12,352,45)){ actions.settings(); return true; }

        // Popular course cards
        if(in(x,y,139,250,273,371)){ actions.basicGates(); return true; }
        if(in(x,y,280,250,414,371)){ actions.sCurve(); return true; }
        if(in(x,y,421,250,554,371)){ actions.parallelTurns(); return true; }
        if(in(x,y,561,250,696,371)){ actions.obstacles(); return true; }
        return true;
    }
}
