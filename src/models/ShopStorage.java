package models;

import database.Database;
import database.ShopStorages;

import java.util.List;

public class ShopStorage extends Entity {
    private List<Integer> entities_ids;

    private List<ShopEntity> entities;

    public ShopStorage(int id, List<Integer> entities_ids) {
        super(id);
        setEntities_ids(entities_ids);
    }

    public ShopStorage() {
        super(-1);
    }

    public List<Integer> getEntities_ids() {
        return entities_ids;
    }

    public void setEntities_ids(List<Integer> entities_ids) {
        this.entities_ids = entities_ids;
        this.entities = Database.getShopEntitiesDb().select(t -> entities_ids.contains(t.getId()));
    }

    public List<ShopEntity> getEntities() {
        return entities;
    }

    public double getCost() {
        double sum = 0;
        for (ShopEntity entity : entities) {
            sum += entity.getCost();
        }
        return sum;
    }

    @Override
    public ShopStorage pull() {
        if (getId() == -1) {
            Database.getShopStoragesDb().insert(this);
        } else {
            ShopStorage storage = Database.getShopStoragesDb().getById(getId());
            if (storage == null) {
                throw new UnsupportedOperationException("This object doesn't exist");
            }
            this.setEntities_ids(storage.entities_ids);
        }
        return this;
    }

    @Override
    public ShopStorage push() {
        super.push();
        return this;
    }

    @Override
    public ShopStorage erase() {
        super.erase();
        return this;
    }

    @Override
    protected ShopStorages getDatabase() {
        return Database.getShopStoragesDb();
    }

    @Override
    public String toString() {
        return "ShopStorage{" +
                "entities_ids=" + entities_ids +
                "} " + super.toString();
    }
}
