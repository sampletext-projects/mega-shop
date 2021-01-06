package models;

import database.Database;

public class Entity {
    private int id;

    public Entity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected Entity pull() {
        return this;
    }

    protected Entity push() {
        if (getId() == -1) {
            getDatabase().insert(this);
        } else {
            getDatabase().update(t -> t.getId() == this.getId(), this);
        }
        return this;
    }

    protected Entity erase() {
        if (this.getId() == -1) {
            throw new UnsupportedOperationException("This object is already erased");
        }
        getDatabase().remove(t -> t.getId() == this.getId());
        this.setId(-1);
        return this;
    }

    protected <T extends Database<Entity>> Database<Entity> getDatabase() {
        return null;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;
        Entity entity = (Entity) o;
        return getId() == entity.getId();
    }
}
