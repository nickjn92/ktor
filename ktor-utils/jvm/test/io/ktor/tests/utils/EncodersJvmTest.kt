/*
 * Copyright 2014-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.tests.utils

import io.ktor.test.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.time.Duration.Companion.seconds

class EncodersJvmTest {

    @Test
    fun `malformed gzip fails`() = runTest(timeout = 1.seconds) {
        val malformedGzip = byteArrayOf(
            0x1f, 0x8b.toByte(),    // Magic
            0x08,                   // Deflate method
            0x00,                   // Flags
            0x00, 0x00, 0x00, 0x00, // Timestamp
            0x00,                   // Extra flags
            0xff.toByte(),          // OS
            // Incomplete deflate stream
            0x01, 0x00, 0x00,
        )

        val failure = assertFails("Malformed gzip should fail") {
            GZip.decode(ByteReadChannel(malformedGzip), coroutineContext).readRemaining()
        }
        assertEquals("Compressed input is incomplete.", failure.message)
    }
}
