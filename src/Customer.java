public class Customer 
{
    private int CustomerId;
    private String Name;
    private String Email;
    private String Password;
    private String PhoneNo;
    private String Address;
    private String RegistrationDate;
    private String DiscountVoucher;

    public Customer() {}
    public Customer(String name, String email, String phoneNo, String address,String password) 
    {
        // this.CustomerId=0;
        this.Name = name;
        this.Email = email;
        this.PhoneNo = phoneNo;
        this.Address = address;
        this.RegistrationDate = java.time.LocalDate.now().toString(); 
        this.Password=password;
        this.DiscountVoucher = null; 
    }
     public void setPhone(String phone)
    {
        this.PhoneNo=phone;
    }
    public int getId()
    {
        return CustomerId;
    }
    public String getName()
    {
        return Name;
    }
    public String getEmail()
    {
        return Email;
    }
    public String getPhoneNo()
    {
        return PhoneNo;
    }
    public String getAddress()
    {
        return Address;
    }
    public String getRegistrationDate()
    {
        return RegistrationDate;
    }
    public String getDiscountVoucher()
    {
        return DiscountVoucher;
    }
    public String getPassword()
    {
        return Password;
    }


    
    public void setId(int id)
    {
        this.CustomerId=id;
    }
    public void setName(String name)
    {
        this.Name=name;
    }
    public void setEmail(String email)
    {
        this.Email=email;
    }
    public void setPassword(String password) 
    {
        this.Password = password;
    }
    public void setAddress(String address)
    {
        this.Address=address;
    }
    public void setDate(String date)
    {
        this.RegistrationDate=date;
    }
    public void setDiscountVoucher(String discount)
    {
         this.DiscountVoucher=discount;
    }
    public void setphone(String phone)
    {
        this.PhoneNo=phone;
    }
}

