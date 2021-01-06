package database;

import models.Entity;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class Database<T extends Entity> {

    private static Users usersDb;
    private static Orders ordersDb;
    private static ShopItems shopItemsDb;
    private static ShopEntities shopEntitiesDb;
    private static ShopStorages shopStoragesDb;
    private static Carts cartsDb;

    public static Users getUsersDb() {
        if (usersDb == null) {
            usersDb = new Users();
        }
        return usersDb;
    }

    public static Orders getOrdersDb() {
        if (ordersDb == null) {
            ordersDb = new Orders();
        }
        return ordersDb;
    }

    public static ShopItems getShopItemsDb() {
        if (shopItemsDb == null) {
            shopItemsDb = new ShopItems();
        }
        return shopItemsDb;
    }

    public static ShopEntities getShopEntitiesDb() {
        if (shopEntitiesDb == null) {
            shopEntitiesDb = new ShopEntities();
        }
        return shopEntitiesDb;
    }

    public static ShopStorages getShopStoragesDb() {
        if (shopStoragesDb == null) {
            shopStoragesDb = new ShopStorages();
        }
        return shopStoragesDb;
    }

    public static Carts getCartsDb() {
        if (cartsDb == null) {
            cartsDb = new Carts();
        }
        return cartsDb;
    }

    protected static Document loadDocumentFromString(String xmlString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));
            document.normalizeDocument();
            return document;
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return null;
    }

    //Fixes first node indentation
    //https://dev64.wordpress.com/2012/10/22/pretty-print-dom-document-by-java/
    protected static void removeWhitespaceNodes(Element e) {
        NodeList children = e.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child instanceof Text && ((Text) child).getData().trim().length() == 0) {
                e.removeChild(child);
            } else if (child instanceof Element) {
                removeWhitespaceNodes((Element) child);
            }
        }
    }

    protected static String convertDocumentToString(Document document) {
        try {
            removeWhitespaceNodes(document.getDocumentElement());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(source, result);
            return result.getWriter().toString();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return "";
    }

    public List<T> select(Predicate<T> predicate) {
        List<T> list = new ArrayList<>();
        try {
            Document document = getDocument();
            NodeList nodeList = document.getElementsByTagName(getNodeName());
            for (int i = 0; i < nodeList.getLength(); i++) {
                T value = deserialize(nodeList.item(i));
                if (predicate == null || predicate.test(value)) {
                    list.add(value);
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return list;
    }

    @SafeVarargs
    public final void insert(T... objects) {
        try {
            Document document = getDocument();
            Element rootElement = document.getDocumentElement();
            for (T object : objects) {
                object.setId(getMaxId() + 1);
                rootElement.appendChild(serialize(object, document));
            }
            saveDocument(document);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void remove(Predicate<T> predicate) {
        if (predicate == null) {
            cleanUpFile();
            return;
        }

        Document document = getDocument();
        Element rootElement = document.getDocumentElement();
        NodeList nodeList = rootElement.getElementsByTagName(getNodeName());
        for (int i = 0; i < nodeList.getLength(); i++) {
            T value = deserialize(nodeList.item(i));
            if (predicate.test(value)) {
                rootElement.removeChild(nodeList.item(i));
            }
        }
        saveDocument(document);
    }

    public void update(Predicate<Entity> predicate, T object) {
        try {
            Document document = getDocument();
            Element rootElement = document.getDocumentElement();
            NodeList nodeList = rootElement.getElementsByTagName(getNodeName());
            Node node = serialize(object, document);
            for (int i = 0; i < nodeList.getLength(); i++) {
                T value = deserialize(nodeList.item(i));
                if (predicate == null || predicate.test(value)) {
                    rootElement.replaceChild(node, nodeList.item(i));
                }
            }
            saveDocument(document);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public T getById(int id) {
        List<T> select = select(t -> t.getId() == id);
        if (select.isEmpty()) {
            return null;
        }
        return select.get(0);
    }

    public int getMaxId() {
        int max_id = 0;
        List<T> selects = select(null);
        for (T select : selects) {
            if (select.getId() > max_id) {
                max_id = select.getId();
            }
        }
        return max_id;
    }

    protected abstract T deserialize(Node node);

    protected abstract Node serialize(T object, Document document);

    protected void cleanUpFile() {
        Document document = getDocument();
        Element rootElement = document.getDocumentElement();
        NodeList nodes = rootElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            rootElement.removeChild(nodes.item(i));
        }
        saveDocument(document);
    }

    protected String getFilePath() {
        return "src/xml/" + getFileName() + ".xml";
    }

    protected Document getDocument() {
        return Database.loadDocumentFromString(Utils.readFile(getFilePath()));
    }

    protected void saveDocument(Document document) {
        Utils.writeFile(getFilePath(), Database.convertDocumentToString(document));
    }

    public abstract String getFileName();

    public abstract String getNodeName();
}
