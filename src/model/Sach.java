package model;

import java.time.LocalDate;

public class Sach {
    private int maSach;
    private String tenSach;
    private String tacGia;
    private String nhaXuatBan;
    private int namXuatBan;
    private int soLuong;
    private int maTheLoai;

    public Sach(String tenSach, String tacGia, String nhaXuatBan,
                int namXuatBan, int soLuong, int maTheLoai) {
        setTenSach(tenSach);
        setTacGia(tacGia);
        setNhaXuatBan(nhaXuatBan);
        setNamXuatBan(namXuatBan);
        setSoLuong(soLuong);
        setMaTheLoai(maTheLoai);
    }

    public Sach(int maSach, String tenSach, String tacGia, String nhaXuatBan,
                int namXuatBan, int soLuong, int maTheLoai) {
        if (maSach <= 0) {
            throw new IllegalArgumentException("Mã sách phải lớn hơn 0.");
        }
        this.maSach = maSach;
        setTenSach(tenSach);
        setTacGia(tacGia);
        setNhaXuatBan(nhaXuatBan);
        setNamXuatBan(namXuatBan);
        setSoLuong(soLuong);
        setMaTheLoai(maTheLoai);
    }

    // --- GETTERS ---
    public int getMaSach() {
        return this.maSach;
    }

    public String getTenSach() {
        return this.tenSach;
    }

    public String getTacGia() {
        return this.tacGia;
    }

    public String getNhaXuatBan() {
        return this.nhaXuatBan;
    }

    public int getNamXuatBan() {
        return this.namXuatBan;
    }

    public int getSoLuong() {
        return this.soLuong;
    }

    public int getMaTheLoai() {
        return this.maTheLoai;
    }

    // --- SETTERS ---
    public void setTenSach(String tenSach) {
        if (tenSach == null || tenSach.isBlank()) {
            throw new IllegalArgumentException("Tên sách không được để trống.");
        }
        this.tenSach = tenSach.trim();
    }

    public void setTacGia(String tacGia) {
        if (tacGia == null || tacGia.isBlank()) {
            throw new IllegalArgumentException("Tác giả không được để trống.");
        }
        this.tacGia = tacGia.trim();
    }

    public void setNhaXuatBan(String nhaXuatBan) {
        if (nhaXuatBan == null || nhaXuatBan.isBlank()) {
            throw new IllegalArgumentException("Nhà xuất bản không được để trống.");
        }
        this.nhaXuatBan = nhaXuatBan.trim();
    }

    public void setNamXuatBan(int namXuatBan) {
        int namHienTai = LocalDate.now().getYear();
        if (namXuatBan <= 0 || namXuatBan > namHienTai) {
            throw new IllegalArgumentException("Năm xuất bản phải từ 1 đến " + namHienTai + ".");
        }
        this.namXuatBan = namXuatBan;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong < 0) {
            throw new IllegalArgumentException("Số lượng sách không được nhỏ hơn 0.");
        }
        this.soLuong = soLuong;
    }

    public void setMaTheLoai(int maTheLoai) {
        if (maTheLoai <= 0) {
            throw new IllegalArgumentException("Mã thể loại phải lớn hơn 0.");
        }
        this.maTheLoai = maTheLoai;
    }
}