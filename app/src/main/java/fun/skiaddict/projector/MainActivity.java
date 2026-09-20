package fun.skiaddict.projector;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.TextView;

import java.util.Locale;

public final class MainActivity extends Activity implements DisplayManager.DisplayListener {
    private static final int RED = Color.rgb(242,31,47);
    private static final int NAVY = Color.rgb(23,34,49);
    private static final int MUTED = Color.rgb(102,112,125);
    private static final int CARD = Color.WHITE;
    private static final int BG = Color.rgb(246,248,250);
    private static final int BORDER = Color.rgb(226,230,234);
    private static final int GOLD = Color.rgb(255,183,35);

    private final AppState state = new AppState();
    private DisplayManager displayManager;
    private ProjectionPresentation presentation;
    private TextView projectorStatus;
    private TextView timerText;
    private CountDownTimer timer;
    private long remainingMs = 10 * 60_000L;
    private boolean timerRunning = false;
    private final Button[] courseButtons = new Button[5];
    private SharedPreferences prefs;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        prefs = getSharedPreferences("ski_addict_demo", MODE_PRIVATE);
        restoreState();

        displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        displayManager.registerDisplayListener(this, null);

        setContentView(buildUi());
        refreshCourseButtons();
        showPresentationIfAvailable();
    }

    @Override protected void onResume() {
        super.onResume();
        showPresentationIfAvailable();
    }

    @Override protected void onDestroy() {
        if (timer != null) timer.cancel();
        if (presentation != null) presentation.dismiss();
        if (displayManager != null) displayManager.unregisterDisplayListener(this);
        super.onDestroy();
    }

    private View buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setPadding(dp(18), dp(12), dp(18), dp(12));

        root.addView(buildHeader(),
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(78)));

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams bodyLp =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        bodyLp.topMargin = dp(10);
        root.addView(body, bodyLp);

        body.addView(buildNav(),
                new LinearLayout.LayoutParams(dp(155), ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout center = new LinearLayout(this);
        center.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams centerLp =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        centerLp.leftMargin = dp(12);
        centerLp.rightMargin = dp(12);
        body.addView(center, centerLp);

        center.addView(buildCourses(),
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(245)));

        LinearLayout bottom = new LinearLayout(this);
        bottom.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams bottomLp =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        bottomLp.topMargin = dp(12);
        center.addView(bottom, bottomLp);

        bottom.addView(buildTimer(),
                new LinearLayout.LayoutParams(dp(340), ViewGroup.LayoutParams.MATCH_PARENT));

        FrameLayout previewCard = card();
        LinearLayout.LayoutParams previewLp =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        previewLp.leftMargin = dp(12);
        bottom.addView(previewCard, previewLp);

        TextView live = label("●  LIVE VIEW", 18, NAVY, true);
        live.setPadding(dp(14), dp(10), 0, 0);
        previewCard.addView(live,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(46)));

        SkiProjectionView preview = new SkiProjectionView(this, state);
        FrameLayout.LayoutParams pvLp =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        pvLp.topMargin = dp(46);
        pvLp.leftMargin = dp(10);
        pvLp.rightMargin = dp(10);
        pvLp.bottomMargin = dp(10);
        previewCard.addView(preview, pvLp);

        body.addView(buildControls(),
                new LinearLayout.LayoutParams(dp(355), ViewGroup.LayoutParams.MATCH_PARENT));

        return root;
    }

    private View buildHeader() {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout brand = new LinearLayout(this);
        brand.setOrientation(LinearLayout.VERTICAL);
        TextView logo = label("Ski Addict", 34, RED, true);
        logo.setTypeface(Typeface.create("sans-serif", Typeface.BOLD_ITALIC));
        TextView club = label("INDOOR SKI CLUB", 11, RED, true);
        club.setLetterSpacing(.12f);
        brand.addView(logo);
        brand.addView(club);
        row.addView(brand, new LinearLayout.LayoutParams(dp(300),
                ViewGroup.LayoutParams.MATCH_PARENT));

        TextView tag = label("TRAIN  PLAY  IMPROVE\nANYTIME. ANYWHERE.", 13, NAVY, false);
        tag.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(tag, new LinearLayout.LayoutParams(dp(245),
                ViewGroup.LayoutParams.MATCH_PARENT));

        Space sp = new Space(this);
        row.addView(sp, new LinearLayout.LayoutParams(0, 1, 1f));

        projectorStatus = label("●  Projector\nChecking...", 15, MUTED, true);
        projectorStatus.setGravity(Gravity.CENTER);
        projectorStatus.setBackground(rounded(Color.WHITE, 18, BORDER));
        row.addView(projectorStatus, new LinearLayout.LayoutParams(dp(185), dp(60)));

        return row;
    }

    private View buildNav() {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.VERTICAL);
        nav.setPadding(0, 0, 0, dp(6));

        nav.addView(navButton("⌂  Home", true));
        nav.addView(navButton("▣  Interactive", false));
        nav.addView(navButton("▤  Projector Setup", false));
        nav.addView(navButton("▦  Saved Presets", false));
        nav.addView(navButton("⚙  Settings", false));

        Space s = new Space(this);
        nav.addView(s, new LinearLayout.LayoutParams(1, 0, 1f));

        TextView foot = label("INDOOR SKI CLUB\n\nMore Runs\nA Brighter You.",
                12, MUTED, false);
        nav.addView(foot);

        return nav;
    }

    private Button navButton(String text, boolean active) {
        Button b = new Button(this);
        b.setAllCaps(false);
        b.setText(text);
        b.setTextSize(13);
        b.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        b.setPadding(dp(12), 0, 0, 0);
        b.setTextColor(active ? Color.WHITE : NAVY);
        b.setBackground(rounded(active ? RED : Color.TRANSPARENT,
                16, Color.TRANSPARENT));

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(54));
        lp.bottomMargin = dp(7);
        b.setLayoutParams(lp);
        return b;
    }

    private View buildCourses() {
        LinearLayout panel = verticalCard();

        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        titleRow.setPadding(dp(16), dp(7), dp(12), 0);

        TextView title = label("⚑  Course", 21, NAVY, true);
        titleRow.addView(title, new LinearLayout.LayoutParams(0, dp(44), 1f));

        TextView hint = label("5 DEMO COURSES", 12, RED, true);
        hint.setGravity(Gravity.CENTER);
        titleRow.addView(hint, new LinearLayout.LayoutParams(dp(130), dp(40)));
        panel.addView(titleRow);

        HorizontalScrollView hsv = new HorizontalScrollView(this);
        hsv.setHorizontalScrollBarEnabled(false);

        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.HORIZONTAL);
        list.setPadding(dp(12), dp(5), dp(12), dp(10));
        hsv.addView(list);

        AppState.Course[] courses = AppState.Course.values();
        String[] icons = {"⛷", "∿", "↑", "★", "▲"};

        for (int i = 0; i < courses.length; i++) {
            final int idx = i;

            LinearLayout item = verticalCard();
            item.setPadding(dp(10), dp(7), dp(10), dp(8));

            TextView hero = label(icons[i], 36,
                    i == 3 ? GOLD : (i == 4 ? NAVY : RED), true);
            hero.setGravity(Gravity.CENTER);
            item.addView(hero,
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(55)));

            item.addView(label(courses[i].title, 14, NAVY, true));
            item.addView(label(courses[i].subtitle, 11, MUTED, false));

            Space gap = new Space(this);
            item.addView(gap, new LinearLayout.LayoutParams(1, 0, 1f));

            Button select = smallButton("Select", false);
            courseButtons[i] = select;
            select.setOnClickListener(v -> {
                state.course = AppState.Course.values()[idx];
                state.notifyChanged();
                refreshCourseButtons();
                saveState();
            });
            item.addView(select,
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(40)));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(175), dp(170));
            lp.rightMargin = dp(9);
            list.addView(item, lp);
        }

        panel.addView(hsv,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        return panel;
    }

    private View buildTimer() {
        LinearLayout panel = verticalCard();
        panel.setPadding(dp(16), dp(10), dp(16), dp(12));

        panel.addView(label("◷  Timer", 21, NAVY, true));
        panel.addView(label("Set your training duration", 12, MUTED, false));

        timerText = label(formatTime(remainingMs), 40, Color.BLACK, true);
        timerText.setGravity(Gravity.CENTER);
        panel.addView(timerText,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(70)));

        LinearLayout presets = new LinearLayout(this);
        String[] mins = {"5 min", "10 min", "20 min", "30 min"};
        int[] vals = {5, 10, 20, 30};

        for (int i = 0; i < mins.length; i++) {
            final int m = vals[i];
            Button b = smallButton(mins[i], m == 10);
            b.setOnClickListener(v -> {
                if (!timerRunning) {
                    remainingMs = m * 60_000L;
                    timerText.setText(formatTime(remainingMs));
                }
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(40), 1f);
            if (i > 0) lp.leftMargin = dp(5);
            presets.addView(b, lp);
        }
        panel.addView(presets);

        Button start = smallButton("▶  Start / Pause", true);
        start.setOnClickListener(v -> toggleTimerAndAnimation());
        LinearLayout.LayoutParams slp =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(50));
        slp.topMargin = dp(8);
        panel.addView(start, slp);

        Button stop = smallButton("■  Stop & Reset", false);
        stop.setOnClickListener(v -> stopAll());
        LinearLayout.LayoutParams stlp =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(43));
        stlp.topMargin = dp(5);
        panel.addView(stop, stlp);

        return panel;
    }

    private View buildControls() {
        LinearLayout panel = verticalCard();
        panel.setPadding(dp(13), dp(11), dp(13), dp(11));

        TextView h = label("☷  ปรับค่าการฝึกซ้อม", 19, Color.WHITE, true);
        h.setPadding(dp(14), 0, 0, 0);
        h.setGravity(Gravity.CENTER_VERTICAL);
        h.setBackground(rounded(RED, 16, Color.TRANSPARENT));
        panel.addView(h,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(55)));

        ScrollView sv = new ScrollView(this);
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        sv.addView(list);

        addSlider(list, "ปรับความเร็ว", "km/h", 5, 30,
                Math.round(state.speedKmh), v -> {
                    state.speedKmh = v;
                    state.notifyChanged();
                    saveState();
                });

        addSlider(list, "ปรับขนาดของรูป", "", 10, 50,
                state.objectSize, v -> {
                    state.objectSize = v;
                    state.notifyChanged();
                    saveState();
                });

        addSlider(list, "ความน่าจะเป็นของอุปสรรค", "%", 30, 70,
                state.obstacleProbability, v -> {
                    state.obstacleProbability = v;
                    state.notifyChanged();
                    saveState();
                });

        addSlider(list, "ระยะห่างด้านยาวของอุปสรรค", "", 20, 80,
                state.longitudinalSpacing, v -> {
                    state.longitudinalSpacing = v;
                    state.notifyChanged();
                    saveState();
                });

        addSlider(list, "ระยะห่างด้านกว้างของอุปสรรค", "", 20, 80,
                state.lateralSpacing, v -> {
                    state.lateralSpacing = v;
                    state.notifyChanged();
                    saveState();
                });

        Button save = smallButton("★  Save Current Preset", true);
        save.setOnClickListener(v -> saveState());

        LinearLayout.LayoutParams saveLp =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(50));
        saveLp.topMargin = dp(8);
        list.addView(save, saveLp);

        panel.addView(sv,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        return panel;
    }

    private interface ValueChanged {
        void onValue(int value);
    }

    private void addSlider(LinearLayout parent, String title, String unit,
                           int min, int max, int initial, ValueChanged listener) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(12), dp(8), dp(12), dp(7));
        box.setBackground(rounded(Color.rgb(250,251,252), 14, BORDER));

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView name = label(title, 12, NAVY, true);
        top.addView(name, new LinearLayout.LayoutParams(0, dp(28), 1f));

        TextView val = label(initial + (unit.isEmpty() ? "" : " " + unit),
                16, RED, true);
        top.addView(val);
        box.addView(top);

        SeekBar seek = new SeekBar(this);
        seek.setMax(max - min);
        seek.setProgress(initial - min);
        seek.setProgressTintList(ColorStateList.valueOf(RED));
        seek.setThumbTintList(ColorStateList.valueOf(Color.WHITE));

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int progress, boolean fromUser) {
                int actual = min + progress;
                val.setText(actual + (unit.isEmpty() ? "" : " " + unit));
                listener.onValue(actual);
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });

        box.addView(seek,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(38)));

        LinearLayout range = new LinearLayout(this);
        TextView lo = label(min + (unit.isEmpty() ? "" : " " + unit),
                10, MUTED, false);
        TextView hi = label(max + (unit.isEmpty() ? "" : " " + unit),
                10, MUTED, false);

        range.addView(lo, new LinearLayout.LayoutParams(0, dp(18), 1f));
        range.addView(hi);
        box.addView(range);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(116));
        lp.topMargin = dp(7);
        parent.addView(box, lp);
    }

    private void toggleTimerAndAnimation() {
        if (timerRunning) {
            if (timer != null) timer.cancel();
            timerRunning = false;
            state.paused = true;
            state.running = true;
            state.notifyChanged();
        } else {
            timerRunning = true;
            state.running = true;
            state.paused = false;
            state.notifyChanged();
            startCountdown();
        }
    }

    private void startCountdown() {
        if (timer != null) timer.cancel();

        timer = new CountDownTimer(remainingMs, 1000) {
            @Override public void onTick(long ms) {
                remainingMs = ms;
                timerText.setText(formatTime(ms));
            }

            @Override public void onFinish() {
                remainingMs = 0;
                timerRunning = false;
                state.running = false;
                state.paused = false;
                state.notifyChanged();
                timerText.setText("00:00");
            }
        }.start();
    }

    private void stopAll() {
        if (timer != null) timer.cancel();
        timerRunning = false;
        state.running = false;
        state.paused = false;
        state.notifyChanged();
        remainingMs = 10 * 60_000L;
        timerText.setText(formatTime(remainingMs));
    }

    private void refreshCourseButtons() {
        for (int i = 0; i < courseButtons.length; i++) {
            if (courseButtons[i] == null) continue;

            boolean active = state.course.ordinal() == i;
            courseButtons[i].setText(active ? "✓ Selected" : "Select");
            courseButtons[i].setTextColor(active ? Color.WHITE : RED);
            courseButtons[i].setBackground(
                    rounded(active ? RED : Color.rgb(255,235,238),
                            12,
                            active ? RED : Color.rgb(255,210,215))
            );
        }
    }

    private void showPresentationIfAvailable() {
        if (displayManager == null || projectorStatus == null) return;

        Display[] displays =
                displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);

        if (displays.length > 0) {
            Display d = displays[0];

            if (presentation == null ||
                    presentation.getDisplay().getDisplayId() != d.getDisplayId()) {

                if (presentation != null) presentation.dismiss();

                presentation = new ProjectionPresentation(this, d, state);
                try {
                    presentation.show();
                } catch (Exception ignored) {
                    presentation = null;
                }
            }

            projectorStatus.setText("●  Projector\nConnected");
            projectorStatus.setTextColor(Color.rgb(0,154,79));
        } else {
            if (presentation != null) {
                presentation.dismiss();
                presentation = null;
            }

            projectorStatus.setText("●  Projector\nNot connected");
            projectorStatus.setTextColor(MUTED);
        }
    }

    @Override public void onDisplayAdded(int displayId) {
        showPresentationIfAvailable();
    }

    @Override public void onDisplayRemoved(int displayId) {
        showPresentationIfAvailable();
    }

    @Override public void onDisplayChanged(int displayId) {
        showPresentationIfAvailable();
    }

    private void saveState() {
        prefs.edit()
                .putInt("course", state.course.ordinal())
                .putFloat("speed", state.speedKmh)
                .putInt("size", state.objectSize)
                .putInt("prob", state.obstacleProbability)
                .putInt("long", state.longitudinalSpacing)
                .putInt("lat", state.lateralSpacing)
                .apply();
    }

    private void restoreState() {
        int c = prefs.getInt("course", 0);

        if (c >= 0 && c < AppState.Course.values().length) {
            state.course = AppState.Course.values()[c];
        }

        state.speedKmh = prefs.getFloat("speed", 18f);
        state.objectSize = prefs.getInt("size", 30);
        state.obstacleProbability = prefs.getInt("prob", 50);
        state.longitudinalSpacing = prefs.getInt("long", 45);
        state.lateralSpacing = prefs.getInt("lat", 40);
    }

    private FrameLayout card() {
        FrameLayout f = new FrameLayout(this);
        f.setBackground(rounded(CARD, 18, BORDER));
        return f;
    }

    private LinearLayout verticalCard() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setBackground(rounded(CARD, 18, BORDER));
        return l;
    }

    private TextView label(String text, int sp, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(sp);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private Button smallButton(String text, boolean primary) {
        Button b = new Button(this);
        b.setAllCaps(false);
        b.setText(text);
        b.setTextSize(13);
        b.setTextColor(primary ? Color.WHITE : RED);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setBackground(rounded(
                primary ? RED : Color.rgb(255,241,243),
                12,
                primary ? RED : Color.rgb(255,210,215)
        ));
        return b;
    }

    private GradientDrawable rounded(int fill, float radiusDp, int border) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill);
        g.setCornerRadius(dp((int)radiusDp));
        if (border != Color.TRANSPARENT) {
            g.setStroke(dp(1), border);
        }
        return g;
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private String formatTime(long ms) {
        long total = Math.max(0, ms) / 1000;
        return String.format(Locale.US, "%02d:%02d", total / 60, total % 60);
    }
}
