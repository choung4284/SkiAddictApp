package fun.skiaddict.projector;

import java.util.concurrent.CopyOnWriteArrayList;

import fun.skiaddict.projector.courses.CourseModule;
import fun.skiaddict.projector.courses.CourseRegistry;

public final class AppState {
    public interface Listener { void onStateChanged(); }

    private final CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();

    public String courseId = CourseRegistry.defaultCourse().id();
    public float speedKmh = 18f;
    public int objectSize = 30;
    public int obstacleProbability = 50;
    public int longitudinalSpacing = 45;
    public int lateralSpacing = 40;
    public boolean running = false;
    public boolean paused = false;

    public CourseModule course() {
        return CourseRegistry.byId(courseId);
    }

    public void selectCourse(String id) {
        courseId = CourseRegistry.byId(id).id();
        notifyChanged();
    }

    public void addListener(Listener listener) { listeners.addIfAbsent(listener); }
    public void removeListener(Listener listener) { listeners.remove(listener); }

    public void notifyChanged() {
        for (Listener listener : listeners) listener.onStateChanged();
    }
}
