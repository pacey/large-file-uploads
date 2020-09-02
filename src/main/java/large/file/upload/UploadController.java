package large.file.upload;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Part;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;

@Controller("/upload")
@ExecuteOn(TaskExecutors.IO)
public class UploadController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    @Post(consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_PLAIN)
    public Single<?> upload(@Part("file") StreamingFileUpload fileUpload) throws Exception {

        final var tempFile = Files.createTempFile(fileUpload.getFilename(), null);
        return Single.fromPublisher(fileUpload.transferTo(tempFile.toFile()))
            .doOnSubscribe(disposable -> log.info("Writing file to {}", tempFile))
            .doOnSuccess(transferSuccessful -> log.info("Finishing writing file to {}", tempFile));
    }

}
