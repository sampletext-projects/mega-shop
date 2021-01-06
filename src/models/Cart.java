package models;

import database.Carts;
import database.Database;

public class Cart extends Entity {
    private int user_id;
    private int storage_id;

    private User user;
    private ShopStorage storage;

    public Cart(int id, int user_id, int storage_id) {
        super(id);
        setUser_id(user_id);
        setStorage_id(storage_id);
    }

    public Cart() {
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

    public User getUser() {
        return user;
    }

    public ShopStorage getStorage() {
        return storage;
    }

    @Override
    public Cart pull() {
        if (getId() == -1) {
            Database.getCartsDb().insert(this);
        } else {
            Cart cart = Database.getCartsDb().getById(getId());
            if (cart == null) {
                throw new UnsupportedOperationException("This object doesn't exist");
            }
            this.setUser_id(cart.user_id);
            this.setStorage_id(cart.storage_id);
        }
        return this;
    }

    @Override
    public Cart push() {
        super.push();
        return this;
    }

    @Override
    public Cart erase() {
        super.erase();
        return this;
    }

    @Override
    protected Carts getDatabase() {
        return Database.getCartsDb();
    }

    @Override
    public String toString() {
        return "Cart{" +
                "user_id=" + user_id +
                ", storage_id=" + storage_id +
                "} " + super.toString();
    }
}
