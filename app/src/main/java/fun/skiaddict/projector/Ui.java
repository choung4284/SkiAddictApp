package fun.skiaddict.projector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public final class Ui {
    public static final int RED=Color.rgb(242,31,47), NAVY=Color.rgb(23,34,49),
            MUTED=Color.rgb(91,105,128), BG=Color.rgb(247,250,253),
            BORDER=Color.rgb(224,232,241), GOLD=Color.rgb(255,183,35), GREEN=Color.rgb(0,166,91);

    public static int dp(Context c,int v){ return Math.round(v*c.getResources().getDisplayMetrics().density); }

    public static GradientDrawable round(Context c,int fill,int radius,int border){
        GradientDrawable g=new GradientDrawable(); g.setColor(fill); g.setCornerRadius(dp(c,radius));
        if(border!=Color.TRANSPARENT) g.setStroke(dp(c,1),border); return g;
    }

    public static TextView text(Context c,String s,int sp,int color,boolean bold){
        TextView t=new TextView(c); t.setText(s); t.setTextSize(sp); t.setTextColor(color);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD); return t;
    }

    public static LinearLayout card(Context c){
        LinearLayout l=new LinearLayout(c); l.setOrientation(LinearLayout.VERTICAL);
        l.setBackground(round(c,Color.WHITE,16,BORDER)); return l;
    }

    public static Button button(Context c,String s,boolean primary){
        Button b=new Button(c); b.setAllCaps(false); b.setText(s); b.setTextSize(12);
        b.setTypeface(Typeface.DEFAULT,Typeface.BOLD); b.setTextColor(primary?Color.WHITE:RED);
        b.setBackground(round(c,primary?RED:Color.rgb(255,245,246),11,primary?RED:Color.rgb(255,215,220)));
        return b;
    }

    public static View sidebar(Activity a,String active){
        LinearLayout n=new LinearLayout(a); n.setOrientation(LinearLayout.VERTICAL);
        n.addView(nav(a,"⌂  "+I18n.t(a,"home"),"home".equals(active),()->go(a,MainActivity.class)));
        n.addView(nav(a,"▥  "+I18n.t(a,"interactive"),"interactive".equals(active),()->Toast.makeText(a,I18n.t(a,"interactive"),Toast.LENGTH_SHORT).show()));
        n.addView(nav(a,"▤  "+I18n.t(a,"projector_setup"),"projector".equals(active),()->go(a,ProjectorSetupActivity.class)));
        n.addView(nav(a,"▦  "+I18n.t(a,"saved_presets"),"presets".equals(active),()->Toast.makeText(a,I18n.t(a,"saved_presets"),Toast.LENGTH_SHORT).show()));
        n.addView(nav(a,"⚙  "+I18n.t(a,"settings"),"settings".equals(active),()->go(a,SettingsActivity.class)));
        View gap=new View(a); n.addView(gap,new LinearLayout.LayoutParams(1,0,1f));
        TextView foot=text(a,"INDOOR\nSKI CLUB\n\nMore Runs\nA Brighter You.",11,MUTED,false);
        foot.setPadding(dp(a,10),0,0,0); n.addView(foot);
        return n;
    }

    private static void go(Activity a,Class<?> cls){
        if(a.getClass()==cls)return;
        Intent i=new Intent(a,cls);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        a.startActivity(i);
        a.overridePendingTransition(0,0);
    }

    private static Button nav(Context c,String s,boolean active,Runnable r){
        Button b=new Button(c); b.setAllCaps(false); b.setText(s); b.setTextSize(12);
        b.setGravity(Gravity.START|Gravity.CENTER_VERTICAL); b.setPadding(dp(c,10),0,dp(c,4),0);
        b.setTextColor(active?Color.WHITE:NAVY); b.setBackground(round(c,active?RED:Color.TRANSPARENT,14,Color.TRANSPARENT));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dp(c,48));
        lp.bottomMargin=dp(c,5); b.setLayoutParams(lp); b.setOnClickListener(v->r.run()); return b;
    }

    public static View header(Activity a,TextView status){
        LinearLayout h=new LinearLayout(a); h.setGravity(Gravity.CENTER_VERTICAL);
        TextView brand=text(a,"Ski Addict",26,RED,true);
        h.addView(brand,new LinearLayout.LayoutParams(dp(a,190),ViewGroup.LayoutParams.MATCH_PARENT));
        TextView motto=text(a,I18n.th(a)?"ฝึก • เล่น • พัฒนา  ทุกที่ ทุกเวลา":"TRAIN • PLAY • IMPROVE  ANYTIME. ANYWHERE.",11,NAVY,true);
        h.addView(motto,new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1f));
        if(status!=null){
            status.setGravity(Gravity.CENTER);
            status.setBackground(round(a,Color.WHITE,12,BORDER));
            h.addView(status,new LinearLayout.LayoutParams(dp(a,140),dp(a,44)));
        }
        return h;
    }
}
