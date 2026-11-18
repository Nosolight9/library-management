package DAO;


import database.DBConnection;
import model.Sach;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SachDAO {

    /**
     * Hàm hỗ trợ chuyển đổi một dòng dữ liệu từ ResultSet thành đối tượng Sach.
     * Sử dụng Constructor 2 (đọc từ DB) của lớp Sach.
     */
    private Sach mapResultSetToSach(ResultSet rs) throws SQLException {
        int maSach = rs.getInt("MaSach");
        String tenSach = rs.getString("TenSach");
        String tacGia = rs.getString("TacGia");
        String nhaXuatBan = rs.getString("NhaXuatBan");
        int namXuatBan = rs.getInt("NamXuatBan");
        int soLuong = rs.getInt("SoLuong");
        int maTheLoai = rs.getInt("MaTheLoai");

        return new Sach(maSach, tenSach, tacGia, nhaXuatBan, namXuatBan, soLuong, maTheLoai);
    }

    /**
     * 1. Lấy toàn bộ danh sách sách trong bảng Sach.
     */
    public List<Sach> getAll() throws SQLException {
        List<Sach> danhSach = new ArrayList<>();
        String sql = "SELECT MaSach, TenSach, TacGia, NhaXuatBan, NamXuatBan, SoLuong, MaTheLoai FROM Sach";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                danhSach.add(mapResultSetToSach(rs));
            }
        }
        return danhSach;
    }

    /**
     * 2. Tìm một cuốn sách theo mã (MaSach).
     */
    public Sach getById(int maSach) throws SQLException {
        String sql = "SELECT MaSach, TenSach, TacGia, NhaXuatBan, NamXuatBan, SoLuong, MaTheLoai FROM Sach WHERE MaSach = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maSach);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSach(rs);
                }
            }
        }
        return null;
    }

    /**
     * 3. Thêm sách mới (không truyền MaSach vì SQL Server tự tăng IDENTITY).
     */
    public boolean insert(Sach sach) throws SQLException {
        String sql = "INSERT INTO Sach (TenSach, TacGia, NhaXuatBan, NamXuatBan, SoLuong, MaTheLoai) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sach.getTenSach());
            ps.setString(2, sach.getTacGia());
            ps.setString(3, sach.getNhaXuatBan());
            ps.setInt(4, sach.getNamXuatBan());
            ps.setInt(5, sach.getSoLuong());
            ps.setInt(6, sach.getMaTheLoai());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 4. Cập nhật thông tin sách dựa vào MaSach.
     */
    public boolean update(Sach sach) throws SQLException {
        String sql = "UPDATE Sach SET TenSach = ?, TacGia = ?, NhaXuatBan = ?, NamXuatBan = ?, SoLuong = ?, MaTheLoai = ? WHERE MaSach = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sach.getTenSach());
            ps.setString(2, sach.getTacGia());
            ps.setString(3, sach.getNhaXuatBan());
            ps.setInt(4, sach.getNamXuatBan());
            ps.setInt(5, sach.getSoLuong());
            ps.setInt(6, sach.getMaTheLoai());
            ps.setInt(7, sach.getMaSach());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 5. Xóa sách theo MaSach.
     * Lưu ý: Nếu sách đã tồn tại trong ChiTietPhieuMuon, SQL Server sẽ ném SQLException (vi phạm Foreign Key).
     */
    public boolean delete(int maSach) throws SQLException {
        String sql = "DELETE FROM Sach WHERE MaSach = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maSach);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 6. Tìm kiếm gần đúng sách theo tên (LIKE).
     */
    public List<Sach> searchByName(String tenSach) throws SQLException {
        if (tenSach == null) {
            throw new IllegalArgumentException("Tên sách tìm kiếm không được để trống.");
        }

        List<Sach> danhSach = new ArrayList<>();
        String sql = "SELECT MaSach, TenSach, TacGia, NhaXuatBan, NamXuatBan, SoLuong, MaTheLoai FROM Sach WHERE TenSach LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + tenSach.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToSach(rs));
                }
            }
        }
        return danhSach;
    }
}