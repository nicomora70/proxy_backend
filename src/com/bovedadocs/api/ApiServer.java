package com.bovedadocs.api;

import com.bovedadocs.audit.AuditEntry;
import com.bovedadocs.model.DocumentContent;
import com.bovedadocs.model.DocumentSummary;
import com.bovedadocs.model.User;
import com.bovedadocs.service.AccessDeniedException;
import com.bovedadocs.service.DocumentNotFoundException;
import com.bovedadocs.service.DocumentService;
import com.bovedadocs.service.RequestContext;
import com.bovedadocs.store.AppState;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class ApiServer {
    private final int port;
    private final AppState state;
    private HttpServer server;

    public ApiServer(int port, AppState state) {
        this.port = port;
        this.state = state;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/api", this::handle);
        server.setExecutor(Executors.newFixedThreadPool(12));
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private void handle(HttpExchange exchange) throws IOException {
        addCors(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
        String userId = query.containsKey("userId") ? query.get("userId") : "u1";
        String layers = query.containsKey("layers") ? query.get("layers") : "log,access,cache,lazy";
        User user = state.user(userId);
        RequestContext ctx = new RequestContext();

        try {
            if (user == null) {
                throw new IllegalArgumentException("Unknown userId: " + userId);
            }
            if (path.equals("/api/health")) {
                send(exchange, 200, "{\"status\":\"ok\",\"service\":\"bovedadocs-backend\"}");
                return;
            }
            if (path.equals("/api/users") && "GET".equals(method)) {
                send(exchange, 200, usersJson());
                return;
            }
            if (path.equals("/api/audit") && "GET".equals(method)) {
                send(exchange, 200, auditJson());
                return;
            }
            if (path.equals("/api/reset") && "POST".equals(method)) {
                state.reset();
                send(exchange, 200, "{\"status\":\"reset\"}");
                return;
            }

            String[] parts = path.split("/");
            boolean documentsEndpoint = parts.length >= 3 && "api".equals(parts[1]) && "documents".equals(parts[2]);
            if (!documentsEndpoint) {
                throw new DocumentNotFoundException("Unknown endpoint: " + path);
            }

            DocumentService service = state.chain(layers);

            if (parts.length == 3 && "GET".equals(method)) {
                List<DocumentSummary> documents = service.list(user, ctx);
                send(exchange, 200, listJson(user, documents, ctx));
                return;
            }

            if (parts.length == 4) {
                String documentId = URLDecoder.decode(parts[3], "UTF-8");
                if ("GET".equals(method)) {
                    DocumentContent content = service.read(user, documentId, ctx);
                    send(exchange, 200, readJson(user, content, ctx));
                    return;
                }
                if ("PUT".equals(method)) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    String content = Json.stringField(body, "content");
                    if (content == null) {
                        throw new IllegalArgumentException("Body must contain a content field");
                    }
                    DocumentSummary document = service.write(user, documentId, content, ctx);
                    send(exchange, 200, writeJson(user, document, ctx));
                    return;
                }
                if ("DELETE".equals(method)) {
                    service.delete(user, documentId, ctx);
                    send(exchange, 200, deleteJson(user, documentId, ctx));
                    return;
                }
            }

            throw new DocumentNotFoundException("Unknown endpoint: " + method + " " + path);
        } catch (AccessDeniedException exception) {
            send(exchange, 403, errorJson(403, exception.getMessage(), ctx));
        } catch (DocumentNotFoundException exception) {
            send(exchange, 404, errorJson(404, exception.getMessage(), ctx));
        } catch (IllegalArgumentException exception) {
            send(exchange, 400, errorJson(400, exception.getMessage(), ctx));
        } catch (Exception exception) {
            send(exchange, 500, errorJson(500, "Unexpected error: " + exception.getMessage(), ctx));
        }
    }

    private String usersJson() {
        StringBuilder json = new StringBuilder("{\"users\":[");
        boolean first = true;
        for (User user : state.users().values()) {
            if (!first) {
                json.append(',');
            }
            first = false;
            json.append(userJson(user));
        }
        return json.append("]}").toString();
    }

    private String listJson(User user, List<DocumentSummary> documents, RequestContext ctx) {
        StringBuilder json = new StringBuilder("{\"user\":").append(userJson(user));
        json.append(",\"count\":").append(documents.size());
        json.append(",\"documents\":[");
        for (int i = 0; i < documents.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append(summaryJson(documents.get(i)));
        }
        json.append("],\"latencyMs\":").append(ctx.getLatencyMs());
        json.append(",\"trace\":").append(traceJson(ctx));
        return json.append('}').toString();
    }

    private String readJson(User user, DocumentContent content, RequestContext ctx) {
        return "{\"user\":" + userJson(user)
                + ",\"document\":{\"id\":\"" + Json.escape(content.getId())
                + "\",\"title\":\"" + Json.escape(content.getTitle())
                + "\",\"content\":\"" + Json.escape(content.getContent())
                + "\",\"sizeKb\":" + content.getSizeKb() + "}"
                + ",\"latencyMs\":" + ctx.getLatencyMs()
                + ",\"cacheHit\":" + ctx.isCacheHit()
                + ",\"lazyReused\":" + ctx.isLazyReused()
                + ",\"trace\":" + traceJson(ctx) + "}";
    }

    private String writeJson(User user, DocumentSummary document, RequestContext ctx) {
        return "{\"user\":" + userJson(user)
                + ",\"document\":" + summaryJson(document)
                + ",\"latencyMs\":" + ctx.getLatencyMs()
                + ",\"trace\":" + traceJson(ctx) + "}";
    }

    private String deleteJson(User user, String documentId, RequestContext ctx) {
        return "{\"user\":" + userJson(user)
                + ",\"deleted\":\"" + Json.escape(documentId) + "\""
                + ",\"latencyMs\":" + ctx.getLatencyMs()
                + ",\"trace\":" + traceJson(ctx) + "}";
    }

    private String auditJson() {
        StringBuilder json = new StringBuilder("{\"entries\":[");
        List<AuditEntry> entries = state.audit().last(60);
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append(auditEntryJson(entries.get(i)));
        }
        return json.append("]}").toString();
    }

    private String auditEntryJson(AuditEntry entry) {
        return "{\"id\":" + entry.getId()
                + ",\"time\":\"" + Json.escape(entry.getTime())
                + "\",\"userId\":\"" + Json.escape(entry.getUserId())
                + "\",\"userName\":\"" + Json.escape(entry.getUserName())
                + "\",\"action\":\"" + Json.escape(entry.getAction())
                + "\",\"documentId\":" + (entry.getDocumentId() == null ? "null" : "\"" + Json.escape(entry.getDocumentId()) + "\"")
                + ",\"status\":\"" + Json.escape(entry.getStatus())
                + "\",\"message\":\"" + Json.escape(entry.getMessage())
                + "\",\"latencyMs\":" + entry.getLatencyMs() + "}";
    }

    private String userJson(User user) {
        return "{\"id\":\"" + Json.escape(user.getId())
                + "\",\"name\":\"" + Json.escape(user.getName())
                + "\",\"area\":\"" + Json.escape(user.getArea())
                + "\",\"role\":\"" + user.getRole().name() + "\"}";
    }

    private String summaryJson(DocumentSummary document) {
        return "{\"id\":\"" + Json.escape(document.getId())
                + "\",\"title\":\"" + Json.escape(document.getTitle())
                + "\",\"area\":\"" + Json.escape(document.getArea())
                + "\",\"ownerId\":\"" + Json.escape(document.getOwnerId())
                + "\",\"ownerName\":\"" + Json.escape(document.getOwnerName())
                + "\",\"classification\":\"" + document.getClassification().name()
                + "\",\"sizeKb\":" + document.getSizeKb()
                + ",\"canRead\":" + document.canRead()
                + ",\"canWrite\":" + document.canWrite()
                + ",\"canDelete\":" + document.canDelete() + "}";
    }

    private String traceJson(RequestContext ctx) {
        StringBuilder json = new StringBuilder("[");
        List<String> trace = ctx.getTrace();
        for (int i = 0; i < trace.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append('"').append(Json.escape(trace.get(i))).append('"');
        }
        return json.append(']').toString();
    }

    private String errorJson(int code, String message, RequestContext ctx) {
        return "{\"error\":\"" + Json.escape(message)
                + "\",\"code\":" + code
                + ",\"latencyMs\":" + ctx.getLatencyMs()
                + ",\"trace\":" + traceJson(ctx) + "}";
    }

    private void send(HttpExchange exchange, int status, String body) throws IOException {
        addCors(exchange);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private void addCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, PUT, DELETE, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Access-Control-Max-Age", "86400");
    }

    private Map<String, String> parseQuery(String rawQuery) throws IOException {
        Map<String, String> query = new HashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) {
            return query;
        }
        for (String pair : rawQuery.split("&")) {
            int separator = pair.indexOf('=');
            String key = separator >= 0 ? pair.substring(0, separator) : pair;
            String value = separator >= 0 ? pair.substring(separator + 1) : "";
            query.put(URLDecoder.decode(key, "UTF-8"), URLDecoder.decode(value, "UTF-8"));
        }
        return query;
    }
}
