package cn.tendata.location.task.batch;

import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class GzipBufferedReaderFactory implements BufferedReaderFactory {
    @Override
    @NonNull
    public BufferedReader create(@NonNull Resource resource, @NonNull String encoding)
        throws IOException {
        GZIPInputStream gis = new GZIPInputStream(resource.getInputStream());
        return new BufferedReader(new InputStreamReader(gis));
    }
}
