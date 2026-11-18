package DAO;

import database.DBConnection;
import model.TheLoai;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TheLoaiDAO {

    /**
     * Hàm hỗ trợ chuyển đổi một dòng dữ liệu từ ResultSet thành đối tượng TheLoai.
     * Sử dụng Constructor 2 (đọc từ DB) của lớp TheLoai.
     */
    private TheLoai mapResultSetToTheLoai(ResultSet rs) throws SQLException {
        int maTheLoai = rs.getInt("MaTheLoai");
        String tenTheLoai = rs.getString("TenTheLoai");
        String moTa = rs.getString("MoTa"); // Cho phép Null[cite: 1]

        return new TheLoai(maTheLoai, tenTheLoai, moTa);
    }

    /**
     * 1. Lấy toàn bộ danh sách thể loại trong bảng TheLoai.
     */
    public List<TheLoai> getAll() throws SQLException {
        List<TheLoai> danhSach = new ArrayList<>();
        String sql = "SELECT MaTheLoai, TenTheLoai, MoTa FROM TheLoai";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                danhSach.add(mapResultSetToTheLoai(rs));
            }
        }
        return danhSach;
    }

    /**
     * 2. Tìm một thể loại theo mã (MaTheLoai).
     */
    public TheLoai getById(int maTheLoai) throws SQLException {
        String sql = "SELECT MaTheLoai, TenTheLoai, MoTa FROM TheLoai WHERE MaTheLoai = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maTheLoai);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTheLoai(rs);
                }
            }
        }
        return null;
    }

    /**
     * 3. Thêm thể loại mới (không truyền MaTheLoai vì SQL Server tự tăng IDENTITY).
     */
    public boolean insert(TheLoai theLoai) throws SQLException {
        String sql = "INSERT INTO TheLoai (TenTheLoai, MoTa) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, theLoai.getTenTheLoai());
            
            // Xử lý giá trị NULL cho cột MoTa (Allow Nulls)[cite: 1]
            if (theLoai.getMoTa() != null && !theLoai.getMoTa().trim().isEmpty()) {
                ps.setString(2, theLoai.getMoTa());
            } else {
                ps.setNull(2, Types.NVARCHAR);
            }

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 4. Cập nhật thông tin thể loại dựa vào MaTheLoai.
     */
    public boolean update(TheLoai theLoai) throws SQLException {
        String sql = "UPDATE TheLoai SET TenTheLoai = ?, MoTa = ? WHERE MaTheLoai = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, theLoai.getTenTheLoai());

            if (theLoai.getMoTa() != null && !theLoai.getMoTa().trim().isEmpty()) {
                ps.setString(2, theLoai.getMoTa());
            } else {
                ps.setNull(2, Types.NVARCHAR);
            }

            ps.setInt(3, theLoai.getMaTheLoai());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 5. Xóa thể loại theo MaTheLoai.
     * Lưu ý: Nếu thể loại đang có sách tham chiếu trong bảng Sach, SQL Server sẽ ném SQLException (vi phạm Foreign Key).
     */
    public boolean delete(int maTheLoai) throws SQLException {
        String sql = "DELETE FROM TheLoai WHERE MaTheLoai = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maTheLoai);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 6. Tìm kiếm gần đúng thể loại theo tên (LIKE).
     */
    public List<TheLoai> searchByName(String tenTheLoai) throws SQLException {
        if (tenTheLoai == null) {
            throw new IllegalArgumentException("Tên thể loại tìm kiếm không được để trống.");
        }

        List<TheLoai> danhSach = new ArrayList<>();
        String sql = "SELECT MaTheLoai, TenTheLoai, MoTa FROM TheLoai WHERE TenTheLoai LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + tenTheLoai.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToTheLoai(rs));
                }
            }
        }
        return danhSach;
    }
}