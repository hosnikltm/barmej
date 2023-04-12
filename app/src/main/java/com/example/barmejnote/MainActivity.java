package com.example.barmejnote;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NoteAdapter.NoteActionListener{

    // تعريف الثوابت
    private static final int ADD_NOTE_REQUEST_CODE = 1;
    private static final int UPDATE_NOTE_REQUEST = 111;

    // تعريف المتغيرات والعناصر المستخدمة في الواجهة
    private List<Note> noteList;
    private RecyclerView noteRecyclerView;
    private NoteAdapter noteAdapter;
    private Menu listMenu;
    private SharedPreferences sharedPreferences;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        // اضغط على زر الإضافة لفتح شاشة إضافة ملاحظة جديد
        findViewById(R.id.addNoteFab).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNewNoteActivity.class);
            startActivityForResult(intent, ADD_NOTE_REQUEST_CODE);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initializeViews() {
        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);

        // العثور على العناصر في الواجهة
        noteRecyclerView = findViewById(R.id.noteRecyclerView);
        noteRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // تهيئة القائمة والمحول

        //يقوم هذا الجزء بتحميل الملاحظات المحفوظة من SharedPreferences باستخدام الدالة "loadNotes()" وتعيينها إلى قائمة الملاحظات
        noteList = loadNotes();
        noteAdapter = new NoteAdapter(this, noteList,this);
        noteRecyclerView.setAdapter(noteAdapter);
        noteAdapter.notifyDataSetChanged();
    }

    @Override
    // خلق القائمة في الشريط العلوي
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        listMenu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    // استجابة للاختيارات في القائمة
    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.list_menu_id:
                // تعيين مدير تخطيط القائمة كقائمة عمودية
                noteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                // إخفاء العنصر المحدد
               item.setVisible(false);
                // جعل العنصر الآخر مرئيًا
               listMenu.findItem(R.id.grid_menu_id).setVisible(true);
                return true;case
                    R.id.grid_menu_id:
                // تعيين مدير تخطيط القائمة كشبكة من عدة أعمدة
                noteRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
                // إخفاء العنصر المحدد
               item.setVisible(false);
                // جعل العنصر الآخر مرئيًا
               listMenu.findItem(R.id.list_menu_id).setVisible(true);
                return true;
            case R.id.grid_remove_all:
                noteList.clear();
                saveNotes();
                noteAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    // تقوم هذه الدالة بإدارة نتائج استجابة طلب إضافة ملاحظة جديدة أو تحديث ملاحظة موجودة.
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST_CODE && resultCode == RESULT_OK) {

            Note note = (Note) Objects.requireNonNull(data).getSerializableExtra("note_key");
            // أضف ملاحظة جديدة إلى القائمة وقم بتحديث المحول
            noteList.add(note);
            noteAdapter.notifyDataSetChanged();
            saveNotes();
        } else if (requestCode == UPDATE_NOTE_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("note_key")) {
                // تحديث الملاحظات الموجودة في القائمة وتحديث المحول
                Note note = (Note) Objects.requireNonNull(data).getSerializableExtra("note_key");
                int position = data.getIntExtra("position", -1);
                if (position != -1) {
                    noteList.set(position, note);
                    noteAdapter.notifyItemChanged(position);
                    saveNotes();

                } else {

                    Log.e("MainActivity", "Failed to update note: position is invalid");
                }
            } else {

                Log.e("MainActivity", "Failed to update note: no data received");

            }
        } else {

            Log.e("MainActivity", "Unhandled result from activity: requestCode = " + requestCode + ", resultCode = " + resultCode);

        }
    }


    @Override
    public void onDelete(int position) {
        if (position >=0 && position < noteList.size()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("تأكيد الحذف");
            builder.setMessage("هل أنت متأكد من أنك تريد حذف هذه الملاحظة؟");
            builder.setPositiveButton("نعم", (dialogInterface, i) -> {
                noteList.remove(position);
                noteAdapter.notifyItemRemoved(position);
                saveNotes();
                dialogInterface.dismiss();
            });
            builder.setNegativeButton("لا", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    @Override
    public void onUpdate(int position, Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تعديل الملاحظة");
        builder.setMessage("هل أنت متأكد من رغبتك في تعديل الملاحظة؟");

        builder.setPositiveButton("نعم", (dialog, which) -> {
            Intent intent = new Intent(MainActivity.this,AddNewNoteActivity.class);
            intent.putExtra("note_key", note);
            intent.putExtra("position", position);
            startActivityForResult(intent, UPDATE_NOTE_REQUEST);
        });

        builder.setNegativeButton("لا", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onTaskCheckedChanged(int position) {
        //  عندما يتم تغيير حالة المهمة المرتبطة بالموقع position في قائمة الملاحظات
        if (noteList.get(position).isChecked()) {
            Toast.makeText(this, "تم اكتمال المهمة", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "تم إلغاء اكتمال المهمة", Toast.LENGTH_SHORT).show();
        }
    }
    // دالة لحفظ الملاحظات في SharedPreferences
    private void saveNotes() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(noteList);
        editor.putString("notes", json);
        editor.apply();
    }

    // دالة لاسترداد الملاحظات من SharedPreferences
    private ArrayList<Note> loadNotes() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("notes", null);
        Type type = new TypeToken<ArrayList<Note>>() {}.getType();
        ArrayList<Note> notes = gson.fromJson(json, type);

        if (notes == null) {
            notes = new ArrayList<>();
        }

        return notes;
    }
}


