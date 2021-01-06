package database;

import models.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;

public class Users extends Database<User> {

    @Override
    public User deserialize(Node node) {
        User user = new User();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            user.setId(Integer.parseInt(Utils.getTag(element, "id")));
            user.setLogin(Utils.getTag(element, "login"));
            user.setPassword(Utils.getTag(element, "password"));
            user.setPosition(Utils.getTag(element, "position"));
            user.setName(Utils.getTag(element, "name"));
        }
        return user;
    }

    @Override
    public Node serialize(User user, Document document) {
        Element userElement = document.createElement(getNodeName());

        Element idElement = document.createElement("id");
        idElement.setTextContent(Integer.toString(user.getId()));

        Element loginElement = document.createElement("login");
        loginElement.setTextContent(user.getLogin());

        Element passwordElement = document.createElement("password");
        passwordElement.setTextContent(user.getPassword());

        Element positionElement = document.createElement("position");
        positionElement.setTextContent(user.getPosition());

        Element nameElement = document.createElement("name");
        nameElement.setTextContent(user.getName());

        userElement.appendChild(idElement);
        userElement.appendChild(loginElement);
        userElement.appendChild(passwordElement);
        userElement.appendChild(positionElement);
        userElement.appendChild(nameElement);
        return userElement;
    }

    public User selectByLoginAndPassword(String login, String password) {
        List<User> userList = select(u -> u.getLogin().equals(login) && u.getPassword().equals(password));
        if (userList.isEmpty()) {
            return null;
        }
        return userList.get(0);
    }

    @Override
    public String getFileName() {
        return "users";
    }

    @Override
    public String getNodeName() {
        return "user";
    }
}
