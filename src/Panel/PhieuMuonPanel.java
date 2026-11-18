package Panel;

import DAO.ChiTietPhieuMuonDAO;
import DAO.DocGiaDAO;
import DAO.PhieuMuonDAO;
import DAO.SachDAO;
import model.ChiTietPhieuMuon;
import model.DocGia;
import model.PhieuMuon;
import model.Sach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Màn hình phiếu mượn: bảng trên = phiếu mượn, bảng dưới = chi tiết (sách) của phiếu đang chọn. */
public class PhieuMuonPanel extends JPanel {
    private final PhieuMuonDAO pmDao = new PhieuMuonDAO();
    private final ChiTietPhieuMuonDAO ctDao = new ChiTietPhieuMuonDAO();
    private final DocGiaDAO dgDao = new DocGiaDAO();
    private final SachDAO sachDao = new SachDAO();

    private final DefaultTableModel pmModel = UI.tableModel("Mã phiếu", "Mã độc giả", "Ngày mượn", "Hạn trả", "Trạng thái");
    private final JTable pmTable = UI.table(pmModel);
    private final DefaultTableModel ctModel = UI.tableModel("Mã sách", "Số lượng", "Ngày trả", "Ghi chú");
    private final JTable ctTable = UI.table(ctModel);

    private final JComboBox<DocGia> cbDocGia = new JComboBox<>();
    private final JTextField txtNgayMuon = new JTextField(10), txtHanTra = new JTextField(10);
    private final JComboBox<Sach> cbSach = new JComboBox<>();
    private final JSpinner spSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final JTextField txtGhiChu = new JTextField(20);

