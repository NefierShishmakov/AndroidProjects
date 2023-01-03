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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import messages.ErrorMessages;
import constants.IntentExtraKeysConstants;
import constants.ValidationConstants;
import database.CommentsContext;
import database.DatabaseHelper;
import messages.ValidationMessages;
import utils.Utils;

public class StudentCommentsActivity extends AppCompatActivity {

    private static final int STUDENT_ID_INDEX = 0;
    private static final int TEACHER_ID_INDEX = 1;

    private final HashMap<ImageButton, TextView> buttonTextViewHashMap = new HashMap<>();
    private final HashMap<TextView, CommentsContext> textViewStringHashMap = new HashMap<>();

    private DatabaseHelper database;

    private int[] ids = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_comments);

        this.database = new DatabaseHelper(this);

        this.setFullWindow();

        this.setButtonsOnClickListeners();

        Bundle extras = getIntent().getExtras();

        this.ids = extras.getIntArray(IntentExtraKeysConstants.STUDENT_COMMENTS_ACTIVITY_KEY1);
        String teacherFullName = extras.getString(IntentExtraKeysConstants.STUDENT_COMMENTS_ACTIVITY_KEY2);

        this.setTeacherFullNameTextView(teacherFullName);
        this.addExistingComments();
    }

    private void setButtonsOnClickListeners() {
        ImageButton logOutImageButton = findViewById(R.id.logOut_button_comments);
        logOutImageButton.setOnClickListener(this.logOutImageButtonOnCLickListener);

        ImageButton showTeachersListBackButton = findViewById(R.id.show_teachers_list_back_button);
        showTeachersListBackButton.setOnClickListener(this.showTeachersListBackButtonOnClickListener);

        ImageButton addCommentButton = findViewById(R.id.add_comment_button);
        addCommentButton.setOnClickListener(this.getAddCommentImageButtonOnClickListener);
    }

    private void setFullWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    private void setTeacherFullNameTextView(String teacherFullName)
    {
        TextView teacherFullNameTextView = findViewById(R.id.teacher_name_textView);
        teacherFullNameTextView.setText(teacherFullName);
    }

    private void addExistingComments() {
        ArrayList<CommentsContext> teacherComments = this.database.getTeachersCommentsList(this.ids[TEACHER_ID_INDEX]);
        LinearLayout commentsScrollView = findViewById(R.id.scroll_view_linear_layout);

        int studentId = this.ids[STUDENT_ID_INDEX];

        for (CommentsContext commentsContext: teacherComments) {
            if (commentsContext.getOwnerId() == studentId) {
                commentsScrollView.addView(this.getOwnerLinearLayout(commentsContext));
            }
            else
            {
                commentsScrollView.addView(this.getNotOwnerLinearLayout(commentsContext));
            }
        }
    }

    private void setLinearLayoutParams(LinearLayout linearLayout) {
        linearLayout.setWeightSum(10);
        linearLayout.setBackgroundColor(getResources().getColor(R.color.gray));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 60;
        linearLayout.setLayoutParams(params);
    }

    private LinearLayout getNotOwnerLinearLayout(CommentsContext context) {
        LinearLayout linearLayout = new LinearLayout(this);
        this.setLinearLayoutParams(linearLayout);

        TextView resultTextView = this.getCommentTextView(605, 9, context.getComment());
        ImageButton openCommentImageButton = this.getOpenCommentImageButton(1, 70);

        this.textViewStringHashMap.put(resultTextView, context);
        this.buttonTextViewHashMap.put(openCommentImageButton, resultTextView);

        linearLayout.addView(resultTextView);
        linearLayout.addView(openCommentImageButton);

        return linearLayout;
    }

    private LinearLayout getOwnerLinearLayout(CommentsContext context)
    {
        LinearLayout linearLayout = new LinearLayout(this);
        this.setLinearLayoutParams(linearLayout);

        TextView resultTextView = this.getCommentTextView(600, 6, context.getComment());
        ImageButton deleteCommentImageButton = this.getDeleteCommentImageButton();
        ImageButton updateCommentImageButton = this.getUpdateCommentImageButton();
        ImageButton openCommentImageButton = this.getOpenCommentImageButton(2, -1);

        this.textViewStringHashMap.put(resultTextView, context);
        this.buttonTextViewHashMap.put(deleteCommentImageButton, resultTextView);
        this.buttonTextViewHashMap.put(updateCommentImageButton, resultTextView);
        this.buttonTextViewHashMap.put(openCommentImageButton, resultTextView);

        linearLayout.addView(resultTextView);
        linearLayout.addView(deleteCommentImageButton);
        linearLayout.addView(updateCommentImageButton);
        linearLayout.addView(openCommentImageButton);

        return linearLayout;
    }

    private ImageButton getDeleteCommentImageButton() {
        ImageButton imageButton = new ImageButton(this);

        imageButton.setBackgroundColor(Color.TRANSPARENT);
        imageButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.width = 60;
        params.height = 85;
        params.weight = 1;

        imageButton.setLayoutParams(params);
        imageButton.setOnClickListener(this.deleteCommentImageButtonOnClickListener);
        return imageButton;
    }

    private ImageButton getUpdateCommentImageButton() {
        ImageButton imageButton = new ImageButton(this);

        imageButton.setBackgroundColor(Color.TRANSPARENT);
        imageButton.setImageResource(android.R.drawable.ic_menu_edit);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.height = 85;

        imageButton.setLayoutParams(params);
        imageButton.setOnClickListener(this.updateImageButtonOnClickListener);
        return imageButton;
    }

    private ImageButton getOpenCommentImageButton(int weight, int width) {
        ImageButton imageButton = new ImageButton(this);

        imageButton.setBackgroundColor(Color.TRANSPARENT);
        imageButton.setImageResource(android.R.drawable.arrow_down_float);
        imageButton.setRotation(0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        if (width != -1) {
            params.width = width;
        }

        params.height = 90;
        params.weight = weight;
        params.gravity = Gravity.TOP;

        imageButton.setLayoutParams(params);
        imageButton.setOnClickListener(this.getOpenCommentButtonOnClickListener);
        return imageButton;
    }

    private TextView getCommentTextView(int width, int weight, String comment)
    {
        TextView textView = new TextView(this);

        textView.setPadding(20, 0, 0, 0);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

        String resultString = Utils.getPartOfComment(comment);
        textView.setText(resultString);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = width;
        params.weight = weight;

        textView.setLayoutParams(params);

        return textView;
    }

    private final View.OnClickListener getOpenCommentButtonOnClickListener = view -> {
        ImageButton currentImageButton = (ImageButton) view;

        int buttonRotation = (int)currentImageButton.getRotation();
        TextView currentTextView = this.buttonTextViewHashMap.get(currentImageButton);
        String comment = this.textViewStringHashMap.get(currentTextView).getComment();

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

    private final View.OnClickListener getAddCommentImageButtonOnClickListener = view -> {
        LinearLayout scrollLinearLayout = findViewById(R.id.scroll_view_linear_layout);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.add_comment_pop_up_window,null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height =  LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;

        final PopupWindow window = new PopupWindow(popupView, width, height, focusable);

        Button confirmButton = popupView.findViewById(R.id.button_add_comment);
        Button denyButton = popupView.findViewById(R.id.button_not_add_comment);

        EditText newCommentEditText = popupView.findViewById(R.id.comment_editText);
        TextView errorTextView = popupView.findViewById(R.id.error_comment_textView);

        confirmButton.setOnClickListener(view1 -> {
            String newComment = newCommentEditText.getText().toString();

            if (newComment.length() < ValidationConstants.MIN_COMMENT_LENGTH) {
                Utils.setErrorText(errorTextView, Utils.getFullMessage(ErrorMessages.NOT_CORRECT_COMMENT_ERROR_TEXT,
                        ValidationMessages.COMMENT_VALIDATION_MESSAGE));
                return;
            }

            this.database.addComment(newComment, this.ids[STUDENT_ID_INDEX], this.ids[TEACHER_ID_INDEX]);
            CommentsContext commentsContext = new CommentsContext(this.database.getMaxCommentsId(),
                    this.ids[STUDENT_ID_INDEX], newComment);

            scrollLinearLayout.addView(this.getOwnerLinearLayout(commentsContext));

            window.dismiss();
        });

        denyButton.setOnClickListener(view12 -> {
            window.dismiss();
        });

        window.showAtLocation(view, Gravity.CENTER, 0, 0);
    };

    private final View.OnClickListener updateImageButtonOnClickListener = view -> {
        ImageButton updateImageButton = (ImageButton) view;

        TextView currTextView = this.buttonTextViewHashMap.get(updateImageButton);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.update_comment_pop_up_window,null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height=  LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;

        final PopupWindow window = new PopupWindow(popupView, width, height, focusable);

        CommentsContext commentsContext = this.textViewStringHashMap.get(currTextView);

        EditText newComment = popupView.findViewById(R.id.comment_editText);
        newComment.setText(commentsContext.getComment());

        TextView errorTextView = popupView.findViewById(R.id.error_update_comment_textView);

        Button confirmButton = popupView.findViewById(R.id.button_update_comment);
        Button denyButton = popupView.findViewById(R.id.button_not_update_comment);

        confirmButton.setOnClickListener(view1 -> {
            String newCommentStr = newComment.getText().toString();

            if (newCommentStr.length() < ValidationConstants.MIN_COMMENT_LENGTH) {
                Utils.setErrorText(errorTextView, Utils.getFullMessage(ErrorMessages.NOT_CORRECT_COMMENT_ERROR_TEXT,
                        ValidationMessages.COMMENT_VALIDATION_MESSAGE));
                return;
            }

            this.database.updateComment(commentsContext.getId(), newCommentStr);

            commentsContext.setComment(newCommentStr);
            String part = Utils.getPartOfComment(newCommentStr);
            currTextView.setText(part);

            window.dismiss();
        });

        denyButton.setOnClickListener(view12 -> {
            window.dismiss();
        });

        window.showAtLocation(view, Gravity.CENTER, 0, 0);
    };

    private final View.OnClickListener deleteCommentImageButtonOnClickListener = view -> {
        ImageButton deleteImageButton = (ImageButton) view;
        TextView currTextView = this.buttonTextViewHashMap.get(deleteImageButton);

        LinearLayout parentLinearLayout = (LinearLayout) deleteImageButton.getParent();

        LinearLayout scrollLinearLayout = findViewById(R.id.scroll_view_linear_layout);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.delete_comment_pop_up_window,null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height=  LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = false;

        final PopupWindow window = new PopupWindow(popupView, width, height, focusable);

        Button confirmButton = popupView.findViewById(R.id.button_accept_delete);
        Button denyButton = popupView.findViewById(R.id.button_deny_delete);

        confirmButton.setOnClickListener(view1 -> {
            this.database.deleteComment(this.textViewStringHashMap.get(currTextView).getId());
            scrollLinearLayout.removeView(parentLinearLayout);
            window.dismiss();
        });

        denyButton.setOnClickListener(view12 -> {
            window.dismiss();
        });

        window.showAtLocation(view, Gravity.CENTER, 0, 0);
    };

    private final View.OnClickListener showTeachersListBackButtonOnClickListener = view -> {
          Intent intent = new Intent(this, TeachersListActivity.class);
          intent.putExtra(IntentExtraKeysConstants.STUDENT_COMMENTS_ACTIVITY_KEY3, this.ids[STUDENT_ID_INDEX]);
          startActivity(intent);
    };

    private final View.OnClickListener logOutImageButtonOnCLickListener = view -> {
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