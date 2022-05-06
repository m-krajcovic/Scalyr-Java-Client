package com.scalyr.api.internal;

import com.github.luben.zstd.ZstdOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.Args;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Wrapping entity that compresses content when writing, using ZStandard compression method.
 *
 * Almost a verbatim copy of GzipCompressingEntity
 */
public class ZstdCompressingEntity extends HttpEntityWrapper {
    public ZstdCompressingEntity(HttpEntity entity) {
        super(entity);
    }

    public Header getContentEncoding() {
        return new BasicHeader("Content-Encoding", "zstd");
    }

    public long getContentLength() {
        return -1L;
    }

    public boolean isChunked() {
        return true;
    }

    public InputStream getContent() {
        throw new UnsupportedOperationException();
    }

    public void writeTo(OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        ZstdOutputStream zstd = new ZstdOutputStream(outStream);
        this.wrappedEntity.writeTo(zstd);
        zstd.close();
    }
}