    public PhieuMuonPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        cbDocGia.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v != null) setText(((DocGia) v).getMaDocGia() + " - " + ((DocGia) v).getTenDocGia());
                return this;
            }
        });
        cbSach.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v != null) setText(((Sach) v).getMaSach() + " - " + ((Sach) v).getTenSach());
                return this;
            }
        });

        // ----- Phần trên: phiếu mượn -----
        JButton btnTao = new JButton("Tạo phiếu"), btnXoaPhieu = new JButton("Xóa phiếu"), btnTai = new JButton("Tải lại");
        JPanel formPM = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formPM.add(new JLabel("Độc giả:")); formPM.add(cbDocGia);
        formPM.add(new JLabel("Ngày mượn:")); formPM.add(txtNgayMuon);
        formPM.add(new JLabel("Hạn trả:")); formPM.add(txtHanTra);
        formPM.add(btnTao); formPM.add(btnXoaPhieu); formPM.add(btnTai);
        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.setBorder(new TitledBorder("Danh sách phiếu mượn"));
        top.add(new JScrollPane(pmTable), BorderLayout.CENTER);
        top.add(formPM, BorderLayout.SOUTH);

        // ----- Phần dưới: chi tiết phiếu -----
        JButton btnThemSach = new JButton("Thêm sách vào phiếu"), btnTra = new JButton("Trả sách (hôm nay)"),
                btnXoaDong = new JButton("Xóa sách khỏi phiếu");
        JPanel formCT = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formCT.add(new JLabel("Sách:")); formCT.add(cbSach);
        formCT.add(new JLabel("Số lượng:")); formCT.add(spSoLuong);
        formCT.add(new JLabel("Ghi chú:")); formCT.add(txtGhiChu);
        JPanel btnCT = UI.buttons(btnThemSach, btnTra, btnXoaDong);
        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setBorder(new TitledBorder("Chi tiết phiếu đang chọn"));
        bottom.add(new JScrollPane(ctTable), BorderLayout.CENTER);
        JPanel bs = new JPanel(new GridLayout(2, 1));
        bs.add(formCT); bs.add(btnCT);
        bottom.add(bs, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, bottom);
        split.setResizeWeight(0.5);
        add(split, BorderLayout.CENTER);

        pmTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) UI.safe(this, this::loadChiTiet);
        });
        btnTao.addActionListener(e -> UI.safe(this, this::taoPhieu));
        btnXoaPhieu.addActionListener(e -> UI.safe(this, this::xoaPhieu));
        btnTai.addActionListener(e -> refresh());
        btnThemSach.addActionListener(e -> UI.safe(this, this::themSach));
        btnTra.addActionListener(e -> UI.safe(this, this::traSach));
        btnXoaDong.addActionListener(e -> UI.safe(this, this::xoaSach));
        refresh();
    }

    public void refresh() {
        UI.safe(this, () -> {
            cbDocGia.removeAllItems();
            for (DocGia d : dgDao.getAll()) cbDocGia.addItem(d);
            cbSach.removeAllItems();
            for (Sach s : sachDao.getAll()) cbSach.addItem(s);
            txtNgayMuon.setText(LocalDate.now().toString());
            txtHanTra.setText(LocalDate.now().plusDays(7).toString());
            loadPhieu();
        });
    }

    private void loadPhieu() throws Exception {
        pmModel.setRowCount(0);
        ctModel.setRowCount(0);
        for (PhieuMuon p : pmDao.getAll())
            pmModel.addRow(new Object[]{p.getMaPhieuMuon(), p.getMaDocGia(), p.getNgayMuon(), p.getHanTra(), p.getTrangThai()});
    }

    private void loadChiTiet() throws Exception {
        ctModel.setRowCount(0);
        int r = pmTable.getSelectedRow();
        if (r < 0) return;
        for (ChiTietPhieuMuon c : ctDao.getByMaPhieuMuon((int) pmModel.getValueAt(r, 0)))
            ctModel.addRow(new Object[]{c.getMaSach(), c.getSoLuong(), c.getNgayTra(), c.getGhiChu()});
    }

    private int maPhieuChon() throws Exception {
        int r = pmTable.getSelectedRow();
        if (r < 0) throw new Exception("Hãy chọn một phiếu mượn ở bảng trên.");
        return (int) pmModel.getValueAt(r, 0);
    }

    private LocalDate ngay(JTextField f, String ten) throws Exception {
        try { return LocalDate.parse(f.getText().trim()); }
        catch (DateTimeParseException e) { throw new Exception(ten + " phải đúng dạng yyyy-MM-dd."); }
    }

    /** Tính lại trạng thái phiếu (Đang mượn / Quá hạn / Đã trả) rồi lưu vào CSDL. */
    private void capNhatTrangThai(int maPhieu) throws Exception {
        PhieuMuon p = pmDao.getById(maPhieu);
        if (p != null) pmDao.updateTrangThai(maPhieu, p.getTrangThai());
    }

    /** Sau khi thay đổi chi tiết: cập nhật trạng thái, tải lại bảng, chọn lại phiếu cũ. */
    private void taiLaiGiuChon(int maPhieu) throws Exception {
        capNhatTrangThai(maPhieu);
        loadPhieu();
        for (int i = 0; i < pmModel.getRowCount(); i++)
            if ((int) pmModel.getValueAt(i, 0) == maPhieu) { pmTable.setRowSelectionInterval(i, i); break; }
    }

    private void taoPhieu() throws Exception {
        DocGia d = (DocGia) cbDocGia.getSelectedItem();
        if (d == null) throw new Exception("Chưa có độc giả nào.");
        PhieuMuon p = new PhieuMuon(d.getMaDocGia(), ngay(txtNgayMuon, "Ngày mượn"), ngay(txtHanTra, "Hạn trả"));
        int ma = pmDao.insert(p);
        UI.info(this, "Đã tạo phiếu mượn số " + ma + ". Hãy chọn phiếu và thêm sách vào.");
        loadPhieu();
    }

    private void xoaPhieu() throws Exception {
        int ma = maPhieuChon();
        if (!UI.confirm(this, "Xóa phiếu " + ma + " và toàn bộ sách trong phiếu?")) return;
        ctDao.deleteByMaPhieuMuon(ma);
        pmDao.delete(ma);
        loadPhieu();
    }

    private void themSach() throws Exception {
        int ma = maPhieuChon();
        Sach s = (Sach) cbSach.getSelectedItem();
        if (s == null) throw new Exception("Chưa có sách nào.");
        if (ctDao.getById(ma, s.getMaSach()) != null) throw new Exception("Sách này đã có trong phiếu.");
        ctDao.insert(new ChiTietPhieuMuon(ma, s.getMaSach(), (int) spSoLuong.getValue(), txtGhiChu.getText()));
        txtGhiChu.setText("");
        taiLaiGiuChon(ma);
    }

    private int maSachChon() throws Exception {
        int r = ctTable.getSelectedRow();
        if (r < 0) throw new Exception("Hãy chọn một dòng sách ở bảng dưới.");
        return (int) ctModel.getValueAt(r, 0);
    }

    private void traSach() throws Exception {
        int ma = maPhieuChon();
        ChiTietPhieuMuon ct = ctDao.getById(ma, maSachChon());
        ct.traSachHomNay();
        ctDao.update(ct);
        taiLaiGiuChon(ma);
    }

    private void xoaSach() throws Exception {
        int ma = maPhieuChon();
        int maSach = maSachChon();
        if (!UI.confirm(this, "Xóa sách này khỏi phiếu?")) return;
        ctDao.delete(ma, maSach);
        taiLaiGiuChon(ma);
    }
}
