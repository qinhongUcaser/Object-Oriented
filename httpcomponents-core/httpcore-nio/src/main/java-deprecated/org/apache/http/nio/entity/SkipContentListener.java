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
import java.nio.ByteBuffer;

import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.util.Args;

/**
 * A simple {@link ContentListener} that reads and ignores all content.
 *
 * @since 4.0
 *
 * @deprecated (4.2)
 */
@Deprecated
public class SkipContentListener implements ContentListener {

    private final ByteBuffer buffer;

    public SkipContentListener(final ByteBufferAllocator allocator) {
        super();
        Args.notNull(allocator, "ByteBuffer allocator");
        this.buffer = allocator.allocate(2048);
    }

    @Override
    public void contentAvailable(
            final ContentDecoder decoder,
            final IOControl ioControl) throws IOException {
        int lastRead;
        do {
            buffer.clear();
            lastRead = decoder.read(buffer);
        } while (lastRead > 0);
    }

    @Override
    public void finished() {
    }

}
