package nl.utwente.di.NoteBridge;

public class Quoter {
    public double getBookPrice(String isbn){
        double i=Double.parseDouble(isbn);
        return ((i * 9) / 5) + 32;
    }
}
