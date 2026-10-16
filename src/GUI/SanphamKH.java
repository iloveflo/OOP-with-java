/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;

import java.awt.*;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Neo 16
 */
public class SanphamKH extends javax.swing.JPanel implements Refreshable {

    private final Service.IProductService productService = new Service.impl.ProductServiceImpl();
    private final DAO.IDanhMucDAO danhMucDAO = new DAO.DanhMucDAOImpl();
    private final Service.IOrderService orderService = new Service.impl.OrderServiceImpl();

    @Override
    public void refresh() {
        doDuLieuSanPham();
    }

    /**
     * Creates new form NVdanhsachsPham1
     */
    public SanphamKH() {
        initComponents();
        styleUI();

        loadComboBoxData();
        doDuLieuSanPham();

        tblDsachSpham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                hienThiChiTietSanPham();
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
                    "Đơn giá bán", "Đơn giá nhập", "Số lượng"
                }, 0
            );
            tblDsachSpham.setModel(model);
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
                    sp.getSoLuong()
                });
            }
            GUI.theme.UITheme.styleTable(tblDsachSpham);
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
            txtSoluongdat.setText("");

            hienThiAnh("src/GUI/icons/Ảnh chụp màn hình 2025-05-07 194832.png");  
        });

        btnThemgiohang.addActionListener(e -> {
            String maQuanAoStr = txtMaquanao.getText().trim();
            String soLuongDatStr = txtSoluongdat.getText().trim();

            if (maQuanAoStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!");
                return;
            }
            if (soLuongDatStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng đặt!");
                return;
            }

            try {
                int maQuanAo = Integer.parseInt(maQuanAoStr);
                int soLuongDat = Integer.parseInt(soLuongDatStr);
                String maKH = Service.UserSession.getInstance().getMaTaiKhoan();

                orderService.themVaoGioHang(maKH, maQuanAo, soLuongDat);
                JOptionPane.showMessageDialog(this, "Đã thêm vào giỏ hàng thành công!");

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
                txtSoluongdat.setText("");

                hienThiAnh("src/GUI/icons/Ảnh chụp màn hình 2025-05-07 194832.png");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
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

    public void doDuLieuSanPham() {
        DefaultTableModel model = new DefaultTableModel(
            new String[] {
                "Mã quần áo", "Tên quần áo", "Thể loại", "Cỡ", "Chất liệu", 
                "Màu", "Đối tượng", "Mùa", "Nơi sản xuất", 
                "Đơn giá bán", "Đơn giá nhập", "Số lượng"
            }, 0
        );
        tblDsachSpham.setModel(model);

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
                    sp.getSoLuong()
                };
                model.addRow(row);
            }
            GUI.theme.UITheme.styleTable(tblDsachSpham);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu sản phẩm:\n" + e.getMessage());
        }
    }

    private void styleUI() {
        GUI.theme.UITheme.styleContainer(this);
        GUI.theme.UITheme.stylePrimaryButton(btnLoc);
        GUI.theme.UITheme.styleSuccessButton(btnThemgiohang);
        GUI.theme.UITheme.styleSecondaryButton(btnLammoi);
        GUI.theme.UITheme.styleTable(tblDsachSpham);
        Anh.setBackground(java.awt.Color.WHITE);
        Anh.setBorder(javax.swing.BorderFactory.createLineBorder(GUI.theme.UITheme.BORDER_COLOR, 1, true));
    }

    public void hienThiChiTietSanPham() {
        int row = tblDsachSpham.getSelectedRow();
        if (row == -1) return;

        String maQuanAoStr = tblDsachSpham.getValueAt(row, 0).toString();
        txtMaquanao.setText(maQuanAoStr);
        txtTenquanao.setText(tblDsachSpham.getValueAt(row, 1).toString());
        boxLoai.setSelectedItem(tblDsachSpham.getValueAt(row, 2).toString());
        boxKichco.setSelectedItem(tblDsachSpham.getValueAt(row, 3).toString());
        boxChatlieu.setSelectedItem(tblDsachSpham.getValueAt(row, 4).toString());
        boxMau.setSelectedItem(tblDsachSpham.getValueAt(row, 5).toString());
        boxDoituong.setSelectedItem(tblDsachSpham.getValueAt(row, 6).toString());
        boxMua.setSelectedItem(tblDsachSpham.getValueAt(row, 7).toString());
        boxNoisanxuat.setSelectedItem(tblDsachSpham.getValueAt(row, 8).toString());
        txtDongiaban.setText(tblDsachSpham.getValueAt(row, 9).toString());
        txtDongianhap.setText(tblDsachSpham.getValueAt(row, 10).toString());
        txtSoluong.setText(tblDsachSpham.getValueAt(row, 11).toString());

        try {
            int maQuanAo = Integer.parseInt(maQuanAoStr);
            productService.getProductById(maQuanAo).ifPresent(sp -> {
                if (sp.getAnh() != null && !sp.getAnh().isEmpty()) {
                    hienThiAnh(sp.getAnh());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hienThiAnh(String duongDan) {
        try {
            File file = new File(duongDan);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, "Ảnh không tồn tại tại: " + duongDan);
                return;
            }

            ImageIcon icon = new ImageIcon(duongDan);
            Image img = icon.getImage().getScaledInstance(Anh.getWidth(), Anh.getHeight(), Image.SCALE_SMOOTH);
            JLabel lbl = new JLabel(new ImageIcon(img));
            Anh.removeAll();
            Anh.setLayout(new BorderLayout());
            Anh.add(lbl, BorderLayout.CENTER);
            Anh.revalidate();
            Anh.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể hiển thị ảnh: " + duongDan);
        }
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
        tblDsachSpham = new javax.swing.JTable();
        lblMaquanao = new javax.swing.JLabel();
        lblTenquanao = new javax.swing.JLabel();
        txtTenquanao = new javax.swing.JTextField();
        txtMaquanao = new javax.swing.JTextField();
        txtDongianhap = new javax.swing.JTextField();
        lblDongianhap = new javax.swing.JLabel();
        boxLoai = new javax.swing.JComboBox<>();
        lblLoai = new javax.swing.JLabel();
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
        txtSoluongdat = new javax.swing.JTextField();
        lblSoluong = new javax.swing.JLabel();
        Anh = new javax.swing.JPanel();
        btnLammoi = new javax.swing.JButton();
        btnLoc = new javax.swing.JButton();
        btnThemgiohang = new javax.swing.JButton();
        txtSoluong = new javax.swing.JTextField();
        lblSoluong1 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(173, 216, 230));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("DANH SÁCH SẢN PHẨM");

        tblDsachSpham.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblDsachSpham);

        lblMaquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMaquanao.setText("Mã quần áo:");

        lblTenquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTenquanao.setText("Tên quần áo:");

        lblDongianhap.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDongianhap.setText("Đơn giá nhập:");

        boxLoai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        boxLoai.setPreferredSize(new java.awt.Dimension(100, 28));

        lblLoai.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblLoai.setText("Loại:");

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
        lblSoluong.setText("Số lượng còn lại:");

        javax.swing.GroupLayout AnhLayout = new javax.swing.GroupLayout(Anh);
        Anh.setLayout(AnhLayout);
        AnhLayout.setHorizontalGroup(
            AnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 142, Short.MAX_VALUE)
        );
        AnhLayout.setVerticalGroup(
            AnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 201, Short.MAX_VALUE)
        );

        txtSoluongdat.setPreferredSize(new Dimension(30, 10));

        btnLammoi.setText("Làm mới");

        btnLoc.setText("Lọc");

        btnThemgiohang.setText("Thêm giỏ hàng");

        lblSoluong1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblSoluong1.setText("Số lượng đặt:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 967, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(327, 327, 327)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(46, 46, 46)
                                        .addComponent(lblMaquanao)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(96, 96, 96)
                                        .addComponent(lblTenquanao)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtTenquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(60, 60, 60)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(lblChatlieu)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(boxChatlieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(lblDoituong)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(boxDoituong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(119, 119, 119)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(lblMua)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(boxMua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(lblNoisanxuat)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(boxNoisanxuat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(lblMau)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(boxMau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(110, 110, 110)
                                        .addComponent(lblLoai)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(boxLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(105, 105, 105)
                                        .addComponent(lblKichco)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(boxKichco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(lblDongianhap)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDongianhap, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblDongiaban)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDongiaban, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblSoluong)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSoluong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(42, 42, 42)))
                        .addComponent(Anh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblSoluong1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSoluongdat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnThemgiohang)
                .addGap(167, 167, 167)
                .addComponent(btnLammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(260, 260, 260))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(Anh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(102, 102, 102)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(boxKichco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblKichco)
                                            .addComponent(boxMau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblMau))
                                        .addGap(87, 87, 87))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(boxMua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblMua)
                                            .addComponent(boxNoisanxuat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblNoisanxuat)
                                            .addComponent(boxDoituong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblDoituong))
                                        .addGap(33, 33, 33)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblDongianhap)
                                    .addComponent(txtDongianhap, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblDongiaban)
                                    .addComponent(txtDongiaban, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblSoluong)
                                    .addComponent(txtSoluong, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblMaquanao)
                                    .addComponent(lblTenquanao)
                                    .addComponent(txtTenquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(boxLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblLoai))
                                .addGap(14, 14, 14)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(boxChatlieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblChatlieu))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThemgiohang, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSoluongdat, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSoluong1))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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
    private javax.swing.JButton btnLammoi;
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnThemgiohang;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
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
    private javax.swing.JLabel lblSoluong1;
    private javax.swing.JLabel lblTenquanao;
    private javax.swing.JTable tblDsachSpham;
    private javax.swing.JTextField txtDongiaban;
    private javax.swing.JTextField txtDongianhap;
    private javax.swing.JTextField txtMaquanao;
    private javax.swing.JTextField txtSoluong;
    private javax.swing.JTextField txtSoluongdat;
    private javax.swing.JTextField txtTenquanao;
    // End of variables declaration//GEN-END:variables
}
