package database;

import models.ShopItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ShopItems extends Database<ShopItem> {

    @Override
    public ShopItem deserialize(Node node) {
        ShopItem item = new ShopItem();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            item.setId(Integer.parseInt(Utils.getTag(element, "id")));
            item.setBrand(Utils.getTag(element, "brand"));
            item.setModel(Utils.getTag(element, "model"));
            item.setType(Utils.getTag(element, "type"));
            item.setPrice(Float.parseFloat(Utils.getTag(element, "price")));
        }
        return item;
    }

    @Override
    public Node serialize(ShopItem shopItem, Document document) {
        Element itemElement = document.createElement(getNodeName());

        Element idElement = document.createElement("id");
        idElement.setTextContent(Integer.toString(shopItem.getId()));

        Element brandElement = document.createElement("brand");
        brandElement.setTextContent(shopItem.getBrand());

        Element modelElement = document.createElement("model");
        modelElement.setTextContent(shopItem.getModel());

        Element typeElement = document.createElement("type");
        typeElement.setTextContent(shopItem.getType());

        Element priceElement = document.createElement("price");
        priceElement.setTextContent(Float.toString(shopItem.getPrice()));

        itemElement.appendChild(idElement);
        itemElement.appendChild(brandElement);
        itemElement.appendChild(modelElement);
        itemElement.appendChild(typeElement);
        itemElement.appendChild(priceElement);
        return itemElement;
    }

    @Override
    public String getFileName() {
        return "shop_items";
    }

    @Override
    public String getNodeName() {
        return "item";
    }
}
