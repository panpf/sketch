/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.panpf.sketch.request;

/**
 * 错误原因
 */
public enum ErrorCause {
    URI_INVALID,

    URI_NO_SUPPORT,


    DOWNLOAD_GET_RESPONSE_CODE_EXCEPTION,

    DOWNLOAD_RESPONSE_CODE_EXCEPTION,

    DOWNLOAD_CONTENT_LENGTH_EXCEPTION,

    DOWNLOAD_EXCEPTION_AND_CANCELED,

    DOWNLOAD_DISK_CACHE_INVALID,

    DOWNLOAD_DISK_CACHE_COMMIT_EXCEPTION,

    DOWNLOAD_DATA_NOT_FULLY_READ,

    DOWNLOAD_READ_DATA_EXCEPTION,

    DOWNLOAD_OPEN_DISK_CACHE_EXCEPTION,

    DOWNLOAD_NOT_FOUND_DISK_CACHE_AFTER_SUCCESS,

    DOWNLOAD_UNKNOWN_EXCEPTION,


    DECODE_UNKNOWN_RESULT_TYPE,

    DECODE_UNABLE_CREATE_DATA_SOURCE,

    DECODE_UNABLE_READ_BOUND_INFORMATION,

    DECODE_BOUND_RESULT_IMAGE_SIZE_INVALID,

    DECODE_NO_MATCHING_DECODE_HELPER,

    DECODE_UNABLE_CREATE_GIF_DRAWABLE,

    DECODE_NOT_FOUND_GIF_LIBRARY,

    DECODE_NO_MATCHING_GIF_SO,

    DECODE_FILE_IO_EXCEPTION,

    DECODE_RESULT_BITMAP_INVALID,

    DECODE_RESULT_BITMAP_SIZE_INVALID,

    DECODE_UNKNOWN_EXCEPTION,

    DECODE_PROCESS_IMAGE_FAIL,

    DECODE_CORRECT_ORIENTATION_FAIL,


    BITMAP_RECYCLED,

    GIF_DRAWABLE_RECYCLED,

    DATA_LOST_AFTER_LOAD_COMPLETED,

    DATA_LOST_AFTER_DOWNLOAD_COMPLETED,
}