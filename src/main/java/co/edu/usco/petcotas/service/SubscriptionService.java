package co.edu.usco.petcotas.service;

import co.edu.usco.petcotas.model.Subscription;
import co.edu.usco.petcotas.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public Subscription createSubscription(String email) {
        if (subscriptionRepository.existsByEmail(email)) {
            throw new RuntimeException("Este correo ya está suscrito");
        }

        Subscription subscription = Subscription.builder()
                .email(email)
                .build();
        return subscriptionRepository.save(subscription);
    }

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public void deleteSubscription(Long id) {
        subscriptionRepository.deleteById(id);
    }
}
