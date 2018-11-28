package cn.tendata.location.task.batch;

import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class GzipBufferedReaderFactory implements BufferedReaderFactory {
    @Override
    public BufferedReader create(Resource resource, String encoding)
        throws IOException {
        GZIPInputStream gis = new GZIPInputStream(resource.getInputStream());
        return new BufferedReader(new InputStreamReader(gis));
    }
}
