package tr.gov.tubitak.bilgem.yte.payment;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

enum Status {
    SUCCESS,
    FAILED
}

@SpringBootApplication
public class PaymentApplication {
    private static final String DAPR_STATE_STORE = "statestore";

    public static void main(String[] args) throws Exception {
        try (DaprClient client = new DaprClientBuilder().build()) {
            for (int i = 1; i <= 16; i++) {
                Payment payment = new Payment();
                payment.setOrderId(i);
                payment.setStatus(i % 4 == 0 ? Status.FAILED : Status.SUCCESS);
                // Save state into the state store
                client.saveState(DAPR_STATE_STORE, String.valueOf(i), payment).block();
                System.out.println("Saving Payment: " + payment.getOrderId());
                TimeUnit.MILLISECONDS.sleep(1000);
            }
        }
    }
}

@Getter
@Setter
class Payment {
    private int orderId;
    private Status status;
}
