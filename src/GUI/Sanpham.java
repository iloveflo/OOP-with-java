/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;

import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

/**
 *
 * @author Neo 16
 */
public class Sanpham extends javax.swing.JPanel implements Refreshable {

    /**
     * Creates new form Sanpham1
     */
    private final Service.IProductService productService = new Service.impl.ProductServiceImpl();
    private final DAO.IDanhMucDAO danhMucDAO = new DAO.DanhMucDAOImpl();

    @Override
    public void refresh() {
        doDuLieuSanPham();
    }

    public Sanpham() {
        initComponents();
        styleUI();

        loadComboBoxData();
        doDuLieuSanPham();

        tblSanpham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                hienThiChiTietSanPham();
            }
        }); 

        btnThem.addActionListener(e -> {
            try {
                String tenQuanAo = txtTenquanao.getText().trim();
                String maQuanAoStr = txtMaquanao.getText().trim();
                String donGiaNhapStr = txtDongianhap.getText().trim();
                String donGiaBanStr = txtDongiaban.getText().trim();
                String soLuongStr = txtSoluong.getText().trim();
                String anh = txtAnh.getText().trim();

                if (tenQuanAo.isEmpty() || maQuanAoStr.isEmpty() || donGiaNhapStr.isEmpty() || donGiaBanStr.isEmpty() || soLuongStr.isEmpty() || anh.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                int maQuanAo = Integer.parseInt(maQuanAoStr);
                int soLuong = Integer.parseInt(soLuongStr);
                java.math.BigDecimal donGiaNhap = new java.math.BigDecimal(donGiaNhapStr);
                java.math.BigDecimal donGiaBan = new java.math.BigDecimal(donGiaBanStr);

                if (productService.getProductById(maQuanAo).isPresent()) {
                    JOptionPane.showMessageDialog(this, "Mã quần áo đã tồn tại!");
                    return;
                }

                Model.SanPham sp = new Model.SanPham();
                sp.setMaQuanAo(maQuanAo);
                sp.setTenQuanAo(tenQuanAo);
                sp.setSoLuong(soLuong);
                sp.setAnh(anh);
                sp.setDonGiaNhap(donGiaNhap);
                sp.setDonGiaBan(donGiaBan);

                Integer idLoai = getSelectedLoaiId();
                if (idLoai != null) sp.setTheLoai(danhMucDAO.getTheLoaiById(idLoai));
                Integer idCo = getSelectedCoId();
                if (idCo != null) sp.setCo(danhMucDAO.getCoById(idCo));
                Integer idChatLieu = getSelectedChatLieuId();
                if (idChatLieu != null) sp.setChatLieu(danhMucDAO.getChatLieuById(idChatLieu));
                Integer idMau = getSelectedMauId();
                if (idMau != null) sp.setMau(danhMucDAO.getMauById(idMau));
                Integer idDoiTuong = getSelectedDoiTuongId();
                if (idDoiTuong != null) sp.setDoiTuong(danhMucDAO.getDoiTuongById(idDoiTuong));
                Integer idMua = getSelectedMuaId();
                if (idMua != null) sp.setMua(danhMucDAO.getMuaById(idMua));
                Integer idNSX = getSelectedNSXId();
                if (idNSX != null) sp.setNoiSanXuat(danhMucDAO.getNoiSanXuatById(idNSX));

                if (productService.addProduct(sp)) {
                    JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!");
                    doDuLieuSanPham();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm sản phẩm thất bại!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        btnXoa.addActionListener(e -> {
            String maQuanAo = txtMaquanao.getText().trim();
            if (maQuanAo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã quần áo cần xóa!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa sản phẩm có mã: " + maQuanAo + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (productService.deleteProduct(Integer.parseInt(maQuanAo))) {
                        JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công!");
                        doDuLieuSanPham();
                    } else {
                        JOptionPane.showMessageDialog(this, "Không thể xóa sản phẩm này!");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
                }
            }
        });

        btnCapnhat.addActionListener(e -> {
            try {
                String maQuanAoStr = txtMaquanao.getText().trim();
                String tenQuanAo = txtTenquanao.getText().trim();
                String soLuongStr = txtSoluong.getText().trim();
                String donGiaNhapStr = txtDongianhap.getText().trim();
                String donGiaBanStr = txtDongiaban.getText().trim();
                String anh = txtAnh.getText().trim();

                if (maQuanAoStr.isEmpty() || tenQuanAo.isEmpty() || soLuongStr.isEmpty() ||
                    donGiaNhapStr.isEmpty() || donGiaBanStr.isEmpty() || anh.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin sản phẩm!");
                    return;
                }

                int maQuanAo = Integer.parseInt(maQuanAoStr);
                int soLuong = Integer.parseInt(soLuongStr);
                java.math.BigDecimal donGiaNhap = new java.math.BigDecimal(donGiaNhapStr);
                java.math.BigDecimal donGiaBan = new java.math.BigDecimal(donGiaBanStr);

                Model.SanPham sp = new Model.SanPham();
                sp.setMaQuanAo(maQuanAo);
                sp.setTenQuanAo(tenQuanAo);
                sp.setSoLuong(soLuong);
                sp.setAnh(anh);
                sp.setDonGiaNhap(donGiaNhap);
                sp.setDonGiaBan(donGiaBan);

                Integer idLoai = getSelectedLoaiId();
                if (idLoai != null) sp.setTheLoai(danhMucDAO.getTheLoaiById(idLoai));
                Integer idCo = getSelectedCoId();
                if (idCo != null) sp.setCo(danhMucDAO.getCoById(idCo));
                Integer idChatLieu = getSelectedChatLieuId();
                if (idChatLieu != null) sp.setChatLieu(danhMucDAO.getChatLieuById(idChatLieu));
                Integer idMau = getSelectedMauId();
                if (idMau != null) sp.setMau(danhMucDAO.getMauById(idMau));
                Integer idDoiTuong = getSelectedDoiTuongId();
                if (idDoiTuong != null) sp.setDoiTuong(danhMucDAO.getDoiTuongById(idDoiTuong));
                Integer idMua = getSelectedMuaId();
                if (idMua != null) sp.setMua(danhMucDAO.getMuaById(idMua));
                Integer idNSX = getSelectedNSXId();
                if (idNSX != null) sp.setNoiSanXuat(danhMucDAO.getNoiSanXuatById(idNSX));

                if (productService.updateProduct(sp)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công!");
                    doDuLieuSanPham();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thất bại!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        btnLoc.addActionListener(e -> {
            Integer idLoai = getSelectedLoaiId();
            Integer idCo = getSelectedCoId();
            Integer idChatLieu = getSelectedChatLieuId();
            Integer idDoiTuong = getSelectedDoiTuongId();
            Integer idMau = getSelectedMauId();
            Integer idMua = getSelectedMuaId();
            Integer idNSX = getSelectedNSXId();

            java.util.List<Model.SanPham> filtered = productService.filterProducts(idLoai, idCo, idChatLieu, idDoiTuong, idMau, idMua, idNSX);
            DefaultTableModel model = new DefaultTableModel(
                new String[] {
                    "Mã quần áo", "Tên quần áo", "Thể loại", "Cỡ", "Chất liệu", 
                    "Màu", "Đối tượng", "Mùa", "Nơi sản xuất", 
                    "Đơn giá bán", "Đơn giá nhập", "Số lượng", "Ảnh"
                }, 0
            );
            tblSanpham.setModel(model);
            for (Model.SanPham sp : filtered) {
                model.addRow(new Object[] {
                    sp.getMaQuanAo(),
                    sp.getTenQuanAo(),
                    sp.getTheLoai() != null ? sp.getTheLoai().getTenLoai() : "",
                    sp.getCo() != null ? sp.getCo().getTenCo() : "",
                    sp.getChatLieu() != null ? sp.getChatLieu().getTenChatLieu() : "",
                    sp.getMau() != null ? sp.getMau().getTenMau() : "",
                    sp.getDoiTuong() != null ? sp.getDoiTuong().getTenDoiTuong() : "",
                    sp.getMua() != null ? sp.getMua().getTenMua() : "",
                    sp.getNoiSanXuat() != null ? sp.getNoiSanXuat().getTenNSX() : "",
                    sp.getDonGiaBan(),
                    sp.getDonGiaNhap(),
                    sp.getSoLuong(),
                    sp.getAnh()
                });
            }
            GUI.theme.UITheme.styleTable(tblSanpham);
        });

        btnLammoi.addActionListener(e -> {
            doDuLieuSanPham();

            boxLoai.setSelectedIndex(-1);
            boxChatlieu.setSelectedIndex(-1);
            boxDoituong.setSelectedIndex(-1);
            boxKichco.setSelectedIndex(-1);
            boxMua.setSelectedIndex(-1);
            boxMau.setSelectedIndex(-1);
            boxNoisanxuat.setSelectedIndex(-1);

            txtMaquanao.setText("");
            txtTenquanao.setText("");
            txtDongianhap.setText("");
            txtDongiaban.setText("");
            txtSoluong.setText("");
            txtAnh.setText("");

            hienThiAnh("src/GUI/icons/Ảnh chụp màn hình 2025-05-07 194832.png");  
        });

        btnMo.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png"
            );
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();
                txtAnh.setText(filePath);
                hienThiAnh(filePath);
            }
        });                              
    }

    private void loadComboBoxData() {
        boxLoai.removeAllItems();
        danhMucDAO.getAllTheLoai().forEach(item -> boxLoai.addItem(item.getTenLoai()));
        boxChatlieu.removeAllItems();
        danhMucDAO.getAllChatLieu().forEach(item -> boxChatlieu.addItem(item.getTenChatLieu()));
        boxDoituong.removeAllItems();
        danhMucDAO.getAllDoiTuong().forEach(item -> boxDoituong.addItem(item.getTenDoiTuong()));
        boxKichco.removeAllItems();
        danhMucDAO.getAllCo().forEach(item -> boxKichco.addItem(item.getTenCo()));
        boxMua.removeAllItems();
        danhMucDAO.getAllMua().forEach(item -> boxMua.addItem(item.getTenMua()));
        boxMau.removeAllItems();
        danhMucDAO.getAllMau().forEach(item -> boxMau.addItem(item.getTenMau()));
        boxNoisanxuat.removeAllItems();
        danhMucDAO.getAllNoiSanXuat().forEach(item -> boxNoisanxuat.addItem(item.getTenNSX()));
    }

    private Integer getSelectedLoaiId() {
        String sel = (String) boxLoai.getSelectedItem();
        if (sel == null) return null;
        return danhMucDAO.getAllTheLoai().stream().filter(x -> x.getTenLoai().equalsIgnoreCase(sel)).map(Model.TheLoai::getMaLoai).findFirst().orElse(null);
    }

    private Integer getSelectedCoId() {
        String sel = (String) boxKichco.getSelectedItem();
        if (sel == null) return null;
        return danhMucDAO.getAllCo().stream().filter(x -> x.getTenCo().equalsIgnoreCase(sel)).map(Model.Co::getMaCo).findFirst().orElse(null);
    }

    private Integer getSelectedChatLieuId() {
        String sel = (String) boxChatlieu.getSelectedItem();
        if (sel == null) return null;
        return danhMucDAO.getAllChatLieu().stream().filter(x -> x.getTenChatLieu().equalsIgnoreCase(sel)).map(Model.ChatLieu::getMaChatLieu).findFirst().orElse(null);
    }

    private Integer getSelectedMauId() {
        String sel = (String) boxMau.getSelectedItem();
        if (sel == null) return null;
        return danhMucDAO.getAllMau().stream().filter(x -> x.getTenMau().equalsIgnoreCase(sel)).map(Model.Mau::getMaMau).findFirst().orElse(null);
    }

    private Integer getSelectedDoiTuongId() {
        String sel = (String) boxDoituong.getSelectedItem();
        if (sel == null) return null;
        return danhMucDAO.getAllDoiTuong().stream().filter(x -> x.getTenDoiTuong().equalsIgnoreCase(sel)).map(Model.DoiTuong::getMaDoiTuong).findFirst().orElse(null);
    }

    private Integer getSelectedMuaId() {
        String sel = (String) boxMua.getSelectedItem();
        if (sel == null) return null;
        return danhMucDAO.getAllMua().stream().filter(x -> x.getTenMua().equalsIgnoreCase(sel)).map(Model.Mua::getMaMua).findFirst().orElse(null);
    }

    private Integer getSelectedNSXId() {
        String sel = (String) boxNoisanxuat.getSelectedItem();
        if (sel == null) return null;
        return danhMucDAO.getAllNoiSanXuat().stream().filter(x -> x.getTenNSX().equalsIgnoreCase(sel)).map(Model.NoiSanXuat::getMaNSX).findFirst().orElse(null);
    }

    public void hienThiChiTietSanPham() {
        int row = tblSanpham.getSelectedRow();
        if (row == -1) return; // không có dòng nào được chọn
    
        txtMaquanao.setText(tblSanpham.getValueAt(row, 0).toString());
        txtTenquanao.setText(tblSanpham.getValueAt(row, 1).toString());
        boxLoai.setSelectedItem(tblSanpham.getValueAt(row, 2).toString());
        boxKichco.setSelectedItem(tblSanpham.getValueAt(row, 3).toString());
        boxChatlieu.setSelectedItem(tblSanpham.getValueAt(row, 4).toString());
        boxMau.setSelectedItem(tblSanpham.getValueAt(row, 5).toString());
        boxDoituong.setSelectedItem(tblSanpham.getValueAt(row, 6).toString());
        boxMua.setSelectedItem(tblSanpham.getValueAt(row, 7).toString());
        boxNoisanxuat.setSelectedItem(tblSanpham.getValueAt(row, 8).toString());
        txtDongiaban.setText(tblSanpham.getValueAt(row, 9).toString());
        txtDongianhap.setText(tblSanpham.getValueAt(row, 10).toString());
        txtSoluong.setText(tblSanpham.getValueAt(row, 11).toString());
        txtAnh.setText(tblSanpham.getValueAt(row, 12).toString());
    
        // Gọi hàm hiển thị ảnh
        hienThiAnh(tblSanpham.getValueAt(row, 12).toString());
    }
    public void hienThiAnh(String duongDan) {
        try {
            // Đảm bảo đường dẫn hợp lệ (kiểm tra xem tệp có tồn tại không)
            File file = new File(duongDan);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, "Ảnh không tồn tại tại: " + duongDan);
                return;
            }
    
            // Tạo đối tượng ImageIcon từ đường dẫn ảnh
            ImageIcon icon = new ImageIcon(duongDan);
            // Tải ảnh và thay đổi kích thước phù hợp với JPanel (Anh)
            Image img = icon.getImage().getScaledInstance(Anh.getWidth(), Anh.getHeight(), Image.SCALE_SMOOTH);
    
            // Tạo một JLabel chứa ảnh đã thay đổi kích thước
            JLabel lbl = new JLabel(new ImageIcon(img));
    
            // Xóa hết nội dung cũ của JPanel trước khi thêm ảnh mới
            Anh.removeAll();
            Anh.setLayout(new BorderLayout()); // Đảm bảo layout hợp lý
            Anh.add(lbl, BorderLayout.CENTER); // Thêm JLabel vào giữa JPanel
    
            // Cập nhật lại giao diện
            Anh.revalidate();
            Anh.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể hiển thị ảnh: " + duongDan);
        }
    }
    
    public void doDuLieuSanPham() {
        DefaultTableModel model = new DefaultTableModel(
            new String[] {
                "Mã quần áo", "Tên quần áo", "Thể loại", "Cỡ", "Chất liệu", 
                "Màu", "Đối tượng", "Mùa", "Nơi sản xuất", 
                "Đơn giá bán", "Đơn giá nhập", "Số lượng", "Ảnh"
            }, 0
        );
        tblSanpham.setModel(model);

        try {
            java.util.List<Model.SanPham> list = productService.getAllProducts();
            for (Model.SanPham sp : list) {
                Object[] row = new Object[] {
                    sp.getMaQuanAo(),
                    sp.getTenQuanAo(),
                    sp.getTheLoai() != null ? sp.getTheLoai().getTenLoai() : "",
                    sp.getCo() != null ? sp.getCo().getTenCo() : "",
                    sp.getChatLieu() != null ? sp.getChatLieu().getTenChatLieu() : "",
                    sp.getMau() != null ? sp.getMau().getTenMau() : "",
                    sp.getDoiTuong() != null ? sp.getDoiTuong().getTenDoiTuong() : "",
                    sp.getMua() != null ? sp.getMua().getTenMua() : "",
                    sp.getNoiSanXuat() != null ? sp.getNoiSanXuat().getTenNSX() : "",
                    sp.getDonGiaBan(),
                    sp.getDonGiaNhap(),
                    sp.getSoLuong(),
                    sp.getAnh()
                };
                model.addRow(row);
            }
            GUI.theme.UITheme.styleTable(tblSanpham);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu sản phẩm:\n" + e.getMessage());
        }
    }

    private void styleUI() {
        GUI.theme.UITheme.styleContainer(this);
        GUI.theme.UITheme.stylePrimaryButton(btnThem);
        GUI.theme.UITheme.styleButton(btnCapnhat, GUI.theme.UITheme.WARNING, new java.awt.Color(217, 119, 6), java.awt.Color.WHITE);
        GUI.theme.UITheme.styleDangerButton(btnXoa);
        GUI.theme.UITheme.styleSecondaryButton(btnLammoi);
        GUI.theme.UITheme.stylePrimaryButton(btnLoc);
        GUI.theme.UITheme.styleSecondaryButton(btnMo);
        GUI.theme.UITheme.styleTable(tblSanpham);
        Anh.setBackground(java.awt.Color.WHITE);
        Anh.setBorder(javax.swing.BorderFactory.createLineBorder(GUI.theme.UITheme.BORDER_COLOR, 1, true));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSanpham = new javax.swing.JTable();
        lblMaquanao = new javax.swing.JLabel();
        lblTenquanao = new javax.swing.JLabel();
        txtTenquanao = new javax.swing.JTextField();
        txtMaquanao = new javax.swing.JTextField();
        btnXoa = new javax.swing.JButton();
        btnCapnhat = new javax.swing.JButton();
        txtDongianhap = new javax.swing.JTextField();
        lblDongianhap = new javax.swing.JLabel();
        boxLoai = new javax.swing.JComboBox<>();
        lblLoai = new javax.swing.JLabel();
        btnThem = new javax.swing.JButton();
        boxChatlieu = new javax.swing.JComboBox<>();
        lblChatlieu = new javax.swing.JLabel();
        boxDoituong = new javax.swing.JComboBox<>();
        lblDoituong = new javax.swing.JLabel();
        boxKichco = new javax.swing.JComboBox<>();
        lblKichco = new javax.swing.JLabel();
        boxMua = new javax.swing.JComboBox<>();
        lblMua = new javax.swing.JLabel();
        boxMau = new javax.swing.JComboBox<>();
        lblMau = new javax.swing.JLabel();
        boxNoisanxuat = new javax.swing.JComboBox<>();
        lblNoisanxuat = new javax.swing.JLabel();
        txtDongiaban = new javax.swing.JTextField();
        lblDongiaban = new javax.swing.JLabel();
        txtSoluong = new javax.swing.JTextField();
        lblSoluong = new javax.swing.JLabel();
        txtAnh = new javax.swing.JTextField();
        lblAnh = new javax.swing.JLabel();
        Anh = new javax.swing.JPanel();
        btnMo = new javax.swing.JButton();
        btnLammoi = new javax.swing.JButton();
        btnLoc = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(173, 216, 230));
        
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("DANH SÁCH SẢN PHẨM");

        tblSanpham.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Mã quần áo", "Tên quần áo", "Thể loại", "Chất liệu", "Đối tượng",
                "Kích cỡ", "Màu", "Mùa", "Nơi sản xuất", "Đơn giá bán",
                "Đơn giá nhập", "Số lượng", "Ảnh"
            }
        ));
        jScrollPane1.setViewportView(tblSanpham);

        lblMaquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMaquanao.setText("Mã quần áo:");

        lblTenquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTenquanao.setText("Tên quần áo:");

        btnXoa.setText("Xóa");

        btnCapnhat.setText("Cập nhật");

        lblDongianhap.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDongianhap.setText("Đơn giá nhập:");

        boxLoai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxLoai.setPreferredSize(new java.awt.Dimension(100, 28));

        lblLoai.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblLoai.setText("Loại:");

        btnThem.setText("Thêm");

        boxChatlieu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxChatlieu.setPreferredSize(new java.awt.Dimension(100, 28));

        lblChatlieu.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblChatlieu.setText("Chất liệu:");

        boxDoituong.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxDoituong.setPreferredSize(new java.awt.Dimension(100, 28));

        lblDoituong.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDoituong.setText("Đối tượng:");

        boxKichco.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxKichco.setPreferredSize(new java.awt.Dimension(100, 28));

        lblKichco.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblKichco.setText("Kích cỡ:");

        boxMua.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxMua.setPreferredSize(new java.awt.Dimension(100, 28));

        lblMua.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMua.setText("Mùa:");

        boxMau.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxMau.setPreferredSize(new java.awt.Dimension(100, 28));

        lblMau.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMau.setText("Màu:");

        boxNoisanxuat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxNoisanxuat.setPreferredSize(new java.awt.Dimension(100, 28));

        lblNoisanxuat.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblNoisanxuat.setText("Nơi sản xuất:");

        lblDongiaban.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDongiaban.setText("Đơn giá bán:");

        lblSoluong.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblSoluong.setText("Số lượng:");

        lblAnh.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblAnh.setText("Ảnh:");

        javax.swing.GroupLayout AnhLayout = new javax.swing.GroupLayout(Anh);
        Anh.setLayout(AnhLayout);
        AnhLayout.setHorizontalGroup(
            AnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 129, Short.MAX_VALUE)
        );
        AnhLayout.setVerticalGroup(
            AnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 191, Short.MAX_VALUE)
        );

        btnMo.setText("Mở");

        btnLammoi.setText("Làm mới");

        btnLoc.setText("Lọc");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(38, 38, 38)
                                            .addComponent(lblMaquanao)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(101, 101, 101)
                                            .addComponent(lblTenquanao)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtTenquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(30, 30, 30)
                                            .addComponent(lblDongianhap)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtDongianhap, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(56, 56, 56)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(lblChatlieu)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(boxChatlieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(lblLoai)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(boxLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(lblDoituong)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(boxDoituong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(18, 18, 18)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(66, 66, 66)
                                            .addComponent(lblDongiaban)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtDongiaban, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(85, 85, 85)
                                            .addComponent(lblSoluong)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtSoluong, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(lblKichco)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(boxKichco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(lblMua)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(boxMua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(lblNoisanxuat)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(boxNoisanxuat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(lblMau)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(boxMau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblAnh)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(Anh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(12, 12, 12))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(186, 186, 186)
                                .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(65, 65, 65)
                                .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(68, 68, 68)
                                .addComponent(btnCapnhat, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(61, 61, 61)
                                .addComponent(btnLammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 978, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(313, 313, 313)
                        .addComponent(jLabel1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnMo, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMaquanao)
                            .addComponent(lblTenquanao)
                            .addComponent(txtTenquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(boxLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblLoai))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(boxChatlieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblChatlieu))
                                .addGap(19, 19, 19)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(boxDoituong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblDoituong)))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(boxKichco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(lblKichco))
                                            .addGap(65, 65, 65))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(65, 65, 65)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(boxMua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(lblMua))))
                                    .addGap(5, 5, 5))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(70, 70, 70)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(boxNoisanxuat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblNoisanxuat)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(boxMau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblMau)
                                            .addComponent(lblAnh)
                                            .addComponent(txtAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(63, 63, 63)))))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblDongianhap)
                                    .addComponent(txtDongianhap, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblDongiaban)
                                    .addComponent(txtDongiaban, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblSoluong)
                                        .addComponent(txtSoluong, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(44, 44, 44))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Anh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMo, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCapnhat, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(156, 156, 156))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 982, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 607, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Anh;
    private javax.swing.JComboBox<String> boxChatlieu;
    private javax.swing.JComboBox<String> boxDoituong;
    private javax.swing.JComboBox<String> boxKichco;
    private javax.swing.JComboBox<String> boxLoai;
    private javax.swing.JComboBox<String> boxMau;
    private javax.swing.JComboBox<String> boxMua;
    private javax.swing.JComboBox<String> boxNoisanxuat;
    private javax.swing.JButton btnCapnhat;
    private javax.swing.JButton btnLammoi;
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnMo;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAnh;
    private javax.swing.JLabel lblChatlieu;
    private javax.swing.JLabel lblDoituong;
    private javax.swing.JLabel lblDongiaban;
    private javax.swing.JLabel lblDongianhap;
    private javax.swing.JLabel lblKichco;
    private javax.swing.JLabel lblLoai;
    private javax.swing.JLabel lblMaquanao;
    private javax.swing.JLabel lblMau;
    private javax.swing.JLabel lblMua;
    private javax.swing.JLabel lblNoisanxuat;
    private javax.swing.JLabel lblSoluong;
    private javax.swing.JLabel lblTenquanao;
    private javax.swing.JTable tblSanpham;
    private javax.swing.JTextField txtAnh;
    private javax.swing.JTextField txtDongiaban;
    private javax.swing.JTextField txtDongianhap;
    private javax.swing.JTextField txtMaquanao;
    private javax.swing.JTextField txtSoluong;
    private javax.swing.JTextField txtTenquanao;
    // End of variables declaration//GEN-END:variables
}
