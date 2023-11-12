package tr.gov.tubitak.bilgem.yte.shipment;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dapr.Topic;
import io.dapr.client.domain.CloudEvent;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

enum Status {
    SUCCESS,
    FAILED
}

@RestController
public class ShipmentController {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentController.class);

    @Topic(name = "paymentStatus", pubsubName = "shipmentpubsub")
    @PostMapping(path = "/paymentStatus", consumes = MediaType.ALL_VALUE)
    public Mono<ResponseEntity> getPaymentStatus(@RequestBody(required = false) CloudEvent<String> cloudEvent) {
        return Mono.fromSupplier(() -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Payment payment = objectMapper.readValue(cloudEvent.getData(), Payment.class);
                logger.info("Subscriber received: " + payment.getOrderId());
                return ResponseEntity.ok("SUCCESS");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}

@Getter
@Setter
class Payment {
    private String paymentId;
    private int orderId;
    private Status status;
}
