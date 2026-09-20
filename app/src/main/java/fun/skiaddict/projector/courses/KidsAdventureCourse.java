package fun.skiaddict.projector.courses;

import java.util.Random;

public final class KidsAdventureCourse implements CourseModule {
    @Override public String id() { return "kids_adventure"; }
    @Override public String title() { return "Kids Adventure"; }
    @Override public String subtitle() { return "Balloons, stars & penguins"; }
    @Override public String icon() { return "★"; }

    @Override public void render(CourseRenderContext ctx) {
        float spacing = ctx.spacingPx() * .88f;
        int first = (int) Math.floor(ctx.worldOffset / spacing) - 2;
        int count = (int) (ctx.height / spacing) + 5;
        float allowed = ctx.lateralPx() * 1.35f;

        for (int n = first; n < first + count; n++) {
            if (!ctx.spawnForIndex(n, Math.min(100,
                    ctx.state.obstacleProbability + 15))) continue;

            float y = ctx.positiveMod(n * spacing + ctx.worldOffset,
                    ctx.height + spacing) - spacing;
            Random r = ctx.seeded(n * 31337L + 7);
            float x = ctx.width * .5f + (r.nextFloat() * 2f - 1f) * allowed;
            int type = Math.floorMod(n, 4);

            if (type == 0) ctx.drawBalloon(x, y, CourseRenderContext.RED);
            else if (type == 1) ctx.drawBalloon(x, y, CourseRenderContext.BLUE);
            else if (type == 2) ctx.drawStar(x, y,
                    ctx.objectPx() * .55f, CourseRenderContext.GOLD);
            else ctx.drawPenguin(x, y);
        }
    }
}
