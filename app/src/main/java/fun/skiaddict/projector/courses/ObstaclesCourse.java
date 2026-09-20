package fun.skiaddict.projector.courses;

import java.util.Random;

public final class ObstaclesCourse implements CourseModule {
    @Override public String id() { return "obstacles"; }
    @Override public String title() { return "Obstacles"; }
    @Override public String subtitle() { return "Random obstacle dodge"; }
    @Override public String icon() { return "▲"; }

    @Override public void render(CourseRenderContext ctx) {
        float spacing = ctx.spacingPx();
        int first = (int) Math.floor(ctx.worldOffset / spacing) - 2;
        int count = (int) (ctx.height / spacing) + 5;
        float allowed = ctx.lateralPx() * 1.40f;

        for (int n = first; n < first + count; n++) {
            if (!ctx.spawnForIndex(n, ctx.state.obstacleProbability)) continue;

            float y = ctx.positiveMod(n * spacing + ctx.worldOffset,
                    ctx.height + spacing) - spacing;
            Random r = ctx.seeded(n * 9127L + 99);
            float x = ctx.width * .5f + (r.nextFloat() * 2f - 1f) * allowed;
            int type = Math.floorMod(n, 3);

            if (type == 0) ctx.drawCone(x, y);
            else if (type == 1) ctx.drawRock(x, y);
            else ctx.drawTree(x, y);
        }
    }
}
