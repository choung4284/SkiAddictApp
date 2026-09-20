package fun.skiaddict.projector;

import java.util.concurrent.CopyOnWriteArrayList;

public final class ProjectorSession {
    public interface Listener { void onProjectorStateChanged(); }

    private static final ProjectorSession INSTANCE=new ProjectorSession();
    public static ProjectorSession get(){ return INSTANCE; }

    private final CopyOnWriteArrayList<Listener> listeners=new CopyOnWriteArrayList<>();
    private String courseId="basic_gates";
    private int[] values=CourseStore.byId(courseId).defaults.clone();
    private boolean playing=false;
    private long resetToken=0;

    private ProjectorSession(){}

    public synchronized void selectCourse(CourseStore.Course course,int[] newValues){
        courseId=course.id;
        values=newValues.clone();
        notifyChanged();
    }

    public synchronized void updateValues(int[] newValues){
        values=newValues.clone();
        notifyChanged();
    }

    public synchronized CourseStore.Course course(){ return CourseStore.byId(courseId); }
    public synchronized int[] values(){ return values.clone(); }
    public synchronized boolean isPlaying(){ return playing; }
    public synchronized long resetToken(){ return resetToken; }

    public synchronized void play(){ playing=true; notifyChanged(); }
    public synchronized void pause(){ playing=false; notifyChanged(); }
    public synchronized void reset(){ playing=false; resetToken++; notifyChanged(); }

    public void addListener(Listener l){ listeners.addIfAbsent(l); }
    public void removeListener(Listener l){ listeners.remove(l); }

    private void notifyChanged(){
        for(Listener l:listeners) l.onProjectorStateChanged();
    }
}
