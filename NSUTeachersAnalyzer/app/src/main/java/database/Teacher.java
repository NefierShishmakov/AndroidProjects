package database;

public class Teacher {
    public Teacher(int id, String surname, String name, String thirdName) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.thirdName = thirdName;
    }

    @Override
    public String toString()
    {
        return this.surname + " " + this.name + " " + this.thirdName;
    }

    public int getId()
    {
        return this.id;
    }

    private final int id;
    private final String surname;
    private final String name;
    private final String thirdName;
}
