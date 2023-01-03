package database;

public class User
{
    public User(int id, String job) {
        this.id = id;
        this.job = job;
    }

    public String getJob() {
        return job;
    }

    public int getId() {
        return id;
    }

    private final int id;
    private final String job;
}
