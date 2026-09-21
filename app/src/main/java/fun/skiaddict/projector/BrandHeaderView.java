package fun.skiaddict.projector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public final class BrandHeaderView extends FrameLayout {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    public BrandHeaderView(Context c,DeviceProfile d){
        super(c);setWillNotDraw(false);
        ImageView logo=new ImageView(c);logo.setImageResource(R.drawable.ski_addict_logo);logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LayoutParams lp=new LayoutParams(d.scaled(225),-1,Gravity.START|Gravity.CENTER_VERTICAL);lp.leftMargin=Ui.dp(c,10);lp.topMargin=d.scaled(5);lp.bottomMargin=d.scaled(5);addView(logo,lp);
        TextView motto=Ui.text(c,"TRAIN   PLAY   IMPROVE\nANYTIME. ANYWHERE.",d.type.equals("Phone")?9:10,Ui.NAVY,true);motto.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams mp=new LayoutParams(d.scaled(230),-1,Gravity.START|Gravity.CENTER_VERTICAL);mp.leftMargin=d.scaled(240);addView(motto,mp);
        TextView right=Ui.text(c,"SKI BETTER\nLIVE BRIGHTER",d.type.equals("Phone")?9:10,Ui.RED,true);right.setGravity(Gravity.END|Gravity.CENTER_VERTICAL);
        LayoutParams rp=new LayoutParams(d.scaled(185),-1,Gravity.END|Gravity.CENTER_VERTICAL);rp.rightMargin=d.scaled(16);addView(right,rp);
    }
    @Override protected void onDraw(Canvas c){
        int w=getWidth(),h=getHeight();paint.setShader(new LinearGradient(0,0,w,0,Color.WHITE,Color.rgb(235,247,255),Shader.TileMode.CLAMP));c.drawRect(0,0,w,h,paint);paint.setShader(null);
        paint.setColor(Color.rgb(205,230,246));android.graphics.Path p=new android.graphics.Path();p.moveTo(w*.45f,h);p.lineTo(w*.57f,h*.42f);p.lineTo(w*.64f,h*.72f);p.lineTo(w*.73f,h*.23f);p.lineTo(w*.81f,h*.70f);p.lineTo(w,h*.20f);p.lineTo(w,h);p.close();c.drawPath(p,paint);
        paint.setColor(Color.WHITE);android.graphics.Path s=new android.graphics.Path();s.moveTo(w*.73f,h*.23f);s.lineTo(w*.76f,h*.45f);s.lineTo(w*.79f,h*.56f);s.lineTo(w*.81f,h*.70f);s.close();c.drawPath(s,paint);
    }
}
