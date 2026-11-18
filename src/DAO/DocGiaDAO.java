package DAO;
import database.DBConnection;
import model.DocGia;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DocGiaDAO {

    /**
     * Hàm hỗ trợ chuyển đổi một dòng ResultSet từ SQL Server thành đối tượng DocGia.
     * Sử dụng Constructor 2 (đọc từ DB) của lớp DocGia.
     */
    private DocGia mapResultSetToDocGia(ResultSet rs) throws SQLException {
        int maDocGia = rs.getInt("MaDocGia");
        String hoTen = rs.getString("HoTen"); // Khớp với tên cột HoTen trong SQL Server
        
        Date sqlNgaySinh = rs.getDate("NgaySinh");
        LocalDate ngaySinh = (sqlNgaySinh != null) ? sqlNgaySinh.toLocalDate() : null;
        
        String gioiTinh = rs.getString("GioiTinh");
        String diaChi = rs.getString("DiaChi");
        String soDienThoai = rs.getString("SoDienThoai");
        String email = rs.getString("Email");
        
        Date sqlNgayDangKy = rs.getDate("NgayDangKy");
        LocalDate ngayDangKy = (sqlNgayDangKy != null) ? sqlNgayDangKy.toLocalDate() : null;

        return new DocGia(maDocGia, hoTen, ngaySinh, gioiTinh, diaChi, soDienThoai, email, ngayDangKy);
    }

    /**
     * 1. Lấy toàn bộ danh sách độc giả.
     */
    public List<DocGia> getAll() throws SQLException {
        List<DocGia> danhSach = new ArrayList<>();
        String sql = "SELECT MaDocGia, HoTen, NgaySinh, GioiTinh, DiaChi, SoDienThoai, Email, NgayDangKy FROM DocGia";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                danhSach.add(mapResultSetToDocGia(rs));
            }
        }
        return danhSach;
    }

    /**
     * 2. Tìm độc giả theo mã (MaDocGia).
     */
    public DocGia getById(int maDocGia) throws SQLException {
        String sql = "SELECT MaDocGia, HoTen, NgaySinh, GioiTinh, DiaChi, SoDienThoai, Email, NgayDangKy FROM DocGia WHERE MaDocGia = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maDocGia);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDocGia(rs);
                }
            }
        }
        return null;
    }

    /**
     * 3. Thêm độc giả mới (không truyền MaDocGia vì SQL Server tự tăng IDENTITY).
     */
    public boolean insert(DocGia docGia) throws SQLException {
        String sql = "INSERT INTO DocGia (HoTen, NgaySinh, GioiTinh, DiaChi, SoDienThoai, Email, NgayDangKy) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, docGia.getTenDocGia());
            ps.setDate(2, docGia.getNgaySinh() != null ? Date.valueOf(docGia.getNgaySinh()) : null);
            ps.setString(3, docGia.getGioiTinh());
            ps.setString(4, docGia.getDiaChi());
            ps.setString(5, docGia.getSoDienThoai());
            ps.setString(6, docGia.getEmail());
            ps.setDate(7, Date.valueOf(docGia.getNgayDangKy()));

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 4. Cập nhật thông tin độc giả theo MaDocGia.
     * (Thường giữ nguyên NgayDangKy ban đầu, chỉ cập nhật thông tin cá nhân).
     */
    public boolean update(DocGia docGia) throws SQLException {
        String sql = "UPDATE DocGia SET HoTen = ?, NgaySinh = ?, GioiTinh = ?, DiaChi = ?, SoDienThoai = ?, Email = ? WHERE MaDocGia = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, docGia.getTenDocGia());
            ps.setDate(2, docGia.getNgaySinh() != null ? Date.valueOf(docGia.getNgaySinh()) : null);
            ps.setString(3, docGia.getGioiTinh());
            ps.setString(4, docGia.getDiaChi());
            ps.setString(5, docGia.getSoDienThoai());
            ps.setString(6, docGia.getEmail());
            ps.setInt(7, docGia.getMaDocGia());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 5. Xóa độc giả theo MaDocGia.
     * Lưu ý: Nếu độc giả đã có phiếu mượn trong bảng PhieuMuon, SQL Server sẽ báo lỗi khóa ngoại.
     */
    public boolean delete(int maDocGia) throws SQLException {
        String sql = "DELETE FROM DocGia WHERE MaDocGia = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maDocGia);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 6. Tìm kiếm gần đúng độc giả theo họ tên (LIKE).
     */
    public List<DocGia> searchByName(String hoTen) throws SQLException {
        List<DocGia> danhSach = new ArrayList<>();
        String sql = "SELECT MaDocGia, HoTen, NgaySinh, GioiTinh, DiaChi, SoDienThoai, Email, NgayDangKy FROM DocGia WHERE HoTen LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + hoTen.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToDocGia(rs));
                }
            }
        }
        return danhSach;
    }
}
