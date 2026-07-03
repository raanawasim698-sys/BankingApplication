package bank.model;

/**
 * Base class representing a real-world person.
 * Used as a foundation for Client via inheritance.
 */
public class Person {
    private String name;
    private String cnic;
    private String phoneNo;

    public Person(String name, String cnic, String phoneNo) {

        this.name    = name.trim();
        this.cnic    = cnic.trim();
        this.phoneNo = phoneNo.trim();
    }

    public String getName()    { return name; }
    public String getCnic()    { return cnic; }
    public String getPhoneNo() { return phoneNo; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name.trim();
    }
    public void setCnic(String cnic) {
        if (cnic == null || !cnic.matches("\\d{5}-\\d{7}-\\d"))
            throw new IllegalArgumentException("Invalid CNIC format.");
        this.cnic = cnic.trim();
    }
    public void setPhoneNo(String phoneNo) {
        if (phoneNo == null || !phoneNo.matches("03\\d{9}"))
            throw new IllegalArgumentException("Invalid phone number.");
        this.phoneNo = phoneNo.trim();
    }

    @Override
    public String toString() {
        return String.format("Name: %s | CNIC: %s | Phone: %s", name, cnic, phoneNo);
    }
}
