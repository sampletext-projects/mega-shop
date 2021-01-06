package database;

import models.ShopEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ShopEntities extends Database<ShopEntity> {

    @Override
    public ShopEntity deserialize(Node node) {
        ShopEntity entity = new ShopEntity();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            entity.setId(Integer.parseInt(Utils.getTag(element, "id")));
            entity.setShopItem_id(Integer.parseInt(Utils.getTag(element, "shop_item_id")));
            entity.setAmount(Integer.parseInt(Utils.getTag(element, "amount")));
        }
        return entity;
    }

    @Override
    public Node serialize(ShopEntity entity, Document document) {
        Element entityElement = document.createElement(getNodeName());

        Element idElement = document.createElement("id");
        idElement.setTextContent(Integer.toString(entity.getId()));

        Element shop_item_idElement = document.createElement("shop_item_id");
        shop_item_idElement.setTextContent(Integer.toString(entity.getShopItem_id()));

        Element amountElement = document.createElement("amount");
        amountElement.setTextContent(Integer.toString(entity.getAmount()));

        entityElement.appendChild(idElement);
        entityElement.appendChild(shop_item_idElement);
        entityElement.appendChild(amountElement);
        return entityElement;
    }

    @Override
    public String getFileName() {
        return "shop_entities";
    }

    @Override
    public String getNodeName() {
        return "entity";
    }
}
