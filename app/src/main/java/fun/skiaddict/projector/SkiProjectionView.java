package fun.skiaddict.projector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.View;

import java.util.Random;

public final class SkiProjectionView extends View implements AppState.Listener {
    private final AppState state;
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private long lastFrameMs = 0L;
    private float worldOffset = 0f;

    private static final int RED = Color.rgb(242, 31, 47);
    private static final int BLUE = Color.rgb(38, 122, 236);
    private static final int NAVY = Color.rgb(23, 34, 49);
    private static final int GREEN = Color.rgb(43, 176, 92);
    private static final int GOLD = Color.rgb(255, 183, 35);

    public SkiProjectionView(Context context, AppState state) {
        super(context);
        this.state = state;
        state.addListener(this);
        p.setStyle(Paint.Style.FILL);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeCap(Paint.Cap.ROUND);
        setBackgroundColor(Color.rgb(246, 250, 253));
    }

    @Override protected void onDetachedFromWindow() {
        state.removeListener(this);
        super.onDetachedFromWindow();
    }

    @Override public void onStateChanged() { invalidate(); }

    @Override protected void onDraw(Canvas c) {
        super.onDraw(c);
        long now = SystemClock.uptimeMillis();
        if (lastFrameMs == 0L) lastFrameMs = now;
        float dt = Math.min(0.05f, (now - lastFrameMs) / 1000f);
        lastFrameMs = now;

        if (state.running && !state.paused) {
            worldOffset += dt * (90f + state.speedKmh * 15f);
        }

        drawMat(c);
        switch (state.course) {
            case BASIC_GATES: drawGates(c); break;
            case S_CURVE: drawSCurve(c); break;
            case STRAIGHT_RUN: drawStraight(c); break;
            case KIDS_ADVENTURE: drawKids(c); break;
            case OBSTACLES: drawObstacles(c); break;
        }
        drawSkis(c);

        if (!state.running) drawHint(c, "READY");
        else if (state.paused) drawHint(c, "PAUSED");

        if (state.running && !state.paused) postInvalidateOnAnimation();
    }

    private void drawMat(Canvas c) {
        int w = getWidth(), h = getHeight();
        p.setColor(Color.rgb(250, 253, 255));
        c.drawRect(0, 0, w, h, p);
        p.setColor(Color.rgb(222, 229, 235));
        c.drawRect(0, 0, w * .025f, h, p);
        c.drawRect(w * .975f, 0, w, h, p);
        p.setColor(Color.rgb(236, 242, 247));
        for (int i = 0; i < 9; i++) {
            float y = i * h / 8f;
            c.drawRect(w * .03f, y, w * .97f, y + 1.2f, p);
        }
    }

    private float spacingPx() {
        return lerp(getHeight() * .10f, getHeight() * .25f,
                (state.longitudinalSpacing - 20f) / 60f);
    }

    private float lateralPx() {
        return lerp(getWidth() * .10f, getWidth() * .34f,
                (state.lateralSpacing - 20f) / 60f);
    }

    private float objectPx() {
        return lerp(Math.min(getWidth(), getHeight()) * .028f,
                Math.min(getWidth(), getHeight()) * .082f,
                (state.objectSize - 10f) / 40f);
    }

    private void drawGates(Canvas c) {
        float spacing = spacingPx();
        int first = (int) Math.floor(worldOffset / spacing) - 1;
        int count = (int) (getHeight() / spacing) + 4;
        float maxLat = lateralPx();

        for (int n = first; n < first + count; n++) {
            float y = positiveMod(n * spacing + worldOffset, getHeight() + spacing) - spacing;
            float side = ((n & 1) == 0 ? -1f : 1f);
            float cx = getWidth() * .5f + side * maxLat * .50f;
            drawGate(c, cx, y, (n & 1) == 0 ? RED : BLUE);
        }
    }

    private void drawGate(Canvas c, float cx, float cy, int color) {
        float s = objectPx();
        float width = s * 1.35f;
        float poleH = s * 1.35f;

        stroke.setColor(color);
        stroke.setStrokeWidth(Math.max(4f, s * .09f));
        c.drawLine(cx - width/2, cy - poleH/2, cx - width/2, cy + poleH/2, stroke);
        c.drawLine(cx + width/2, cy - poleH/2, cx + width/2, cy + poleH/2, stroke);

        p.setColor(color);
        RectF flag = new RectF(cx - width/2, cy - poleH*.40f,
                cx + width/2, cy + poleH*.12f);
        c.drawRoundRect(flag, s*.10f, s*.10f, p);

        stroke.setColor(Color.WHITE);
        stroke.setStrokeWidth(Math.max(2f, s*.05f));
        c.drawLine(cx - s*.20f, cy - s*.12f, cx, cy - s*.25f, stroke);
        c.drawLine(cx, cy - s*.25f, cx + s*.20f, cy - s*.12f, stroke);
    }

