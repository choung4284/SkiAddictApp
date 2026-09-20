package fun.skiaddict.projector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CourseStore {
    public enum Category {
        ALPINE("ALPINE","⛷"), S_CURVE("S-CURVE","∿"), STRAIGHT("STRAIGHT","↑"),
        KIDS("KIDS","★"), OBSTACLES("OBSTACLES","▲");
        public final String title, icon;
        Category(String title, String icon){ this.title=title; this.icon=icon; }
    }

    public static final class Course {
        public final String id, title, subtitle, summary;
        public final Category category;
        public final String[] parameterNames;
        public final int[] min, max, defaults;
        Course(String id,String title,String subtitle,String summary,Category category,
               String[] parameterNames,int[] min,int[] max,int[] defaults){
            this.id=id; this.title=title; this.subtitle=subtitle; this.summary=summary;
            this.category=category; this.parameterNames=parameterNames;
            this.min=min; this.max=max; this.defaults=defaults;
        }
    }

    private static String[] alpineP(){ return new String[]{"Speed","Gate Width","Gate Spacing","Gate Size","Difficulty"}; }
    private static String[] curveP(){ return new String[]{"Speed","Curve Width","Curve Frequency","Path Thickness","Difficulty"}; }
    private static String[] straightP(){ return new String[]{"Speed","Lane Width","Target Spacing","Target Size","Difficulty"}; }
    private static String[] kidsP(){ return new String[]{"Speed","Object Size","Spawn Rate","Longitudinal Gap","Lateral Range"}; }
    private static String[] obstacleP(){ return new String[]{"Speed","Obstacle Size","Obstacle Probability","Longitudinal Gap","Lateral Range"}; }
    private static int[] mins(){ return new int[]{5,10,20,10,1}; }
    private static int[] maxs(){ return new int[]{30,80,80,50,5}; }
    private static int[] vals(int a,int b,int c,int d,int e){ return new int[]{a,b,c,d,e}; }

    public static final List<Course> ALL = Arrays.asList(
        new Course("basic_gates","Basic Gates","ประตูพื้นฐาน","Wide basic gates for learning rhythm and direction.",Category.ALPINE,alpineP(),mins(),maxs(),vals(18,45,50,30,1)),
        new Course("slalom_standard","Slalom (Standard)","สลาลอมมาตรฐาน","Classic left-right slalom with consistent spacing.",Category.ALPINE,alpineP(),mins(),maxs(),vals(20,40,44,28,2)),
        new Course("random_gates","Random Gates","ประตูสุ่ม","Randomized gate positions for adaptability.",Category.ALPINE,alpineP(),mins(),maxs(),vals(23,38,40,26,4)),
        new Course("wide_turn","Wide Turn","โค้งกว้าง","Wide flowing turns for edge control.",Category.ALPINE,alpineP(),mins(),maxs(),vals(18,65,60,32,2)),
        new Course("figure_8","Figure 8","เลข 8","Continuous figure-eight turning pattern.",Category.ALPINE,alpineP(),mins(),maxs(),vals(17,58,55,30,4)),

        new Course("s_curve","S-Curve","เส้นโค้งตัว S","Smooth S-shaped path for carving rhythm.",Category.S_CURVE,curveP(),mins(),maxs(),vals(15,62,48,32,1)),
        new Course("candy_trail","Candy Trail","ทางลูกกวาด","Playful curving trail with colorful targets.",Category.S_CURVE,curveP(),mins(),maxs(),vals(13,68,42,36,1)),
        new Course("snowman_trail","Snowman Trail","ทางตุ๊กตาหิมะ","Gentle S turns with friendly snowman markers.",Category.S_CURVE,curveP(),mins(),maxs(),vals(12,64,44,35,1)),
        new Course("follow_leader","Follow the Leader","ตามผู้นำ","Follow the highlighted route precisely.",Category.S_CURVE,curveP(),mins(),maxs(),vals(18,48,55,28,3)),

        new Course("edge_control","Edge Control","ฝึกการกดขอบ","Small directional changes while maintaining a straight run.",Category.STRAIGHT,straightP(),mins(),maxs(),vals(16,42,50,28,2)),
        new Course("reaction_lights","Reaction Lights","ไฟตอบสนอง","React to illuminated targets as they appear.",Category.STRAIGHT,straightP(),mins(),maxs(),vals(20,30,38,26,4)),

        new Course("penguin_parade","Penguin Parade","เพนกวินเดินขบวน","Cute penguin trail designed for young skiers.",Category.KIDS,kidsP(),mins(),maxs(),vals(10,38,65,54,66)),
        new Course("balloon_pop","Balloon Pop","ลูกโป่งมหรรษา","Collect or pop colorful balloons.",Category.KIDS,kidsP(),mins(),maxs(),vals(11,40,70,50,72)),
        new Course("animal_footprints","Animal Footprints","รอยเท้าสัตว์","Follow animal footprints across the slope.",Category.KIDS,kidsP(),mins(),maxs(),vals(10,36,64,56,68)),
        new Course("random_balloon_hunt","Random Balloon Hunt","เก็บลูกโป่งสุ่มตำแหน่ง","Balloon targets respawn at random positions.",Category.KIDS,kidsP(),mins(),maxs(),vals(10,38,70,50,74)),
        new Course("coin_collection","Coin Collection","เก็บเหรียญสะสม","Collect coins placed across the training area.",Category.KIDS,kidsP(),mins(),maxs(),vals(12,34,66,46,64)),
        new Course("color_match","Color Match","เก็บตามสี","Follow or collect the requested color.",Category.KIDS,kidsP(),mins(),maxs(),vals(12,32,66,46,62)),
        new Course("combo_score","Combo & Score","คอมโบและคะแนน","Link pickups together for combo scoring.",Category.KIDS,kidsP(),mins(),maxs(),vals(13,34,70,44,68)),

        new Course("maze","Maze","เขาวงกต","Navigate a maze-like route without touching boundaries.",Category.OBSTACLES,obstacleP(),mins(),maxs(),vals(16,28,52,42,58)),
        new Course("obstacles","Obstacles","หลบสิ่งกีดขวาง","Avoid cones, rocks and trees.",Category.OBSTACLES,obstacleP(),mins(),maxs(),vals(18,30,60,44,62)),
        new Course("auto_difficulty","Auto Difficulty","ปรับความยากอัตโนมัติ","Obstacle density increases as the session progresses.",Category.OBSTACLES,obstacleP(),mins(),maxs(),vals(20,28,65,40,68))
    );

    public static List<Course> byCategory(Category c){
        List<Course> out=new ArrayList<>();
        for(Course x:ALL) if(x.category==c) out.add(x);
        return out;
    }
    public static Course byId(String id){
        for(Course c:ALL) if(c.id.equals(id)) return c;
        return ALL.get(0);
    }
}
