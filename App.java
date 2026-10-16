import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) throws IOException {
        // Render provides the PORT env var; default to 10000 for local runs
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "10000"));

        byte[] page = Files.readAllBytes(Path.of("index.html"));
        byte[] notFound = "Not found".getBytes(StandardCharsets.UTF_8);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            boolean ok = path.equals("/") || path.equals("/index.html");

            byte[] body = ok ? page : notFound;
            exchange.getResponseHeaders().set("Content-Type",
                    ok ? "text/html; charset=utf-8" : "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(ok ? 200 : 404, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });

        // Health check endpoint for Render
        server.createContext("/health", exchange -> {
            byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });

        server.start();
        System.out.println("Angel's page is live on port " + port + " 💖");
    }
}
