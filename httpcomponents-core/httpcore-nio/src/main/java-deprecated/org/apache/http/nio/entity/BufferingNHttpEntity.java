/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.nio.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.SimpleInputBuffer;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

/**
 * A {@link ConsumingNHttpEntity} that consumes content into a buffer. The
 * content can be retrieved as an InputStream via
 * {@link HttpEntity#getContent()}, or written to an output stream via
 * {@link HttpEntity#writeTo(OutputStream)}.
 *
 * @since 4.0
 *
 * @deprecated use (4.2)
 *  {@link org.apache.http.nio.protocol.BasicAsyncRequestProducer}
 *  or {@link org.apache.http.nio.protocol.BasicAsyncResponseProducer}
 */
@Deprecated
public class BufferingNHttpEntity extends HttpEntityWrapper implements
        ConsumingNHttpEntity {

    private final static int BUFFER_SIZE = 2048;

    private final SimpleInputBuffer buffer;
    private boolean finished;
    private boolean consumed;

    public BufferingNHttpEntity(
            final HttpEntity httpEntity,
            final ByteBufferAllocator allocator) {
        super(httpEntity);
        this.buffer = new SimpleInputBuffer(BUFFER_SIZE, allocator);
    }

    @Override
    public void consumeContent(
            final ContentDecoder decoder,
            final IOControl ioControl) throws IOException {
        this.buffer.consumeContent(decoder);
        if (decoder.isCompleted()) {
            this.finished = true;
        }
    }

    @Override
    public void finish() {
        this.finished = true;
    }

    /**
     * Obtains entity's content as {@link InputStream}.
     *
     *  @throws IllegalStateException if content of the entity has not been
     *    fully received or has already been consumed.
     */
    @Override
    public InputStream getContent() throws IOException {
        Asserts.check(this.finished, "Entity content has not been fully received");
        Asserts.check(!this.consumed, "Entity content has been consumed");
        this.consumed = true;
        return new ContentInputStream(this.buffer);
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

    @Override
    public void writeTo(final OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        final InputStream inStream = getContent();
        final byte[] buff = new byte[BUFFER_SIZE];
        int l;
        // consume until EOF
        while ((l = inStream.read(buff)) != -1) {
            outStream.write(buff, 0, l);
        }
    }

}
