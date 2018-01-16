package com.example.internet_speed_testing

import java.math.BigDecimal
import java.time.Duration

data class ProgressionModel(
        var count: Int = 0,
        var progressTotal: Float = 0f,
        var progressDownload: Float = 0f,
        var progressUpload: Float = 0f,
        var pingDuration: Float = 0f,
        var uploadSpeed: BigDecimal = BigDecimal(0),
        var downloadSpeed: BigDecimal = BigDecimal(0),
        var downloadDuration: Long = 0L,
        var uploadDuration: Long = 0L
)
