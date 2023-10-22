package lk.ijse.dep11.app.db;

import lk.ijse.dep11.app.tm.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ItemDataAccessTest {

    @BeforeEach
    void setUp() throws SQLException {
        SingleConnectionDataSource.getInstance().getConnection().setAutoCommit(false);
    }

    @AfterEach
    void tearDown() throws SQLException {
        SingleConnectionDataSource.getInstance().getConnection().rollback();
        SingleConnectionDataSource.getInstance().getConnection().setAutoCommit(true);
    }

    @Test
    void saveItem() {
       assertDoesNotThrow(()->{
           ItemDataAccess.saveItem(new Item("425625","Pencil",25,new BigDecimal("25.50")));
           ItemDataAccess.saveItem(new Item("452633","Mouse",20,new BigDecimal("1500.00")));
           assertThrows(SQLException.class, ()->{
               ItemDataAccess.saveItem(new Item("425625","Pencil",25,new BigDecimal("25.50")));
           });
       });
    }

    @Test
    void deleteItem() throws SQLException {
        ItemDataAccess.saveItem(new Item("425625","Pencil",25,new BigDecimal("25.50")));
        ItemDataAccess.saveItem(new Item("452633","Mouse",20,new BigDecimal("1500.00")));
        int size = ItemDataAccess.getAllItems().size();
        assertDoesNotThrow(()-> ItemDataAccess.deleteItem("425625"));
        assertEquals(size-1,ItemDataAccess.getAllItems().size());
    }

    @Test
    void updateItem() throws SQLException {
        ItemDataAccess.saveItem(new Item("425625","Pencil",25,new BigDecimal("25.50")));
        assertDoesNotThrow(()->{
           ItemDataAccess.updateItem(new Item("425625","Pencil",15,new BigDecimal("25.50")));
        });

    }

    @Test
    void getAllItems() throws SQLException {
        ItemDataAccess.saveItem(new Item("425625","Pencil",25,new BigDecimal("25.50")));
        ItemDataAccess.saveItem(new Item("452633","Mouse",20,new BigDecimal("1500.00")));
        assertTrue( ItemDataAccess.getAllItems().size() >= 2);
    }
}