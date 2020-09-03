package large.file.upload;

import io.micronaut.http.multipart.PartData;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.annotations.NonNull;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class TempFileSink implements CompletableOnSubscribe, Subscriber<PartData> {

    private static final Logger log = LoggerFactory.getLogger(TempFileSink.class);
    private final StreamingFileUpload streamingFileUpload;
    private OutputStream outputStream;
    private CompletableEmitter emitter;
    private Subscription subscription;

    public TempFileSink(StreamingFileUpload streamingFileUpload) {
        this.streamingFileUpload = streamingFileUpload;
    }

    @Override
    public void subscribe(@NonNull CompletableEmitter emitter) throws Exception {
        this.emitter = emitter;

        var tempFile = Files.createTempFile(streamingFileUpload.getFilename(), null);
        this.outputStream = Files.newOutputStream(tempFile);

        streamingFileUpload.subscribe(this);
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        this.subscription.request(1);
    }

    @Override
    public void onNext(PartData partData) {
        try {
            outputStream.write(partData.getBytes());
            subscription.request(1);
        } catch (IOException e) {

            closeOutputStream();
            subscription.cancel();
            emitter.onError(e);
            log.error("Failed to read or write data", e);
        }
    }

    @Override
    public void onError(Throwable t) {

        closeOutputStream();
        log.error("Failed to read or write data", t);
        emitter.onError(t);
    }

    @Override
    public void onComplete() {
        closeOutputStream();
        emitter.onComplete();
    }

    private void closeOutputStream() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException ioException) {
            log.warn("Failed to close output stream");
        }
    }
}
