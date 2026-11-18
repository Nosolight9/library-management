package Panel;

import DAO.SachDAO;
import DAO.TheLoaiDAO;
import model.Sach;
import model.TheLoai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SachPanel extends JPanel {
    private final SachDAO dao = new SachDAO();
    private final TheLoaiDAO theLoaiDao = new TheLoaiDAO();
    private final DefaultTableModel model = UI.tableModel("Mã", "Tên sách", "Tác giả", "NXB", "Năm XB", "Số lượng", "Mã thể loại");
    private final JTable table = UI.table(model);
    private final JTextField txtMa = new JTextField(), txtTen = new JTextField(), txtTacGia = new JTextField(),
            txtNxb = new JTextField(), txtNam = new JTextField(), txtSoLuong = new JTextField(), txtTim = new JTextField(20);
    private final JComboBox<TheLoai> cbTheLoai = new JComboBox<>();

    public SachPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        txtMa.setEditable(false);
        cbTheLoai.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v != null) setText(((TheLoai) v).getMaTheLoai() + " - " + ((TheLoai) v).getTenTheLoai());
                return this;
            }
        });

        JButton btnTim = new JButton("Tìm"), btnAll = new JButton("Hiện tất cả");
        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.add(new JLabel("Tên sách:")); north.add(txtTim); north.add(btnTim); north.add(btnAll);

        JButton btnThem = new JButton("Thêm"), btnSua = new JButton("Sửa"),
                btnXoa = new JButton("Xóa"), btnMoi = new JButton("Làm mới form");
        JPanel south = new JPanel(new BorderLayout());
        south.add(UI.form(
                new String[]{"Mã sách:", "Tên sách:", "Tác giả:", "Nhà xuất bản:", "Năm xuất bản:", "Số lượng:", "Thể loại:"},
                new JComponent[]{txtMa, txtTen, txtTacGia, txtNxb, txtNam, txtSoLuong, cbTheLoai}), BorderLayout.CENTER);
        south.add(UI.buttons(btnThem, btnSua, btnXoa, btnMoi), BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) fillForm(); });
        btnTim.addActionListener(e -> UI.safe(this, () -> load(dao.searchByName(txtTim.getText()))));
        btnAll.addActionListener(e -> refresh());
        btnThem.addActionListener(e -> UI.safe(this, this::them));
        btnSua.addActionListener(e -> UI.safe(this, this::sua));
        btnXoa.addActionListener(e -> UI.safe(this, this::xoa));
        btnMoi.addActionListener(e -> clearForm());
        refresh();
    }

    public void refresh() {
        UI.safe(this, () -> {
            cbTheLoai.removeAllItems();
            for (TheLoai t : theLoaiDao.getAll()) cbTheLoai.addItem(t);
            load(dao.getAll());
            clearForm();
        });
    }

    private void load(List<Sach> ds) {
        model.setRowCount(0);
        for (Sach s : ds) model.addRow(new Object[]{s.getMaSach(), s.getTenSach(), s.getTacGia(),
                s.getNhaXuatBan(), s.getNamXuatBan(), s.getSoLuong(), s.getMaTheLoai()});
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        txtMa.setText(model.getValueAt(r, 0).toString());
        txtTen.setText(model.getValueAt(r, 1).toString());
        txtTacGia.setText(model.getValueAt(r, 2).toString());
        txtNxb.setText(model.getValueAt(r, 3).toString());
        txtNam.setText(model.getValueAt(r, 4).toString());
        txtSoLuong.setText(model.getValueAt(r, 5).toString());
        int maTL = (int) model.getValueAt(r, 6);
        for (int i = 0; i < cbTheLoai.getItemCount(); i++)
            if (cbTheLoai.getItemAt(i).getMaTheLoai() == maTL) cbTheLoai.setSelectedIndex(i);
    }

    private void clearForm() {
        txtMa.setText(""); txtTen.setText(""); txtTacGia.setText("");
        txtNxb.setText(""); txtNam.setText(""); txtSoLuong.setText("");
        table.clearSelection();
    }

    private int maTheLoaiChon() throws Exception {
        TheLoai t = (TheLoai) cbTheLoai.getSelectedItem();
        if (t == null) throw new Exception("Chưa có thể loại nào. Hãy thêm thể loại trước.");
        return t.getMaTheLoai();
    }

    private int soNguyen(JTextField f, String ten) throws Exception {
        try { return Integer.parseInt(f.getText().trim()); }
        catch (NumberFormatException e) { throw new Exception(ten + " phải là số nguyên."); }
    }

    private void them() throws Exception {
        Sach s = new Sach(txtTen.getText(), txtTacGia.getText(), txtNxb.getText(),
                soNguyen(txtNam, "Năm xuất bản"), soNguyen(txtSoLuong, "Số lượng"), maTheLoaiChon());
        dao.insert(s);
        UI.info(this, "Thêm sách thành công!");
        refresh();
    }

    private void sua() throws Exception {
        if (txtMa.getText().isEmpty()) throw new Exception("Hãy chọn một cuốn sách trong bảng.");
        Sach s = new Sach(Integer.parseInt(txtMa.getText()), txtTen.getText(), txtTacGia.getText(), txtNxb.getText(),
                soNguyen(txtNam, "Năm xuất bản"), soNguyen(txtSoLuong, "Số lượng"), maTheLoaiChon());
        dao.update(s);
        UI.info(this, "Cập nhật thành công!");
        refresh();
    }

    private void xoa() throws Exception {
        if (txtMa.getText().isEmpty()) throw new Exception("Hãy chọn một cuốn sách trong bảng.");
        if (!UI.confirm(this, "Bạn có chắc muốn xóa cuốn sách này?")) return;
        try {
            dao.delete(Integer.parseInt(txtMa.getText()));
        } catch (java.sql.SQLException ex) {
            throw new Exception("Không thể xóa: sách này đã có trong phiếu mượn.");
        }
        refresh();
    }
}
