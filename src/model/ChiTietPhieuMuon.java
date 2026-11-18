package model;

import java.time.LocalDate;

public class ChiTietPhieuMuon {
    private int maPhieuMuon;
    private int maSach;
    private int soLuong;
    private LocalDate ngayTra;
    private String ghiChu;

    /**
     * CONSTRUCTOR 1: Dùng khi THÊM CHI TIẾT PHIẾU MƯỢN MỚI.
     * - Mặc định ngayTra là null (chưa trả sách).
     */
    public ChiTietPhieuMuon(int maPhieuMuon, int maSach, int soLuong, String ghiChu) {
        if (maPhieuMuon <= 0) {
            throw new IllegalArgumentException("Mã phiếu mượn phải lớn hơn 0.");
        }
        if (maSach <= 0) {
            throw new IllegalArgumentException("Mã sách phải lớn hơn 0.");
        }
        this.maPhieuMuon = maPhieuMuon;
        this.maSach = maSach;
        setSoLuong(soLuong);
        setNgayTra(null);
        setGhiChu(ghiChu);
    }

    /**
     * CONSTRUCTOR 2: Dùng khi ĐỌC DỮ LIỆU TỪ DATABASE (DAO).
     * - Có đầy đủ tất cả các cột (bao gồm ngayTra có thể null hoặc đã có ngày).
     */
    public ChiTietPhieuMuon(int maPhieuMuon, int maSach, int soLuong,
                            LocalDate ngayTra, String ghiChu) {
        if (maPhieuMuon <= 0) {
            throw new IllegalArgumentException("Mã phiếu mượn phải lớn hơn 0.");
        }
        if (maSach <= 0) {
            throw new IllegalArgumentException("Mã sách phải lớn hơn 0.");
        }
        this.maPhieuMuon = maPhieuMuon;
        this.maSach = maSach;
        setSoLuong(soLuong);
        setNgayTra(ngayTra);
        setGhiChu(ghiChu);
    }

    // --- GETTERS ---
    public int getMaPhieuMuon() {
        return this.maPhieuMuon;
    }

    public int getMaSach() {
        return this.maSach;
    }

    public int getSoLuong() {
        return this.soLuong;
    }

    public LocalDate getNgayTra() {
        return this.ngayTra;
    }

    public String getGhiChu() {
        return this.ghiChu;
    }

    // --- SETTERS ---
    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng sách mượn phải lớn hơn 0.");
        }
        this.soLuong = soLuong;
    }

    public void setNgayTra(LocalDate ngayTra) {
        // Cho phép null vì cột NgayTra trong DB có Allow Nulls
        if (ngayTra != null && ngayTra.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày trả không được lớn hơn ngày hiện tại (" + LocalDate.now() + ").");
        }
        this.ngayTra = ngayTra;
    }

    public void setGhiChu(String ghiChu) {
        // Cho phép null vì cột GhiChu trong DB có Allow Nulls
        if (ghiChu == null || ghiChu.isBlank()) {
            this.ghiChu = null;
            return;
        }
        String cleanNote = ghiChu.trim();
        if (cleanNote.length() > 255) {
            throw new IllegalArgumentException("Ghi chú không được vượt quá 255 ký tự.");
        }
        this.ghiChu = cleanNote;
    }

    // --- PHƯƠNG THỨC HỖ TRỢ NGHIỆP VỤ ---
    
    /**
     * Kiểm tra đầu sách này trong phiếu đã được trả hay chưa.
     * Giúp tầng DAO hoặc PhieuMuon xác định biến conSachChuaTra dễ dàng.
     */
    public boolean isDaTra() {
        return this.ngayTra != null;
    }

    /**
     * Đánh dấu trả sách vào ngày hiện tại.
     */
    public void traSachHomNay() {
        if (isDaTra()) {
            throw new IllegalStateException("Sách này đã được trả trước đó vào ngày " + this.ngayTra + ".");
        }
        this.ngayTra = LocalDate.now();
    }
}
