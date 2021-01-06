package models;

import database.Database;
import database.Users;

public class User extends Entity {
    public static User activeUser;

    private String login;
    private String password;
    private String position;
    private String name;

    public User(int id, String login, String password, String position, String name) {
        super(id);
        this.login = login;
        this.password = password;
        this.position = position;
        this.name = name;
    }

    public User() {
        super(-1);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public User pull() {
        if (getId() == -1) {
            Database.getUsersDb().insert(this);
        } else {
            User user = Database.getUsersDb().getById(getId());
            if (user == null) {
                throw new UnsupportedOperationException("This object doesn't exist");
            }
            this.setName(user.name);
            this.setLogin(user.login);
            this.setPassword(user.password);
            this.setPosition(user.position);
        }
        return this;
    }

    @Override
    public User push() {
        super.push();
        return this;
    }

    @Override
    public User erase() {
        super.erase();
        return this;
    }

    @Override
    protected Users getDatabase() {
        return Database.getUsersDb();
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", position='" + position + '\'' +
                ", name='" + name + '\'' +
                "} " + super.toString();
    }
}
