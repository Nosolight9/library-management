package model;

import java.time.LocalDate;

public class PhieuMuon {
    // Các hằng số trạng thái chuẩn
    public static final String TRANG_THAI_DANG_MUON = "Đang mượn";
    public static final String TRANG_THAI_QUA_HAN = "Quá hạn";
    public static final String TRANG_THAI_DA_TRA = "Đã trả";

    private int maPhieuMuon;
    private int maDocGia;
    private LocalDate ngayMuon;
    private LocalDate hanTra;
    private String trangThai;

    /**
     * CONSTRUCTOR 1: Dùng khi TẠO PHIẾU MƯỢN MỚI (SQL Server tự tăng maPhieuMuon).
     */
    public PhieuMuon(int maDocGia, LocalDate ngayMuon, LocalDate hanTra) {
        if (maDocGia <= 0) {
            throw new IllegalArgumentException("Mã độc giả phải lớn hơn 0.");
        }
        this.maDocGia = maDocGia;
        setNgayMuon(ngayMuon);
        setHanTraMoi(hanTra);
        kiemTraTrangThai(true); // Mặc định phiếu mới còn sách chưa trả
    }

    /**
     * CONSTRUCTOR 2: Dùng khi ĐỌC DỮ LIỆU TỪ DATABASE (DAO).
     * Tham số conSachChuaTra lấy từ truy vấn bảng ChiTietPhieuMuon (COUNT(*) WHERE NgayTra IS NULL > 0).
     */
    public PhieuMuon(int maPhieuMuon, int maDocGia, LocalDate ngayMuon,
                     LocalDate hanTra, boolean conSachChuaTra) {
        if (maPhieuMuon <= 0) {
            throw new IllegalArgumentException("Mã phiếu mượn phải lớn hơn 0.");
        }
        if (maDocGia <= 0) {
            throw new IllegalArgumentException("Mã độc giả phải lớn hơn 0.");
        }
        this.maPhieuMuon = maPhieuMuon;
        this.maDocGia = maDocGia;
        setNgayMuon(ngayMuon);
        setHanTraLichSu(hanTra);
        kiemTraTrangThai(conSachChuaTra);
    }

    // --- GETTERS ---
    public int getMaPhieuMuon() {
        return this.maPhieuMuon;
    }

    public int getMaDocGia() {
        return this.maDocGia;
    }

    public LocalDate getNgayMuon() {
        return this.ngayMuon;
    }

    public LocalDate getHanTra() {
        return this.hanTra;
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    // --- SETTERS ---
    public void setNgayMuon(LocalDate ngayMuon) {
        if (ngayMuon == null) {
            throw new IllegalArgumentException("Ngày mượn không được để trống.");
        }
        LocalDate today = LocalDate.now();
        if (ngayMuon.isAfter(today)) {
            throw new IllegalArgumentException("Ngày mượn không được lớn hơn ngày hiện tại (" + today + ").");
        }
        this.ngayMuon = ngayMuon;
    }

    // Dùng khi tạo phiếu mới: Hạn trả phải >= ngày mượn VÀ >= ngày hiện tại
    public void setHanTraMoi(LocalDate hanTra) {
        setHanTraLichSu(hanTra);
        LocalDate today = LocalDate.now();
        if (hanTra.isBefore(today)) {
            throw new IllegalArgumentException("Hạn trả của phiếu mới phải sau hoặc bằng ngày hiện tại (" + today + ").");
        }
    }

    // Dùng khi đọc dữ liệu cũ từ DB: Chỉ cần đảm bảo hạn trả >= ngày mượn
    public void setHanTraLichSu(LocalDate hanTra) {
        if (hanTra == null) {
            throw new IllegalArgumentException("Hạn trả không được để trống.");
        }
        if (this.ngayMuon != null && hanTra.isBefore(this.ngayMuon)) {
            throw new IllegalArgumentException("Hạn trả phải sau hoặc bằng ngày mượn (" + this.ngayMuon + ").");
        }
        this.hanTra = hanTra;
    }

    // Giới hạn chỉ cho phép 3 trạng thái hợp lệ (để private để ép buộc cập nhật qua nghiệp vụ)
    private void setTrangThai(String trangThai) {
        if (trangThai == null ||
            (!trangThai.equals(TRANG_THAI_DANG_MUON) &&
             !trangThai.equals(TRANG_THAI_QUA_HAN) &&
             !trangThai.equals(TRANG_THAI_DA_TRA))) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ. Chỉ chấp nhận: Đang mượn, Quá hạn, Đã trả.");
        }
        this.trangThai = trangThai;
    }

    // --- NGHIỆP VỤ TRẠNG THÁI ---
    public String kiemTraTrangThai(boolean conSachChuaTra) {
        if (!conSachChuaTra) {
            setTrangThai(TRANG_THAI_DA_TRA);
        } else if (LocalDate.now().isAfter(this.hanTra)) {
            setTrangThai(TRANG_THAI_QUA_HAN);
        } else {
            setTrangThai(TRANG_THAI_DANG_MUON);
        }
        return this.trangThai;
    }

    public void capNhatKhiTraSach(boolean conSachChuaTra) {
        kiemTraTrangThai(conSachChuaTra);
    }
}