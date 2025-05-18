import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO {

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public List<Branch> getAllBranches() throws SQLException {
        List<Branch> branches = new ArrayList<>();
        String sql = "SELECT BRANCHID, BNAME, BPHONE, AID FROM BRANCH";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("BRANCHID");
                String name = rs.getString("BNAME");
                String phone = rs.getString("BPHONE");
                int adminId = rs.getInt("AID");

                branches.add(new Branch(id, name, phone, adminId));
            }
        }

        return branches;
    }

    public List<Branch> searchBranches(String name) throws SQLException {
        List<Branch> branches = new ArrayList<>();
        String sql = "SELECT BRANCHID, BNAME, BPHONE, AID FROM BRANCH WHERE BNAME LIKE ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("BRANCHID");
                    String bname = rs.getString("BNAME");
                    String phone = rs.getString("BPHONE");
                    int adminId = rs.getInt("AID");

                    branches.add(new Branch(id, bname, phone, adminId));
                }
            }
        }
        return branches;
    }

    public void insertBranch(Branch branch) throws SQLException {
        String sql = "INSERT INTO BRANCH (BNAME, BPHONE, AID) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, branch.getName());
            stmt.setString(2, branch.getPhone());
            stmt.setInt(3, branch.getAdminId());
            stmt.executeUpdate();
        }
    }

    public void updateBranch(Branch branch) throws SQLException {
        String sql = "UPDATE BRANCH SET BNAME = ?, BPHONE = ?, AID = ? WHERE BRANCHID = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, branch.getName());
            stmt.setString(2, branch.getPhone());
            stmt.setInt(3, branch.getAdminId());
            stmt.setInt(4, branch.getBranchId());
            stmt.executeUpdate();
        }
    }

    public void deleteBranch(int branchId) throws SQLException {
        String sql = "DELETE FROM BRANCH WHERE BRANCHID = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, branchId);
            stmt.executeUpdate();
        }
    }
}



