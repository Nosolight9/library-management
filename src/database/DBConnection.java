package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=QuanLyThuVien;encrypt=true;trustServerCertificate=true;";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "123456";

    private DBConnection() {
    }

    /** Hàm các lớp DAO đang gọi: mở kết nối tới SQL Server. */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không tìm thấy SQL Server JDBC Driver.", e);
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    // ----- Các hàm kiểm tra kết nối (trước đây nằm trong KetNoi.java) -----

    public static Connection DoConnection() {
        try {
            Connection cNT = getConnection();
            System.out.println("-> KET NOI THANH CONG");
            return cNT;
        } catch (SQLException e) {
            System.err.println("-> KET NOI THAT BAI");
            e.printStackTrace();
            return null;
        }
    }

    public static void DisConnection(Connection cNT) {
        try {
            if (cNT != null) {
                cNT.close();
                System.out.println("NGAT KET NOI THANH CONG");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void printInfo(Connection cNT) {
        if (cNT != null) {
            try {
                DatabaseMetaData mtdt = cNT.getMetaData();
                System.out.println(mtdt.getDatabaseProductName() + " " + mtdt.getDatabaseProductVersion());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
