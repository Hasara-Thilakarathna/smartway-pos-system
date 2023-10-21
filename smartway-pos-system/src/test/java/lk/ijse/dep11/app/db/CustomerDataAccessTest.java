package lk.ijse.dep11.app.db;

import lk.ijse.dep11.app.tm.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDataAccessTest {
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
    void saveCustomer() {
        assertDoesNotThrow(() -> {
            CustomerDataAccess.saveCustomer(new Customer("C001", "Hasara", "Kegalle"));
            CustomerDataAccess.saveCustomer(new Customer("ABC", "Nimesh", "Matara"));
            assertThrows(SQLException.class, () -> CustomerDataAccess.saveCustomer(new Customer("C001", "Hasara", "Kegalle")));
        });
    }

    @Test
    void deleteCustomer() throws SQLException {
        CustomerDataAccess.saveCustomer(new Customer("C001", "Hasara", "Kegalle"));
        int size = CustomerDataAccess.getAllCustomers().size();
        assertDoesNotThrow(() -> {
            CustomerDataAccess.deleteCustomer("C001");
            assertEquals(size - 1, CustomerDataAccess.getAllCustomers().size());
        });

    }

    @Test
    void updateCustomer() throws SQLException {
        CustomerDataAccess.saveCustomer(new Customer("C001", "Hasara", "Kegalle"));
        assertDoesNotThrow(() ->
                CustomerDataAccess.updateCustomer(new Customer("C001", "Nimesh", "Kegalle")));
    }

    @Test
    void getAllCustomers() throws SQLException {
        CustomerDataAccess.saveCustomer(new Customer("C001", "Nimesh", "Kegalle"));
        CustomerDataAccess.saveCustomer(new Customer("ABCC", "Malaka", "Kandy"));
        CustomerDataAccess.saveCustomer(new Customer("ACC", "Nimal", "Kandy"));
        List<Customer> customerList = CustomerDataAccess.getAllCustomers();
        assertTrue(customerList.size() >= 3);
    }

    @Test
    void getLastId() throws SQLException {

        CustomerDataAccess.saveCustomer(new Customer("ACC", "Nimal", "Kandy"));
        String lastId = CustomerDataAccess.getLastId();
        if(CustomerDataAccess.getAllCustomers().isEmpty()){
            assertNull(lastId);
        }else {
            assertNotNull(lastId);
        }
    }
}