    private void drawSCurve(Canvas c) {
        int h = getHeight();
        float amp = lateralPx();
        float phase = worldOffset * .0065f;
        float halfWidth = objectPx() * .70f;

        stroke.setStrokeWidth(halfWidth * 2f);
        stroke.setColor(Color.argb(115, 64, 174, 238));

        Path path = new Path();
        for (int y = -20; y <= h + 20; y += 10) {
            float x = getWidth()*.5f + (float)Math.sin(y*.0105f + phase) * amp;
            if (y == -20) path.moveTo(x, y); else path.lineTo(x, y);
        }
        c.drawPath(path, stroke);

        stroke.setStrokeWidth(Math.max(3f, halfWidth*.08f));
        stroke.setColor(Color.argb(180, 255, 255, 255));
        c.drawPath(path, stroke);
    }

    private void drawStraight(Canvas c) {
        float cx = getWidth() * .5f;
        float dash = Math.max(30f, objectPx()*.65f);

        stroke.setStrokeWidth(Math.max(5f, objectPx()*.10f));
        stroke.setColor(BLUE);

        float offset = positiveMod(worldOffset, dash*2f);
        for (float y = -dash*2 + offset; y < getHeight(); y += dash*2f) {
            c.drawLine(cx, y, cx, y + dash, stroke);
        }

        float spacing = spacingPx();
        int count = (int)(getHeight()/spacing)+3;
        for (int i=0; i<count; i++) {
            float y = positiveMod(i*spacing + worldOffset, getHeight()+spacing)-spacing;
            drawArrow(c, cx, y, objectPx(), BLUE);
        }
    }

    private void drawArrow(Canvas c, float x, float y, float s, int color) {
        stroke.setColor(color);
        stroke.setStrokeWidth(Math.max(5f, s*.13f));
        c.drawLine(x, y+s*.42f, x, y-s*.35f, stroke);
        c.drawLine(x, y-s*.35f, x-s*.30f, y-s*.05f, stroke);
        c.drawLine(x, y-s*.35f, x+s*.30f, y-s*.05f, stroke);
    }

    private void drawKids(Canvas c) {
        float spacing = spacingPx() * .88f;
        int first = (int)Math.floor(worldOffset/spacing)-2;
        int count = (int)(getHeight()/spacing)+5;
        float allowed = lateralPx() * 1.35f;

        for (int n=first; n<first+count; n++) {
            if (!spawnForIndex(n, Math.min(100, state.obstacleProbability + 15))) continue;

            float y = positiveMod(n*spacing + worldOffset,
                    getHeight()+spacing)-spacing;
            Random r = seeded(n*31337L + 7);
            float x = getWidth()*.5f + (r.nextFloat()*2f-1f)*allowed;
            int type = Math.floorMod(n, 4);

            if (type == 0) drawBalloon(c, x, y, RED);
            else if (type == 1) drawBalloon(c, x, y, BLUE);
            else if (type == 2) drawStar(c, x, y, objectPx()*.55f, GOLD);
            else drawPenguin(c, x, y);
        }
    }

    private void drawObstacles(Canvas c) {
        float spacing = spacingPx();
        int first = (int)Math.floor(worldOffset/spacing)-2;
        int count = (int)(getHeight()/spacing)+5;
        float allowed = lateralPx() * 1.40f;

        for (int n=first; n<first+count; n++) {
            if (!spawnForIndex(n, state.obstacleProbability)) continue;

            float y = positiveMod(n*spacing + worldOffset,
                    getHeight()+spacing)-spacing;
            Random r = seeded(n*9127L + 99);
            float x = getWidth()*.5f + (r.nextFloat()*2f-1f)*allowed;
            int type = Math.floorMod(n, 3);

            if (type == 0) drawCone(c, x, y);
            else if (type == 1) drawRock(c, x, y);
            else drawTree(c, x, y);
        }
    }

    private boolean spawnForIndex(int index, int probability) {
        return seeded(index * 15485863L + 23).nextInt(100) < probability;
    }

    private Random seeded(long seed) {
        return new Random(seed);
    }

    private void drawBalloon(Canvas c, float x, float y, int color) {
        float s = objectPx();
        p.setColor(color);
        c.drawOval(new RectF(x-s*.36f, y-s*.52f, x+s*.36f, y+s*.28f), p);
        stroke.setColor(Color.rgb(120,120,120));
        stroke.setStrokeWidth(2f);
        c.drawLine(x, y+s*.28f, x, y+s*.75f, stroke);
    }

    private void drawStar(Canvas c, float x, float y, float r, int color) {
        Path path = new Path();
        for (int i=0; i<10; i++) {
            double a = -Math.PI/2 + i*Math.PI/5;
            float rr = (i%2==0) ? r : r*.42f;
            float px = x + (float)Math.cos(a)*rr;
            float py = y + (float)Math.sin(a)*rr;
            if (i==0) path.moveTo(px,py); else path.lineTo(px,py);
        }
        path.close();
        p.setColor(color);
        c.drawPath(path,p);
    }

