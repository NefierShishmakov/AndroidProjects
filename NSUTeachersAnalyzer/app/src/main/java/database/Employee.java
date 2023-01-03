package database;

public class Employee {
    public Employee(String surname, String name, String thirdName, String job, String email, String password)
    {
        this.surname = surname;
        this.name = name;
        this.thirdName = thirdName;
        this.job = job;
        this.email = email;
        this.password = password;
    }

    public String getSurname() {
        return this.surname;
    }

    public String getName() {
        return this.name;
    }

    public String getThirdName() {
        return this.thirdName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getJob() {
        return this.job;
    }

    private final String surname;
    private final String name;
    private final String thirdName;
    private final String email;
    private final String password;
    private final String job;
}
