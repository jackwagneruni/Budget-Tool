import java.util.Date;

public interface FinancialItem {
    float getAmount();
    Date getDate();
    String getDescription();
    Category getCategory();
}