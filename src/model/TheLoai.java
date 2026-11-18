package model;

public class TheLoai {
    private int MaTheLoai;
    private String TenTheLoai;
    private String MoTa;

    public TheLoai(int MaTheLoai, String TenTheLoai, String MoTa) {
        setMaTheLoai(MaTheLoai);
        setTenTheLoai(TenTheLoai);
        this.MoTa = MoTa;
    }

    public int getMaTheLoai() {
        return this.MaTheLoai;
    }

    public String getTenTheLoai() {
        return this.TenTheLoai;
    }

    public String getMoTa() {
        return this.MoTa;
    }

    public void setTenTheLoai(String TenTheLoai) {
        if (TenTheLoai == null || TenTheLoai.isEmpty()) {
            throw new IllegalArgumentException("Tên thể loại không được để trống.");
        }
        this.TenTheLoai = TenTheLoai;
    }

    public void setMaTheLoai(int MaTheLoai) {
        if (MaTheLoai < 0) {
            throw new IllegalArgumentException("Mã thể loại không hợp lệ.");
        }
        this.MaTheLoai = MaTheLoai;
    }

    public void setMoTa(String MoTa) {
        this.MoTa = MoTa;
    }
}
