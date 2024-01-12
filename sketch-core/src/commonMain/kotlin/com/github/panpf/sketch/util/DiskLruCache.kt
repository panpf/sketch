/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.util

import android.util.Log
import com.github.panpf.sketch.util.DiskLruCache.Editor
import java.io.BufferedWriter
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.EOFException
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FilterOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Reader
import java.io.StringWriter
import java.io.UnsupportedEncodingException
import java.io.Writer
import java.nio.charset.Charset
import java.util.Arrays
import java.util.concurrent.Callable
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit.SECONDS
import java.util.regex.Pattern

/**
 * A cache that uses a bounded amount of space on a filesystem. Each cache
 * entry has a string key and a fixed number of values. Each key must match
 * the regex **[a-z0-9_-]{1,120}**. Values are byte sequences,
 * accessible as streams or files. Each value must be between `0` and
 * `Integer.MAX_VALUE` bytes in length.
 *
 *
 * The cache stores its data in a directory on the filesystem. This
 * directory must be exclusive to the cache; the cache may delete or overwrite
 * files from its directory. It is an error for multiple processes to use the
 * same cache directory at the same time.
 *
 *
 * This cache limits the number of bytes that it will store on the
 * filesystem. When the number of stored bytes exceeds the limit, the cache will
 * remove entries in the background until the limit is satisfied. The limit is
 * not strict: the cache may temporarily exceed it while waiting for files to be
 * deleted. The limit does not include filesystem overhead or the cache
 * journal so space-sensitive applications should set a conservative limit.
 *
 *
 * Clients call [.edit] to create or update the values of an entry. An
 * entry may have only one editor at one time; if a value is not available to be
 * edited then [.edit] will return null.
 *
 *  * When an entry is being **created** it is necessary to
 * supply a full set of values; the empty value should be used as a
 * placeholder if necessary.
 *  * When an entry is being **edited**, it is not necessary
 * to supply data for every value; values default to their previous
 * value.
 *
 * Every [.edit] call must be matched by a call to [Editor.commit]
 * or [Editor.abort]. Committing is atomic: a read observes the full set
 * of values as they were before or after the commit, but never a mix of values.
 *
 *
 * Clients call [.get] to read a snapshot of an entry. The read will
 * observe the value at the time that [.get] was called. Updates and
 * removals after the call do not impact ongoing reads.
 *
 *
 * This class is tolerant of some I/O errors. If files are missing from the
 * filesystem, the corresponding entries will be dropped from the cache. If
 * an error occurs while writing a cache value, the edit will fail silently.
 * Callers should handle other problems by catching `IOException` and
 * responding appropriately.
 *
 *
 *  Based https://github.com/JakeWharton/DiskLruCache version 2.0.2
 */
