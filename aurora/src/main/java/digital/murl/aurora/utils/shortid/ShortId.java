package digital.murl.aurora.utils.shortid;

/**
 * Copyright (c) Dylan Greene
 * All rights reserved.
 *
 * Code adapted from https://github.com/snimavat/shortid
 *
 * MIT +no-false-attribs License
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * Distributions of all or part of the Software intended to be used
 * by the recipients as they would use the unmodified Software,
 * containing modifications that substantially alter, remove, or
 * disable functionality of the Software, outside of the documented
 * configuration mechanisms provided by the Software, shall be
 * modified such that the Original Author's bug reporting email
 * addresses and urls are either replaced with the contact information
 * of the parties responsible for the changes, or removed entirely.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

public class ShortId {

    private static final int version = 7;
    private static final Long REDUCE_TIME = 1567752802062L;
    private static final int clusterWorkerId = 0;

    private static int counter;
    private static int previousSeconds;

    /**
     * Generate unique id
     * Returns string id
     */
    public static String generate() {

        String str = "";

        int seconds = (int) Math.floor((System.currentTimeMillis() - REDUCE_TIME) * 0.001);

        if (seconds == previousSeconds) {
            counter++;
        } else {
            counter = 0;
            previousSeconds = seconds;
        }

        str = str + Encode.encode(Alphabet::lookup, version);
        str = str + Encode.encode(Alphabet::lookup, clusterWorkerId);
        if (counter > 0) {
            str = str + Encode.encode(Alphabet::lookup, counter);
        }
        str = str + Encode.encode(Alphabet::lookup, seconds);

        return str;
    }

}
