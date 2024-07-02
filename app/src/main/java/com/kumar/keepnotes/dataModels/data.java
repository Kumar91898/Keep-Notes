package com.kumar.keepnotes.dataModels;

import java.util.Date;

public class data {
    private String subject;
    private String note;
    private String date;
    private String documentId;
    private String category;

    public data(String subject, String note, String date, String documentId, String category) {
        this.subject = subject;
        this.note = note;
        this.date = date;
        this.category = category;
        this.documentId = documentId;
    }

    public String getCategory() {
        return category;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getSubject() {
        return subject;
    }

    public String getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }
}
