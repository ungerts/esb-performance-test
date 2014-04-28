/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package samples.services.http;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple http/s servlet that will echo the given message using the same content type of the
 * request. Internally uses a ByteBuffer to optimize on performance, and "knows" to detect the
 * size this buffer to be allocated for optimal performance
 *
 * @author Asankha Perera (asankha AT adroitlogic DOT com)
 *
 * 11 June 2008: Enhance to add an optional 'smart' delay before responding.. again uses the SOAPAction
 * 20 June 2008: Allow the buffer(s) to be dynamically created to support large messages
 * 7  Sep  2009: Allow generic POST messages
 */
public class EchoService extends HttpServlet {

    private static final int DEFAULT_BUFFER_SIZE = 4;
    public static volatile long delayMillis = 0;
    static volatile int count;
    static volatile int tocount;
    static volatile AtomicInteger outstanding = new AtomicInteger();

    public void doPost(final HttpServletRequest request,
                       final HttpServletResponse response) {

        ByteBuffer bb = null;
        List<ByteBuffer> bbList = null;

        try {
            int bufKBytes = DEFAULT_BUFFER_SIZE;
            int delaySecs = 0;

            String soapAction = request.getHeader("SOAPAction");
            if (soapAction != null) {
                if (soapAction.startsWith("\"")) {
                    soapAction = soapAction.replaceAll("\"", "");
                }
                int dotPos = soapAction.indexOf(".");
                int secondDotPos = (dotPos == -1 ? -1 : soapAction.indexOf(".", dotPos+1));

                if (secondDotPos > 0) {
                    bufKBytes = Integer.parseInt(soapAction.substring(dotPos+1, secondDotPos));
                    delaySecs = Integer.parseInt(soapAction.substring(secondDotPos+1));
                } else if (dotPos > 0) {
                    bufKBytes = Integer.parseInt(soapAction.substring(dotPos+1));
                }
            }

            // quick hack to increase buffer to be adequate
            switch (bufKBytes) {
                case 5: bufKBytes = 6; break;
                case 10: bufKBytes = 12; break;
                case 100: bufKBytes = 110; break;
            }

            bb = ByteBuffer.allocate(bufKBytes * 1024);
            ReadableByteChannel rbc = Channels.newChannel(request.getInputStream());

            int len, tot = 0;
            while ((len = rbc.read(bb)) > 0) {
                tot += len;
                if (tot >= bb.capacity()) {
                    // --- auto expand logic ---
                    if (bbList == null) {
                        bbList = new ArrayList<ByteBuffer>();
                    }
                    bb.flip();
                    bbList.add(bb);
                    bufKBytes = bufKBytes * 2;
                    bb = ByteBuffer.allocate(bufKBytes * 1024);
                }
            }
            bb.flip();

            // sleep when a "sleep" header exists - but if "port" is also specified, only when it matches
            long delayAmount = 0;
            String sleep = request.getHeader("sleep");
            if (sleep != null) {
                String port = request.getHeader("port");
                if (port != null) {
                    if (request.getLocalPort() == Integer.parseInt(port)) {
                        delayAmount = Long.parseLong(sleep);
                        System.out.println("Echo Service on port : " + port + " sleeping for : " + delayAmount);
                    }
                } else {
                    delayAmount = Long.parseLong(sleep);
                    System.out.println("Echo Service on port : " + request.getLocalPort() + " sleeping for : " + delayAmount);
                }
            }

            // --- auto expand logic ---
            if (bbList != null) {
                bbList.add(bb);
            }

            if (delaySecs > 0 && request.getLocalPort() == 9000) { // sleep only when running on port 9000
                delayAmount = delaySecs * 1000;
            } else if (delayMillis > 0) {
                delayAmount = delayMillis;
            }

            if (delayAmount != 0 && request.isAsyncSupported()) {
                int i = count++;
                outstanding.incrementAndGet();
                if (i % 1000 == 0) {
                    System.out.println("Total: " + count);
                }

                AsyncContext context = request.startAsync();
                final ByteBuffer bb2 = bb;
                final List<ByteBuffer> bbList2 = bbList;

                context.addListener(new AsyncListener() {
                    public void onComplete(AsyncEvent event) throws IOException {
                    }
                    public void onTimeout(AsyncEvent event) throws IOException {
                        int i = tocount++;
                        int out = outstanding.decrementAndGet();
                        if (i % 1000 == 0) {
                            System.out.println("Timeout! " + tocount + " " + out);
                        }
                        writeOutput(request, response, bbList2, bb2, event.getAsyncContext());
                    }
                    public void onError(AsyncEvent event) throws IOException {
                    }
                    public void onStartAsync(AsyncEvent event) throws IOException {
                    }

                });

                context.setTimeout(delayAmount);
                return;
            } else if (delayAmount != 0) {
                Thread.sleep(delayAmount);
            }
            writeOutput(request, response, bbList, bb, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeOutput(HttpServletRequest request,
                            HttpServletResponse response,
                            List<ByteBuffer> bbList,
                            ByteBuffer bb,
                            AsyncContext context) throws IOException {
        response.setContentType(request.getContentType());
        response.setHeader("port", Integer.toString(request.getLocalPort()));
        //System.out.println("Reply from Echo service on port : " + request.getLocalPort());

        OutputStream out = response.getOutputStream();
        WritableByteChannel wbc = Channels.newChannel(out);

        if (bbList == null) {
            while (wbc.write(bb) > 0);
        } else {
            // --- auto expand logic ---
            for (ByteBuffer b : bbList) {
                while (wbc.write(b) > 0);
            }
        }
        out.close();
        if (context != null) {
            context.complete();
        }
    }
}