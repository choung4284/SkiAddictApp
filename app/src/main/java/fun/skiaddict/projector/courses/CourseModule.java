package fun.skiaddict.projector.courses;

public interface CourseModule {
    String id();
    String title();
    String subtitle();
    String icon();
    void render(CourseRenderContext ctx);
}
