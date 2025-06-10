package builder;

public class HttpRequestClient {
    public static void main(String[] args) {
        HttpRequest.HttpRequestBuilder builder = new HttpRequest.HttpRequestBuilder("https://example.com/api");
        builder.setMethod("POST")
                .addHeader("Content-Type", "application/json")
                .addQueryParam("key", "value")
                .setBody("{\"name\":\"John Doe\"}")
                .setTimeout(5000);
        HttpRequest request = builder.build();
        System.out.println("HTTP Request created successfully:");
        String requestStr = request.toString();
        System.out.println(requestStr);
    }
}
