package model;

import java.time.LocalDate;

public class DocGia {
    private int maDocGia;
    private String tenDocGia;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String diaChi;
    private String soDienThoai;
    private String email;
    private LocalDate ngayDangKy;

    /**
     * CONSTRUCTOR 1: Dùng khi THÊM ĐỘC GIẢ MỚI.
     * - Không cần truyền maDocGia (SQL Server tự tăng).
     * - Tự động lấy ngayDangKy là ngày hiện tại (LocalDate.now()).
     */
    public DocGia(String tenDocGia, LocalDate ngaySinh, String gioiTinh,
                  String diaChi, String soDienThoai, String email) {
        setTenDocGia(tenDocGia);
        setNgaySinh(ngaySinh);
        setGioiTinh(gioiTinh);
        setDiaChi(diaChi);
        setSoDienThoai(soDienThoai);
        setEmail(email);
        setNgayDangKy(LocalDate.now());
    }

    /**
     * CONSTRUCTOR 2: Dùng khi ĐỌC DỮ LIỆU TỪ DATABASE (DAO).
     * - Có đầy đủ maDocGia và ngayDangKy từ lịch sử.
     */
    public DocGia(int maDocGia, String tenDocGia, LocalDate ngaySinh, String gioiTinh,
                  String diaChi, String soDienThoai, String email, LocalDate ngayDangKy) {
        if (maDocGia <= 0) {
            throw new IllegalArgumentException("Mã độc giả phải lớn hơn 0.");
        }
        this.maDocGia = maDocGia;
        setTenDocGia(tenDocGia);
        setNgaySinh(ngaySinh);
        setGioiTinh(gioiTinh);
        setDiaChi(diaChi);
        setSoDienThoai(soDienThoai);
        setEmail(email);
        setNgayDangKy(ngayDangKy);
    }

    // --- GETTERS ---
    public int getMaDocGia() {
        return this.maDocGia;
    }

    public String getTenDocGia() {
        return this.tenDocGia;
    }

    public LocalDate getNgaySinh() {
        return this.ngaySinh;
    }

    public String getGioiTinh() {
        return this.gioiTinh;
    }

    public String getDiaChi() {
        return this.diaChi;
    }

    public String getSoDienThoai() {
        return this.soDienThoai;
    }

    public String getEmail() {
        return this.email;
    }

    public LocalDate getNgayDangKy() {
        return this.ngayDangKy;
    }

    // --- SETTERS ---
    public void setTenDocGia(String tenDocGia) {
        if (tenDocGia == null || tenDocGia.isBlank()) {
            throw new IllegalArgumentException("Tên độc giả không được để trống.");
        }
        this.tenDocGia = tenDocGia.trim();
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        if (ngaySinh == null) {
            throw new IllegalArgumentException("Ngày sinh không được để trống.");
        }
        LocalDate today = LocalDate.now();
        if (ngaySinh.isAfter(today)) {
            throw new IllegalArgumentException("Ngày sinh không được lớn hơn ngày hiện tại (" + today + ").");
        }
        this.ngaySinh = ngaySinh;
    }

    public void setGioiTinh(String gioiTinh) {
        if (gioiTinh == null || gioiTinh.isBlank()) {
            throw new IllegalArgumentException("Giới tính không được để trống.");
        }
        String gt = gioiTinh.trim();
        if (gt.equalsIgnoreCase("Nam")) {
            this.gioiTinh = "Nam";
        } else if (gt.equalsIgnoreCase("Nữ")) {
            this.gioiTinh = "Nữ";
        } else {
            throw new IllegalArgumentException("Giới tính chỉ được là 'Nam' hoặc 'Nữ'.");
        }
    }

    public void setDiaChi(String diaChi) {
        if (diaChi == null || diaChi.isBlank()) {
            throw new IllegalArgumentException("Địa chỉ không được để trống.");
        }
        this.diaChi = diaChi.trim();
    }

    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.isBlank()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }
        String sdt = soDienThoai.trim();
        // Kiểm tra bắt đầu bằng số 0 và có đúng 10 chữ số
        if (!sdt.matches("0\\d{9}")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải gồm 10 chữ số và bắt đầu bằng số 0).");
        }
        this.soDienThoai = sdt;
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }
        String mail = email.trim();
        // Kiểm tra định dạng email cơ bản: ten@tenmien.duoi
        if (!mail.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Email không đúng định dạng (ví dụ: ten@example.com).");
        }
        this.email = mail;
    }

    public void setNgayDangKy(LocalDate ngayDangKy) {
        if (ngayDangKy == null) {
            throw new IllegalArgumentException("Ngày đăng ký không được để trống.");
        }
        LocalDate today = LocalDate.now();
        if (ngayDangKy.isAfter(today)) {
            throw new IllegalArgumentException("Ngày đăng ký không được lớn hơn ngày hiện tại (" + today + ").");
        }
        if (this.ngaySinh != null && ngayDangKy.isBefore(this.ngaySinh)) {
            throw new IllegalArgumentException("Ngày đăng ký không được trước ngày sinh.");
        }
        this.ngayDangKy = ngayDangKy;
    }
}