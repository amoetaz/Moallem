package com.moallem.stu.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Session implements Parcelable {

    private String questionType;
    private Boolean isReplyed;
    private Boolean isFinished;
    private Boolean isTeacherOnline;
    private Boolean isStudentOnline;
    private Boolean isStudentReachedZeroMins;
    private String date;
    private String studentName;
    private String teacherName;
    private String studentId;
    private String teacherId;
    private String teacherPic;
    private String firstPic;
    private String firstComment;
    private String key;
    private String storageDataID;

    public Session(){

    }

    protected Session(Parcel in) {
        questionType = in.readString();
        byte tmpIsReplyed = in.readByte();
        isReplyed = tmpIsReplyed == 0 ? null : tmpIsReplyed == 1;
        byte tmpIsFinished = in.readByte();
        isFinished = tmpIsFinished == 0 ? null : tmpIsFinished == 1;
        byte tmpIsTeacherOnline = in.readByte();
        isTeacherOnline = tmpIsTeacherOnline == 0 ? null : tmpIsTeacherOnline == 1;
        byte tmpIsStudentOnline = in.readByte();
        isStudentOnline = tmpIsStudentOnline == 0 ? null : tmpIsStudentOnline == 1;
        byte tmpIsStudentReachedZeroMins = in.readByte();
        isStudentReachedZeroMins = tmpIsStudentReachedZeroMins == 0 ? null : tmpIsStudentReachedZeroMins == 1;
        date = in.readString();
        studentName = in.readString();
        teacherName = in.readString();
        studentId = in.readString();
        teacherId = in.readString();
        teacherPic = in.readString();
        firstPic = in.readString();
        firstComment = in.readString();
        key = in.readString();
        storageDataID = in.readString();
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public void setReplyed(Boolean replyed) {
        isReplyed = replyed;
    }

    public void setFinished(Boolean finished) {
        isFinished = finished;
    }

    public void setTeacherOnline(Boolean teacherOnline) {
        isTeacherOnline = teacherOnline;
    }

    public void setStudentOnline(Boolean studentOnline) {
        isStudentOnline = studentOnline;
    }

    public void setStudentReachedZeroMins(Boolean studentReachedZeroMins) {
        isStudentReachedZeroMins = studentReachedZeroMins;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public void setTeacherPic(String teacherPic) {
        this.teacherPic = teacherPic;
    }

    public void setFirstPic(String firstPic) {
        this.firstPic = firstPic;
    }

    public void setFirstComment(String firstComment) {
        this.firstComment = firstComment;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setStorageDataID(String storageDataID) {
        this.storageDataID = storageDataID;
    }

    public String getQuestionType() {

        return questionType;
    }

    public Boolean getReplyed() {
        return isReplyed;
    }

    public Boolean getFinished() {
        return isFinished;
    }

    public Boolean getTeacherOnline() {
        return isTeacherOnline;
    }

    public Boolean getStudentOnline() {
        return isStudentOnline;
    }

    public Boolean getStudentReachedZeroMins() {
        return isStudentReachedZeroMins;
    }

    public String getDate() {
        return date;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getTeacherPic() {
        return teacherPic;
    }

    public String getFirstPic() {
        return firstPic;
    }

    public String getFirstComment() {
        return firstComment;
    }

    public String getKey() {
        return key;
    }

    public String getStorageDataID() {
        return storageDataID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(questionType);
        dest.writeByte((byte) (isReplyed == null ? 0 : isReplyed ? 1 : 2));
        dest.writeByte((byte) (isFinished == null ? 0 : isFinished ? 1 : 2));
        dest.writeByte((byte) (isTeacherOnline == null ? 0 : isTeacherOnline ? 1 : 2));
        dest.writeByte((byte) (isStudentOnline == null ? 0 : isStudentOnline ? 1 : 2));
        dest.writeByte((byte) (isStudentReachedZeroMins == null ? 0 : isStudentReachedZeroMins ? 1 : 2));
        dest.writeString(date);
        dest.writeString(studentName);
        dest.writeString(teacherName);
        dest.writeString(studentId);
        dest.writeString(teacherId);
        dest.writeString(teacherPic);
        dest.writeString(firstPic);
        dest.writeString(firstComment);
        dest.writeString(key);
        dest.writeString(storageDataID);
    }
}
