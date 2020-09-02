package large.file.upload;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
public class LargeFileUploadTest {

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    @Disabled("HttpClientConfiguration#maxContentLength has an integer overflow problem")
    void testUpload() {

        // Given: A multipart body with a large file
        final var multipartBody = MultipartBody.builder()
            .addPart("fileUpload", Paths.get(System.getProperty("user.home"), "file.txt").toFile())
            .build();

        // When: The upload is sent to the server
        final var httpRequest = HttpRequest.POST("/upload", multipartBody)
            .contentType(MediaType.MULTIPART_FORM_DATA_TYPE)
            .accept(MediaType.TEXT_PLAIN_TYPE);
        final var httpResponse = httpClient.toBlocking()
            .exchange(httpRequest);

        // Then: The server will eventually return a 200
        assertThat((Object) httpResponse.getStatus()).isEqualTo(HttpStatus.OK);
    }
}
