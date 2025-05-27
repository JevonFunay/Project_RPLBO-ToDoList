package org.example.project_rplbo;

public class Task {
    private int id;
    private String judul;
    private String status;
    private String isi;
    private String tenggat;
    private String kategori;

    public Task(String judul, String status, String isi, String tenggat, String kategori) {
        this.judul = judul;
        this.status = status;
        this.isi = isi;
        this.tenggat = tenggat;
        this.kategori = kategori;
    }

    public Task(int id, String judul, String status, String isi, String tenggat, String kategori) {
        this.id = id;
        this.judul = judul;
        this.status = status;
        this.isi = isi;
        this.tenggat = tenggat;
        this.kategori = kategori;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public int getId() {
        return id;
    }

    public String getJudul() {
        return judul;
    }

    public String getIsi() { return isi ; }

    public String getStatus() {
        return status;
    }

    public String getTenggat() { return tenggat;}

    public void setId() {
        this.id = id;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTenggat(String tenggat) {
        this.tenggat = tenggat;
    }
}