class DiskLruCache private constructor(
    /**
     * Returns the directory where this cache stores its data.
     */
    val directory: File,
    private val appVersion: Int,
    private val valueCount: Int,
    private var maxSize: Long
) : Closeable {
    /**
     * This cache uses a single background thread to evict entries.
     */
    private val executorService = ThreadPoolExecutor(0, 1, 60L, SECONDS, LinkedBlockingQueue())
    private val journalFile: File = File(directory, JOURNAL_FILE)
    private val journalFileTmp: File = File(directory, JOURNAL_FILE_TEMP)
    private val journalFileBackup: File = File(directory, JOURNAL_FILE_BACKUP)
    private val lruEntries = LinkedHashMap<String, Entry?>(0, 0.75f, true)
    private var size: Long = 0
    private var journalWriter: Writer? = null
    private var redundantOpCount = 0

    /**
     * To differentiate between old and current snapshots, each entry is given
     * a sequence number each time an edit is committed. A snapshot is stale if
     * its sequence number is not equal to its entry's sequence number.
     */
    private var nextSequenceNumber: Long = 0
    private val cleanupCallable: Callable<Void> = Callable {
        synchronized(this@DiskLruCache) {
            if (journalWriter == null) {
                return@Callable null // Closed.
            }
            trimToSize()
            if (journalRebuildRequired()) {
                rebuildJournal()
                redundantOpCount = 0
            }
        }
        null
    }

    @Throws(IOException::class)
    private fun readJournal() {
        val reader = StrictLineReader(FileInputStream(journalFile), Util.US_ASCII)
        try {
            val magic = reader.readLine()
            val version = reader.readLine()
            val appVersionString = reader.readLine()
            val valueCountString = reader.readLine()
            val blank = reader.readLine()
            if (MAGIC != magic
                || VERSION_1 != version
                || Integer.toString(appVersion) != appVersionString
                || Integer.toString(valueCount) != valueCountString
                || "" != blank
            ) {
                throw IOException(
                    "unexpected journal header: [" + magic + ", " + version + ", "
                            + valueCountString + ", " + blank + "]"
                )
            }
            var lineCount = 0
            while (true) {
                try {
                    readJournalLine(reader.readLine())
                    lineCount++
                } catch (endOfJournal: EOFException) {
                    break
                }
            }
            redundantOpCount = lineCount - lruEntries.size

            // If we ended on a truncated line, rebuild the journal before appending to it.
            if (reader.hasUnterminatedLine()) {
                rebuildJournal()
            } else {
                journalWriter = BufferedWriter(
                    OutputStreamWriter(
                        FileOutputStream(journalFile, true), Util.US_ASCII
                    )
                )
            }
        } finally {
            Util.closeQuietly(reader)
        }
    }

    @Throws(IOException::class)
    private fun readJournalLine(line: String) {
        val firstSpace = line.indexOf(' ')
        if (firstSpace == -1) {
            throw IOException("unexpected journal line: $line")
        }
        val keyBegin = firstSpace + 1
        val secondSpace = line.indexOf(' ', keyBegin)
        val key: String
        if (secondSpace == -1) {
            key = line.substring(keyBegin)
            if (firstSpace == REMOVE.length && line.startsWith(REMOVE)) {
                lruEntries.remove(key)
                return
            }
        } else {
            key = line.substring(keyBegin, secondSpace)
        }
        var entry = lruEntries[key]
        if (entry == null) {
            entry = Entry(key)
            lruEntries[key] = entry
        }
        if (secondSpace != -1 && firstSpace == CLEAN.length && line.startsWith(CLEAN)) {
            val parts =
                line.substring(secondSpace + 1).split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            entry.readable = true
            entry.currentEditor = null
            entry.setLengths(parts)
        } else if (secondSpace == -1 && firstSpace == DIRTY.length && line.startsWith(DIRTY)) {
            entry.currentEditor = Editor(entry)
        } else if (secondSpace == -1 && firstSpace == READ.length && line.startsWith(READ)) {
            // This work was already done by calling lruEntries.get().
        } else {
            throw IOException("unexpected journal line: $line")
        }
    }

    /**
     * Computes the initial size and collects garbage as a part of opening the
     * cache. Dirty entries are assumed to be inconsistent and will be deleted.
     */
    @Throws(IOException::class)
    private fun processJournal() {
        deleteIfExists(journalFileTmp)
        val i = lruEntries.values.iterator()
        while (i.hasNext()) {
            val entry = i.next()
            if (entry!!.currentEditor == null) {
                for (t in 0 until valueCount) {
                    size += entry.lengths[t]
                }
            } else {
                entry.currentEditor = null
                for (t in 0 until valueCount) {
                    deleteIfExists(entry.getCleanFile(t))
                    deleteIfExists(entry.getDirtyFile(t))
                }
                i.remove()
            }
        }
    }

    /**
     * Creates a new journal that omits redundant information. This replaces the
     * current journal if it exists.
     */
    @Synchronized
    @Throws(IOException::class)
    private fun rebuildJournal() {
        if (journalWriter != null) {
            journalWriter!!.close()
        }
        val writer: Writer = BufferedWriter(
            OutputStreamWriter(FileOutputStream(journalFileTmp), Util.US_ASCII)
        )
        try {
            writer.write(MAGIC)
            writer.write("\n")
            writer.write(VERSION_1)
            writer.write("\n")
            writer.write(Integer.toString(appVersion))
            writer.write("\n")
            writer.write(Integer.toString(valueCount))
            writer.write("\n")
            writer.write("\n")
            for (entry in lruEntries.values) {
                if (entry!!.currentEditor != null) {
                    writer.write(DIRTY + ' ' + entry.key + '\n')
                } else {
                    writer.write(CLEAN + ' ' + entry.key + entry.getLengths() + '\n')
                }
            }
        } finally {
            writer.close()
        }
        if (journalFile.exists()) {
            renameTo(journalFile, journalFileBackup, true)
        }
        renameTo(journalFileTmp, journalFile, false)
        journalFileBackup.delete()
        journalWriter = BufferedWriter(
            OutputStreamWriter(FileOutputStream(journalFile, true), Util.US_ASCII)
        )
    }

    /**
     * Returns a snapshot of the entry named `key`, or null if it doesn't
     * exist is not currently readable. If a value is returned, it is moved to
     * the head of the LRU queue.
     */
    @Synchronized
    @Throws(IOException::class)
    operator fun get(key: String): Snapshot? {
        checkNotClosed()
        validateKey(key)
        val entry = lruEntries[key] ?: return null
        if (!entry.readable) {
            return null
        }

        // Open all streams eagerly to guarantee that we see a single published
        // snapshot. If we opened streams lazily then the streams could come
        // from different edits.
//        InputStream[] ins = new InputStream[valueCount];
//        try {
//            for (int i = 0; i < valueCount; i++) {
//                ins[i] = new FileInputStream(entry.getCleanFile(i));
//            }
//        } catch (FileNotFoundException e) {
//            // A file must have been deleted manually!
//            for (int i = 0; i < valueCount; i++) {
//                if (ins[i] != null) {
//                    Util.closeQuietly(ins[i]);
//                } else {
//                    break;
//                }
//            }
//            return null;
//        }
        val cleanFiles = (0 until valueCount).map { entry.getCleanFile(it) }.toTypedArray()
        redundantOpCount++
        journalWriter!!.append("$READ $key\n")
        if (journalRebuildRequired()) {
            executorService.submit(cleanupCallable)
        }
        return Snapshot(key, entry.sequenceNumber, cleanFiles, entry.lengths)
    }

    /**
     * Returns exist of the entry named `key`
     */
    @Synchronized
    @Throws(IOException::class)
    fun exist(key: String): Boolean {
        checkNotClosed()
        validateKey(key)
        val entry = lruEntries[key]
        redundantOpCount++
        journalWriter!!.append(READ + ' ' + key + '\n')
        if (journalRebuildRequired()) {
            executorService.submit(cleanupCallable)
        }
        return entry != null && entry.readable
    }

    /**
     * Returns an editor for the entry named `key`, or null if another
     * edit is in progress.
     */
    @Throws(IOException::class)
    fun edit(key: String): Editor? {
        return edit(key, ANY_SEQUENCE_NUMBER)
    }

    @Synchronized
    @Throws(IOException::class)
    private fun edit(key: String, expectedSequenceNumber: Long): Editor? {
        checkNotClosed()
        validateKey(key)
        var entry = lruEntries[key]
        if (expectedSequenceNumber != ANY_SEQUENCE_NUMBER && (entry == null
                    || entry.sequenceNumber != expectedSequenceNumber)
        ) {
            return null // Snapshot is stale.
        }
        if (entry == null) {
            entry = Entry(key)
            lruEntries[key] = entry
        } else if (entry.currentEditor != null) {
            return null // Another edit is in progress.
        }
        val editor = Editor(entry)
        entry.currentEditor = editor

        // Flush the journal before creating files to prevent file leaks.
        journalWriter!!.write("$DIRTY $key\n")
        journalWriter!!.flush()
        return editor
    }

    /**
     * Returns the maximum number of bytes that this cache should use to store
     * its data.
     */
    @Synchronized
    fun getMaxSize(): Long {
        return maxSize
    }

    /**
     * Changes the maximum number of bytes the cache can store and queues a job
     * to trim the existing store, if necessary.
     */
    @Synchronized
    fun setMaxSize(maxSize: Long) {
        this.maxSize = maxSize
        executorService.submit(cleanupCallable)
    }

    /**
     * Returns the number of bytes currently being used to store the values in
     * this cache. This may be greater than the max size if a background
     * deletion is pending.
     */
    @Synchronized
    fun size(): Long {
        return size
    }

    @Synchronized
    @Throws(IOException::class)
    private fun completeEdit(editor: Editor, success: Boolean) {
        val entry = editor.entry
        check(entry.currentEditor == editor)

        // If this edit is creating the entry for the first time, every index must have a value.
        if (success && !entry.readable) {
            for (i in 0 until valueCount) {
                if (!editor.written!![i]) {
                    editor.abort()
                    throw IllegalStateException("Newly created entry didn't create value for index $i")
                }
                if (!entry.getDirtyFile(i).exists()) {
                    editor.abort()
                    return
                }
            }
        }
        for (i in 0 until valueCount) {
            val dirty = entry.getDirtyFile(i)
            if (success) {
                if (dirty.exists()) {
                    val clean = entry.getCleanFile(i)
                    dirty.renameTo(clean)
                    val oldLength = entry.lengths[i]
                    val newLength = clean.length()
                    entry.lengths[i] = newLength
                    size = size - oldLength + newLength
                }
            } else {
                deleteIfExists(dirty)
            }
        }
        redundantOpCount++
        entry.currentEditor = null
        if (entry.readable or success) {
            entry.readable = true
            journalWriter!!.write(CLEAN + ' ' + entry.key + entry.getLengths() + '\n')
            if (success) {
                entry.sequenceNumber = nextSequenceNumber++
            }
        } else {
            lruEntries.remove(entry.key)
            journalWriter!!.write(REMOVE + ' ' + entry.key + '\n')
        }
        journalWriter!!.flush()
        if (size > maxSize || journalRebuildRequired()) {
            executorService.submit(cleanupCallable)
        }
    }

    /**
     * We only rebuild the journal when it will halve the size of the journal
     * and eliminate at least 2000 ops.
     */
    private fun journalRebuildRequired(): Boolean {
        val redundantOpCompactThreshold = 2000
        return (redundantOpCount >= redundantOpCompactThreshold //
                && redundantOpCount >= lruEntries.size)
    }

    /**
     * Drops the entry for `key` if it exists and can be removed. Entries
     * actively being edited cannot be removed.
     *
     * @return true if an entry was removed.
     */
    @Synchronized
    @Throws(IOException::class)
    fun remove(key: String): Boolean {
        checkNotClosed()
        validateKey(key)
        val entry = lruEntries[key]
        if (entry == null || entry.currentEditor != null) {
            return false
        }
        for (i in 0 until valueCount) {
            val file = entry.getCleanFile(i)
            if (file.exists() && !file.delete()) {
                throw IOException("failed to delete $file")
            }
            size -= entry.lengths[i]
            entry.lengths[i] = 0
        }
        redundantOpCount++
        journalWriter!!.append("$REMOVE $key\n")
        lruEntries.remove(key)
        if (journalRebuildRequired()) {
            executorService.submit(cleanupCallable)
        }
        return true
    }

    @get:Synchronized
    val isClosed: Boolean
        /**
         * Returns true if this cache has been closed.
         */
        get() = journalWriter == null

    private fun checkNotClosed() {
        checkNotNull(journalWriter) { "cache is closed" }
    }

    /**
     * Force buffered operations to the filesystem.
     */
    @Synchronized
    @Throws(IOException::class)
    fun flush() {
        checkNotClosed()
        trimToSize()
        journalWriter!!.flush()
    }

    /**
     * Closes this cache. Stored values will remain on the filesystem.
     */
    @Synchronized
    @Throws(IOException::class)
    override fun close() {
        if (journalWriter == null) {
            return  // Already closed.
        }
        for (entry in ArrayList(lruEntries.values)) {
            entry?.currentEditor?.abort()
        }
        trimToSize()
        journalWriter!!.close()
        journalWriter = null
    }

    @Throws(IOException::class)
    private fun trimToSize() {
        while (size > maxSize) {
            val (key) = lruEntries.entries.iterator().next()
            remove(key)
        }
    }

    /**
     * Closes the cache and deletes all of its stored values. This will delete
     * all files in the cache directory including files that weren't created by
     * the cache.
     */
    @Throws(IOException::class)
    fun delete() {
        close()
        Util.deleteContents(directory)
    }

    private fun validateKey(key: String) {
        val matcher = LEGAL_KEY_PATTERN.matcher(key)
        require(matcher.matches()) {
            ("keys must match regex "
                    + STRING_KEY_PATTERN + ": \"" + key + "\"")
        }
    }

    /**
     * Buffers input from an [InputStream] for reading lines.
     *
     *
     * This class is used for buffered reading of lines. For purposes of this class, a line ends
     * with "\n" or "\r\n". End of input is reported by throwing `EOFException`. Unterminated
     * line at end of input is invalid and will be ignored, the caller may use `hasUnterminatedLine()` to detect it after catching the `EOFException`.
     *
     *
     * This class is intended for reading input that strictly consists of lines, such as line-based
     * cache entries or cache journal. Unlike the [java.io.BufferedReader] which in conjunction
     * with [java.io.InputStreamReader] provides similar functionality, this class uses different
     * end-of-input reporting and a more restrictive definition of a line.
     *
     *
     * This class supports only charsets that encode '\r' and '\n' as a single byte with value 13
     * and 10, respectively, and the representation of no other character contains these values.
     * We currently check in constructor that the charset is one of US-ASCII, UTF-8 and ISO-8859-1.
     * The default charset is US_ASCII.
     *
     * @param in       the `InputStream` to read data from.
     * @param capacity the capacity of the buffer.
     * @param charset  the charset used to decode data. Only US-ASCII, UTF-8 and ISO-8859-1 are
     * supported.
     * @throws NullPointerException     if `in` or `charset` is null.
     * @throws IllegalArgumentException if `capacity` is negative or zero
     * or the specified charset is not supported.
     */
    class StrictLineReader(`in`: InputStream?, capacity: Int, charset: Charset?) : Closeable {
        private val `in`: InputStream
        private val charset: Charset

        /*
         * Buffered data is stored in {@code buf}. As long as no exception occurs, 0 <= pos <= end
         * and the data in the range [pos, end) is buffered for reading. At end of input, if there is
         * an unterminated line, we set end == -1, otherwise end == pos. If the underlying
         * {@code InputStream} throws an {@code IOException}, end may remain as either pos or -1.
         */
        private var buf: ByteArray?
        private var pos = 0
        private var end = 0

        /**
         * Constructs a new `LineReader` with the specified charset and the default capacity.
         *
         * @param in      the `InputStream` to read data from.
         * @param charset the charset used to decode data. Only US-ASCII, UTF-8 and ISO-8859-1 are
         * supported.
         * @throws NullPointerException     if `in` or `charset` is null.
         * @throws IllegalArgumentException if the specified charset is not supported.
         */
        constructor(`in`: InputStream?, charset: Charset?) : this(`in`, 8192, charset)

        init {
            if (`in` == null || charset == null) {
                throw NullPointerException()
            }
            require(capacity >= 0) { "capacity <= 0" }
            require(charset == Util.US_ASCII) { "Unsupported encoding" }
            this.`in` = `in`
            this.charset = charset
            buf = ByteArray(capacity)
        }

        /**
         * Closes the reader by closing the underlying `InputStream` and
         * marking this reader as closed.
         *
         * @throws IOException for errors when closing the underlying `InputStream`.
         */
        @Throws(IOException::class)
        override fun close() {
            synchronized(`in`) {
                if (buf != null) {
                    buf = null
                    `in`.close()
                }
            }
        }

        /**
         * Reads the next line. A line ends with `"\n"` or `"\r\n"`,
         * this end of line marker is not included in the result.
         *
         * @return the next line from the input.
         * @throws IOException  for underlying `InputStream` errors.
         * @throws EOFException for the end of source stream.
         */
        @Throws(IOException::class)
        fun readLine(): String {
            synchronized(`in`) {
                if (buf == null) {
                    throw IOException("LineReader is closed")
                }

                // Read more data if we are at the end of the buffered data.
                // Though it's an error to read after an exception, we will let {@code fillBuf()}
                // throw again if that happens; thus we need to handle end == -1 as well as end == pos.
                if (pos >= end) {
                    fillBuf()
                }
                // Try to find LF in the buffered data and return the line if successful.
                for (i in pos until end) {
                    if (buf!![i] == LF) {
                        val lineEnd = if (i != pos && buf!![i - 1] == CR) i - 1 else i
                        val res = String(buf!!, pos, lineEnd - pos, charset(charset.name()))
                        pos = i + 1
                        return res
                    }
                }

                // Let's anticipate up to 80 characters on top of those already read.
                val out: ByteArrayOutputStream = object : ByteArrayOutputStream(end - pos + 80) {
                    override fun toString(): String {
                        val length = if (count > 0 && buf[count - 1] == CR) count - 1 else count
                        return try {
                            String(buf, 0, length, charset(charset.name()))
                        } catch (e: UnsupportedEncodingException) {
                            throw AssertionError(e) // Since we control the charset this will never happen.
                        }
                    }
                }
                while (true) {
                    out.write(buf, pos, end - pos)
                    // Mark unterminated line in case fillBuf throws EOFException or IOException.
                    end = -1
                    fillBuf()
                    // Try to find LF in the buffered data and return the line if successful.
                    for (i in pos until end) {
                        if (buf!![i] == LF) {
                            if (i != pos) {
                                out.write(buf, pos, i - pos)
                            }
                            pos = i + 1
                            return out.toString()
                        }
                    }
                }
            }
        }

        fun hasUnterminatedLine(): Boolean {
            return end == -1
        }

        /**
         * Reads new input data into the buffer. Call only with pos == end or end == -1,
         * depending on the desired outcome if the function throws.
         */
        @Throws(IOException::class)
        private fun fillBuf() {
            val result = `in`.read(buf, 0, buf!!.size)
            if (result == -1) {
                throw EOFException()
            }
            pos = 0
            end = result
        }

        companion object {
            private const val CR = '\r'.code.toByte()
            private const val LF = '\n'.code.toByte()
        }
    }

    /**
     * Junk drawer of utility methods.
     */
    object Util {
        val US_ASCII: Charset = Charset.forName("US-ASCII")
        val UTF_8: Charset = Charset.forName("UTF-8")

        @Throws(IOException::class)
        fun readFully(reader: Reader): String {
            return try {
                val writer = StringWriter()
                val buffer = CharArray(1024)
                var count: Int
                while (reader.read(buffer).also { count = it } != -1) {
                    writer.write(buffer, 0, count)
                }
                writer.toString()
            } finally {
                reader.close()
            }
        }

        /**
         * Deletes the contents of `dir`. Throws an IOException if any file
         * could not be deleted, or if `dir` is not a readable directory.
         */
        fun deleteContents(dir: File) {
            val files = dir.listFiles()
                ?: //                throw new IOException("not a readable directory: " + dir);
                return
            for (file in files) {
                if (file.isDirectory) {
                    deleteContents(file)
                }
                if (!file.delete()) {
                    Log.e("DiskLruCache", "failed to delete file: " + file.path)
                }
            }
        }

        fun closeQuietly( /*Auto*/
                          closeable: Closeable?
        ) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (rethrown: RuntimeException) {
                    throw rethrown
                } catch (ignored: Exception) {
                }
            }
        }
    }

    /**
     * A snapshot of the values for an entry.
     */
    inner class Snapshot(
        private val key: String,
        private val sequenceNumber: Long,
        private val cleanFiles: Array<File>,
        private val lengths: LongArray
    ) {
        /**
         * Returns an editor for this snapshot's entry, or null if either the
         * entry has changed since this snapshot was created or if another edit
         * is in progress.
         */
        @Throws(IOException::class)
        fun edit(): Editor? {
            return this@DiskLruCache.edit(key, sequenceNumber)
        }

        /**
         * Returns the unbuffered stream with the value for `index`.
         */
        fun getFile(index: Int): File {
            return cleanFiles[index]
        }

        /**
         * Returns the unbuffered stream with the value for `index`.
         */
        @Throws(FileNotFoundException::class)
        fun getInputStream(index: Int): InputStream {
            return FileInputStream(getFile(index))
        }

        /**
         * Returns the string value for `index`.
         */
        @Throws(IOException::class)
        fun getString(index: Int): String {
            val input = getInputStream(index)
            return try {
                inputStreamToString(input)
            } finally {
                Util.closeQuietly(input)
            }
        }

        /**
         * Returns the byte length of the value for `index`.
         */
        fun getLength(index: Int): Long {
            return lengths[index]
        }
    }

    /**
     * Edits the values for an entry.
     */
    inner class Editor internal constructor(val entry: Entry) {
        val written: BooleanArray?
        private var hasErrors = false
        private var committed = false

        init {
            written = if (entry.readable) null else BooleanArray(valueCount)
        }

        /**
         * Returns an unbuffered input stream to read the last committed value,
         * or null if no value has been committed.
         */
        @Throws(IOException::class)
        fun newInputStream(index: Int): InputStream? {
            synchronized(this@DiskLruCache) {
                check(entry.currentEditor == this)
                return if (!entry.readable) {
                    null
                } else try {
                    FileInputStream(entry.getCleanFile(index))
                } catch (e: FileNotFoundException) {
                    null
                }
            }
        }

        /**
         * Returns the last committed value as a string, or null if no value
         * has been committed.
         */
        @Throws(IOException::class)
        fun getString(index: Int): String? {
            val `in` = newInputStream(index)
            return if (`in` != null) inputStreamToString(`in`) else null
        }

        /**
         * Returns a new unbuffered output stream to write the value at
         * `index`. If the underlying output stream encounters errors
         * when writing to the filesystem, this edit will be aborted when
         * [.commit] is called. The returned output stream does not throw
         * IOExceptions.
         */
        @Throws(IOException::class)
        fun newOutputStream(index: Int): OutputStream {
            require(!(index < 0 || index >= valueCount)) {
                ("Expected index " + index + " to "
                        + "be greater than 0 and less than the maximum value count "
                        + "of " + valueCount)
            }
            synchronized(this@DiskLruCache) {
                check(entry.currentEditor == this)
                if (!entry.readable) {
                    written!![index] = true
                }
                val dirtyFile = entry.getDirtyFile(index)
                val outputStream: FileOutputStream
                outputStream = try {
                    FileOutputStream(dirtyFile)
                } catch (e: FileNotFoundException) {
                    // Attempt to recreate the cache directory.
                    directory.mkdirs()
                    try {
                        FileOutputStream(dirtyFile)
                    } catch (e2: FileNotFoundException) {
                        // We are unable to recover. Silently eat the writes.
                        return NULL_OUTPUT_STREAM
                    }
                }
                return FaultHidingOutputStream(outputStream)
            }
        }

        /**
         * Sets the value at `index` to `value`.
         */
        @Throws(IOException::class)
        operator fun set(index: Int, value: String?) {
            var writer: Writer? = null
            try {
                writer = OutputStreamWriter(newOutputStream(index), Util.UTF_8)
                writer.write(value)
            } finally {
                Util.closeQuietly(writer)
            }
        }

        /**
         * Commits this edit so it is visible to readers.  This releases the
         * edit lock so another edit may be started on the same key.
         */
        @Throws(IOException::class)
        fun commit() {
            if (hasErrors) {
                completeEdit(this, false)
                remove(entry.key) // The previous entry is stale.
            } else {
                completeEdit(this, true)
            }
            committed = true
        }

        /**
         * Aborts this edit. This releases the edit lock so another edit may be
         * started on the same key.
         */
        @Throws(IOException::class)
        fun abort() {
            completeEdit(this, false)
        }

        fun abortUnlessCommitted() {
            if (!committed) {
                try {
                    abort()
                } catch (ignored: IOException) {
                }
            }
        }

        private inner class FaultHidingOutputStream(out: OutputStream) : FilterOutputStream(out) {
            override fun write(oneByte: Int) {
                try {
                    out.write(oneByte)
                } catch (e: IOException) {
                    hasErrors = true
                }
            }

            override fun write(buffer: ByteArray, offset: Int, length: Int) {
                try {
                    out.write(buffer, offset, length)
                } catch (e: IOException) {
                    hasErrors = true
                }
            }

            override fun close() {
                try {
                    out.close()
                } catch (e: IOException) {
                    hasErrors = true
                }
            }

            override fun flush() {
                try {
                    out.flush()
                } catch (e: IOException) {
                    hasErrors = true
                }
            }
        }
    }

    inner class Entry internal constructor(val key: String) {
        /**
         * Lengths of this entry's files.
         */
        val lengths: LongArray = LongArray(valueCount)

        /**
         * True if this entry has ever been published.
         */
        var readable = false

        /**
         * The ongoing edit or null if this entry is not being edited.
         */
        var currentEditor: Editor? = null

        /**
         * The sequence number of the most recently committed edit to this entry.
         */
        var sequenceNumber: Long = 0

        @Throws(IOException::class)
        fun getLengths(): String {
            val result = StringBuilder()
            for (size in lengths) {
                result.append(' ').append(size)
            }
            return result.toString()
        }

        /**
         * Set lengths using decimal numbers like "10123".
         */
        @Throws(IOException::class)
        fun setLengths(strings: Array<String>) {
            if (strings.size != valueCount) {
                throw invalidLengths(strings)
            }
            try {
                for (i in strings.indices) {
                    lengths[i] = strings[i].toLong()
                }
            } catch (e: NumberFormatException) {
                throw invalidLengths(strings)
            }
        }

        @Throws(IOException::class)
        private fun invalidLengths(strings: Array<String>): IOException {
            throw IOException("unexpected journal line: " + Arrays.toString(strings))
        }

        fun getCleanFile(i: Int): File {
            return File(directory, "$key.$i")
        }

        fun getDirtyFile(i: Int): File {
            return File(directory, "$key.$i.tmp")
        }
    }

    companion object {
        const val JOURNAL_FILE = "journal"
        const val JOURNAL_FILE_BACKUP = "journal.bkp"
        const val JOURNAL_FILE_TEMP = "journal.tmp"
        const val MAGIC = "libcore.io.DiskLruCache"
        const val VERSION_1 = "1"
        const val ANY_SEQUENCE_NUMBER: Long = -1
        const val STRING_KEY_PATTERN = "[a-z0-9_-]{1,120}"
        val LEGAL_KEY_PATTERN = Pattern.compile(STRING_KEY_PATTERN)
        private const val CLEAN = "CLEAN"
        private const val DIRTY = "DIRTY"
        private const val REMOVE = "REMOVE"
        private const val READ = "READ"

        /*
         * This cache uses a journal file named "journal". A typical journal file
         * looks like this:
         *     libcore.io.DiskLruCache
         *     1
         *     100
         *     2
         *
         *     CLEAN 3400330d1dfc7f3f7f4b8d4d803dfcf6 832 21054
         *     DIRTY 335c4c6028171cfddfbaae1a9c313c52
         *     CLEAN 335c4c6028171cfddfbaae1a9c313c52 3934 2342
         *     REMOVE 335c4c6028171cfddfbaae1a9c313c52
         *     DIRTY 1ab96a171faeeee38496d8b330771a7a
         *     CLEAN 1ab96a171faeeee38496d8b330771a7a 1600 234
         *     READ 335c4c6028171cfddfbaae1a9c313c52
         *     READ 3400330d1dfc7f3f7f4b8d4d803dfcf6
         *
         * The first five lines of the journal form its header. They are the
         * constant string "libcore.io.DiskLruCache", the disk cache's version,
         * the application's version, the value count, and a blank line.
         *
         * Each of the subsequent lines in the file is a record of the state of a
         * cache entry. Each line contains space-separated values: a state, a key,
         * and optional state-specific values.
         *   o DIRTY lines track that an entry is actively being created or updated.
         *     Every successful DIRTY action should be followed by a CLEAN or REMOVE
         *     action. DIRTY lines without a matching CLEAN or REMOVE indicate that
         *     temporary files may need to be deleted.
         *   o CLEAN lines track a cache entry that has been successfully published
         *     and may be read. A publish line is followed by the lengths of each of
         *     its values.
         *   o READ lines track accesses for LRU.
         *   o REMOVE lines track entries that have been deleted.
         *
         * The journal file is appended to as cache operations occur. The journal may
         * occasionally be compacted by dropping redundant lines. A temporary file named
         * "journal.tmp" will be used during compaction; that file should be deleted if
         * it exists when the cache is opened.
         */
        private val NULL_OUTPUT_STREAM: OutputStream = object : OutputStream() {
            @Throws(IOException::class)
            override fun write(b: Int) {
                // Eat all writes silently. Nom nom.
            }
        }

        /**
         * Opens the cache in `directory`, creating a cache if none exists
         * there.
         *
         * @param directory  a writable directory
         * @param valueCount the number of values per cache entry. Must be positive.
         * @param maxSize    the maximum number of bytes this cache should use to store
         * @throws IOException if reading or writing the cache directory fails
         */
        @Throws(IOException::class)
        fun open(directory: File, appVersion: Int, valueCount: Int, maxSize: Long): DiskLruCache {
            require(maxSize > 0) { "maxSize <= 0" }
            require(valueCount > 0) { "valueCount <= 0" }

            // If a bkp file exists, use it instead.
            val backupFile = File(directory, JOURNAL_FILE_BACKUP)
            if (backupFile.exists()) {
                val journalFile = File(directory, JOURNAL_FILE)
                // If journal file also exists just delete backup file.
                if (journalFile.exists()) {
                    backupFile.delete()
                } else {
                    renameTo(backupFile, journalFile, false)
                }
            }

            // Prefer to pick up where we left off.
            var cache = DiskLruCache(directory, appVersion, valueCount, maxSize)
            if (cache.journalFile.exists()) {
                try {
                    cache.readJournal()
                    cache.processJournal()
                    return cache
                } catch (journalIsCorrupt: IOException) {
                    println(
                        "DiskLruCache "
                                + directory
                                + " is corrupt: "
                                + journalIsCorrupt.message
                                + ", removing"
                    )
                    cache.delete()
                }
            }

            // Create a new empty cache.
            directory.mkdirs()
            cache = DiskLruCache(directory, appVersion, valueCount, maxSize)
            cache.rebuildJournal()
            return cache
        }

        @Throws(IOException::class)
        private fun deleteIfExists(file: File) {
            if (file.exists() && !file.delete()) {
                throw IOException()
            }
        }

        @Throws(IOException::class)
        private fun renameTo(from: File, to: File, deleteDestination: Boolean) {
            if (deleteDestination) {
                deleteIfExists(to)
            }
            if (!from.renameTo(to)) {
                throw IOException()
            }
        }

        @Throws(IOException::class)
        private fun inputStreamToString(`in`: InputStream): String {
            return Util.readFully(InputStreamReader(`in`, Util.UTF_8))
        }
    }
}