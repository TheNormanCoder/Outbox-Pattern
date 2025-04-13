package com.example.outbox.api;

import com.example.outbox.domain.model.Order;
import com.example.outbox.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller REST per la gestione degli ordini.
 * Espone endpoint per le operazioni CRUD sugli ordini.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    /**
     * Crea un nuovo ordine.
     * Questo metodo salva l'ordine nel database e pubblica un evento
     * utilizzando il pattern Outbox.
     *
     * @param order L'ordine da creare
     * @return L'ordine creato con l'ID assegnato
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        log.info("Ricevuta richiesta di creazione ordine: {}", order);
        Order createdOrder = orderService.createOrder(order);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    /**
     * Recupera tutti gli ordini.
     *
     * @return Lista di tutti gli ordini
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        log.info("Ricevuta richiesta per tutti gli ordini");
        List<Order> orders = orderService.findAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Recupera un ordine specifico per ID.
     *
     * @param id L'ID dell'ordine da recuperare
     * @return L'ordine se trovato, 404 altrimenti
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        log.info("Ricevuta richiesta per l'ordine con ID: {}", id);
        Optional<Order> order = orderService.findOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Aggiorna un ordine esistente.
     * Anche questo metodo utilizzerà il pattern Outbox per pubblicare
     * un evento di aggiornamento.
     *
     * @param id L'ID dell'ordine da aggiornare
     * @param order I nuovi dati dell'ordine
     * @return L'ordine aggiornato se trovato, 404 altrimenti
     */
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        log.info("Ricevuta richiesta di aggiornamento per l'ordine con ID: {}", id);

        if (!orderService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        order.setId(id); // Assicura che l'ID nell'URL sia usato
        Order updatedOrder = orderService.updateOrder(order);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Elimina un ordine per ID.
     * Anche questa operazione pubblica un evento di eliminazione tramite Outbox.
     *
     * @param id L'ID dell'ordine da eliminare
     * @return 204 No Content se eliminato con successo, 404 se non trovato
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.info("Ricevuta richiesta di eliminazione per l'ordine con ID: {}", id);

        if (!orderService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}

