package fun.skiaddict.projector;

import android.content.Context;
import android.content.SharedPreferences;

public final class I18n {
    private static final String PREF="skiaddict_settings";
    private static final String KEY_LANG="language";

    public static boolean th(Context c){
        return "th".equals(c.getSharedPreferences(PREF,Context.MODE_PRIVATE).getString(KEY_LANG,"en"));
    }

    public static void setLanguage(Context c,String lang){
        c.getSharedPreferences(PREF,Context.MODE_PRIVATE).edit().putString(KEY_LANG,lang).apply();
    }

    public static String t(Context c,String key){
        boolean th=th(c);
        switch(key){
            case "home": return th?"หน้าหลัก":"Home";
            case "interactive": return th?"โหมดโต้ตอบ":"Interactive";
            case "projector_setup": return th?"ตั้งค่าโปรเจคเตอร์":"Projector Setup";
            case "saved_presets": return th?"พรีเซ็ตที่บันทึกไว้":"Saved Presets";
            case "settings": return th?"ตั้งค่า":"Settings";
            case "connected": return th?"เชื่อมต่อแล้ว":"Connected";
            case "not_connected": return th?"ยังไม่เชื่อมต่อ":"Not connected";
            case "category": return th?"หมวดหมู่":"CATEGORY";
            case "courses": return th?"คอร์ส":"COURSES";
            case "open": return th?"เปิด":"Open";
            case "live_view": return th?"มุมมองสด":"LIVE VIEW";
            case "parameters": return th?"พารามิเตอร์":"PARAMETERS";
            case "start": return th?"เริ่ม":"Start";
            case "pause": return th?"หยุดชั่วคราว":"Pause";
            case "reset": return th?"รีเซ็ต":"Reset";
            case "back_home": return th?"← หน้าหลัก":"← HOME";
            case "course_detail": return th?"รายละเอียดคอร์ส":"COURSE DETAIL";
            case "projector_preview": return th?"ตัวอย่างโปรเจคเตอร์":"Projector Preview";
            case "machine_preset": return th?"พรีเซ็ตเครื่อง":"Machine Preset";
            case "mat_size": return th?"ขนาดพรม":"Mat Size";
            case "alignment": return th?"การจัดตำแหน่ง":"Alignment";
            case "keystone": return th?"คีย์สโตน 4 มุม":"Keystone (4-Corner)";
            case "perspective": return th?"เปอร์สเปกทีฟ":"Perspective";
            case "image": return th?"ภาพ":"Image";
            case "calibration_tools": return th?"เครื่องมือปรับเทียบ":"Calibration Tools";
            case "apply": return th?"ใช้งาน":"Apply";
            case "save_preset": return th?"บันทึกพรีเซ็ต":"Save Preset";
            case "width": return th?"ความกว้าง":"Width";
            case "length": return th?"ความยาว":"Length";
            case "safe_margin": return th?"ระยะขอบปลอดภัย":"Safe Margin";
            case "x_position": return th?"ตำแหน่ง X":"X Position";
            case "y_position": return th?"ตำแหน่ง Y":"Y Position";
            case "scale_x": return th?"สเกล X":"Scale X";
            case "scale_y": return th?"สเกล Y":"Scale Y";
            case "rotation": return th?"การหมุน":"Rotation";
            case "perspective_strength": return th?"ความแรงเปอร์สเปกทีฟ":"Perspective Strength";
            case "horizon_height": return th?"ระดับเส้นขอบฟ้า":"Horizon Height";
            case "vanishing_x": return th?"จุดรวมสายตา X":"Vanishing Point X";
            case "vanishing_y": return th?"จุดรวมสายตา Y":"Vanishing Point Y";
            case "brightness": return th?"ความสว่าง":"Brightness";
            case "contrast": return th?"คอนทราสต์":"Contrast";
            case "object_scale": return th?"ขนาดวัตถุ":"Object Scale";
            case "show_grid": return th?"แสดงกริด":"Show Grid";
            case "show_corners": return th?"แสดงมุม":"Show Corners";
            case "show_boundary": return th?"แสดงขอบเขต":"Show Boundary";
            case "show_center": return th?"แสดงเส้นกลาง":"Show Center Line";
            case "reset_calibration": return th?"รีเซ็ตการปรับเทียบ":"Reset Calibration";
            case "language": return th?"ภาษา":"Language";
            case "thai": return "ไทย";
            case "english": return "English";
            case "general": return th?"ทั่วไป":"General";
            case "display_graphics": return th?"การแสดงผลและกราฟิก":"Display & Graphics";
            case "projector_hardware": return th?"โปรเจคเตอร์ / ฮาร์ดแวร์":"Projector / Hardware";
            case "course_training": return th?"คอร์สและการฝึกซ้อม":"Course & Training";
            case "data_account": return th?"ข้อมูลและบัญชี":"Data & Account";
            case "maintenance": return th?"การบำรุงรักษา":"Maintenance";
            case "about": return th?"เกี่ยวกับ":"About";
            case "units": return th?"หน่วย":"Units";
            case "metric": return th?"เมตริก (ม., กม./ชม.)":"Metric (m, km/h)";
            case "theme": return th?"ธีม":"Theme";
            case "light": return th?"สว่าง":"Light";
            case "dark": return th?"มืด":"Dark";
            case "resolution": return th?"ความละเอียด":"Resolution";
            case "frame_rate": return th?"เฟรมเรต":"Frame Rate";
            case "ui_brightness": return th?"ความสว่าง UI":"UI Brightness";
            case "auto_connect": return th?"เชื่อมต่อโปรเจคเตอร์อัตโนมัติ":"Auto Connect Projector";
            case "auto_apply_preset": return th?"ใช้พรีเซ็ตล่าสุดอัตโนมัติ":"Auto Apply Last Preset";
            case "default_course": return th?"คอร์สเริ่มต้น":"Default Course";
            case "default_difficulty": return th?"ระดับเริ่มต้น":"Default Difficulty";
            case "save_history": return th?"บันทึกประวัติการฝึก":"Save Training History";
            case "diagnostics": return th?"ตรวจสอบระบบ":"System Diagnostics";
            case "app_version": return th?"เวอร์ชันแอป":"App Version";
            case "beginner": return th?"ระดับเริ่มต้น":"Beginner";
            case "object_only": return th?"เฉพาะวัตถุ / พื้นดำ":"Object Only / Black Background";
            case "fit_to_mat": return th?"พอดีกับพรม":"Fit to Mat";
            case "top_left": return th?"ซ้ายบน":"Top Left";
            case "top_right": return th?"ขวาบน":"Top Right";
            case "bottom_left": return th?"ซ้ายล่าง":"Bottom Left";
            case "bottom_right": return th?"ขวาล่าง":"Bottom Right";
            case "welcome": return th?"ยินดีต้อนรับสู่ Ski Addict":"Welcome to Ski Addict";
            case "select_course": return th?"เลือกคอร์สเพื่อเริ่มการฝึก":"Select a course to start training";
            case "same_layout": return th?"มือถือและแท็บเล็ตใช้เลย์เอาต์เดียวกัน":"Phone and tablet use the same layout";
            case "tap_course": return th?"แตะคอร์สเพื่อเปิดมุมมองสดและพารามิเตอร์":"Tap a course to open Live View and parameters";
            case "projector_status": return th?"โปรเจคเตอร์":"Projector";
        }
        return key;
    }

