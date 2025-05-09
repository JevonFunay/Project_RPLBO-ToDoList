package org.example.project_rplbo;

public class Task {
    private String judul;
    private String status;
    private String isi;
    private String tenggat;

    public Task(String judul, String status, String isi, String tenggat) {
        this.judul = judul;
        this.status = status;
        this.isi = isi;
        this.tenggat = tenggat;
    }

    public String getJudul() {
        return judul;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsi() { return isi ; }
    public String getTenggat() { return tenggat;}
}