package fun.skiaddict.projector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.view.View;

public final class BrandHeaderView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path=new Path();

    public BrandHeaderView(Context c){ super(c); }

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        int w=getWidth(),h=getHeight();
        p.setShader(new LinearGradient(0,0,w,0,Color.WHITE,Color.rgb(236,247,255),Shader.TileMode.CLAMP));
        c.drawRect(0,0,w,h,p);p.setShader(null);

        // soft mountain skyline
        p.setColor(Color.rgb(205,228,245));
        path.reset();path.moveTo(w*.34f,h*.88f);path.lineTo(w*.43f,h*.42f);path.lineTo(w*.49f,h*.68f);
        path.lineTo(w*.58f,h*.20f);path.lineTo(w*.64f,h*.61f);path.lineTo(w*.73f,h*.35f);path.lineTo(w*.82f,h*.70f);path.lineTo(w,h*.25f);path.lineTo(w,h);path.lineTo(w*.34f,h);path.close();c.drawPath(path,p);

        p.setColor(Color.WHITE);
        path.reset();path.moveTo(w*.43f,h*.42f);path.lineTo(w*.455f,h*.55f);path.lineTo(w*.47f,h*.47f);path.lineTo(w*.49f,h*.68f);path.close();c.drawPath(path,p);
        path.reset();path.moveTo(w*.58f,h*.20f);path.lineTo(w*.61f,h*.43f);path.lineTo(w*.64f,h*.61f);path.lineTo(w*.595f,h*.48f);path.close();c.drawPath(path,p);

        p.setFakeBoldText(true);p.setTextSize(h*.48f);p.setColor(Ui.RED);p.setTextAlign(Paint.Align.LEFT);
        c.drawText("Ski Addict",18,h*.60f,p);
        p.setTextSize(h*.16f);p.setColor(Ui.NAVY);c.drawText("INDOOR SKI CLUB",22,h*.82f,p);
        p.setFakeBoldText(false);

        p.setTextSize(h*.17f);p.setColor(Ui.NAVY);p.setFakeBoldText(true);
        c.drawText("TRAIN   PLAY   IMPROVE",w*.205f,h*.38f,p);
        p.setFakeBoldText(false);p.setTextSize(h*.13f);p.setColor(Color.rgb(84,108,157));
        c.drawText("ANYTIME. ANYWHERE.",w*.205f,h*.64f,p);

        p.setTextAlign(Paint.Align.RIGHT);p.setTextSize(h*.15f);p.setColor(Ui.RED);p.setFakeBoldText(true);
        c.drawText("SKI BETTER",w-22,h*.38f,p);c.drawText("LIVE BRIGHTER",w-22,h*.62f,p);p.setFakeBoldText(false);
        p.setStrokeWidth(3);p.setColor(Ui.RED);c.drawLine(w-92,h*.73f,w-22,h*.73f,p);
        p.setTextAlign(Paint.Align.LEFT);
    }
}
