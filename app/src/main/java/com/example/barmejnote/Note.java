package com.example.barmejnote;

import java.io.Serializable;
import java.util.Date;

// Serializable هي واجهة علامة في Java تشير إلى أن كائنات الفئة التي تنفذها يمكن تسلسلها أو تحويلها إلى دفق بايت للإرسال عبر شبكة أو للتخزين المستمر.
public class Note implements Serializable {
   private String id ,titleTask, subTitleNote,subTitlePhoto, imageUri, backgroundColor,  type;
    private Date date;
    private boolean isChecked;

    public Note(){

    }
    // منشئ للملاحظة النصية
    public Note (String id, String subTitle, Date date, String backgroundColor, String type){
        this.id = id;
        this.subTitleNote = subTitle;
        this.date = date;
        this.backgroundColor = backgroundColor;
        this.type = type;
    }
    // منشئ للملاحظة الصور
    public Note (String id,String subTitle, String imageUri, Date date , String backgroundColor, String type){
        this.id = id;
        this.subTitlePhoto = subTitle;
        this.imageUri = imageUri;
        this.date = date;
        this.backgroundColor = backgroundColor;
        this.type = type;
    }
    // منشئ للملاحظة المهام
    public Note(String id, String taskTitle, boolean isChecked,Date date, String backgroundColor, String type){
        this.id = id;
        this.titleTask = taskTitle;
        this.isChecked = isChecked;
        this.date = date;
        this.backgroundColor = backgroundColor;
        this.type = type;
    }

    public Note(String id, String type) {
        this.id = id;
        this.type = type;
    }

    // Getters و setters
    //هي طرق تستخدم للوصول إلى قيم الحقول الخاصة (متغيرات المثيل)
    // وتعديلها في فئة. إنها تسمح لنا بتوفير طريقة خاضعة للرقابة للوصول إلى البيانات المخزنة في وتعديلها

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubTitleNote() {
        return subTitleNote;
    }

    public void setSubTitleNote(String subTitleNote) {
        this.subTitleNote = subTitleNote;
    }

    public String getSubTitlePhoto() {
        return subTitlePhoto;
    }

    public void setSubTitlePhoto(String subTitlePhoto) {
        this.subTitlePhoto = subTitlePhoto;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getTitleTask() {
        return titleTask;
    }

    public void setTitleTask(String titleTask) {
        this.titleTask = titleTask;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
