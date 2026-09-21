package fun.skiaddict.projector;

import android.content.Context;

public final class I18n {
    private static final String PREF="skiaddict_settings", KEY_LANG="language";
    public static boolean th(Context c){return "th".equals(c.getSharedPreferences(PREF,Context.MODE_PRIVATE).getString(KEY_LANG,"th"));}
    public static void setLanguage(Context c,String lang){c.getSharedPreferences(PREF,Context.MODE_PRIVATE).edit().putString(KEY_LANG,lang).apply();}
    public static String t(Context c,String k){
        boolean th=th(c);
        switch(k){
            case "home":return th?"หน้าหลัก":"Home"; case "back_home":return th?"← หน้าหลัก":"← HOME"; case "interactive":return th?"โหมดโต้ตอบ":"Interactive"; case "projector_setup":return th?"ตั้งค่าโปรเจคเตอร์":"Projector Setup"; case "saved_presets":return th?"พรีเซ็ตที่บันทึกไว้":"Saved Presets"; case "settings":return th?"ตั้งค่า":"Settings";
            case "projector_status":return th?"โปรเจคเตอร์":"Projector"; case "connected":return th?"เชื่อมต่อแล้ว":"Connected"; case "not_connected":return th?"ยังไม่เชื่อมต่อ":"Not connected";
            case "start":return th?"เริ่ม":"Start"; case "pause":return th?"หยุดชั่วคราว":"Pause"; case "reset":return th?"รีเซ็ต":"Reset"; case "parameters":return th?"พารามิเตอร์":"Parameters";
            case "beginner":return th?"ระดับเริ่มต้น":"Beginner"; case "course_detail":return th?"รายละเอียดคอร์ส":"Course Detail";
            case "general":return th?"ทั่วไป":"General"; case "display_graphics":return th?"การแสดงผลและกราฟิก":"Display & Graphics"; case "projector_hardware":return th?"ฮาร์ดแวร์ / โปรเจคเตอร์":"Projector / Hardware"; case "course_training":return th?"คอร์สและการฝึกซ้อม":"Course & Training"; case "maintenance":return th?"การบำรุงรักษา":"Maintenance"; case "about":return th?"เกี่ยวกับ":"About";
            case "language":return th?"ภาษา":"Language"; case "units":return th?"หน่วย":"Units"; case "metric":return th?"เมตริก (ม., กม./ชม.)":"Metric"; case "theme":return th?"ธีม":"Theme"; case "light":return th?"สว่าง":"Light"; case "resolution":return th?"ความละเอียด":"Resolution"; case "frame_rate":return th?"เฟรมเรต":"Frame Rate"; case "ui_brightness":return th?"ความสว่าง UI":"UI Brightness";
            case "auto_connect":return th?"เชื่อมต่อโปรเจคเตอร์อัตโนมัติ":"Auto Connect Projector"; case "default_course":return th?"คอร์สเริ่มต้น":"Default Course"; case "default_difficulty":return th?"ระดับความยากเริ่มต้น":"Default Difficulty"; case "save_history":return th?"บันทึกประวัติการฝึก":"Save Training History"; case "diagnostics":return th?"ตรวจสอบระบบ":"System Diagnostics"; case "app_version":return th?"เวอร์ชันแอป":"App Version";
            case "developer_mode":return th?"โหมดนักพัฒนา":"Developer Mode"; case "enable_developer":return th?"เปิดโหมดนักพัฒนา":"Enable Developer Mode"; case "projector_mirror":return "Projector Mirror Preview"; case "debug_overlay":return th?"แสดง Debug Overlay":"Show Debug Overlay"; case "safe_area":return th?"แสดง Safe Area Guide":"Show Safe Area";
            case "x_position":return th?"ตำแหน่ง X":"X Position"; case "y_position":return th?"ตำแหน่ง Y":"Y Position"; case "scale_x":return th?"สเกล X":"Scale X"; case "scale_y":return th?"สเกล Y":"Scale Y"; case "rotation":return th?"การหมุน":"Rotation"; case "perspective":return th?"มุมมอง Perspective":"Perspective"; case "brightness":return th?"ความสว่าง":"Brightness"; case "object_scale":return th?"ขนาดวัตถุ":"Object Scale"; case "safe_margin":return th?"Safe Margin":"Safe Margin"; case "top_left":return th?"มุมซ้ายบน":"Top Left"; case "top_right":return th?"มุมขวาบน":"Top Right"; case "bottom_left":return th?"มุมซ้ายล่าง":"Bottom Left"; case "bottom_right":return th?"มุมขวาล่าง":"Bottom Right"; case "apply":return th?"ใช้งาน":"Apply";
        }return k;
    }
    public static String param(Context c,String p){if(!th(c))return p;switch(p){case "Speed":return "ความเร็ว";case "Gate Width":return "ความกว้างประตู";case "Gate Spacing":return "ระยะห่างประตู";case "Gate Size":return "ขนาดประตู";case "Difficulty":return "ระดับความยาก";case "Stage Count":return "จำนวนสเตจ";}return p;}
    public static String category(Context c,CourseStore.Category cat){if(!th(c))return cat.title;switch(cat){case ALPINE:return "Alpine";case S_CURVE:return "S-Curve";case STRAIGHT:return "Straight";case KIDS:return "Kids";case OBSTACLES:return "Obstacles";}return cat.title;}
    public static String courseSummary(Context c,CourseStore.Course course){if(course==null)return "";if(!th(c))return course.summary;switch(course.id){case "basic_gates":return "ฝึกจังหวะและทิศทางด้วยประตูพื้นฐาน";case "s_curve":return "ฝึกการเลี้ยวต่อเนื่องเป็นรูปตัว S";case "wide_turn":return "ฝึกโค้งกว้างและการควบคุมขอบสกี";case "obstacles":return "ฝึกหลบสิ่งกีดขวางและการตอบสนอง";default:return course.summary;}}
    public static String courseTitle(Context c,CourseStore.Course course){if(course==null)return ""; if(!th(c))return course.title;switch(course.id){case "basic_gates":return "ประตูพื้นฐาน";case "s_curve":return "เส้นโค้งตัว S";case "wide_turn":return "โค้งกว้าง";case "obstacles":return "หลบสิ่งกีดขวาง";default:return course.title;}}
}
