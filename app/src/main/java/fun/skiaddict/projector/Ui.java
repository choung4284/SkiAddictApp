package fun.skiaddict.projector;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class Ui {
    public static final int RED=Color.rgb(242,31,47), NAVY=Color.rgb(23,34,49),
            MUTED=Color.rgb(102,112,125), BG=Color.rgb(246,248,250),
            BORDER=Color.rgb(226,230,234), GOLD=Color.rgb(255,183,35);

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
        l.setBackground(round(c,Color.WHITE,18,BORDER)); return l;
    }
    public static Button button(Context c,String s,boolean primary){
        Button b=new Button(c); b.setAllCaps(false); b.setText(s); b.setTextSize(13);
        b.setTypeface(Typeface.DEFAULT,Typeface.BOLD); b.setTextColor(primary?Color.WHITE:RED);
        b.setBackground(round(c,primary?RED:Color.rgb(255,241,243),12,primary?RED:Color.rgb(255,210,215)));
        return b;
    }
    public static View sidebar(Context c){
        LinearLayout n=new LinearLayout(c); n.setOrientation(LinearLayout.VERTICAL);
        n.addView(nav(c,"⌂  Home",true)); n.addView(nav(c,"▣  Interactive",false));
        n.addView(nav(c,"▤  Projector Setup",false)); n.addView(nav(c,"▦  Saved Presets",false));
        n.addView(nav(c,"⚙  Settings",false));
        View gap=new View(c); n.addView(gap,new LinearLayout.LayoutParams(1,0,1f));
        n.addView(text(c,"INDOOR SKI CLUB\n\nMore Runs\nA Brighter You.",12,MUTED,false));
        return n;
    }
    private static Button nav(Context c,String s,boolean active){
        Button b=new Button(c); b.setAllCaps(false); b.setText(s); b.setTextSize(13);
        b.setGravity(Gravity.START|Gravity.CENTER_VERTICAL); b.setPadding(dp(c,12),0,0,0);
        b.setTextColor(active?Color.WHITE:NAVY); b.setBackground(round(c,active?RED:Color.TRANSPARENT,16,Color.TRANSPARENT));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dp(c,54));
        lp.bottomMargin=dp(c,7); b.setLayoutParams(lp); return b;
    }
}
