package com.hotel.util;

import java.util.HashMap;
import java.util.Map;

public class LangManager {
    private static String currentLang = "vi"; // Mặc định là Tiếng Việt
    
    // Từ điển song ngữ
    private static final Map<String, String> dictVi = new HashMap<>();
    private static final Map<String, String> dictEn = new HashMap<>();

    static {
        // Dữ liệu Tiếng Việt
        dictVi.put("menu.dashboard", "Tổng quan");
        dictVi.put("menu.rooms", "Phòng");
        dictVi.put("menu.guests", "Khách hàng");
        dictVi.put("menu.reservations", "Đặt phòng");
        dictVi.put("menu.billing", "Thanh toán");
        dictVi.put("menu.staff", "Nhân sự");
        dictVi.put("menu.reports", "Báo cáo");
        dictVi.put("menu.logout", "Đăng xuất");
        dictVi.put("dashboard.title", "Dashboard");
        dictVi.put("dashboard.sub", "Tổng quan hệ thống");
        dictVi.put("dashboard.total_rooms", "Tổng phòng");
        dictVi.put("dashboard.occupied", "Khách đang lưu trú");
        dictVi.put("dashboard.total_guests", "Tổng khách hàng");
        dictVi.put("dashboard.revenue", "Doanh thu");
        dictVi.put("dashboard.recent_res", "PHIẾU ĐẶT PHÒNG GẦN ĐÂY");
        dictVi.put("dashboard.room_status", "TRẠNG THÁI PHÒNG");
        dictVi.put("status.empty", "Trống");
        dictVi.put("status.occupied", "Có khách");
        dictVi.put("status.pending", "Chờ nhận");
        dictVi.put("status.checked_in", "Đã nhận");
        dictVi.put("status.checked_out", "Đã trả");
        dictVi.put("status.cancelled", "Đã hủy");
        dictVi.put("btn.add", "Thêm");
        dictVi.put("btn.edit", "Sửa");
        dictVi.put("btn.delete", "Xóa");
        dictVi.put("btn.clear", "Xóa form");
        dictVi.put("btn.save", "Lưu sửa");
        dictVi.put("btn.checkout", "Thanh toán");
        dictVi.put("lbl.choose_guest", "Chọn khách hàng *");
        dictVi.put("lbl.choose_room", "Chọn phòng *");
        dictVi.put("lbl.checkin_date", "Ngày Check-in (dd/MM/yyyy) *");
        dictVi.put("lbl.checkout_date", "Ngày Check-out (dd/MM/yyyy) *");
        dictVi.put("lbl.room_id", "Mã phòng *");
        dictVi.put("lbl.room_type", "Loại phòng");
        dictVi.put("lbl.status", "Trạng thái");
        dictVi.put("lbl.action", "Thao tác");
        dictVi.put("lbl.res_id", "Mã đặt");
        dictVi.put("lbl.guest", "Khách hàng");
        dictVi.put("lbl.room", "Phòng");
        dictVi.put("lbl.checkin", "Check-in");
        dictVi.put("lbl.checkout", "Check-out");
        dictVi.put("sub.res", "Quản lý khách hàng đặt phòng và nhận/trả phòng");
        dictVi.put("sub.room", "Thêm, sửa, xóa và tìm kiếm phòng khách sạn");
        dictVi.put("sub.guest", "Quản lý thông tin khách lưu trú");
        dictVi.put("sub.staff", "Quản lý nhân sự và ca làm việc");
        dictVi.put("sub.billing", "Thanh toán hóa đơn và in biên lai");
        dictVi.put("sub.report", "Thống kê tổng hợp hoạt động khách sạn");
        dictVi.put("lbl.rev_by_room", "DOANH THU THEO LOẠI PHÒNG");
        dictVi.put("lbl.status_dist", "PHÂN BỔ TRẠNG THÁI");
        dictVi.put("lbl.total_rev", "TỔNG DOANH THU");
        dictVi.put("lbl.occ_rate", "TỈ LỆ LẤP ĐẦY");
        dictVi.put("lbl.total_guests_upper", "TỔNG KHÁCH HÀNG");
        dictVi.put("lbl.total_res", "TỔNG PHIẾU ĐẶT");
        dictVi.put("lbl.room_details", "CHI TIẾT TỪNG PHÒNG");
        dictVi.put("lbl.nights_total", "Tổng đêm");
        dictVi.put("lbl.res_short", "phiếu");
        dictVi.put("lbl.guest_id", "Mã KH *");
        dictVi.put("lbl.name", "Họ tên *");
        dictVi.put("lbl.phone", "Điện thoại *");
        dictVi.put("lbl.id_card", "CCCD/CMND");
        dictVi.put("lbl.email", "Email");
        dictVi.put("lbl.address", "Địa chỉ");
        dictVi.put("lbl.staff_id", "Mã nhân viên *");
        dictVi.put("lbl.role", "Chức vụ");
        dictVi.put("lbl.salary", "Lương");
        dictVi.put("lbl.shift", "Ca làm việc");
        dictVi.put("lbl.pay_method", "Phương thức thanh toán");
        dictVi.put("lbl.total_amount", "Tổng tiền");
        dictVi.put("lbl.amount_paid", "Khách đưa");
        dictVi.put("lbl.change", "Tiền thừa");
        dictVi.put("btn.print", "In Hóa Đơn");
        dictVi.put("err.empty_fields", "Vui lòng nhập đầy đủ các trường bắt buộc!");
        dictVi.put("err.id_exists", "Mã này đã tồn tại!");
        dictVi.put("msg.add_success", "Thêm thành công!");
        dictVi.put("msg.update_success", "Cập nhật thành công!");
        dictVi.put("msg.delete_success", "Xóa thành công!");

        // English Data
        dictEn.put("menu.dashboard", "Dashboard");
        dictEn.put("menu.rooms", "Rooms");
        dictEn.put("menu.guests", "Guests");
        dictEn.put("menu.reservations", "Reservations");
        dictEn.put("menu.billing", "Billing");
        dictEn.put("menu.staff", "Staff");
        dictEn.put("menu.reports", "Reports");
        dictEn.put("menu.logout", "Logout");
        dictEn.put("dashboard.title", "Dashboard");
        dictEn.put("dashboard.sub", "System Overview");
        dictEn.put("dashboard.total_rooms", "Total Rooms");
        dictEn.put("dashboard.occupied", "Occupied Rooms");
        dictEn.put("dashboard.total_guests", "Total Guests");
        dictEn.put("dashboard.revenue", "Revenue");
        dictEn.put("dashboard.recent_res", "RECENT RESERVATIONS");
        dictEn.put("dashboard.room_status", "ROOM STATUS");
        dictEn.put("status.empty", "Empty");
        dictEn.put("status.occupied", "Occupied");
        dictEn.put("status.pending", "Pending");
        dictEn.put("status.checked_in", "Checked In");
        dictEn.put("status.checked_out", "Checked Out");
        dictEn.put("status.cancelled", "Cancelled");
        dictEn.put("btn.add", "Add");
        dictEn.put("btn.edit", "Edit");
        dictEn.put("btn.delete", "Delete");
        dictEn.put("btn.clear", "Clear");
        dictEn.put("btn.save", "Save");
        dictEn.put("btn.checkout", "Checkout");
        dictEn.put("lbl.choose_guest", "Select Guest *");
        dictEn.put("lbl.choose_room", "Select Room *");
        dictEn.put("lbl.checkin_date", "Check-in Date (dd/MM/yyyy) *");
        dictEn.put("lbl.checkout_date", "Check-out Date (dd/MM/yyyy) *");
        dictEn.put("lbl.room_id", "Room ID *");
        dictEn.put("lbl.room_type", "Room Type");
        dictEn.put("lbl.status", "Status");
        dictEn.put("lbl.action", "Action");
        dictEn.put("lbl.res_id", "Res ID");
        dictEn.put("lbl.guest", "Guest");
        dictEn.put("lbl.room", "Room");
        dictEn.put("lbl.checkin", "Check-in");
        dictEn.put("lbl.checkout", "Check-out");
        dictEn.put("sub.res", "Manage reservations, check-ins and check-outs");
        dictEn.put("sub.room", "Add, edit, delete and search hotel rooms");
        dictEn.put("sub.guest", "Manage guest information");
        dictEn.put("sub.staff", "Manage staff and work shifts");
        dictEn.put("sub.billing", "Checkout and print receipts");
        dictEn.put("sub.report", "Comprehensive statistics of hotel operations");
        dictEn.put("lbl.rev_by_room", "REVENUE BY ROOM TYPE");
        dictEn.put("lbl.status_dist", "STATUS DISTRIBUTION");
        dictEn.put("lbl.total_rev", "TOTAL REVENUE");
        dictEn.put("lbl.occ_rate", "OCCUPANCY RATE");
        dictEn.put("lbl.total_guests_upper", "TOTAL GUESTS");
        dictEn.put("lbl.total_res", "TOTAL RESERVATIONS");
        dictEn.put("lbl.room_details", "ROOM DETAILS");
        dictEn.put("lbl.nights_total", "Total Nights");
        dictEn.put("lbl.res_short", "res");
        dictEn.put("lbl.guest_id", "Guest ID *");
        dictEn.put("lbl.name", "Full Name *");
        dictEn.put("lbl.phone", "Phone *");
        dictEn.put("lbl.id_card", "ID Card");
        dictEn.put("lbl.email", "Email");
        dictEn.put("lbl.address", "Address");
        dictEn.put("lbl.staff_id", "Staff ID *");
        dictEn.put("lbl.role", "Role");
        dictEn.put("lbl.salary", "Salary");
        dictEn.put("lbl.shift", "Shift");
        dictEn.put("lbl.pay_method", "Payment Method");
        dictEn.put("lbl.total_amount", "Total Amount");
        dictEn.put("lbl.amount_paid", "Amount Paid");
        dictEn.put("lbl.change", "Change");
        dictEn.put("btn.print", "Print Receipt");
        dictEn.put("err.empty_fields", "Please fill in all required fields!");
        dictEn.put("err.id_exists", "ID already exists!");
        dictEn.put("msg.add_success", "Added successfully!");
        dictEn.put("msg.update_success", "Updated successfully!");
        dictEn.put("msg.delete_success", "Deleted successfully!");
    }

    public static void setLanguage(String lang) {
        if (lang.equals("en") || lang.equals("vi")) {
            currentLang = lang;
        }
    }

    public static String getLanguage() {
        return currentLang;
    }

    public static String getString(String key) {
        Map<String, String> dict = currentLang.equals("en") ? dictEn : dictVi;
        return dict.getOrDefault(key, key);
    }

    public static String formatCurrency(double amount) {
        if (currentLang.equals("en")) {
            return String.format("%,.0f VND", amount);
        } else {
            return String.format("%,.0f VNĐ", amount);
        }
    }
}
