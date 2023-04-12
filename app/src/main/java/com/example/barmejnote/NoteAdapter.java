package com.example.barmejnote;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // ثوابت لأنواع مختلفة من طرق عرض الملاحظات
    private static final int TYPE_NOTE = 0;
    private static final int TYPE_NOTE_PHOTO = 1;
    private static final int TYPE_NOTE_CHECK = 2;

    // قائمة الملاحظات المراد عرضها
    private final List<Note> noteList;
    // سياق النشاط
    private final Context context;
    // مستمع لإجراءات الملاحظات مثل الحذف أو التحديث
    private final NoteActionListener noteActionListener;
    // منشئ للمحول
    public NoteAdapter(Context context, List<Note> noteList, NoteActionListener listener) {
        this.noteList = noteList;
        this.context = context;
        this.noteActionListener = listener;
    }

    @NonNull
    @Override
    // ينشئ ويعيد حامل العرض المناسب لنوع العرض المحدد
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        // تضخيم التخطيط المناسب بناءً على نوع العرض
        switch (viewType) {
            case TYPE_NOTE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_note, parent, false);
                return new NoteViewHolder(view);
            case TYPE_NOTE_PHOTO:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_note_photo, parent, false);
                return new NotePhotoViewHolder(view);
            case TYPE_NOTE_CHECK:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_note_check, parent, false);
                return new NoteCheckViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    // يربط البيانات الخاصة بالموضع المحدد بصاحب العرض المناسب
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Note note = noteList.get(position);

        Log.d(TAG, "onBindViewHolder: note = " + note);
        // تحقق مما إذا كان كائن الملاحظة ليس فارغًا
        if (note != null) {
            // عندما يكون نوع حامل العرض هو TYPE_NOTE ، احصل على مثيل NoteViewHolder من الحامل
            switch (holder.getItemViewType()) {
                case TYPE_NOTE:
                    NoteViewHolder noteViewHolder = (NoteViewHolder) holder;
                    noteViewHolder.subtitleTextView.setText(note.getSubTitleNote());
                    setColor(noteViewHolder.linearLayout, note.getBackgroundColor());
                    if (note.getDate() != null) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
                        noteViewHolder.dateTextView.setText(format.format(note.getDate()));
                    }
                    noteItemPopupMenu(noteViewHolder.imageNoteMenu, position, note);
                    timeDateUtils(note,noteViewHolder.dateTimeTextView);
                    break;
                // عندما يكون نوع حامل العرض هو TYPE_NOTE_PHOTO ، احصل على مثيل NotePHOTOViewHolder من الحامل
                case TYPE_NOTE_PHOTO:
                    NotePhotoViewHolder notePhotoViewHolder = (NotePhotoViewHolder) holder;
                    notePhotoViewHolder.titleTextView.setText(note.getSubTitlePhoto());
                    Picasso.get().load(note.getImageUri()).into(notePhotoViewHolder.photoImageView);
                    setColor(notePhotoViewHolder.linearLayout, note.getBackgroundColor());
                    if (note.getDate() != null) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  hh:mm", Locale.getDefault());
                        notePhotoViewHolder.dateTextView.setText(format.format(note.getDate()));
                    }
                    noteItemPopupMenu(notePhotoViewHolder.photoMenuImageView, position, note);
                    timeDateUtils(note,notePhotoViewHolder.dateTimePhotoTextView);
                    break;
                // عندما يكون نوع حامل العرض هو TYPE_NOTE_CHECK ، احصل على مثيل NoteCheckViewHolder من الحامل
                case TYPE_NOTE_CHECK:
                    NoteCheckViewHolder noteCheckViewHolder = (NoteCheckViewHolder) holder;
                    // قم بتعيين عنوان المهمة في الملاحظة إلى عنوان عرض النص
                    noteCheckViewHolder.titleTextView.setText(note.getTitleTask());
                    // اضبط حالة خانة الاختيار في الملاحظة على خانة اختيار المهمة
                    noteCheckViewHolder.taskCheckBox.setChecked(note.isChecked());
                    // أضف مستمعًا إلى خانة اختيار المهمة للاستماع إلى التغييرات في حالتها
                    noteCheckViewHolder.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        // إذا تم تحديد خانة الاختيار
                        if (isChecked) {
                            // قم بإنشاء مربع حوار تنبيه لعرض رسالة تأكيد للمستخدم
                            new AlertDialog.Builder(context)
                                    .setTitle(R.string.completion)
                                    .setMessage(R.string.this_task)
                                    .setPositiveButton(R.string.yes1, (dialog, which) -> {
                                        // إذا نقر المستخدم على "نعم" ، فاستدع طريقة onTaskCheckedChanged () في noteActionListener
                                        noteActionListener.onTaskCheckedChanged(position);
                                        // وتعيين لون خلفية الملاحظة إلى اللون الأخضر ونص عرض النص في العنوان يتوسطه خط
                                        noteCheckViewHolder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.green));
                                        noteCheckViewHolder.titleTextView.setPaintFlags(noteCheckViewHolder.titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    })
                                    // إذا نقر المستخدم على "لا" ، قم بإلغاء تحديد مربع الاختيار
                                    .setNegativeButton(R.string.no1, (dialog, which) -> noteCheckViewHolder.taskCheckBox.setChecked(false))
                                    // إذا نقر المستخدم على "قيد التقدم" ، فاستدع طريقة onTaskCheckedChanged () في noteActionListener
                                    // وتعيين لون خلفية الملاحظة إلى noteColor6 ، ولون نص العنوانTextView إلى اللون الأحمر ، وحجم نص العنوان إلى 20
                                    .setNeutralButton(R.string.in_progress, (dialog, which) -> {
                                        noteCheckViewHolder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.noteColor6));
                                        noteCheckViewHolder.titleTextView.setTextColor(R.color.red);
                                        noteCheckViewHolder.titleTextView.setTextSize(20);
                                        noteActionListener.onTaskCheckedChanged(position);
                                    })
                                    .show();
                        } else {
                            // إذا لم يتم تحديد خانة الاختيار ، فاستدع طريقة onTaskCheckedChanged () في noteActionListener
                            noteActionListener.onTaskCheckedChanged(position);
                            // وقم بإزالة الخط من عنوان عرض النص وضبط لون خلفية الملاحظة على لون خلفية الملاحظة
                            noteCheckViewHolder.titleTextView.setPaintFlags(noteCheckViewHolder.titleTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            setColor(noteCheckViewHolder.linearLayout,note.getBackgroundColor());
                        }
                    });
                    // ضبط لون خلفية الملاحظة على لون خلفية الملاحظة
                    setColor(noteCheckViewHolder.linearLayout, note.getBackgroundColor());
                    // إذا كانت الملاحظة لها تاريخ ، فقم بتعيين تاريخها المنسق على dateTextView
                    if (note.getDate() != null) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
                        noteCheckViewHolder.dateTextView.setText(format.format(note.getDate()));
                    }
                    // قم بتعيين قائمة منبثقة على imageCheckMenu ImageView لإظهار خيارات للمستخدم لتحديث الملاحظة أو حذفها
                    noteItemPopupMenu(noteCheckViewHolder.imageCheckMenu, position, note);
                    timeDateUtils(note, noteCheckViewHolder.dateTimeCheckTextView);

                    break;
            }
        }
    }

    @Override
    // تقوم هذه الطريقة بإرجاع نوع عرض العنصر في المحدد
    public int getItemViewType(int position) {
        // يحدد نوع العرض الذي سيتم تضخيمه بناءً على خصائص كائن الملاحظة.
        Note note = noteList.get(position);
        if (note != null && note.getImageUri() != null) {
            return TYPE_NOTE_PHOTO;
        } else if (note != null && note.getTitleTask() != null
        ) {
            return TYPE_NOTE_CHECK;
        } else {
            return TYPE_NOTE;
        }
    }

    // تحدد هذه الطريقة لون الخلفية لطريقة عرض بناءً على ملف
    public  void setColor(View view, String colorNote) {
        int colorBackground;

        if (colorNote != null) {
            switch (colorNote) {
                case "yellow":
                    colorBackground = ContextCompat.getColor(context, R.color.yellow);
                    break;
                case "red":
                    colorBackground = ContextCompat.getColor(context, R.color.red);
                    break;
                case "blue":
                    colorBackground = ContextCompat.getColor(context, R.color.blue);
                    break;
                default:
                    colorBackground = ContextCompat.getColor(context, R.color.white);
                    break;
            }
            view.setBackgroundColor(colorBackground);
        }
    }



    @Override
    public int getItemCount() {
        return noteList != null ? noteList.size() : 0;
    }




    @SuppressLint("NonConstantResourceId")
    private void noteItemPopupMenu(ImageView noteCheckViewHolder, int position, Note note) {
        noteCheckViewHolder.setOnClickListener(v -> {
            // إنشاء قائمة منبثقة للملاحظة في الموضع الحالي للعنصر
            PopupMenu popupMenu = new PopupMenu(context, v);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.note_popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                // معالجة نقرات عنصر القائمة
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        // استدعاء طريقة onDelete لواجهة noteActionListener عند النقر فوق عنصر القائمة "حذف"
                        noteActionListener.onDelete(position);
                        return true;
                    case R.id.action_update:
                        // استدعاء طريقة onUpdate لواجهة noteActionListener عند النقر فوق عنصر القائمة "تحديث"
                        noteActionListener.onUpdate(position, note);
                        return true;
                    default:
                        return false;
                }
            });
            // إظهار القائمة المنبثقة
            popupMenu.show();
        });
    }


    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView  subtitleTextView, dateTextView, dateTimeTextView;
        private final ImageView imageNoteMenu;
        private final LinearLayout linearLayout;
        private final CardView cardView;

        public NoteViewHolder(View itemView) {
            super(itemView);
            // البحث عن وجهات النظر من التخطيط
            subtitleTextView = itemView.findViewById(R.id.subtitleTextView);
            dateTextView = itemView.findViewById(R.id.dateNoteTextView);
            imageNoteMenu = itemView.findViewById(R.id.image_note_menu);
            linearLayout = itemView.findViewById(R.id.linear_note);
            dateTimeTextView = itemView.findViewById(R.id.dateNoteTextViewTime);
            cardView = itemView.findViewById(R.id.card_view_note);
        }
    }

    public static class NotePhotoViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView, dateTextView, dateTimePhotoTextView;
        private final ImageView photoImageView, photoMenuImageView;
        private final LinearLayout linearLayout;

        public NotePhotoViewHolder(View itemView) {
            super(itemView);
            // البحث عن وجهات النظر من التخطيط
            titleTextView = itemView.findViewById(R.id.titlePhotoTextView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            dateTextView = itemView.findViewById(R.id.datePhotoTextView);
            linearLayout = itemView.findViewById(R.id.linear_photo);
            photoMenuImageView = itemView.findViewById(R.id.image_photo_note_menu);
            dateTimePhotoTextView = itemView.findViewById(R.id.datePhotoTextViewTime);
        }
    }

    public static class NoteCheckViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView, dateTextView, dateTimeCheckTextView;
        private final CheckBox taskCheckBox;
        private final ImageView imageCheckMenu;
        private final LinearLayout linearLayout;

        public NoteCheckViewHolder(View itemView) {
            super(itemView);
            // البحث عن وجهات النظر من التخطيط
            titleTextView = itemView.findViewById(R.id.titleTaskTextView);
            taskCheckBox = itemView.findViewById(R.id.checkBox);
            dateTextView = itemView.findViewById(R.id.dateCheckTextView);
            imageCheckMenu = itemView.findViewById(R.id.image_check_note_menu);
            linearLayout = itemView.findViewById(R.id.linear_check);
            dateTimeCheckTextView = itemView.findViewById(R.id.dateCheckTextViewTime);
        }
    }
    @SuppressLint("DefaultLocale")
    protected void timeDateUtils(Note note , TextView time){
        long now = System.currentTimeMillis();
        long timeElapsed = now - note.getDate().getTime();
        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(note.getDate().getTime(), now, DateUtils.MINUTE_IN_MILLIS);
        time.setText(relativeTime);

    }
    //تحدد هذه الواجهة الإجراءات التي يمكن تنفيذها على ملاحظة في عرض جهاز إعادة التدوير.
    public interface NoteActionListener{
        // تسمى عندما يريد المستخدم حذف ملاحظة. تأخذ موضع الملاحظة في عرض إعادة التدوير.
        void onDelete(int position);
        // يتم الاتصال به عندما يريد المستخدم تحديث ملاحظة. تأخذ موضع الملاحظة في عرض جهاز إعادة التدوير وكائن الملاحظة المحدّث.
        void onUpdate(int position, Note note);
        // يتم استدعاؤها عندما يقوم المستخدم بفحص أو إلغاء تحديد مهمة في NoteCheckViewHolder. تأخذ موضع الملاحظة في عرض إعادة التدوير.
        void onTaskCheckedChanged(int position);
    }
}