    public static String category(Context c,CourseStore.Category cat){
        if(!th(c)) return cat.title;
        switch(cat){
            case ALPINE:return "อัลไพน์";
            case S_CURVE:return "เอสเคิร์ฟ";
            case STRAIGHT:return "ทักษะเส้นตรง";
            case KIDS:return "เด็ก";
            case OBSTACLES:return "สิ่งกีดขวาง";
        }
        return cat.title;
    }

    public static String courseTitle(Context c,CourseStore.Course course){
        if(!th(c)) return course.title;
        switch(course.id){
            case "basic_gates":return "ประตูพื้นฐาน";
            case "slalom_standard":return "สลาลอมมาตรฐาน";
            case "random_gates":return "ประตูสุ่ม";
            case "wide_turn":return "โค้งกว้าง";
            case "figure_8":return "เลข 8";
            case "s_curve":return "เส้นโค้งตัว S";
            case "candy_trail":return "ทางลูกกวาด";
            case "snowman_trail":return "ทางตุ๊กตาหิมะ";
            case "follow_leader":return "ตามผู้นำ";
            case "edge_control":return "ฝึกการกดขอบ";
            case "reaction_lights":return "ไฟตอบสนอง";
            case "penguin_parade":return "ขบวนเพนกวิน";
            case "balloon_pop":return "ลูกโป่งมหรรษา";
            case "animal_footprints":return "รอยเท้าสัตว์";
            case "random_balloon_hunt":return "ล่าลูกโป่งสุ่ม";
            case "coin_collection":return "เก็บเหรียญ";
            case "color_match":return "จับคู่สี";
            case "combo_score":return "คอมโบและคะแนน";
            case "maze":return "เขาวงกต";
            case "obstacles":return "หลบสิ่งกีดขวาง";
            case "auto_difficulty":return "ปรับความยากอัตโนมัติ";
        }
        return course.subtitle;
    }

