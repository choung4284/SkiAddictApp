package fun.skiaddict.projector.courses;

import android.graphics.Color;
import android.graphics.Path;

public final class SCurveCourse implements CourseModule {
    @Override public String id() { return "s_curve"; }
    @Override public String title() { return "S-Curve"; }
    @Override public String subtitle() { return "Continuous carving line"; }
    @Override public String icon() { return "∿"; }

    @Override public void render(CourseRenderContext ctx) {
        float amp = ctx.lateralPx();
        float phase = ctx.worldOffset * .0065f;
        float halfWidth = ctx.objectPx() * .70f;

        ctx.stroke.setStrokeWidth(halfWidth * 2f);
        ctx.stroke.setColor(Color.argb(115, 64, 174, 238));

        Path path = new Path();
        for (int y = -20; y <= ctx.height + 20; y += 10) {
            float x = ctx.width * .5f + (float) Math.sin(y * .0105f + phase) * amp;
            if (y == -20) path.moveTo(x, y); else path.lineTo(x, y);
        }
        ctx.canvas.drawPath(path, ctx.stroke);

        ctx.stroke.setStrokeWidth(Math.max(3f, halfWidth * .08f));
        ctx.stroke.setColor(Color.argb(180, 255, 255, 255));
        ctx.canvas.drawPath(path, ctx.stroke);
    }
}
