package database;

import models.ShopEntity;
import models.ShopStorage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class ShopStorages extends Database<ShopStorage> {

    @Override
    public ShopStorage deserialize(Node node) {
        ShopStorage shopStorage = new ShopStorage();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            shopStorage.setId(Integer.parseInt(Utils.getTag(element, "id")));
            Node entitiesNode = Utils.getTagNode(element, "entities");
            NodeList shop_entity_idNodes = ((Element)entitiesNode).getElementsByTagName("id");
            List<Integer> entities = new ArrayList<>();
            for (int i = 0; i < shop_entity_idNodes.getLength(); i++) {
                if(shop_entity_idNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    entities.add(Integer.parseInt(shop_entity_idNodes.item(i).getTextContent()));
                }
            }
            shopStorage.setEntities_ids(entities);
        }
        return shopStorage;
    }

    @Override
    public Node serialize(ShopStorage storage, Document document) {
        Element storageElement = document.createElement(getNodeName());

        Element idElement = document.createElement("id");
        idElement.setTextContent(Integer.toString(storage.getId()));

        Element entities_ids_element = document.createElement("entities");
        for (int entity_id : storage.getEntities_ids()) {
            Element entity_id_element = document.createElement("id");
            entity_id_element.setTextContent(Integer.toString(entity_id));
            entities_ids_element.appendChild(entity_id_element);
        }

        storageElement.appendChild(idElement);
        storageElement.appendChild(entities_ids_element);
        return storageElement;
    }

    public ShopStorage getMainStorage() {
        return select(t -> t.getId() == 0).get(0);
    }

    @Override
    public String getFileName() {
        return "shop_storages";
    }

    @Override
    public String getNodeName() {
        return "storage";
    }
}
