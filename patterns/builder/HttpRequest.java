package patterns.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    public final String url;
    public final String method;
    public final Map<String, String> headers;
    public final Map<String, String> queryParams;
    public final String body;
    public final int timeout;

    private HttpRequest(HttpRequestBuilder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = Collections.unmodifiableMap(new HashMap<>(builder.headers));
        this.queryParams = Collections.unmodifiableMap(new HashMap<>(builder.queryParams));
        this.body = builder.body;
        this.timeout = builder.timeout;

        // Validate URL format
        if (!this.url.matches("^(http|https)://.*")) {
            throw new IllegalArgumentException("Invalid URL format");
        }

        // Validate method
        if (!this.method.matches("GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS")) {
            throw new IllegalArgumentException("Invalid HTTP method: " + this.method);
        }
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                ", timeout=" + timeout +
                '}';
    }

    public static class HttpRequestBuilder {
        private final String url;
        private String method;
        private Map<String, String> headers;
        private Map<String, String> queryParams;
        private String body;
        private int timeout;

        public HttpRequestBuilder(String url) {
            if (url == null || url.trim().isEmpty()) {
                throw new IllegalArgumentException("URL cannot be null or empty");
            }
            // regex validation of url
            if (!url.matches("^(http|https)://.*")) {
                throw new IllegalArgumentException("Invalid URL format");
            }
            this.url = url;
            this.method = "GET";
            this.headers = new HashMap<>();
            this.queryParams = new HashMap<>();
            this.body = null;
            this.timeout = 5000; // default timeout
        }

        public HttpRequestBuilder setMethod(String method) {
            if (method == null || method.trim().isEmpty()) {
                throw new IllegalArgumentException("Method cannot be null or empty");
            }
            this.method = method.toUpperCase();
            return this;
        }

        public HttpRequestBuilder addHeader(String key, String value) {
            if (key == null || key.trim().isEmpty() || value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Header key and value cannot be null or empty");
            }
            this.headers.put(key, value);
            return this;
        }

        public HttpRequestBuilder setBody(String body) {
            this.body = body;
            return this;
        }

        public HttpRequestBuilder setTimeout(int timeout) {
            if (timeout <= 0) {
                throw new IllegalArgumentException("Timeout must be greater than zero");
            }
            this.timeout = timeout;
            return this;
        }

        public HttpRequestBuilder addQueryParam(String key, String value) {
            if (key == null || key.trim().isEmpty() || value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Query parameter key and value cannot be null or empty");
            }
            this.queryParams.put(key, value);
            return this;
        }

        public HttpRequest build() {
            if ((this.method.equals("POST") || this.method.equals("PUT"))
                    && (this.body == null || this.body.isEmpty())) {
                System.out.println("Warning: POST or PUT method without body may not be effective.");
            }
            return new HttpRequest(this);
        }
    }
}