    private void drawPenguin(Canvas c, float x, float y) {
        float s = objectPx();

        p.setColor(NAVY);
        c.drawOval(new RectF(x-s*.38f,y-s*.55f,x+s*.38f,y+s*.45f),p);

        p.setColor(Color.WHITE);
        c.drawOval(new RectF(x-s*.25f,y-s*.25f,x+s*.25f,y+s*.38f),p);

        p.setColor(GOLD);
        Path beak = new Path();
        beak.moveTo(x,y-s*.10f);
        beak.lineTo(x+s*.18f,y);
        beak.lineTo(x,y+s*.06f);
        beak.close();
        c.drawPath(beak,p);

        p.setColor(Color.WHITE);
        c.drawCircle(x-s*.13f,y-s*.30f,s*.08f,p);
        c.drawCircle(x+s*.13f,y-s*.30f,s*.08f,p);

        p.setColor(NAVY);
        c.drawCircle(x-s*.13f,y-s*.30f,s*.035f,p);
        c.drawCircle(x+s*.13f,y-s*.30f,s*.035f,p);
    }

    private void drawCone(Canvas c, float x, float y) {
        float s = objectPx();

        Path cone = new Path();
        cone.moveTo(x,y-s*.55f);
        cone.lineTo(x-s*.34f,y+s*.35f);
        cone.lineTo(x+s*.34f,y+s*.35f);
        cone.close();

        p.setColor(Color.rgb(255,106,34));
        c.drawPath(cone,p);

        p.setColor(Color.WHITE);
        c.drawRect(x-s*.25f,y-s*.05f,x+s*.25f,y+s*.08f,p);

        p.setColor(NAVY);
        c.drawRoundRect(new RectF(x-s*.42f,y+s*.34f,x+s*.42f,y+s*.46f),
                s*.05f,s*.05f,p);
    }

    private void drawRock(Canvas c, float x, float y) {
        float s = objectPx();

        Path rock = new Path();
        rock.moveTo(x-s*.48f,y+s*.34f);
        rock.lineTo(x-s*.32f,y-s*.25f);
        rock.lineTo(x-s*.04f,y-s*.50f);
        rock.lineTo(x+s*.38f,y-s*.20f);
        rock.lineTo(x+s*.50f,y+s*.34f);
        rock.close();

        p.setColor(Color.rgb(92,105,117));
        c.drawPath(rock,p);

        stroke.setColor(Color.rgb(155,168,180));
        stroke.setStrokeWidth(Math.max(2f,s*.05f));
        c.drawLine(x-s*.12f,y-s*.30f,x+s*.20f,y+s*.15f,stroke);
    }

    private void drawTree(Canvas c, float x, float y) {
        float s = objectPx();

        p.setColor(Color.rgb(127,82,49));
        c.drawRect(x-s*.08f,y+s*.18f,x+s*.08f,y+s*.52f,p);

        p.setColor(GREEN);

        Path t = new Path();
        t.moveTo(x,y-s*.58f);
        t.lineTo(x-s*.45f,y+s*.18f);
        t.lineTo(x+s*.45f,y+s*.18f);
        t.close();
        c.drawPath(t,p);

        Path t2 = new Path();
        t2.moveTo(x,y-s*.28f);
        t2.lineTo(x-s*.52f,y+s*.34f);
        t2.lineTo(x+s*.52f,y+s*.34f);
        t2.close();
        c.drawPath(t2,p);
    }

    private void drawSkis(Canvas c) {
        float w = getWidth(), h = getHeight();
        float s = Math.min(w,h)*.07f;

        p.setColor(NAVY);
        c.drawRoundRect(new RectF(w*.5f-s*.38f,h-s*.85f,
                w*.5f-s*.12f,h+s*.25f),s*.12f,s*.12f,p);
        c.drawRoundRect(new RectF(w*.5f+s*.12f,h-s*.85f,
                w*.5f+s*.38f,h+s*.25f),s*.12f,s*.12f,p);

        p.setColor(RED);
        c.drawRect(w*.5f-s*.34f,h-s*.60f,w*.5f-s*.16f,h-s*.52f,p);
        c.drawRect(w*.5f+s*.16f,h-s*.60f,w*.5f+s*.34f,h-s*.52f,p);
    }

    private void drawHint(Canvas c, String text) {
        p.setColor(Color.argb(160,23,34,49));
        RectF box = new RectF(getWidth()*.38f,getHeight()*.43f,
                getWidth()*.62f,getHeight()*.57f);
        c.drawRoundRect(box,24,24,p);

        p.setColor(Color.WHITE);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(Math.max(28,getHeight()*.055f));
        p.setFakeBoldText(true);
        c.drawText(text,getWidth()*.5f,getHeight()*.52f,p);
        p.setFakeBoldText(false);
        p.setTextAlign(Paint.Align.LEFT);
    }

    private static float positiveMod(float a, float b) {
        float r = a % b;
        return r < 0 ? r + b : r;
    }

    private static float lerp(float a, float b, float t) {
        return a + (b-a) * Math.max(0f, Math.min(1f, t));
    }
}
