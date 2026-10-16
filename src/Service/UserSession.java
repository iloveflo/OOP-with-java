package Service;

import Model.LoaiTaiKhoan;
import Model.Nguoi;
import Model.TaiKhoan;

public class UserSession {
    private static UserSession instance;

    private TaiKhoan currentAccount;
    private Nguoi currentUser;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public synchronized void startSession(TaiKhoan account, Nguoi user) {
        this.currentAccount = account;
        this.currentUser = user;
    }

    public synchronized void clearSession() {
        this.currentAccount = null;
        this.currentUser = null;
    }

    public synchronized boolean isLoggedIn() {
        return this.currentAccount != null;
    }

    public synchronized TaiKhoan getCurrentAccount() {
        return currentAccount;
    }

    public synchronized Nguoi getCurrentUser() {
        return currentUser;
    }

    public synchronized String getMaTaiKhoan() {
        return currentAccount != null ? currentAccount.getMaTaiKhoan() : "";
    }

    public synchronized LoaiTaiKhoan getRole() {
        return currentAccount != null ? currentAccount.getLoaiTaiKhoan() : null;
    }

    public synchronized String getLoaiTaiKhoan() {
        return currentAccount != null && currentAccount.getLoaiTaiKhoan() != null ? currentAccount.getLoaiTaiKhoan().getValue() : "";
    }

    public synchronized boolean hasRole(LoaiTaiKhoan role) {
        return currentAccount != null && currentAccount.getLoaiTaiKhoan() == role;
    }
}

