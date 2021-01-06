package models;

import database.Database;
import database.Orders;

public class Order extends Entity {
    private int user_id;
    private int storage_id;
    private boolean finished;

    private User user;
    private ShopStorage storage;

    public Order(int id, int user_id, int storage_id, boolean finished) {
        super(id);
        setUser_id(user_id);
        setStorage_id(storage_id);
        setFinished(finished);
    }

    public Order() {
        super(-1);
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
        this.user = Database.getUsersDb().getById(user_id);
    }

    public int getStorage_id() {
        return storage_id;
    }

    public void setStorage_id(int storage_id) {
        this.storage_id = storage_id;
        this.storage = Database.getShopStoragesDb().getById(storage_id);
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public User getUser() {
        return user;
    }

    public ShopStorage getStorage() {
        return storage;
    }

    @Override
    public Order pull() {
        if (getId() == -1) {
            Database.getOrdersDb().insert(this);
        } else {
            Order order = Database.getOrdersDb().getById(getId());
            if (order == null) {
                throw new UnsupportedOperationException("This object doesn't exist");
            }
            this.setUser_id(order.user_id);
            this.setStorage_id(order.storage_id);
            this.setFinished(order.finished);
        }
        return this;
    }

    @Override
    public Order push() {
        super.push();
        return this;
    }

    @Override
    public Order erase() {
        super.erase();
        return this;
    }

    @Override
    protected Orders getDatabase() {
        return Database.getOrdersDb();
    }

    @Override
    public String toString() {
        return "Order{" +
                "user_id=" + user_id +
                ", storage_id=" + storage_id +
                ", finished=" + finished +
                "} " + super.toString();
    }
}
