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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class Alphabet {
    private static final String ORIGINAL = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-";

    static long seed = 1;
    private static final Random random = new Random(seed);

    static final String alphabet = ORIGINAL;
    static String shuffled;

    private static String shuffle() {
        List<String> sourceArray = Arrays.asList(alphabet.split(""));
        Collections.shuffle(sourceArray, random);
        return String.join("", sourceArray);
    }

    static String getShuffled() {
        if (shuffled != null) {
            return shuffled;
        }
        shuffled = shuffle();
        return shuffled;
    }

    static Character lookup(int index) {
        String alphabetShuffled = getShuffled();
        return alphabetShuffled.charAt(index);
    }

}
