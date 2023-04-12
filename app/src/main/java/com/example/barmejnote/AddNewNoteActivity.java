package com.example.barmejnote;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class AddNewNoteActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST_PERMISSIONS = 2;

    private RadioButton blueRadioButton, redRadioButton,yellowRadioButton;
    private  RadioButton photoRadioButton;
    private RadioButton noteRadioButton;
    private RadioButton checkRadioButton;
    private RadioGroup mNoteRadioGroup, mColorRadioGroup;
    private CardView photoCardView, noteCardView, checkCardView;
    private EditText photoNoteEditText, noteEditText, checkNoteEditText;

    private ImageView photoImageView;
    String colorBackground;
    private String noteImagePath;
    
    private  Note existingNote;
    int position;

    private Button saveButton ,updateButton;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        // Find views
        intoInitialize();

        // Set listeners for radio buttons
        radioButtonListener();

        handleExistingNote();

        updateButton.setOnClickListener(v -> updateNote());

        saveButton.setOnClickListener(view -> saveNote());
    }

    private void handleExistingNote() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("note_key")) {
            // تتوفر بيانات المذكرة الحالية ، قم بملء حقول النموذج مسبقًا بالبيانات
            existingNote = (Note) intent.getSerializableExtra("note_key");
            position = intent.getIntExtra("position", -1);

            // تعيين زر الاختيار لنوع الملاحظة الحالية
            switch (existingNote.getType()) {
                case "TYPE_NOTE":
                    noteRadioButton.setChecked(true);
                    noteEditText.setText(existingNote.getSubTitleNote());
                    break;
                case "TYPE_NOTE_CHECK":
                    checkRadioButton.setChecked(true);
                    checkNoteEditText.setText(existingNote.getTitleTask());
                    break;
                case "TYPE_PHOTO_NOTE":
                    photoRadioButton.setChecked(true);
                    photoNoteEditText.setText(existingNote.getSubTitlePhoto());
                    photoImageView.setImageURI(Uri.parse(existingNote.getImageUri()));
                    break;
            }

            // تعيين زر الاختيار لنوع الملاحظة الحالية
            switch (existingNote.getBackgroundColor()) {
                case "blue":
                    blueRadioButton.setChecked(true);
                    break;
                case "yellow":
                    yellowRadioButton.setChecked(true);
                    break;
                case "red":
                    redRadioButton.setChecked(true);
                    break;
            }
            // تظهر زر التحديث وتختفي زر الحفظ
            updateButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
        }else {
            // يقوم بإخفاء زر التحديث وإظهار زر الحفظ
            updateButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
        }
    }


    // مستمع لأزرار الراديو
    private void radioButtonListener() {

        // إعداد مستمع مجموعة ملاحظة الراديو
        mColorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    // إذا تم تحديد زر الاختيار الأزرق ، فاضبط لون الخلفية على "الأزرق"
                    case R.id.radioButtonBlue:
                        colorBackground = "blue";
                        break;
                    // إذا تم تحديد زر الاختيار الأصفر ، فاضبط لون الخلفية على "أصفر"
                    case R.id.radioButtonYellow:
                        colorBackground = "yellow";
                        break;
                    // إذا تم تحديد زر الاختيار الأحمر ، فاضبط لون الخلفية على "أحمر"
                    case R.id.radioButtonRed:
                        colorBackground = "red";
                        break;
                }
            }
        });
        // إعداد مستمع مجموعة ملاحظة الراديو
        mNoteRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonNote:
                        // إذا تم تحديد زر راديو الملاحظة ، فقم بإظهار شاشة ادخال الملاحظة وإخفاء الآخرين
                        photoCardView.setVisibility(View.GONE);
                        noteCardView.setVisibility(View.VISIBLE);
                        checkCardView.setVisibility(View.GONE);
                        break;
                    case R.id.radioButtonCheck:
                        // إذا تم تحديد زر الراديو في خانة الاختيار ، فقم بإظهار ادخال المهام وإخفاء الآخرين
                        photoCardView.setVisibility(View.GONE);
                        noteCardView.setVisibility(View.GONE);
                        checkCardView.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radioButtonPhoto:
                        // إذا تم تحديد زر راديو للصورة ، فقم بإظهار  شاشة ادخال ملاحظة الصور وإخفاء الآخرين
                        photoCardView.setVisibility(View.VISIBLE);
                        noteCardView.setVisibility(View.GONE);
                        checkCardView.setVisibility(View.GONE);
                        createPhotoNote(); // Call createPhotoNote() method
                        break;
                }
            }
        });

    }

    // طريقة إستدعاء و تهيئة جميع عناصر واجهة إضافة الملاحظات
    private void intoInitialize() {
        photoRadioButton = findViewById(R.id.radioButtonPhoto);
        noteRadioButton = findViewById(R.id.radioButtonNote);
        checkRadioButton = findViewById(R.id.radioButtonCheck);
        photoCardView = findViewById(R.id.cardViewPhoto);
        noteCardView = findViewById(R.id.cardViewNote);
        checkCardView = findViewById(R.id.cardViewCheckNote);
        photoNoteEditText = findViewById(R.id.photoNoteEditText);
        noteEditText = findViewById(R.id.noteEditText);
        checkNoteEditText = findViewById(R.id.checkNoteEditText);
        photoImageView = findViewById(R.id.photoImageView);
        blueRadioButton = findViewById(R.id.radioButtonBlue);
        yellowRadioButton = findViewById(R.id.radioButtonYellow);
        redRadioButton = findViewById(R.id.radioButtonRed);
        saveButton = findViewById(R.id.button_submit);
        updateButton = findViewById(R.id.button_update);
        mNoteRadioGroup = findViewById(R.id.typeRadioGroup);
        mColorRadioGroup = findViewById(R.id.colorRadioGroup);
    }

    // هذه الدالة يتأكد من أن الصلاحية المطلوبة
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_IMAGE_REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // تم منح الإذن ، افتح معرض الصور
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            } else {
                // تم رفض الإذن ، أظهر رسالة للمستخدم
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }

    }

    // هذه الدالة تنفيذ الإجراء المناسب عند الحصول على الرد من النشاط الذي تم افتتاحه لاختيار الصورة
    // تستخدم هذه الدالة لاختيار الصورة وعرضها على الشاشة
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // استرداد معرف URI للصورة المختارة
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            noteImagePath = imageUri.toString(); // تخزين عنوان URI للصورة كسلسلة
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                photoImageView.setImageBitmap(bitmap); // اضبط الصورة البتماب  "النقطية" على طريقة عرض الصورة
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.failed_load_image, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // هذه الدالة تنفيذ الإجراء المناسب عند النقر على الصورة لاختيارها
    protected void createPhotoNote(){
        photoImageView.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(AddNewNoteActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // طلب الإذن للوصول إلى ملفات التخزين الخارجي إذا كان غير ممنوحًا بعد طلب الإذن
                ActivityCompat.requestPermissions(AddNewNoteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PICK_IMAGE_REQUEST_PERMISSIONS);
            } else {
                // افتح النشاط لاختيار الصورة إذا تم منح الإذن بالوصول إلى ملفات التخزين الخارجي
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

    }

    // إنشاء الملاحظة الجديدة وحفظها
    private void saveNote() {
        // إنشاء مُعرف فريد للملاحظة الجديدة
        String uniqueId = UUID.randomUUID().toString();
        Note note = null;

        String  noteType;
        if (photoRadioButton.isChecked()) {
            // جلب نص الملاحظة ونوعها في حال كانت ملاحظة صورة
            String notePhotoText = photoNoteEditText.getText().toString();
            noteType = "TYPE_PHOTO_NOTE";
            // يتم تحقق اذا كان النص غير فارغ
            if (!TextUtils.isEmpty(notePhotoText) && !TextUtils.isEmpty(noteImagePath)) {
                // إنشاء الملاحظة الجديدة
                note = new Note(uniqueId, notePhotoText, noteImagePath, new Date(), colorBackground, noteType);
            }else{
                //  اذا كان النص فارغ عرض رسالة للمستخدم
                Toast.makeText(this, R.string.enter_title_photo, Toast.LENGTH_SHORT).show();
            }
        } else if (noteRadioButton.isChecked()) {
            // جلب نص الملاحظة ونوعها في حال كانت ملاحظة نصية
            String noteText = noteEditText.getText().toString();
            // يتم تحقق اذا كان النص غير فارغ
            if (!TextUtils.isEmpty(noteText)) {
                noteType = "TYPE_NOTE";
                // إنشاء الملاحظة الجديدة
                note = new Note(uniqueId, noteText, new Date(), colorBackground, noteType);
            }else {
                //  اذا كان النص فارغ عرض رسالة للمستخدم
                Toast.makeText(this, R.string.enter_note_please, Toast.LENGTH_SHORT).show();
            }
        } else if (checkRadioButton.isChecked()) {
            // جلب نص الملاحظة ونوعها في حال كانت ملاحظة مهام
            String noteCheckText = checkNoteEditText.getText().toString();
            noteType = "TYPE_NOTE_CHECK";
            // يتم تحقق اذا كان النص غير فارغ
            if (!TextUtils.isEmpty(noteCheckText)) {
                // إنشاء الملاحظة الجديدة
                note = new Note(uniqueId, noteCheckText, false, new Date(), colorBackground, noteType);
            }else {
                //  اذا كان النص فارغ عرض رسالة للمستخدم
                Toast.makeText(this, R.string.enter_title_task, Toast.LENGTH_SHORT).show();
            }
        }

        // إرسال الملاحظة الجديدة إلى الشاشة الرائسية او السابقة
        Intent intent = new Intent();
        intent.putExtra("note_key", note);

        setResult(RESULT_OK ,intent);
        Toast.makeText(this, R.string.note_has_been_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    public void updateNote() {
        String photoNote = photoNoteEditText.getText().toString();
        String noteTitle = noteEditText.getText().toString();
        String checkNote = checkNoteEditText.getText().toString();

        Note updateNote = new Note(existingNote.getId(), existingNote.getType());

        switch (existingNote.getType()) {
            case "TYPE_PHOTO_NOTE":
                // يتم تحقق اذا كان النص غير فارغ
                if (!TextUtils.isEmpty(photoNote)) {
                    // يتم تحديث الصورة و العنوان ملاحظة
                    updateNote.setSubTitlePhoto(photoNote);
                    updateNote.setImageUri(noteImagePath);
                } else {
                    //  اذا كان النص فارغ عرض رسالة للمستخدم
                    Toast.makeText(this, R.string.enter_title_photo, Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case "TYPE_NOTE":
                // يتم تحقق اذا كان النص غير فارغ
                if (!TextUtils.isEmpty(noteTitle)) {
                    // يتم تحديث العنوان ملاحظة
                    updateNote.setSubTitleNote(noteTitle);
                } else {
                    //  اذا كان النص فارغ عرض رسالة للمستخدم
                    Toast.makeText(this, R.string.enter_note_please, Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case "TYPE_NOTE_CHECK":
                // يتم تحقق اذا كان النص غير فارغ
                if (!TextUtils.isEmpty(checkNote)) {
                    // يتم تحديث  العنوان المهام
                    updateNote.setTitleTask(checkNote);
                    updateNote.setChecked(false);
                } else {
                    //  اذا كان النص فارغ عرض رسالة للمستخدم
                    Toast.makeText(this, R.string.enter_title_task, Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }
        // تحديث التاريخ ولون الخلفية
        updateNote.setDate(new Date());
        updateNote.setBackgroundColor(colorBackground);

        // إعداد النتيجة والانتهاء من النشاط
        Intent resultIntent = new Intent();
        resultIntent.putExtra("note_key", updateNote);
        resultIntent.putExtra("position", position);
        setResult(RESULT_OK, resultIntent);
        Toast.makeText(this, R.string.updated_successfully, Toast.LENGTH_SHORT).show();
        finish();
    }



}