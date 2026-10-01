package com.trantrongmanh.lab3.model;

import java.io.Serializable;

/**
 * Model class đại diện cho thông tin sinh viên.
 */
public class Student implements Serializable {

    private int id;
    private String hoTen;
    private String email;
    private String ngaySinh;   // định dạng dd/MM/yyyy
    private String gioiTinh;   // "Nam" hoặc "Nữ"
    private int avatarResId;   // resource ID của ảnh đại diện

    public Student() {}

    public Student(int id, String hoTen, String email,
                   String ngaySinh, String gioiTinh, int avatarResId) {
        this.id = id;
        this.hoTen = hoTen;
        this.email = email;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.avatarResId = avatarResId;
    }

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public int getAvatarResId() { return avatarResId; }
    public void setAvatarResId(int avatarResId) { this.avatarResId = avatarResId; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", hoTen='" + hoTen + "', email='" + email
                + "', ngaySinh='" + ngaySinh + "', gioiTinh='" + gioiTinh + "'}";
    }
}
