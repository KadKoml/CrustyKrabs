package kz.aitu.CrustyKrabs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.aitu.CrustyKrabs.dbconnection.DbConnection;
import kz.aitu.CrustyKrabs.entities.MenuItem;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

@RestController
public class MyController {

    private final ObjectMapper oMapper;

    public MyController(ObjectMapper objectMapper) {
        this.oMapper = objectMapper;
    }

    @GetMapping("/main/menuItem")
    public String getSampleMenuItem() {
        MenuItem menuItem = new MenuItem(1, "Burger", 5.99);
        String jsonData = null;
        try {
            jsonData = oMapper.writeValueAsString(menuItem);
        } catch (JsonProcessingException e) {
            System.out.println("Error converting to JSON");
        }
        return jsonData;
    }

    @PostMapping("/main/customMenuItem")
    public String createCustomMenuItem(@RequestParam int id, @RequestParam String name, @RequestParam double price) {
        String jsonData = null;
        MenuItem menuItem = new MenuItem(id, name, price);
        try {
            jsonData = oMapper.writeValueAsString(menuItem);
        } catch (JsonProcessingException e) {
            System.out.println("Error converting to JSON: " + e.toString());
        }

        DbConnection con = new DbConnection();
        try {
            con.connect();
        } catch (Exception e) {
            System.out.println("Database connection error");
            throw new RuntimeException(e);
        }
        return jsonData;
    }

    @GetMapping("/main/allMenuItems")
    public String getAllMenuItems() {
        DbConnection myConnection = new DbConnection();
        Connection con;
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        try {
            con = myConnection.connect();
            menuItems = myConnection.getMenuItems(con);
        } catch (Exception e) {
            System.out.println("Error retrieving all menu items");
        }

        String jsonData = null;
        try {
            jsonData = oMapper.writeValueAsString(menuItems);
        } catch (JsonProcessingException e) {
            System.out.println("Error converting to JSON");
        }
        return jsonData;
    }


    @PostMapping("/main/createMenuItem")
    public String createMenuItem(@RequestParam int id, @RequestParam String name, @RequestParam double price) {
        DbConnection myConnection = new DbConnection();
        MenuItem menuItem = new MenuItem(id, name, price);
        String jsonData = null;
        try (Connection con = myConnection.connect()) {
            MenuItem createdMenuItem = myConnection.createMenuItem(con, menuItem);
            jsonData = oMapper.writeValueAsString(createdMenuItem);
        } catch (JsonProcessingException e) {
            System.out.println("JSON processing error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error creating menu item: " + e.getMessage());
        }
        return jsonData;
    }

    @PutMapping("/main/updateMenuItem")
    public String updateMenuItem(@RequestParam int id, @RequestParam String newName, @RequestParam double newPrice) {
        DbConnection myConnection = new DbConnection();
        String jsonData = null;

        try (Connection con = myConnection.connect()) { // Открываем соединение
            MenuItem menuItem = new MenuItem(id, newName, newPrice); // Создаем объект
            MenuItem updatedMenuItem = myConnection.updateMenuItem(con, menuItem); // Обновляем в БД

            if (updatedMenuItem != null) {
                jsonData = new ObjectMapper().writeValueAsString(updatedMenuItem);
            } else {
                jsonData = "{\"error\": \"MenuItem not updated\"}";
            }
        } catch (Exception e) {
            System.out.println("Error updating menu item: " + e.getMessage());
        }

        return jsonData;
    }


    @DeleteMapping("/main/deleteMenuItem")
    public String deleteMenuItem(@RequestParam int id) {
        DbConnection myConnection = new DbConnection();
        Connection con;
        MenuItem deletedMenuItem = null;
        String jsonData = null;
        try {
            con = myConnection.connect();
            deletedMenuItem = myConnection.deleteMenuItem(con, id);
        } catch (Exception e) {
            System.out.println("Error deleting menu item: " + e.getMessage());
        }
        try {
            jsonData = oMapper.writeValueAsString(deletedMenuItem);
        } catch (JsonProcessingException e) {
            System.out.println("Error converting to JSON");
        }
        return jsonData;
    }
}
