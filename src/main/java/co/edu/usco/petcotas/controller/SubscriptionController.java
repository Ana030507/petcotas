package co.edu.usco.petcotas.controller;

import co.edu.usco.petcotas.model.Subscription;
import co.edu.usco.petcotas.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // 📨 Cualquiera puede suscribirse (público)
    @PostMapping
    public ResponseEntity<Subscription> subscribe(@RequestParam String email) {
        return ResponseEntity.ok(subscriptionService.createSubscription(email));
    }

    // 🔒 Solo el admin puede ver todas las suscripciones
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Subscription>> getAll() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }
}
