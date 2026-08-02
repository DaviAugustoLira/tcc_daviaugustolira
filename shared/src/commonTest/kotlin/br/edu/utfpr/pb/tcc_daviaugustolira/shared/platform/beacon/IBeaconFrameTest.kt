package br.edu.utfpr.pb.tcc_daviaugustolira.shared.platform.beacon

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class IBeaconFrameTest {
    private val validFrame =
        byteArrayOf(
            0x02,
            0x15,
            0xE2.toByte(),
            0xC5.toByte(),
            0x6D,
            0xB5.toByte(),
            0xDF.toByte(),
            0xFB.toByte(),
            0x48,
            0xD2.toByte(),
            0xB0.toByte(),
            0x60,
            0xD0.toByte(),
            0xF5.toByte(),
            0xA7.toByte(),
            0x10,
            0x96.toByte(),
            0xE0.toByte(),
            0x00,
            0x01,
            0x00,
            0x02,
            0xC5.toByte(),
        )

    @Test
    fun `parses a valid Apple iBeacon frame`() {
        val frame = parseIBeaconFrame(companyId = 0x004C, data = validFrame)

        assertEquals(
            IBeaconFrame(
                uuid = "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0",
                major = 1,
                minor = 2,
                txPower = -59,
            ),
            frame,
        )
    }

    @Test
    fun `rejects frames from a company id other than Apple`() {
        assertNull(parseIBeaconFrame(companyId = 0x0006, data = validFrame))
    }

    @Test
    fun `rejects a truncated frame`() {
        assertNull(parseIBeaconFrame(companyId = 0x004C, data = validFrame.copyOf(10)))
    }

    @Test
    fun `rejects a frame with the wrong iBeacon type or length header`() {
        val wrongHeader = validFrame.copyOf()
        wrongHeader[0] = 0x01
        assertNull(parseIBeaconFrame(companyId = 0x004C, data = wrongHeader))
    }
}
