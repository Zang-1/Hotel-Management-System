@echo off
chcp 65001 > nul
echo ============================================
echo   Grand Azure Hotel Management System
echo ============================================
echo.

echo [1/2] Compiling Java source files...
if not exist "bin" mkdir bin

javac -encoding UTF-8 -cp "lib/*" -d bin ^
  src/com/hotel/model/Room.java ^
  src/com/hotel/model/StandardRoom.java ^
  src/com/hotel/model/DeluxeRoom.java ^
  src/com/hotel/model/SuiteRoom.java ^
  src/com/hotel/model/Guest.java ^
  src/com/hotel/model/Reservation.java ^
  src/com/hotel/model/Staff.java ^
  src/com/hotel/exception/DuplicateDataException.java ^
  src/com/hotel/exception/ValidationException.java ^
  src/com/hotel/util/FileHandler.java ^
  src/com/hotel/util/LangManager.java ^
  src/com/hotel/dao/RoomDAO.java ^
  src/com/hotel/dao/GuestDAO.java ^
  src/com/hotel/dao/ReservationDAO.java ^
  src/com/hotel/dao/StaffDAO.java ^
  src/com/hotel/manager/RoomManager.java ^
  src/com/hotel/manager/GuestManager.java ^
  src/com/hotel/manager/ReservationManager.java ^
  src/com/hotel/manager/StaffManager.java ^
  src/com/hotel/ui/UIConstants.java ^
  src/com/hotel/ui/UIHelper.java ^
  src/com/hotel/ui/TableActionCell.java ^
  src/com/hotel/ui/BasePanel.java ^
  src/com/hotel/ui/LoginFrame.java ^
  src/com/hotel/ui/DashboardPanel.java ^
  src/com/hotel/ui/RoomPanel.java ^
  src/com/hotel/ui/GuestPanel.java ^
  src/com/hotel/ui/ReservationPanel.java ^
  src/com/hotel/ui/StaffPanel.java ^
  src/com/hotel/ui/BillingPanel.java ^
  src/com/hotel/ui/ReportPanel.java ^
  src/com/hotel/ui/MainFrame.java ^
  src/com/hotel/Main.java

if %errorlevel% neq 0 (
  echo.
  echo [ERROR] Compilation failed! Please check errors above.
  pause
  exit /b 1
)

echo [SUCCESS] Compilation successful!
echo.
echo [2/2] Launching application...
echo.
java -cp "bin;lib/*" com.hotel.Main

pause
