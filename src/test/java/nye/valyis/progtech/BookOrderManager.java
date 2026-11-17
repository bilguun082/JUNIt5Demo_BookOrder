package nye.valyis.progtech;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// ==========================================
// 3. JUNIT 5 TEST CLASS: BookOrderManagerTest
// ==========================================
class BookOrderManagerTest {

    private BookOrderManager manager;
    private Book sampleBook;

    // Fixture setup before each test case
    @BeforeEach
    void setUp() {
        manager = new BookOrderManager();
        sampleBook = new Book("Java Programming", 100.0, 10);
        manager.addBookToInventory(sampleBook);
    }

    // Fixture teardown after each test case
    @AfterEach
    void tearDown() {
        manager = null;
        sampleBook = null;
    }

    @Test
    void testCalculateOrderTotalStandard() {
        // State testing: verifying correct mathematical calculation
        double total = manager.calculateOrderTotal("Java Programming", 2);
        assertEquals(200.0, total, 0.001, "Total should be 200.0 without discount.");
    }

    @Test
    void testCalculateOrderTotalWithDiscount() {
        manager.setDiscountRate(0.1); // 10% discount
        double total = manager.calculateOrderTotal("Java Programming", 2);
        assertEquals(180.0, total, 0.001, "Total should be 180.0 with 10% discount.");
    }

    @Test
    void testCalculateOrderTotalNullTitleThrowsException() {
        // Exception testing using assertThrows
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.calculateOrderTotal(null, 1);
        });
        assertEquals("Book title cannot be null or empty.", exception.getMessage());
    }

    // ==========================================
    // HOMEWORK SECTION: BRANCH COVERAGE EXERCISE
    // ==========================================

    @Test
    void testAddNullBookThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.addBookToInventory(null);
        });
        assertEquals("Book cannot be null.", exception.getMessage());
    }

    @Test
    void testSetInvalidDiscountRateThrowsException() {
        // Boundary test: rate < 0.0
        IllegalArgumentException excNegative = assertThrows(IllegalArgumentException.class, () -> {
            manager.setDiscountRate(-0.01);
        });
        assertEquals("Discount rate must be between 0.0 and 1.0.", excNegative.getMessage());

        // Boundary test: rate > 1.0
        IllegalArgumentException excTooHigh = assertThrows(IllegalArgumentException.class, () -> {
            manager.setDiscountRate(1.01);
        });
        assertEquals("Discount rate must be between 0.0 and 1.0.", excTooHigh.getMessage());

        // Valid boundaries (0.0 and 1.0)
        assertDoesNotThrow(() -> manager.setDiscountRate(0.0));
        assertDoesNotThrow(() -> manager.setDiscountRate(1.0));
    }

    @Test
    void testCalculateOrderTotalInsufficientStock() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            manager.calculateOrderTotal("Java Programming", 11);
        });
        assertEquals("Insufficient stock available.", exception.getMessage());
    }

    @Test
    void testProcessCheckoutSuccessAndStockReduction() {
        // Add another book to ensure inventory loops encounter both non-matching and matching items
        Book anotherBook = new Book("Clean Code", 120.0, 5);
        manager.addBookToInventory(anotherBook);

        boolean result = manager.processCheckout("Clean Code", 2, true);
        assertTrue(result, "Checkout should succeed for Clean Code.");
        assertEquals(3, anotherBook.getStock(), "Stock should be reduced by the ordered quantity.");

        boolean resultSample = manager.processCheckout("Java Programming", 4, false);
        assertTrue(resultSample, "Checkout should succeed for Java Programming.");
        assertEquals(6, sampleBook.getStock(), "Stock should be reduced by the ordered quantity.");
    }

    @Test
    void testCalculateOrderTotalEmptyOrBlankTitleThrowsException() {
        IllegalArgumentException emptyException = assertThrows(IllegalArgumentException.class, () -> {
            manager.calculateOrderTotal("", 1);
        });
        assertEquals("Book title cannot be null or empty.", emptyException.getMessage());

        IllegalArgumentException blankException = assertThrows(IllegalArgumentException.class, () -> {
            manager.calculateOrderTotal("   ", 1);
        });
        assertEquals("Book title cannot be null or empty.", blankException.getMessage());
    }

    @Test
    void testCalculateOrderTotalInvalidQuantityThrowsException() {
        IllegalArgumentException zeroException = assertThrows(IllegalArgumentException.class, () -> {
            manager.calculateOrderTotal("Java Programming", 0);
        });
        assertEquals("Quantity must be greater than zero.", zeroException.getMessage());

        IllegalArgumentException negativeException = assertThrows(IllegalArgumentException.class, () -> {
            manager.calculateOrderTotal("Java Programming", -3);
        });
        assertEquals("Quantity must be greater than zero.", negativeException.getMessage());
    }

    @Test
    void testCalculateOrderTotalBookNotFoundThrowsException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            manager.calculateOrderTotal("Non Existent Book", 1);
        });
        assertEquals("Book not found in inventory.", exception.getMessage());
    }

    @Test
    void testCalculateOrderTotalExactStockBoundary() {
        double total = manager.calculateOrderTotal("Java Programming", 10);
        assertEquals(1000.0, total, 0.001);
    }

    @Test
    void testProcessCheckoutInvalidInputs() {
        assertFalse(manager.processCheckout(null, 2, false), "Checkout should fail when bookTitle is null.");
        assertFalse(manager.processCheckout("Java Programming", 0, false), "Checkout should fail when quantity is 0.");
        assertFalse(manager.processCheckout("Java Programming", -1, false), "Checkout should fail when quantity is negative.");
    }

    @Test
    void testProcessCheckoutFullLoopBranchCoverage() {
        class ChangingTitleBook extends Book {
            private int accessCount = 0;

            ChangingTitleBook(String title, double price, int stock) {
                super(title, price, stock);
            }

            @Override
            public String getTitle() {
                accessCount++;
                if (accessCount == 1) {
                    return "Magic Book";
                }
                return "Disappeared Book";
            }
        }

        BookOrderManager customManager = new BookOrderManager();
        customManager.addBookToInventory(new ChangingTitleBook("Magic Book", 50.0, 5));
        boolean result = customManager.processCheckout("Magic Book", 1, false);
        assertTrue(result);
    }

    @Test
    void testBookModelGettersAndSetters() {
        Book book = new Book("Algorithms", 75.5, 8);
        assertEquals("Algorithms", book.getTitle());
        assertEquals(75.5, book.getPrice(), 0.001);
        assertEquals(8, book.getStock());

        book.setStock(12);
        assertEquals(12, book.getStock());
    }
}