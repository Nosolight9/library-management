package DAO;

import database.DBConnection;
import model.PhieuMuon;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhieuMuonDAO {

    /**
     * Hàm hỗ trợ chuyển đổi một dòng dữ liệu từ ResultSet thành đối tượng PhieuMuon.
     * Sử dụng Constructor 2 (đọc từ DB) của lớp PhieuMuon.
     */
    private PhieuMuon mapResultSetToPhieuMuon(ResultSet rs) throws SQLException {
        int maPhieuMuon = rs.getInt("MaPhieuMuon");
        int maDocGia = rs.getInt("MaDocGia");
        LocalDate ngayMuon = rs.getDate("NgayMuon").toLocalDate();
        LocalDate hanTra = rs.getDate("HanTra").toLocalDate();
        
        // Lấy số lượng sách chưa trả từ cột phụ (subquery) trong câu lệnh SELECT
        boolean conSachChuaTra = rs.getInt("SoSachChuaTra") > 0;

        return new PhieuMuon(maPhieuMuon, maDocGia, ngayMuon, hanTra, conSachChuaTra);
    }

    /**
     * 1. Lấy toàn bộ danh sách phiếu mượn trong bảng PhieuMuon.
     * Kèm theo đếm số sách chưa trả (NgayTra IS NULL) từ bảng ChiTietPhieuMuon để khớp Constructor 2.
     */
    public List<PhieuMuon> getAll() throws SQLException {
        List<PhieuMuon> danhSach = new ArrayList<>();
        String sql = "SELECT pm.MaPhieuMuon, pm.MaDocGia, pm.NgayMuon, pm.HanTra, pm.TrangThai, " +
                     "(SELECT COUNT(*) FROM ChiTietPhieuMuon ct WHERE ct.MaPhieuMuon = pm.MaPhieuMuon AND ct.NgayTra IS NULL) AS SoSachChuaTra " +
                     "FROM PhieuMuon pm";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                danhSach.add(mapResultSetToPhieuMuon(rs));
            }
        }
        return danhSach;
    }

    /**
     * 2. Tìm một phiếu mượn theo mã (MaPhieuMuon).
     */
    public PhieuMuon getById(int maPhieuMuon) throws SQLException {
        String sql = "SELECT pm.MaPhieuMuon, pm.MaDocGia, pm.NgayMuon, pm.HanTra, pm.TrangThai, " +
                     "(SELECT COUNT(*) FROM ChiTietPhieuMuon ct WHERE ct.MaPhieuMuon = pm.MaPhieuMuon AND ct.NgayTra IS NULL) AS SoSachChuaTra " +
                     "FROM PhieuMuon pm WHERE pm.MaPhieuMuon = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPhieuMuon);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhieuMuon(rs);
                }
            }
        }
        return null;
    }

    /**
     * 3. Thêm phiếu mượn mới (không truyền MaPhieuMuon vì SQL Server tự tăng IDENTITY).
     * Trả về MaPhieuMuon vừa được sinh ra (hoặc -1 nếu thất bại) để tiện thêm tiếp ChiTietPhieuMuon.
     */
    public int insert(PhieuMuon phieuMuon) throws SQLException {
        String sql = "INSERT INTO PhieuMuon (MaDocGia, NgayMuon, HanTra, TrangThai) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, phieuMuon.getMaDocGia());
            ps.setDate(2, Date.valueOf(phieuMuon.getNgayMuon()));
            ps.setDate(3, Date.valueOf(phieuMuon.getHanTra()));
            ps.setString(4, phieuMuon.getTrangThai());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    /**
     * 4. Cập nhật thông tin phiếu mượn dựa vào MaPhieuMuon.
     */
    public boolean update(PhieuMuon phieuMuon) throws SQLException {
        String sql = "UPDATE PhieuMuon SET MaDocGia = ?, NgayMuon = ?, HanTra = ?, TrangThai = ? WHERE MaPhieuMuon = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, phieuMuon.getMaDocGia());
            ps.setDate(2, Date.valueOf(phieuMuon.getNgayMuon()));
            ps.setDate(3, Date.valueOf(phieuMuon.getHanTra()));
            ps.setString(4, phieuMuon.getTrangThai());
            ps.setInt(5, phieuMuon.getMaPhieuMuon());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 5. Xóa phiếu mượn theo MaPhieuMuon.
     * Lưu ý: Nếu phiếu mượn đã có dữ liệu trong ChiTietPhieuMuon, SQL Server sẽ ném SQLException (vi phạm Foreign Key).
     */
    public boolean delete(int maPhieuMuon) throws SQLException {
        String sql = "DELETE FROM PhieuMuon WHERE MaPhieuMuon = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPhieuMuon);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 6. Tìm danh sách phiếu mượn theo mã độc giả (MaDocGia).
     */
    public List<PhieuMuon> getByDocGia(int maDocGia) throws SQLException {
        List<PhieuMuon> danhSach = new ArrayList<>();
        String sql = "SELECT pm.MaPhieuMuon, pm.MaDocGia, pm.NgayMuon, pm.HanTra, pm.TrangThai, " +
                     "(SELECT COUNT(*) FROM ChiTietPhieuMuon ct WHERE ct.MaPhieuMuon = pm.MaPhieuMuon AND ct.NgayTra IS NULL) AS SoSachChuaTra " +
                     "FROM PhieuMuon pm WHERE pm.MaDocGia = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maDocGia);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToPhieuMuon(rs));
                }
            }
        }
        return danhSach;
    }

    /**
     * 7. Cập nhật nhanh cột TrangThai trong Database sau khi gọi phieuMuon.capNhatKhiTraSach(...).
     */
    public boolean updateTrangThai(int maPhieuMuon, String trangThaiMoi) throws SQLException {
        String sql = "UPDATE PhieuMuon SET TrangThai = ? WHERE MaPhieuMuon = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangThaiMoi);
            ps.setInt(2, maPhieuMuon);

            return ps.executeUpdate() > 0;
        }
    }
}