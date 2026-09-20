package fun.skiaddict.projector.courses;

public final class StraightRunCourse implements CourseModule {
    @Override public String id() { return "straight_run"; }
    @Override public String title() { return "Straight Run"; }
    @Override public String subtitle() { return "Balance & direction"; }
    @Override public String icon() { return "↑"; }

    @Override public void render(CourseRenderContext ctx) {
        float cx = ctx.width * .5f;
        float dash = Math.max(30f, ctx.objectPx() * .65f);

        ctx.stroke.setStrokeWidth(Math.max(5f, ctx.objectPx() * .10f));
        ctx.stroke.setColor(CourseRenderContext.BLUE);

        float offset = ctx.positiveMod(ctx.worldOffset, dash * 2f);
        for (float y = -dash * 2 + offset; y < ctx.height; y += dash * 2f) {
            ctx.canvas.drawLine(cx, y, cx, y + dash, ctx.stroke);
        }

        float spacing = ctx.spacingPx();
        int count = (int) (ctx.height / spacing) + 3;
        for (int i = 0; i < count; i++) {
            float y = ctx.positiveMod(i * spacing + ctx.worldOffset,
                    ctx.height + spacing) - spacing;
            ctx.drawArrow(cx, y, ctx.objectPx(), CourseRenderContext.BLUE);
        }
    }
}
