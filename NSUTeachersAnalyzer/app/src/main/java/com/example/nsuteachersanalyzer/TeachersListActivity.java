package com.example.nsuteachersanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import constants.IntentExtraKeysConstants;
import database.DatabaseHelper;
import database.Teacher;

public class TeachersListActivity extends AppCompatActivity {

    private final HashMap<TextView, Teacher> teacherTextViewHashMap = new HashMap<>();

    private DatabaseHelper database;

    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_list);
        this.setFullWindow();

        this.database = new DatabaseHelper(this);

        Bundle extras = getIntent().getExtras();

        this.studentId = extras.getInt(IntentExtraKeysConstants.TEACHERS_LIST_ACTIVITY_KEY1);

        this.setButtonsOnClickListeners();
        this.addTextViewsToScrollView();
    }

    private void setButtonsOnClickListeners() {
        ImageButton showMainWindowImageButton = findViewById(R.id.logOut_teachersList_button);
        showMainWindowImageButton.setOnClickListener(this.showMainWindowImageButtonOnClickListener);
    }

    private void setFullWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    private void addTextViewsToScrollView()
    {
        LinearLayout scrollViewLinearLayout = findViewById(R.id.scroll_view_linear_layout);

        ArrayList<Teacher> teacherArrayList = this.database.getTeachersList(getResources().getString(R.string.teacher_job_title));

        for (Teacher teacher: teacherArrayList)
        {
            TextView currTeacherTextView = this.createTextView(teacher);
            this.teacherTextViewHashMap.put(currTeacherTextView, teacher);
            scrollViewLinearLayout.addView(currTeacherTextView);
        }
    }

    private TextView createTextView(Teacher teacher)
    {
        TextView teacherTextView = new TextView(this);
        teacherTextView.setBackgroundResource(R.color.white);
        teacherTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.height = 100;
        params.bottomMargin = 30;
        teacherTextView.setLayoutParams(params);

        teacherTextView.setTextColor(getResources().getColor(R.color.blue));
        teacherTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        teacherTextView.setTypeface(teacherTextView.getTypeface(), Typeface.BOLD);
        teacherTextView.setText(teacher.toString());

        teacherTextView.setClickable(true);
        teacherTextView.setOnClickListener(this.teacherTextViewOnClickListener);

        return teacherTextView;
    }

    private final View.OnClickListener teacherTextViewOnClickListener = view -> {
        Teacher teacher = this.teacherTextViewHashMap.get((TextView) view);
        Intent openCommentsIntent = new Intent(this, StudentCommentsActivity.class);

        int[] ids = new int[] {this.studentId, teacher.getId()};

        openCommentsIntent.putExtra(IntentExtraKeysConstants.TEACHERS_LIST_ACTIVITY_KEY2, ids);
        openCommentsIntent.putExtra(IntentExtraKeysConstants.TEACHERS_LIST_ACTIVITY_KEY3, teacher.toString());

        startActivity(openCommentsIntent);
    };

    private final View.OnClickListener showMainWindowImageButtonOnClickListener = view -> {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.log_out_pop_up_window,null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height=  LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = false;

        final PopupWindow window = new PopupWindow(popupView, width, height, focusable);

        Button confirmButton = popupView.findViewById(R.id.button_accept);
        Button denyButton = popupView.findViewById(R.id.button_deny);

        confirmButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        denyButton.setOnClickListener(view12 -> {
            window.dismiss();
        });

        window.showAtLocation(view, Gravity.CENTER, 0, 0);
    };
}