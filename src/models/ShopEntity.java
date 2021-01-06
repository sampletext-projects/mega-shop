package models;

import database.Database;
import database.ShopEntities;

public class ShopEntity extends Entity {
    private int shopItem_id;
    private int amount;

    private ShopItem shopItem;

    public ShopEntity(int id, int shopItem_id, int amount) {
        super(id);
        setShopItem_id(shopItem_id);
        this.amount = amount;
    }

    public ShopEntity() {
        super(-1);
    }

    public int getShopItem_id() {
        return shopItem_id;
    }

    public void setShopItem_id(int shopItem_id) {
        this.shopItem_id = shopItem_id;
        this.shopItem = Database.getShopItemsDb().getById(shopItem_id);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ShopItem getShopItem() {
        return shopItem;
    }

    public double getCost() {
        return shopItem.getPrice() * amount;
    }

    @Override
    public ShopEntity pull() {
        if (getId() == -1) {
            Database.getShopEntitiesDb().insert(this);
        } else {
            ShopEntity entity = Database.getShopEntitiesDb().getById(getId());
            if (entity == null) {
                throw new UnsupportedOperationException("This object doesn't exist");
            }
            this.setShopItem_id(entity.shopItem_id);
            this.setAmount(entity.amount);
        }
        return this;
    }

    @Override
    public ShopEntity push() {
        super.push();
        return this;
    }

    @Override
    public ShopEntity erase() {
        super.erase();
        return this;
    }

    @Override
    protected ShopEntities getDatabase() {
        return Database.getShopEntitiesDb();
    }

    @Override
    public String toString() {
        return "ShopEntity{" +
                "shopItem_id=" + shopItem_id +
                ", amount=" + amount +
                "} " + super.toString();
    }
}
