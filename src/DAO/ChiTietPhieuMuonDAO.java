package DAO;

import database.DBConnection;
import model.ChiTietPhieuMuon;

import java.sql.Connection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuMuonDAO {

    /**
     * Hàm hỗ trợ chuyển đổi một dòng dữ liệu từ ResultSet thành đối tượng ChiTietPhieuMuon.
     * Sử dụng Constructor 2 (đọc từ DB) của lớp ChiTietPhieuMuon.
     */
    private ChiTietPhieuMuon mapResultSetToChiTiet(ResultSet rs) throws SQLException {
        int maPhieuMuon = rs.getInt("MaPhieuMuon");
        int maSach = rs.getInt("MaSach");
        int soLuong = rs.getInt("SoLuong");
        
        // Xử lý giá trị NULL cho cột NgayTra (Allow Nulls)
        Date sqlNgayTra = rs.getDate("NgayTra");
        LocalDate ngayTra = (sqlNgayTra != null) ? sqlNgayTra.toLocalDate() : null;
        
        String ghiChu = rs.getString("GhiChu"); // Cho phép Null

        return new ChiTietPhieuMuon(maPhieuMuon, maSach, soLuong, ngayTra, ghiChu);
    }

    /**
     * 1. Lấy toàn bộ danh sách chi tiết phiếu mượn.
     */
    public List<ChiTietPhieuMuon> getAll() throws SQLException {
        List<ChiTietPhieuMuon> danhSach = new ArrayList<>();
        String sql = "SELECT MaPhieuMuon, MaSach, SoLuong, NgayTra, GhiChu FROM ChiTietPhieuMuon";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                danhSach.add(mapResultSetToChiTiet(rs));
            }
        }
        return danhSach;
    }

    /**
     * 2. Tìm một dòng chi tiết theo khóa chính kép (MaPhieuMuon và MaSach).
     */
    public ChiTietPhieuMuon getById(int maPhieuMuon, int maSach) throws SQLException {
        String sql = "SELECT MaPhieuMuon, MaSach, SoLuong, NgayTra, GhiChu FROM ChiTietPhieuMuon WHERE MaPhieuMuon = ? AND MaSach = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPhieuMuon);
            ps.setInt(2, maSach);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToChiTiet(rs);
                }
            }
        }
        return null;
    }

    /**
     * 3. Lấy danh sách các sách thuộc về một Phiếu Mượn cụ thể (MaPhieuMuon).
     */
    public List<ChiTietPhieuMuon> getByMaPhieuMuon(int maPhieuMuon) throws SQLException {
        List<ChiTietPhieuMuon> danhSach = new ArrayList<>();
        String sql = "SELECT MaPhieuMuon, MaSach, SoLuong, NgayTra, GhiChu FROM ChiTietPhieuMuon WHERE MaPhieuMuon = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPhieuMuon);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToChiTiet(rs));
                }
            }
        }
        return danhSach;
    }

    /**
     * 4. Thêm mới một chi tiết phiếu mượn (truyền đủ MaPhieuMuon và MaSach).
     */
    public boolean insert(ChiTietPhieuMuon ct) throws SQLException {
        String sql = "INSERT INTO ChiTietPhieuMuon (MaPhieuMuon, MaSach, SoLuong, NgayTra, GhiChu) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ct.getMaPhieuMuon());
            ps.setInt(2, ct.getMaSach());
            ps.setInt(3, ct.getSoLuong());

            if (ct.getNgayTra() != null) {
                ps.setDate(4, Date.valueOf(ct.getNgayTra()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            if (ct.getGhiChu() != null) {
                ps.setString(5, ct.getGhiChu());
            } else {
                ps.setNull(5, Types.NVARCHAR);
            }

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 5. Cập nhật thông tin chi tiết phiếu mượn (số lượng, ngày trả, ghi chú) dựa vào khóa chính kép.
     */
    public boolean update(ChiTietPhieuMuon ct) throws SQLException {
        String sql = "UPDATE ChiTietPhieuMuon SET SoLuong = ?, NgayTra = ?, GhiChu = ? WHERE MaPhieuMuon = ? AND MaSach = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ct.getSoLuong());

            if (ct.getNgayTra() != null) {
                ps.setDate(2, Date.valueOf(ct.getNgayTra()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            if (ct.getGhiChu() != null) {
                ps.setString(3, ct.getGhiChu());
            } else {
                ps.setNull(3, Types.NVARCHAR);
            }

            ps.setInt(4, ct.getMaPhieuMuon());
            ps.setInt(5, ct.getMaSach());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 6. Xóa một đầu sách khỏi phiếu mượn theo khóa chính kép (MaPhieuMuon, MaSach).
     */
    public boolean delete(int maPhieuMuon, int maSach) throws SQLException {
        String sql = "DELETE FROM ChiTietPhieuMuon WHERE MaPhieuMuon = ? AND MaSach = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPhieuMuon);
            ps.setInt(2, maSach);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 7. Xóa toàn bộ chi tiết của một phiếu mượn (hữu ích khi cần xóa cả phiếu mượn).
     */
    public boolean deleteByMaPhieuMuon(int maPhieuMuon) throws SQLException {
        String sql = "DELETE FROM ChiTietPhieuMuon WHERE MaPhieuMuon = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPhieuMuon);

            return ps.executeUpdate() > 0;
        }
    }
}