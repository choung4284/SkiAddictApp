package fun.skiaddict.projector.courses;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.Random;

import fun.skiaddict.projector.AppState;

public final class CourseRenderContext {
    public static final int RED = Color.rgb(242, 31, 47);
    public static final int BLUE = Color.rgb(38, 122, 236);
    public static final int NAVY = Color.rgb(23, 34, 49);
    public static final int GREEN = Color.rgb(43, 176, 92);
    public static final int GOLD = Color.rgb(255, 183, 35);

    public final Canvas canvas;
    public final AppState state;
    public final float worldOffset;
    public final Paint fill;
    public final Paint stroke;
    public final int width;
    public final int height;

    public CourseRenderContext(Canvas canvas, AppState state, float worldOffset,
                               Paint fill, Paint stroke, int width, int height) {
        this.canvas = canvas;
        this.state = state;
        this.worldOffset = worldOffset;
        this.fill = fill;
        this.stroke = stroke;
        this.width = width;
        this.height = height;
    }

    public float spacingPx() {
        return lerp(height * .10f, height * .25f,
                (state.longitudinalSpacing - 20f) / 60f);
    }

    public float lateralPx() {
        return lerp(width * .10f, width * .34f,
                (state.lateralSpacing - 20f) / 60f);
    }

    public float objectPx() {
        return lerp(Math.min(width, height) * .028f,
                Math.min(width, height) * .082f,
                (state.objectSize - 10f) / 40f);
    }

    public float positiveMod(float a, float b) {
        float r = a % b;
        return r < 0 ? r + b : r;
    }

    public float lerp(float a, float b, float t) {
        return a + (b - a) * Math.max(0f, Math.min(1f, t));
    }

    public Random seeded(long seed) {
        return new Random(seed);
    }

    public boolean spawnForIndex(int index, int probability) {
        return seeded(index * 15485863L + 23).nextInt(100) < probability;
    }

    public void drawGate(float cx, float cy, int color) {
        float s = objectPx();
        float gateWidth = s * 1.35f;
        float poleH = s * 1.35f;

        stroke.setColor(color);
        stroke.setStrokeWidth(Math.max(4f, s * .09f));
        canvas.drawLine(cx - gateWidth / 2, cy - poleH / 2,
                cx - gateWidth / 2, cy + poleH / 2, stroke);
        canvas.drawLine(cx + gateWidth / 2, cy - poleH / 2,
                cx + gateWidth / 2, cy + poleH / 2, stroke);

        fill.setColor(color);
        RectF flag = new RectF(cx - gateWidth / 2, cy - poleH * .40f,
                cx + gateWidth / 2, cy + poleH * .12f);
        canvas.drawRoundRect(flag, s * .10f, s * .10f, fill);

        stroke.setColor(Color.WHITE);
        stroke.setStrokeWidth(Math.max(2f, s * .05f));
        canvas.drawLine(cx - s * .20f, cy - s * .12f,
                cx, cy - s * .25f, stroke);
        canvas.drawLine(cx, cy - s * .25f,
                cx + s * .20f, cy - s * .12f, stroke);
    }

    public void drawArrow(float x, float y, float s, int color) {
        stroke.setColor(color);
        stroke.setStrokeWidth(Math.max(5f, s * .13f));
        canvas.drawLine(x, y + s * .42f, x, y - s * .35f, stroke);
        canvas.drawLine(x, y - s * .35f, x - s * .30f, y - s * .05f, stroke);
        canvas.drawLine(x, y - s * .35f, x + s * .30f, y - s * .05f, stroke);
    }

    public void drawBalloon(float x, float y, int color) {
        float s = objectPx();
        fill.setColor(color);
        canvas.drawOval(new RectF(x - s * .36f, y - s * .52f,
                x + s * .36f, y + s * .28f), fill);
        stroke.setColor(Color.rgb(120, 120, 120));
        stroke.setStrokeWidth(2f);
        canvas.drawLine(x, y + s * .28f, x, y + s * .75f, stroke);
    }

