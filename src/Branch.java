public class Branch {
    private int branchId;
    private String name;
    private String phone;
    private int adminId;

    public Branch(int branchId, String name, String phone, int adminId) {
        this.branchId = branchId;
        this.name = name;
        this.phone = phone;
        this.adminId = adminId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
}


