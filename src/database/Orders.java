package database;

import models.Order;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Orders extends Database<Order> {

    @Override
    public Order deserialize(Node node) {
        Order order = new Order();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            order.setId(Integer.parseInt(Utils.getTag(element, "id")));
            order.setUser_id(Integer.parseInt(Utils.getTag(element, "user_id")));
            order.setStorage_id(Integer.parseInt(Utils.getTag(element, "storage_id")));
            order.setFinished(Boolean.parseBoolean(Utils.getTag(element, "finished")));
        }
        return order;
    }

    @Override
    public Node serialize(Order order, Document document) {
        Element orderElement = document.createElement(getNodeName());

        Element idElement = document.createElement("id");
        idElement.setTextContent(Integer.toString(order.getId()));

        Element user_idElement = document.createElement("user_id");
        user_idElement.setTextContent(Integer.toString(order.getUser_id()));

        Element storage_idElement = document.createElement("storage_id");
        storage_idElement.setTextContent(Integer.toString(order.getStorage_id()));

        Element finishedElement = document.createElement("finished");
        finishedElement.setTextContent(Boolean.toString(order.isFinished()));

        orderElement.appendChild(idElement);
        orderElement.appendChild(user_idElement);
        orderElement.appendChild(storage_idElement);
        orderElement.appendChild(finishedElement);
        return orderElement;
    }

    @Override
    public String getFileName() {
        return "orders";
    }

    @Override
    public String getNodeName() {
        return "order";
    }
}
