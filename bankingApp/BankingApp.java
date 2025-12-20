import service.BankingService;

public class BankingApp {
    public static void main(String[] args) {
        BankingService bankingService = new BankingService();
        bankingService.start();
    }
}
