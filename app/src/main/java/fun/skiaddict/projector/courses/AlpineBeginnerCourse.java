package fun.skiaddict.projector.courses;

public final class AlpineBeginnerCourse implements CourseModule {
    @Override public String id() { return "alpine_beginner"; }
    @Override public String title() { return "Alpine Beginner"; }
    @Override public String subtitle() { return "Basic Gates"; }
    @Override public String icon() { return "⛷"; }

    @Override public void render(CourseRenderContext ctx) {
        float spacing = ctx.spacingPx();
        int first = (int) Math.floor(ctx.worldOffset / spacing) - 1;
        int count = (int) (ctx.height / spacing) + 4;
        float maxLat = ctx.lateralPx();

        for (int n = first; n < first + count; n++) {
            float y = ctx.positiveMod(n * spacing + ctx.worldOffset,
                    ctx.height + spacing) - spacing;
            float side = ((n & 1) == 0 ? -1f : 1f);
            float cx = ctx.width * .5f + side * maxLat * .50f;
            ctx.drawGate(cx, y, (n & 1) == 0
                    ? CourseRenderContext.RED : CourseRenderContext.BLUE);
        }
    }
}