    public void drawStar(float x, float y, float r, int color) {
        Path path = new Path();
        for (int i = 0; i < 10; i++) {
            double a = -Math.PI / 2 + i * Math.PI / 5;
            float rr = (i % 2 == 0) ? r : r * .42f;
            float px = x + (float) Math.cos(a) * rr;
            float py = y + (float) Math.sin(a) * rr;
            if (i == 0) path.moveTo(px, py); else path.lineTo(px, py);
        }
        path.close();
        fill.setColor(color);
        canvas.drawPath(path, fill);
    }

    public void drawPenguin(float x, float y) {
        float s = objectPx();

        fill.setColor(NAVY);
        canvas.drawOval(new RectF(x - s * .38f, y - s * .55f,
                x + s * .38f, y + s * .45f), fill);

        fill.setColor(Color.WHITE);
        canvas.drawOval(new RectF(x - s * .25f, y - s * .25f,
                x + s * .25f, y + s * .38f), fill);

        fill.setColor(GOLD);
        Path beak = new Path();
        beak.moveTo(x, y - s * .10f);
        beak.lineTo(x + s * .18f, y);
        beak.lineTo(x, y + s * .06f);
        beak.close();
        canvas.drawPath(beak, fill);

        fill.setColor(Color.WHITE);
        canvas.drawCircle(x - s * .13f, y - s * .30f, s * .08f, fill);
        canvas.drawCircle(x + s * .13f, y - s * .30f, s * .08f, fill);

        fill.setColor(NAVY);
        canvas.drawCircle(x - s * .13f, y - s * .30f, s * .035f, fill);
        canvas.drawCircle(x + s * .13f, y - s * .30f, s * .035f, fill);
    }

    public void drawCone(float x, float y) {
        float s = objectPx();

        Path cone = new Path();
        cone.moveTo(x, y - s * .55f);
        cone.lineTo(x - s * .34f, y + s * .35f);
        cone.lineTo(x + s * .34f, y + s * .35f);
        cone.close();

        fill.setColor(Color.rgb(255, 106, 34));
        canvas.drawPath(cone, fill);

        fill.setColor(Color.WHITE);
        canvas.drawRect(x - s * .25f, y - s * .05f,
                x + s * .25f, y + s * .08f, fill);

        fill.setColor(NAVY);
        canvas.drawRoundRect(new RectF(x - s * .42f, y + s * .34f,
                x + s * .42f, y + s * .46f), s * .05f, s * .05f, fill);
    }

    public void drawRock(float x, float y) {
        float s = objectPx();

        Path rock = new Path();
        rock.moveTo(x - s * .48f, y + s * .34f);
        rock.lineTo(x - s * .32f, y - s * .25f);
        rock.lineTo(x - s * .04f, y - s * .50f);
        rock.lineTo(x + s * .38f, y - s * .20f);
        rock.lineTo(x + s * .50f, y + s * .34f);
        rock.close();

        fill.setColor(Color.rgb(92, 105, 117));
        canvas.drawPath(rock, fill);

        stroke.setColor(Color.rgb(155, 168, 180));
        stroke.setStrokeWidth(Math.max(2f, s * .05f));
        canvas.drawLine(x - s * .12f, y - s * .30f,
                x + s * .20f, y + s * .15f, stroke);
    }

    public void drawTree(float x, float y) {
        float s = objectPx();

        fill.setColor(Color.rgb(127, 82, 49));
        canvas.drawRect(x - s * .08f, y + s * .18f,
                x + s * .08f, y + s * .52f, fill);

        fill.setColor(GREEN);

        Path t = new Path();
        t.moveTo(x, y - s * .58f);
        t.lineTo(x - s * .45f, y + s * .18f);
        t.lineTo(x + s * .45f, y + s * .18f);
        t.close();
        canvas.drawPath(t, fill);

        Path t2 = new Path();
        t2.moveTo(x, y - s * .28f);
        t2.lineTo(x - s * .52f, y + s * .34f);
        t2.lineTo(x + s * .52f, y + s * .34f);
        t2.close();
        canvas.drawPath(t2, fill);
    }
}