    public static String courseSummary(Context c,CourseStore.Course course){
        if(!th(c)) return course.summary;
        switch(course.id){
            case "basic_gates":return "ฝึกจังหวะและทิศทางด้วยประตูพื้นฐานที่กว้าง";
            case "slalom_standard":return "สลาลอมซ้ายขวาด้วยระยะประตูสม่ำเสมอ";
            case "random_gates":return "ประตูสุ่มตำแหน่งเพื่อฝึกการปรับตัว";
            case "wide_turn":return "ฝึกโค้งกว้างและการใช้ขอบสกีอย่างต่อเนื่อง";
            case "figure_8":return "ฝึกเลี้ยวต่อเนื่องตามรูปเลข 8";
            case "s_curve":return "เส้นทางตัว S สำหรับฝึกจังหวะการคาร์ฟ";
            case "candy_trail":return "เส้นทางโค้งสนุกพร้อมเป้าหมายสีสัน";
            case "snowman_trail":return "โค้งนุ่มนวลพร้อมจุดสังเกตตุ๊กตาหิมะ";
            case "follow_leader":return "ฝึกตามเส้นทางที่กำหนดอย่างแม่นยำ";
            case "edge_control":return "ฝึกการเปลี่ยนแรงกดขอบระหว่างรักษาทิศทาง";
            case "reaction_lights":return "ตอบสนองต่อเป้าหมายที่สว่างขึ้นอย่างรวดเร็ว";
            case "penguin_parade":return "เส้นทางเพนกวินสำหรับเด็กและผู้เริ่มต้น";
            case "balloon_pop":return "เก็บหรือแตะลูกโป่งสีต่าง ๆ";
            case "animal_footprints":return "ตามรอยเท้าสัตว์บนลานสกี";
            case "random_balloon_hunt":return "ลูกโป่งจะเกิดใหม่แบบสุ่มตำแหน่ง";
            case "coin_collection":return "เก็บเหรียญที่ปรากฏบนพื้นที่ฝึก";
            case "color_match":return "เลือกและตามเป้าหมายตามสีที่กำหนด";
            case "combo_score":return "เก็บเป้าหมายต่อเนื่องเพื่อทำคอมโบและคะแนน";
            case "maze":return "หาทางผ่านเขาวงกตโดยไม่ชนขอบ";
            case "obstacles":return "หลบกรวย หิน และสิ่งกีดขวางต่าง ๆ";
            case "auto_difficulty":return "ความหนาแน่นของสิ่งกีดขวางเพิ่มตามการฝึก";
        }
        return course.summary;
    }

    public static String param(Context c,String p){
        if(!th(c)) return p;
        switch(p){
            case "Speed":return "ความเร็ว";
            case "Gate Width":return "ความกว้างประตู";
            case "Gate Spacing":return "ระยะห่างประตู";
            case "Gate Size":return "ขนาดประตู";
            case "Difficulty":return "ระดับความยาก";
            case "Curve Width":return "ความกว้างโค้ง";
            case "Curve Frequency":return "ความถี่ของโค้ง";
            case "Path Thickness":return "ความหนาเส้นทาง";
            case "Lane Width":return "ความกว้างเลน";
            case "Target Spacing":return "ระยะห่างเป้าหมาย";
            case "Target Size":return "ขนาดเป้าหมาย";
            case "Object Size":return "ขนาดวัตถุ";
            case "Spawn Rate":return "อัตราการเกิด";
            case "Longitudinal Gap":return "ระยะห่างตามแนวยาว";
            case "Lateral Range":return "ช่วงด้านข้าง";
            case "Obstacle Size":return "ขนาดสิ่งกีดขวาง";
            case "Obstacle Probability":return "ความถี่สิ่งกีดขวาง";
        }
        return p;
    }
}
