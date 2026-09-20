package fun.skiaddict.projector;

import java.util.concurrent.CopyOnWriteArrayList;

public final class AppState {
    public enum Course {
        BASIC_GATES("Alpine Beginner", "Basic Gates"),
        S_CURVE("S-Curve", "Continuous carving line"),
        STRAIGHT_RUN("Straight Run", "Balance & direction"),
        KIDS_ADVENTURE("Kids Adventure", "Balloons, stars & penguins"),
        OBSTACLES("Obstacles", "Random obstacle dodge");

        public final String title;
        public final String subtitle;
        Course(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }
    }

    public interface Listener { void onStateChanged(); }

    private final CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();

    public Course course = Course.BASIC_GATES;
    public float speedKmh = 18f;
    public int objectSize = 30;
    public int obstacleProbability = 50;
    public int longitudinalSpacing = 45;
    public int lateralSpacing = 40;
    public boolean running = false;
    public boolean paused = false;

    public void addListener(Listener listener) { listeners.addIfAbsent(listener); }
    public void removeListener(Listener listener) { listeners.remove(listener); }

    public void notifyChanged() {
        for (Listener listener : listeners) listener.onStateChanged();
    }
}
