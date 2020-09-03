package large.file.upload;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Part;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.reactivex.Completable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

@Controller()
@ExecuteOn(TaskExecutors.IO)
public class UploadController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    @Post(value = "/mixed-upload", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_PLAIN)
    public Completable mixedUpload(
        @Part("text") String text,
        @Part("smallFile") CompletedFileUpload smallFileUpload,
        @Part("largeFile") StreamingFileUpload largeFileUpload
    ) {

        log.info("Upload controller called for {}, {} and {}", text, smallFileUpload.getFilename(), largeFileUpload.getFilename());

        return Completable.create(new TempFileSink(largeFileUpload));
    }

    @Post(value = "/stream-upload", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_PLAIN)
    public Completable streamingUpload(
        @Part("text") String text,
        @Part("smallFile") StreamingFileUpload smallFileUpload,
        @Part("largeFile") StreamingFileUpload largeFileUpload
    ) {

        log.info("Upload controller called for {}, {} and {}", text, smallFileUpload.getFilename(), largeFileUpload.getFilename());

        return Completable.create(new TempFileSink(smallFileUpload))
            .andThen(Completable.create(new TempFileSink(largeFileUpload)));
    }

    @Post(value = "/stream-upload-nulls", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_PLAIN)
    public Completable streamingUploadWithNulls(
        @Part("text") String text,
        @Part("smallFile") StreamingFileUpload smallFileUpload,
        @Part("largeFile") StreamingFileUpload largeFileUpload,
        @Nullable @Part("anotherLargeFile") StreamingFileUpload anotherLargeFile
    ) {

        log.info("Upload controller called for {}, {} and {}", text, smallFileUpload.getFilename(), largeFileUpload.getFilename());

        final var completable = Completable.create(new TempFileSink(smallFileUpload))
            .andThen(Completable.create(new TempFileSink(largeFileUpload)));

        if (anotherLargeFile != null) {
            return completable.andThen(Completable.create(new TempFileSink(anotherLargeFile)));
        }

        return completable;
    }
}
