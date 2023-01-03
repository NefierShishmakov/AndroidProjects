package com.example.nsuteachersanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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
import database.CommentsContext;
import database.DatabaseHelper;
import utils.Utils;

public class TeacherCommentsActivity extends AppCompatActivity {

    private final HashMap<ImageButton, TextView> buttonTextViewHashMap = new HashMap<>();
    private final HashMap<TextView, CommentsContext> textViewStringHashMap = new HashMap<>();

    private DatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_comments);

        this.database = new DatabaseHelper(this);

        this.setFullWindow();

        this.setButtonsOnClickListeners();

        Bundle extras = getIntent().getExtras();
        int teacherId = extras.getInt(IntentExtraKeysConstants.TEACHER_COMMENTS_ACTIVITY_KEY1);

        this.addComments(teacherId);
    }

    private void addComments(int teacherId) {
        LinearLayout scrollLinearLayout = findViewById(R.id.teacher_comments_linear_layout);

        ArrayList<CommentsContext> teacherComments = this.database.getTeachersCommentsList(teacherId);

        for (CommentsContext context: teacherComments)
        {
            scrollLinearLayout.addView(this.getLinearLayoutForComment(context));
        }
    }

    private void setButtonsOnClickListeners() {
        ImageButton logOutButton = findViewById(R.id.logOut_teacherComments_button);
        logOutButton.setOnClickListener(this.logOutButtonOnClickListener);
    }

    private void setFullWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    private void setLinearLayoutParams(LinearLayout linearLayout) {
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackgroundColor(getResources().getColor(R.color.gray));
        linearLayout.setWeightSum(10);

        LinearLayout.LayoutParams mainLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mainLayoutParams.bottomMargin = 60;

        linearLayout.setLayoutParams(mainLayoutParams);
    }

    private LinearLayout getLinearLayoutForComment(CommentsContext commentsContext) {
        LinearLayout mainLayout = new LinearLayout(this);
        this.setLinearLayoutParams(mainLayout);

        TextView commentTextView = this.getTextViewForComment(commentsContext.getComment());
        ImageButton commentImageButton = this.getImageButtonForComment();

        this.buttonTextViewHashMap.put(commentImageButton, commentTextView);
        this.textViewStringHashMap.put(commentTextView, commentsContext);

        mainLayout.addView(commentTextView);
        mainLayout.addView(commentImageButton);

        return mainLayout;
    }

    private ImageButton getImageButtonForComment() {
        ImageButton imageButton = new ImageButton(this);

        imageButton.setBackgroundColor(Color.TRANSPARENT);
        imageButton.setImageResource(android.R.drawable.arrow_down_float);
        imageButton.setRotation(0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.width = 70;
        params.height = 90;
        params.weight = 1;
        params.gravity = Gravity.TOP;

        imageButton.setLayoutParams(params);
        imageButton.setOnClickListener(this.getOpenCommentImageButton);

        return imageButton;
    }

    private TextView getTextViewForComment(String fullComment) {
        TextView textView = new TextView(this);

        textView.setPadding(20, 0, 0, 0);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

        String resultString = Utils.getPartOfComment(fullComment);
        textView.setText(resultString);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = 605;
        params.weight = 9;

        textView.setLayoutParams(params);

        return textView;
    }

    private final View.OnClickListener getOpenCommentImageButton = view -> {
        ImageButton currentImageButton = (ImageButton) view;

        int buttonRotation = (int)currentImageButton.getRotation();
        TextView currentTextView = buttonTextViewHashMap.get(currentImageButton);
        String comment = textViewStringHashMap.get(currentTextView).getComment();

        switch(buttonRotation)
        {
            case 0:
                currentTextView.setText(comment);
                currentImageButton.setRotation(90);
                break;
            case 90:
                String partOfComment = Utils.getPartOfComment(comment);
                currentTextView.setText(partOfComment);
                currentImageButton.setRotation(0);
                break;
        }
    };

    private final View.OnClickListener logOutButtonOnClickListener = view -> {
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