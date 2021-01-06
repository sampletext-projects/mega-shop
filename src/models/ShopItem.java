package models;

import database.Database;
import database.ShopItems;

public class ShopItem extends Entity {
    private String brand;
    private String model;
    private String type;
    private float price;

    public ShopItem(int id, String brand, String model, String type, float price) {
        super(id);
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.price = price;
    }

    public ShopItem() {
        super(-1);
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public ShopItem pull() {
        if (getId() == -1) {
            Database.getShopItemsDb().insert(this);
        } else {
            ShopItem item = Database.getShopItemsDb().getById(getId());
            if (item == null) {
                throw new UnsupportedOperationException("This object doesn't exist");
            }
            this.setBrand(item.brand);
            this.setModel(item.model);
            this.setType(item.type);
            this.setPrice(item.price);
        }
        return this;
    }

    @Override
    public ShopItem push() {
        super.push();
        return this;
    }

    @Override
    public ShopItem erase() {
        super.erase();
        return this;
    }

    @Override
    protected ShopItems getDatabase() {
        return Database.getShopItemsDb();
    }

    @Override
    public String toString() {
        return "ShopItem{" +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                "} " + super.toString();
    }
}
