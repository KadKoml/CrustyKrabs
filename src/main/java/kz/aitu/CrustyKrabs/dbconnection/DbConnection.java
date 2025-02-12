package kz.aitu.CrustyKrabs.dbconnection;

import kz.aitu.CrustyKrabs.entities.MenuItem;

import java.sql.*;
import java.util.ArrayList;

public class DbConnection {
    private final String url = "jdbc:postgresql://localhost:5432/restaurant_db";
    private final String username = "postgres";
    private final String password = "5234";

    public Connection connect() throws SQLException {
        Connection con = DriverManager.getConnection(url, username, password);
        System.out.println("Connected to database");
        return con;
    }

    public int disconnect(Connection con) throws SQLException {
        if (con != null) {
            con.close();
            System.out.println("Disconnected from database");
            return 0;
        }
        System.out.println("No connection to database");
        return 1;
    }

    public ArrayList<MenuItem> getMenuItems(Connection con) throws SQLException {
        String query = "SELECT * FROM public.menu_items";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        ArrayList<MenuItem> menuItems = new ArrayList<>();

        while (rs.next()) {
            MenuItem item = new MenuItem();
            item.setId(rs.getInt("id"));
            item.setName(rs.getString("name"));
            item.setPrice(rs.getDouble("price"));
            menuItems.add(item);
        }
        st.close();
        disconnect(con);
        return menuItems;
    }

    public MenuItem findMenuItemById(Connection con, int id) throws SQLException {
        String query = "SELECT * FROM public.menu_items WHERE id = ?";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        MenuItem item = null;
        if (rs.next()) {
            item = new MenuItem();
            item.setId(rs.getInt("id"));
            item.setName(rs.getString("name"));
            item.setPrice(rs.getDouble("price"));
        }
        st.close();
        disconnect(con);
        return item;
    }

    public MenuItem createMenuItem(Connection con, MenuItem menuItem) throws SQLException {
        String query = "INSERT INTO public.menu_items (id, name, price) VALUES (?, ?, ?)";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1, menuItem.getId());
        st.setString(2, menuItem.getName());
        st.setDouble(3, menuItem.getPrice());

        int success = st.executeUpdate();
        st.close();
        disconnect(con);
        if (success > 0) {
            System.out.println("MenuItem created successfully");
            return menuItem;
        }
        return null;
    }

    public MenuItem updateMenuItem(Connection con, MenuItem menuItem) throws SQLException {
        String query = "UPDATE public.menu_items SET name=?, price=? WHERE id=?";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1, menuItem.getName());
        st.setDouble(2, menuItem.getPrice());
        st.setInt(3, menuItem.getId());

        int success = st.executeUpdate();
        st.close();
        disconnect(con);
        if (success > 0) {
            System.out.println("MenuItem updated successfully");
            return menuItem;
        }
        return null;
    }

    public MenuItem deleteMenuItem(Connection con, int id) throws SQLException {
        String query = "DELETE FROM public.menu_items WHERE id=?";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1, id);

        int success = st.executeUpdate();
        st.close();
        disconnect(con);
        if (success > 0) {
            System.out.println("MenuItem deleted successfully");
            return new MenuItem(id, null, 0.0);
        }
        return null;
    }
}
