// package mcpserver;
//
// import org.springframework.http.MediaType;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RestController;
// import reactor.core.publisher.Flux;
// import java.time.Duration;
// import java.time.LocalTime;
//
// @RestController
// public class SseController {
//
//     @GetMapping(path = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//     public Flux<String> sendEvents() {
//         return Flux.interval(Duration.ofSeconds(1))
//                 .map(sequence -> "Event at " + LocalTime.now());
//     }
// }
