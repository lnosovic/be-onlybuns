package com.example.demo.controller;

import com.example.demo.config.AppConfig;
import com.example.demo.Service.LoadBalancingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LoadBalancerController {

    private static final Logger logger = LoggerFactory.getLogger(LoadBalancerController.class);

    private final AppConfig appConfig;
    private final LoadBalancingStrategy loadBalancingStrategy;
    private final RestTemplate restTemplate = new RestTemplate();

    public LoadBalancerController(AppConfig appConfig, @Qualifier("roundRobin") LoadBalancingStrategy loadBalancingStrategy) {
        this.appConfig = appConfig;
        this.loadBalancingStrategy = loadBalancingStrategy;
    }

    // @RequestMapping("/**") presreće sve zahteve koji stignu na ovaj servis
    @RequestMapping("/**")
    public ResponseEntity<byte[]> forwardRequest(HttpServletRequest request) {
        List<String> availableInstances = appConfig.getInstances();
        int maxRetries = availableInstances.size(); // Pokušaćemo na svakoj instanci jednom

        for (int retryCount = 0; retryCount < maxRetries; retryCount++) {
            // 1. Odaberi sledeću instancu koristeći našu strategiju
            String instanceUrl = loadBalancingStrategy.getNextInstance(availableInstances);

            try {
                // 2. Kreiraj punu putanju za prosleđivanje
                // Npr. ako je zahtev bio GET /api/posts, a instanca je http://localhost:8081,
                // nova putanja će biti http://localhost:8081/api/posts
                URI newUri = new URI(instanceUrl + request.getRequestURI() +
                        (request.getQueryString() != null ? "?" + request.getQueryString() : ""));

                logger.info("Prosleđujem zahtev na: {}. Pokušaj {}/{}", newUri, retryCount + 1, maxRetries);

                // 3. Pripremi HTTP entitet sa telom i zaglavljima originalnog zahteva
                HttpHeaders headers = new HttpHeaders();
                request.getHeaderNames().asIterator()
                        .forEachRemaining(headerName -> headers.set(headerName, request.getHeader(headerName)));

                // Čitanje tela zahteva (request body)
                byte[] requestBody = request.getInputStream().readAllBytes();
                HttpEntity<byte[]> httpEntity = new HttpEntity<>(requestBody, headers);

                // 4. Pošalji zahtev odabranoj instanci
                return restTemplate.exchange(
                        newUri,
                        HttpMethod.valueOf(request.getMethod()),
                        httpEntity,
                        byte[].class // Primamo odgovor kao niz bajtova da bismo podržali sve tipove sadržaja
                );

            } catch (ResourceAccessException e) {
                // OVO JE NAŠ RETRY POLICY
                // Uhvatili smo grešku jer instanca nije dostupna (npr. ugašena)
                logger.error("Instanca {} nije dostupna. Greška: {}. Pokušavam sledeću...", instanceUrl, e.getMessage());
                // Petlja će se nastaviti i u sledećoj iteraciji će probati sa novom instancom

                // Uklanjamo instancu koja ne radi iz liste za buduće pokušaje u okviru istog zahteva
                // Napomena: Ovo je jednostavna implementacija. U realnom svetu, koristili biste
                // mehanizam "Circuit Breaker" da privremeno izbacite instancu iz rotacije.
                final String failedInstance = instanceUrl;
                availableInstances = availableInstances.stream()
                        .filter(inst -> !inst.equals(failedInstance))
                        .collect(Collectors.toList());
                if (availableInstances.isEmpty()) {
                    logger.error("Sve instance su nedostupne. Prekidam prosleđivanje.");
                    break; // Nema više instanci za probati
                }

            } catch (Exception e) {
                // Neka druga neočekivana greška
                logger.error("Neočekivana greška prilikom prosleđivanja zahteva", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
            }
        }

        // Ako smo izašli iz petlje a nismo uspeli da pošaljemo zahtev
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Svi serveri su trenutno nedostupni.".getBytes());
    }
}