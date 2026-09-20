package fun.skiaddict.projector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.View;

import fun.skiaddict.projector.courses.CourseRenderContext;

public final class SkiProjectionView extends View implements AppState.Listener {
    private final AppState state;
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private long lastFrameMs = 0L;
    private float worldOffset = 0f;

    private static final int RED = Color.rgb(242, 31, 47);
    private static final int NAVY = Color.rgb(23, 34, 49);

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

    @Override public void onStateChanged() {
        invalidate();
    }

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

        CourseRenderContext ctx = new CourseRenderContext(
                c,
                state,
                worldOffset,
                p,
                stroke,
                getWidth(),
                getHeight()
        );
        state.course().render(ctx);

        drawSkis(c);

        if (!state.running) drawHint(c, "READY");
        else if (state.paused) drawHint(c, "PAUSED");

        if (state.running && !state.paused) postInvalidateOnAnimation();
    }

    private void drawMat(Canvas c) {
        int w = getWidth();
        int h = getHeight();

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

    private void drawSkis(Canvas c) {
        float w = getWidth();
        float h = getHeight();
        float s = Math.min(w, h) * .07f;

        p.setColor(NAVY);
        c.drawRoundRect(new RectF(w * .5f - s * .38f, h - s * .85f,
                w * .5f - s * .12f, h + s * .25f),
                s * .12f, s * .12f, p);
        c.drawRoundRect(new RectF(w * .5f + s * .12f, h - s * .85f,
                w * .5f + s * .38f, h + s * .25f),
                s * .12f, s * .12f, p);

        p.setColor(RED);
        c.drawRect(w * .5f - s * .34f, h - s * .60f,
                w * .5f - s * .16f, h - s * .52f, p);
        c.drawRect(w * .5f + s * .16f, h - s * .60f,
                w * .5f + s * .34f, h - s * .52f, p);
    }

    private void drawHint(Canvas c, String text) {
        p.setColor(Color.argb(160, 23, 34, 49));
        RectF box = new RectF(getWidth() * .36f, getHeight() * .43f,
                getWidth() * .64f, getHeight() * .57f);
        c.drawRoundRect(box, 24, 24, p);

        p.setColor(Color.WHITE);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(Math.max(28, getHeight() * .055f));
        p.setFakeBoldText(true);
        c.drawText(text, getWidth() * .5f, getHeight() * .52f, p);
        p.setFakeBoldText(false);
        p.setTextAlign(Paint.Align.LEFT);
    }
}
