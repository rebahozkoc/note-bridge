package nl.utwente.di.NoteBridge;
import org.junit.jupiter.api.Assertions ;
import org.junit.jupiter.api.Test ;
/* * *
Tests the Quoter*/
public class TestQuoter {
    @Test
    public void testBook1 () throws Exception {
        Quoter quoter = new Quoter() ;
        double price = quoter.getBookPrice("1") ;
        Assertions.assertEquals (10.0 , price , 0.0 ,"Price of book 1") ;
    }
}

