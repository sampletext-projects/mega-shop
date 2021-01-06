package database;

import models.Cart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Carts extends Database<Cart> {

    @Override
    public Cart deserialize(Node node) {
        Cart cart = new Cart();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            cart.setId(Integer.parseInt(Utils.getTag(element, "id")));
            cart.setUser_id(Integer.parseInt(Utils.getTag(element, "user_id")));
            cart.setStorage_id(Integer.parseInt(Utils.getTag(element, "storage_id")));
        }
        return cart;
    }

    @Override
    public Node serialize(Cart cart, Document document) {
        Element orderElement = document.createElement(getNodeName());

        Element idElement = document.createElement("id");
        idElement.setTextContent(Integer.toString(cart.getId()));

        Element user_idElement = document.createElement("user_id");
        user_idElement.setTextContent(Integer.toString(cart.getUser_id()));

        Element storage_idElement = document.createElement("storage_id");
        storage_idElement.setTextContent(Integer.toString(cart.getStorage_id()));

        orderElement.appendChild(idElement);
        orderElement.appendChild(user_idElement);
        orderElement.appendChild(storage_idElement);
        return orderElement;
    }

    public Cart getByUserId(int userId) {
        return select(t -> t.getUser_id() == userId).get(0);
    }

    @Override
    public String getFileName() {
        return "carts";
    }

    @Override
    public String getNodeName() {
        return "cart";
    }
}
