package fun.skiaddict.projector.courses;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CourseRegistry {
    private static final List<CourseModule> COURSES = Collections.unmodifiableList(Arrays.asList(
            new AlpineBeginnerCourse(),
            new SCurveCourse(),
            new StraightRunCourse(),
            new KidsAdventureCourse(),
            new ObstaclesCourse()
    ));

    private CourseRegistry() {}

    public static List<CourseModule> all() {
        return COURSES;
    }

    public static CourseModule defaultCourse() {
        return COURSES.get(0);
    }

    public static CourseModule byId(String id) {
        if (id != null) {
            for (CourseModule course : COURSES) {
                if (course.id().equals(id)) return course;
            }
        }
        return defaultCourse();
    }

    public static int indexOf(String id) {
        for (int i = 0; i < COURSES.size(); i++) {
            if (COURSES.get(i).id().equals(id)) return i;
        }
        return 0;
    }
}
