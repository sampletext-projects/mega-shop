package database;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {
    public static String getTag(Element element, String tag) {
        Node node = getTagNode(element, tag);
        String nodeValue = node.getTextContent();
        return nodeValue;
    }

    public static Node getTagNode(Element element, String tag) {
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            if (element.getChildNodes().item(i).getNodeName().equals(tag)) {
                return element.getChildNodes().item(i);
            }
        }
        return null;
    }

    public static String readFile(String filePath) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void writeFile(String filePath, String content) {
        try {
            Files.write(Paths.get(filePath), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
